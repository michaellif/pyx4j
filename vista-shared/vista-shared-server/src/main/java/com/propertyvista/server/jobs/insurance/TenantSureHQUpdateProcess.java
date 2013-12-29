/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs.insurance;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.tenant.insurance.TenantSureProcessFacade;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.operations.domain.tenantsure.TenantSureHQUpdateFile;
import com.propertyvista.server.jobs.PmcProcess;
import com.propertyvista.server.jobs.PmcProcessContext;

public class TenantSureHQUpdateProcess implements PmcProcess {

    private TenantSureHQUpdateFile file;

    public TenantSureHQUpdateProcess() {
    }

    @Override
    public boolean start(PmcProcessContext context) {
        file = ServerSideFactory.create(TenantSureProcessFacade.class).receiveHQUpdatesFile();
        return (file != null);
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        ServerSideFactory.create(TenantSureProcessFacade.class).processHQUpdate(context.getExecutionMonitor(), file);
    }

    @Override
    public void complete(PmcProcessContext context) {
    }

}
