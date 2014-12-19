/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 10, 2014
 * @author vlads
 */
package com.propertyvista.server.jobs.insurance;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.tenant.insurance.TenantSureProcessFacade;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.server.jobs.PmcProcess;
import com.propertyvista.server.jobs.PmcProcessContext;

public class TenantSureRenewalProcess implements PmcProcess {

    public TenantSureRenewalProcess() {
    }

    @Override
    public boolean start(PmcProcessContext context) {
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        ServerSideFactory.create(TenantSureProcessFacade.class).processRenewal(context.getExecutionMonitor(), new LogicalDate(context.getForDate()));
    }

    @Override
    public void complete(PmcProcessContext context) {
    }
}
