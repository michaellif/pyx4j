/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-15
 * @author vlads
 */
package com.propertyvista.test.mock.schedule;

import java.util.Date;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.ConnectionTarget;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.operations.domain.scheduler.PmcProcessOptions;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.RunStatus;
import com.propertyvista.server.TaskRunner;
import com.propertyvista.server.jobs.PmcProcess;
import com.propertyvista.server.jobs.PmcProcessContext;
import com.propertyvista.server.jobs.PmcProcessFactory;

public class SchedulerMock {

    private static final Logger log = LoggerFactory.getLogger(SchedulerMock.class);

    // Pass the Namespace of single PMC context to OperationsTriggerFacadeMock (example PadReceiveAcknowledgmentProcess)
    static final ThreadLocal<String> requestNamspaceLocal = new ThreadLocal<String>();

    public static ExecutionMonitor runProcess(PmcProcessType processType, String forDateStr) {
        return runProcess(processType, DateUtils.detectDateformat(forDateStr));
    }

    public static ExecutionMonitor runProcess(PmcProcessType processType, String forDateStr, int expectedErred, int expectedFailed) {
        return runProcess(processType, DateUtils.detectDateformat(forDateStr), expectedErred, expectedFailed);
    }

    public static ExecutionMonitor runProcess(PmcProcessType processType, Date forDate, int expectedErred, int expectedFailed) {
        ExecutionMonitor executionMonitor = runProcess(processType, forDate);
        Assert.assertEquals(processType + " Erred", Long.valueOf(expectedErred), executionMonitor.getErred());
        Assert.assertEquals(processType + " Failed", Long.valueOf(expectedFailed), executionMonitor.getFailed());
        return executionMonitor;
    }

    /**
     * We run the process only for single PMC in Mock
     */
    public static ExecutionMonitor runProcess(PmcProcessType processType, Date forDate) {
        try {
            requestNamspaceLocal.set(NamespaceManager.getNamespace());
            Persistence.service().startTransaction(TransactionScopeOption.Suppress, ConnectionTarget.BackgroundProcess);

            if (forDate == null) {
                forDate = SystemDateManager.getDate();
            }

            final PmcProcessContext sharedContext = new PmcProcessContext(forDate);
            final PmcProcess pmcProcess = PmcProcessFactory.createPmcProcess(processType);

            boolean canStart = TaskRunner.runInOperationsNamespace(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return pmcProcess.start(sharedContext);
                }
            });
            if (canStart) {
                ExecutionMonitor executionMonitor = null;
                if (!processType.hasOption(PmcProcessOptions.GlobalOnly)) {

                    PmcProcessContext pmcContext = new PmcProcessContext(forDate);
                    pmcProcess.executePmcJob(pmcContext);
                    log.debug("Completed PmcProcess: date={}, process={} ({}), \n executionMonitor={}", forDate, processType, pmcProcess.getClass()
                            .getSimpleName(), pmcContext.getExecutionMonitor());
                    executionMonitor = pmcContext.getExecutionMonitor();
                } else {
                    executionMonitor = sharedContext.getExecutionMonitor();
                }

                TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        pmcProcess.complete(RunStatus.Completed, sharedContext);
                        return null;
                    }
                });

                return executionMonitor;
            } else {
                return sharedContext.getExecutionMonitor();
            }
        } finally {
            requestNamspaceLocal.remove();
            Persistence.service().endTransaction();
        }
    }

}
