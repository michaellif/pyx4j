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
package com.propertyvista.biz.financial.billing.internal;

import java.math.BigDecimal;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public final class BillingInternalFacadeImpl implements BillingFacade {

    public BillingInternalFacadeImpl() {

    }

    @Override
    public Bill runBilling(Lease leaseId) {
        return BillingManager.instance().runBilling(leaseId, false);
    }

    @Override
    public Bill runBilling(Lease leaseId, BillingCycle cycle) {
        return BillingManager.instance().runBilling(leaseId, cycle, false);
    }

    @Override
    public Bill runBillingPreview(Lease leaseId) {
        return BillingManager.instance().runBilling(leaseId, true);
    }

    @Override
    public Bill getBill(Lease lease, int billSequenceNumber) {
        return BillingManager.instance().getBill(lease, billSequenceNumber);
    }

    @Override
    public Bill getLatestConfirmedBill(Lease lease) {
        return BillingManager.instance().getLatestConfirmedBill(lease);
    }

    @Override
    public Bill getLatestBill(Lease lease) {
        return BillingManager.instance().getLatestBill(lease);
    }

    @Override
    public boolean isLatestBill(Bill bill) {
        return BillingManager.instance().isLatestBill(bill);
    }

    @Override
    public Bill confirmBill(Bill bill) {
        return BillingManager.instance().confirmBill(bill);
    }

    @Override
    public Bill rejectBill(Bill bill, String reason) {
        return BillingManager.instance().rejectBill(bill, reason);
    }

    @Override
    public BillingCycle getNextBillBillingCycle(Lease lease) {
        return BillingManager.instance().getNextBillBillingCycle(lease);
    }

    @Override
    public void updateLeaseAdjustmentTax(LeaseAdjustment adjustment) {
        BillingManager.instance().updateLeaseAdjustmentTax(adjustment);
    }

    @Override
    public BigDecimal getMaxLeaseTermMonthlyTotal(LeaseTerm leaseTerm) {
        return BillingUtils.getMaxLeaseTermMonthlyTotal(leaseTerm);
    }

    @Override
    public BigDecimal getActualPrice(BillableItem billableItem) {
        return BillingUtils.getActualPrice(billableItem);
    }

}
