/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.EnumSet;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadBatchProcessingStatus;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecordProcessingStatus;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.payment.pad.PadFile.FileAcknowledgmentStatus;
import com.propertyvista.payment.pad.CaledonPadUtils;
import com.propertyvista.payment.pad.data.PadAckBatch;
import com.propertyvista.payment.pad.data.PadAckDebitRecord;
import com.propertyvista.payment.pad.data.PadAckFile;

class FundsTransferCaledonAcknowledgement {

    private final ExecutionMonitor executionMonitor;

    FundsTransferCaledonAcknowledgement(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    void validateAndPersistFile(PadAckFile ackFile) {
        PadFile padFile;
        {
            EntityQueryCriteria<PadFile> criteria = EntityQueryCriteria.create(PadFile.class);
            criteria.eq(criteria.proto().fileCreationNumber(), ackFile.fileCreationNumber());
            criteria.eq(criteria.proto().fundsTransferType(), ackFile.fundsTransferType());
            padFile = Persistence.service().retrieve(criteria);
            if (padFile == null) {
                throw new Error("Unexpected fileCreationNumber '" + ackFile.fileCreationNumber().getValue() + "' in file " + ackFile.fileName().getValue());
            }
            if (padFile.status().getValue() != PadFile.PadFileStatus.Sent) {
                throw new Error("Unexpected file status '" + padFile.status().getValue() + "' for file " + ackFile.fileName().getValue());
            }
        }

        for (FileAcknowledgmentStatus acknowledgmentStatus : EnumSet.allOf(FileAcknowledgmentStatus.class)) {
            if (acknowledgmentStatus.getStatusCode().equals(ackFile.acknowledgmentStatusCode().getValue())) {
                padFile.acknowledgmentStatus().setValue(acknowledgmentStatus);
                padFile.acknowledgmentStatusCode().setValue(ackFile.acknowledgmentStatusCode().getValue());
                break;
            }
        }
        if (padFile.acknowledgmentStatus().isNull()) {
            throw new Error("Unexpected acknowledgmentStatusCode '" + ackFile.acknowledgmentStatusCode().getValue() + "' in file "
                    + ackFile.fileName().getValue());
        }

        padFile.acknowledged().setValue(SystemDateManager.getDate());
        padFile.acknowledgmentRejectReasonMessage().setValue(ackFile.acknowledgmentRejectReasonMessage().getValue());

        if (padFile.acknowledgmentStatus().getValue() == FileAcknowledgmentStatus.Accepted) {
            assertAcknowledgedValues(padFile, ackFile);
            if (ackFile.batches().size() > 0) {
                throw new Error("Unexpected batches rejects for acknowledgmentStatus '" + ackFile.acknowledgmentStatusCode().getValue() + "' in file "
                        + ackFile.fileName().getValue());
            }
            if (ackFile.records().size() > 0) {
                throw new Error("Unexpected record level rejects for acknowledgmentStatus '" + ackFile.acknowledgmentStatusCode().getValue() + "' in file "
                        + ackFile.fileName().getValue());
            }
            padFile.status().setValue(PadFile.PadFileStatus.Acknowledged);
            Persistence.service().merge(padFile);

            markAcknowledgedReceived(padFile);

            executionMonitor.setMessage("All Accepted");
            executionMonitor.addProcessedEvent("fileStatus", padFile.acknowledgmentStatusCode().getValue());
        } else if (EnumSet.of(FileAcknowledgmentStatus.BatchAndTransactionReject, FileAcknowledgmentStatus.TransactionReject,
                FileAcknowledgmentStatus.BatchLevelReject).contains(padFile.acknowledgmentStatus().getValue())) {
            assertAcknowledgedValues(padFile, ackFile);
            updateBatches(padFile, ackFile);
            updateRecords(padFile, ackFile);
            padFile.status().setValue(PadFile.PadFileStatus.Acknowledged);
            Persistence.service().merge(padFile);

            markAcknowledgedReceived(padFile);

            executionMonitor.addProcessedEvent("fileStatus", padFile.acknowledgmentStatusCode().getValue());
        } else {
            padFile.status().setValue(PadFile.PadFileStatus.Invalid);
            Persistence.service().merge(padFile);

            executionMonitor.setMessage("File not Accepted");
            executionMonitor.addErredEvent("fileStatus", padFile.acknowledgmentStatusCode().getValue());
        }
    }

    private void assertAcknowledgedValues(PadFile padFile, PadAckFile ackFile) {
        if (!padFile.recordsCount().getValue().equals(Integer.valueOf(ackFile.recordsCount().getValue()))) {
            throw new Error("Unexpected recordsCount '" + ackFile.recordsCount().getValue() + "' != '" + padFile.recordsCount().getValue() + "'; in akFile "
                    + ackFile.fileCreationNumber().getValue());
        }
        if (!CaledonPadUtils.formatAmount(padFile.fileAmount().getValue()).equals(ackFile.fileAmount().getValue())) {
            throw new Error("Unexpected fileAmount '" + ackFile.fileAmount().getValue() + "', expected'" + padFile.fileAmount().getValue() + "'; in akFile "
                    + ackFile.fileCreationNumber().getValue());
        }
    }

    private void updateBatches(PadFile padFile, PadAckFile ackFile) {
        for (PadAckBatch akBatch : ackFile.batches()) {
            EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
            criteria.eq(criteria.proto().padFile(), padFile);
            criteria.eq(criteria.proto().batchNumber(), Integer.valueOf(akBatch.batchId().getValue()));
            PadBatch padBatch = Persistence.service().retrieve(criteria);
            if (padBatch == null) {
                throw new Error("Unexpected batchId '" + akBatch.batchId().getValue() + "', terminalId '" + akBatch.terminalId().getValue() + "' in akFile "
                        + ackFile.fileCreationNumber().getValue());
            }

            // assert Acknowledged Values
            if (!padBatch.merchantTerminalId().getValue().equals(akBatch.terminalId().getValue())) {
                throw new Error("Unexpected terminalId '" + akBatch.terminalId().getValue() + "' in akFile " + ackFile.fileCreationNumber().getValue());
            }
            if (!CaledonPadUtils.formatAmount(padBatch.batchAmount().getValue()).equals(akBatch.batchAmount().getValue())) {
                throw new Error("Unexpected batchAmount '" + akBatch.batchAmount().getValue() + "', terminalId '" + akBatch.terminalId().getValue()
                        + "' in akFile " + ackFile.fileCreationNumber().getValue());
            }
            if (!padBatch.acknowledgmentStatusCode().isNull()) {
                throw new Error("Already acknowledged batchId '" + akBatch.batchId().getValue() + "', terminalId '" + akBatch.terminalId().getValue()
                        + "' in akFile " + ackFile.fileCreationNumber().getValue());
            }

            padBatch.processingStatus().setValue(PadBatchProcessingStatus.AcknowledgedReceived);
            padBatch.acknowledgmentStatusCode().setValue(akBatch.acknowledgmentStatusCode().getValue());
            Persistence.service().merge(padBatch);

            executionMonitor.addFailedEvent("Batch Rejected", padBatch.batchAmount().getValue());
        }
    }

    private void updateRecords(PadFile padFile, PadAckFile ackFile) {
        for (PadAckDebitRecord akDebitRecord : ackFile.records()) {
            EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
            criteria.eq(criteria.proto().padBatch().padFile(), padFile);
            criteria.eq(criteria.proto().transactionId(), akDebitRecord.transactionId());
            PadDebitRecord padDebitRecord = Persistence.service().retrieve(criteria);
            if (padDebitRecord == null) {
                throw new Error("Unexpected transactionId '" + akDebitRecord.transactionId().getValue() + "', clientId '" + akDebitRecord.clientId().getValue()
                        + "' in akFile " + ackFile.fileCreationNumber().getValue());
            }
            // assert Acknowledged Values
            if (!CaledonPadUtils.formatAmount(padDebitRecord.amount().getValue()).equals(akDebitRecord.amount().getValue())) {
                throw new Error("Unexpected recordAmount '" + padDebitRecord.amount().getValue() + "', terminalId '" + akDebitRecord.terminalId().getValue()
                        + "' in akFile " + ackFile.fileCreationNumber().getValue());
            }
            if (!padDebitRecord.clientId().getValue().equals(akDebitRecord.clientId().getValue())) {
                throw new Error("Unexpected clientId '" + padDebitRecord.clientId().getValue() + "', terminalId '" + akDebitRecord.terminalId().getValue()
                        + "' in akFile " + ackFile.fileCreationNumber().getValue());
            }
            if (!padDebitRecord.acknowledgmentStatusCode().isNull()) {
                throw new Error("Already acknowledged transactionId '" + akDebitRecord.transactionId().getValue() + "', clientId '"
                        + akDebitRecord.clientId().getValue() + "' in akFile " + ackFile.fileCreationNumber().getValue());
            }

            if (padDebitRecord.processed().isBooleanTrue()) {
                throw new Error("Already processed transactionId '" + akDebitRecord.transactionId().getValue() + "', clientId '"
                        + akDebitRecord.clientId().getValue() + "' in akFile " + ackFile.fileCreationNumber().getValue());
            }

            padDebitRecord.processingStatus().setValue(PadDebitRecordProcessingStatus.AcknowledgedReceived);
            padDebitRecord.acknowledgmentStatusCode().setValue(akDebitRecord.acknowledgmentStatusCode().getValue());
            Persistence.service().merge(padDebitRecord);

            executionMonitor.addFailedEvent("Record Rejected", padDebitRecord.amount().getValue());
        }
    }

    private void markAcknowledgedReceived(PadFile padFile) {
        {
            EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
            criteria.eq(criteria.proto().padBatch().padFile(), padFile);
            criteria.isNull(criteria.proto().processingStatus());
            for (PadDebitRecord padDebitRecord : Persistence.service().query(criteria)) {
                padDebitRecord.processingStatus().setValue(PadDebitRecordProcessingStatus.AcknowledgedReceived);
                executionMonitor.addProcessedEvent("Record Acknowledged", padDebitRecord.amount().getValue());
                Persistence.service().persist(padDebitRecord);
            }
        }

        {
            EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
            criteria.eq(criteria.proto().padFile(), padFile);
            criteria.isNull(criteria.proto().processingStatus());
            for (PadBatch padBatch : Persistence.service().query(criteria)) {
                padBatch.processingStatus().setValue(PadBatchProcessingStatus.AcknowledgedReceived);
                executionMonitor.addProcessedEvent("Batch Acknowledged");
                Persistence.service().persist(padBatch);
            }
        }
    }
}
