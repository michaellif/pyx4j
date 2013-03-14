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
package com.propertyvista.biz.financial.ar.yardi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.TransactionHistoryDTO;

public class ARYardiFacadeImpl implements ARFacade {

    @Override
    public boolean validatePayment(PaymentRecord paymentRecord) throws ARException {
        return ARYardiPaymentManager.getInstance().validatePayment(paymentRecord);
    }

    @Override
    public void postPayment(PaymentRecord paymentRecord) throws ARException {
        ARYardiPaymentManager.getInstance().postPayment(paymentRecord);
    }

    @Override
    public void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) throws ARException {
        ARYardiPaymentManager.getInstance().rejectPayment(paymentRecord, applyNSF);
    }

    @Override
    public BigDecimal getCurrentBalance(BillingAccount billingAccount) {
        return ARYardiTransactionManager.getInstance().getCurrentBallance(billingAccount);
    }

    @Override
    public TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount) {
        return ARYardiTransactionManager.getInstance().getTransactionHistory(billingAccount);
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
        return ARYardiTransactionManager.getInstance().getNotCoveredDebitInvoiceLineItems(billingAccount);
    }

    @Override
    public List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(BillingAccount billingAccount) {
        return ARYardiTransactionManager.getInstance().getNotConsumedCreditInvoiceLineItems(billingAccount);
    }

    @Override
    public List<InvoiceLineItem> getLatestBillingActivity(BillingAccount billingAccount) {
        // get latest payments (for the last 3 months)
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        LogicalDate dateFrom = new LogicalDate(cal.getTime());
        EntityQueryCriteria<InvoicePayment> criteria = EntityQueryCriteria.create(InvoicePayment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.gt(criteria.proto().postDate(), dateFrom));
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().paymentRecord()));
        return new ArrayList<InvoiceLineItem>(Persistence.service().query(criteria));
    }

    @Override
    public BigDecimal getPADBalance(BillingAccount billingAccount, BillingCycle cycle) {
        return ARYardiTransactionManager.getInstance().getPADBalance(billingAccount, cycle);
    }

    @Override
    public void updateArrearsHistory(BillingAccount billingAccount) {
        ARYardiArrearsManager.getInstance().updateArrearsHistory(billingAccount);
    }

    @Override
    public void updateArrearsHistory(Building building) {
        ARYardiArrearsManager.getInstance().updateArrearsHistory(building);
    }

    @Override
    public Collection<AgingBuckets> getAgingBuckets(BillingAccount billingAccount) {
        return ARYardiArrearsManager.getInstance().getAgingBuckets(billingAccount);
    }

    @Override
    public LeaseArrearsSnapshot getArrearsSnapshot(BillingAccount billingAccount, LogicalDate asOf) {
        return ARYardiArrearsManager.getInstance().retrieveArrearsSnapshot(billingAccount, asOf);
    }

    @Override
    public BuildingArrearsSnapshot getArrearsSnapshot(Building buildingStub, LogicalDate asOf) {
        return ARYardiArrearsManager.getInstance().retrieveArrearsSnapshot(buildingStub, asOf);
    }

    @Override
    public EntitySearchResult<LeaseArrearsSnapshot> getArrearsSnapshotRoster(LogicalDate asOf, List<Building> buildings, Vector<Criterion> searchCriteria,
            Vector<Sort> sortCriteria, int pageNumber, int pageSize) {
        return ARYardiArrearsManager.getInstance().retrieveArrearsSnapshotRoster(asOf, buildings, searchCriteria, sortCriteria, pageNumber, pageSize);
    }

}
