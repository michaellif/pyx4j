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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillCharge;
import com.propertyvista.domain.financial.billing.BillChargeTax;
import com.propertyvista.domain.tenant.lease.BillableItem;

public class ChargeProcessor {

    private final Bill bill;

    ChargeProcessor(Bill bill) {
        this.bill = bill;
    }

    void createCharges() {
        createCharge(bill.billingAccount().leaseFinancial().lease().serviceAgreement().serviceItem());
        for (BillableItem item : bill.billingAccount().leaseFinancial().lease().serviceAgreement().featureItems()) {
            createCharge(item);
        }
    }

    private void createCharge(BillableItem billableItem) {
        if (billableItem.isNull()) {
            throw new Error("Service Item is mandatory in lease");
        }

        Persistence.service().retrieve(billableItem.item().product());

        if (!isBillableItemApplicable(billableItem, bill)) {
            return;
        }

        BillCharge charge = EntityFactory.create(BillCharge.class);
        charge.bill().set(bill);
        charge.billableItem().set(billableItem);
        charge.amount().setValue(billableItem.item().price().getValue());
        if (!charge.amount().isNull()) {
            charge.taxes().addAll(TaxUtils.calculateTaxes(charge.amount().getValue(), billableItem.item().type(), bill.billingRun().building()));
        }
        charge.taxTotal().setValue(new BigDecimal(0));
        for (BillChargeTax chargeTax : charge.taxes()) {
            charge.taxTotal().setValue(charge.taxTotal().getValue().add(chargeTax.amount().getValue()));
        }

        addCharge(charge);
    }

    private void addCharge(BillCharge charge) {
        if (BillingUtils.isService(charge.billableItem().item().product())) { //Service
            bill.serviceCharge().setValue(charge.amount().getValue());
        } else if (BillingUtils.isRecurringFeature(charge.billableItem().item().product())) { //Recurring Feature
            bill.recurringFeatureCharges().setValue(bill.recurringFeatureCharges().getValue().add(charge.amount().getValue()));
        } else {
            bill.oneTimeFeatureCharges().setValue(bill.oneTimeFeatureCharges().getValue().add(charge.amount().getValue()));
        }
        bill.charges().add(charge);
        bill.taxes().setValue(bill.taxes().getValue().add(charge.taxTotal().getValue()));
    }

    private static boolean isBillableItemApplicable(BillableItem item, Bill bill) {
        if (item.billingPeriodNumber().isNull()) {
            throw new Error("billingPeriodNumber should not be null");
        } else if (bill.billingPeriodNumber().getValue() >= item.billingPeriodNumber().getValue()) {
            if (BillingUtils.isFeature(item.item().product()) && !BillingUtils.isRecurringFeature(item.item().product())) {
                return bill.billingPeriodNumber().getValue() == item.billingPeriodNumber().getValue();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
}
