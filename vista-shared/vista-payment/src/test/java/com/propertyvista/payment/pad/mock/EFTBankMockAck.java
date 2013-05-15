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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.validator.EntityValidator;

import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.payment.pad.CaledonPadUtils;
import com.propertyvista.payment.pad.data.PadAckBatch;
import com.propertyvista.payment.pad.data.PadAckDebitRecord;
import com.propertyvista.payment.pad.data.PadAckFile;
import com.propertyvista.test.mock.MockEventBus;

class EFTBankMockAck implements ScheduledResponseAcknowledgment.Handler {

    private static final Logger log = LoggerFactory.getLogger(EFTBankMockReconciliation.class);

    private final Map<String, ScheduledResponseAcknowledgment> scheduled = new HashMap<String, ScheduledResponseAcknowledgment>();

    EFTBankMockAck() {
        MockEventBus.addHandler(ScheduledResponseAcknowledgment.class, this);
        // TODO add Bach reject request
    }

    @Override
    public void scheduleTransactionAcknowledgmentResponse(ScheduledResponseAcknowledgment event) {
        log.debug("schedule reject in acknowledgment for transactionId:{}", event.transactionId);
        scheduled.put(event.transactionId, event);
    }

    PadAckFile createAcknowledgementFile(PadFile unacknowledgedFile) {
        PadAckFile ackFile = EntityFactory.create(PadAckFile.class);

        boolean batchLevelReject = false;
        boolean transactionReject = false;
        for (PadBatch padBatch : unacknowledgedFile.batches()) {
            // TODO Has Bach reject request
            if (false) {
                PadAckBatch batch = EntityFactory.create(PadAckBatch.class);
                batch.batchId().setValue(String.valueOf(padBatch.batchNumber().getValue()));
                batch.terminalId().setValue(padBatch.merchantTerminalId().getValue());
                batch.acknowledgmentStatusCode().setValue("");
                batch.batchAmount().setValue(padBatch.accountNumber().getValue());
                ackFile.batches().add(batch);
                batchLevelReject = true;
            } else {
                // Find TRANSACTION  REJECT RECORD
                for (PadDebitRecord padDebitRecord : padBatch.records()) {
                    ScheduledResponseAcknowledgment askReject = scheduled.get(padDebitRecord.transactionId().getValue());
                    if (askReject == null) {
                        EFTBankMock.instance().addAcknowledgedRecord(padDebitRecord);
                    } else {
                        log.debug("reject in acknowledgment for transactionId:{}", padDebitRecord.transactionId().getValue());

                        scheduled.remove(padDebitRecord.transactionId().getValue());

                        PadAckDebitRecord record = EntityFactory.create(PadAckDebitRecord.class);
                        record.terminalId().setValue(padBatch.merchantTerminalId().getValue());
                        record.clientId().setValue(padDebitRecord.clientId().getValue());
                        record.transactionId().setValue(padDebitRecord.transactionId().getValue());
                        record.amount().setValue(CaledonPadUtils.formatAmount(padDebitRecord.amount().getValue()));
                        record.acknowledgmentStatusCode().setValue(askReject.acknowledgmentStatusCode);
                        EntityValidator.validate(record);
                        ackFile.records().add(record);
                        transactionReject = true;
                    }
                }
            }
        }

        ackFile.companyId().setValue(unacknowledgedFile.companyId().getValue());
        ackFile.fileCreationNumber().setValue(unacknowledgedFile.fileCreationNumber().getValue());
        String fileCreationDate = new SimpleDateFormat("yyyyMMdd").format(unacknowledgedFile.created().getValue());
        ackFile.fileCreationDate().setValue(fileCreationDate);

        ackFile.batcheCount().setValue(String.valueOf(unacknowledgedFile.batches().size()));
        ackFile.recordsCount().setValue(String.valueOf(unacknowledgedFile.recordsCount().getValue()));

        ackFile.fileAmount().setValue(CaledonPadUtils.formatAmount(unacknowledgedFile.fileAmount().getValue()));

        if (batchLevelReject && transactionReject) {
            ackFile.acknowledgmentStatusCode().setValue("0004");
        } else if (batchLevelReject) {
            ackFile.acknowledgmentStatusCode().setValue("0002");
        } else if (transactionReject) {
            ackFile.acknowledgmentStatusCode().setValue("0003");
        } else {
            // Accepted
            ackFile.acknowledgmentStatusCode().setValue("0000");
        }
        return ackFile;
    }
}
