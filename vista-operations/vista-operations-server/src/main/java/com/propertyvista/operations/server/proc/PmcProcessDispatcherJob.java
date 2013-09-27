/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 4, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.proc;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.ExceptionMessagesExtractor;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.operations.domain.scheduler.ExecutionReport;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.domain.scheduler.RunDataStatus;
import com.propertyvista.operations.domain.scheduler.RunStatus;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.domain.scheduler.TriggerPmc;
import com.propertyvista.server.jobs.PmcProcess;
import com.propertyvista.server.jobs.PmcProcessContext;
import com.propertyvista.server.jobs.PmcProcessFactory;

public class PmcProcessDispatcherJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(PmcProcessDispatcherJob.class);

    public static JobDataMap newJobDataMap(final Trigger trigger) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JobData.triggerId.name(), trigger.getPrimaryKey().asLong());
        return jobDataMap;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();
        if (SystemMaintenance.isSystemMaintenance()) {
            log.warn("Ignored trigger fired during SystemMaintenance");
            return;
        }
        try {
            Lifecycle.startElevatedUserContext();
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            Persistence.service().startTransaction(TransactionScopeOption.Suppress, ConnectionTarget.BackgroundProcess);
            Trigger process = Persistence.service().retrieve(Trigger.class, new Key(dataMap.getLong(JobData.triggerId.name())));

            if ((process.scheduleSuspended().getValue(Boolean.FALSE)) && (dataMap.getBoolean(JobData.manualExecution.name()) != Boolean.TRUE)) {
                log.info("Ignored suspended triggers{}", process.getStringView());
                return;
            }

            Date scheduledFireTime = context.getScheduledFireTime();
            Date forDate = null;
            if (dataMap.containsKey(JobData.forDate.name())) {
                forDate = new Date(dataMap.getLong(JobData.forDate.name()));
            }
            Long forPmcKey = null;
            if (dataMap.containsKey(JobData.forPmc.name())) {
                forPmcKey = dataMap.getLong(JobData.forPmc.name());
            }

            log.info("process start {} {} {}", process.id().getStringView(), process.triggerType().getStringView(), process.getStringView(), process);
            Long operationsUserKey = null;
            if (dataMap.containsKey(JobData.startedBy.name())) {
                operationsUserKey = dataMap.getLong(JobData.startedBy.name());
            }

            startAndRun(process, forPmcKey, scheduledFireTime, forDate, operationsUserKey);

            log.info("process end {} {} {}", process.id().getStringView(), process.triggerType().getStringView(), process.getStringView(), process);
        } finally {
            Persistence.service().endTransaction();
            Lifecycle.endContext();
        }
    }

    private void startAndRun(Trigger trigger, Long forPmcKey, Date scheduledFireTime, Date forDate, Long operationsUserKey) {
        EntityQueryCriteria<Run> criteria = EntityQueryCriteria.create(Run.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().trigger(), trigger));
        criteria.add(PropertyCriterion.in(criteria.proto().status(), RunStatus.Sleeping, RunStatus.Running));
        Run run = Persistence.service().retrieve(criteria);
        if (run != null) {
            // Need to resume run
            if (!run.status().getValue().equals(RunStatus.Sleeping)) {
                return;
            }
        } else {
            run = EntityFactory.create(Run.class);
        }
        run.started().setValue(new Date());
        run.status().setValue(RunStatus.Running);
        if (operationsUserKey != null) {
            run.startedBy().setPrimaryKey(new Key(operationsUserKey));
        }
        run.trigger().set(trigger);

        if (forDate == null) {
            run.forDate().setValue(scheduledFireTime);
        } else {
            run.forDate().setValue(forDate);
        }

        Persistence.service().persist(run);

        run.runData().setAttachLevel(AttachLevel.Detached);

        try {
            PmcProcessMonitor.onExecutionStart(run);

            PmcProcess pmcProcess = startProcess(run);

            if (pmcProcess != null) {

                if (forPmcKey == null) {
                    createPopulation(run, pmcProcess);
                } else {
                    createSinglePmcPopulation(run, pmcProcess, forPmcKey);
                }

                try {
                    executeRun(run, pmcProcess);
                } catch (Throwable e) {
                    log.error("job execution error", e);
                    run.status().setValue(RunStatus.Failed);
                }
            }
        } finally {
            PmcProcessMonitor.onExecutionEnds(run);
        }

        if (run.status().getValue() != RunStatus.Sleeping) {
            run.completed().setValue(new Date());
        }
        Persistence.service().persist(run);

        if ((run.status().getValue() == RunStatus.Sleeping) && !trigger.sleepRetry().isNull()) {
            // Reschedule run automatically
            JobUtils.schedulSleepRetry(trigger, scheduledFireTime);
        }
        JobNotifications.notify(trigger, run);
    }

    private PmcProcess startProcess(Run run) {
        PmcProcess pmcProcess = PmcProcessFactory.createPmcProcess(run.trigger().triggerType().getValue());
        PmcProcessContext context = new PmcProcessContext(run.forDate().getValue(), run.executionReport().processed().getValue(), run.executionReport()
                .failed().getValue(), run.executionReport().erred().getValue());
        PmcProcessMonitor.setContext(run, pmcProcess, context);
        try {
            if (!pmcProcess.start(context)) {
                run.status().setValue(RunStatus.Sleeping);
                return null;
            }
        } catch (Throwable e) {
            log.error("pmcProcess execution error", e);
            run.errorMessage().setValue(ExecutionMonitor.truncErrorMessage(ExceptionMessagesExtractor.getAllMessages(e)));
            run.status().setValue(RunStatus.Failed);
            return null;
        } finally {
            Persistence.service().retrieveMember(run.executionReport().details(), AttachLevel.Attached);
            context.getExecutionMonitor().updateExecutionReport(run.executionReport());
            Persistence.service().persist(run.executionReport());
        }
        return pmcProcess;
    }

    private void createPopulation(Run run, PmcProcess pmcProcess) {
        EntityQueryCriteria<Pmc> allPmcCriteria = EntityQueryCriteria.create(Pmc.class);
        allPmcCriteria.add(PropertyCriterion.eq(allPmcCriteria.proto().status(), PmcStatus.Active));

        switch (run.trigger().populationType().getValue()) {
        case allPmc:
            for (Pmc pmc : Persistence.service().query(allPmcCriteria)) {
                if (pmcProcess.allowExecution(pmc.features())) {
                    RunData runData = EntityFactory.create(RunData.class);
                    runData.execution().set(run);
                    runData.status().setValue(RunDataStatus.NeverRan);
                    runData.pmc().set(pmc);
                    Persistence.service().persist(runData);
                }
            }
            break;
        case except:
            HashSet<Key> except = new HashSet<Key>();
            for (TriggerPmc triggerPmc : run.trigger().population()) {
                except.add(triggerPmc.pmc().getPrimaryKey());
            }
            for (Pmc pmc : Persistence.service().query(allPmcCriteria)) {
                if (!except.contains(pmc.getPrimaryKey()) && pmcProcess.allowExecution(pmc.features())) {
                    RunData runData = EntityFactory.create(RunData.class);
                    runData.execution().set(run);
                    runData.status().setValue(RunDataStatus.NeverRan);
                    runData.pmc().set(pmc);
                    Persistence.service().persist(runData);
                }
            }
            break;
        case manual:
            for (TriggerPmc triggerPmc : run.trigger().population()) {
                RunData runData = EntityFactory.create(RunData.class);
                runData.execution().set(run);
                runData.status().setValue(RunDataStatus.NeverRan);
                runData.pmc().setPrimaryKey(triggerPmc.pmc().getPrimaryKey());
                Persistence.service().persist(runData);
            }
            break;
        case none:
            break;
        }
    }

    private void createSinglePmcPopulation(Run run, PmcProcess pmcProcess, Long forPmcKey) {
        Pmc pmc = Persistence.service().retrieve(Pmc.class, new Key(forPmcKey));
        RunData runData = EntityFactory.create(RunData.class);
        runData.execution().set(run);
        runData.status().setValue(RunDataStatus.NeverRan);
        runData.pmc().set(pmc);
        Persistence.service().persist(runData);
    }

    private void executeRun(Run run, PmcProcess pmcProcess) {
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
