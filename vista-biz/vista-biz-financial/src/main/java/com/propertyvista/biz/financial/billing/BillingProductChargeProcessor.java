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

import java.math.BigDecimal;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.MoneyUtils;
import com.propertyvista.biz.financial.TaxUtils;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceAdjustmentSubLineItem;
import com.propertyvista.domain.financial.billing.InvoiceConcessionSubLineItem;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceProductCredit;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.portal.rpc.shared.BillingException;

public class BillingProductChargeProcessor extends AbstractBillingProcessor {

    private static final I18n i18n = I18n.get(BillingProductChargeProcessor.class);

    BillingProductChargeProcessor(BillProducer billingManager) {
        super(billingManager);
    }

    @Override
    protected void execute() {
        createCharges();
    }

    private void createCharges() {

        if (!Bill.BillType.Final.equals(getBillingManager().getNextPeriodBill().billType().getValue())) {
            createCharge(getBillingManager().getNextPeriodBill().billingAccount().lease().currentTerm().version().leaseProducts().serviceItem());
        }

        for (BillableItem billableItem : getBillingManager().getNextPeriodBill().billingAccount().lease().currentTerm().version().leaseProducts()
                .featureItems()) {
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

//        reviseChargeForCurrentPeriod(billableItem);

//        reviseChargeForPreviousPeriod(billableItem);
    }

    private void createChargeForNextPeriod(BillableItem billableItem) {

        if (Bill.BillType.Final.equals(getBillingManager().getNextPeriodBill().billType().getValue())) {
            return;
        }
        addCharge(createCharge(billableItem, getBillingManager().getNextPeriodBill(), InvoiceProductCharge.Period.next));
    }

    private void reviseChargeForCurrentPeriod(BillableItem billableItem) {
        if (getBillingManager().getCurrentPeriodBill() == null) {
            return;
        }

        InvoiceProductCharge revisedCharge = createCharge(billableItem, getBillingManager().getCurrentPeriodBill(), InvoiceProductCharge.Period.current);

        InvoiceProductCharge originalCharge = null;

        for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(getBillingManager().getCurrentPeriodBill(), InvoiceProductCharge.class)) {
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
        if (getBillingManager().getPreviousPeriodBill() == null) {
            return;
        }

        InvoiceProductCharge finalCharge = createCharge(billableItem, getBillingManager().getPreviousPeriodBill(), InvoiceProductCharge.Period.previous);

        InvoiceProductCharge originalCharge = null;

        //TODO handle case when both previous and current have charge

        for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(getBillingManager().getPreviousPeriodBill(), InvoiceProductCharge.class)) {
            if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                    && InvoiceProductCharge.Period.next.equals(charge.period().getValue())) {
                originalCharge = charge;
                break;
            }
        }

        for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(getBillingManager().getCurrentPeriodBill(), InvoiceProductCharge.class)) {
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
        DateRange overlap = BillDateUtils.getOverlappingRange(new DateRange(bill.billingPeriodStartDate().getValue(), bill.billingPeriodEndDate().getValue()),
                new DateRange(billableItem.effectiveDate().getValue(), billableItem.expirationDate().getValue()));

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

        if (BillingUtils.isService(billableItem.item().product())) {
            charge.debitType().setValue(DebitType.lease);
        } else if (BillingUtils.isFeature(billableItem.item().product())) {
            switch (billableItem.item().product().<Feature.FeatureV> cast().holder().featureType().getValue()) {
            case parking:
                charge.debitType().setValue(DebitType.parking);
                break;
            case pet:
                charge.debitType().setValue(DebitType.pet);
                break;
            case addOn:
                charge.debitType().setValue(DebitType.addOn);
                break;
            case utility:
                charge.debitType().setValue(DebitType.utility);
                break;
            case locker:
                charge.debitType().setValue(DebitType.locker);
                break;
            case booking:
                charge.debitType().setValue(DebitType.booking);
                break;
            default:
                charge.debitType().setValue(DebitType.other);
                break;
            }
        }

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
        InvoiceAdjustmentSubLineItem adjustment = EntityFactory.create(InvoiceAdjustmentSubLineItem.class);

        BigDecimal amount = null;

        if (BillableItemAdjustment.Type.percentage.equals(billableItemAdjustment.type().getValue())) {
            amount = billableItemAdjustment.billableItem().agreedPrice().getValue().multiply(billableItemAdjustment.value().getValue());
        } else if (BillableItemAdjustment.Type.monetary.equals(billableItemAdjustment.type().getValue())) {
            amount = billableItemAdjustment.value().getValue();
        }

        if (Bill.BillType.Final.equals(getBillingManager().getNextPeriodBill().billType().getValue())) {
            //TODO final bill
            adjustment.amount().setValue(new BigDecimal("0.00"));
        } else {
            DateRange overlap = BillDateUtils.getOverlappingRange(new DateRange(bill.billingPeriodStartDate().getValue(), bill.billingPeriodEndDate()
                    .getValue()), new DateRange(billableItemAdjustment.effectiveDate().getValue(), billableItemAdjustment.expirationDate().getValue()));

            if (overlap == null) {
                return;
            }

            overlap = BillDateUtils.getOverlappingRange(overlap, new DateRange(billableItemAdjustment.billableItem().effectiveDate().getValue(),
                    billableItemAdjustment.billableItem().expirationDate().getValue()));

            if (overlap == null) {
                return;
            }

            BigDecimal proration = ProrationUtils.prorate(overlap.getFromDate(), overlap.getToDate(), bill.billingCycle());
            adjustment.amount().setValue(MoneyUtils.round(amount.multiply(proration)));
        }

        if (billableItemAdjustment.billableItem().item().type().isInstanceOf(FeatureItemType.class)) {
            adjustment.description().setValue(
                    billableItemAdjustment.billableItem().item().type().<FeatureItemType> cast().featureType().getStringView() + " " + i18n.tr("Adjustment"));
        } else if (billableItemAdjustment.billableItem().item().type().isInstanceOf(ServiceItemType.class)) {
            adjustment.description().setValue(
                    billableItemAdjustment.billableItem().item().type().<ServiceItemType> cast().serviceType().getStringView() + " " + i18n.tr("Adjustment"));
        }

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
            cycle = getBillingManager().getPreviousPeriodBill().billingCycle();
            break;
        case current:
            cycle = getBillingManager().getCurrentPeriodBill().billingCycle();
            break;
        case next:
            cycle = getBillingManager().getNextPeriodBill().billingCycle();
            break;
        }

        BigDecimal proration = ProrationUtils.prorate(charge.fromDate().getValue(), charge.toDate().getValue(), cycle);
        return MoneyUtils.round(charge.chargeSubLineItem().billableItem().agreedPrice().getValue().multiply(proration));
    }

    private void addCharge(InvoiceProductCharge charge) {
        if (charge == null) {
            return;
        }

        if (BillingUtils.isService(charge.chargeSubLineItem().billableItem().item().product())) { //Service
            getBillingManager().getNextPeriodBill().serviceCharge().setValue(charge.amount().getValue());
        } else if (BillingUtils.isRecurringFeature(charge.chargeSubLineItem().billableItem().item().product())) { //Recurring Feature
            getBillingManager().getNextPeriodBill().recurringFeatureCharges()
                    .setValue(getBillingManager().getNextPeriodBill().recurringFeatureCharges().getValue().add(charge.amount().getValue()));
        } else {
            getBillingManager().getNextPeriodBill().oneTimeFeatureCharges()
                    .setValue(getBillingManager().getNextPeriodBill().oneTimeFeatureCharges().getValue().add(charge.amount().getValue()));
        }
        getBillingManager().getNextPeriodBill().taxes().setValue(getBillingManager().getNextPeriodBill().taxes().getValue().add(charge.taxTotal().getValue()));
        getBillingManager().getNextPeriodBill().lineItems().add(charge);
    }

    private void reviseChargeForPeriod(BillableItem billableItem, InvoiceProductCharge.Period period) {
        Bill bill = null;
        switch (period) {
        case current:
            bill = getBillingManager().getCurrentPeriodBill();
            break;
        case previous:
            bill = getBillingManager().getPreviousPeriodBill();
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
            Bill currBill = getBillingManager().getCurrentPeriodBill();
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

//        System.out.println("=0=> Revising: found " + period.name() + " charge for " + billableItem.item().description().getValue() + ": "
//                + (originalCharge == null ? "none" : originalCharge.amount().getValue()) + " -> "
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
        getBillingManager().getNextPeriodBill().previousChargeRefunds()
                .setValue(getBillingManager().getNextPeriodBill().previousChargeRefunds().getValue().add(credit.amount().getValue()));
        getBillingManager().getNextPeriodBill().lineItems().add(credit);
    }
}
