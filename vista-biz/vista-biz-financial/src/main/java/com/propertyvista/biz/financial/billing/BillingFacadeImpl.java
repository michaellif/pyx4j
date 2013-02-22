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
package com.propertyvista.biz.financial.billing;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.operations.domain.scheduler.StatisticsRecord;

public class BillingFacadeImpl implements BillingFacade {

    @Override
    public Bill runBilling(Lease leaseId) {
        return BillingManager.runBilling(leaseId, false);
    }

    @Override
    public Bill runBillingPreview(Lease leaseId) {
        return BillingManager.runBilling(leaseId, true);
    }

    @Override
    public void runBilling(LogicalDate date, StatisticsRecord dynamicStatisticsRecord) {
        BillingManager.runBilling(date, dynamicStatisticsRecord);
    }

    @Override
    public void runBilling(BillingCycle billingCycle, StatisticsRecord dynamicStatisticsRecord) {
        BillingManager.runBilling(billingCycle, dynamicStatisticsRecord);
    }

    @Override
    public Bill getBill(Lease lease, int billSequenceNumber) {
        return BillingManager.getBill(lease, billSequenceNumber);
    }

    @Override
    public Bill getLatestConfirmedBill(Lease lease) {
        return BillingManager.getLatestConfirmedBill(lease);
    }

    @Override
    public Bill getLatestBill(Lease lease) {
        return BillingManager.getLatestBill(lease);
    }

    @Override
    public boolean isLatestBill(Bill bill) {
        return BillingManager.isLatestBill(bill);
    }

    @Override
    public Bill confirmBill(Bill bill) {
        return BillingManager.confirmBill(bill);
    }

    @Override
    public Bill rejectBill(Bill bill, String reason) {
        return BillingManager.rejectBill(bill, reason);
    }

    @Override
    public void initializeFutureBillingCycles() {
        BillingManager.initializeFutureBillingCycles();
    }

    @Override
    public BillingType ensureBillingType(Lease lease) {
        return BillingManager.ensureBillingType(lease);
    }

    @Override
    public void updateLeaseAdjustmentTax(LeaseAdjustment adjustment) {
        BillingManager.updateLeaseAdjustmentTax(adjustment);
    }

    @Override
    public LogicalDate getNextCycleExecutionDate(BillingCycle cycle) {
        LogicalDate startDate = BillDateUtils.calculateSubsiquentBillingCycleStartDate(cycle.billingType().paymentFrequency().getValue(), cycle
                .billingCycleStartDate().getValue());
        return BillDateUtils.calculateBillingCycleTargetExecutionDate(cycle.billingType().paymentFrequency().getValue(), startDate);
    }

    @Override
    public BigDecimal getMaxLeaseTermMonthlyTotal(LeaseTerm leaseTerm) {
        return BillingUtils.getMaxLeaseTermMonthlyTotal(leaseTerm);
    }
}
