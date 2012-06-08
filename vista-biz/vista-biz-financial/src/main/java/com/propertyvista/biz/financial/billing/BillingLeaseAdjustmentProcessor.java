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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.InvoiceLineItemFactory;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class BillingLeaseAdjustmentProcessor extends AbstractBillingProcessor {

    BillingLeaseAdjustmentProcessor(AbstractBillingManager billingManager) {
        super(billingManager);
    }

    @Override
    protected void execute() {
        createPendingLeaseAdjustments();
        attachImmediateLeaseAdjustments();
    }

    private void createPendingLeaseAdjustments() {
        for (LeaseAdjustment adjustment : getBillingManager().getNextPeriodBill().billingAccount().adjustments()) {
            if (LeaseAdjustment.Status.submited == adjustment.status().getValue()
                    && LeaseAdjustment.ExecutionType.pending == adjustment.executionType().getValue()) {
                // Find if adjustment effective date fails on current or next billing period 
                DateRange overlap = BillDateUtils.getOverlappingRange(new DateRange(getBillingManager().getPreviousPeriodBill().billingPeriodStartDate()
                        .getValue(), getBillingManager().getNextPeriodBill().billingPeriodEndDate().getValue()), new DateRange(adjustment.targetDate()
                        .getValue(), adjustment.targetDate().getValue()));
                if (overlap != null) {
                    //Check if that adjustment is already presented in previous bills
                    boolean attachedToPreviousBill = false;
                    if (LeaseAdjustmentReason.ActionType.charge.equals(adjustment.reason().actionType().getValue())) {
                        List<InvoiceAccountCharge> charges = new ArrayList<InvoiceAccountCharge>();
                        charges.addAll(BillingUtils.getLineItemsForType(getBillingManager().getPreviousPeriodBill(), InvoiceAccountCharge.class));
                        charges.addAll(BillingUtils.getLineItemsForType(getBillingManager().getCurrentPeriodBill(), InvoiceAccountCharge.class));
                        for (InvoiceAccountCharge charge : charges) {
                            if (charge.adjustment().uid().equals(adjustment.uid())) {
                                attachedToPreviousBill = true;
                            }
                        }
                        if (!attachedToPreviousBill) {
                            createPendingCharge(adjustment);
                        }
                    } else if (LeaseAdjustmentReason.ActionType.credit.equals(adjustment.reason().actionType().getValue())) {
                        List<InvoiceAccountCredit> credits = new ArrayList<InvoiceAccountCredit>();
                        credits.addAll(BillingUtils.getLineItemsForType(getBillingManager().getPreviousPeriodBill(), InvoiceAccountCredit.class));
                        credits.addAll(BillingUtils.getLineItemsForType(getBillingManager().getCurrentPeriodBill(), InvoiceAccountCredit.class));
                        for (InvoiceAccountCredit credit : credits) {
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

    private void attachImmediateLeaseAdjustments() {
        List<InvoiceLineItem> items = BillingUtils.getUnclaimedLineItems(getBillingManager().getNextPeriodBill().billingAccount());
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
        InvoiceAccountCharge charge = InvoiceLineItemFactory.createInvoiceAccountCharge(adjustment);
        charge.dueDate().setValue(getBillingManager().getNextPeriodBill().dueDate().getValue());
        Persistence.service().persist(charge);
        addCharge(charge);
    }

    private void createPendingCredit(LeaseAdjustment adjustment) {
        InvoiceAccountCredit credit = InvoiceLineItemFactory.createInvoiceAccountCredit(adjustment);
        Persistence.service().persist(credit);
        addCredit(credit);
    }

    private void addCharge(InvoiceAccountCharge charge) {
        Bill bill = getBillingManager().getNextPeriodBill();
        bill.pendingAccountAdjustments().setValue(bill.pendingAccountAdjustments().getValue().add(charge.amount().getValue()));
        bill.lineItems().add(charge);
        bill.taxes().setValue(bill.taxes().getValue().add(charge.taxTotal().getValue()));
    }

    private void addCredit(InvoiceAccountCredit credit) {
        Bill bill = getBillingManager().getNextPeriodBill();
        bill.pendingAccountAdjustments().setValue(bill.pendingAccountAdjustments().getValue().add(credit.amount().getValue()));
        bill.lineItems().add(credit);
    }
}
