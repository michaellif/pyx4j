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

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillChargeAdjustment;
import com.propertyvista.domain.financial.billing.BillChargeTax;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseFinancial;

public class ChargeAdjustmentProcessor {

    private final Bill bill;

    ChargeAdjustmentProcessor(Bill bill) {
        this.bill = bill;
    }

    void createChargeAdjustments() {
        createChargeAdjustments(bill.billingAccount().leaseFinancial().lease().leaseProducts().serviceItem());
        for (BillableItem item : bill.billingAccount().leaseFinancial().lease().leaseProducts().featureItems()) {
            createChargeAdjustments(item);
        }
    }

    private void createChargeAdjustments(BillableItem item) {
        for (BillableItemAdjustment adjustment : item.adjustments()) {
            createChargeAdjustment(adjustment);
        }
    }

    private void createChargeAdjustment(BillableItemAdjustment itemAdjustment) {

        BillChargeAdjustment adjustment = EntityFactory.create(BillChargeAdjustment.class);
        adjustment.bill().set(bill);
        adjustment.billableItemAdjustment().set(itemAdjustment);

        BigDecimal amount = null;

        if (BillableItemAdjustment.AdjustmentType.percentage.equals(itemAdjustment.adjustmentType().getValue())) {
            amount = itemAdjustment.billableItem().item().price().getValue().multiply(itemAdjustment.value().getValue());
        } else if (BillableItemAdjustment.AdjustmentType.monetary.equals(itemAdjustment.adjustmentType().getValue())) {
            amount = itemAdjustment.value().getValue();
        } else if (BillableItemAdjustment.AdjustmentType.free.equals(itemAdjustment.adjustmentType().getValue())) {
            amount = itemAdjustment.billableItem().item().price().getValue().multiply(new BigDecimal(-1));
        }

        if (Bill.BillType.Final.equals(bill.billType().getValue())) {
            //TODO final bill
            adjustment.amount().setValue(new BigDecimal("0.00"));
        } else {
            DateRange overlap = DateUtils.getOverlappingRange(new DateRange(bill.billingPeriodStartDate().getValue(), bill.billingPeriodEndDate().getValue()),
                    new DateRange(itemAdjustment.effectiveDate().getValue(), itemAdjustment.expirationDate().getValue()));

            if (overlap == null) {
                return;
            }

            overlap = DateUtils.getOverlappingRange(overlap, new DateRange(itemAdjustment.billableItem().effectiveDate().getValue(), itemAdjustment
                    .billableItem().expirationDate().getValue()));

            if (overlap == null) {
                return;
            }

            //TODO use policy to determin proration type
            BigDecimal proration = ProrationUtils.prorate(overlap.getFromDate(), overlap.getToDate(), LeaseFinancial.ProrationMethod.Actual);
            adjustment.amount().setValue(amount.multiply(proration));
        }

        if (!adjustment.amount().isNull()) {
            adjustment.taxes().addAll(
                    TaxUtils.calculateTaxes(adjustment.amount().getValue(), itemAdjustment.billableItem().item().type(), bill.billingRun().building()));
        }
        adjustment.taxTotal().setValue(new BigDecimal(0));
        for (BillChargeTax chargeTax : adjustment.taxes()) {
            adjustment.taxTotal().setValue(adjustment.taxTotal().getValue().add(chargeTax.amount().getValue()));
        }

        addCharge(adjustment);
    }

    private void addCharge(BillChargeAdjustment adjustment) {
        bill.chargeAdjustments().add(adjustment);
        bill.totalAdjustments().setValue(bill.totalAdjustments().getValue().add(adjustment.amount().getValue()));
        bill.taxes().setValue(bill.taxes().getValue().add(adjustment.taxTotal().getValue()));
    }
}
