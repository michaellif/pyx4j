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
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.InternalBillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.TransactionHistoryDTO;

public class ARFacadeImpl implements ARFacade {

    @Override
    public void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        ARTransactionManager.postInvoiceLineItem(invoiceLineItem);
    }

    @Override
    public void postPayment(PaymentRecord paymentRecord) {
        new ARPaymentProcessor().postPayment(paymentRecord);
    }

    @Override
    public DebitCreditLink createHardLink(PaymentRecord paymentRecord, InvoiceDebit debit, BigDecimal amount) {
        return ARCreditDebitLinkManager.createHardLink(paymentRecord, debit, amount);
    }

    @Override
    public void removeHardLink(DebitCreditLink link) {
        new ARCreditDebitLinkManager().removeHardLink(link);
    }

    @Override
    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {
        new ARPaymentProcessor().rejectPayment(paymentRecord);
        if (applyNSF) {
            new ARNSFProcessor().applyNSFCharge(paymentRecord);
        }
    }

    @Override
    public void postImmediateAdjustment(LeaseAdjustment adjustment) {
        new ARLeaseAdjustmentProcessor().postImmediateAdjustment(adjustment);
    }

    @Override
    public void postDepositRefund(Deposit deposit) {
        new ARDepositProcessor().postDepositRefund(deposit);
    }

    @Override
    public TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount) {
        return ARTransactionManager.getTransactionHistory(billingAccount.<InternalBillingAccount> cast());
    }

    @Override
    public List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(BillingAccount billingAccount) {
        return ARTransactionManager.getNotCoveredDebitInvoiceLineItems(billingAccount.<InternalBillingAccount> cast());
    }

    @Override
    public List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(BillingAccount billingAccount) {
        return ARTransactionManager.getNotConsumedCreditInvoiceLineItems(billingAccount.<InternalBillingAccount> cast());
    }

    @Override
    public BuildingArrearsSnapshot getArrearsSnapshot(Building buildingStub, LogicalDate asOf) {
        return ARArrearsManager.getArrearsSnapshot(buildingStub, asOf);
    }

    @Override
    public EntitySearchResult<LeaseArrearsSnapshot> getArrearsSnapshotRoster(LogicalDate asOf, List<Building> buildings, Vector<Criterion> searchCriteria,
            Vector<Sort> sortCriteria, int pageNumber, int pageSize) {
        return ARArrearsManager.getArrearsSnapshotRoster(asOf, buildings, searchCriteria, sortCriteria, pageNumber, pageSize);
    }

    @Override
    public void updateArrearsHistory(BillingAccount billingAccount) {
        ARArrearsManager.updateArrearsHistory(billingAccount.<InternalBillingAccount> cast());

    }

    @Override
    public void updateArrearsHistory(Building building) {
        ARArrearsManager.updateArrearsHistory(building);
    }

    @Override
    public BigDecimal getCurrentBalance(BillingAccount billingAccount) {
        return ARTransactionManager.getCurrentBallance(billingAccount.<InternalBillingAccount> cast());
    }

    @Override
    public List<InvoiceLineItem> getLatestBillingActivity(BillingAccount billingAccount) {
        return BillingUtils.getUnclaimedLineItems(billingAccount.<InternalBillingAccount> cast());
    }

}
