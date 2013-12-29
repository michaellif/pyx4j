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
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.AbstractBillingProcessor;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.financial.deposit.DepositFacade.ProductTerm;
import com.propertyvista.domain.financial.ARCode.Type;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillType;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Deposit.DepositType;
import com.propertyvista.domain.tenant.lease.DepositLifecycle.DepositStatus;

public class BillingDepositProcessor extends AbstractBillingProcessor<InternalBillProducer> {

    BillingDepositProcessor(InternalBillProducer billProducer) {
        super(billProducer);
    }

    @Override
    public void execute() {
        // TODO: Misha/Stas review please: do not calculate charges for null-duration billing period: 
        if (!getBillProducer().getNextPeriodBill().billingPeriodStartDate().isNull()) {
            createInvoiceDeposits();
        }
        createDepositRefunds();
        attachDepositRefunds();
    }

    private void createInvoiceDeposits() {
        createInvoiceDeposit(getBillProducer().getNextPeriodBill().billingAccount().lease().currentTerm().version().leaseProducts().serviceItem());
        for (BillableItem billableItem : getBillProducer().getNextPeriodBill().billingAccount().lease().currentTerm().version().leaseProducts().featureItems()) {
            createInvoiceDeposit(billableItem);
        }
    }

    //Deposit should be taken on a first billing period of the BillableItem
    private void createInvoiceDeposit(BillableItem billableItem) {
        //This is first time charge have been issued - add deposit
        for (Deposit deposit : billableItem.deposits()) {
            LogicalDate effectiveDate = billableItem.effectiveDate().getValue();
            if (!deposit.isProcessed().isBooleanTrue()
                    && (effectiveDate == null || !effectiveDate.after(getBillProducer().getNextPeriodBill().billingPeriodEndDate().getValue()))) {
                InvoiceDeposit invoiceDeposit = EntityFactory.create(InvoiceDeposit.class);
                invoiceDeposit.billingAccount().set(getBillProducer().getNextPeriodBill().billingAccount());
                invoiceDeposit.dueDate().setValue(getBillProducer().getNextPeriodBill().dueDate().getValue());
                invoiceDeposit.arCode().set(ServerSideFactory.create(ARFacade.class).getReservedARCode(Type.Deposit));
                invoiceDeposit.description().setValue(deposit.description().getStringView());
                invoiceDeposit.amount().setValue(deposit.amount().getValue());
                invoiceDeposit.taxTotal().setValue(BigDecimal.ZERO);
                invoiceDeposit.deposit().set(deposit);
                addInvoiceDeposit(invoiceDeposit);
            }
        }
    }

    private void addInvoiceDeposit(InvoiceDeposit invoiceDeposit) {
        if (invoiceDeposit == null) {
            return;
        }
        getBillProducer().getNextPeriodBill().depositAmount()
                .setValue(getBillProducer().getNextPeriodBill().depositAmount().getValue().add(invoiceDeposit.amount().getValue()));
        getBillProducer().getNextPeriodBill().lineItems().add(invoiceDeposit);
        //TODO
        // getBillingManager().getNextPeriodBill().taxes().setValue(getBillingManager().getNextPeriodBill().taxes().getValue().add(deposit.taxTotal().getValue()));
    }

    private void createDepositRefunds() {
        // LastMonthDeposit - if this is the last month bill, or final bill, post the refund
        Bill nextBill = getBillProducer().getNextPeriodBill();
        if (nextBill.billType().getValue().equals(BillType.Final)
                || !nextBill.billingCycle().billingCycleEndDate().getValue().before(nextBill.billingAccount().lease().currentTerm().termTo().getValue())) {
            Persistence.ensureRetrieve(nextBill.billingAccount().deposits(), AttachLevel.Attached);

            Map<Deposit, ProductTerm> deposits = ServerSideFactory.create(DepositFacade.class).getCurrentDeposits(nextBill.billingAccount().lease());
            for (Deposit deposit : deposits.keySet()) {
                if (DepositType.LastMonthDeposit.equals(deposit.type().getValue()) && DepositStatus.Paid.equals(deposit.lifecycle().status().getValue())) {
                    ServerSideFactory.create(ARFacade.class).postDepositRefund(deposit);
                }
            }
        }
    }

    private void attachDepositRefunds() {
        Bill bill = getBillProducer().getNextPeriodBill();
        List<InvoiceLineItem> items = BillingUtils.getUnclaimedLineItems(bill.billingAccount(), bill.billingCycle());
        for (InvoiceDepositRefund payment : BillingUtils.getLineItemsForType(items, InvoiceDepositRefund.class)) {
            addDepositRefund(payment);
        }
    }

    private void addDepositRefund(InvoiceDepositRefund depositRefund) {
        Bill bill = getBillProducer().getNextPeriodBill();
        bill.lineItems().add(depositRefund);
        bill.depositRefundAmount().setValue(bill.depositRefundAmount().getValue().add(depositRefund.amount().getValue()));
    }
}
