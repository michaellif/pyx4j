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
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.ar.ARArrearsManager;
import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.financial.billing.LeaseProductsPriceEstimator;
import com.propertyvista.biz.financial.billing.internal.BillingInternalFacadeImpl;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.Type;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.LeaseAgingBuckets;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.TransactionHistoryDTO;

public class ARInternalFacadeImpl implements ARFacade {

    @Override
    public void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        postInvoiceLineItem(invoiceLineItem, null);
    }

    @Override
    public void postInvoiceLineItem(InvoiceLineItem invoiceLineItem, BillingCycle billingCycle) {
        ARInternalTransactionManager.instance().postInvoiceLineItem(invoiceLineItem, billingCycle);
    }

    @Override
    public boolean validatePayment(PaymentRecord payment) throws ARException {
        return true;
    }

    @Override
    public void postPayment(PaymentRecord paymentRecord) {
        ARInternalPaymentManager.instance().postPayment(paymentRecord);
    }

    @Override
    public DebitCreditLink createHardLink(PaymentRecord paymentRecord, InvoiceDebit debit, BigDecimal amount) {
        return ARInternalCreditDebitLinkManager.instance().createHardLink(paymentRecord, debit, amount);
    }

    @Override
    public void removeHardLink(DebitCreditLink link) {
        ARInternalCreditDebitLinkManager.instance().removeHardLink(link);
    }

    @Override
    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {
        ARInternalPaymentManager.instance().rejectPayment(paymentRecord, applyNSF);
        ServerSideFactory.create(NotificationFacade.class).rejectPayment(paymentRecord, applyNSF);
    }

    @Override
    public void postImmediateAdjustment(LeaseAdjustment adjustment) {
        ARInternalLeaseAdjustmentManager.instance().postImmediateAdjustment(adjustment);
    }

    @Override
    public void postDepositRefund(Deposit deposit) {
        ARInternalDepositManager.instance().postDepositRefund(deposit);
    }

    @Override
    public TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount) {
        return ARInternalTransactionManager.instance().getTransactionHistory(billingAccount);
    }

    @Override
    public List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(BillingAccount billingAccount) {
        return ARInternalTransactionManager.instance().getNotCoveredDebitInvoiceLineItems(billingAccount);
    }

    @Override
    public List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(BillingAccount billingAccount) {
        return ARInternalTransactionManager.instance().getNotConsumedCreditInvoiceLineItems(billingAccount);
    }

    @Override
    public BuildingArrearsSnapshot getArrearsSnapshot(Building buildingStub, LogicalDate asOf) {
        return ARArrearsManager.instance().retrieveArrearsSnapshot(buildingStub, asOf);
    }

    @Override
    public Collection<LeaseAgingBuckets> getAgingBuckets(BillingAccount billingAccount) {
        return ARArrearsManager.instance().getAgingBuckets(billingAccount);
    }

    @Override
    public LeaseArrearsSnapshot getArrearsSnapshot(BillingAccount billingAccount, LogicalDate asOf) {
        return ARArrearsManager.instance().retrieveArrearsSnapshot(billingAccount, asOf);
    }

    @Override
    public EntitySearchResult<LeaseArrearsSnapshot> getArrearsSnapshotRoster(LogicalDate asOf, List<Building> buildings, Vector<Criterion> searchCriteria,
            Vector<Sort> sortCriteria, int pageNumber, int pageSize) {
        return ARArrearsManager.instance().retrieveArrearsSnapshotRoster(asOf, buildings, searchCriteria, sortCriteria, pageNumber, pageSize);
    }

    @Override
    public void updateArrearsHistory(BillingAccount billingAccount) {
        ARArrearsManager.instance().updateArrearsHistory(billingAccount);

    }

    @Override
    public void updateArrearsHistory(Building building) {
        ARArrearsManager.instance().updateArrearsHistory(building);
    }

    @Override
    public BigDecimal getCurrentBalance(BillingAccount billingAccount) {
        return ARInternalTransactionManager.instance().getCurrentBallance(billingAccount);
    }

    @Override
    public List<InvoiceLineItem> getLatestBillingActivity(BillingAccount billingAccount) {
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        BillingCycle prevCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(billingAccount.lease(), now);
        BillingCycle nextCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(prevCycle);
        return BillingUtils.getUnclaimedLineItems(billingAccount, nextCycle);
    }

    @Override
    public ARCode getReservedARCode(Type type) {
        return ARInternalTransactionManager.instance().getDefaultARCode(type);
    }

    @Override
    public BillingFacade getBillingFacade() {
        return BillingInternalFacadeImpl.instance();
    }

    @Override
    public List<InvoiceProductCharge> estimateLeaseCharges(BillingCycle billingCycle, Lease lease) {
        return new LeaseProductsPriceEstimator(billingCycle, lease).calculateCharges();
    }

}
