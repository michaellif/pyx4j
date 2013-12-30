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
package com.propertyvista.eft.mock.efttransport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.dev.DataDump;

import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.biz.system.eft.FileCreationException;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;
import com.propertyvista.operations.domain.eft.caledoneft.to.FundsTransferAckFile;
import com.propertyvista.operations.domain.eft.dbp.DirectDebitRecord;
import com.propertyvista.test.mock.MockEventBus;

class EFTBankMock implements ScheduledBmoPayment.Handler {

    private static final Logger log = LoggerFactory.getLogger(EFTBankMockReconciliation.class);

    private boolean connectionErrorEnabled;

    private EFTBankMock() {
        MockEventBus.addHandler(ScheduledBmoPayment.class, this);

        MockEventBus.addHandler(ScheduleTransportConnectionError.class, new ScheduleTransportConnectionError.Handler() {
            @Override
            public void scheduleTransportConnectionError(ScheduleTransportConnectionError event) {
                connectionErrorEnabled = event.connectionErrorEnabled;
            }
        });
    }

    private static class SingletonHolder {
        public static final EFTBankMock INSTANCE = new EFTBankMock();
    }

    static EFTBankMock instance() {
        return SingletonHolder.INSTANCE;
    }

    private final List<FundsTransferFile> receivedPadFile = new ArrayList<FundsTransferFile>();

    private final List<FundsTransferRecord> unprocessedRecords = new ArrayList<FundsTransferRecord>();

    private final List<FundsTransferRecord> reconciliationRecords = new ArrayList<FundsTransferRecord>();

    private final EFTBankMockAck acknowledgment = new EFTBankMockAck();

    private final EFTBankMockReconciliation reconciliation = new EFTBankMockReconciliation();

    private final List<DirectDebitRecord> scheduledBmoRecords = new ArrayList<DirectDebitRecord>();

    void receivedPadFile(FundsTransferFile padFile) throws SftpTransportConnectionException, FileCreationException {
        if (connectionErrorEnabled) {
            throw new SftpTransportConnectionException("Connection error Mock", null);
        }

        log.debug("receivedPadFile {}, records {}", padFile.fileCreationNumber().getValue(), padFile.recordsCount().getValue());
        receivedPadFile.add(padFile.<FundsTransferFile> duplicate());
        DataDump.dumpToDirectory("eft", "pad", padFile);
    }

    void addAcknowledgedRecord(FundsTransferRecord padDebitRecord) {
        log.debug("Acknowledged transactionId:{}", padDebitRecord.transactionId().getValue());
        unprocessedRecords.add(padDebitRecord);
    }

    FundsTransferAckFile acknowledgeFile(String companyId) throws SftpTransportConnectionException {
        if (connectionErrorEnabled) {
            throw new SftpTransportConnectionException("Connection error Mock", null);
        }

        // Find unacknowledged file
        FundsTransferFile unacknowledgedFile = null;
        for (FundsTransferFile padFile : receivedPadFile) {
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
            FundsTransferAckFile ack = acknowledgment.createAcknowledgementFile(unacknowledgedFile);
            DataDump.dumpToDirectory("eft", "acknowledgment", ack);
            return ack;
        }
    }

    public FundsReconciliationFile reconciliationFile(String companyId) throws SftpTransportConnectionException {
        if (connectionErrorEnabled) {
            throw new SftpTransportConnectionException("Connection error Mock", null);
        }

        List<FundsTransferRecord> records = new ArrayList<FundsTransferRecord>();
        Iterator<FundsTransferRecord> it = unprocessedRecords.iterator();
        while (it.hasNext()) {
            FundsTransferRecord padRecord = it.next();
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
            FundsReconciliationFile rec = reconciliation.createReconciliationFile(records);
            //TODO
            rec.fundsTransferType().setValue(records.get(0).padBatch().padFile().fundsTransferType().getValue());
            DataDump.dumpToDirectory("eft", "reconciliation", rec);
            return rec;
        }
    }

    @Override
    public void scheduleTransactionReconciliationResponse(ScheduledBmoPayment event) {
        DirectDebitRecord directDebitRecord = EntityFactory.create(DirectDebitRecord.class);
        directDebitRecord.amount().setValue(event.amount);
        directDebitRecord.paymentReferenceNumber().setValue(event.paymentReferenceNumber);
        directDebitRecord.accountNumber().setValue(event.accountNumber);
        scheduledBmoRecords.add(directDebitRecord);

    }
}
