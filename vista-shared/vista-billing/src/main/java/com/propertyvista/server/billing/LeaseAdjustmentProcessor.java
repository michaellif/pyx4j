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
import com.propertyvista.domain.financial.billing.BillChargeTax;
import com.propertyvista.domain.financial.billing.BillLeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class LeaseAdjustmentProcessor {

    private final Bill bill;

    LeaseAdjustmentProcessor(Bill bill) {
        this.bill = bill;
    }

    void createLeaseAdjustments() {
        for (LeaseAdjustment item : bill.billingAccount().leaseFinancial().adjustments()) {
            createLeaseAdjustment(item);
        }
    }

    private void createLeaseAdjustment(LeaseAdjustment item) {
        if (!isLeaseAdjustmentApplicable(item)) {
            return;
        }

        BillLeaseAdjustment adjustment = EntityFactory.create(BillLeaseAdjustment.class);
        adjustment.bill().set(bill);
        bill.leaseAdjustments().add(adjustment);
        bill.totalAdjustments().setValue(bill.totalAdjustments().getValue().add(adjustment.amount().getValue()));

        if (!adjustment.amount().isNull()) {
            adjustment.taxes().addAll(TaxUtils.calculateTaxes(adjustment.amount().getValue(), item.reason(), bill.billingRun().building()));
        }
        adjustment.taxTotal().setValue(new BigDecimal(0));
        for (BillChargeTax chargeTax : adjustment.taxes()) {
            adjustment.taxTotal().setValue(adjustment.taxTotal().getValue().add(chargeTax.amount().getValue()));
        }

        addLeaseAdjustment(adjustment);
    }

    private void addLeaseAdjustment(BillLeaseAdjustment item) {
        bill.totalAdjustments().setValue(bill.totalAdjustments().getValue().add(item.amount().getValue()));
        bill.leaseAdjustments().add(item);
        bill.taxes().setValue(bill.taxes().getValue().add(item.taxTotal().getValue()));
    }

    private boolean isLeaseAdjustmentApplicable(LeaseAdjustment item) {
        // TODO Implement
        return true;
    }

}
