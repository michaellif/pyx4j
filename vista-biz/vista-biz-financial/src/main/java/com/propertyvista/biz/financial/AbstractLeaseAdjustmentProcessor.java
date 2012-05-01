/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial;

import java.math.BigDecimal;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.billing.TaxUtils;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceChargeTax;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class AbstractLeaseAdjustmentProcessor extends AbstractProcessor {

    protected InvoiceAccountCharge createCharge(LeaseAdjustment adjustment) {
        InvoiceAccountCharge charge = EntityFactory.create(InvoiceAccountCharge.class);

        charge.billingAccount().set(adjustment.billingAccount());
        charge.amount().setValue(adjustment.amount().getValue().negate());
        charge.adjustment().set(adjustment);
        charge.targetDate().setValue(adjustment.targetDate().getValue());
        charge.description().setValue(adjustment.reason().name().getValue());
        charge.debitType().setValue(DebitType.accountCharge);

        calculateTax(charge, adjustment.billingAccount().lease().unit().belongsTo());

        return charge;

    }

    protected InvoiceAccountCredit createCredit(LeaseAdjustment adjustment) {
        InvoiceAccountCredit credit = EntityFactory.create(InvoiceAccountCredit.class);

        credit.billingAccount().set(adjustment.billingAccount());
        credit.amount().setValue(adjustment.amount().getValue().negate());
        credit.adjustment().set(adjustment);
        credit.targetDate().setValue(adjustment.targetDate().getValue());
        credit.description().setValue(adjustment.reason().name().getValue());

        return credit;
    }

    private void calculateTax(InvoiceAccountCharge charge, Building building) {
        if (!charge.amount().isNull()) {
            charge.taxes().addAll(TaxUtils.calculateTaxes(charge.amount().getValue(), charge.adjustment().reason(), building));
        }
        charge.taxTotal().setValue(new BigDecimal(0));
        for (InvoiceChargeTax chargeTax : charge.taxes()) {
            charge.taxTotal().setValue(charge.taxTotal().getValue().add(chargeTax.amount().getValue()));
        }
    }
}
