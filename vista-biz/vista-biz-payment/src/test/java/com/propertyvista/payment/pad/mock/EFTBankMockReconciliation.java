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
package com.propertyvista.payment.pad.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.operations.domain.payment.pad.MerchantReconciliationStatus;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationSummary;
import com.propertyvista.operations.domain.payment.pad.TransactionReconciliationStatus;

class EFTBankMockReconciliation {

    private static final Logger log = LoggerFactory.getLogger(EFTBankMockReconciliation.class);

    EFTBankMockReconciliation() {

    }

    PadReconciliationFile createReconciliationFile(List<PadDebitRecord> records) {
        PadReconciliationFile reconciliationFile = EntityFactory.create(PadReconciliationFile.class);
        reconciliationFile.fileName().setValue(String.valueOf(System.nanoTime()));

        Map<String, PadReconciliationSummary> byMID = new HashMap<String, PadReconciliationSummary>();

        log.debug("Creating Reconciliation for {} records", records.size());

        for (PadDebitRecord padRecord : records) {
            PadReconciliationSummary summary = getSummary(reconciliationFile, byMID, padRecord.padBatch().merchantTerminalId().getValue());
            addRecordToSummary(summary, padRecord);
        }

        return reconciliationFile;
    }

    private PadReconciliationSummary getSummary(PadReconciliationFile reconciliationFile, Map<String, PadReconciliationSummary> byMID, String merchantTerminalId) {
        PadReconciliationSummary summary = byMID.get(merchantTerminalId);
        if (summary == null) {
            summary = EntityFactory.create(PadReconciliationSummary.class);
            summary.processingStatus().setValue(Boolean.FALSE);
            summary.reconciliationStatus().setValue(MerchantReconciliationStatus.PAID);
            summary.merchantTerminalId().setValue(merchantTerminalId);

            byMID.put(merchantTerminalId, summary);
            reconciliationFile.batches().add(summary);
        }
        return summary;
    }

    private void addRecordToSummary(PadReconciliationSummary summary, PadDebitRecord padRecord) {
        PadReconciliationDebitRecord record = EntityFactory.create(PadReconciliationDebitRecord.class);
        record.processingStatus().setValue(Boolean.FALSE);

        record.paymentDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        record.merchantTerminalId().setValue(summary.merchantTerminalId().getValue());
        record.clientId().setValue(padRecord.clientId().getValue());
        record.transactionId().setValue(padRecord.transactionId().getValue());
        record.amount().setValue(padRecord.amount().getValue());

        record.reconciliationStatus().setValue(TransactionReconciliationStatus.PROCESSED);

        summary.records().add(record);

    }

}
