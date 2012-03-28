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
package com.propertyvista.server.accounting.billing;

import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.server.accaunting.AbstractPaymentProcessor;

public class BillingPaymentProcessor extends AbstractPaymentProcessor {

    private final Billing billing;

    BillingPaymentProcessor(Billing billing) {
        this.billing = billing;
    }

    void attachPayments() {
        for (InvoicePayment payment : BillingUtils.getLineItemsForType(billing.getNextPeriodBill().billingAccount().interimLineItems(), InvoicePayment.class)) {
            attachPayment(payment);
        }
    }

    private void attachPayment(InvoicePayment payment) {
        billing.getNextPeriodBill().lineItems().add(payment);
        billing.getNextPeriodBill().paymentReceivedAmount()
                .setValue(billing.getNextPeriodBill().paymentReceivedAmount().getValue().add(payment.amount().getValue()));
    }

}
