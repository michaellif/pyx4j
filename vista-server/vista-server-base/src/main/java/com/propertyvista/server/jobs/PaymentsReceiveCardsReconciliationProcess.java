/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 24, 2014
 * @author vlads
 */
package com.propertyvista.server.jobs;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.payment.PaymentProcessFacade;
import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.RunStatus;

public class PaymentsReceiveCardsReconciliationProcess implements PmcProcess {

    private Integer recordsReceived;

    @Override
    public boolean start(PmcProcessContext context) {
        recordsReceived = ServerSideFactory.create(PaymentProcessFacade.class).receiveCardsReconciliation(context.getExecutionMonitor());
        return recordsReceived != null;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return false;
    }

    @Override
    public void executePmcJob(PmcProcessContext context) {
        throw new Error("this should not be called");
    }

    @Override
    public RunStatus complete(RunStatus runStatus, PmcProcessContext context) {
        if ((recordsReceived != null) && (recordsReceived > 0)) {
            ServerSideFactory.create(OperationsTriggerFacade.class).startProcess(PmcProcessType.paymentsProcessCardsReconciliation);
        }
        return runStatus;
    }

}
