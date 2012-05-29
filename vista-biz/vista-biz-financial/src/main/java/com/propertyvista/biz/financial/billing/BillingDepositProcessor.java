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
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit.ValueType;

public class BillingDepositProcessor extends AbstractProcessor {

    private static final I18n i18n = I18n.get(BillingDepositProcessor.class);

    private final AbstractBillingProcessor billing;

    BillingDepositProcessor(AbstractBillingProcessor billing) {
        this.billing = billing;
    }

    void createDeposits() {

        createDeposit(billing.getNextPeriodBill().billingAccount().lease().version().leaseProducts().serviceItem());

        for (BillableItem billableItem : billing.getNextPeriodBill().billingAccount().lease().version().leaseProducts().featureItems()) {
            if (billableItem.isNull()) {
                throw new BillingException("Service Item is mandatory in lease");
            }
            createDeposit(billableItem);
        }
    }

    //Deposit should be taken on a first billing period of the BillableItem
    private void createDeposit(BillableItem billableItem) {

        if (billableItem.deposit().isNull()) {
            return;
        }

        InvoiceProductCharge nextCharge = null;
        InvoiceProductCharge currentCharge = null;
        for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(billing.getNextPeriodBill(), InvoiceProductCharge.class)) {
            if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                    && InvoiceProductCharge.Period.next.equals(charge.period().getValue())) {
                nextCharge = charge;
                break;
            }
        }

        if (billing.getCurrentPeriodBill() != null) {
            for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(billing.getCurrentPeriodBill(), InvoiceProductCharge.class)) {
                if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                        && InvoiceProductCharge.Period.next.equals(charge.period().getValue())) {
                    currentCharge = charge;
                    break;
                }
            }
        }

        //This is first time charge have been issued - add deposit
        if (nextCharge != null && currentCharge == null) {
            if (!billableItem.deposit().isNull()) {
                InvoiceDeposit deposit = EntityFactory.create(InvoiceDeposit.class);
                deposit.billingAccount().set(billing.getNextPeriodBill().billingAccount());
                deposit.dueDate().setValue(billing.getNextPeriodBill().dueDate().getValue());
                deposit.debitType().setValue(DebitType.deposit);
                deposit.description().setValue(i18n.tr("Deposit for") + " " + billableItem.item().description().getStringView());

                if (ValueType.amount == billableItem.deposit().valueType().getValue()) {
                    deposit.amount().setValue(billableItem.deposit().depositAmount().getValue());
                } else if (ValueType.percentage == billableItem.deposit().valueType().getValue()) {
                    //TODO consider real price of service or feature including concessions etc
                    deposit.amount().setValue(billableItem.deposit().depositAmount().getValue().multiply(billableItem.item().price().getValue()));
                } else {
                    throw new Error("Unsupported ValueType");
                }

                deposit.taxTotal().setValue(new BigDecimal("0.00"));

                Persistence.service().persist(deposit);

                addDeposit(deposit);
            }
        }
    }

    private void addDeposit(InvoiceDeposit deposit) {
        if (deposit == null) {
            return;
        }
        billing.getNextPeriodBill().depositAmount().setValue(billing.getNextPeriodBill().depositAmount().getValue().add(deposit.amount().getValue()));
        billing.getNextPeriodBill().lineItems().add(deposit);
        //TODO
        // billing.getNextPeriodBill().taxes().setValue(billing.getNextPeriodBill().taxes().getValue().add(deposit.taxTotal().getValue()));
    }

    private void attachDepositRefund(InvoiceDepositRefund depositRefund) {
        billing.getNextPeriodBill().lineItems().add(depositRefund);
        billing.getNextPeriodBill().depositRefundAmount()
                .setValue(billing.getNextPeriodBill().depositRefundAmount().getValue().add(depositRefund.amount().getValue().negate()));
    }

}
