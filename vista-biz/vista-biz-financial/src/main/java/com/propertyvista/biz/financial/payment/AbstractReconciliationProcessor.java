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

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.AggregatedTransfer.AggregatedTransferStatus;
import com.propertyvista.domain.financial.CaledonFundsTransferType;
import com.propertyvista.domain.financial.EftAggregatedTransfer;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationRecordRecord;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationSummary;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecordProcessingStatus;
import com.propertyvista.server.TaskRunner;

abstract class AbstractReconciliationProcessor {

    private static final Logger log = LoggerFactory.getLogger(AbstractReconciliationProcessor.class);

    protected final ExecutionMonitor executionMonitor;

    protected final CaledonFundsTransferType fundsTransferType;

    protected final Pmc pmc;

    AbstractReconciliationProcessor(CaledonFundsTransferType fundsTransferType, ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
        this.fundsTransferType = fundsTransferType;
        this.pmc = VistaDeployment.getCurrentPmc();
    }

    final void processPmcReconciliation() {
        processReconciliationSummaryRecords();
        processReconciliationDebitRecords();
    }

    /**
     * Creates records aggregation. each record is processed individually after this
     */
    protected abstract void processReconciliationSummary(FundsReconciliationSummary summary);

    protected EftAggregatedTransfer createAggregatedTransfer(FundsReconciliationSummary summary) {
        EftAggregatedTransfer at = EntityFactory.create(EftAggregatedTransfer.class);
        at.padReconciliationSummaryKey().setValue(summary.getPrimaryKey());
        switch (summary.reconciliationStatus().getValue()) {
        case HOLD:
            at.status().setValue(AggregatedTransferStatus.Hold);
            break;
        case PAID:
            at.status().setValue(AggregatedTransferStatus.Paid);
            break;
        }
        // Find MerchantAccount
        {
            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
            criteria.eq(criteria.proto().merchantTerminalId(), summary.merchantTerminalId());
            at.merchantAccount().set(Persistence.service().retrieve(criteria));
            if (at.merchantAccount().isNull()) {
                throw new Error("Merchant Account '" + summary.merchantTerminalId().getValue() + "' not found");
            }
        }

        at.fundsTransferType().setValue(summary.reconciliationFile().fundsTransferType().getValue().asFundsTransferType());
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

        return at;
    }

    private void processReconciliationSummaryRecords() {

        List<FundsReconciliationSummary> summaryTransactions = TaskRunner.runInOperationsNamespace(new Callable<List<FundsReconciliationSummary>>() {
            @Override
            public List<FundsReconciliationSummary> call() throws Exception {
                EntityQueryCriteria<FundsReconciliationSummary> criteria = EntityQueryCriteria.create(FundsReconciliationSummary.class);
                criteria.eq(criteria.proto().reconciliationFile().fundsTransferType(), fundsTransferType);
                criteria.eq(criteria.proto().processingStatus(), Boolean.FALSE);
                criteria.eq(criteria.proto().merchantAccount().pmc(), pmc);
                List<FundsReconciliationSummary> transactions = Persistence.service().query(criteria);
                for (FundsReconciliationSummary summary : transactions) {
                    Persistence.service().retrieveMember(summary.records());
                }
                return transactions;
            }
        });

        for (final FundsReconciliationSummary summary : summaryTransactions) {

            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        processReconciliationSummary(summary);

                        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                            @Override
                            public Void call() {
                                summary.processingStatus().setValue(Boolean.TRUE);
                                Persistence.service().persist(summary);
                                return null;
                            }
                        });

                        return null;
                    }

                });

                executionMonitor.addInfoEvent("AggregatedTransfer", null, summary.netAmount().getValue());

            } catch (Throwable e) {
                log.error("AggregatedTransfer {} creation failed", summary.id().getValue(), e);
                executionMonitor.addErredEvent("AggregatedTransfer", summary.netAmount().getValue(),
                        SimpleMessageFormat.format("AggregatedTransferReconciliation {0} {1}", summary.id(), summary.merchantTerminalId()), e);
            }
        }

    }

    protected abstract void processReconciliationDebitRecord(FundsReconciliationRecordRecord debitRecord, FundsTransferRecord padDebitRecord);

    private void processReconciliationDebitRecords() {

        List<FundsReconciliationRecordRecord> records = TaskRunner.runInOperationsNamespace(new Callable<List<FundsReconciliationRecordRecord>>() {
            @Override
            public List<FundsReconciliationRecordRecord> call() throws Exception {
                EntityQueryCriteria<FundsReconciliationRecordRecord> criteria = EntityQueryCriteria.create(FundsReconciliationRecordRecord.class);
                criteria.eq(criteria.proto().processingStatus(), Boolean.FALSE);
                criteria.eq(criteria.proto().reconciliationSummary().processingStatus(), Boolean.TRUE);
                criteria.eq(criteria.proto().reconciliationSummary().merchantAccount().pmc(), pmc);
                criteria.eq(criteria.proto().reconciliationSummary().reconciliationFile().fundsTransferType(), fundsTransferType);
                return Persistence.service().query(criteria);
            }
        });

        for (final FundsReconciliationRecordRecord debitRecord : records) {
            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        debitRecordUnitOfWrok(debitRecord);
                        return null;
                    }

                });

                switch (debitRecord.reconciliationStatus().getValue()) {
                case PROCESSED:
                    executionMonitor.addProcessedEvent("Payment Processed", debitRecord.amount().getValue());
                    break;
                case REJECTED:
                    executionMonitor.addFailedEvent("Payment Rejected", debitRecord.amount().getValue());
                    break;
                case RETURNED:
                    executionMonitor.addFailedEvent("Payment Returned", debitRecord.amount().getValue());
                    break;
                case DUPLICATE:
                    executionMonitor.addErredEvent("Payment Duplicate", debitRecord.amount().getValue(), "TransactionId "
                            + debitRecord.transactionId().getValue());
                    break;
                }

            } catch (Throwable e) {
                log.error("payment transaction '" + debitRecord.transactionId().getValue() + "' processing error", e);
                executionMonitor.addErredEvent("Payment Error", debitRecord.amount().getValue(), e);
            }
        }

    }

    private void debitRecordUnitOfWrok(final FundsReconciliationRecordRecord debitRecord) {
        final FundsTransferRecord padDebitRecord = getPadDebitRecord(debitRecord);

        // Status and integrity validations
        switch (debitRecord.reconciliationStatus().getValue()) {
        case PROCESSED:
            if (padDebitRecord.processingStatus().getValue() != FundsTransferRecordProcessingStatus.ReconciliationReceived) {
                throw new Error("Payment " + fundsTransferType + " transaction '" + padDebitRecord.getStringView() + "' was not attached to AggregatedTransfer");
            }
            if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                throw new Error("Payment " + fundsTransferType + " transaction '" + padDebitRecord.getStringView() + "' already received");
            }
            break;
        case REJECTED:
            if (padDebitRecord.processingStatus().getValue() != FundsTransferRecordProcessingStatus.ReconciliationReceived) {
                throw new Error("Payment " + fundsTransferType + " transaction '" + padDebitRecord.getStringView() + "' was not attached to AggregatedTransfer");
            }
            if (padDebitRecord.processed().getValue(Boolean.FALSE)) {
                throw new Error("Payment " + fundsTransferType + " transaction '" + padDebitRecord.getStringView() + "' already received");
            }
            break;
        case RETURNED:
            break;
        case DUPLICATE:
            // TODO What todo ?
        default:
            throw new IllegalArgumentException("reconciliationStatus:" + debitRecord.reconciliationStatus().getValue());
        }

        // Do actual work and update corresponding payment records
        processReconciliationDebitRecord(debitRecord, padDebitRecord);

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                padDebitRecord.processingStatus().setValue(FundsTransferRecordProcessingStatus.ReconciliationProcessed);
                padDebitRecord.statusChangeDate().setValue(SystemDateManager.getDate());
                padDebitRecord.processed().setValue(Boolean.TRUE);
                Persistence.service().persist(padDebitRecord);

                debitRecord.processingStatus().setValue(Boolean.TRUE);
                Persistence.service().persist(debitRecord);
                return null;
            }
        });
    }

    protected FundsTransferRecord getPadDebitRecord(final FundsReconciliationRecordRecord debitRecord) {
        // Verify PAD record

        final FundsTransferRecord padDebitRecord = TaskRunner.runInOperationsNamespace(new Callable<FundsTransferRecord>() {
            @Override
            public FundsTransferRecord call() throws Exception {
                EntityQueryCriteria<FundsTransferRecord> criteria = EntityQueryCriteria.create(FundsTransferRecord.class);
                criteria.eq(criteria.proto().transactionId(), debitRecord.transactionId());
                criteria.eq(criteria.proto().padBatch().padFile().fundsTransferType(), fundsTransferType);
                criteria.ne(criteria.proto().processingStatus(), FundsTransferRecordProcessingStatus.AcknowledgeReject);
                criteria.eq(criteria.proto().padBatch().pmc(), pmc);
                FundsTransferRecord padDebitRecord = Persistence.service().retrieve(criteria);
                if (padDebitRecord != null) {
                    retrieveOperationsPadDebitRecordDetails(padDebitRecord);
                }
                return padDebitRecord;
            }
        });
        if (padDebitRecord == null) {
            throw new Error("Payment " + fundsTransferType + " transaction '" + debitRecord.transactionId().getValue() + "' not found");
        }

        return padDebitRecord;
    }

    protected void retrieveOperationsPadDebitRecordDetails(FundsTransferRecord padDebitRecord) {

    }

    protected void reconciliationClearedPayment(FundsReconciliationRecordRecord debitRecord, PaymentRecord paymentRecord) {
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Processed payment '" + debitRecord.transactionId().getValue() + "' can't be cleared");
        }
        paymentRecord.padReconciliationDebitRecordKey().setValue(debitRecord.getPrimaryKey());

        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Cleared);
        paymentRecord.lastStatusChangeDate().setValue(SystemDateManager.getLogicalDate());
        paymentRecord.finalizeDate().setValue(SystemDateManager.getLogicalDate());
        Persistence.service().merge(paymentRecord);
        log.info("Payment {} {} {} Cleared", fundsTransferType, paymentRecord.id().getValue(), paymentRecord.amount().getValue());
        ServerSideFactory.create(NotificationFacade.class).paymentCleared(paymentRecord);
    }

}
