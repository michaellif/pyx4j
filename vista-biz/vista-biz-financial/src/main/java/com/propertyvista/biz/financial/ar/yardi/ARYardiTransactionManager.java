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
package com.propertyvista.biz.financial.ar.yardi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.ar.ARAbstractTransactionManager;
import com.propertyvista.biz.financial.ar.ARArreasManagerUtils;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.yardi.YardiCharge;
import com.propertyvista.domain.financial.yardi.YardiCredit;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.dto.TransactionHistoryDTO;

class ARYardiTransactionManager extends ARAbstractTransactionManager {

    private ARYardiTransactionManager() {
    }

    private static class SingletonHolder {
        public static final ARYardiTransactionManager INSTANCE = new ARYardiTransactionManager();
    }

    static ARYardiTransactionManager instance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(BillingAccount billingAccount, boolean padItemsOnly) {
        List<InvoiceDebit> debits = new ArrayList<InvoiceDebit>();

        EntityQueryCriteria<YardiCharge> criteria = EntityQueryCriteria.create(YardiCharge.class);
        criteria.eq(criteria.proto().billingAccount(), billingAccount);
        criteria.ge(criteria.proto().outstandingDebit(), BigDecimal.ZERO);
        debits.addAll(Persistence.service().query(criteria));
        return debits;

    }

    @Override
    protected List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(BillingAccount billingAccount) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected BigDecimal getCurrentBallance(BillingAccount billingAccount) {
        return calculateTotal(getYardiCharges(billingAccount), getYardiCredits(billingAccount), getYardiPayments(billingAccount));
    }

    @Override
    protected TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount) {
        TransactionHistoryDTO th = EntityFactory.create(TransactionHistoryDTO.class);
        List<YardiCharge> charges = getYardiCharges(billingAccount);
        List<YardiCredit> credits = getYardiCredits(billingAccount);
        List<YardiPayment> payments = getYardiPayments(billingAccount);
        th.lineItems().addAll(charges);
        th.lineItems().addAll(credits);
        th.lineItems().addAll(payments);
        th.currentBalanceAmount().setValue(calculateTotal(charges, credits, payments));
        th.issueDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        Collection<AgingBuckets> agingBucketsCollection = ServerSideFactory.create(ARFacade.class).getAgingBuckets(billingAccount);
        th.agingBuckets().addAll(agingBucketsCollection);
        th.totalAgingBuckets().set(ARArreasManagerUtils.addInPlace(ARArreasManagerUtils.createAgingBuckets(DebitType.total), agingBucketsCollection));
        return th;
    }

    private List<YardiCharge> getYardiCharges(BillingAccount billingAccount) {
        EntityQueryCriteria<YardiCharge> criteria = EntityQueryCriteria.create(YardiCharge.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().dueDate()));
        criteria.asc(criteria.proto().id());
        return Persistence.service().query(criteria);
    }

    private List<YardiCredit> getYardiCredits(BillingAccount billingAccount) {
        EntityQueryCriteria<YardiCredit> criteria = EntityQueryCriteria.create(YardiCredit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().postDate()));
        criteria.asc(criteria.proto().id());
        return Persistence.service().query(criteria);
    }

    private List<YardiPayment> getYardiPayments(BillingAccount billingAccount) {
        EntityQueryCriteria<YardiPayment> criteria = EntityQueryCriteria.create(YardiPayment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().postDate()));
        criteria.asc(criteria.proto().id());
        return Persistence.service().query(criteria);
    }

    private BigDecimal calculateTotal(List<YardiCharge> charges, List<YardiCredit> credits, List<YardiPayment> payments) {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceLineItem item : charges) {
            total = total.add(item.amount().getValue());
        }
        for (InvoiceLineItem item : credits) {
            total = total.add(item.amount().getValue());
        }
        for (InvoiceLineItem item : payments) {
            total = total.add(item.amount().getValue());
        }
        return total;
    }
}
