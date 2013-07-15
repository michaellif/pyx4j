/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 15, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.payment.PaymentBatchContext;
import com.propertyvista.domain.financial.ARCode;
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

public interface ARFacade {

    void postInvoiceLineItem(InvoiceLineItem invoiceLineItem);

    void postInvoiceLineItem(InvoiceLineItem invoiceLineItem, BillingCycle billingCycle);

    /**
     * Batch is open, after this call it should be posted or canceled
     * 
     * @param building
     *            for with the batch should be created
     * @throws ARException
     */
    PaymentBatchContext createPaymentBatchContext(Building building) throws ARException;

    /**
     * @param payment
     * @param paymentBatchContext
     *            optional BatchContext
     * @throws ARException
     */
    void postPayment(PaymentRecord payment, PaymentBatchContext paymentBatchContext) throws ARException;

    void rejectPayment(PaymentRecord payment, boolean applyNSF) throws ARException;

    void postImmediateAdjustment(LeaseAdjustment adjustment);

    public void postDepositRefund(Deposit deposit);

    DebitCreditLink createHardLink(PaymentRecord paymentRecord, InvoiceDebit debit, BigDecimal amount);

    void removeHardLink(DebitCreditLink link);

    List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(BillingAccount billingAccount);

    List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(BillingAccount billingAccount);

    TransactionHistoryDTO getTransactionHistory(BillingAccount billingAccount);

    Collection<LeaseAgingBuckets> getAgingBuckets(BillingAccount billingAccount);

    BigDecimal getCurrentBalance(BillingAccount billingAccount);

    BuildingArrearsSnapshot getArrearsSnapshot(Building buildingStub, LogicalDate asOf, boolean secure);

    LeaseArrearsSnapshot getArrearsSnapshot(BillingAccount billingAccount, LogicalDate asOf);

    EntitySearchResult<LeaseArrearsSnapshot> getArrearsSnapshotRoster(LogicalDate asOf, List<Building> buildings, Vector<Criterion> searchCriteria,
            Vector<Sort> sortCriteria, int pageNumber, int pageSize);

    void updateArrearsHistory(BillingAccount billingAccount);

    void updateArrearsHistory(Building building);

    List<InvoiceLineItem> getLatestBillingActivity(BillingAccount billingAccount);

    ARCode getReservedARCode(ARCode.Type type);

    BillingFacade getBillingFacade();

    List<InvoiceProductCharge> estimateLeaseCharges(BillingCycle billingCycle, Lease lease);
}
