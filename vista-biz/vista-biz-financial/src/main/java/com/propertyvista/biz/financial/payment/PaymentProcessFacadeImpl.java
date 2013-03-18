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
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.payment.pad.PadFile.FileAcknowledgmentStatus;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationSummary;
import com.propertyvista.server.jobs.TaskRunner;

public class PaymentProcessFacadeImpl implements PaymentProcessFacade {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessFacadeImpl.class);

    private static final String PROCESSED = "Processed";

    private static final String REJECTED = "Rejected";

    private static final String ERRED = "Erred";

    @Override
    public PadFile preparePadFile() {
        return new PadCaledon().preparePadFile();
    }

    @Override
    public boolean sendPadFile(final PadFile padFile) {
        return new UnitOfWork(TransactionScopeOption.Suppress).execute(new Executable<Boolean, RuntimeException>() {
            @Override
            public Boolean execute() {
                return new PadCaledon().sendPadFile(padFile);
            }
        });
    }

    @Override
    public void prepareEcheckPayments(final ExecutionMonitor executionMonitor, final PadFile padFile) {
        // We take all Queued records in this PMC
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Queued));
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentMethod().type(), PaymentType.Echeck));
        ICursorIterator<PaymentRecord> paymentRecordIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (paymentRecordIterator.hasNext()) {

                final PaymentRecord paymentRecord = paymentRecordIterator.next();

                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() {
                        if (new PadProcessor().processPayment(paymentRecord, padFile)) {
                            executionMonitor.addProcessedEvent("Processed amount", paymentRecord.amount().getValue());
                        } else {
                            executionMonitor.addFailedEvent("No Merchant Account", paymentRecord.amount().getValue());
                        }
                        return null;
                    }

                });
                // If there are error we may create new run again.

            }
        } finally {
            paymentRecordIterator.completeRetrieval();
        }
    }

    @Override
    public PadFile receivePadAcknowledgementFile() {
        return new PadCaledon().receivePadAcknowledgementFile();
    }

    @Override
    public void processAcknowledgement(final ExecutionMonitor executionMonitor, final PadFile padFile) {
        if (!EnumSet.of(FileAcknowledgmentStatus.BatchAndTransactionReject, FileAcknowledgmentStatus.TransactionReject,
                FileAcknowledgmentStatus.BatchLevelReject, FileAcknowledgmentStatus.Accepted).contains(padFile.acknowledgmentStatus().getValue())) {
            throw new Error("Invalid pad file acknowledgmentStatus " + padFile.acknowledgmentStatus().getValue());
        }

        final String namespace = NamespaceManager.getNamespace();
        List<PadDebitRecord> rejectedRecodrs = TaskRunner.runInOperationsNamespace(new Callable<List<PadDebitRecord>>() {
            @Override
            public List<PadDebitRecord> call() {
                EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().padFile(), padFile));
                criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().pmcNamespace(), namespace));
                criteria.add(PropertyCriterion.isNotNull(criteria.proto().acknowledgmentStatusCode()));
                return Persistence.service().query(criteria);
            }
        });

        for (PadDebitRecord debitRecord : rejectedRecodrs) {
            new PadProcessor().acknowledgmentReject(debitRecord);
            executionMonitor.addFailedEvent("Debit Record rejected", debitRecord.amount().getValue());
        }

        List<PadBatch> rejectedBatch = TaskRunner.runInOperationsNamespace(new Callable<List<PadBatch>>() {
            @Override
            public List<PadBatch> call() {
                EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().padFile(), padFile));
                criteria.add(PropertyCriterion.eq(criteria.proto().pmcNamespace(), namespace));
                criteria.add(PropertyCriterion.isNotNull(criteria.proto().acknowledgmentStatusCode()));
                List<PadBatch> rejectedBatch = Persistence.service().query(criteria);
                for (PadBatch padBatch : rejectedBatch) {
                    Persistence.service().retrieveMember(padBatch.records());
                    executionMonitor.addFailedEvent("Pad Batch rejected", padBatch.batchAmount().getValue());
                }
                return rejectedBatch;
            }
        });

        for (PadBatch padBatch : rejectedBatch) {
            new PadProcessor().aggregatedTransferRejected(padBatch);
        }

        if (rejectedBatch.size() == 0 && rejectedRecodrs.size() == 0) {
            Integer countBatchs = TaskRunner.runInOperationsNamespace(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
                    criteria.add(PropertyCriterion.eq(criteria.proto().padFile(), padFile));
                    criteria.add(PropertyCriterion.eq(criteria.proto().pmcNamespace(), namespace));
                    return Persistence.service().count(criteria);
                }
            });
            if (countBatchs > 0) {
                executionMonitor.setMessage("All Accepted");
            }
        }
    }

    @Override
    public void updatePadFileAcknowledProcessingStatus(PadFile padFileId) {
        PadFile padFile = Persistence.service().retrieve(PadFile.class, padFileId.getPrimaryKey());
        if (padFile.status().getValue() != PadFile.PadFileStatus.Acknowledged) {
            throw new IllegalArgumentException(SimpleMessageFormat.format("Invalid PadFile {0} status {1}", padFile.id(), padFile.status()));
        }
        padFile.status().setValue(PadFile.PadFileStatus.AcknowledgeProcesed);

        EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().padFile(), padFile));
        criteria.add(PropertyCriterion.eq(criteria.proto().processed(), Boolean.FALSE));
        int unprocessedRecords = Persistence.service().count(criteria);
        if (unprocessedRecords == 0) {
            padFile.status().setValue(PadFile.PadFileStatus.Procesed);
        }
        Persistence.service().persist(padFile);
    }

    @Override
    public void updatePadFileReconciliationProcessingStatus() {
        EntityQueryCriteria<PadFile> filesCriteria = EntityQueryCriteria.create(PadFile.class);
        filesCriteria.add(PropertyCriterion.eq(filesCriteria.proto().status(), PadFile.PadFileStatus.AcknowledgeProcesed));
        for (PadFile padFile : Persistence.service().query(filesCriteria)) {
            EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().padFile(), padFile));
            criteria.add(PropertyCriterion.eq(criteria.proto().processed(), Boolean.FALSE));
            int unprocessedRecords = Persistence.service().count(criteria);
            if (unprocessedRecords == 0) {
                padFile.status().setValue(PadFile.PadFileStatus.Procesed);
                Persistence.service().persist(padFile);
            }
        }
    }

    @Override
    public PadReconciliationFile receivePadReconciliation() {
        return new PadCaledon().receivePadReconciliation();
    }

    @Override
    public void processPadReconciliation(ExecutionMonitor executionMonitor, final PadReconciliationFile reconciliationFile) {
        final String namespace = NamespaceManager.getNamespace();

        List<PadReconciliationSummary> transactions = TaskRunner.runInOperationsNamespace(new Callable<List<PadReconciliationSummary>>() {
            @Override
            public List<PadReconciliationSummary> call() throws Exception {
                EntityQueryCriteria<PadReconciliationSummary> criteria = EntityQueryCriteria.create(PadReconciliationSummary.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().reconciliationFile(), reconciliationFile));
                criteria.add(PropertyCriterion.eq(criteria.proto().merchantAccount().pmc().namespace(), namespace));

                return Persistence.service().query(criteria);
            }
        });

        if (transactions.size() == 0) {
            return;
        }

        for (PadReconciliationSummary summary : transactions) {
            new PadProcessor().aggregatedTransferReconciliation(executionMonitor, summary);
        }
    }

    @Override
    public void createPreauthorisedPayments(ExecutionMonitor executionMonitor, LogicalDate runDate) {
        new PreauthorisedPaymentsManager().createPreauthorisedPayments(executionMonitor, runDate);
    }

    @Override
    public void updateScheduledPreauthorisedPayments(ExecutionMonitor executionMonitor, LogicalDate runDate) {
        new PreauthorisedPaymentsManager().updateScheduledPreauthorisedPayments(executionMonitor, runDate);
    }

    @Override
    public void processScheduledPayments(ExecutionMonitor executionMonitor, PaymentType paymentType) {
        EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentStatus(), PaymentRecord.PaymentStatus.Scheduled));
        criteria.add(PropertyCriterion.eq(criteria.proto().paymentMethod().type(), paymentType));
        criteria.add(PropertyCriterion.le(criteria.proto().targetDate(), SystemDateManager.getDate()));

        ICursorIterator<PaymentRecord> paymentRecordIterator = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (paymentRecordIterator.hasNext()) {
                processScheduledPayment(paymentRecordIterator.next(), executionMonitor);
            }
        } finally {
            paymentRecordIterator.completeRetrieval();
        }
    }

    private void processScheduledPayment(final PaymentRecord paymentRecord, final ExecutionMonitor executionMonitor) {
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, PaymentException>() {

                @Override
                public Void execute() throws PaymentException {
                    PaymentRecord processedPaymentRecord = ServerSideFactory.create(PaymentFacade.class).processPayment(paymentRecord);

                    if (processedPaymentRecord.paymentStatus().getValue() == PaymentRecord.PaymentStatus.Rejected) {
                        executionMonitor.addFailedEvent(//@formatter:off
                                REJECTED,
                                processedPaymentRecord.amount().getValue(),
                                SimpleMessageFormat.format("Payment was rejected")
                        );//@formatter:on
                    } else {
                        executionMonitor.addProcessedEvent(//@formatter:off
                                PROCESSED,
                                processedPaymentRecord.amount().getValue(),
                                SimpleMessageFormat.format("Payment was processed")
                        );//@formatter:on
                    }
                    return null;
                }
            });
        } catch (PaymentException e) {
            log.error("Preauthorised payment creation failed", e);
            executionMonitor.addErredEvent(ERRED, e);
        }
    }

}
