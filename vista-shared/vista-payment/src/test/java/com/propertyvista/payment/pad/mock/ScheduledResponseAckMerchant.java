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

import com.propertyvista.test.mock.MockEvent;

public class ScheduledResponseAckMerchant extends MockEvent<ScheduledResponseAckMerchant.Handler> {

    public final String merchantTerminalId;

    // 4 characters
    public final String acknowledgmentStatusCode;

    public interface Handler {

        void scheduleMerchantAcknowledgmentResponse(ScheduledResponseAckMerchant event);

    }

    public ScheduledResponseAckMerchant(String merchantTerminalId, String acknowledgmentStatusCode) {
        super();
        this.merchantTerminalId = merchantTerminalId;
        if (acknowledgmentStatusCode.length() > 4) {
            throw new IllegalArgumentException();
        }
        this.acknowledgmentStatusCode = acknowledgmentStatusCode;
    }

    @Override
    protected final void dispatch(Handler handler) {
        handler.scheduleMerchantAcknowledgmentResponse(this);
    }
}
