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
package com.propertyvista.biz.financial.billing.yardi;

import java.math.BigDecimal;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public final class BillingYardiFacadeImpl implements BillingFacade {

    public BillingYardiFacadeImpl() {

    }

    @Override
    public BigDecimal getMaxLeaseTermMonthlyTotal(LeaseTerm leaseTerm) {
        return BillingUtils.getMaxLeaseTermMonthlyTotal(leaseTerm);
    }

    @Override
    public Bill runBilling(Lease leaseId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bill runBilling(Lease leaseId, BillingCycle cycle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bill runBillingPreview(Lease leaseId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bill getBill(Lease lease, int billSequenceNumber) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bill getLatestConfirmedBill(Lease lease) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bill getLatestBill(Lease lease) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLatestBill(Bill bill) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bill confirmBill(Bill billStub) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bill rejectBill(Bill billStub, String reason) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BillingCycle getNextBillBillingCycle(Lease lease) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateLeaseAdjustmentTax(LeaseAdjustment adjustment) {
        throw new UnsupportedOperationException();
    }

}
