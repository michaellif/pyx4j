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
 */
package com.propertyvista.biz.financial.billingcycle;

import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.test.integration.Tester;

public class BillingTypeTester extends Tester {

    private final BillingType billingType;

    public BillingTypeTester(BillingType billingType) {
        this.billingType = billingType;
    }

    public BillingTypeTester(Building building, BillingPeriod billingPeriod, String leaseStartDate) {
        this(BillingCycleManager.instance().ensureBillingType(building, billingPeriod, getDate(leaseStartDate)));
    }

    public BillingTypeTester billingCycleStartDate(Integer day) {
        assertEquals("Billing Cycle Start Day", day, billingType.billingCycleStartDay().getValue());
        return this;
    }

}
