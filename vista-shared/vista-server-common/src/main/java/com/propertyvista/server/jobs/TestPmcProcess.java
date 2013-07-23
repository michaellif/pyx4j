/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.settings.PmcVistaFeatures;

public class TestPmcProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(TestPmcProcess.class);

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Test Job started");
        ExecutionMonitor executionMonitor = context.getExecutionMonitor();
        executionMonitor.addErredEvent("test", "Test Start error message");
        executionMonitor.addFailedEvent("test", "Test Start failed message");
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        Random random = new Random();
        int max = random.nextInt(300);

        ExecutionMonitor executionMonitor = context.getExecutionMonitor();
        for (int i = 0; i < max; i++) {
            executionMonitor.addInfoEvent("message", "message #" + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            executionMonitor.addProcessedEvent("test");
            if (executionMonitor.isTerminationRequested()) {
                break;
            }

        }

        executionMonitor.addErredEvent("test", "Test error message");
        executionMonitor.addFailedEvent("test", "Test failed message");

        executionMonitor.addProcessedEvent("test2", "Test processed message");
        executionMonitor.addFailedEvent("test2", "Test2 failed message");

    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Test Job complete");
    }
}
