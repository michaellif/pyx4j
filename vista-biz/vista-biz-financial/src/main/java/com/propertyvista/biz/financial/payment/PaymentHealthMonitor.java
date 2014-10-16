/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.Date;
import java.util.EnumSet;
import java.util.concurrent.Callable;

import org.apache.commons.lang.time.DateUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.payment.CreditCardFacade.ReferenceNumberPrefix;
import com.propertyvista.biz.system.OperationsAlertFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferBatch;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferBatchProcessingStatus;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecordProcessingStatus;
import com.propertyvista.operations.domain.eft.cards.CardTransactionRecord;
import com.propertyvista.operations.domain.eft.cards.CardsClearanceRecord;
import com.propertyvista.operations.domain.eft.cards.CardsClearanceRecordProcessingStatus;
import com.propertyvista.operations.domain.eft.cards.CardsReconciliationRecord;
import com.propertyvista.operations.domain.eft.cards.CardsReconciliationRecordProcessingStatus;
import com.propertyvista.server.TaskRunner;

class PaymentHealthMonitor {

    private final ExecutionMonitor executionMonitor;

    public PaymentHealthMonitor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    void heathMonitorOperations(LogicalDate forDate) {
        verifyFundsTransfer(forDate);
        verifyCardTransactions(forDate);
    }

    private void verifyFundsTransfer(LogicalDate forDate) {
        // UnAcknowledged Files
        {
            Date reportSince = DateUtils.addHours(forDate, -6);
            EntityQueryCriteria<FundsTransferFile> criteria = EntityQueryCriteria.create(FundsTransferFile.class);
            criteria.eq(criteria.proto().status(), FundsTransferFile.PadFileStatus.Sent);
            criteria.le(criteria.proto().sent(), reportSince);
            int count = Persistence.service().count(criteria);
            if (count > 0) {
                criteria.asc(criteria.proto().sent());
                FundsTransferFile instance = Persistence.service().retrieve(criteria);
                ServerSideFactory.create(OperationsAlertFacade.class).record(instance,
                        "There are {0} UnAcknowledged FundsTransfer file(s), File was not Acknowledged since {1}", count, instance.sent());
                executionMonitor.addFailedEvent("FundsTransferFile", instance.fileAmount().getValue());
            }
        }

        // UnPpocessed  FundsTransferBatch Acknowledgment
        {
            Date reportSince = DateUtils.addHours(forDate, -6);
            EntityQueryCriteria<FundsTransferBatch> criteria = EntityQueryCriteria.create(FundsTransferBatch.class);
            criteria.eq(criteria.proto().padFile().status(), FundsTransferFile.PadFileStatus.Acknowledged);
            criteria.le(criteria.proto().padFile().sent(), reportSince);
            criteria.eq(criteria.proto().processingStatus(), FundsTransferBatchProcessingStatus.AcknowledgedReceived);
            int count = Persistence.service().count(criteria);
            if (count > 0) {
                criteria.asc(criteria.proto().padFile().sent());
                FundsTransferBatch instance = Persistence.service().retrieve(criteria);
                ServerSideFactory.create(OperationsAlertFacade.class).record(instance, "There are {0} UnPpocessed Acknowledged Batch(s)", count);
                executionMonitor.addFailedEvent("FundsTransferBatch", instance.batchAmount().getValue());
            }
        }

        // No Reconciliation for 2 days
        {
            Date reportSince = DateUtils.addDays(forDate, -2);
            EntityQueryCriteria<FundsTransferRecord> criteria = EntityQueryCriteria.create(FundsTransferRecord.class);
            criteria.eq(criteria.proto().padBatch().padFile().status(), FundsTransferFile.PadFileStatus.Acknowledged);
            criteria.le(criteria.proto().padBatch().padFile().sent(), reportSince);
            criteria.eq(criteria.proto().padBatch().processingStatus(), FundsTransferBatchProcessingStatus.AcknowledgeProcessed);
            criteria.ne(criteria.proto().processingStatus(), FundsTransferRecordProcessingStatus.ReconciliationProcessed);

            int count = Persistence.service().count(criteria);
            if (count > 0) {
                criteria.asc(criteria.proto().padBatch().padFile().sent());
                FundsTransferRecord instance = Persistence.service().retrieve(criteria);
                ServerSideFactory.create(OperationsAlertFacade.class).record(instance, "There are {0} FundsTransferRecord(s) without Reconciliation", count);
                executionMonitor.addFailedEvent("FundsTransferRecord", instance.amount().getValue());
            }
        }
    }

    private void verifyCardTransactions(LogicalDate forDate) {
        {
            Date reportSince = DateUtils.addDays(forDate, -1);
            EntityQueryCriteria<CardTransactionRecord> criteria = EntityQueryCriteria.create(CardTransactionRecord.class);
            criteria.ge(criteria.proto().creationDate(), reportSince);
            int count = Persistence.service().count(criteria);
            if (count >= 10) {
                criteria.ne(criteria.proto().saleResponseCode(), "0000");
                int failedCount = Persistence.service().count(criteria);
                if (failedCount == count) {
                    CardTransactionRecord instance = Persistence.service().retrieve(criteria);
                    ServerSideFactory.create(OperationsAlertFacade.class).record(instance, "All {0} CardTransaction(s) in last 24 hours failed", count);
                    executionMonitor.addFailedEvent("CardTransactionRecord", instance.amount().getValue());
                }
            }
        }

        {
            Date reportSince = DateUtils.addMonths(forDate, -2);
            Date reportBefore = DateUtils.addDays(forDate, -2);
            EntityQueryCriteria<CardsClearanceRecord> criteria = EntityQueryCriteria.create(CardsClearanceRecord.class);
            criteria.eq(criteria.proto().status(), CardsClearanceRecordProcessingStatus.Received);
            criteria.ge(criteria.proto().recordReceivedDate(), reportSince);
            criteria.le(criteria.proto().recordReceivedDate(), reportBefore);
            // Ignore TenantSure and Equifax payment gracefully for now
            criteria.isNull(criteria.proto().merchantAccount());
            int count = Persistence.service().count(criteria);
            if (count > 0) {
                CardsClearanceRecord instance = Persistence.service().retrieve(criteria);
                ServerSideFactory.create(OperationsAlertFacade.class).record(instance, "There are {0} UnPpocessed CardsClearanceRecord(s)", count);
                executionMonitor.addFailedEvent("CardsClearanceRecord", instance.amount().getValue());
            }
        }

        {
            Date reportSince = DateUtils.addMonths(forDate, -2);
            Date reportBefore = DateUtils.addDays(forDate, -2);
            EntityQueryCriteria<CardsReconciliationRecord> criteria = EntityQueryCriteria.create(CardsReconciliationRecord.class);
            criteria.eq(criteria.proto().status(), CardsReconciliationRecordProcessingStatus.Received);
            criteria.ge(criteria.proto().recordReceivedDate(), reportSince);
            criteria.le(criteria.proto().recordReceivedDate(), reportBefore);
            // Ignore TenantSure and Equifax payment gracefully for now
            criteria.isNull(criteria.proto().merchantAccount());
            int count = Persistence.service().count(criteria);
            if (count > 0) {
                CardsReconciliationRecord instance = Persistence.service().retrieve(criteria);
                ServerSideFactory.create(OperationsAlertFacade.class).record(instance, "There are {0} UnPpocessed CardsReconciliationRecord(s)", count);
                executionMonitor.addFailedEvent("CardsReconciliationRecord", instance.totalDeposit().getValue());
            }
        }

    }

    public void heathMonitorPmc(LogicalDate forDate) {
        verifyFundsTransferPmc(forDate);
        verifyCardTransactionsPmc(forDate);
    }

    private void verifyFundsTransferPmc(LogicalDate forDate) {
        // see if we received and processed reconciliation report
        {
            Date reportSince = DateUtils.addMonths(forDate, -2);
            Date reportBefore = DateUtils.addDays(forDate, -2);
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.ge(criteria.proto().finalizedDate(), reportSince);
            criteria.le(criteria.proto().finalizedDate(), reportBefore);
            criteria.in(criteria.proto().paymentMethod().type(), PaymentType.Echeck, PaymentType.DirectBanking);
            criteria.in(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Cleared, PaymentRecord.PaymentStatus.Queued,
                    PaymentRecord.PaymentStatus.Received);
            criteria.isNull(criteria.proto().aggregatedTransfer());
            int count = Persistence.service().count(criteria);
            if (count > 0) {
                PaymentRecord instance = Persistence.service().retrieve(criteria);
                ServerSideFactory.create(OperationsAlertFacade.class).record(instance, " {0} EFT Payment Records do not have Aggregated Transfer", count);
                executionMonitor.addFailedEvent("EftAggregatedTransfer", instance.amount().getValue());
            }
        }

        {
            Date reportSince = DateUtils.addDays(forDate, -7);
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.le(criteria.proto().lastStatusChangeDate(), reportSince);
            criteria.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Queued);
            int count = Persistence.service().count(criteria);
            if (count > 0) {
                PaymentRecord instance = Persistence.service().retrieve(criteria);
                ServerSideFactory.create(OperationsAlertFacade.class).record(instance, "There are {0} Payment Records Queued for a week", count);
                executionMonitor.addFailedEvent("QueuedPaymentRecord", instance.amount().getValue());
            }
        }
    }

    private void verifyCardTransactionsPmc(LogicalDate forDate) {
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        // see if caledon created reconciliation report
        {
            Date reportSince = DateUtils.addMonths(forDate, -2);
            Date reportBefore = DateUtils.addDays(forDate, -3);
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.ge(criteria.proto().createdDate(), reportSince);
            criteria.le(criteria.proto().createdDate(), reportBefore);
            criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.CreditCard);
            criteria.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Received);
            criteria.isNull(criteria.proto().aggregatedTransfer());
            int count = Persistence.service().count(criteria);
            if (count > 0) {
                PaymentRecord instance = Persistence.service().retrieve(criteria);
                ServerSideFactory.create(OperationsAlertFacade.class).record(instance, "{0} Received Card Payment Records Are not Cleared", count);
                executionMonitor.addFailedEvent("CardsAggregatedTransfer", instance.amount().getValue());
            }
        }
        {
            Date reportSince = DateUtils.addMonths(forDate, -2);
            Date reportBefore = DateUtils.addDays(forDate, -4);
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.ge(criteria.proto().finalizedDate(), reportSince);
            criteria.le(criteria.proto().finalizedDate(), reportBefore);
            criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.CreditCard);
            criteria.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Cleared);
            criteria.isNull(criteria.proto().aggregatedTransfer());
            int count = Persistence.service().count(criteria);
            if (count > 0) {
                PaymentRecord instance = Persistence.service().retrieve(criteria);
                ServerSideFactory.create(OperationsAlertFacade.class).record(instance, "{0} Cleared Card Payment Records do not have Aggregated Transfer",
                        count);
                executionMonitor.addFailedEvent("CardsAggregatedTransfer", instance.amount().getValue());
            }
        }

        {
            Date reportSince = DateUtils.addMonths(forDate, -2);
            EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
            criteria.ge(criteria.proto().createdDate(), reportSince);
            criteria.eq(criteria.proto().paymentMethod().type(), PaymentType.CreditCard);
            ICursorIterator<PaymentRecord> iterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
            try {
                while (iterator.hasNext()) {
                    PaymentRecord paymentRecord = iterator.next();
                    CardTransactionRecord cardTransactionRecord = getCardTransactionRecord(pmc, paymentRecord);
                    if (cardTransactionRecord == null) {
                        ServerSideFactory.create(OperationsAlertFacade.class).record(paymentRecord, "{0} Card Payment Record do not have TransactionRecord",
                                paymentRecord.id());
                        executionMonitor.addFailedEvent("CardTransaction", paymentRecord.amount().getValue());
                    } else {
                        boolean statusMismatch = false;
                        if (EnumSet.of(PaymentRecord.PaymentStatus.Cleared, PaymentRecord.PaymentStatus.Received).contains(
                                paymentRecord.paymentStatus().getValue())) {
                            if (!"0000".equals(cardTransactionRecord.saleResponseCode().getValue()) || cardTransactionRecord.voided().getValue(false)) {
                                statusMismatch = true;
                            }
                        } else if (("0000".equals(cardTransactionRecord.saleResponseCode().getValue())) && !cardTransactionRecord.voided().getValue(false)) {
                            statusMismatch = true;
                        }
                        if (statusMismatch) {
                            ServerSideFactory.create(OperationsAlertFacade.class).record(paymentRecord,
                                    "{0} Card Payment Record and TransactionRecord have status mismatch", paymentRecord.id());
                            executionMonitor.addFailedEvent("CardTransaction", paymentRecord.amount().getValue());
                        }
                    }
                }
            } finally {
                iterator.close();
            }

        }

    }

    private CardTransactionRecord getCardTransactionRecord(final Pmc pmc, final PaymentRecord paymentRecord) {
        return TaskRunner.runInOperationsNamespace(new Callable<CardTransactionRecord>() {
            @Override
            public CardTransactionRecord call() {
                EntityQueryCriteria<CardTransactionRecord> criteria = EntityQueryCriteria.create(CardTransactionRecord.class);
                criteria.eq(criteria.proto().pmc(), pmc);
                criteria.in(criteria.proto().paymentTransactionId(), ServerSideFactory.create(CreditCardFacade.class)
                        .getProdAndTestTransactionreferenceNumbers(ReferenceNumberPrefix.RentPayments, paymentRecord.id()));
                return Persistence.service().retrieve(criteria);
            }
        });
    }
}
