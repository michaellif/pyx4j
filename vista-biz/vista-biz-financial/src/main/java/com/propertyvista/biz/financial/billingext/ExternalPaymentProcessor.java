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
package com.propertyvista.biz.financial.billingext;

import java.util.List;

import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;

public class ExternalPaymentProcessor extends ExternalAbstractProcessor {

    ExternalPaymentProcessor(ExternalBillProducer billingManager) {
        super(billingManager);
    }

    @Override
    protected void execute() {
        attachPaymentRecords();
    }

    private void attachPaymentRecords() {
        Bill bill = getBillProducer().getCurrentBill();
        List<InvoiceLineItem> items = BillingUtils.getUnclaimedLineItems(bill.billingAccount(), bill.billingCycle());
        for (InvoicePayment payment : BillingUtils.getLineItemsForType(items, InvoicePayment.class)) {
            bill.lineItems().add(payment);
            bill.paymentReceivedAmount().setValue(bill.paymentReceivedAmount().getValue().add(payment.amount().getValue()));
        }
    }
}
