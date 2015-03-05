/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 18, 2014
 * @author vlads
 */
package com.propertyvista.server.jobs;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.payment.PaymentProcessFacade;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.operations.domain.scheduler.RunStatus;

public class PaymentsCardsSendProcess implements PmcProcess {

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
        ServerSideFactory.create(PaymentProcessFacade.class).cardsSend(context.getExecutionMonitor());
    }

    @Override
    public RunStatus complete(RunStatus runStatus, PmcProcessContext context) {
        return runStatus;
    }

}
