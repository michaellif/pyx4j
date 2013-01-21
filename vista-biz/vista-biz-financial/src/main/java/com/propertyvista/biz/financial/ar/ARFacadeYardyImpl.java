/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-18
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.financial.yardi.YardiPaymentReversal;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.TransactionHistoryDTO;

public class ARFacadeYardyImpl implements ARFacade {
    private static final I18n i18n = I18n.get(ARFacadeYardyImpl.class);

    @Override
    public void postPayment(PaymentRecord paymentRecord) {
        YardiPayment payment = EntityFactory.create(YardiPayment.class);
        payment.paymentRecord().set(paymentRecord);
        payment.amount().setValue(paymentRecord.amount().getValue().negate());
        payment.billingAccount().set(paymentRecord.billingAccount());
        payment.description().setValue(i18n.tr("Payment Received - Thank You"));
        payment.claimed().setValue(false);
        payment.postDate().setValue(new LogicalDate(SysDateManager.getSysDate()));

        Persistence.service().persist(payment);
    }

    @Override
    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {
        YardiPaymentReversal reversal = EntityFactory.create(YardiPaymentReversal.class);
        reversal.paymentRecord().set(paymentRecord);
        reversal.amount().setValue(paymentRecord.amount().getValue());
        reversal.billingAccount().set(paymentRecord.billingAccount());
        reversal.description().setValue(i18n.tr("Payment from ''{0}'' was rejected", paymentRecord.createdDate().getValue().toString()));
        reversal.taxTotal().setValue(BigDecimal.ZERO);
        reversal.claimed().setValue(false);
        reversal.postDate().setValue(new LogicalDate(SysDateManager.getSysDate()));
        // TODO - use applyNSF...

        Persistence.service().persist(reversal);
    }

    @Override
    public BigDecimal getCurrentBalance(BillingAccount billingAccount) {
        return calculateTotal(getCurrentTransactions(billingAccount));
    }

    @Override
    public TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount) {
        TransactionHistoryDTO th = EntityFactory.create(TransactionHistoryDTO.class);
        th.lineItems().addAll(getCurrentTransactions(billingAccount));
        th.currentBalanceAmount().setValue(calculateTotal(th.lineItems()));
        th.issueDate().setValue(new LogicalDate(SysDateManager.getSysDate()));

        return th;
    }

    private List<InvoiceLineItem> getCurrentTransactions(BillingAccount billingAccount) {
        EntityQueryCriteria<InvoiceLineItem> criteria = EntityQueryCriteria.create(InvoiceLineItem.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().postDate()));
        criteria.asc(criteria.proto().id());
        return Persistence.service().query(criteria);
    }

    private BigDecimal calculateTotal(List<InvoiceLineItem> lineItems) {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceLineItem item : lineItems) {
            total = total.add(item.amount().getValue());
        }
        return total;
    }

    @Override
    public void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postImmediateAdjustment(LeaseAdjustment adjustment) {
        throw new UnsupportedOperationException();

    }

    @Override
    public DebitCreditLink createHardLink(PaymentRecord paymentRecord, InvoiceDebit debit, BigDecimal amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeHardLink(DebitCreditLink link) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void postDepositRefund(Deposit deposit) {
        throw new UnsupportedOperationException();

    }

    @Override
    public List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(BillingAccount billingAccount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(BillingAccount billingAccount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BuildingArrearsSnapshot getArrearsSnapshot(Building buildingStub, LogicalDate asOf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntitySearchResult<LeaseArrearsSnapshot> getArrearsSnapshotRoster(LogicalDate asOf, List<Building> buildings, Vector<Criterion> searchCriteria,
            Vector<Sort> sortCriteria, int pageNumber, int pageSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateArrearsHistory(BillingAccount billingAccount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateArrearsHistory(Building building) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<InvoiceLineItem> getNotAcquiredLineItems(BillingAccount billingAccount) {
        throw new UnsupportedOperationException();
    }

}
