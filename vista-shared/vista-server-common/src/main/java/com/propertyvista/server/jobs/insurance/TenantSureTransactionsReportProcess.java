/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-15
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.jobs.insurance;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.essentials.server.report.ReportTableFormatter;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.tenant.insurance.TenantSureProcessFacade;
import com.propertyvista.server.jobs.PmcProcess;
import com.propertyvista.server.jobs.PmcProcessContext;

public class TenantSureTransactionsReportProcess implements PmcProcess {

    protected ReportTableFormatter formatter;

    @Override
    public boolean start(PmcProcessContext context) {
        formatter = ServerSideFactory.create(TenantSureProcessFacade.class).startTransactionsReport();
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        ServerSideFactory.create(TenantSureProcessFacade.class).processTransactionsReport(context.getExecutionMonitor(),
                DateUtils.daysAdd(new LogicalDate(context.getForDate()), -1), formatter);
    }

    @Override
    public void complete(PmcProcessContext context) {
        ServerSideFactory.create(TenantSureProcessFacade.class).completeTransactionsReport(formatter,
                DateUtils.daysAdd(new LogicalDate(context.getForDate()), -1));
    }

}
