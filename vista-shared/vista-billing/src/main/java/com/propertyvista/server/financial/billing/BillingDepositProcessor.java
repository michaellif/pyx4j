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
package com.propertyvista.server.financial.billing;

import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.tenant.lease.BillableItem;

public class BillingDepositProcessor {

    private final Billing billing;

    BillingDepositProcessor(Billing billing) {
        this.billing = billing;
    }

    void createDeposits() {
        createDeposit(billing.getNextPeriodBill().billingAccount().lease().version().leaseProducts().serviceItem());

        for (BillableItem billableItem : billing.getNextPeriodBill().billingAccount().lease().version().leaseProducts().featureItems()) {
            if (billableItem.isNull()) {
                throw new BillingException("Service Item is mandatory in lease");
            }
            createDeposit(billableItem);
        }
    }

    //Deposit should be taken on a first billing period of the BillableItem
    private void createDeposit(BillableItem serviceItem) {
        //TODO
    }

    private void attachDepositRefund(InvoiceDepositRefund depositRefund) {
        billing.getNextPeriodBill().lineItems().add(depositRefund);
        billing.getNextPeriodBill().paymentReceivedAmount()
                .setValue(billing.getNextPeriodBill().depositRefundAmount().getValue().add(depositRefund.amount().getValue()));
    }

}
