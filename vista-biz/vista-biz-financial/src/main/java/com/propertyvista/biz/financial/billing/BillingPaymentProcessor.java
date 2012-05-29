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

import java.util.List;

import com.propertyvista.biz.financial.AbstractProcessor;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoiceNSF;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoicePaymentBackOut;

public class BillingPaymentProcessor extends AbstractProcessor {

    private final AbstractBillingProcessor billing;

    BillingPaymentProcessor(AbstractBillingProcessor billing) {
        this.billing = billing;
    }

    void attachPaymentRecords() {

        List<InvoiceLineItem> items = BillingUtils.getNotConsumedLineItems(billing.getNextPeriodBill().billingAccount());

        for (InvoicePayment payment : BillingUtils.getLineItemsForType(items, InvoicePayment.class)) {
            attachPayment(payment);
        }

        for (InvoicePaymentBackOut paymentBackOut : BillingUtils.getLineItemsForType(items, InvoicePaymentBackOut.class)) {
            attachPaymentBackOut(paymentBackOut);
        }

        for (InvoiceNSF nsf : BillingUtils.getLineItemsForType(items, InvoiceNSF.class)) {
            attachNSF(nsf);
        }
    }

    private void attachPayment(InvoicePayment payment) {
        Bill bill = billing.getNextPeriodBill();
        bill.lineItems().add(payment);
        bill.paymentReceivedAmount().setValue(bill.paymentReceivedAmount().getValue().add(payment.amount().getValue()));
    }

    private void attachPaymentBackOut(InvoicePaymentBackOut paymentBackOut) {
        Bill bill = billing.getNextPeriodBill();
        bill.lineItems().add(paymentBackOut);
        bill.paymentReceivedAmount().setValue(bill.paymentReceivedAmount().getValue().subtract(paymentBackOut.amount().getValue()));
    }

    private void attachNSF(InvoiceNSF nsf) {
        Bill bill = billing.getNextPeriodBill();
        bill.lineItems().add(nsf);
        // bill..setValue(bill.paymentReceivedAmount().getValue().subtract(paymentBackOut.amount().getValue()));
    }
}
