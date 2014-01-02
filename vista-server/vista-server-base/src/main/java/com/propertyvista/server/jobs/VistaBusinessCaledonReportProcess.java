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
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.essentials.server.report.ReportTableFormatter;

import com.propertyvista.biz.operations.business.VistaBusinessFacade;
import com.propertyvista.domain.settings.PmcVistaFeatures;

public class VistaBusinessCaledonReportProcess implements PmcProcess {

    protected ReportTableFormatter formater;

    @Override
    public boolean start(PmcProcessContext context) {
        formater = ServerSideFactory.create(VistaBusinessFacade.class).startCaledonReport();
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        ServerSideFactory.create(VistaBusinessFacade.class).processCaledonReportPmc(context.getExecutionMonitor(), formater);
    }

    @Override
    public void complete(PmcProcessContext context) {
        ServerSideFactory.create(VistaBusinessFacade.class).completeCaledonReport(formater);
    }

}
