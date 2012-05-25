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

import java.io.File;
import java.util.EnumSet;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.admin.domain.payment.pad.PadBatch;
import com.propertyvista.admin.domain.payment.pad.PadDebitRecord;
import com.propertyvista.admin.domain.payment.pad.PadFile;
import com.propertyvista.admin.domain.payment.pad.PadFile.FileAcknowledgmentStatus;
import com.propertyvista.payment.pad.CaledonPadAcknowledgmentParser;
import com.propertyvista.payment.pad.CaledonPadFileWriter;
import com.propertyvista.payment.pad.ak.PadAkBatch;
import com.propertyvista.payment.pad.ak.PadAkDebitRecord;
import com.propertyvista.payment.pad.ak.PadAkFile;

class PadCaledonAcknowledgement {

    PadFile processFile(File file) {
        PadAkFile akFile = new CaledonPadAcknowledgmentParser().parsReport(file);
        PadFile padFile;
        {
            EntityQueryCriteria<PadFile> criteria = EntityQueryCriteria.create(PadFile.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().fileCreationNumber(), akFile.fileCreationNumber().getValue()));
            criteria.add(PropertyCriterion.eq(criteria.proto().status(), PadFile.PadFileStatus.Sent));
            padFile = Persistence.service().retrieve(criteria);
            if (padFile == null) {
                throw new Error("Unexpected fileCreationNumber '" + akFile.fileCreationNumber().getValue() + "' in file " + file.getName());
            }
        }

        for (FileAcknowledgmentStatus acknowledgmentStatus : EnumSet.allOf(FileAcknowledgmentStatus.class)) {
            if (acknowledgmentStatus.getStatusCode().equals(akFile.acknowledgmentStatusCode().getValue())) {
                padFile.acknowledgmentStatus().setValue(acknowledgmentStatus);
                padFile.acknowledgmentStatusCode().setValue(akFile.acknowledgmentStatusCode().getValue());
                break;
            }
        }
        if (padFile.acknowledgmentStatus().isNull()) {
            throw new Error("Unexpected acknowledgmentStatusCode '" + akFile.acknowledgmentStatusCode().getValue() + "' in file " + file.getName());
        }

        padFile.acknowledged().setValue(Persistence.service().getTransactionSystemTime());

        if (akFile.acknowledgmentStatusCode().getValue().equals(FileAcknowledgmentStatus.Accepted)) {
            assertAcknowledgedValues(padFile, akFile);
            padFile.status().setValue(PadFile.PadFileStatus.Acknowledged);
            Persistence.service().merge(padFile);
            Persistence.service().commit();
        } else if (EnumSet.of(FileAcknowledgmentStatus.BatchAndTransactionReject, FileAcknowledgmentStatus.TransactionReject,
                FileAcknowledgmentStatus.BatchLevelReject).contains(padFile.acknowledgmentStatus().getValue())) {
            assertAcknowledgedValues(padFile, akFile);
            updateBatches(padFile, akFile);
            updateRecords(padFile, akFile);
            padFile.status().setValue(PadFile.PadFileStatus.Acknowledged);
            Persistence.service().merge(padFile);
            Persistence.service().commit();
        } else {
            padFile.status().setValue(PadFile.PadFileStatus.Invalid);
            Persistence.service().merge(padFile);
            Persistence.service().commit();
        }

        return padFile;
    }

    private void assertAcknowledgedValues(PadFile padFile, PadAkFile akFile) {
        if (!padFile.recordsCount().getValue().equals(Integer.valueOf(akFile.recordsCount().getValue()))) {
            throw new Error("Unexpected recordsCount '" + akFile.recordsCount().getValue() + "' != '" + padFile.recordsCount().getValue() + "' in akFile "
                    + akFile.fileCreationNumber().getValue());
        }
        if (!CaledonPadFileWriter.formatAmount(padFile.fileAmount().getValue()).equals(akFile.fileAmount().getValue())) {
            throw new Error("Unexpected fileAmount '" + akFile.fileAmount().getValue() + "' in akFile " + akFile.fileCreationNumber().getValue());
        }
    }

    private void updateBatches(PadFile padFile, PadAkFile akFile) {
        for (PadAkBatch akBatch : akFile.batches()) {
            EntityQueryCriteria<PadBatch> criteria = EntityQueryCriteria.create(PadBatch.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().padFile(), padFile));
            criteria.add(PropertyCriterion.eq(criteria.proto().batchNumber(), akBatch.batchId()));
            //criteria.add(PropertyCriterion.eq(criteria.proto().merchantTerminalId(), akBatch.terminalId()));
            PadBatch padBatch = Persistence.service().retrieve(criteria);
            if (padBatch == null) {
                throw new Error("Unexpected batchId '" + akBatch.batchId().getValue() + "', terminalId '" + akBatch.terminalId().getValue() + "' in akFile "
                        + akFile.fileCreationNumber().getValue());
            }

            // assert Acknowledged Values
            if (!padBatch.merchantTerminalId().getValue().equals(akBatch.terminalId().getValue())) {
                throw new Error("Unexpected terminalId '" + akBatch.terminalId().getValue() + "' in akFile " + akFile.fileCreationNumber().getValue());
            }
            if (!CaledonPadFileWriter.formatAmount(padBatch.batchAmount().getValue()).equals(akBatch.batchAmount().getValue())) {
                throw new Error("Unexpected batchAmount '" + akBatch.batchAmount().getValue() + "', terminalId '" + akBatch.terminalId().getValue()
                        + "' in akFile " + akFile.fileCreationNumber().getValue());
            }
            if (!padBatch.acknowledgmentStatusCode().isNull()) {
                throw new Error("Already acknowledged batchId '" + akBatch.batchId().getValue() + "', terminalId '" + akBatch.terminalId().getValue()
                        + "' in akFile " + akFile.fileCreationNumber().getValue());
            }

            padBatch.acknowledgmentStatusCode().setValue(akBatch.acknowledgmentStatusCode().getValue());
            Persistence.service().merge(padBatch);
        }
    }

    private void updateRecords(PadFile padFile, PadAkFile akFile) {
        for (PadAkDebitRecord akDebitRecord : akFile.records()) {
            EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().padBatch().padFile(), padFile));
            criteria.add(PropertyCriterion.eq(criteria.proto().transactionId(), akDebitRecord.transactionId()));
            //criteria.add(PropertyCriterion.eq(criteria.proto().clientId(), akDebitRecord.clientId()));
            PadDebitRecord padDebitRecord = Persistence.service().retrieve(criteria);
            if (padDebitRecord == null) {
                throw new Error("Unexpected transactionId '" + akDebitRecord.transactionId().getValue() + "', clientId '" + akDebitRecord.clientId().getValue()
                        + "' in akFile " + akFile.fileCreationNumber().getValue());
            }
            // assert Acknowledged Values
            if (!CaledonPadFileWriter.formatAmount(padDebitRecord.amount().getValue()).equals(akDebitRecord.amount().getValue())) {
                throw new Error("Unexpected recordAmount '" + padDebitRecord.amount().getValue() + "', terminalId '" + akDebitRecord.terminalId().getValue()
                        + "' in akFile " + akFile.fileCreationNumber().getValue());
            }
            if (!padDebitRecord.clientId().getValue().equals(akDebitRecord.clientId().getValue())) {
                throw new Error("Unexpected clientId '" + padDebitRecord.clientId().getValue() + "', terminalId '" + akDebitRecord.terminalId().getValue()
                        + "' in akFile " + akFile.fileCreationNumber().getValue());
            }
            if (!padDebitRecord.acknowledgmentStatusCode().isNull()) {
                throw new Error("Already acknowledged transactionId '" + akDebitRecord.transactionId().getValue() + "', clientId '"
                        + akDebitRecord.clientId().getValue() + "' in akFile " + akFile.fileCreationNumber().getValue());
            }

            padDebitRecord.acknowledgmentStatusCode().setValue(akDebitRecord.acknowledgmentStatusCode().getValue());
            Persistence.service().merge(padDebitRecord);
        }
    }
}
