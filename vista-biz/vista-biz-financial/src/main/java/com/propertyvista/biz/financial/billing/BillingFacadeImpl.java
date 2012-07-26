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

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingFacadeImpl implements BillingFacade {

    @Override
    public Bill runBilling(Lease leaseId) {
        return BillingRunner.runBilling(leaseId, false);
    }

    @Override
    public Bill runBillingPreview(Lease leaseId) {
        return BillingRunner.runBilling(leaseId, true);
    }

    @Override
    public void runBilling(LogicalDate date, StatisticsRecord dynamicStatisticsRecord) {
        BillingRunner.runBilling(date, dynamicStatisticsRecord);
    }

    @Override
    public void runBilling(BillingCycle billingCycle, StatisticsRecord dynamicStatisticsRecord) {
        BillingRunner.runBilling(billingCycle, dynamicStatisticsRecord);
    }

    @Override
    public Bill getLatestConfirmedBill(Lease lease) {
        return BillingRunner.getLatestConfirmedBill(lease);
    }

    @Override
    public Bill getLatestBill(Lease lease) {
        return BillingRunner.getLatestBill(lease);
    }

    @Override
    public boolean isLatestBill(Bill bill) {
        return BillingRunner.isLatestBill(bill);
    }

    @Override
    public Bill confirmBill(Bill bill) {
        return BillingRunner.confirmBill(bill);
    }

    @Override
    public Bill rejectBill(Bill bill, String reason) {
        return BillingRunner.rejectBill(bill, reason);
    }

    @Override
    public void initializeFutureBillingCycles() {
        BillingRunner.initializeFutureBillingCycles();
    }

    @Override
    public BillingType ensureBillingType(Lease lease) {
        return BillingRunner.ensureBillingType(lease);
    }

}
