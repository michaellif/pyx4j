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
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.TransactionHistoryDTO;

public class ARTransactionManager {

    static void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        if (!invoiceLineItem.postDate().isNull()) {
            return;
        }
        if (invoiceLineItem.isAssignableFrom(InvoiceCredit.class)) {
            ARCreditDebitLinkManager.consumeCredit((InvoiceCredit) invoiceLineItem);
        } else if (invoiceLineItem.isAssignableFrom(InvoiceDebit.class)) {
            ARCreditDebitLinkManager.coverDebit((InvoiceDebit) invoiceLineItem);
        }

        invoiceLineItem.postDate().setValue(new LogicalDate(SysDateManager.getSysDate()));
        Persistence.service().persist(invoiceLineItem);

    }

    static TransactionHistoryDTO getTransactionHistory(Lease lease) {
        return getTransactionHistory(lease, null);
    }

    static TransactionHistoryDTO getTransactionHistory(Lease lease, LogicalDate fromDate) {
        TransactionHistoryDTO th = EntityFactory.create(TransactionHistoryDTO.class);
        EntityQueryCriteria<InvoiceLineItem> criteria = EntityQueryCriteria.create(InvoiceLineItem.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), lease.billingAccount()));
        if (fromDate != null) {
            criteria.add(PropertyCriterion.ge(criteria.proto().postDate(), fromDate));
        }
        List<InvoiceLineItem> lineItems = Persistence.service().query(criteria);
        th.lineItems().addAll(lineItems);
        return th;
    }

    static List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(Lease lease) {
        EntityQueryCriteria<InvoiceDebit> criteria = EntityQueryCriteria.create(InvoiceDebit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), lease.billingAccount()));
        criteria.add(PropertyCriterion.ne(criteria.proto().outstandingDebit(), new BigDecimal("0.00")));
        List<InvoiceDebit> lineItems = Persistence.service().query(criteria);
        return lineItems;
    }

    static List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(Lease lease) {
        EntityQueryCriteria<InvoiceCredit> criteria = EntityQueryCriteria.create(InvoiceCredit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), lease.billingAccount()));
        criteria.add(PropertyCriterion.ne(criteria.proto().outstandingCredit(), new BigDecimal("0.00")));
        List<InvoiceCredit> lineItems = Persistence.service().query(criteria);
        return lineItems;
    }

}
