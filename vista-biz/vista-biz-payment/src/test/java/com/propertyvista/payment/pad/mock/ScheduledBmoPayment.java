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

import java.math.BigDecimal;

import com.propertyvista.test.mock.MockEvent;

public class ScheduledBmoPayment extends MockEvent<ScheduledBmoPayment.Handler> {

    public final String accountNumber;

    public final BigDecimal amount;

    public final String paymentReferenceNumber;

    public interface Handler {

        void scheduleTransactionReconciliationResponse(ScheduledBmoPayment event);

    }

    public ScheduledBmoPayment(String accountNumber, BigDecimal amount, String paymentReferenceNumber) {
        super();
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.paymentReferenceNumber = paymentReferenceNumber;
    }

    @Override
    protected final void dispatch(Handler handler) {
        handler.scheduleTransactionReconciliationResponse(this);
    }

}
