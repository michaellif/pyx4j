/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.tenant.lease.LeaseProcessFacade;
import com.propertyvista.domain.settings.PmcVistaFeatures;

public class LeaseActivationProcess implements PmcProcess {

    private static final Logger log = LoggerFactory.getLogger(LeaseActivationProcess.class);

    @Override
    public boolean start(PmcProcessContext context) {
        log.info("Activate Lease batch job started");
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return !features.yardiIntegration().getValue(false);
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        ServerSideFactory.create(LeaseProcessFacade.class).leaseActivation(context.getExecutionMonitor(), new LogicalDate(context.getForDate()));
        log.info(context.getExecutionMonitor().toString());
    }

    @Override
    public void complete(PmcProcessContext context) {
        log.info("Activate Lease batch job finished");
    }
}
