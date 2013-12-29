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
package com.propertyvista.biz.financial.billing.internal;

import java.math.BigDecimal;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.AbstractBillingProcessor;
import com.propertyvista.domain.financial.ARCode.Type;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceCarryforwardCharge;
import com.propertyvista.domain.financial.billing.InvoiceCarryforwardCredit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;

public class BillingCarryforwardProcessor extends AbstractBillingProcessor<InternalBillProducer> {

    private static final I18n i18n = I18n.get(BillingCarryforwardProcessor.class);

    BillingCarryforwardProcessor(InternalBillProducer billProducer) {
        super(billProducer);
    }

    @Override
    public void execute() {
        createInitialBalanceRecord();
    }

    private void createInitialBalanceRecord() {
        // calculate product charge total
        Bill nextPeriodBill = getBillProducer().getNextPeriodBill();
        BigDecimal total = nextPeriodBill.serviceCharge().getValue()
        // @formatter:off
            .add(nextPeriodBill.recurringFeatureCharges().getValue())
            .add(nextPeriodBill.oneTimeFeatureCharges().getValue())
            .add(nextPeriodBill.depositAmount().getValue())
            .add(nextPeriodBill.taxes().getValue());
        // @formatter:on
        BigDecimal initialBalance = nextPeriodBill.billingAccount().carryforwardBalance().getValue().subtract(total);
        InvoiceLineItem zeroCycleBalance = null;
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            InvoiceCarryforwardCredit credit = EntityFactory.create(InvoiceCarryforwardCredit.class);
            credit.arCode().set(ServerSideFactory.create(ARFacade.class).getReservedARCode(Type.CarryForwardCredit));
            zeroCycleBalance = credit;
        } else {
            InvoiceCarryforwardCharge charge = EntityFactory.create(InvoiceCarryforwardCharge.class);
            charge.dueDate().setValue(getBillProducer().getNextPeriodBill().dueDate().getValue());
            charge.taxTotal().setValue(BigDecimal.ZERO);
            charge.arCode().set(ServerSideFactory.create(ARFacade.class).getReservedARCode(Type.CarryForwardCharge));
            zeroCycleBalance = charge;
        }
        zeroCycleBalance.billingAccount().set(getBillProducer().getNextPeriodBill().billingAccount());
        zeroCycleBalance.amount().setValue(initialBalance);
        zeroCycleBalance.description().setValue(i18n.tr("Carryforward Balance"));
        getBillProducer().getNextPeriodBill().lineItems().add(zeroCycleBalance);
        getBillProducer().getNextPeriodBill().carryForwardCredit()
                .setValue(getBillProducer().getNextPeriodBill().carryForwardCredit().getValue().add(zeroCycleBalance.amount().getValue()));

    }

}
