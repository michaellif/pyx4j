/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 1, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecordProcessing;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecordProcessingStatus;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecordTransaction;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationSummary;
import com.propertyvista.server.jobs.TaskRunner;

class DirectDebitReconciliationProcessor extends AbstractReconciliationProcessor {

    private static final Logger log = LoggerFactory.getLogger(DirectDebitReconciliationProcessor.class);

    DirectDebitReconciliationProcessor(ExecutionMonitor executionMonitor) {
        super(FundsTransferType.DirectBankingPayment, executionMonitor);
    }

    @Override
    protected void retrieveOperationsPadDebitRecordDetails(PadDebitRecord padDebitRecord) {
        Persistence.service().retrieveMember(padDebitRecord.transactionRecords());
    }

    @Override
    protected void processReconciliationSummary(PadReconciliationSummary summary) {
        AggregatedTransfer at = createAggregatedTransfer(summary);
        Persistence.service().persist(at);

        // Override Caledon report values by calculating our onw fee
        at.grossPaymentCount().setValue(0);
        if (at.grossPaymentFee().isNull()) {
            at.grossPaymentFee().setValue(BigDecimal.ZERO);
        }

        // Validate payment records and add them to this aggregatedTransfer
        for (final PadReconciliationDebitRecord debitRecord : summary.records()) {
            final PadDebitRecord padDebitRecord = getPadDebitRecord(debitRecord);

            //TODO Improve validation
            switch (debitRecord.reconciliationStatus().getValue()) {
            case PROCESSED:
                if (padDebitRecord.processingStatus().getValue() != PadDebitRecordProcessingStatus.AcknowledgeProcessed) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' was not Acknowledged");
                }
                if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' already received");
                }
                break;
            case REJECTED:
                if (padDebitRecord.processingStatus().getValue() != PadDebitRecordProcessingStatus.AcknowledgeProcessed) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' was not Acknowledged");
                }
                if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' already received");
                }
                break;
            case RETURNED:
                if (padDebitRecord.processingStatus().getValue() != PadDebitRecordProcessingStatus.ReconciliationProcessed) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' was not processed");
                }
                break;
            case DUPLICATE:
                // TODO What todo ?
            default:
                throw new IllegalArgumentException("reconciliationStatus:" + debitRecord.reconciliationStatus().getValue());
            }

            for (PadDebitRecordTransaction transactionRecord : padDebitRecord.transactionRecords()) {
                PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, transactionRecord.paymentRecordKey().getValue());
                switch (debitRecord.reconciliationStatus().getValue()) {
                case PROCESSED:
                    paymentRecord.aggregatedTransfer().set(at);
                    at.grossPaymentCount().setValue(at.grossPaymentCount().getValue() + 1);
                    at.grossPaymentFee().setValue(at.grossPaymentFee().getValue().add(transactionRecord.feeAmount().getValue()));
                    break;
                case REJECTED:
                    paymentRecord.aggregatedTransfer().set(at);
                    break;
                case RETURNED:
                    paymentRecord.aggregatedTransferReturn().set(at);
                    break;
                default:
                    break;
                }
                Persistence.service().persist(paymentRecord);
            }

            if (padDebitRecord.processingStatus().getValue() == PadDebitRecordProcessingStatus.AcknowledgeProcessed) {
                TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                    @Override
                    public Void call() {
                        padDebitRecord.processingStatus().setValue(PadDebitRecordProcessingStatus.ReconciliationReceived);
                        Persistence.service().persist(padDebitRecord);
                        return null;
                    }
                });
            }

        }

        Persistence.service().persist(at);

    }

    @Override
    protected void processReconciliationDebitRecord(PadReconciliationDebitRecord debitRecord, PadDebitRecord padDebitRecord) {
        for (PadDebitRecordTransaction transactionRecord : padDebitRecord.transactionRecords()) {
            PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, transactionRecord.paymentRecordKey().getValue());

            switch (debitRecord.reconciliationStatus().getValue()) {
            case PROCESSED:
                reconciliationClearedPayment(debitRecord, paymentRecord);
                break;
            case REJECTED:
                rejectPaymentRecord(paymentRecord);
                break;
            case RETURNED:
                rejectPaymentRecord(paymentRecord);
                break;
            default:
                break;
            }
        }
    }

    private void rejectPaymentRecord(PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Received, PaymentRecord.PaymentStatus.Cleared).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Unexpected payment record status " + paymentRecord.getPrimaryKey() + " " + paymentRecord.paymentStatus().getValue());
        }
        // Update record status. Allow to ReSend automatically or Cancel Manually
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Queued);
        Persistence.service().persist(paymentRecord);

        PaymentRecordProcessing processing = EntityFactory.create(PaymentRecordProcessing.class);
        processing.paymentRecord().set(paymentRecord);
        Persistence.service().persist(processing);

        log.info("Payment {} {} Queued", paymentRecord.id().getValue(), paymentRecord.amount().getValue());
    }

}
