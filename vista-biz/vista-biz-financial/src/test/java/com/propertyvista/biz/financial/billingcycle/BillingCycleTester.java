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

import com.propertyvista.biz.financial.Tester;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.property.asset.building.Building;

public class BillingCycleTester extends Tester {

    private final BillingCycle billingCycle;

    public BillingCycleTester(BillingCycle billingCycle, boolean continueOnError) {
        super(continueOnError);
        this.billingCycle = billingCycle;
    }

    public BillingCycleTester(BillingCycle billingCycle) {
        this(billingCycle, false);
    }

    public BillingCycleTester(Building building, BillingPeriod billingPeriod, String leaseStartDate) {
        this(BillingCycleManager.instance().getBillingCycle(building, billingPeriod, getDate(leaseStartDate)));
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
        assertEquals("Bill Execution Date", getDate(date), billingCycle.billExecutionDate().getValue());
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
