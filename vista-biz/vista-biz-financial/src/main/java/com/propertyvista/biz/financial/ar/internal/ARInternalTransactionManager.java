/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 12, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar.internal;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.ar.ARAbstractTransactionManager;
import com.propertyvista.biz.financial.ar.InvoiceDebitComparator;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoicePaymentBackOut;
import com.propertyvista.dto.TransactionHistoryDTO;

class ARInternalTransactionManager extends ARAbstractTransactionManager {

    private ARInternalTransactionManager() {
    }

    private static class SingletonHolder {
        public static final ARInternalTransactionManager INSTANCE = new ARInternalTransactionManager();
    }

    static ARInternalTransactionManager instance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        postInvoiceLineItem(invoiceLineItem, null);
    }

    @Override
    protected void postInvoiceLineItem(InvoiceLineItem invoiceLineItem, BillingCycle billingCycle) {
        super.postInvoiceLineItem(invoiceLineItem, billingCycle);
        manageCreditDebitLinks(invoiceLineItem);

    }

    private void manageCreditDebitLinks(InvoiceLineItem invoiceLineItem) {
        if (invoiceLineItem.isInstanceOf(InvoicePaymentBackOut.class)) {
            InvoicePaymentBackOut backOut = invoiceLineItem.cast();
            ARInternalCreditDebitLinkManager.instance().declinePayment(backOut);
        } else if (invoiceLineItem.isInstanceOf(InvoiceCredit.class)) {
            InvoiceCredit invoiceCredit = invoiceLineItem.cast();

            invoiceCredit.outstandingCredit().setValue(invoiceCredit.amount().getValue());

            ARInternalCreditDebitLinkManager.instance().consumeCredit(invoiceCredit);

        } else if (invoiceLineItem.isInstanceOf(InvoiceDebit.class)) {
            InvoiceDebit invoiceDebit = invoiceLineItem.cast();

            invoiceDebit.outstandingDebit().setValue(invoiceDebit.amount().getValue());
            invoiceDebit.outstandingDebit().setValue(invoiceDebit.outstandingDebit().getValue().add(invoiceDebit.taxTotal().getValue()));

            ARInternalCreditDebitLinkManager.instance().coverDebit(invoiceDebit);

        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(BillingAccount billingAccount) {
        List<InvoiceDebit> lineItems;
        {
            EntityQueryCriteria<InvoiceDebit> criteria = EntityQueryCriteria.create(InvoiceDebit.class);
            criteria.eq(criteria.proto().billingAccount(), billingAccount);
            criteria.ne(criteria.proto().outstandingDebit(), BigDecimal.ZERO);
            criteria.isNotNull(criteria.proto().postDate());
            lineItems = Persistence.service().query(criteria);
        }

        Collections.sort(lineItems, new InvoiceDebitComparator(billingAccount));

        return lineItems;
    }

    @Override
    protected List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(BillingAccount billingAccount) {
        EntityQueryCriteria<InvoiceCredit> criteria = EntityQueryCriteria.create(InvoiceCredit.class);
        criteria.eq(criteria.proto().billingAccount(), billingAccount);
        criteria.ne(criteria.proto().outstandingCredit(), BigDecimal.ZERO);
        criteria.isNotNull(criteria.proto().postDate());
        criteria.asc(criteria.proto().postDate());
        List<InvoiceCredit> lineItems = Persistence.service().query(criteria);
        return lineItems;
    }

    protected InvoicePayment getCorrespodingCreditByPayment(BillingAccount billingAccount, PaymentRecord paymentRecord) {
        InvoicePayment credit;
        {
            EntityQueryCriteria<InvoicePayment> criteria = EntityQueryCriteria.create(InvoicePayment.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
            criteria.add(PropertyCriterion.eq(criteria.proto().paymentRecord(), paymentRecord));
            credit = Persistence.service().retrieve(criteria);
        }
        return credit;
    }

    protected List<InvoiceCredit> getSuccedingCreditInvoiceLineItems(BillingAccount billingAccount, InvoiceCredit credit) {
        List<InvoiceCredit> lineItems;
        {
            EntityQueryCriteria<InvoiceCredit> criteria = EntityQueryCriteria.create(InvoiceCredit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
            // do not include incoming credit
            criteria.add(PropertyCriterion.ge(criteria.proto().id(), credit.id()));
            criteria.asc(criteria.proto().id());
            lineItems = Persistence.service().query(criteria);
        }

        return lineItems;
    }

    @Override
    protected BigDecimal getCurrentBallance(BillingAccount billingAccount) {
        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestConfirmedBill(billingAccount.lease());
        if (bill == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal currentBallanceAmount = bill.totalDueAmount().getValue();

        LogicalDate now = SystemDateManager.getLogicalDate();
        BillingCycle prevCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(billingAccount.lease(), now);
        if (prevCycle != null) {
            BillingCycle nextCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(prevCycle);
            List<InvoiceLineItem> items = BillingUtils.getUnclaimedLineItems((BillingAccount) billingAccount, nextCycle);

            for (InvoiceLineItem item : items) {
                currentBallanceAmount = currentBallanceAmount.add(BillingUtils.calculateTotal(item));
            }
        }
        return currentBallanceAmount;
    }

    @Override
    protected TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount) {
        return super.getTransactionHistory(billingAccount);
    }

}
