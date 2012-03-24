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

import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class LeaseAdjustmentProcessor {

    private final Billing billing;

    LeaseAdjustmentProcessor(Billing billing) {
        this.billing = billing;
    }

    void createPendingLeaseAdjustments() {
        for (LeaseAdjustment item : billing.getNextPeriodBill().billingAccount().lease().version().leaseProducts().adjustments()) {
            if (LeaseAdjustment.ActionType.pending.equals(item.actionType().getValue())) {
                createPendingLeaseAdjustment(item);
            }
        }
    }

    void attachImmediateLeaseAdjustments() {
        for (InvoiceAccountCredit adjustment : BillingUtils.getLineItemsForType(billing.getNextPeriodBill().billingAccount().interimLineItems(),
                InvoiceAccountCredit.class)) {
            attachImmediateLeaseAdjustment(adjustment);
        }
    }

    private void createPendingLeaseAdjustment(LeaseAdjustment item) {
        if (!isLeaseAdjustmentApplicable(item)) {
            return;
        }

        InvoiceAccountCredit adjustment = EntityFactory.create(InvoiceAccountCredit.class);
        adjustment.bill().set(billing.getNextPeriodBill());

        billing.getNextPeriodBill().totalAdjustments().setValue(billing.getNextPeriodBill().totalAdjustments().getValue().add(item.amount().getValue()));

//        if (!adjustment.amount().isNull()) {
//            adjustment.taxes().addAll(
//                    TaxUtils.calculateTaxes(adjustment.amount().getValue(), item.reason(), billing.getNextPeriodBill().billingRun().building()));
//        }
//        adjustment.taxTotal().setValue(new BigDecimal(0));
//        for (InvoiceChargeTax chargeTax : adjustment.taxes()) {
//            adjustment.taxTotal().setValue(adjustment.taxTotal().getValue().add(chargeTax.amount().getValue()));
//        }

        addLeaseAdjustment(adjustment);
    }

    private void addLeaseAdjustment(InvoiceAccountCredit item) {
        billing.getNextPeriodBill().totalAdjustments().setValue(billing.getNextPeriodBill().totalAdjustments().getValue().add(item.amount().getValue()));
        billing.getNextPeriodBill().lineItems().add(item);
//        billing.getNextPeriodBill().taxes().setValue(billing.getNextPeriodBill().taxes().getValue().add(item.taxTotal().getValue()));
    }

    private void attachImmediateLeaseAdjustment(InvoiceAccountCredit adjustment) {
        // TODO Auto-generated method stub

    }

    private boolean isLeaseAdjustmentApplicable(LeaseAdjustment item) {
        // TODO Implement
        return true;
    }

}
