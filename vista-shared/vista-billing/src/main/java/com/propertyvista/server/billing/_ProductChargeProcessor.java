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

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillChargeTax;
import com.propertyvista.domain.financial.billing.BillEntryAdjustment;
import com.propertyvista.domain.financial.billing._InvoiceProductCharge;
import com.propertyvista.domain.tenant.lease.BillableItem;

public class _ProductChargeProcessor {

    private final static Logger log = LoggerFactory.getLogger(_ProductChargeProcessor.class);

    private final Billing billing;

    _ProductChargeProcessor(Billing billing) {
        this.billing = billing;

        billing.getNextPeriodBill().serviceCharge().setValue(new BigDecimal(0));
        billing.getNextPeriodBill().recurringFeatureCharges().setValue(new BigDecimal(0));
        billing.getNextPeriodBill().oneTimeFeatureCharges().setValue(new BigDecimal(0));

    }

    void createCharges() {
        if (!Bill.BillType.Final.equals(billing.getNextPeriodBill().billType().getValue())) {
            createChargeForNextPeriod(billing.getNextPeriodBill().billingAccount().lease().version().leaseProducts().serviceItem());
        }

        for (BillableItem billableItem : billing.getNextPeriodBill().billingAccount().lease().version().leaseProducts().featureItems()) {
            if (billableItem.isNull()) {
                throw new BillingException("Service Item is mandatory in lease");
            }
            Persistence.service().retrieve(billableItem.item().product());

            createChargeForNextPeriod(billableItem);

            updateChargeForCurrentPeriod(billableItem);

            updateChargeForPreviousPeriod(billableItem);
        }
    }

    private void createChargeForNextPeriod(BillableItem billableItem) {

        if (Bill.BillType.Final.equals(billing.getNextPeriodBill().billType().getValue())) {
            return;
        }
        addCharge(createCharge(billableItem, billing.getNextPeriodBill(), _InvoiceProductCharge.Period.next));
    }

    private boolean sameBillableItem(BillableItem billableItem1, BillableItem billableItem2) {
        return billableItem1.originalId().equals(billableItem2.originalId());
    }

    private void updateChargeForCurrentPeriod(BillableItem billableItem) {
        if (billing.getCurrentPeriodBill() == null) {
            return;
        }

        _InvoiceProductCharge revisedCharge = createCharge(billableItem, billing.getCurrentPeriodBill(), _InvoiceProductCharge.Period.current);

        _InvoiceProductCharge originalCharge = null;

        for (_InvoiceProductCharge charge : BillingUtils.getLineItemsForType(billing.getCurrentPeriodBill(), _InvoiceProductCharge.class)) {
            if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                    && _InvoiceProductCharge.Period.next.equals(charge.period().getValue())) {
                originalCharge = charge;
                break;
            }
        }

        if (revisedCharge != null && originalCharge == null) {
            addCharge(revisedCharge);
        } else if (revisedCharge != null && originalCharge != null) {
            if (!revisedCharge.total().getValue().equals(originalCharge.total().getValue())) {
                //TODO   billing.getBillEntryAdjustmentProcessor().createBillEntryAdjustment(originalCharge, revisedCharge);
            }
        }
    }

    private void updateChargeForPreviousPeriod(BillableItem billableItem) {
        if (billing.getPreviousPeriodBill() == null) {
            return;
        }

        _InvoiceProductCharge finalCharge = createCharge(billableItem, billing.getPreviousPeriodBill(), _InvoiceProductCharge.Period.previous);

        _InvoiceProductCharge originalCharge = null;

        //TODO handle case when both previous and current have charge

        for (_InvoiceProductCharge charge : BillingUtils.getLineItemsForType(billing.getPreviousPeriodBill(), _InvoiceProductCharge.class)) {
            if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                    && _InvoiceProductCharge.Period.next.equals(charge.period().getValue())) {
                originalCharge = charge;
                break;
            }
        }

        for (_InvoiceProductCharge charge : BillingUtils.getLineItemsForType(billing.getCurrentPeriodBill(), _InvoiceProductCharge.class)) {
            if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                    && _InvoiceProductCharge.Period.current.equals(charge.period().getValue())) {
                originalCharge = charge;
                break;
            }
        }

        if (finalCharge != null && originalCharge == null) {
            addCharge(finalCharge);
        } else if (finalCharge != null && originalCharge != null) {
            BillEntryAdjustment billEntryAdjustment = null;
            for (BillEntryAdjustment adjustment : billing.getCurrentPeriodBill().billEntryAdjustments()) {
                if (originalCharge.equals(adjustment.originalBillEntry())) {
                    billEntryAdjustment = adjustment;
                    break;
                }
            }
            if (billEntryAdjustment != null) {
                //TODO    billing.getBillEntryAdjustmentProcessor().createBillEntryAdjustment(billEntryAdjustment.revisedBillEntry(), finalCharge);
            }
        }
    }

    private _InvoiceProductCharge createCharge(BillableItem billableItem, Bill bill, _InvoiceProductCharge.Period period) {

        DateRange overlap = DateUtils.getOverlappingRange(new DateRange(bill.billingPeriodStartDate().getValue(), bill.billingPeriodEndDate().getValue()),
                new DateRange(billableItem.effectiveDate().getValue(), billableItem.expirationDate().getValue()));

        if (overlap == null) {
            return null;
        }

        _InvoiceProductCharge charge = EntityFactory.create(_InvoiceProductCharge.class);

        charge.billingAccount().set(bill.billingAccount());

        charge.bill().set(billing.getNextPeriodBill());
        charge.chargeSubLineItem().billableItem().set(billableItem);
        charge.period().setValue(period);

        charge.fromDate().setValue(overlap.getFromDate());
        charge.toDate().setValue(overlap.getToDate());

        if (BillingUtils.isOneTimeFeature(charge.chargeSubLineItem().billableItem().item().product())) {
            charge.total().setValue(charge.chargeSubLineItem().billableItem().item().price().getValue());
        } else {
            prorate(charge);
        }

        calculateTax(charge);

        Persistence.service().persist(charge);

        return charge;
    }

    private void prorate(_InvoiceProductCharge charge) {
        //TODO use policy to determin proration type
        BigDecimal proration = ProrationUtils.prorate(charge.fromDate().getValue(), charge.toDate().getValue(), BillingAccount.ProrationMethod.Actual);
        charge.total().setValue(charge.chargeSubLineItem().billableItem().item().price().getValue().multiply(proration));
    }

    private void calculateTax(_InvoiceProductCharge charge) {
        if (!charge.total().isNull()) {
            charge.taxes().addAll(
                    TaxUtils.calculateTaxes(charge.total().getValue(), charge.chargeSubLineItem().billableItem().item().type(), billing.getNextPeriodBill()
                            .billingRun().building()));
        }
        charge.taxTotal().setValue(new BigDecimal(0));
        for (BillChargeTax chargeTax : charge.taxes()) {
            charge.taxTotal().setValue(charge.taxTotal().getValue().add(chargeTax.amount().getValue()));
        }
    }

    private void addCharge(_InvoiceProductCharge charge) {
        if (charge == null) {
            return;
        }
        if (BillingUtils.isService(charge.chargeSubLineItem().billableItem().item().product())) { //Service
            billing.getNextPeriodBill().serviceCharge().setValue(charge.total().getValue());
        } else if (BillingUtils.isRecurringFeature(charge.chargeSubLineItem().billableItem().item().product())) { //Recurring Feature
            billing.getNextPeriodBill().recurringFeatureCharges()
                    .setValue(billing.getNextPeriodBill().recurringFeatureCharges().getValue().add(charge.total().getValue()));
        } else {
            billing.getNextPeriodBill().oneTimeFeatureCharges()
                    .setValue(billing.getNextPeriodBill().oneTimeFeatureCharges().getValue().add(charge.total().getValue()));
        }
        billing.getNextPeriodBill().lineItems().add(charge);
        billing.getNextPeriodBill().taxes().setValue(billing.getNextPeriodBill().taxes().getValue().add(charge.taxTotal().getValue()));
    }

}
