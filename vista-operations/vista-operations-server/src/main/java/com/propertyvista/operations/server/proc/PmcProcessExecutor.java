/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 7, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.proc;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.server.ExceptionMessagesExtractor;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.operations.domain.scheduler.ExecutionReport;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.domain.scheduler.RunDataStatus;
import com.propertyvista.operations.domain.scheduler.RunStatus;
import com.propertyvista.server.jobs.PmcProcess;
import com.propertyvista.server.jobs.PmcProcessContext;

class PmcProcessExecutor {

    private static final Logger log = LoggerFactory.getLogger(PmcProcessExecutor.class);

    private final Run run;

    private final PmcProcess pmcProcess;

    PmcProcessExecutor(Run run, PmcProcess pmcProcess) {
        this.run = run;
        this.pmcProcess = pmcProcess;
    }

    void execute() {
        long startTimeNano = System.nanoTime();
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try {

            RunStatus runStatus = RunStatus.Completed;

            EntityQueryCriteria<RunData> criteria = EntityQueryCriteria.create(RunData.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().execution(), run));
            criteria.add(PropertyCriterion.eq(criteria.proto().status(), RunDataStatus.NeverRan));
            ICursorIterator<RunData> it = Persistence.service().query(null, criteria, AttachLevel.Attached);
            while (it.hasNext() && (!PmcProcessMonitor.isPendingTermination(run))) {
                RunData runData = it.next();
                if (runData.status().equals(RunDataStatus.Canceled)) {
                    continue;
                }
                runData.updated().setValue(new Date());
                runData.started().setValue(new Date());
                runData.status().setValue(RunDataStatus.Running);
                Persistence.service().persist(runData);

                executeOneRunData(executorService, pmcProcess, runData, run.forDate().getValue());

                addRunDataStatsToRunStats(run.executionReport(), startTimeNano, runData);

                Persistence.service().persist(runData);

                if (runData.status().getValue() != RunDataStatus.Processed) {
                    runStatus = RunStatus.PartiallyCompleted;
                }
                if (runData.executionReport().erred().getValue(0L) > 0) {
                    runStatus = RunStatus.PartiallyCompleted;
                }
            }

            // Completion
            try {
                PmcProcessContext context = new PmcProcessContext(run.forDate().getValue(), run.executionReport().processed().getValue(), run.executionReport()
                        .failed().getValue(), run.executionReport().erred().getValue());
                PmcProcessMonitor.setContext(run, pmcProcess, context);
                pmcProcess.complete(context);
                Persistence.service().retrieveMember(run.executionReport().details(), AttachLevel.Attached);
                context.getExecutionMonitor().updateExecutionReport(run.executionReport());
                Persistence.service().persist(run.executionReport());
            } catch (Throwable e) {
                log.error("pmcProcess execution error", e);
                run.errorMessage().setValue(ExecutionMonitor.truncErrorMessage(ExceptionMessagesExtractor.getAllMessages(e)));
                run.status().setValue(RunStatus.Failed);
                return;
            }
            run.status().setValue(runStatus);
        } finally {
            executorService.shutdownNow();
        }
    }

    private void addRunDataStatsToRunStats(ExecutionReport stats, long startTimeNano, RunData runData) {
        long durationNano = System.nanoTime() - startTimeNano;

        DomainUtil.nvlAddLong(stats.total(), runData.executionReport().total());
        DomainUtil.nvlAddLong(stats.processed(), runData.executionReport().processed());
        DomainUtil.nvlAddLong(stats.failed(), runData.executionReport().failed());
        DomainUtil.nvlAddLong(stats.erred(), runData.executionReport().erred());

        stats.totalDuration().setValue(durationNano / Consts.MSEC2NANO);
        if ((!stats.total().isNull()) && (stats.total().getValue() != 0)) {
            stats.averageDuration().setValue(durationNano / (Consts.MSEC2NANO * stats.total().getValue()));
        }
        Persistence.service().persist(stats);
    }

    private void executeOneRunData(ExecutorService executorService, final PmcProcess pmcProcess, final RunData runData, final Date forDate) {
        long startTimeNano = System.nanoTime();
        final PmcProcessContext context = new PmcProcessContext(forDate);

        PmcProcessMonitor.setContext(runData.execution(), pmcProcess, context);

        Future<Boolean> futureResult = executorService.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                try {
                    Lifecycle.startElevatedUserContext();
                    NamespaceManager.setNamespace(runData.pmc().namespace().getValue());
                    new UnitOfWork(TransactionScopeOption.Suppress, ConnectionTarget.BackgroundProcess).execute(new Executable<Void, RuntimeException>() {

                        @Override
                        public Void execute() {
                            pmcProcess.executePmcJob(context);
                            return null;
                        }

                    });

                    return Boolean.TRUE;
                } finally {
                    Lifecycle.endContext();
                }
            }
        });

        Throwable executionException = null;

        boolean compleated = false;
        while (!compleated) {

            if (futureResult.isDone()) {
                compleated = true;
            }
            ExecutionMonitor monitor = context.getExecutionMonitor();
            // Update Statistics in UI
            if (monitor.isDirty()) {
                monitor.updateExecutionReportMajorStats(runData.executionReport());
                Persistence.service().persist(runData.executionReport());
                monitor.markClean();
            }

            try {
                futureResult.get(15, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                continue;
            } catch (InterruptedException e) {
                throw new Error(e);
            } catch (ExecutionException e) {
                executionException = e.getCause();
                if (executionException == null) {
                    executionException = e;
                }
                log.error("pmcProcess {} pmc:{} execution error", pmcProcess.getClass().getSimpleName(), runData.pmc().namespace().getValue(),
                        executionException);
                break;
            }
        }

        long durationNano = System.nanoTime() - startTimeNano;

        runData.executionReport().totalDuration().setValue(durationNano / Consts.MSEC2NANO);
        if ((!runData.executionReport().total().isNull()) && (runData.executionReport().total().getValue() != 0)) {
            runData.executionReport().averageDuration().setValue(durationNano / (Consts.MSEC2NANO * runData.executionReport().total().getValue()));
        }

        Persistence.service().retrieveMember(runData.executionReport().details(), AttachLevel.Attached);
        context.getExecutionMonitor().updateExecutionReport(runData.executionReport());
        Persistence.service().persist(runData.executionReport());
        if (executionException != null) {
            runData.status().setValue(RunDataStatus.Erred);
            runData.errorMessage().setValue(ExecutionMonitor.truncErrorMessage(ExceptionMessagesExtractor.getAllMessages(executionException)));
        } else if (context.getExecutionMonitor().isTerminationRequested()) {
            runData.status().setValue(RunDataStatus.Terminated);
        } else {
            runData.status().setValue(RunDataStatus.Processed);
        }
    }
}
