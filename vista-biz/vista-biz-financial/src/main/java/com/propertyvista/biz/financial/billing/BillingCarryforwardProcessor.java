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

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceCarryforwardCharge;
import com.propertyvista.domain.financial.billing.InvoiceCarryforwardCredit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;

public class BillingCarryforwardProcessor extends AbstractBillingProcessor {

    private static final I18n i18n = I18n.get(BillingCarryforwardProcessor.class);

    BillingCarryforwardProcessor(AbstractBillingManager billingManager) {
        super(billingManager);
    }

    @Override
    protected void execute() {
        createInitialBalanceRecord();
    }

    private void createInitialBalanceRecord() {
        // calculate product charge total
        Bill nextPeriodBill = getBillingManager().getNextPeriodBill();
        BigDecimal total = nextPeriodBill.serviceCharge().getValue()
// @formatter:off
                .add(nextPeriodBill.recurringFeatureCharges().getValue())
                .add(nextPeriodBill.taxes().getValue());
        BigDecimal initialBalance = nextPeriodBill.billingAccount().carryforwardBalance().getValue().subtract(total);
        InvoiceLineItem zeroCycleBalance = null;
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            InvoiceCarryforwardCharge charge = EntityFactory.create(InvoiceCarryforwardCharge.class);
            charge.dueDate().setValue(getBillingManager().getNextPeriodBill().dueDate().getValue());
            charge.taxTotal().setValue(BigDecimal.ZERO);
            zeroCycleBalance = charge;
        } else {
            InvoiceCarryforwardCredit credit = EntityFactory.create(InvoiceCarryforwardCredit.class);
            zeroCycleBalance = credit;
        }
        zeroCycleBalance.billingAccount().set(getBillingManager().getNextPeriodBill().billingAccount());
        zeroCycleBalance.amount().setValue(initialBalance);
        zeroCycleBalance.description().setValue(i18n.tr("Carryforward Balance"));
        Persistence.service().persist(zeroCycleBalance);
        getBillingManager().getNextPeriodBill().lineItems().add(zeroCycleBalance);
        getBillingManager().getNextPeriodBill().carryForwardCredit()
        .setValue(getBillingManager().getNextPeriodBill().carryForwardCredit().getValue().add(zeroCycleBalance.amount().getValue()));

    }

}
