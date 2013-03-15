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
package com.propertyvista.biz.financial.billing;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public interface BillingFacade {

    Bill runBilling(Lease leaseId);

    Bill runBilling(Lease leaseId, BillingCycle cycle);

    Bill runBillingPreview(Lease leaseId);

    Bill getBill(Lease lease, int billSequenceNumber);

    Bill getLatestConfirmedBill(Lease lease);

    Bill getLatestBill(Lease lease);

    boolean isLatestBill(Bill bill);

    Bill confirmBill(Bill billStub);

    Bill rejectBill(Bill billStub, String reason);

    BillingType ensureBillingType(Lease lease);

    // Unitls:

    void updateLeaseAdjustmentTax(LeaseAdjustment adjustment);

    LogicalDate getNextCycleExecutionDate(BillingCycle cycle);

    BigDecimal getMaxLeaseTermMonthlyTotal(LeaseTerm leaseTerm);

    void onLeaseBillingPolicyChange(LeaseBillingPolicy oldPolicy, LeaseBillingPolicy newPolicy);

}
