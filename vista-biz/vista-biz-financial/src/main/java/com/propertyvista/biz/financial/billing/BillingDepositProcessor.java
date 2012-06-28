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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositStatus;

public class BillingDepositProcessor extends AbstractBillingProcessor {

    private static final I18n i18n = I18n.get(BillingDepositProcessor.class);

    BillingDepositProcessor(AbstractBillingManager billingManager) {
        super(billingManager);
    }

    @Override
    protected void execute() {
        createInvoiceDeposits();
        attachDepositRefunds();
    }

    private void createInvoiceDeposits() {

        EntityQueryCriteria<Deposit> depositCriteria = EntityQueryCriteria.create(Deposit.class);
        depositCriteria.add(PropertyCriterion.eq(depositCriteria.proto().billingAccount(), getBillingManager().getNextPeriodBill().billingAccount()));
        depositCriteria.add(PropertyCriterion.eq(depositCriteria.proto().status(), DepositStatus.Created));

        for (Deposit deposit : Persistence.service().query(depositCriteria)) {
            createInvoiceDeposit(deposit);
        }
    }

    //Deposit should be taken on a first billing period of the BillableItem
    private void createInvoiceDeposit(Deposit deposit) {
        BillableItem billableItem = deposit.billableItem();
        Persistence.service().retrieve(billableItem);
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
        if (nextCharge != null && currentCharge == null) {
            InvoiceDeposit invoiceDeposit = EntityFactory.create(InvoiceDeposit.class);
            invoiceDeposit.billingAccount().set(getBillingManager().getNextPeriodBill().billingAccount());
            invoiceDeposit.dueDate().setValue(getBillingManager().getNextPeriodBill().dueDate().getValue());
            invoiceDeposit.debitType().setValue(DebitType.deposit);
            invoiceDeposit.description().setValue(deposit.description().getStringView());
            invoiceDeposit.amount().setValue(deposit.initialAmount().getValue());
            invoiceDeposit.taxTotal().setValue(BigDecimal.ZERO);
            invoiceDeposit.deposit().set(deposit);

            addInvoiceDeposit(invoiceDeposit);
        }
    }

    private void addInvoiceDeposit(InvoiceDeposit invoiceDeposit) {
        if (invoiceDeposit == null) {
            return;
        }
        getBillingManager().getNextPeriodBill().depositAmount()
                .setValue(getBillingManager().getNextPeriodBill().depositAmount().getValue().add(invoiceDeposit.amount().getValue()));
        getBillingManager().getNextPeriodBill().lineItems().add(invoiceDeposit);
        //TODO
        // getBillingManager().getNextPeriodBill().taxes().setValue(getBillingManager().getNextPeriodBill().taxes().getValue().add(deposit.taxTotal().getValue()));
    }

    private void attachDepositRefunds() {
        List<InvoiceLineItem> items = BillingUtils.getUnclaimedLineItems(getBillingManager().getNextPeriodBill().billingAccount());
        for (InvoiceDepositRefund payment : BillingUtils.getLineItemsForType(items, InvoiceDepositRefund.class)) {
            addDepositRefund(payment);
        }
    }

    private void addDepositRefund(InvoiceDepositRefund depositRefund) {
        getBillingManager().getNextPeriodBill().lineItems().add(depositRefund);
        getBillingManager().getNextPeriodBill().depositRefundAmount()
                .setValue(getBillingManager().getNextPeriodBill().depositRefundAmount().getValue().add(depositRefund.amount().getValue().negate()));
    }
}
