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
package com.propertyvista.biz.financial.payment;

import java.util.EnumSet;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationRecordRecord;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationSummary;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecordProcessingStatus;
import com.propertyvista.server.TaskRunner;

class PadReconciliationProcessor extends AbstractReconciliationProcessor {

    private static final Logger log = LoggerFactory.getLogger(PadReconciliationProcessor.class);

    PadReconciliationProcessor(ExecutionMonitor executionMonitor) {
        super(FundsTransferType.PreAuthorizedDebit, executionMonitor);
    }

    @Override
    protected void processReconciliationSummary(final FundsReconciliationSummary summary) {
        AggregatedTransfer at = createAggregatedTransfer(summary);
        Persistence.service().persist(at);

        // Validate payment records and add them to this aggregatedTransfer
        for (final FundsReconciliationRecordRecord debitRecord : summary.records()) {
            PaymentRecord paymentRecord = getPaymentRecord(debitRecord);
            final FundsTransferRecord padDebitRecord = getPadDebitRecord(debitRecord);

            //TODO Improve validation
            switch (debitRecord.reconciliationStatus().getValue()) {
            case PROCESSED:
                if (padDebitRecord.processingStatus().getValue() != FundsTransferRecordProcessingStatus.AcknowledgeProcessed) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' was not Acknowledged");
                }
                if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' already received");
                }
                paymentRecord.aggregatedTransfer().set(at);
                break;
            case REJECTED:
                if (padDebitRecord.processingStatus().getValue() != FundsTransferRecordProcessingStatus.AcknowledgeProcessed) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' was not Acknowledged");
                }
                if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' already received");
                }
                paymentRecord.aggregatedTransfer().set(at);
                break;
            case RETURNED:
                if (padDebitRecord.processingStatus().getValue() != FundsTransferRecordProcessingStatus.ReconciliationProcessed) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' was not processed to be RETURNED");
                }
                paymentRecord.aggregatedTransferReturn().set(at);
                break;
            case DUPLICATE:
                // TODO What todo ?
            default:
                throw new IllegalArgumentException("reconciliationStatus:" + debitRecord.reconciliationStatus().getValue());
            }

            Persistence.service().persist(paymentRecord);

            if (padDebitRecord.processingStatus().getValue() == FundsTransferRecordProcessingStatus.AcknowledgeProcessed) {
                TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                    @Override
                    public Void call() {
                        padDebitRecord.processingStatus().setValue(FundsTransferRecordProcessingStatus.ReconciliationReceived);
                        Persistence.service().persist(padDebitRecord);
                        return null;
                    }
                });
            }

        }

    }

    private PaymentRecord getPaymentRecord(FundsReconciliationRecordRecord debitRecord) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class,
                PadTransactionUtils.toVistaPaymentRecordId(debitRecord.transactionId()));
        if (paymentRecord == null) {
            throw new Error("Payment transaction '" + debitRecord.transactionId().getValue() + "' not found");
        }
        if (PaymentType.Echeck != paymentRecord.paymentMethod().type().getValue()) {
            throw new IllegalArgumentException("Invalid PaymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
        }
        if (debitRecord.amount().getValue().compareTo(paymentRecord.amount().getValue()) != 0) {
            throw new Error("Unexpected transaction amount '" + paymentRecord.amount().getValue() + "', terminalId '"
                    + debitRecord.merchantTerminalId().getValue() + "', transactionId " + debitRecord.transactionId().getValue());
        }
        return paymentRecord;
    }

    @Override
    protected void processReconciliationDebitRecord(FundsReconciliationRecordRecord debitRecord, FundsTransferRecord padDebitRecord) {
        PaymentRecord paymentRecord = getPaymentRecord(debitRecord);

        switch (debitRecord.reconciliationStatus().getValue()) {
        case PROCESSED:
            reconciliationClearedPayment(debitRecord, paymentRecord);
            break;
        case REJECTED:
            reconciliationRejectPayment(debitRecord, paymentRecord);
            break;
        case RETURNED:
            reconciliationReturnedPayment(debitRecord, paymentRecord);
            break;
        case DUPLICATE:
            // TODO What todo ?
        default:
            throw new IllegalArgumentException("reconciliationStatus:" + debitRecord.reconciliationStatus().getValue());
        }
    }

    private boolean isNSFApplicable(FundsReconciliationRecordRecord debitRecord) {
        String reasonCode = debitRecord.reasonCode().getValue("").trim();
        if (reasonCode.equals("912")) {
            return false;
        } else {
            return true;
        }
    }

    private void reconciliationRejectPayment(FundsReconciliationRecordRecord debitRecord, PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Processed payment '" + debitRecord.transactionId().getValue() + "' can't be rejected");
        }
        paymentRecord.padReconciliationDebitRecordKey().setValue(debitRecord.getPrimaryKey());

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
        paymentRecord.lastStatusChangeDate().setValue(SystemDateManager.getLogicalDate());
        paymentRecord.finalizeDate().setValue(SystemDateManager.getLogicalDate());

        paymentRecord.transactionErrorMessage().setValue(
                CommonsStringUtils.nvl_concat(debitRecord.reasonCode().getValue(), debitRecord.reasonText().getValue(), " "));

        Persistence.service().merge(paymentRecord);

        try {
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, isNSFApplicable(debitRecord));
        } catch (ARException e) {
            throw new Error("Payment can't be rejected", e);
        }
        log.info("Payment {} {} {} Rejected", fundsTransferType, paymentRecord.id().getValue(), paymentRecord.amount().getValue());
    }

    private void reconciliationReturnedPayment(FundsReconciliationRecordRecord debitRecord, PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Cleared).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Unprocessed payment '" + debitRecord.transactionId().getValue() + "' can't be returned");
        }
        paymentRecord.padReconciliationReturnRecordKey().setValue(debitRecord.getPrimaryKey());

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Returned);
        paymentRecord.lastStatusChangeDate().setValue(SystemDateManager.getLogicalDate());
        paymentRecord.finalizeDate().setValue(SystemDateManager.getLogicalDate());

        paymentRecord.transactionErrorMessage().setValue(
                CommonsStringUtils.nvl_concat(debitRecord.reasonCode().getValue(), debitRecord.reasonText().getValue(), " "));

        Persistence.service().merge(paymentRecord);

        try {
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, isNSFApplicable(debitRecord));
        } catch (ARException e) {
            throw new Error("Payment can't be returned", e);
        }

        log.info("Payment {} {} {} Returned", fundsTransferType, paymentRecord.id().getValue(), paymentRecord.amount().getValue());
    }

}
