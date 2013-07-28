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
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecordProcessingStatus;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationSummary;
import com.propertyvista.server.jobs.TaskRunner;

class PadReconciliationProcessor {

    private static final Logger log = LoggerFactory.getLogger(PadReconciliationProcessor.class);

    private final ExecutionMonitor executionMonitor;

    PadReconciliationProcessor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    void processPmcReconciliation() {
        processAggregatedTransfer();
        processRecords();
    }

    private void processAggregatedTransfer() {
        final Pmc pmc = VistaDeployment.getCurrentPmc();

        List<PadReconciliationSummary> transactions = TaskRunner.runInOperationsNamespace(new Callable<List<PadReconciliationSummary>>() {
            @Override
            public List<PadReconciliationSummary> call() throws Exception {
                EntityQueryCriteria<PadReconciliationSummary> criteria = EntityQueryCriteria.create(PadReconciliationSummary.class);
                criteria.eq(criteria.proto().reconciliationFile().fundsTransferType(), FundsTransferType.PreAuthorizedDebit);
                criteria.eq(criteria.proto().processingStatus(), Boolean.FALSE);
                criteria.eq(criteria.proto().merchantAccount().pmc(), pmc);
                return Persistence.service().query(criteria);
            }
        });
        for (final PadReconciliationSummary summary : transactions) {

            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        createAggregatedTransferReconciliation(summary);
                        return null;
                    }

                });

                executionMonitor.addProcessedEvent("AggregatedTransfer", summary.netAmount().getValue());

            } catch (Throwable e) {
                log.error("AggregatedTransfer {} creation failed", summary.id().getValue(), e);
                executionMonitor.addErredEvent("AggregatedTransfer", summary.netAmount().getValue(),
                        SimpleMessageFormat.format("AggregatedTransferReconciliation {0} {1}", summary.id(), summary.merchantTerminalId()), e);
            }
        }
    }

    private void createAggregatedTransferReconciliation(final PadReconciliationSummary summary) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();

        final AggregatedTransfer at = EntityFactory.create(AggregatedTransfer.class);
        at.padReconciliationSummaryKey().setValue(summary.getPrimaryKey());
        switch (summary.reconciliationStatus().getValue()) {
        case HOLD:
            at.status().setValue(AggregatedTransferStatus.Hold);
            break;
        case PAID:
            at.status().setValue(AggregatedTransferStatus.Paid);
            break;
        }
        at.fundsTransferType().setValue(summary.reconciliationFile().fundsTransferType().getValue());
        at.paymentDate().setValue(summary.paymentDate().getValue());
        at.grossPaymentAmount().setValue(summary.grossPaymentAmount().getValue());
        at.grossPaymentFee().setValue(summary.grossPaymentFee().getValue());
        at.grossPaymentCount().setValue(summary.grossPaymentCount().getValue());
        at.rejectItemsAmount().setValue(summary.rejectItemsAmount().getValue());
        at.rejectItemsFee().setValue(summary.rejectItemsFee().getValue());
        at.rejectItemsCount().setValue(summary.rejectItemsCount().getValue());
        at.returnItemsAmount().setValue(summary.returnItemsAmount().getValue());
        at.returnItemsFee().setValue(summary.returnItemsFee().getValue());
        at.returnItemsCount().setValue(summary.returnItemsCount().getValue());
        at.netAmount().setValue(summary.netAmount().getValue());
        at.adjustments().setValue(summary.adjustments().getValue());
        at.previousBalance().setValue(summary.previousBalance().getValue());
        at.merchantBalance().setValue(summary.merchantBalance().getValue());
        at.fundsReleased().setValue(summary.fundsReleased().getValue());

        // Find MerchantAccount
        {
            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().merchantTerminalId(), summary.merchantTerminalId()));
            at.merchantAccount().set(Persistence.service().retrieve(criteria));
            if (at.merchantAccount().isNull()) {
                throw new Error("Merchant Account '" + summary.merchantTerminalId().getValue() + "' not found");
            }
        }

        Persistence.service().persist(at);

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                Persistence.service().retrieveMember(summary.records());
                return null;
            }
        });

        // Validate payment records and add them to this aggregatedTransfer
        for (final PadReconciliationDebitRecord debitRecord : summary.records()) {
            PaymentRecord paymentRecord = getPaymentRecord(debitRecord);
            final PadDebitRecord padDebitRecord = getPadDebitRecord(pmc, debitRecord);

            //TODO Improve validation
            switch (debitRecord.reconciliationStatus().getValue()) {
            case PROCESSED:
                if (padDebitRecord.processingStatus().getValue() != PadDebitRecordProcessingStatus.AcknowledgeProcesed) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' was not Acknowledged");
                }
                if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' already received");
                }
                paymentRecord.aggregatedTransfer().set(at);
                break;
            case REJECTED:
                if (padDebitRecord.processingStatus().getValue() != PadDebitRecordProcessingStatus.AcknowledgeProcesed) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' was not Acknowledged");
                }
                if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' already received");
                }
                paymentRecord.aggregatedTransfer().set(at);
                break;
            case RETURNED:
                if (padDebitRecord.processingStatus().getValue() != PadDebitRecordProcessingStatus.ReconciliationProcesed) {
                    throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' was not processed");
                }
                paymentRecord.aggregatedTransferReturn().set(at);
                break;
            case DUPLICATE:
                // TODO What todo ?
            default:
                throw new IllegalArgumentException("reconciliationStatus:" + debitRecord.reconciliationStatus().getValue());
            }

            Persistence.service().persist(paymentRecord);

            if (padDebitRecord.processingStatus().getValue() == PadDebitRecordProcessingStatus.AcknowledgeProcesed) {
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

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                summary.processingStatus().setValue(Boolean.TRUE);
                Persistence.service().persist(summary);
                return null;
            }
        });

    }

    private void processRecords() {
        final Pmc pmc = VistaDeployment.getCurrentPmc();

        List<PadReconciliationDebitRecord> records = TaskRunner.runInOperationsNamespace(new Callable<List<PadReconciliationDebitRecord>>() {
            @Override
            public List<PadReconciliationDebitRecord> call() throws Exception {
                EntityQueryCriteria<PadReconciliationDebitRecord> criteria = EntityQueryCriteria.create(PadReconciliationDebitRecord.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().processingStatus(), Boolean.FALSE));
                criteria.add(PropertyCriterion.eq(criteria.proto().reconciliationSummary().processingStatus(), Boolean.TRUE));
                criteria.add(PropertyCriterion.eq(criteria.proto().reconciliationSummary().merchantAccount().pmc(), pmc));
                return Persistence.service().query(criteria);
            }
        });

        for (final PadReconciliationDebitRecord debitRecord : records) {
            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        processDebitRecord(pmc, debitRecord);
                        return null;
                    }

                });

                switch (debitRecord.reconciliationStatus().getValue()) {
                case PROCESSED:
                    executionMonitor.addProcessedEvent("Processed", debitRecord.amount().getValue());
                    break;
                case REJECTED:
                    executionMonitor.addFailedEvent("Rejected", debitRecord.amount().getValue());
                    break;
                case RETURNED:
                    executionMonitor.addFailedEvent("Returned", debitRecord.amount().getValue());
                    break;
                case DUPLICATE:
                    executionMonitor.addErredEvent("Duplicate", debitRecord.amount().getValue(), "TransactionId " + debitRecord.transactionId().getValue());
                    break;
                }

            } catch (Throwable e) {
                log.error("payment transaction '" + debitRecord.transactionId().getValue() + "' processing error", e);
                executionMonitor.addErredEvent("Error", debitRecord.amount().getValue(), e);
            }
        }
    }

    private PaymentRecord getPaymentRecord(PadReconciliationDebitRecord debitRecord) {
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

    private PadDebitRecord getPadDebitRecord(final Pmc pmc, final PadReconciliationDebitRecord debitRecord) {
        // Verify PAD record
        final PadDebitRecord padDebitRecord = TaskRunner.runInOperationsNamespace(new Callable<PadDebitRecord>() {
            @Override
            public PadDebitRecord call() throws Exception {
                EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
                criteria.eq(criteria.proto().transactionId(), debitRecord.transactionId());
                criteria.ne(criteria.proto().processingStatus(), PadDebitRecordProcessingStatus.AcknowledgeReject);
                criteria.eq(criteria.proto().padBatch().pmc(), pmc);
                return Persistence.service().retrieve(criteria);
            }
        });
        if (padDebitRecord == null) {
            throw new Error("Payment PAD transaction '" + debitRecord.transactionId().getValue() + "' not found");
        }

        return padDebitRecord;
    }

    private void processDebitRecord(final Pmc pmc, final PadReconciliationDebitRecord debitRecord) {
        PaymentRecord paymentRecord = getPaymentRecord(debitRecord);
        final PadDebitRecord padDebitRecord = getPadDebitRecord(pmc, debitRecord);

        switch (debitRecord.reconciliationStatus().getValue()) {
        case PROCESSED:
            if (padDebitRecord.processingStatus().getValue() != PadDebitRecordProcessingStatus.ReconciliationReceived) {
                throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' was not attached to AggregatedTransfer");
            }
            if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' already received");
            }
            reconciliationClearedPayment(debitRecord, paymentRecord);
            break;
        case REJECTED:
            if (padDebitRecord.processingStatus().getValue() != PadDebitRecordProcessingStatus.ReconciliationReceived) {
                throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' was not attached to AggregatedTransfer");
            }
            if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                throw new Error("Payment PAD transaction '" + padDebitRecord.getStringView() + "' already received");
            }
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

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                padDebitRecord.processingStatus().setValue(PadDebitRecordProcessingStatus.ReconciliationProcesed);
                padDebitRecord.processed().setValue(Boolean.TRUE);
                Persistence.service().persist(padDebitRecord);

                debitRecord.processingStatus().setValue(Boolean.TRUE);
                Persistence.service().persist(debitRecord);
                return null;
            }
        });
    }

    private void reconciliationRejectPayment(PadReconciliationDebitRecord debitRecord, PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Processed payment '" + debitRecord.transactionId().getValue() + "' can't be rejected");
        }
        paymentRecord.padReconciliationDebitRecordKey().setValue(debitRecord.getPrimaryKey());

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        paymentRecord.transactionErrorMessage().setValue(debitRecord.reasonCode().getValue() + " " + debitRecord.reasonText().getValue());

        Persistence.service().merge(paymentRecord);
        log.info("Payment {} {} Rejected", paymentRecord.id().getValue(), paymentRecord.amount().getValue());

        try {
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, true);
        } catch (ARException e) {
            throw new Error("Payment can't be rejected", e);
        }

    }

    private void reconciliationClearedPayment(PadReconciliationDebitRecord debitRecord, PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Processed payment '" + debitRecord.transactionId().getValue() + "' can't be cleared");
        }
        paymentRecord.padReconciliationDebitRecordKey().setValue(debitRecord.getPrimaryKey());

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.service().merge(paymentRecord);
        log.info("Payment {} {} Cleared", paymentRecord.id().getValue(), paymentRecord.amount().getValue());
    }

    private void reconciliationReturnedPayment(PadReconciliationDebitRecord debitRecord, PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Cleared).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Unprocessed payment '" + debitRecord.transactionId().getValue() + "' can't be returned");
        }
        paymentRecord.padReconciliationReturnRecordKey().setValue(debitRecord.getPrimaryKey());

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Returned);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        paymentRecord.transactionErrorMessage().setValue(debitRecord.reasonCode().getValue() + " " + debitRecord.reasonText().getValue());

        Persistence.service().merge(paymentRecord);

        try {
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, true);
        } catch (ARException e) {
            throw new Error("Payment can't be returned", e);
        }

        log.info("Payment {} {} Returned", paymentRecord.id().getValue(), paymentRecord.amount().getValue());
    }

}
