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

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.tenant.insurance.TenantSureProcessFacade;
import com.propertyvista.server.jobs.PmcProcess;
import com.propertyvista.server.jobs.PmcProcessContext;

public class PaymentsTenantSureProcess implements PmcProcess {

    public PaymentsTenantSureProcess() {
    }

    @Override
    public boolean start(PmcProcessContext context) {
        return true;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        GregorianCalendar dueDateCal = new GregorianCalendar();
        dueDateCal.setTime(context.getForDate());
        dueDateCal.add(Calendar.DAY_OF_MONTH, -1);

        ServerSideFactory.create(TenantSureProcessFacade.class).processPayments(context.getExecutionMonitor(), new LogicalDate(dueDateCal.getTime()));
    }

    @Override
    public void complete(PmcProcessContext context) {
    }

}
