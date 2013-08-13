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

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadBatchProcessingStatus;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecordProcessingStatus;
import com.propertyvista.server.jobs.TaskRunner;

abstract class AbstractAcknowledgementProcessor {

    private static final Logger log = LoggerFactory.getLogger(AbstractAcknowledgementProcessor.class);

    protected final ExecutionMonitor executionMonitor;

    private final FundsTransferType fundsTransferType;

    AbstractAcknowledgementProcessor(FundsTransferType fundsTransferType, ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
        this.fundsTransferType = fundsTransferType;
    }

    final void processPmcAcknowledgement() {
        final Pmc pmc = VistaDeployment.getCurrentPmc();

        List<PadBatch> batchList = TaskRunner.runInOperationsNamespace(new Callable<List<PadBatch>>() {
            @Override
            public List<PadBatch> call() {
                EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
                criteria.eq(criteria.proto().padFile().fundsTransferType(), fundsTransferType);
                criteria.eq(criteria.proto().pmc(), pmc);
                criteria.eq(criteria.proto().processingStatus(), PadBatchProcessingStatus.AcknowledgedReceived);
                List<PadBatch> batchList = Persistence.service().query(criteria);
                for (PadBatch padBatch : batchList) {
                    Persistence.service().retrieveMember(padBatch.records());
                    retrieveOperationsPadBatchDetails(padBatch);
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

    protected void retrieveOperationsPadBatchDetails(PadBatch padBatch) {
    }

    protected abstract void createRejectedAggregatedTransfer(PadBatch padBatch);

    protected abstract void acknowledgmentReject(PadDebitRecord debitRecord);

    private void processPadBatchReject(final PadBatch padBatch) {
        for (PadDebitRecord debitRecord : padBatch.records()) {
            Validate.isTrue(debitRecord.processingStatus().getValue() == PadDebitRecordProcessingStatus.AcknowledgedReceived,
                    "Invalid PadDebitRecord records status");
        }

        // Find MerchantAccount
        {
            // TODO handle the case when merchant account was changed.
            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
            criteria.eq(criteria.proto().id(), padBatch.merchantAccountKey());
            criteria.eq(criteria.proto().merchantTerminalId(), padBatch.merchantTerminalId());
            MerchantAccount merchantAccount = Persistence.service().retrieve(criteria);
            if (merchantAccount == null) {
                throw new Error("Merchant Account '" + padBatch.merchantTerminalId().getValue() + "' not found");
            }
            merchantAccount.invalid().setValue(Boolean.TRUE);
            Persistence.service().persist(merchantAccount);
        }

        createRejectedAggregatedTransfer(padBatch);

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

        executionMonitor.addFailedEvent("Funds Transfer Batch Rejected", padBatch.batchAmount().getValue());
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
                    padBatch.processingStatus().setValue(PadBatchProcessingStatus.AcknowledgeProcessed);
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
                debitRecord.processingStatus().setValue(PadDebitRecordProcessingStatus.AcknowledgeProcessed);
                Persistence.service().persist(debitRecord);
                return null;
            }
        });

        if (!debitRecord.acknowledgmentStatusCode().isNull()) {
            executionMonitor.addFailedEvent("Record Rejected", debitRecord.amount().getValue());
        } else {
            executionMonitor.addProcessedEvent("Record Acknowledged", debitRecord.amount().getValue());
        }
    }

    protected String getAcknowledgmentErrorMessage(PadBatch padBatch) {
        // Caledon status codes
        if ("1003".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            return "Invalid Terminal ID";
        } else if ("1004".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            return "Invalid Bank ID";
        } else if ("1005".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            return "Invalid Bank Transit Number";
        } else if ("1006".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            return "Invalid Bank Account Number";
        } else if ("1007".equals(padBatch.acknowledgmentStatusCode().getValue())) {
            return "Bank Information Mismatch";
        } else {
            return padBatch.acknowledgmentStatusCode().getValue();
        }
    }

    protected String getAcknowledgmentErrorMessage(PadDebitRecord debitRecord) {
        // Caledon status codes
        if ("2001".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            return "Invalid Amount";
        } else if ("2002".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            return "Invalid Bank ID ";
        } else if ("2003".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            return "Invalid Bank Transit Number";
        } else if ("2004".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            return "Invalid Bank Account Number";
        } else if ("2005".equals(debitRecord.acknowledgmentStatusCode().getValue())) {
            return "Invalid Reference Number";
        } else {
            return debitRecord.acknowledgmentStatusCode().getValue();
        }
    }
}
