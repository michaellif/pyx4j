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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.RunData;
import com.propertyvista.admin.domain.scheduler.RunDataStatus;
import com.propertyvista.admin.domain.scheduler.RunStatus;
import com.propertyvista.admin.domain.scheduler.Trigger;
import com.propertyvista.admin.domain.scheduler.TriggerPmc;
import com.propertyvista.server.jobs.PmcProcess;
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
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        try {
            Lifecycle.startElevatedUserContext();
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            Persistence.service().startBackgroundProcessTransaction();
            Trigger process = Persistence.service().retrieve(Trigger.class, new Key(dataMap.getLong(JobData.triggerId.name())));
            log.info("starting {}", process);
            startAndRun(process);
        } finally {
            Persistence.service().endTransaction();
            Lifecycle.endContext();
        }
    }

    private void startAndRun(Trigger trigger) {
        EntityQueryCriteria<Run> criteria = EntityQueryCriteria.create(Run.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().trigger(), trigger));
        criteria.add(PropertyCriterion.ne(criteria.proto().status(), RunStatus.Completed));
        Run run = Persistence.service().retrieve(criteria);
        if (run != null) {
            // Need to resume run
            if (!run.status().getValue().equals(RunStatus.Sleeping)) {
                return;
            }
        } else {
            run = EntityFactory.create(Run.class);
        }
        run.status().setValue(RunStatus.Running);
        run.trigger().set(trigger);
        Persistence.service().persist(run);
        Persistence.service().commit();
        createPopulation(run);
        run.runData().setAttachLevel(AttachLevel.Detached);
        try {
            executeRun(run);
        } catch (Throwable e) {
            log.error("job execution error", e);
            run.status().setValue(RunStatus.Failed);
        }
        Persistence.service().persist(run);
        Persistence.service().commit();

    }

    private void createPopulation(Run run) {
        switch (run.trigger().populationType().getValue()) {
        case allPmc:
            for (Key pmcKey : Persistence.service().queryKeys(EntityQueryCriteria.create(Pmc.class))) {
                RunData runData = EntityFactory.create(RunData.class);
                runData.execution().set(run);
                runData.status().setValue(RunDataStatus.NeverRan);
                runData.pmc().setPrimaryKey(pmcKey);
                Persistence.service().persist(runData);
            }
            break;
        case manual:
            for (TriggerPmc triggerPmc : run.trigger().population()) {
                RunData runData = EntityFactory.create(RunData.class);
                runData.execution().set(run);
                runData.status().setValue(RunDataStatus.NeverRan);
                runData.pmc().setPrimaryKey(triggerPmc.getPrimaryKey());
                Persistence.service().persist(runData);
            }
            break;
        }
        Persistence.service().commit();
    }

    private void executeRun(Run run) {
        PmcProcess pmcProcess = PmcProcessFactory.createPmcProcess(run.trigger().triggerType().getValue());
        if (!pmcProcess.start()) {
            run.status().setValue(RunStatus.Sleeping);
            return;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        EntityQueryCriteria<RunData> criteria = EntityQueryCriteria.create(RunData.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().execution(), run));
        criteria.add(PropertyCriterion.ne(criteria.proto().status(), RunDataStatus.NeverRan));
        ICursorIterator<RunData> it = Persistence.service().query(null, criteria, AttachLevel.Attached);
        while (it.hasNext()) {
            RunData runData = it.next();
            runData.updated().setValue(new Date());
            executeOneRunData(executorService, pmcProcess, runData);
            Persistence.service().persist(runData);
            Persistence.service().commit();
        }
        run.status().setValue(RunStatus.Completed);

    }

    private void executeOneRunData(ExecutorService executorService, final PmcProcess pmcProcess, final RunData runData) {
        Future<RunDataStatus> futureResult = executorService.submit(new Callable<RunDataStatus>() {

            @Override
            public RunDataStatus call() throws Exception {
                try {
                    Lifecycle.startElevatedUserContext();
                    NamespaceManager.setNamespace(runData.pmc().namespace().getValue());
                    Persistence.service().startBackgroundProcessTransaction();
                    return pmcProcess.executePmc(runData.stats());
                } finally {
                    Persistence.service().endTransaction();
                    Lifecycle.endContext();
                }
            }
        });

        RunDataStatus result = null;
        while (result == null) {
            boolean compleated = false;
            if (futureResult.isDone()) {
                compleated = true;
            }

            // Update Statistics 
            Persistence.service().persist(runData.stats());
            Persistence.service().commit();

            try {
                result = futureResult.get(15, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                continue;
            } catch (InterruptedException e) {
                throw new Error(e);
            } catch (ExecutionException e) {
                throw new Error(e);
            }
            if (compleated) {
                break;
            }
        }

        Persistence.service().persist(runData.stats());
        if (result != null) {
            runData.status().setValue(result);
        } else {
            runData.status().setValue(RunDataStatus.Erred);
        }
    }
}
