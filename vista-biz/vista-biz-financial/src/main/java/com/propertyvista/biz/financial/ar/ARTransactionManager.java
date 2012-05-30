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
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.policy.policies.ARPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.TransactionHistoryDTO;

public class ARTransactionManager {

    static void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        if (!invoiceLineItem.postDate().isNull()) {
            throw new ARException("The LineItem is already posted");
        }

        if (invoiceLineItem.amount().getValue().equals(new BigDecimal("0.00"))) {
            throw new ARException("The LineItem has 0 value");
        }

        invoiceLineItem.postDate().setValue(new LogicalDate(SysDateManager.getSysDate()));
        createCreditDebitLinks(invoiceLineItem);
    }

    private static void createCreditDebitLinks(InvoiceLineItem invoiceLineItem) {
        if (invoiceLineItem.isInstanceOf(InvoiceCredit.class)) {
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
            criteria.add(PropertyCriterion.ne(criteria.proto().outstandingDebit(), new BigDecimal("0.00")));
            criteria.add(PropertyCriterion.isNotNull(criteria.proto().postDate()));
            criteria.asc(criteria.proto().postDate());
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
        criteria.add(PropertyCriterion.ne(criteria.proto().outstandingCredit(), new BigDecimal("0.00")));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().postDate()));
        criteria.asc(criteria.proto().postDate());
        List<InvoiceCredit> lineItems = Persistence.service().query(criteria);
        return lineItems;
    }

    static BigDecimal getCurrentBallance(BillingAccount billingAccount) {
        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestConfirmedBill(billingAccount.lease());
        if (bill == null) {
            return new BigDecimal("0.00");
        }

        BigDecimal currentBallanceAmount = bill.totalDueAmount().getValue();
        List<InvoiceLineItem> items = BillingUtils.getNotConsumedLineItems(billingAccount);

        for (InvoiceLineItem item : items) {
            currentBallanceAmount = currentBallanceAmount.add(BillingUtils.calculateTotal(item));
        }
        return currentBallanceAmount;
    }

}
