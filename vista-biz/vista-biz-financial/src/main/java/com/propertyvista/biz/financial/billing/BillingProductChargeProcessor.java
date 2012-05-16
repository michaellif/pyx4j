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

import com.propertyvista.biz.financial.AbstractProcessor;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceAdjustmentSubLineItem;
import com.propertyvista.domain.financial.billing.InvoiceChargeTax;
import com.propertyvista.domain.financial.billing.InvoiceConcessionSubLineItem;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;

public class BillingProductChargeProcessor extends AbstractProcessor {

    private static final I18n i18n = I18n.get(BillingProductChargeProcessor.class);

    private final Billing billing;

    BillingProductChargeProcessor(Billing billing) {
        this.billing = billing;

        billing.getNextPeriodBill().serviceCharge().setValue(new BigDecimal(0));
        billing.getNextPeriodBill().recurringFeatureCharges().setValue(new BigDecimal(0));
        billing.getNextPeriodBill().oneTimeFeatureCharges().setValue(new BigDecimal(0));

    }

    void createCharges() {

        if (!Bill.BillType.Final.equals(billing.getNextPeriodBill().billType().getValue())) {
            createCharge(billing.getNextPeriodBill().billingAccount().lease().version().leaseProducts().serviceItem());
        }

        for (BillableItem billableItem : billing.getNextPeriodBill().billingAccount().lease().version().leaseProducts().featureItems()) {
            if (billableItem.isNull()) {
                throw new BillingException("Service Item is mandatory in lease");
            }
            createCharge(billableItem);
        }
    }

    private void createCharge(BillableItem billableItem) {
        Persistence.service().retrieve(billableItem.item().product());

        createChargeForNextPeriod(billableItem);

        reviseChargeForCurrentPeriod(billableItem);

        reviseChargeForPreviousPeriod(billableItem);
    }

    private void createChargeForNextPeriod(BillableItem billableItem) {

        if (Bill.BillType.Final.equals(billing.getNextPeriodBill().billType().getValue())) {
            return;
        }
        addCharge(createCharge(billableItem, billing.getNextPeriodBill(), InvoiceProductCharge.Period.next));
    }

    private void reviseChargeForCurrentPeriod(BillableItem billableItem) {
        if (billing.getCurrentPeriodBill() == null) {
            return;
        }

        InvoiceProductCharge revisedCharge = createCharge(billableItem, billing.getCurrentPeriodBill(), InvoiceProductCharge.Period.current);

        InvoiceProductCharge originalCharge = null;

        for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(billing.getCurrentPeriodBill(), InvoiceProductCharge.class)) {
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
                //TODO   billing.getBillEntryAdjustmentProcessor().createBillEntryAdjustment(originalCharge, revisedCharge);
            }
        }
    }

    private void reviseChargeForPreviousPeriod(BillableItem billableItem) {
        if (billing.getPreviousPeriodBill() == null) {
            return;
        }

        InvoiceProductCharge finalCharge = createCharge(billableItem, billing.getPreviousPeriodBill(), InvoiceProductCharge.Period.previous);

        InvoiceProductCharge originalCharge = null;

        //TODO handle case when both previous and current have charge

        for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(billing.getPreviousPeriodBill(), InvoiceProductCharge.class)) {
            if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                    && InvoiceProductCharge.Period.next.equals(charge.period().getValue())) {
                originalCharge = charge;
                break;
            }
        }

        for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(billing.getCurrentPeriodBill(), InvoiceProductCharge.class)) {
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
//            for (BillEntryAdjustment adjustment : billing.getCurrentPeriodBill().billEntryAdjustments()) {
//                if (originalCharge.equals(adjustment.originalBillEntry())) {
//                    billEntryAdjustment = adjustment;
//                    break;
//                }
//            }
//            if (billEntryAdjustment != null) {
//                //TODO    billing.getBillEntryAdjustmentProcessor().createBillEntryAdjustment(billEntryAdjustment.revisedBillEntry(), finalCharge);
//            }
        }
    }

    private InvoiceProductCharge createCharge(BillableItem billableItem, Bill bill, InvoiceProductCharge.Period period) {

        // Find if billable item period overlaps with the bill period. 
        DateRange overlap = DateUtils.getOverlappingRange(new DateRange(bill.billingPeriodStartDate().getValue(), bill.billingPeriodEndDate().getValue()),
                new DateRange(billableItem.effectiveDate().getValue(), billableItem.expirationDate().getValue()));

        // If billable item is not in effect in this billing period do nothing
        if (overlap == null) {
            return null;
        }

        InvoiceProductCharge charge = EntityFactory.create(InvoiceProductCharge.class);

        charge.billingAccount().set(bill.billingAccount());
        charge.bill().set(billing.getNextPeriodBill());
        charge.period().setValue(period);
        charge.fromDate().setValue(overlap.getFromDate());
        charge.toDate().setValue(overlap.getToDate());
        charge.dueDate().setValue(billing.getNextPeriodBill().billingPeriodStartDate().getValue());

        if (BillingUtils.isService(billableItem.item().product())) {
            charge.debitType().setValue(DebitType.lease);
        } else if (BillingUtils.isFeature(billableItem.item().product())) {
            switch (billableItem.item().product().<Feature.FeatureV> cast().type().getValue()) {
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
        createAdjustmentSubLineItems(charge, billableItem);
        createConcessionSubLineItems(charge, billableItem);

        charge.amount().setValue(charge.chargeSubLineItem().amount().getValue());

        for (InvoiceAdjustmentSubLineItem subLineItem : charge.adjustmentSubLineItems()) {
            charge.amount().setValue(charge.amount().getValue().add(subLineItem.amount().getValue()));
        }

        for (InvoiceConcessionSubLineItem subLineItem : charge.concessionSubLineItems()) {
            charge.amount().setValue(charge.amount().getValue().add(subLineItem.amount().getValue()));
        }

        calculateTax(charge);

        charge.description().setValue(charge.chargeSubLineItem().billableItem().item().description().getStringView());

        return charge;
    }

    private void createChargeSubLineItem(InvoiceProductCharge charge, BillableItem billableItem) {
        charge.chargeSubLineItem().billableItem().set(billableItem);

        if (BillingUtils.isOneTimeFeature(charge.chargeSubLineItem().billableItem().item().product())) {
            // do not prorate one-time charge
            charge.chargeSubLineItem().amount().setValue(charge.chargeSubLineItem().billableItem().item().price().getValue());
        } else {
            charge.chargeSubLineItem().amount().setValue(prorate(charge));
        }

        charge.chargeSubLineItem().description().setValue(billableItem.item().description().getStringView());
    }

    private void createAdjustmentSubLineItems(InvoiceProductCharge charge, BillableItem billableItem) {
        for (BillableItemAdjustment adjustment : billableItem.adjustments()) {
            createAdjustmentSubLineItem(adjustment, charge);
        }
    }

    private void createAdjustmentSubLineItem(BillableItemAdjustment billableItemAdjustment, InvoiceProductCharge charge) {
        InvoiceAdjustmentSubLineItem adjustment = EntityFactory.create(InvoiceAdjustmentSubLineItem.class);

        BigDecimal amount = null;

        if (BillableItemAdjustment.AdjustmentType.percentage.equals(billableItemAdjustment.adjustmentType().getValue())) {
            amount = billableItemAdjustment.billableItem().item().price().getValue().multiply(billableItemAdjustment.value().getValue());
        } else if (BillableItemAdjustment.AdjustmentType.monetary.equals(billableItemAdjustment.adjustmentType().getValue())) {
            amount = billableItemAdjustment.value().getValue();
        }

        if (Bill.BillType.Final.equals(billing.getNextPeriodBill().billType().getValue())) {
            //TODO final bill
            adjustment.amount().setValue(new BigDecimal("0.00"));
        } else {
            DateRange overlap = DateUtils.getOverlappingRange(new DateRange(billing.getNextPeriodBill().billingPeriodStartDate().getValue(), billing
                    .getNextPeriodBill().billingPeriodEndDate().getValue()), new DateRange(billableItemAdjustment.effectiveDate().getValue(),
                    billableItemAdjustment.expirationDate().getValue()));

            if (overlap == null) {
                return;
            }

            overlap = DateUtils.getOverlappingRange(overlap, new DateRange(billableItemAdjustment.billableItem().effectiveDate().getValue(),
                    billableItemAdjustment.billableItem().expirationDate().getValue()));

            if (overlap == null) {
                return;
            }

            //TODO use policy to determin proration type
            BigDecimal proration = ProrationUtils.prorate(overlap.getFromDate(), overlap.getToDate(), billing.getNextPeriodBill().billingRun().building());
            adjustment.amount().setValue(amount.multiply(proration));
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
        //TODO use policy to determin proration type
        BigDecimal proration = ProrationUtils.prorate(charge.fromDate().getValue(), charge.toDate().getValue(), billing.getNextPeriodBill().billingRun()
                .building());
        return charge.chargeSubLineItem().billableItem().item().price().getValue().multiply(proration);
    }

    private void calculateTax(InvoiceProductCharge charge) {
        if (!charge.amount().isNull()) {
            charge.taxes().addAll(
                    TaxUtils.calculateTaxes(charge.amount().getValue(), charge.chargeSubLineItem().billableItem().item().type(), billing.getNextPeriodBill()
                            .billingRun().building()));
        }
        charge.taxTotal().setValue(new BigDecimal(0));
        for (InvoiceChargeTax chargeTax : charge.taxes()) {
            charge.taxTotal().setValue(charge.taxTotal().getValue().add(chargeTax.amount().getValue()));
        }
    }

    private void addCharge(InvoiceProductCharge charge) {
        if (charge == null) {
            return;
        }

        Persistence.service().persist(charge);

        if (BillingUtils.isService(charge.chargeSubLineItem().billableItem().item().product())) { //Service
            billing.getNextPeriodBill().serviceCharge().setValue(charge.amount().getValue());
        } else if (BillingUtils.isRecurringFeature(charge.chargeSubLineItem().billableItem().item().product())) { //Recurring Feature
            billing.getNextPeriodBill().recurringFeatureCharges()
                    .setValue(billing.getNextPeriodBill().recurringFeatureCharges().getValue().add(charge.amount().getValue()));
        } else {
            billing.getNextPeriodBill().oneTimeFeatureCharges()
                    .setValue(billing.getNextPeriodBill().oneTimeFeatureCharges().getValue().add(charge.amount().getValue()));
        }
        billing.getNextPeriodBill().lineItems().add(charge);
        billing.getNextPeriodBill().taxes().setValue(billing.getNextPeriodBill().taxes().getValue().add(charge.taxTotal().getValue()));
    }

}
