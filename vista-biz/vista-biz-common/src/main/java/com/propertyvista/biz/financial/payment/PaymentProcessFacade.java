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
 */
package com.propertyvista.biz.financial.payment;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.CaledonFundsTransferType;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;

public interface PaymentProcessFacade {

    FundsTransferFile prepareFundsTransferFile(CaledonFundsTransferType fundsTransferType);

    void prepareEcheckFundsTransfer(ExecutionMonitor executionMonitor, FundsTransferFile padFile);

    void prepareDirectDebitFundsTransfer(ExecutionMonitor executionMonitor, FundsTransferFile padFile);

    boolean sendFundsTransferFile(FundsTransferFile padFile);

    //Funds Transfer
    CaledonFundsTransferType receiveFundsTransferAcknowledgementFile(ExecutionMonitor executionMonitor);

    void processPmcPadAcknowledgement(ExecutionMonitor executionMonitor);

    void processPmcDirectDebitAcknowledgement(ExecutionMonitor executionMonitor);

    CaledonFundsTransferType receiveFundsTransferReconciliation(ExecutionMonitor executionMonitor);

    void processPmcPadReconciliation(ExecutionMonitor executionMonitor);

    void processPmcDirectDebitReconciliation(ExecutionMonitor executionMonitor);

    Integer receiveCardsReconciliation(ExecutionMonitor executionMonitor);

    void processCardsReconciliation(ExecutionMonitor executionMonitor);

    void createPmcPreauthorisedPayments(ExecutionMonitor executionMonitor, LogicalDate padGenerationDate);

    void processPmcScheduledPayments(ExecutionMonitor executionMonitor, PaymentType paymentType, LogicalDate forDate);

    void cardsSend(ExecutionMonitor executionMonitor);

    void cardsPostRejected(ExecutionMonitor executionMonitor);

    void deleteExpiringAutopayAgreement(ExecutionMonitor executionMonitor, LogicalDate forDate);

    void verifyYardiPaymentIntegration(ExecutionMonitor executionMonitor, LogicalDate forDate);

    Integer receiveBmoFiles(ExecutionMonitor executionMonitor);

    void processDirectDebitRecords(ExecutionMonitor executionMonitor);

    void healthMonitorOperations(ExecutionMonitor executionMonitor, LogicalDate forDate);

    void healthMonitorPmc(ExecutionMonitor executionMonitor, LogicalDate forDate);
}
