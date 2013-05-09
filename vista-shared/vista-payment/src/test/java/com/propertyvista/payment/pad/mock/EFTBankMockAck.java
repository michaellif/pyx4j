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

import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.payment.pad.data.PadAckFile;
import com.propertyvista.test.mock.MockEventBus;

class EFTBankMockAck implements ScheduledResponseAcknowledgment.Handler {

    private final List<ScheduledResponseAcknowledgment> scheduled = new ArrayList<ScheduledResponseAcknowledgment>();

    EFTBankMockAck() {
        MockEventBus.addHandler(ScheduledResponseAcknowledgment.TYPE, this);
    }

    @Override
    public void scheduleTransactionAcknowledgmentResponse(ScheduledResponseAcknowledgment event) {
        // TODO Auto-generated method stub
    }

    void scheduleTransactionAcknowledgmentResponse(String transactionId, String acknowledgmentStatusCode) {
        // TODO Auto-generated method stub
    }

    PadAckFile createAcknowledgementFile(PadFile unacknowledgedFile) {
        return null;
    }

}
