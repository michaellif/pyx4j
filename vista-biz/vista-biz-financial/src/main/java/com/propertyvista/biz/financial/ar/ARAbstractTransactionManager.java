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
import java.util.List;

import org.apache.commons.lang.Validate;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.InternalBillingAccount;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.dto.TransactionHistoryDTO;

public abstract class ARAbstractTransactionManager {

    protected void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        Validate.isTrue(invoiceLineItem.postDate().isNull(), "The LineItem is already posted");

        if (invoiceLineItem.amount().getValue().compareTo(BigDecimal.ZERO) == 0) {
            // ignore line items with 0 amount
            return;
        }

        invoiceLineItem.postDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.service().persist(invoiceLineItem);
    }

    protected TransactionHistoryDTO getTransactionHistory(InternalBillingAccount billingAccount) {
        return getTransactionHistory(billingAccount, null);
    }

    TransactionHistoryDTO getTransactionHistory(InternalBillingAccount billingAccount, LogicalDate fromDate) {
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
        th.issueDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        th.currentBalanceAmount().setValue(getCurrentBallance(billingAccount));

        Collection<AgingBuckets> agingBucketsCollection = ServerSideFactory.create(ARFacade.class).getAgingBuckets(billingAccount);

        th.agingBuckets().addAll(agingBucketsCollection);
        th.totalAgingBuckets().set(ARArrearsManager.addInPlace(ARArrearsManager.createAgingBuckets(DebitType.total), agingBucketsCollection));

        return th;
    }

    abstract protected List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(InternalBillingAccount billingAccount);

    abstract protected List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(InternalBillingAccount billingAccount);

    abstract protected BigDecimal getCurrentBallance(InternalBillingAccount billingAccount);

}
