/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.billing;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.BillPayment;
import com.propertyvista.domain.financial.billing.Payment;

public class PaymentProcessor {

    private final Billing billing;

    PaymentProcessor(Billing billing) {
        this.billing = billing;
    }

    void createPayments() {
        for (Payment item : billing.getNextPeriodBill().billingAccount().payments()) {
            createPayment(item);
        }
    }

    private void createPayment(Payment payment) {

        BillPayment billPayment = EntityFactory.create(BillPayment.class);
        billPayment.payment().set(payment);
        billPayment.bill().set(billing.getNextPeriodBill());

        billing.getNextPeriodBill().paymentReceivedAmount()
                .setValue(billing.getNextPeriodBill().paymentReceivedAmount().getValue().add(billPayment.payment().amount().getValue()));
    }

}
