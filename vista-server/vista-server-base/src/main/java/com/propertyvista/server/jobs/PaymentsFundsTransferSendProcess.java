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
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;

public class PaymentsFundsTransferSendProcess implements PmcProcess {

    private final FundsTransferType fundsTransferType;

    private FundsTransferFile padFile;

    public PaymentsFundsTransferSendProcess(FundsTransferType fundsTransferType) {
        this.fundsTransferType = fundsTransferType;
    }

    @Override
    public boolean start(PmcProcessContext context) {
        if (VistaDeployment.isVistaStaging()) {
            return false;
        }
        padFile = ServerSideFactory.create(PaymentProcessFacade.class).prepareFundsTransferFile(fundsTransferType);
        return true;
    }

    @Override
    public boolean allowExecution(PmcVistaFeatures features) {
        return true;
    }

    @Override
    public void executePmcJob(final PmcProcessContext context) {
        switch (fundsTransferType) {
        case PreAuthorizedDebit:
            ServerSideFactory.create(PaymentProcessFacade.class).prepareEcheckFundsTransfer(context.getExecutionMonitor(), padFile);
            break;
        case DirectBankingPayment:
            ServerSideFactory.create(PaymentProcessFacade.class).prepareDirectDebitFundsTransfer(context.getExecutionMonitor(), padFile);
            break;
        default:
            throw new IllegalArgumentException();
        }

    }

    @Override
    public void complete(PmcProcessContext context) {
        if (ServerSideFactory.create(PaymentProcessFacade.class).sendFundsTransferFile(this.padFile)) {
            context.getExecutionMonitor().setMessage("PAD file# " + padFile.fileCreationNumber().getStringView());
            context.getExecutionMonitor().addInfoEvent("sent file", padFile.fileName().getValue());
            context.getExecutionMonitor().addInfoEvent("fileCreationNumber", padFile.fileCreationNumber().getValue());
        } else {
            context.getExecutionMonitor().setMessage("Nothing to send");
        }
    }

}
