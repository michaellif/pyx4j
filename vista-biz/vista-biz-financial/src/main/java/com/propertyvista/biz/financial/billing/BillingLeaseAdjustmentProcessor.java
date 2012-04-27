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

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.AbstractLeaseAdjustmentProcessor;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class BillingLeaseAdjustmentProcessor extends AbstractLeaseAdjustmentProcessor {

    private final Billing billing;

    BillingLeaseAdjustmentProcessor(Billing billing) {
        this.billing = billing;
    }

    void createPendingLeaseAdjustments() {
        for (LeaseAdjustment adjustment : billing.getNextPeriodBill().billingAccount().adjustments()) {
            if (LeaseAdjustment.ExecutionType.pending.equals(adjustment.executionType().getValue())) {
                // Find if adjustment effective date failes on current or next billing period 
                DateRange overlap = DateUtils.getOverlappingRange(new DateRange(billing.getCurrentPeriodBill().billingPeriodStartDate().getValue(), billing
                        .getNextPeriodBill().billingPeriodEndDate().getValue()), new DateRange(adjustment.effectiveDate().getValue(), adjustment
                        .effectiveDate().getValue()));
                if (overlap != null) {
                    //Check if that adjustment is already presented in previous bill
                    boolean attachedToPreviousBill = false;
                    if (LeaseAdjustment.ActionType.charge.equals(adjustment.actionType().getValue())) {
                        for (InvoiceAccountCharge charge : BillingUtils.getLineItemsForType(billing.getCurrentPeriodBill(), InvoiceAccountCharge.class)) {
                            if (charge.adjustment().uid().equals(adjustment.uid())) {
                                attachedToPreviousBill = true;
                            }
                        }
                        if (!attachedToPreviousBill) {
                            createPendingCharge(adjustment);
                        }
                    } else if (LeaseAdjustment.ActionType.credit.equals(adjustment.actionType().getValue())) {
                        for (InvoiceAccountCredit credit : BillingUtils.getLineItemsForType(billing.getCurrentPeriodBill(), InvoiceAccountCredit.class)) {
                            if (credit.adjustment().uid().equals(adjustment.uid())) {
                                attachedToPreviousBill = true;
                            }
                        }
                        if (!attachedToPreviousBill) {
                            createPendingCredit(adjustment);
                        }
                    }
                }
            }
        }
    }

    void attachImmediateLeaseAdjustments() {
        for (InvoiceAccountCredit credit : BillingUtils.getLineItemsForType(billing.getNextPeriodBill().billingAccount().interimLineItems(),
                InvoiceAccountCredit.class)) {
            attachImmediateCredit(credit);
        }
        for (InvoiceAccountCharge charge : BillingUtils.getLineItemsForType(billing.getNextPeriodBill().billingAccount().interimLineItems(),
                InvoiceAccountCharge.class)) {
            attachImmediateCharge(charge);
        }
    }

    private void attachImmediateCredit(InvoiceAccountCredit credit) {
        addCredit(credit);
    }

    private void attachImmediateCharge(InvoiceAccountCharge charge) {
        addCharge(charge);
    }

    private void createPendingCharge(LeaseAdjustment adjustment) {
        InvoiceAccountCharge charge = createCharge(adjustment);
        charge.bill().set(billing.getNextPeriodBill());
        Persistence.service().persist(charge);
        addCharge(charge);
    }

    private void createPendingCredit(LeaseAdjustment adjustment) {
        InvoiceAccountCredit credit = createCredit(adjustment);
        credit.bill().set(billing.getNextPeriodBill());
        Persistence.service().persist(credit);
        addCredit(credit);
    }

    private void addCharge(InvoiceAccountCharge charge) {
        Bill bill = billing.getNextPeriodBill();
        bill.totalAdjustments().setValue(bill.totalAdjustments().getValue().add(charge.amount().getValue()));
        bill.lineItems().add(charge);
        bill.taxes().setValue(bill.taxes().getValue().add(charge.taxTotal().getValue()));
    }

    private void addCredit(InvoiceAccountCredit credit) {
        Bill bill = billing.getNextPeriodBill();
        bill.totalAdjustments().setValue(bill.totalAdjustments().getValue().add(credit.amount().getValue()));
        bill.lineItems().add(credit);
    }
}
