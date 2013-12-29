/*    public ARCode getDefaultARCode(Type type) throws ARException {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().type(), type));
        criteria.add(PropertyCriterion.eq(criteria.proto().defaultCode(), Boolean.TRUE));
        List<ARCode> codes = Persistence.service().query(criteria);
        if (codes.size() == 0) {
            throw new ARException("Default ARCode for " + type + " is not found");
        } else if (codes.size() > 1) {
            throw new ARException("More than one default ARCode for " + type + " is found");
        }
        return codes.get(0);
    }


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
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.Type;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.LeaseAgingBuckets;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.TransactionHistoryDTO;

public abstract class ARAbstractTransactionManager {

    protected void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        postInvoiceLineItem(invoiceLineItem, null);
    }

    protected void postInvoiceLineItem(InvoiceLineItem invoiceLineItem, BillingCycle billingCycle) {
        Validate.isTrue(invoiceLineItem.postDate().isNull(), "The LineItem is already posted");

        if (invoiceLineItem.amount().getValue().compareTo(BigDecimal.ZERO) == 0) {
            // ignore line items with 0 amount
            return;
        }

        LogicalDate postDate = new LogicalDate(SystemDateManager.getDate());
        if (billingCycle == null) {
            Persistence.ensureRetrieve(invoiceLineItem.billingAccount(), AttachLevel.Attached);
            Persistence.ensureRetrieve(invoiceLineItem.billingAccount().lease(), AttachLevel.Attached);
            billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(invoiceLineItem.billingAccount().lease(), postDate);
            if (billingCycle == null) {
                billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getLeaseFirstBillingCycle(invoiceLineItem.billingAccount().lease());
            } else {
                billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(billingCycle);
            }
        }

        invoiceLineItem.billingCycle().set(billingCycle);
        invoiceLineItem.postDate().setValue(postDate);

        Persistence.service().persist(invoiceLineItem);
    }

    protected TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount) {
        TransactionHistoryDTO th = EntityFactory.create(TransactionHistoryDTO.class);
        EntityQueryCriteria<InvoiceLineItem> criteria = EntityQueryCriteria.create(InvoiceLineItem.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().postDate()));
        criteria.asc(criteria.proto().id());

        List<InvoiceLineItem> lineItems = Persistence.service().query(criteria);
        th.lineItems().addAll(lineItems);
        th.issueDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        th.currentBalanceAmount().setValue(getCurrentBallance(billingAccount));

        Collection<LeaseAgingBuckets> agingBucketsCollection = ServerSideFactory.create(ARFacade.class).getAgingBuckets(billingAccount);

        th.agingBuckets().addAll(agingBucketsCollection);
        th.totalAgingBuckets().set(
                ARArreasManagerUtils.addInPlace(ARArreasManagerUtils.createAgingBuckets(LeaseAgingBuckets.class, null), agingBucketsCollection));

        return th;
    }

    abstract protected List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(BillingAccount billingAccount);

    abstract protected List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(BillingAccount billingAccount);

    abstract protected BigDecimal getCurrentBallance(BillingAccount billingAccount);

    public LogicalDate getTransactionDueDate(BillingAccount billingAccount, LogicalDate postDate) {
        Persistence.ensureRetrieve(billingAccount.lease(), AttachLevel.Attached);
        BillingCycle currentCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(billingAccount.lease(), postDate);
        BillingCycle targetCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(currentCycle);

        LogicalDate startDate;
        int dueDateOffset;
        Lease lease = Persistence.service().retrieve(Lease.class, billingAccount.lease().getPrimaryKey());
        if (lease.leaseTo().isNull() || targetCycle.billingCycleEndDate().getValue().before(lease.leaseTo().getValue())) {
            // normal cycle
            startDate = targetCycle.billingCycleStartDate().getValue();
            dueDateOffset = billingAccount.paymentDueDayOffset().getValue();
        } else {
            // final cycle
            startDate = lease.leaseTo().getValue();
            dueDateOffset = billingAccount.finalDueDayOffset().getValue();
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, dueDateOffset);
        return new LogicalDate(calendar.getTime());
    }

    public ARCode getDefaultARCode(Type type) {
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().type(), type));
        criteria.add(PropertyCriterion.eq(criteria.proto().reserved(), Boolean.TRUE));
        List<ARCode> codes = Persistence.service().query(criteria);
        if (codes.size() == 0) {
            throw new Error("Default ARCode for " + type + " is not found");
        } else if (codes.size() > 1) {
            throw new Error("More than one default ARCode for " + type + " is found");
        }
        return codes.get(0);
    }

}
