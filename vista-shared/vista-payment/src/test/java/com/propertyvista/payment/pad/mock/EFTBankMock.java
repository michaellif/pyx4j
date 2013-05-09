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
import java.util.List;

import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.payment.pad.data.PadAckFile;

class EFTBankMock {

    private EFTBankMock() {
    }

    private static class SingletonHolder {
        public static final EFTBankMock INSTANCE = new EFTBankMock();
    }

    static EFTBankMock instance() {
        return SingletonHolder.INSTANCE;
    }

    private final List<PadFile> receivedPadFile = new ArrayList<PadFile>();

    private final List<PadDebitRecord> uprocessedRecords = new ArrayList<PadDebitRecord>();

    private final EFTBankMockAck acknowledgment = new EFTBankMockAck();

    void receivedPadFile(PadFile padFile) {
        receivedPadFile.add(padFile.<PadFile> duplicate());
    }

    void scheduleTransactionAcknowledgmentResponse(String transactionId, String acknowledgmentStatusCode) {
        acknowledgment.scheduleTransactionAcknowledgmentResponse(transactionId, acknowledgmentStatusCode);
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
            return null;
        } else {
            receivedPadFile.remove(unacknowledgedFile);
            return acknowledgment.createAcknowledgementFile(unacknowledgedFile);
        }
    }
}
