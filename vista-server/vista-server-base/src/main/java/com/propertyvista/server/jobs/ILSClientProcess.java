/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.ils.VistaILSFacade;

public class ILSClientProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(ILSClientProcess.class);

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("ILS Client Process started");
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        ExecutionMonitor executionMonitor = context.getExecutionMonitor();
        ServerSideFactory.create(VistaILSFacade.class).updateGottarentListing(executionMonitor);
    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("ILS Client Process complete");
    }
}