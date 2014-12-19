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
 */
package com.propertyvista.biz.financial.billing.internal;

import java.util.ArrayList;
import java.util.List;

import com.propertyvista.biz.financial.InvoiceLineItemFactory;
import com.propertyvista.biz.financial.billing.AbstractBillingProcessor;
import com.propertyvista.biz.financial.billing.BillDateUtils;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billing.DateRange;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class BillingLeaseAdjustmentProcessor extends AbstractBillingProcessor<InternalBillProducer> {

    BillingLeaseAdjustmentProcessor(InternalBillProducer billProducer) {
        super(billProducer);
    }

    @Override
    public void execute() {
        createPendingLeaseAdjustments();
        attachImmediateLeaseAdjustments();
    }

    private void createPendingLeaseAdjustments() {
        for (LeaseAdjustment adjustment : getBillProducer().getNextPeriodBill().billingAccount().adjustments()) {
            if (LeaseAdjustment.Status.submited == adjustment.status().getValue()
                    && LeaseAdjustment.ExecutionType.pending == adjustment.executionType().getValue()) {
                // Find if adjustment effective date fails on current or next billing period 
                DateRange overlap = BillDateUtils.getOverlappingRange(new DateRange(getBillProducer().getPreviousPeriodBill().billingPeriodStartDate()
                        .getValue(), getBillProducer().getNextPeriodBill().billingPeriodEndDate().getValue()), new DateRange(
                        adjustment.targetDate().getValue(), adjustment.targetDate().getValue()));
                if (overlap != null) {
                    //Check if that adjustment is already presented in previous bills
                    boolean attachedToPreviousBill = false;
                    if (ARCode.Type.AccountCharge.equals(adjustment.code().type().getValue())) {
                        List<InvoiceAccountCharge> charges = new ArrayList<InvoiceAccountCharge>();
                        charges.addAll(BillingUtils.getLineItemsForType(getBillProducer().getPreviousPeriodBill(), InvoiceAccountCharge.class));
                        charges.addAll(BillingUtils.getLineItemsForType(getBillProducer().getCurrentPeriodBill(), InvoiceAccountCharge.class));
                        for (InvoiceAccountCharge charge : charges) {
                            if (charge.adjustment().uuid().equals(adjustment.uuid())) {
                                attachedToPreviousBill = true;
                            }
                        }
                        if (!attachedToPreviousBill) {
                            createPendingCharge(adjustment);
                        }
                    } else if (ARCode.Type.AccountCredit.equals(adjustment.code().type().getValue())) {
                        List<InvoiceAccountCredit> credits = new ArrayList<InvoiceAccountCredit>();
                        credits.addAll(BillingUtils.getLineItemsForType(getBillProducer().getPreviousPeriodBill(), InvoiceAccountCredit.class));
                        credits.addAll(BillingUtils.getLineItemsForType(getBillProducer().getCurrentPeriodBill(), InvoiceAccountCredit.class));
                        for (InvoiceAccountCredit credit : credits) {
                            if (credit.adjustment().uuid().equals(adjustment.uuid())) {
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
        Bill bill = getBillProducer().getNextPeriodBill();
        List<InvoiceLineItem> items = BillingUtils.getUnclaimedLineItems(bill.billingAccount(), bill.billingCycle());
        for (InvoiceAccountCredit credit : BillingUtils.getLineItemsForType(items, InvoiceAccountCredit.class)) {
            attachImmediateCredit(credit);
        }
        for (InvoiceAccountCharge charge : BillingUtils.getLineItemsForType(items, InvoiceAccountCharge.class)) {
            attachImmediateCharge(charge);
        }
    }

    private void attachImmediateCredit(InvoiceAccountCredit credit) {
        Bill bill = getBillProducer().getNextPeriodBill();
        bill.immediateAccountAdjustments().setValue(bill.immediateAccountAdjustments().getValue().add(credit.amount().getValue()));
        bill.lineItems().add(credit);
    }

    private void attachImmediateCharge(InvoiceAccountCharge charge) {
        Bill bill = getBillProducer().getNextPeriodBill();
        bill.immediateAccountAdjustments().setValue(
                bill.immediateAccountAdjustments().getValue().add(charge.amount().getValue()).add(charge.taxTotal().getValue()));
        bill.lineItems().add(charge);
    }

    private void createPendingCharge(LeaseAdjustment adjustment) {
        InvoiceAccountCharge charge = InvoiceLineItemFactory.createInvoiceAccountCharge(adjustment);
        charge.dueDate().setValue(getBillProducer().getNextPeriodBill().dueDate().getValue());
        Bill bill = getBillProducer().getNextPeriodBill();
        bill.pendingAccountAdjustments().setValue(bill.pendingAccountAdjustments().getValue().add(charge.amount().getValue()));
        bill.lineItems().add(charge);
        bill.taxes().setValue(bill.taxes().getValue().add(charge.taxTotal().getValue()));
    }

    private void createPendingCredit(LeaseAdjustment adjustment) {
        InvoiceAccountCredit credit = InvoiceLineItemFactory.createInvoiceAccountCredit(adjustment);
        Bill bill = getBillProducer().getNextPeriodBill();
        bill.pendingAccountAdjustments().setValue(bill.pendingAccountAdjustments().getValue().add(credit.amount().getValue()));
        bill.lineItems().add(credit);
    }

}
