/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-09
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.pad.mock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.payment.pad.data.PadAckFile;

class EFTBankMock {

    private static final Logger log = LoggerFactory.getLogger(EFTBankMockReconciliation.class);

    private EFTBankMock() {
    }

    private static class SingletonHolder {
        public static final EFTBankMock INSTANCE = new EFTBankMock();
    }

    static EFTBankMock instance() {
        return SingletonHolder.INSTANCE;
    }

    private final List<PadFile> receivedPadFile = new ArrayList<PadFile>();

    private final List<PadDebitRecord> unprocessedRecords = new ArrayList<PadDebitRecord>();

    private final List<PadDebitRecord> reconciliationRecords = new ArrayList<PadDebitRecord>();

    private final EFTBankMockAck acknowledgment = new EFTBankMockAck();

    private final EFTBankMockReconciliation reconciliation = new EFTBankMockReconciliation();

    void receivedPadFile(PadFile padFile) {
        log.debug("receivedPadFile {}, records {}", padFile.fileCreationNumber().getValue(), padFile.recordsCount().getValue());
        receivedPadFile.add(padFile.<PadFile> duplicate());
        DataDump.dumpToDirectory("eft", "pad", padFile);
    }

    void addAcknowledgedRecord(PadDebitRecord padDebitRecord) {
        log.debug("Acknowledged transactionId:{}", padDebitRecord.transactionId().getValue());
        unprocessedRecords.add(padDebitRecord);
    }

    PadAckFile acknowledgeFile(String companyId) {
        // Find unacknowledged file
        PadFile unacknowledgedFile = null;
        for (PadFile padFile : receivedPadFile) {
            if (padFile.companyId().getValue().equals(companyId)) {
                unacknowledgedFile = padFile;
                break;
            }
        }

        if (unacknowledgedFile == null) {
            log.debug("No file acknowledge for companyId {} ", companyId);
            return null;
        } else {
            receivedPadFile.remove(unacknowledgedFile);
            PadAckFile ack = acknowledgment.createAcknowledgementFile(unacknowledgedFile);
            DataDump.dumpToDirectory("eft", "acknowledgment", ack);
            return ack;
        }
    }

    public PadReconciliationFile reconciliationFile(String companyId) {
        List<PadDebitRecord> records = new ArrayList<PadDebitRecord>();
        Iterator<PadDebitRecord> it = unprocessedRecords.iterator();
        while (it.hasNext()) {
            PadDebitRecord padRecord = it.next();
            if (padRecord.padBatch().padFile().companyId().getValue().equals(companyId)) {
                it.remove();
                records.add(padRecord);
            }
        }
        if (records.size() == 0) {
            log.debug("No unprocessed records for companyId {} ", companyId);
            return null;
        } else {
            reconciliationRecords.addAll(records);
            PadReconciliationFile rec = reconciliation.createReconciliationFile(records);
            //TODO
            rec.fundsTransferType().setValue(records.get(0).padBatch().padFile().fundsTransferType().getValue());
            DataDump.dumpToDirectory("eft", "reconciliation", rec);
            return rec;
        }
    }
}
