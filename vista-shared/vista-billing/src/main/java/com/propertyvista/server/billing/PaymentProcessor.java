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

    /**
     * 
     * Accepted || Posted && New (and targetDate is past) - next bill should pick it and process the payment (BillingStatus -> Processed)
     * Rejected && New - apply NSF charge (v0.5) (BillingStatus -> Reverted)
     * Accepted && Processed - N/A
     * Posted && Processed - do nothing
     * Rejected && Processed - apply NSF charge + revert payment using adjustment + late payment charge (v0.5) (BillingStatus -> Reverted)
     * Reverted - do nothing
     * 
     */
    void createPayments() {
        for (Payment payment : billing.getNextPeriodBill().billingAccount().payments()) {
            if (Payment.BillingStatus.New.equals(payment.billingStatus().getValue())
                    && (Payment.PaymentStatus.Received.equals(payment.paymentStatus().getValue()) || Payment.PaymentStatus.Posted.equals(payment
                            .paymentStatus().getValue()))) {
                createPayment(payment);
            }
        }
    }

    private void createPayment(Payment payment) {
        BillPayment billPayment = EntityFactory.create(BillPayment.class);
        billPayment.payment().set(payment);
        billPayment.amount().setValue(payment.amount().getValue());
        billing.getNextPeriodBill().billPayments().add(billPayment);
        billing.getNextPeriodBill().paymentReceivedAmount()
                .setValue(billing.getNextPeriodBill().paymentReceivedAmount().getValue().add(billPayment.payment().amount().getValue()));
    }

}
