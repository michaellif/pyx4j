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
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
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
import com.propertyvista.server.jobs.report.StatisticsUtils;

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
            Persistence.service().startBackgroundProcessTransaction();
            Trigger process = Persistence.service().retrieve(Trigger.class, new Key(dataMap.getLong(JobData.triggerId.name())));

            Date scheduledFireTime = context.getScheduledFireTime();
            if (dataMap.containsKey(JobData.forDate.name())) {
                scheduledFireTime = new Date(dataMap.getLong(JobData.forDate.name()));
            }

            log.info("starting {}", process.getStringView());
            startAndRun(process, scheduledFireTime);
        } finally {
            Persistence.service().endTransaction();
            Lifecycle.endContext();
        }
    }

    private void startAndRun(Trigger trigger, Date scheduledFireTime) {
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
        run.trigger().set(trigger);

        if (run.forDate().isNull()) {
            run.forDate().setValue(scheduledFireTime);
        }

        Persistence.service().persist(run);
        Persistence.service().commit();

        run.runData().setAttachLevel(AttachLevel.Detached);

        PmcProcess pmcProcess = startProcess(run);

        if (pmcProcess != null) {
            try {
                executeRun(run, pmcProcess);
            } catch (Throwable e) {
                log.error("job execution error", e);
                run.status().setValue(RunStatus.Failed);
            }
        }

        Persistence.service().persist(run);
        Persistence.service().commit();

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
        try {
            if (!pmcProcess.start(context)) {
                run.status().setValue(RunStatus.Sleeping);
                return null;
            }
        } catch (Throwable e) {
            log.error("pmcProcess execution error", e);
            run.errorMessage().setValue(e.getMessage());
            run.status().setValue(RunStatus.Failed);
            return null;
        } finally {
            context.getExecutionMonitor().updateExecutionReport(run.executionReport());
            Persistence.service().persist(run.executionReport());
        }
        createPopulation(run);
        return pmcProcess;
    }

    private void createPopulation(Run run) {
        EntityQueryCriteria<Pmc> allPmcCriteria = EntityQueryCriteria.create(Pmc.class);
        allPmcCriteria.add(PropertyCriterion.eq(allPmcCriteria.proto().status(), PmcStatus.Active));

        switch (run.trigger().populationType().getValue()) {
        case allPmc:
            for (Key pmcKey : Persistence.service().queryKeys(allPmcCriteria)) {
                RunData runData = EntityFactory.create(RunData.class);
                runData.execution().set(run);
                runData.status().setValue(RunDataStatus.NeverRan);
                runData.pmc().setPrimaryKey(pmcKey);
                Persistence.service().persist(runData);
            }
            break;
        case except:
            HashSet<Key> except = new HashSet<Key>();
            for (TriggerPmc triggerPmc : run.trigger().population()) {
                except.add(triggerPmc.pmc().getPrimaryKey());
            }
            for (Key pmcKey : Persistence.service().queryKeys(allPmcCriteria)) {
                if (!except.contains(pmcKey)) {
                    RunData runData = EntityFactory.create(RunData.class);
                    runData.execution().set(run);
                    runData.status().setValue(RunDataStatus.NeverRan);
                    runData.pmc().setPrimaryKey(pmcKey);
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
        }
        Persistence.service().commit();
    }

    private void executeRun(Run run, PmcProcess pmcProcess) {
        long startTimeNano = System.nanoTime();
        PmcProcessContext context = new PmcProcessContext(run.forDate().getValue());
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try {

            RunStatus runStatus = RunStatus.Completed;

            EntityQueryCriteria<RunData> criteria = EntityQueryCriteria.create(RunData.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().execution(), run));
            criteria.add(PropertyCriterion.eq(criteria.proto().status(), RunDataStatus.NeverRan));
            ICursorIterator<RunData> it = Persistence.service().query(null, criteria, AttachLevel.Attached);
            while (it.hasNext()) {
                RunData runData = it.next();
                runData.updated().setValue(new Date());
                runData.started().setValue(new Date());
                runData.status().setValue(RunDataStatus.Running);
                Persistence.service().persist(runData);
                Persistence.service().commit();
                executeOneRunData(executorService, pmcProcess, runData, run.forDate().getValue());

                addRunDataStatsToRunStats(run.executionReport(), startTimeNano, runData);

                Persistence.service().persist(runData);
                Persistence.service().commit();

                if (runData.status().getValue() != RunDataStatus.Processed) {
                    runStatus = RunStatus.PartiallyCompleted;
                }
                if (runData.executionReport().erred().getValue(0L) > 0) {
                    runStatus = RunStatus.PartiallyCompleted;
                }
            }

            try {
                pmcProcess.complete(context);
                context.getExecutionMonitor().updateExecutionReport(run.executionReport());
                Persistence.service().persist(run.executionReport());
            } catch (Throwable e) {
                log.error("pmcProcess execution error", e);
                run.errorMessage().setValue(e.getMessage());
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

        StatisticsUtils.nvlAddLong(stats.total(), runData.executionReport().total());
        StatisticsUtils.nvlAddLong(stats.processed(), runData.executionReport().processed());
        StatisticsUtils.nvlAddDouble(stats.amountProcessed(), runData.executionReport().amountProcessed());
        StatisticsUtils.nvlAddLong(stats.failed(), runData.executionReport().failed());
        StatisticsUtils.nvlAddLong(stats.erred(), runData.executionReport().erred());
        StatisticsUtils.nvlAddDouble(stats.amountFailed(), runData.executionReport().amountFailed());

        stats.totalDuration().setValue(durationNano / Consts.MSEC2NANO);
        if ((!stats.total().isNull()) && (stats.total().getValue() != 0)) {
            stats.averageDuration().setValue(durationNano / (Consts.MSEC2NANO * stats.total().getValue()));
        }
        Persistence.service().persist(stats);
    }

    private void executeOneRunData(ExecutorService executorService, final PmcProcess pmcProcess, final RunData runData, final Date forDate) {
        long startTimeNano = System.nanoTime();
        final PmcProcessContext context = new PmcProcessContext(forDate, runData.executionReport().processed().getValue(), runData.executionReport().failed()
                .getValue(), runData.executionReport().erred().getValue());

        Future<Boolean> futureResult = executorService.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                boolean success = false;
                try {
                    Lifecycle.startElevatedUserContext();
                    NamespaceManager.setNamespace(runData.pmc().namespace().getValue());
                    Persistence.service().startBackgroundProcessTransaction();

                    pmcProcess.executePmcJob(context);
                    success = true;
                    return Boolean.TRUE;
                } finally {
                    try {
                        if (!success) {
                            Persistence.service().rollback();
                        }
                        Persistence.service().endTransaction();
                    } finally {
                        Lifecycle.endContext();
                    }
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
                Persistence.service().commit();
            }

            try {
                futureResult.get(15, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                continue;
            } catch (InterruptedException e) {
                throw new Error(e);
            } catch (ExecutionException e) {
                executionException = e.getCause();
                log.error("pmcProcess execution error", executionException);
                break;
            }
        }

        long durationNano = System.nanoTime() - startTimeNano;

        runData.executionReport().totalDuration().setValue(durationNano / Consts.MSEC2NANO);
        if ((!runData.executionReport().total().isNull()) && (runData.executionReport().total().getValue() != 0)) {
            runData.executionReport().averageDuration().setValue(durationNano / (Consts.MSEC2NANO * runData.executionReport().total().getValue()));
        }

        context.getExecutionMonitor().updateExecutionReport(runData.executionReport());
        Persistence.service().persist(runData.executionReport());
        if (executionException != null) {
            runData.status().setValue(RunDataStatus.Erred);
            runData.errorMessage().setValue(executionException.getMessage());
        } else {
            runData.status().setValue(RunDataStatus.Processed);
        }
    }
}
