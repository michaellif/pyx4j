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
package com.propertyvista.admin.server.proc;

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
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.RunData;
import com.propertyvista.admin.domain.scheduler.RunDataStatus;
import com.propertyvista.admin.domain.scheduler.RunStats;
import com.propertyvista.admin.domain.scheduler.RunStatus;
import com.propertyvista.admin.domain.scheduler.Trigger;
import com.propertyvista.admin.domain.scheduler.TriggerPmc;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.server.jobs.PmcProcess;
import com.propertyvista.server.jobs.PmcProcessContext;
import com.propertyvista.server.jobs.PmcProcessFactory;
import com.propertyvista.server.jobs.StatisticsUtils;

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
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
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
        boolean wasSleeping = RunStatus.Sleeping.equals(run.status().getValue());
        run.started().setValue(new Date());
        run.status().setValue(RunStatus.Running);
        run.trigger().set(trigger);

        if (run.forDate().isNull()) {
            run.forDate().setValue(scheduledFireTime);
        }

        Persistence.service().persist(run);
        Persistence.service().commit();
        if (!wasSleeping) {
            createPopulation(run);
        }
        run.runData().setAttachLevel(AttachLevel.Detached);
        try {
            executeRun(run);
        } catch (Throwable e) {
            log.error("job execution error", e);
            run.status().setValue(RunStatus.Failed);
        }
        Persistence.service().persist(run);
        Persistence.service().commit();

        if ((run.status().getValue() == RunStatus.Sleeping) && !trigger.sleepRetry().isNull()) {
            // Reschedule run automatically
            JobUtils.schedulSleepRetry(trigger, scheduledFireTime);
        }

        JobNotifications.notify(trigger, run);

    }

    private void createPopulation(Run run) {
        EntityQueryCriteria<Pmc> allPmcCriteria = EntityQueryCriteria.create(Pmc.class);
        allPmcCriteria.add(PropertyCriterion.eq(allPmcCriteria.proto().status(), PmcStatus.Active));

        switch (run.trigger().populationType().getValue()) {
        case AllPmc:
            for (Key pmcKey : Persistence.service().queryKeys(allPmcCriteria)) {
                RunData runData = EntityFactory.create(RunData.class);
                runData.execution().set(run);
                runData.status().setValue(RunDataStatus.NeverRan);
                runData.pmc().setPrimaryKey(pmcKey);
                Persistence.service().persist(runData);
            }
            break;
        case Except:
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
        case Manual:
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

    private void executeRun(Run run) {
        PmcProcess pmcProcess = PmcProcessFactory.createPmcProcess(run.trigger().triggerType().getValue());
        long startTimeNano = System.nanoTime();
        PmcProcessContext context = new PmcProcessContext(run.stats(), run.forDate().getValue());
        try {
            if (!pmcProcess.start(context)) {
                run.status().setValue(RunStatus.Sleeping);
                return;
            }
        } catch (Throwable e) {
            log.error("pmcProcess execution error", e);
            run.errorMessage().setValue(e.getMessage());
            run.status().setValue(RunStatus.Failed);
            return;
        }

        Persistence.service().persist(run.stats());
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try {
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

                updateRunStats(run.stats(), startTimeNano, runData);

                Persistence.service().persist(runData);
                Persistence.service().commit();
            }

            try {
                pmcProcess.complete(context);
            } catch (Throwable e) {
                log.error("pmcProcess execution error", e);
                run.errorMessage().setValue(e.getMessage());
                run.status().setValue(RunStatus.Failed);
                return;
            }
            run.status().setValue(RunStatus.Completed);
        } finally {
            executorService.shutdownNow();
        }

    }

    private void updateRunStats(RunStats stats, long startTimeNano, RunData runData) {
        long durationNano = System.nanoTime() - startTimeNano;

        StatisticsUtils.nvlAddLong(stats.total(), runData.stats().total());
        StatisticsUtils.nvlAddLong(stats.processed(), runData.stats().processed());
        StatisticsUtils.nvlAddDouble(stats.amountProcessed(), runData.stats().amountProcessed());
        StatisticsUtils.nvlAddLong(stats.failed(), runData.stats().failed());
        StatisticsUtils.nvlAddDouble(stats.amountFailed(), runData.stats().amountFailed());

        stats.totalDuration().setValue(durationNano / Consts.MSEC2NANO);
        if ((!stats.total().isNull()) && (stats.total().getValue() != 0)) {
            stats.averageDuration().setValue(durationNano / (Consts.MSEC2NANO * stats.total().getValue()));
        }
        Persistence.service().persist(stats);
    }

    private void executeOneRunData(ExecutorService executorService, final PmcProcess pmcProcess, final RunData runData, final Date forDate) {
        long startTimeNano = System.nanoTime();
        RunStats savedStats = runData.stats().duplicate();
        Future<Boolean> futureResult = executorService.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                boolean success = false;
                try {
                    Lifecycle.startElevatedUserContext();
                    NamespaceManager.setNamespace(runData.pmc().namespace().getValue());
                    Persistence.service().startBackgroundProcessTransaction();

                    PmcProcessContext context = new PmcProcessContext(runData.stats(), forDate);
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

            // Update Statistics 
            if (!EntityGraph.fullyEqualValues(savedStats, runData.stats())) {
                savedStats = runData.stats().duplicate();
                Persistence.service().persist(runData.stats());
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

        runData.stats().totalDuration().setValue(durationNano / Consts.MSEC2NANO);
        if ((!runData.stats().total().isNull()) && (runData.stats().total().getValue() != 0)) {
            runData.stats().averageDuration().setValue(durationNano / (Consts.MSEC2NANO * runData.stats().total().getValue()));
        }

        Persistence.service().persist(runData.stats());
        if (executionException != null) {
            runData.status().setValue(RunDataStatus.Erred);
            runData.errorMessage().setValue(executionException.getMessage());
        } else {
            runData.status().setValue(RunDataStatus.Processed);
        }
    }
}
