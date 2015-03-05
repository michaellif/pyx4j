/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 3, 2015
 * @author stanp
 */
package com.propertyvista.server.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.legal.eviction.N4ManagementFacade;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.operations.domain.scheduler.RunStatus;

public class N4AutoCancellationProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(N4AutoCancellationProcess.class);

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("N4 Auto Cancellation Process started");
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        ExecutionMonitor monitor = context.getExecutionMonitor();
        ServerSideFactory.create(N4ManagementFacade.class).autoCancelN4(monitor);
    }

    @Override
    public RunStatus complete(RunStatus runStatus, PmcProcessContext context) {
        log.info("N4 Auto Cancellation Process complete");
        return runStatus;
    }

}
