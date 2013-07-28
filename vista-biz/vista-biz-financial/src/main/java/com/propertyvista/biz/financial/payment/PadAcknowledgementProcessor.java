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

import org.apache.commons.lang.Validate;
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
import com.propertyvista.domain.financial.PaymentRecordProcessing;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadBatchProcessingStatus;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecordProcessingStatus;
import com.propertyvista.server.jobs.TaskRunner;

class PadAcknowledgementProcessor {

    private static final Logger log = LoggerFactory.getLogger(PadAcknowledgementProcessor.class);

    private final ExecutionMonitor executionMonitor;

    PadAcknowledgementProcessor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    void processPmcAcknowledgement() {

        final Pmc pmc = VistaDeployment.getCurrentPmc();

        List<PadBatch> batchList = TaskRunner.runInOperationsNamespace(new Callable<List<PadBatch>>() {
            @Override
            public List<PadBatch> call() {
                EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
                criteria.eq(criteria.proto().padFile().fundsTransferType(), FundsTransferType.PreAuthorizedDebit);
                criteria.eq(criteria.proto().pmc(), pmc);
                criteria.eq(criteria.proto().processingStatus(), PadBatchProcessingStatus.AcknowledgedReceived);
                List<PadBatch> batchList = Persistence.service().query(criteria);
                for (PadBatch padBatch : batchList) {
                    Persistence.service().retrieveMember(padBatch.records());
                }
                return batchList;
            }
        });

        for (final PadBatch padBatch : batchList) {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                @Override
                public Void execute() {
                    processPadBatch(padBatch);
                    return null;
                }

            });
        }

    }

    private void processPadBatch(final PadBatch padBatch) {
        if (!padBatch.acknowledgmentStatusCode().isNull()) {
            processPadBatchReject(padBatch);
        } else {
            processPadBatchRecords(padBatch);
        }
    }

    private void processPadBatchReject(final PadBatch padBatch) {
        for (PadDebitRecord debitRecord : padBatch.records()) {
            Validate.isTrue(debitRecord.processingStatus().getValue() == PadDebitRecordProcessingStatus.AcknowledgedReceived,
                    "Invalid PadDebitRecord records status");
        }
        aggregatedTransferRejected(padBatch);

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                padBatch.processingStatus().setValue(PadBatchProcessingStatus.AcknowledgeReject);
                Persistence.service().persist(padBatch);

                // mark BatchRecords as AcknowledgeProcesed
                for (PadDebitRecord debitRecord : padBatch.records()) {
                    debitRecord.processingStatus().setValue(PadDebitRecordProcessingStatus.AcknowledgeReject);
                    Persistence.service().persist(debitRecord);
                }
                return null;
            }
        });

        executionMonitor.addFailedEvent("Pad Batch Rejected", padBatch.batchAmount().getValue());
    }

    private void processPadBatchRecords(final PadBatch padBatch) {
        // there still maybe individual rejected records

        int unprocessedRecordsCount = 0;
        for (final PadDebitRecord debitRecord : padBatch.records()) {
            if (debitRecord.processingStatus().getValue() == PadDebitRecordProcessingStatus.AcknowledgedReceived) {

                try {
                    new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                        @Override
                        public Void execute() {
                            processPadRecord(debitRecord);
                            return null;
                        }

                    });

                } catch (Throwable e) {
                    unprocessedRecordsCount++;
                    log.error("PadDebitRecord {} processing failed", debitRecord, e);
                    executionMonitor.addErredEvent("DebitRecordErred", debitRecord.amount().getValue(),
                            SimpleMessageFormat.format("DebitRecord {0} {1}", debitRecord.id(), debitRecord), e);
                }

            }
        }

        if (unprocessedRecordsCount == 0) {
            TaskRunner.runInOperationsNamespace(new Callable<Void>() {
                @Override
                public Void call() {
                    padBatch.processingStatus().setValue(PadBatchProcessingStatus.AcknowledgeProcesed);
                    Persistence.service().persist(padBatch);
                    return null;
                }
            });
            executionMonitor.addProcessedEvent("Pad Batch Acknowledged", padBatch.batchAmount().getValue());
        }

    }

    private void processPadRecord(final PadDebitRecord debitRecord) {
        if (!debitRecord.acknowledgmentStatusCode().isNull()) {
            acknowledgmentReject(debitRecord);
        }

        TaskRunner.runInOperationsNamespace(new Callable<Void>() {
            @Override
            public Void call() {
                debitRecord.processingStatus().setValue(PadDebitRecordProcessingStatus.AcknowledgeProcesed);
                Persistence.service().persist(debitRecord);
                return null;
            }
        });

        if (!debitRecord.acknowledgmentStatusCode().isNull()) {
            executionMonitor.addFailedEvent("Debit Record Rejected", debitRecord.amount().getValue());
        } else {
            executionMonitor.addProcessedEvent("Debit Record Acknowledged", debitRecord.amount().getValue());
        }
    }

    public void aggregatedTransferRejected(PadBatch padBatch) {
        AggregatedTransfer at = EntityFactory.create(AggregatedTransfer.class);
        at.status().setValue(AggregatedTransferStatus.Rejected);
        at.paymentDate().setValue(new LogicalDate(padBatch.padFile().created().getValue()));
        at.grossPaymentAmount().setValue(padBatch.batchAmount().getValue());
        at.grossPaymentCount().setValue(padBatch.records().size());
        at.merchantAccount().setPrimaryKey(padBatch.merchantAccountKey().getValue());

        // Find MerchantAccount
        {
            // TODO handle the case when merchant account was changed.
            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().id(), padBatch.merchantAccountKey()));
            criteria.add(PropertyCriterion.eq(criteria.proto().merchantTerminalId(), padBatch.merchantTerminalId()));
            MerchantAccount merchantAccount = Persistence.service().retrieve(criteria);
            if (merchantAccount == null) {
                throw new Error("Merchant Account '" + padBatch.merchantTerminalId().getValue() + "' not found");
            }
            merchantAccount.invalid().setValue(Boolean.TRUE);
            Persistence.service().persist(merchantAccount);
        }

        // Caledon status codes
        if ("1003".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            at.transactionErrorMessage().setValue("Invalid Terminal ID");
        } else if ("1004".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            at.transactionErrorMessage().setValue("Invalid Bank ID ");
        } else if ("1005".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            at.transactionErrorMessage().setValue("Invalid Bank Transit Number ");
        } else if ("1006".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            at.transactionErrorMessage().setValue("Invalid Bank Account Number ");
        } else if ("1007".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            at.transactionErrorMessage().setValue("Bank Information Mismatch");
        } else {
            at.transactionErrorMessage().setValue(padBatch.acknowledgmentStatusCode().getValue());
        }

        Persistence.service().persist(at);

        for (PadDebitRecord debitRecord : padBatch.records()) {
            PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class,
                    PadTransactionUtils.toVistaPaymentRecordId(debitRecord.transactionId()));
            if (paymentRecord == null) {
                throw new Error("Payment transaction '" + debitRecord.transactionId().getValue() + "' not found");
            }
            if (!EnumSet.of(PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
                throw new Error("Unexpected payment record status " + paymentRecord.getPrimaryKey() + " " + paymentRecord.paymentStatus().getValue());
            }
            // Update record status. Allow to ReSend automatically or Cancel Manually
            paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Queued);
            Persistence.service().persist(paymentRecord);

            PaymentRecordProcessing processing = EntityFactory.create(PaymentRecordProcessing.class);
            processing.paymentRecord().set(paymentRecord);
            processing.aggregatedTransfer().set(at);
            Persistence.service().persist(processing);
        }
    }

    public void acknowledgmentReject(PadDebitRecord debitRecord) {
        PaymentRecord paymentRecord = Persistence.service().retrieve(PaymentRecord.class,
                PadTransactionUtils.toVistaPaymentRecordId(debitRecord.transactionId()));
        if (!EnumSet.of(PaymentRecord.PaymentStatus.Processing, PaymentRecord.PaymentStatus.Received).contains(paymentRecord.paymentStatus().getValue())) {
            throw new Error("Processed payment can't be rejected");
        }
        if (PaymentType.Echeck != paymentRecord.paymentMethod().type().getValue()) {
            throw new IllegalArgumentException("Invalid PaymentMethod:" + paymentRecord.paymentMethod().type().getStringView());
        }
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
        paymentRecord.lastStatusChangeDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        paymentRecord.finalizeDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        // Caledon status codes
        if ("2001".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            paymentRecord.transactionErrorMessage().setValue("Invalid Amount");
        } else if ("2002".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            paymentRecord.transactionErrorMessage().setValue("Invalid Bank ID ");
        } else if ("2003".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            paymentRecord.transactionErrorMessage().setValue("Invalid Bank Transit Number ");
        } else if ("2004".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            paymentRecord.transactionErrorMessage().setValue("Invalid Bank Account Number ");
        } else if ("2005".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            paymentRecord.transactionErrorMessage().setValue("Invalid Reference Number");
        } else {
            paymentRecord.transactionErrorMessage().setValue(debitRecord.acknowledgmentStatusCode().getValue());
        }

        Persistence.service().merge(paymentRecord);

        try {
            ServerSideFactory.create(ARFacade.class).rejectPayment(paymentRecord, false);
        } catch (ARException e) {
            throw new Error("Processed payment can't be rejected", e);
        }

        log.info("Payment {} {} Rejected", paymentRecord.id().getValue(), paymentRecord.amount().getValue());
    }

}
