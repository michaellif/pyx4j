/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.mock.efttransport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationRecordRecord;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationSummary;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;
import com.propertyvista.operations.domain.eft.caledoneft.MerchantReconciliationStatus;
import com.propertyvista.operations.domain.eft.caledoneft.TransactionReconciliationStatus;
import com.propertyvista.test.mock.MockEventBus;

class EFTBankMockReconciliation implements ScheduledResponseReconciliation.Handler {

    private static final Logger log = LoggerFactory.getLogger(EFTBankMockReconciliation.class);

    private final Map<String, ScheduledResponseReconciliation> transactionsScheduled = new HashMap<>();

    EFTBankMockReconciliation() {
        MockEventBus.addHandler(ScheduledResponseReconciliation.class, this);
    }

    void reset() {
        transactionsScheduled.clear();
    }

    @Override
    public void scheduleTransactionReconciliationResponse(ScheduledResponseReconciliation event) {
        log.debug("schedule transaction reject in Reconciliation for transactionId:{}", event.transactionId);
        transactionsScheduled.put(event.transactionId, event);
        EFTBankMock.instance().addReconciliationResponse(event.transactionId);
    }

    FundsReconciliationFile createReconciliationFile(List<FundsTransferRecord> records) {
        FundsReconciliationFile reconciliationFile = EntityFactory.create(FundsReconciliationFile.class);
        reconciliationFile.fileName().setValue(String.valueOf(System.nanoTime()));

        Map<String, FundsReconciliationSummary> byMID = new HashMap<String, FundsReconciliationSummary>();

        log.debug("Creating Reconciliation for {} records", records.size());

        for (FundsTransferRecord padRecord : records) {
            FundsReconciliationSummary summary = getSummary(reconciliationFile, byMID, padRecord.padBatch().merchantTerminalId().getValue());
            addRecordToSummary(summary, padRecord);
        }

        return reconciliationFile;
    }

    private FundsReconciliationSummary getSummary(FundsReconciliationFile reconciliationFile, Map<String, FundsReconciliationSummary> byMID,
            String merchantTerminalId) {
        FundsReconciliationSummary summary = byMID.get(merchantTerminalId);
        if (summary == null) {
            summary = EntityFactory.create(FundsReconciliationSummary.class);
            summary.processingStatus().setValue(Boolean.FALSE);
            summary.reconciliationStatus().setValue(MerchantReconciliationStatus.PAID);
            summary.merchantTerminalId().setValue(merchantTerminalId);

            byMID.put(merchantTerminalId, summary);
            reconciliationFile.batches().add(summary);
        }
        return summary;
    }

    private void addRecordToSummary(FundsReconciliationSummary summary, FundsTransferRecord padRecord) {
        FundsReconciliationRecordRecord record = EntityFactory.create(FundsReconciliationRecordRecord.class);
        record.processingStatus().setValue(Boolean.FALSE);

        record.paymentDate().setValue(SystemDateManager.getLogicalDate());
        record.merchantTerminalId().setValue(summary.merchantTerminalId().getValue());
        record.clientId().setValue(padRecord.clientId().getValue());
        record.transactionId().setValue(padRecord.transactionId().getValue());
        record.amount().setValue(padRecord.amount().getValue());

        ScheduledResponseReconciliation reject = transactionsScheduled.get(padRecord.transactionId().getValue());
        if (reject == null) {
            record.reconciliationStatus().setValue(TransactionReconciliationStatus.PROCESSED);
        } else {
            if (EFTBankMock.instance().hadReconciliation(padRecord)) {
                record.reconciliationStatus().setValue(TransactionReconciliationStatus.RETURNED);
            } else {
                record.reconciliationStatus().setValue(TransactionReconciliationStatus.REJECTED);
            }
            record.reasonCode().setValue(reject.reasonCode);
            record.reasonText().setValue(reject.reasonText);
        }

        summary.records().add(record);

    }

}
