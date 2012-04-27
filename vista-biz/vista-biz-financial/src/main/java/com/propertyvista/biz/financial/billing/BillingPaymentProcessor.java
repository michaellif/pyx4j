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
package com.propertyvista.biz.financial.billing;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoicePayment;

public class BillingPaymentProcessor {

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
        Bill bill = billing.getNextPeriodBill();
        bill.lineItems().add(payment);
        bill.paymentReceivedAmount().setValue(bill.paymentReceivedAmount().getValue().add(payment.amount().getValue()));
    }

}
