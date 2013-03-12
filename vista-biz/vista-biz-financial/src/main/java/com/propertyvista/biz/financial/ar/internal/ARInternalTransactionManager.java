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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.ar.ARAbstractTransactionManager;
import com.propertyvista.biz.financial.ar.InvoiceDebitComparator;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.InternalBillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoicePaymentBackOut;
import com.propertyvista.domain.policy.policies.ARPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.TransactionHistoryDTO;

class ARInternalTransactionManager extends ARAbstractTransactionManager {

    private ARInternalTransactionManager() {
    }

    private static class SingletonHolder {
        public static final ARInternalTransactionManager INSTANCE = new ARInternalTransactionManager();
    }

    static ARInternalTransactionManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        super.postInvoiceLineItem(invoiceLineItem);
        manageCreditDebitLinks(invoiceLineItem);

    }

    private void manageCreditDebitLinks(InvoiceLineItem invoiceLineItem) {
        if (invoiceLineItem.isInstanceOf(InvoicePaymentBackOut.class)) {
            InvoicePaymentBackOut backOut = invoiceLineItem.cast();
            ARInternalCreditDebitLinkManager.getInstance().declinePayment(backOut);
        } else if (invoiceLineItem.isInstanceOf(InvoiceCredit.class)) {
            InvoiceCredit invoiceCredit = invoiceLineItem.cast();

            invoiceCredit.outstandingCredit().setValue(invoiceCredit.amount().getValue());

            ARInternalCreditDebitLinkManager.getInstance().consumeCredit(invoiceCredit);

        } else if (invoiceLineItem.isInstanceOf(InvoiceDebit.class)) {
            InvoiceDebit invoiceDebit = invoiceLineItem.cast();

            invoiceDebit.outstandingDebit().setValue(invoiceDebit.amount().getValue());
            invoiceDebit.outstandingDebit().setValue(invoiceDebit.outstandingDebit().getValue().add(invoiceDebit.taxTotal().getValue()));

            ARInternalCreditDebitLinkManager.getInstance().coverDebit(invoiceDebit);

        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(InternalBillingAccount billingAccount) {
        List<InvoiceDebit> lineItems;
        {
            EntityQueryCriteria<InvoiceDebit> criteria = EntityQueryCriteria.create(InvoiceDebit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
            criteria.add(PropertyCriterion.ne(criteria.proto().outstandingDebit(), BigDecimal.ZERO));
            criteria.add(PropertyCriterion.isNotNull(criteria.proto().postDate()));
            lineItems = Persistence.service().query(criteria);
        }

        //Find building that billingAccount belongs to
        Building building;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().units().$()._Leases().$().billingAccount(), billingAccount));
            building = Persistence.service().retrieve(criteria);
        }

        // make a sorting in required mode. ConsumeCreditPolicy should be set on Application level.
        ARPolicy arPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, ARPolicy.class);
        Collections.sort(lineItems, new InvoiceDebitComparator(arPolicy));

        return lineItems;
    }

    @Override
    protected List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(InternalBillingAccount billingAccount) {
        EntityQueryCriteria<InvoiceCredit> criteria = EntityQueryCriteria.create(InvoiceCredit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.ne(criteria.proto().outstandingCredit(), BigDecimal.ZERO));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().postDate()));
        criteria.asc(criteria.proto().postDate());
        List<InvoiceCredit> lineItems = Persistence.service().query(criteria);
        return lineItems;
    }

    protected InvoicePayment getCorrespodingCreditByPayment(InternalBillingAccount billingAccount, PaymentRecord paymentRecord) {
        InvoicePayment credit;
        {
            EntityQueryCriteria<InvoicePayment> criteria = EntityQueryCriteria.create(InvoicePayment.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
            criteria.add(PropertyCriterion.eq(criteria.proto().paymentRecord(), paymentRecord));
            credit = Persistence.service().retrieve(criteria);
        }
        return credit;
    }

    protected List<InvoiceCredit> getSuccedingCreditInvoiceLineItems(InternalBillingAccount billingAccount, InvoiceCredit credit) {
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
    protected BigDecimal getCurrentBallance(InternalBillingAccount billingAccount) {
        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestConfirmedBill(billingAccount.lease());
        if (bill == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal currentBallanceAmount = bill.totalDueAmount().getValue();
        List<InvoiceLineItem> items = BillingUtils.getUnclaimedLineItems(billingAccount);

        for (InvoiceLineItem item : items) {
            currentBallanceAmount = currentBallanceAmount.add(BillingUtils.calculateTotal(item));
        }
        return currentBallanceAmount;
    }

    @Override
    protected TransactionHistoryDTO getTransactionHistory(InternalBillingAccount billingAccount) {
        return super.getTransactionHistory(billingAccount);
    }

}
