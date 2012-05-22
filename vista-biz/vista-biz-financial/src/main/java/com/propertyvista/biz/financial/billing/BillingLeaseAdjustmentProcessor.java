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

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.AbstractLeaseAdjustmentProcessor;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class BillingLeaseAdjustmentProcessor extends AbstractLeaseAdjustmentProcessor {

    private final Billing billing;

    BillingLeaseAdjustmentProcessor(Billing billing) {
        this.billing = billing;
    }

    void createPendingLeaseAdjustments() {
        for (LeaseAdjustment adjustment : billing.getNextPeriodBill().billingAccount().adjustments()) {
            if (LeaseAdjustment.ExecutionType.pending.equals(adjustment.executionType().getValue())) {
                // Find if adjustment effective date failes on current or next billing period 
                DateRange overlap = BillDateUtils.getOverlappingRange(new DateRange(billing.getCurrentPeriodBill().billingPeriodStartDate().getValue(), billing
                        .getNextPeriodBill().billingPeriodEndDate().getValue()), new DateRange(adjustment.targetDate().getValue(), adjustment.targetDate()
                        .getValue()));
                if (overlap != null) {
                    //Check if that adjustment is already presented in previous bill
                    boolean attachedToPreviousBill = false;
                    if (LeaseAdjustmentReason.ActionType.charge.equals(adjustment.reason().actionType().getValue())) {
                        for (InvoiceAccountCharge charge : BillingUtils.getLineItemsForType(billing.getCurrentPeriodBill(), InvoiceAccountCharge.class)) {
                            if (charge.adjustment().uid().equals(adjustment.uid())) {
                                attachedToPreviousBill = true;
                            }
                        }
                        if (!attachedToPreviousBill) {
                            createPendingCharge(adjustment);
                        }
                    } else if (LeaseAdjustmentReason.ActionType.credit.equals(adjustment.reason().actionType().getValue())) {
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
        List<InvoiceLineItem> items = BillingUtils.getNotConsumedLineItems(billing.getNextPeriodBill().billingAccount());
        for (InvoiceAccountCredit credit : BillingUtils.getLineItemsForType(items, InvoiceAccountCredit.class)) {
            attachImmediateCredit(credit);
        }
        for (InvoiceAccountCharge charge : BillingUtils.getLineItemsForType(items, InvoiceAccountCharge.class)) {
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
        charge.dueDate().setValue(billing.getNextPeriodBill().dueDate().getValue());
        Persistence.service().persist(charge);
        addCharge(charge);
    }

    private void createPendingCredit(LeaseAdjustment adjustment) {
        InvoiceAccountCredit credit = createCredit(adjustment);
        Persistence.service().persist(credit);
        addCredit(credit);
    }

    private void addCharge(InvoiceAccountCharge charge) {
        Bill bill = billing.getNextPeriodBill();
        bill.pendingAccountAdjustments().setValue(bill.pendingAccountAdjustments().getValue().add(charge.amount().getValue()));
        bill.lineItems().add(charge);
        bill.taxes().setValue(bill.taxes().getValue().add(charge.taxTotal().getValue()));
    }

    private void addCredit(InvoiceAccountCredit credit) {
        Bill bill = billing.getNextPeriodBill();
        bill.pendingAccountAdjustments().setValue(bill.pendingAccountAdjustments().getValue().add(credit.amount().getValue()));
        bill.lineItems().add(credit);
    }
}
