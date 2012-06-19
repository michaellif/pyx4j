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
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.ValueType;

public class BillingDepositProcessor extends AbstractBillingProcessor {

    private static final I18n i18n = I18n.get(BillingDepositProcessor.class);

    BillingDepositProcessor(AbstractBillingManager billingManager) {
        super(billingManager);
    }

    @Override
    protected void execute() {
        createDeposits();
        attachDepositRefunds();
    }

    private void createDeposits() {

        createDeposit(getBillingManager().getNextPeriodBill().billingAccount().lease().version().leaseProducts().serviceItem());

        for (BillableItem billableItem : getBillingManager().getNextPeriodBill().billingAccount().lease().version().leaseProducts().featureItems()) {
            if (billableItem.isNull()) {
                throw new BillingException("Service Item is mandatory in lease");
            }
            createDeposit(billableItem);
        }
    }

    //Deposit should be taken on a first billing period of the BillableItem
    private void createDeposit(BillableItem billableItem) {

        if (billableItem.deposits().isEmpty()) {
            return;
        }

        InvoiceProductCharge nextCharge = null;
        InvoiceProductCharge currentCharge = null;
        for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(getBillingManager().getNextPeriodBill(), InvoiceProductCharge.class)) {
            if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                    && InvoiceProductCharge.Period.next.equals(charge.period().getValue())) {
                nextCharge = charge;
                break;
            }
        }

        if (getBillingManager().getCurrentPeriodBill() != null) {
            for (InvoiceProductCharge charge : BillingUtils.getLineItemsForType(getBillingManager().getCurrentPeriodBill(), InvoiceProductCharge.class)) {
                if (sameBillableItem(billableItem, charge.chargeSubLineItem().billableItem())
                        && InvoiceProductCharge.Period.next.equals(charge.period().getValue())) {
                    currentCharge = charge;
                    break;
                }
            }
        }

        //This is first time charge have been issued - add deposit
        if (nextCharge != null && currentCharge == null && !billableItem.deposits().isEmpty()) {
            for (Deposit deposit : billableItem.deposits()) {
                InvoiceDeposit invDeposit = EntityFactory.create(InvoiceDeposit.class);
                invDeposit.billingAccount().set(getBillingManager().getNextPeriodBill().billingAccount());
                invDeposit.dueDate().setValue(getBillingManager().getNextPeriodBill().dueDate().getValue());
                invDeposit.debitType().setValue(DebitType.deposit);
                invDeposit.description().setValue(deposit.description().getStringView());

                if (ValueType.amount == deposit.valueType().getValue()) {
                    invDeposit.amount().setValue(deposit.initialAmount().getValue());
                } else if (ValueType.percentage == deposit.valueType().getValue()) {
                    //TODO consider real price of service or feature including concessions etc
                    invDeposit.amount().setValue(
                            deposit.initialAmount().getValue().multiply(billableItem.agreedPrice().getValue()).setScale(2, BigDecimal.ROUND_HALF_UP));
                } else {
                    throw new Error("Unsupported ValueType");
                }

                invDeposit.taxTotal().setValue(BigDecimal.ZERO);

                addDeposit(invDeposit);
            }
        }
    }

    private void addDeposit(InvoiceDeposit deposit) {
        if (deposit == null) {
            return;
        }
        getBillingManager().getNextPeriodBill().depositAmount()
                .setValue(getBillingManager().getNextPeriodBill().depositAmount().getValue().add(deposit.amount().getValue()));
        getBillingManager().getNextPeriodBill().lineItems().add(deposit);
        //TODO
        // getBillingManager().getNextPeriodBill().taxes().setValue(getBillingManager().getNextPeriodBill().taxes().getValue().add(deposit.taxTotal().getValue()));
    }

    private void addDepositRefund(InvoiceDepositRefund depositRefund) {
        getBillingManager().getNextPeriodBill().lineItems().add(depositRefund);
        getBillingManager().getNextPeriodBill().depositRefundAmount()
                .setValue(getBillingManager().getNextPeriodBill().depositRefundAmount().getValue().add(depositRefund.amount().getValue().negate()));
    }

    private void attachDepositRefunds() {
        List<InvoiceLineItem> items = BillingUtils.getUnclaimedLineItems(getBillingManager().getNextPeriodBill().billingAccount());
        for (InvoiceDepositRefund payment : BillingUtils.getLineItemsForType(items, InvoiceDepositRefund.class)) {
            addDepositRefund(payment);
        }
    }

}
