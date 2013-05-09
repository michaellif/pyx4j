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

import com.google.web.bindery.event.shared.Event;

class ScheduledResponseAcknowledgment extends Event<ScheduledResponseAcknowledgment.Handler> {

    String transactionId;

    String acknowledgmentStatusCode;

    public interface Handler {

        void scheduleTransactionAcknowledgmentResponse(ScheduledResponseAcknowledgment event);

    }

    public static final Type<ScheduledResponseAcknowledgment.Handler> TYPE = new Type<ScheduledResponseAcknowledgment.Handler>();

    @Override
    public com.google.web.bindery.event.shared.Event.Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.scheduleTransactionAcknowledgmentResponse(this);
    }
}
