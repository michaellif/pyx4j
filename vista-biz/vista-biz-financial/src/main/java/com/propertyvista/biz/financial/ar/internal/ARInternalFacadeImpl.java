/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar.internal;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.InternalBillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.TransactionHistoryDTO;

public class ARInternalFacadeImpl implements ARFacade {

    @Override
    public void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        ARInternalTransactionManager.getInstance().postInvoiceLineItem(invoiceLineItem);
    }

    @Override
    public boolean validatePayment(PaymentRecord payment) throws ARException {
        return true;
    }

    @Override
    public void postPayment(PaymentRecord paymentRecord) {
        ARInternalPaymentManager.getInstance().postPayment(paymentRecord);
    }

    @Override
    public DebitCreditLink createHardLink(PaymentRecord paymentRecord, InvoiceDebit debit, BigDecimal amount) {
        return ARInternalCreditDebitLinkManager.getInstance().createHardLink(paymentRecord, debit, amount);
    }

    @Override
    public void removeHardLink(DebitCreditLink link) {
        ARInternalCreditDebitLinkManager.getInstance().removeHardLink(link);
    }

    @Override
    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {
        ARInternalPaymentManager.getInstance().rejectPayment(paymentRecord, applyNSF);
    }

    @Override
    public void postImmediateAdjustment(LeaseAdjustment adjustment) {
        ARInternalLeaseAdjustmentManager.getInstance().postImmediateAdjustment(adjustment);
    }

    @Override
    public void postDepositRefund(Deposit deposit) {
        ARInternalDepositManager.getInstance().postDepositRefund(deposit);
    }

    @Override
    public TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount) {
        return ARInternalTransactionManager.getInstance().getTransactionHistory(billingAccount.<InternalBillingAccount> cast());
    }

    @Override
    public List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(BillingAccount billingAccount) {
        return ARInternalTransactionManager.getInstance().getNotCoveredDebitInvoiceLineItems(billingAccount.<InternalBillingAccount> cast());
    }

    @Override
    public List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(BillingAccount billingAccount) {
        return ARInternalTransactionManager.getInstance().getNotConsumedCreditInvoiceLineItems(billingAccount.<InternalBillingAccount> cast());
    }

    @Override
    public BuildingArrearsSnapshot getArrearsSnapshot(Building buildingStub, LogicalDate asOf) {
        return ARInternalArrearsManager.getInstance().retrieveArrearsSnapshot(buildingStub, asOf);
    }

    @Override
    public Collection<AgingBuckets> getAgingBuckets(BillingAccount billingAccount) {
        return ARInternalArrearsManager.getInstance().getAgingBuckets(billingAccount);
    }

    @Override
    public LeaseArrearsSnapshot getArrearsSnapshot(BillingAccount billingAccount, LogicalDate asOf) {
        return ARInternalArrearsManager.getInstance().retrieveArrearsSnapshot(billingAccount, asOf);
    }

    @Override
    public EntitySearchResult<LeaseArrearsSnapshot> getArrearsSnapshotRoster(LogicalDate asOf, List<Building> buildings, Vector<Criterion> searchCriteria,
            Vector<Sort> sortCriteria, int pageNumber, int pageSize) {
        return ARInternalArrearsManager.getInstance().retrieveArrearsSnapshotRoster(asOf, buildings, searchCriteria, sortCriteria, pageNumber, pageSize);
    }

    @Override
    public void updateArrearsHistory(BillingAccount billingAccount) {
        ARInternalArrearsManager.getInstance().updateArrearsHistory(billingAccount);

    }

    @Override
    public void updateArrearsHistory(Building building) {
        ARInternalArrearsManager.getInstance().updateArrearsHistory(building);
    }

    @Override
    public BigDecimal getCurrentBalance(BillingAccount billingAccount) {
        return ARInternalTransactionManager.getInstance().getCurrentBallance(billingAccount.<InternalBillingAccount> cast());
    }

    @Override
    public List<InvoiceLineItem> getLatestBillingActivity(BillingAccount billingAccount) {
        return BillingUtils.getUnclaimedLineItems(billingAccount.<InternalBillingAccount> cast());
    }

    @Override
    public Map<LeaseTermTenant, BigDecimal> getPADBalance(BillingAccount billingAccount, BillingCycle cycle) {
        // TODO Auto-generated method stub
        return null;
    }

}
