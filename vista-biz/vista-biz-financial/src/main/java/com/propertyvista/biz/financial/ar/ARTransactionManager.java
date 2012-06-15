/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 22, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoicePaymentBackOut;
import com.propertyvista.domain.policy.policies.ARPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.TransactionHistoryDTO;

public class ARTransactionManager {

    static void processBackOutPayment(InvoicePaymentBackOut backOut) {

        ARCreditDebitLinkManager.declinePayment(backOut);
    }

    static void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        if (!invoiceLineItem.postDate().isNull()) {
            throw new ARException("The LineItem is already posted");
        }

        if (invoiceLineItem.amount().getValue().compareTo(BigDecimal.ZERO) == 0) {
            throw new ARException("The LineItem has 0 value");
        }

        invoiceLineItem.postDate().setValue(new LogicalDate(SysDateManager.getSysDate()));

        manageCreditDebitLinks(invoiceLineItem);

        Persistence.service().persist(invoiceLineItem);
    }

    private static void manageCreditDebitLinks(InvoiceLineItem invoiceLineItem) {
        if (invoiceLineItem.isInstanceOf(InvoicePaymentBackOut.class)) {
            InvoicePaymentBackOut backout = invoiceLineItem.cast();
            processBackOutPayment(backout);
        } else if (invoiceLineItem.isInstanceOf(InvoiceCredit.class)) {
            InvoiceCredit invoiceCredit = invoiceLineItem.cast();

            invoiceCredit.outstandingCredit().setValue(invoiceCredit.amount().getValue());

            ARCreditDebitLinkManager.consumeCredit(invoiceCredit);

        } else if (invoiceLineItem.isInstanceOf(InvoiceDebit.class)) {
            InvoiceDebit invoiceDebit = invoiceLineItem.cast();

            invoiceDebit.outstandingDebit().setValue(invoiceDebit.amount().getValue());
            invoiceDebit.outstandingDebit().setValue(invoiceDebit.outstandingDebit().getValue().add(invoiceDebit.taxTotal().getValue()));

            ARCreditDebitLinkManager.coverDebit(invoiceDebit);

        } else {
            throw new IllegalArgumentException();
        }
    }

    static TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount) {
        return getTransactionHistory(billingAccount, null);
    }

    static TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount, LogicalDate fromDate) {
        TransactionHistoryDTO th = EntityFactory.create(TransactionHistoryDTO.class);
        EntityQueryCriteria<InvoiceLineItem> criteria = EntityQueryCriteria.create(InvoiceLineItem.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().postDate()));
        if (fromDate != null) {
            criteria.add(PropertyCriterion.ge(criteria.proto().postDate(), fromDate));
        }
        criteria.asc(criteria.proto().id());

        List<InvoiceLineItem> lineItems = Persistence.service().query(criteria);
        th.lineItems().addAll(lineItems);
        th.fromDate().setValue(fromDate);
        th.issueDate().setValue(new LogicalDate(SysDateManager.getSysDate()));
        th.currentBalanceAmount().setValue(getCurrentBallance(billingAccount));

        Collection<AgingBuckets> agingBucketsCollection = ARArrearsManager.getAgingBuckets(billingAccount);

        th.agingBuckets().addAll(agingBucketsCollection);

        th.totalAgingBuckets().set(ARArrearsManager.calculateTotalAgingBuckets(agingBucketsCollection));

        return th;
    }

    static List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(BillingAccount billingAccount) {
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
            criteria.add(PropertyCriterion.eq(criteria.proto()._Units().$()._Leases().$().billingAccount(), billingAccount));
            building = Persistence.service().retrieve(criteria);
        }

        // make a sorting in required mode. ConsumeCreditPolicy should be set on Application level.
        ARPolicy arPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, ARPolicy.class);
        Collections.sort(lineItems, new InvoiceDebitComparator(arPolicy));

        return lineItems;
    }

    static List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(BillingAccount billingAccount) {
        EntityQueryCriteria<InvoiceCredit> criteria = EntityQueryCriteria.create(InvoiceCredit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.ne(criteria.proto().outstandingCredit(), BigDecimal.ZERO));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().postDate()));
        criteria.asc(criteria.proto().postDate());
        List<InvoiceCredit> lineItems = Persistence.service().query(criteria);
        return lineItems;
    }

    static BigDecimal getCurrentBallance(BillingAccount billingAccount) {
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

    static InvoicePayment getCorrespodingCreditByPayment(BillingAccount billingAccount, PaymentRecord paymentRecord) {
        InvoicePayment credit;
        {
            EntityQueryCriteria<InvoicePayment> criteria = EntityQueryCriteria.create(InvoicePayment.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
            criteria.add(PropertyCriterion.eq(criteria.proto().paymentRecord(), paymentRecord));
            credit = Persistence.service().retrieve(criteria);
        }
        return credit;
    }

    static List<InvoiceCredit> getSuccedingCreditInvoiceLineItems(BillingAccount billingAccount, InvoiceCredit credit) {
        List<InvoiceCredit> lineItems;
        {
            EntityQueryCriteria<InvoiceCredit> criteria = EntityQueryCriteria.create(InvoiceCredit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
            // do not include incoming credit
            criteria.add(PropertyCriterion.gt(criteria.proto().id(), credit.id()));
            criteria.asc(criteria.proto().id());
            lineItems = Persistence.service().query(criteria);
        }

        return lineItems;
    }
}
