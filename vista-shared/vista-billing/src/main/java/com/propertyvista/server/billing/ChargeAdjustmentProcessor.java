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
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;

public class ChargeAdjustmentProcessor {

    private final Bill bill;

    ChargeAdjustmentProcessor(Bill bill) {
        this.bill = bill;
    }

    void createChargeAdjustments() {
        createChargeAdjustments(bill.billingAccount().leaseFinancial().lease().serviceAgreement().serviceItem());
        for (BillableItem item : bill.billingAccount().leaseFinancial().lease().serviceAgreement().featureItems()) {
            createChargeAdjustments(item);
        }
    }

    private void createChargeAdjustments(BillableItem item) {
        for (BillableItemAdjustment adjustment : item.adjustments()) {
            createChargeAdjustment(adjustment);
        }
    }

    private void createChargeAdjustment(BillableItemAdjustment itemAdjustment) {

        if (!isBillableItemAdjustmentApplicable(itemAdjustment, bill)) {
            return;
        }

        BillChargeAdjustment adjustment = EntityFactory.create(BillChargeAdjustment.class);
        adjustment.bill().set(bill);
        adjustment.billableItemAdjustment().set(itemAdjustment);

        if (BillableItemAdjustment.AdjustmentType.percentage.equals(itemAdjustment.adjustmentType().getValue())) {
            adjustment.amount().setValue(itemAdjustment.billableItem().item().price().getValue().multiply(itemAdjustment.value().getValue()));
        } else if (BillableItemAdjustment.AdjustmentType.monetary.equals(itemAdjustment.adjustmentType().getValue())) {
            adjustment.amount().setValue(itemAdjustment.value().getValue());
        } else if (BillableItemAdjustment.AdjustmentType.free.equals(itemAdjustment.adjustmentType().getValue())) {
            adjustment.amount().setValue(itemAdjustment.billableItem().item().price().getValue().multiply(new BigDecimal(-1)));
        }

        bill.chargeAdjustments().add(adjustment);
        bill.totalAdjustments().setValue(bill.totalAdjustments().getValue().add(adjustment.amount().getValue()));
    }

    private static boolean isBillableItemAdjustmentApplicable(BillableItemAdjustment adjustment, Bill bill) {
//        if (BillableItemAdjustment.TermType.postLease.equals(adjustment.termType().getValue())) {
//            return false;
//        } else if (BillableItemAdjustment.TermType.oneTime.equals(adjustment.termType().getValue())
//                && bill.billingPeriodNumber().getValue() == adjustment.billingPeriodNumber().getValue()) {
//            return true;
//        } else if (BillableItemAdjustment.TermType.inLease.equals(adjustment.termType().getValue())
//                && bill.billingPeriodNumber().getValue() >= adjustment.billingPeriodNumber().getValue()) {
//            return true;
//        } else {
//            return false;
//        }
        return true;
    }

}
