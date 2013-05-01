/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationSummary;

class PadCaledonReconciliation {

    private final ExecutionMonitor executionMonitor;

    PadCaledonReconciliation(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    void validateAndPersistFile(PadReconciliationFile reconciliationFile) {

        // Save detached objects
        List<PadReconciliationSummary> batches = new ArrayList<PadReconciliationSummary>(reconciliationFile.batches());
        Persistence.service().persist(reconciliationFile);

        // Match merchantAccounts.
        for (PadReconciliationSummary summary : batches) {
            summary.reconciliationFile().set(reconciliationFile);

            EntityQueryCriteria<PmcMerchantAccountIndex> criteria = EntityQueryCriteria.create(PmcMerchantAccountIndex.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().merchantTerminalId(), summary.merchantTerminalId()));
            PmcMerchantAccountIndex macc = Persistence.service().retrieve(criteria);
            if (macc == null) {
                throw new Error("Unexpected TerminalId '" + summary.merchantTerminalId().getValue() + "' in file " + reconciliationFile.fileName().getValue());
            }
            summary.merchantAccount().set(macc);

            summary.processingStatus().setValue(false);

            //Save detached objects
            List<PadReconciliationDebitRecord> records = new ArrayList<PadReconciliationDebitRecord>(summary.records());
            Persistence.service().persist(summary);

            for (final PadReconciliationDebitRecord debitRecord : records) {
                debitRecord.reconciliationSummary().set(summary);
                debitRecord.processingStatus().setValue(false);
                Persistence.service().persist(debitRecord);

                switch (debitRecord.reconciliationStatus().getValue()) {
                case PROCESSED:
                    executionMonitor.addProcessedEvent("Processed", debitRecord.amount().getValue());
                    break;
                case REJECTED:
                    executionMonitor.addFailedEvent("Rejected", debitRecord.amount().getValue());
                    break;
                case RETURNED:
                    executionMonitor.addFailedEvent("Returned", debitRecord.amount().getValue());
                    break;
                case DUPLICATE:
                    executionMonitor.addErredEvent("Duplicate", debitRecord.amount().getValue(), "TransactionId " + debitRecord.transactionId().getValue());
                    break;
                }
            }

        }

    }
}
