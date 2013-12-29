/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.payment.PaymentProcessFacade;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.settings.PmcVistaFeatures;

public class PaymentsFundsTransferProcessAcknowledgmentProcess implements PmcProcess {

    private final FundsTransferType fundsTransferType;

    public PaymentsFundsTransferProcessAcknowledgmentProcess(FundsTransferType fundsTransferType) {
        this.fundsTransferType = fundsTransferType;
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
        switch (fundsTransferType) {
        case PreAuthorizedDebit:
            ServerSideFactory.create(PaymentProcessFacade.class).processPmcPadAcknowledgement(context.getExecutionMonitor());
            break;
        case DirectBankingPayment:
            ServerSideFactory.create(PaymentProcessFacade.class).processPmcDirectDebitAcknowledgement(context.getExecutionMonitor());
            break;
        default:
            throw new IllegalArgumentException();
        }

    }

    @Override
    public void complete(PmcProcessContext context) {
    }

}
