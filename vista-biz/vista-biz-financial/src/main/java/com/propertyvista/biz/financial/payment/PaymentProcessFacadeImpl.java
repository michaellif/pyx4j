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
package com.propertyvista.biz.financial.payment;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.operations.domain.payment.pad.PadFile;

public class PaymentProcessFacadeImpl implements PaymentProcessFacade {

    @Override
    public PadFile prepareFundsTransferFile(FundsTransferType fundsTransferType) {
        return new FundsTransferCaledon().prepareFundsTransferFile(fundsTransferType);
    }

    @Override
    public boolean sendFundsTransferFile(final PadFile padFile) {
        return new FundsTransferCaledon().sendFundsTransferFile(padFile);
    }

    @Override
    public void prepareEcheckFundsTransfer(final ExecutionMonitor executionMonitor, final PadFile padFile) {
        new PadFundsTransfer(executionMonitor, padFile).prepareEcheckFundsTransfer();
    }

    @Override
    public void prepareDirectDebitFundsTransfer(ExecutionMonitor executionMonitor, PadFile padFile) {
        new DirectDebitFundsTransfer(executionMonitor, padFile).prepareDirectDebitFundsTransfer();
    }

    @Override
    public FundsTransferType receiveFundsTransferAcknowledgementFile(ExecutionMonitor executionMonitor) {
        return new FundsTransferCaledon().receiveFundsTransferAcknowledgementFile(executionMonitor);
    }

    @Override
    public void processPmcPadAcknowledgement(ExecutionMonitor executionMonitor) {
        new PadAcknowledgementProcessor(executionMonitor).processPmcAcknowledgement();
    }

    @Override
    public void processPmcDirectDebitAcknowledgement(ExecutionMonitor executionMonitor) {
        new DirectDebitAcknowledgementProcessor(executionMonitor).processPmcAcknowledgement();
    }

    @Override
    public FundsTransferType receiveFundsTransferReconciliation(ExecutionMonitor executionMonitor) {
        return new FundsTransferCaledon().receiveFundsTransferReconciliation(executionMonitor);
    }

    @Override
    public void processPmcPadReconciliation(ExecutionMonitor executionMonitor) {
        new PadReconciliationProcessor(executionMonitor).processPmcReconciliation();
    }

    @Override
    public void processPmcDirectDebitReconciliation(ExecutionMonitor executionMonitor) {
        new DirectDebitReconciliationProcessor(executionMonitor).processPmcReconciliation();
    }

    @Override
    public void createPmcPreauthorisedPayments(ExecutionMonitor executionMonitor, LogicalDate runDate) {
        new AutopaytManager().createPreauthorisedPayments(executionMonitor, runDate);
    }

    @Override
    public void processPmcScheduledPayments(ExecutionMonitor executionMonitor, PaymentType paymentType, LogicalDate forDate) {
        new ScheduledPaymentsManager().processScheduledPayments(executionMonitor, paymentType, forDate);
    }

    @Override
    public void deleteExpiringAutopayAgreement(ExecutionMonitor executionMonitor, LogicalDate forDate) {
        new AutopayAgreementMananger().deleteExpiringAutopayAgreement(executionMonitor, forDate);
    }

    @Override
    public void verifyYardiPaymentIntegration(ExecutionMonitor executionMonitor, LogicalDate forDate) {
        new ScheduledPaymentsManager().verifyYardiPaymentIntegration(executionMonitor, forDate);
    }

    @Override
    public Integer receiveBmoFiles(ExecutionMonitor executionMonitor) {
        return new DirectDebitReceiveProcessor().receiveBmoFiles(executionMonitor);
    }

    @Override
    public void processDirectDebitRecords(ExecutionMonitor executionMonitor) {
        new DirectDebitPostProcessor().processDirectDebitRecords(executionMonitor);
    }

}
