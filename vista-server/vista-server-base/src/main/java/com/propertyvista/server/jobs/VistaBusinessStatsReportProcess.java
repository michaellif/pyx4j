/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-10
 * @author vlads
 */
package com.propertyvista.server.jobs;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.essentials.server.docs.sheet.ReportTableFormatter;

import com.propertyvista.biz.operations.business.VistaBusinessFacade;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.operations.domain.scheduler.RunStatus;

public class VistaBusinessStatsReportProcess implements PmcProcess {

    protected ReportTableFormatter formater;

    @Override
    public boolean start(PmcProcessContext context) {
        formater = ServerSideFactory.create(VistaBusinessFacade.class).startStatsReport();
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        ServerSideFactory.create(VistaBusinessFacade.class).processStatsReportsPmc(context.getExecutionMonitor(), formater);
    }

    @Override
    public RunStatus complete(RunStatus runStatus, PmcProcessContext context) {
        ServerSideFactory.create(VistaBusinessFacade.class).completeStatsReport(formater);
        return runStatus;
    }

}
