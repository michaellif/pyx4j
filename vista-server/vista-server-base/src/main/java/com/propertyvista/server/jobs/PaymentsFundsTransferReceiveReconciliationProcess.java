/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.payment.PaymentProcessFacade;
import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;

public class PaymentsFundsTransferReceiveReconciliationProcess implements PmcProcess {

    private FundsTransferType fundsTransferType;

    @Override
    public boolean start(PmcProcessContext context) {
        if (VistaDeployment.isVistaStaging()) {
            return false;
        } else {
            fundsTransferType = ServerSideFactory.create(PaymentProcessFacade.class).receiveFundsTransferReconciliation(context.getExecutionMonitor());
            return (fundsTransferType != null);
        }
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
    public void complete(PmcProcessContext context) {
        switch (fundsTransferType) {
        case PreAuthorizedDebit:
            ServerSideFactory.create(OperationsTriggerFacade.class).startProcess(PmcProcessType.paymentsPadProcessReconciliation);
            break;
        case DirectBankingPayment:
            ServerSideFactory.create(OperationsTriggerFacade.class).startProcess(PmcProcessType.paymentsDbpProcessReconciliation);
            break;
        default:
            throw new IllegalArgumentException();
        }
    }
}
