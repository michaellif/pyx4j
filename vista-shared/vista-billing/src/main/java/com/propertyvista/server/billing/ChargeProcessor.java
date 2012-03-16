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
import com.propertyvista.domain.financial.billing.BillCharge;
import com.propertyvista.domain.financial.billing.BillChargeTax;
import com.propertyvista.domain.financial.billing.BillEntry;
import com.propertyvista.domain.financial.billing.BillEntryAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItem;

public class ChargeProcessor {

    private final static Logger log = LoggerFactory.getLogger(ChargeProcessor.class);

    private final Billing billing;

    ChargeProcessor(Billing billing) {
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
        addCharge(createCharge(billableItem, billing.getNextPeriodBill(), BillEntry.Period.next));
    }

    private void updateChargeForCurrentPeriod(BillableItem billableItem) {
        if (billing.getCurrentPeriodBill() == null) {
            return;
        }

        BillCharge revisedCharge = createCharge(billableItem, billing.getCurrentPeriodBill(), BillEntry.Period.current);

        BillCharge originalCharge = null;

        for (BillCharge charge : billing.getCurrentPeriodBill().charges()) {
            if (billableItem.equals(charge.billableItem()) && BillEntry.Period.next.equals(charge.period().getValue())) {
                originalCharge = charge;
                break;
            }
        }

        if (revisedCharge != null && originalCharge == null) {
            addCharge(revisedCharge);
        } else if (revisedCharge != null && originalCharge != null) {
            if (!revisedCharge.amount().getValue().equals(originalCharge.amount().getValue())) {
                billing.getBillEntryAdjustmentProcessor().createBillEntryAdjustment(originalCharge, revisedCharge);
            }
        }
    }

    private void updateChargeForPreviousPeriod(BillableItem billableItem) {
        if (billing.getPreviousPeriodBill() == null) {
            return;
        }

        BillCharge finalCharge = createCharge(billableItem, billing.getPreviousPeriodBill(), BillEntry.Period.previous);

        BillCharge originalCharge = null;

        for (BillCharge charge : billing.getPreviousPeriodBill().charges()) {
            if (billableItem.equals(charge.billableItem()) && BillEntry.Period.next.equals(charge.period().getValue())) {
                originalCharge = charge;
                break;
            }
        }

        for (BillCharge charge : billing.getCurrentPeriodBill().charges()) {
            if (billableItem.equals(charge.billableItem()) && BillEntry.Period.current.equals(charge.period().getValue())) {
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
                billing.getBillEntryAdjustmentProcessor().createBillEntryAdjustment(billEntryAdjustment.revisedBillEntry(), finalCharge);
            }
        }
    }

    private BillCharge createCharge(BillableItem billableItem, Bill bill, BillEntry.Period period) {

        DateRange overlap = DateUtils.getOverlappingRange(new DateRange(bill.billingPeriodStartDate().getValue(), bill.billingPeriodEndDate().getValue()),
                new DateRange(billableItem.effectiveDate().getValue(), billableItem.expirationDate().getValue()));

        if (overlap == null) {
            return null;
        }

        BillCharge charge = EntityFactory.create(BillCharge.class);
        charge.bill().set(billing.getNextPeriodBill());
        charge.billableItem().set(billableItem);
        charge.period().setValue(period);

        charge.fromDate().setValue(overlap.getFromDate());
        charge.toDate().setValue(overlap.getToDate());

        if (BillingUtils.isOneTimeFeature(charge.billableItem().item().product())) {
            charge.amount().setValue(charge.billableItem().item().price().getValue());
        } else {
            prorate(charge);
        }

        calculateTax(charge);

        return charge;
    }

    private void prorate(BillCharge charge) {
        //TODO use policy to determin proration type
        BigDecimal proration = ProrationUtils.prorate(charge.fromDate().getValue(), charge.toDate().getValue(), BillingAccount.ProrationMethod.Actual);
        charge.amount().setValue(charge.billableItem().item().price().getValue().multiply(proration));
    }

    private void calculateTax(BillCharge charge) {
        if (!charge.amount().isNull()) {
            charge.taxes().addAll(
                    TaxUtils.calculateTaxes(charge.amount().getValue(), charge.billableItem().item().type(), billing.getNextPeriodBill().billingRun()
                            .building()));
        }
        charge.taxTotal().setValue(new BigDecimal(0));
        for (BillChargeTax chargeTax : charge.taxes()) {
            charge.taxTotal().setValue(charge.taxTotal().getValue().add(chargeTax.amount().getValue()));
        }
    }

    private void addCharge(BillCharge charge) {
        if (charge == null) {
            return;
        }
        if (BillingUtils.isService(charge.billableItem().item().product())) { //Service
            billing.getNextPeriodBill().serviceCharge().setValue(charge.amount().getValue());
        } else if (BillingUtils.isRecurringFeature(charge.billableItem().item().product())) { //Recurring Feature
            billing.getNextPeriodBill().recurringFeatureCharges()
                    .setValue(billing.getNextPeriodBill().recurringFeatureCharges().getValue().add(charge.amount().getValue()));
        } else {
            billing.getNextPeriodBill().oneTimeFeatureCharges()
                    .setValue(billing.getNextPeriodBill().oneTimeFeatureCharges().getValue().add(charge.amount().getValue()));
        }
        billing.getNextPeriodBill().charges().add(charge);
        billing.getNextPeriodBill().taxes().setValue(billing.getNextPeriodBill().taxes().getValue().add(charge.taxTotal().getValue()));
    }

}
