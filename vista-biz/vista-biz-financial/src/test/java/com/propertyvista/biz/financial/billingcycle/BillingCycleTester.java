/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billingcycle;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.integration.Tester;

public class BillingCycleTester extends Tester {

    private final BillingCycle billingCycle;

    public BillingCycleTester(BillingCycle billingCycle) {
        super();
        this.billingCycle = billingCycle;
        if (billingCycle.stats().getAttachLevel() == AttachLevel.Detached) {
            Persistence.service().retrieveMember(billingCycle.stats());
        }
    }

    /** Retrieve BillingCycle; create if not existent based on the LeaseBillingPolicy for building */
    public static BillingCycle ensureBillingCycleForDate(Lease lease, String date) {
        return BillingCycleManager.instance().ensureBillingCycleForDate(lease, getDate(date));
    }

    /** Retrieve BillingCycle; create if not existent based on the LeaseBillingPolicy for building */
    public static BillingCycle ensureBillingCycleForDate(Building building, BillingPeriod billingPeriod, String leaseStartDate) {
        LogicalDate startDate = getDate(leaseStartDate);
        BillingType billingType = BillingCycleManager.instance().ensureBillingType(building, billingPeriod, startDate);
        return BillingCycleManager.instance().ensureBillingCycleForDate(building, billingType, startDate);
    }

    /** Retrieve BillingCycle if exists */
    public static BillingCycle getBillingCycleForDate(Building building, BillingPeriod billingPeriod, String date) {
        return BillingCycleManager.instance().ensureBillingCycleForDate(building, billingPeriod, getDate(date));
    }

    public BillingCycleTester billingCycleStartDate(String date) {
        assertEquals("Billing Cycle Start Date", getDate(date), billingCycle.billingCycleStartDate().getValue());
        return this;
    }

    public BillingCycleTester billingCycleEndDate(String date) {
        assertEquals("Billing Cycle End Date", getDate(date), billingCycle.billingCycleEndDate().getValue());
        return this;
    }

    public BillingCycleTester billExecutionDate(String date) {
        assertEquals("Target Bill Execution Date", getDate(date), billingCycle.targetBillExecutionDate().getValue());
        return this;
    }

    public BillingCycleTester autopayExecutionDate(String date) {
        assertEquals("Target AutoPay Execution Date", getDate(date), billingCycle.targetAutopayExecutionDate().getValue());
        return this;
    }

    public BillingCycleTester notConfirmedBills(Long ammount) {
        assertEquals("Not Confirmed Bills", ammount, ifNull(billingCycle.stats().notConfirmed().getValue(), 0L));
        return this;
    }

    public BillingCycleTester failedBills(Long ammount) {
        assertEquals("Failed Bills", ammount, ifNull(billingCycle.stats().failed().getValue(), 0L));
        return this;
    }

    public BillingCycleTester rejectedBills(Long ammount) {
        assertEquals("Rejected Bills", ammount, ifNull(billingCycle.stats().rejected().getValue(), 0L));
        return this;
    }

    public BillingCycleTester confirmedBills(Long ammount) {
        assertEquals("Confirmed Bills", ammount, ifNull(billingCycle.stats().confirmed().getValue(), 0L));
        return this;
    }

}
