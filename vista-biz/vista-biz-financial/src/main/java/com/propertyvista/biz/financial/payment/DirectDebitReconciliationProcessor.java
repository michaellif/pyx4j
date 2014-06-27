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

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.Validate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.EftAggregatedTransfer;
import com.propertyvista.domain.financial.CaledonFundsTransferType;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecordProcessing;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationRecordRecord;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationSummary;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecordProcessingStatus;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecordTransaction;
import com.propertyvista.server.TaskRunner;

class DirectDebitReconciliationProcessor extends AbstractReconciliationProcessor {

    private static final Logger log = LoggerFactory.getLogger(DirectDebitReconciliationProcessor.class);

    DirectDebitReconciliationProcessor(ExecutionMonitor executionMonitor) {
        super(CaledonFundsTransferType.DirectBankingPayment, executionMonitor);
    }

    @Override
    protected void retrieveOperationsPadDebitRecordDetails(FundsTransferRecord padDebitRecord) {
        Persistence.service().retrieveMember(padDebitRecord.transactionRecords());
    }

    @Override
    protected void processReconciliationSummary(FundsReconciliationSummary summary) {
        EftAggregatedTransfer at = createAggregatedTransfer(summary);
        Persistence.service().persist(at);

        // Override Caledon report values by calculating our onw fee
        at.grossPaymentCount().setValue(0);

        BigDecimal grossPaymentAmount = BigDecimal.ZERO;
        BigDecimal grossPaymentFee = BigDecimal.ZERO;

        // Validate payment records and add them to this aggregatedTransfer
        for (final FundsReconciliationRecordRecord debitRecord : summary.records()) {
            final FundsTransferRecord padDebitRecord = getPadDebitRecord(debitRecord);

            //TODO Improve validation
            switch (debitRecord.reconciliationStatus().getValue()) {
            case PROCESSED:
                if (padDebitRecord.processingStatus().getValue() != FundsTransferRecordProcessingStatus.AcknowledgeProcessed) {
                    throw new Error("Payment DirectBanking transaction '" + padDebitRecord.getStringView() + "' was not Acknowledged");
                }
                if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                    throw new Error("Payment DirectBanking transaction '" + padDebitRecord.getStringView() + "' already received");
                }
                break;
            case REJECTED:
                if (padDebitRecord.processingStatus().getValue() != FundsTransferRecordProcessingStatus.AcknowledgeProcessed) {
                    throw new Error("Payment DirectBanking transaction '" + padDebitRecord.getStringView() + "' was not Acknowledged");
                }
                if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                    throw new Error("Payment DirectBanking transaction '" + padDebitRecord.getStringView() + "' already received");
                }
                break;
            case RETURNED:
                if (padDebitRecord.processingStatus().getValue() != FundsTransferRecordProcessingStatus.ReconciliationProcessed) {
                    throw new Error("Payment DirectBanking transaction '" + padDebitRecord.getStringView() + "' was not processed");
                }
                break;
            case DUPLICATE:
                // TODO What todo ?
            default:
                throw new IllegalArgumentException("reconciliationStatus:" + debitRecord.reconciliationStatus().getValue());
            }

            for (FundsTransferRecordTransaction transactionRecord : padDebitRecord.transactionRecords()) {
                PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, transactionRecord.paymentRecordKey().getValue());
                switch (debitRecord.reconciliationStatus().getValue()) {
                case PROCESSED:
                    paymentRecord.aggregatedTransfer().set(at);
                    grossPaymentFee = grossPaymentFee.add(transactionRecord.feeAmount().getValue());
                    grossPaymentAmount = grossPaymentAmount.add(paymentRecord.amount().getValue());
                    at.grossPaymentCount().setValue(at.grossPaymentCount().getValue() + 1);
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

            if (padDebitRecord.processingStatus().getValue() == FundsTransferRecordProcessingStatus.AcknowledgeProcessed) {
                TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                    @Override
                    public Void call() {
                        padDebitRecord.processingStatus().setValue(FundsTransferRecordProcessingStatus.ReconciliationReceived);
                        padDebitRecord.statusChangeDate().setValue(SystemDateManager.getDate());
                        Persistence.service().persist(padDebitRecord);
                        return null;
                    }
                });
            }

        }

        if (at.grossPaymentFee().isNull()) {
            at.grossPaymentFee().setValue(BigDecimal.ZERO);
        }
        at.grossPaymentFee().setValue(at.grossPaymentFee().getValue().add(grossPaymentFee));
        at.grossPaymentAmount().setValue(at.grossPaymentAmount().getValue(BigDecimal.ZERO).add(grossPaymentFee));

        if (at.grossPaymentAmount().getValue().compareTo(grossPaymentAmount) != 0) {
            log.warn(SimpleMessageFormat.format("Unexpected Payment total amount {0} != grossPaymentAmount {1}, grossPaymentFee {2}", grossPaymentAmount,
                    at.grossPaymentAmount(), at.grossPaymentFee()));
        }

        Persistence.service().persist(at);

    }

    @Override
    protected void processReconciliationDebitRecord(FundsReconciliationRecordRecord debitRecord, FundsTransferRecord padDebitRecord) {
        for (FundsTransferRecordTransaction transactionRecord : padDebitRecord.transactionRecords()) {
            PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class, transactionRecord.paymentRecordKey().getValue());
            Validate.isEquals(PaymentType.DirectBanking, paymentRecord.paymentMethod().type().getValue(), "PaymentRecord {0}", paymentRecord);

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
                throw new Error("Unexpected payment record reconciliation status " + paymentRecord.getPrimaryKey() + " "
                        + debitRecord.reconciliationStatus().getValue());
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

        log.info("Payment {} {} {} Queued", fundsTransferType, paymentRecord.id().getValue(), paymentRecord.amount().getValue());
    }

}
