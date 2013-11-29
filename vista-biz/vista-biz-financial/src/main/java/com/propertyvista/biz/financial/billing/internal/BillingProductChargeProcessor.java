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
package com.propertyvista.biz.financial.billing.internal;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.TaxUtils;
import com.propertyvista.biz.financial.billing.AbstractBillingProcessor;
import com.propertyvista.biz.financial.billing.BillDateUtils;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billing.DateRange;
import com.propertyvista.biz.financial.billing.ProrationUtils;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceAdjustmentSubLineItem;
import com.propertyvista.domain.financial.billing.InvoiceConcessionSubLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceProductCredit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.portal.rpc.shared.BillingException;

public class BillingProductChargeProcessor extends AbstractBillingProcessor<InternalBillProducer> {

    private static final I18n i18n = I18n.get(BillingProductChargeProcessor.class);

    BillingProductChargeProcessor(InternalBillProducer billProducer) {
        super(billProducer);
    }

    @Override
    public void execute() {
        // TODO: Misha/Stas review please: do not calculate charges for null-duration billing period: 
        if (!getBillProducer().getNextPeriodBill().billingPeriodStartDate().isNull()) {
            createCharges();
        }
    }

    private void createCharges() {

        BillableItem service = getBillProducer().getNextPeriodBill().billingAccount().lease().currentTerm().version().leaseProducts().serviceItem();
        if (!Bill.BillType.Final.equals(getBillProducer().getNextPeriodBill().billType().getValue())) {
            createCharge(service);
        }
        // for Final bill charges may still need to be revised
        reviseChargeForPeriod(service, InvoiceProductCharge.Period.current);
        reviseChargeForPeriod(service, InvoiceProductCharge.Period.previous);

        for (BillableItem billableItem : getBillProducer().getNextPeriodBill().billingAccount().lease().currentTerm().version().leaseProducts().featureItems()) {
            if (billableItem.isNull()) {
                throw new BillingException("Service Item is mandatory in lease");
            }
            createCharge(billableItem);

            reviseChargeForPeriod(billableItem, InvoiceProductCharge.Period.current);
            reviseChargeForPeriod(billableItem, InvoiceProductCharge.Period.previous);
        }
    }

    private void createCharge(BillableItem billableItem) {
        Persistence.service().retrieve(billableItem.item().product());

        createChargeForNextPeriod(billableItem);

        //TODO
        if (false) {
            reviseChargeForCurrentPeriod(billableItem);

            reviseChargeForPreviousPeriod(billableItem);
        }
    }

    private void createChargeForNextPeriod(BillableItem billableItem) {

        if (Bill.BillType.Final.equals(getBillProducer().getNextPeriodBill().billType().getValue())) {
            return;
        }
        addCharge(createCharge(billableItem, getBillProducer().getNextPeriodBill(), InvoiceProductCharge.Period.next));
    }

    private void reviseChargeForCurrentPeriod(BillableItem billableItem) {
        if (getBillProducer().getCurrentPeriodBill() == null) {
            return;
        }

        InvoiceProductCharge revisedCharge = createCharge(billableItem, getBillProducer().getCurrentPeriodBill(), InvoiceProductCharge.Period.current);

        InvoiceProductCharge originalCharge = null;

        for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(getBillProducer().getCurrentPeriodBill(), InvoiceProductCharge.class)) {
            if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                    && InvoiceProductCharge.Period.next.equals(charge.period().getValue())) {
                originalCharge = charge;
                break;
            }
        }

        if (revisedCharge != null && originalCharge == null) {
            addCharge(revisedCharge);
        } else if (revisedCharge != null && originalCharge != null) {
            if (!revisedCharge.amount().getValue().equals(originalCharge.amount().getValue())) {
                //TODO   getBillingManager().getBillEntryAdjustmentProcessor().createBillEntryAdjustment(originalCharge, revisedCharge);
            }
        }
    }

    private void reviseChargeForPreviousPeriod(BillableItem billableItem) {
        if (getBillProducer().getPreviousPeriodBill() == null) {
            return;
        }

        InvoiceProductCharge finalCharge = createCharge(billableItem, getBillProducer().getPreviousPeriodBill(), InvoiceProductCharge.Period.previous);

        InvoiceProductCharge originalCharge = null;

        //TODO handle case when both previous and current have charge

        for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(getBillProducer().getPreviousPeriodBill(), InvoiceProductCharge.class)) {
            if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                    && InvoiceProductCharge.Period.next.equals(charge.period().getValue())) {
                originalCharge = charge;
                break;
            }
        }

        for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(getBillProducer().getCurrentPeriodBill(), InvoiceProductCharge.class)) {
            if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                    && InvoiceProductCharge.Period.current.equals(charge.period().getValue())) {
                originalCharge = charge;
                break;
            }
        }

        if (finalCharge != null && originalCharge == null) {
            addCharge(finalCharge);
        } else if (finalCharge != null && originalCharge != null) {
//            BillEntryAdjustment billEntryAdjustment = null;
//            for (BillEntryAdjustment adjustment : getBillingManager().getCurrentPeriodBill().billEntryAdjustments()) {
//                if (originalCharge.equals(adjustment.originalBillEntry())) {
//                    billEntryAdjustment = adjustment;
//                    break;
//                }
//            }
//            if (billEntryAdjustment != null) {
//                //TODO    getBillingManager().getBillEntryAdjustmentProcessor().createBillEntryAdjustment(billEntryAdjustment.revisedBillEntry(), finalCharge);
//            }
        }
    }

    private InvoiceProductCharge createCharge(BillableItem billableItem, Bill bill, InvoiceProductCharge.Period period) {

        // Find if billable item period overlaps with the bill period.
        LogicalDate expirationDate = billableItem.expirationDate().getValue();
        // Keep in mind possible lease termination
        if (bill.billingAccount().lease().isValueDetached()) {
            Persistence.service().retrieve(bill.billingAccount());
            Persistence.service().retrieve(bill.billingAccount().lease());
        }
        if (expirationDate == null || expirationDate.after(bill.billingAccount().lease().leaseTo().getValue())) {
            expirationDate = bill.billingAccount().lease().leaseTo().getValue();
        }
        DateRange overlap = BillDateUtils.getOverlappingRange(new DateRange(bill.billingPeriodStartDate().getValue(), bill.billingPeriodEndDate().getValue()),
                new DateRange(billableItem.effectiveDate().getValue(), expirationDate));

        // If billable item is not in effect in this billing period do nothing
        if (overlap == null) {
            return null;
        }

        InvoiceProductCharge charge = EntityFactory.create(InvoiceProductCharge.class);

        charge.billingAccount().set(bill.billingAccount());
        charge.period().setValue(period);
        charge.fromDate().setValue(overlap.getFromDate());
        charge.toDate().setValue(overlap.getToDate());
        charge.dueDate().setValue(bill.dueDate().getValue());
        charge.arCode().set(billableItem.item().product().holder().code());

        if (BillingUtils.isService(billableItem.item().product())) {
            charge.productType().setValue(InvoiceProductCharge.ProductType.service);
        } else if (BillingUtils.isRecurringFeature(billableItem.item().product())) {
            charge.productType().setValue(InvoiceProductCharge.ProductType.recurringFeature);
        } else if (BillingUtils.isOneTimeFeature(billableItem.item().product())) {
            charge.productType().setValue(InvoiceProductCharge.ProductType.oneTimeFeature);
        } else {
            throw new Error("Unknown product type");
        }

        createChargeSubLineItem(charge, billableItem);
        createAdjustmentSubLineItems(charge, billableItem, bill);
        createConcessionSubLineItems(charge, billableItem);

        charge.amount().setValue(charge.chargeSubLineItem().amount().getValue());

        for (InvoiceAdjustmentSubLineItem subLineItem : charge.adjustmentSubLineItems()) {
            charge.amount().setValue(charge.amount().getValue().add(subLineItem.amount().getValue()));
        }

        for (InvoiceConcessionSubLineItem subLineItem : charge.concessionSubLineItems()) {
            charge.amount().setValue(charge.amount().getValue().add(subLineItem.amount().getValue()));
        }

        TaxUtils.calculateProductChargeTaxes(charge, bill.billingCycle().building());

        charge.description().setValue(charge.chargeSubLineItem().billableItem().item().description().getStringView());

        return charge;
    }

    private void createChargeSubLineItem(InvoiceProductCharge charge, BillableItem billableItem) {
        charge.chargeSubLineItem().billableItem().set(billableItem);

        if (BillingUtils.isOneTimeFeature(charge.chargeSubLineItem().billableItem().item().product())) {
            // do not prorate one-time charge
            charge.chargeSubLineItem().amount().setValue(charge.chargeSubLineItem().billableItem().agreedPrice().getValue());
        } else {
            charge.chargeSubLineItem().amount().setValue(prorate(charge));
        }

        charge.chargeSubLineItem().description().setValue(billableItem.item().description().getStringView());
    }

    private void createAdjustmentSubLineItems(InvoiceProductCharge charge, BillableItem billableItem, Bill bill) {
        for (BillableItemAdjustment adjustment : billableItem.adjustments()) {
            createAdjustmentSubLineItem(adjustment, charge, bill);
        }
    }

    private void createAdjustmentSubLineItem(BillableItemAdjustment billableItemAdjustment, InvoiceProductCharge charge, Bill bill) {
        if (Bill.BillType.Final.equals(bill.billType().getValue())) {
            throw new Error(i18n.tr("Final bill should not have adjustments"));
        }

        InvoiceAdjustmentSubLineItem adjustment = EntityFactory.create(InvoiceAdjustmentSubLineItem.class);

        BigDecimal amount = null;

        if (BillableItemAdjustment.Type.percentage.equals(billableItemAdjustment.type().getValue())) {
            amount = billableItemAdjustment.billableItem().agreedPrice().getValue().multiply(billableItemAdjustment.value().getValue());
        } else if (BillableItemAdjustment.Type.monetary.equals(billableItemAdjustment.type().getValue())) {
            amount = billableItemAdjustment.value().getValue();
        }

        DateRange overlap = BillDateUtils.getOverlappingRange(new DateRange(bill.billingPeriodStartDate().getValue(), bill.billingPeriodEndDate().getValue()),
                new DateRange(billableItemAdjustment.effectiveDate().getValue(), billableItemAdjustment.expirationDate().getValue()));

        if (overlap == null) {
            return;
        }

        overlap = BillDateUtils.getOverlappingRange(overlap, new DateRange(billableItemAdjustment.billableItem().effectiveDate().getValue(),
                billableItemAdjustment.billableItem().expirationDate().getValue()));

        if (overlap == null) {
            return;
        }

        BigDecimal proration = ProrationUtils.prorate(overlap.getFromDate(), overlap.getToDate(), bill.billingCycle());
        adjustment.amount().setValue(DomainUtil.roundMoney(amount.multiply(proration)));
        adjustment.description().setValue(billableItemAdjustment.billableItem().item().name().getStringView() + " " + i18n.tr("Adjustment"));
        adjustment.billableItemAdjustment().set(billableItemAdjustment);

        charge.adjustmentSubLineItems().add(adjustment);

    }

    private void createConcessionSubLineItems(InvoiceProductCharge charge, BillableItem billableItem) {
        //TODO
    }

    private BigDecimal prorate(InvoiceProductCharge charge) {
        BillingCycle cycle = null;
        switch (charge.period().getValue()) {
        case previous:
            cycle = getBillProducer().getPreviousPeriodBill().billingCycle();
            break;
        case current:
            cycle = getBillProducer().getCurrentPeriodBill().billingCycle();
            break;
        case next:
            cycle = getBillProducer().getNextPeriodBill().billingCycle();
            break;
        }

        BigDecimal proration = ProrationUtils.prorate(charge.fromDate().getValue(), charge.toDate().getValue(), cycle);
        return DomainUtil.roundMoney(charge.chargeSubLineItem().billableItem().agreedPrice().getValue().multiply(proration));
    }

    private void addCharge(InvoiceProductCharge charge) {
        if (charge == null) {
            return;
        }

        Bill bill = getBillProducer().getNextPeriodBill();
        if (BillingUtils.isService(charge.chargeSubLineItem().billableItem().item().product())) { //Service
            bill.serviceCharge().setValue(bill.serviceCharge().getValue().add(charge.amount().getValue()));
        } else if (BillingUtils.isRecurringFeature(charge.chargeSubLineItem().billableItem().item().product())) { //Recurring Feature
            bill.recurringFeatureCharges().setValue(bill.recurringFeatureCharges().getValue().add(charge.amount().getValue()));
        } else {
            bill.oneTimeFeatureCharges().setValue(bill.oneTimeFeatureCharges().getValue().add(charge.amount().getValue()));
        }
        bill.taxes().setValue(bill.taxes().getValue().add(charge.taxTotal().getValue()));
        bill.lineItems().add(charge);
    }

    private void reviseChargeForPeriod(BillableItem billableItem, InvoiceProductCharge.Period period) {
        Bill bill = null;
        switch (period) {
        case current:
            bill = getBillProducer().getCurrentPeriodBill();
            break;
        case previous:
            bill = getBillProducer().getPreviousPeriodBill();
            break;
        }

        if (bill == null) {
            return;
        }

        InvoiceProductCharge revisedCharge = createCharge(billableItem, bill, period);

        InvoiceProductCharge originalCharge = null;

        // Look for original charge where charge.period() = next
        for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(bill, InvoiceProductCharge.class)) {
            if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                    && InvoiceProductCharge.Period.next.equals(charge.period().getValue())) {
                originalCharge = charge;
                break;
            }
        }

        if (originalCharge == null && revisedCharge == null) {
            return;
        }

        if (InvoiceProductCharge.Period.previous.equals(period)) {
            // original charge from previous period could have already been revised in current period bill as follows:
            // case 1 - new amount: issue a credit for the original amount and a charge from the new amount
            // case 2 - full refund: simply credit for the original amount
            Bill currBill = getBillProducer().getCurrentPeriodBill();
            // Look for corresponding charge in currBill where charge.period() = previous and use that as original charge if found
            InvoiceProductCharge found = null;
            for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(currBill, InvoiceProductCharge.class)) {
                if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                        && InvoiceProductCharge.Period.current.equals(charge.period().getValue())) {
                    originalCharge = found = charge;
                    break;
                }
            }
            if (found == null) {
                // no new charge found; now if we find a credit for the original charge then it was a full refund
                for (InvoiceProductCredit credit : BillingUtils.getLineItemsForType(currBill, InvoiceProductCredit.class)) {
                    if (credit.productCharge().equals(originalCharge)) {
                        // original charge was refunded - set to null
                        originalCharge = null;
                    }
                }
            }
        }

//        System.out.println("=0=> Revising (bill " + getBillingManager().getNextPeriodBill().billSequenceNumber().getValue() + "): found " + period.name()
//                + " charge for " + billableItem.item().description().getValue() + ": "
//                + (originalCharge == null ? "none" : originalCharge.amount().getValue() + "/" + originalCharge.postDate().getValue()) + " -> "
//                + (revisedCharge == null ? "none" : revisedCharge.amount().getValue()));

        if (originalCharge != null && revisedCharge != null && originalCharge.amount().getValue().compareTo(revisedCharge.amount().getValue()) == 0) {
            return;
        }

        if (originalCharge != null) {
//            System.out.println("=1=> Revising " + period.name() + " period: credit for " + originalCharge.amount().getValue());
            addCredit(originalCharge);
        }

        if (revisedCharge != null) {
//            System.out.println("=2=> Revising " + period.name() + " period: charge for " + revisedCharge.amount().getValue());
            addCharge(revisedCharge);
        }
    }

    private void addCredit(InvoiceProductCharge charge) {
        InvoiceProductCredit credit = EntityFactory.create(InvoiceProductCredit.class);
        credit.productCharge().set(charge);
        credit.amount().setValue(charge.amount().getValue().add(charge.taxTotal().getValue()).negate());
        credit.billingAccount().set(charge.billingAccount());
        credit.description().setValue(i18n.tr("Revised {0} Credit", charge.description().getValue()));
        getBillProducer().getNextPeriodBill().previousChargeRefunds()
                .setValue(getBillProducer().getNextPeriodBill().previousChargeRefunds().getValue().add(credit.amount().getValue()));
        getBillProducer().getNextPeriodBill().lineItems().add(credit);
    }
}
