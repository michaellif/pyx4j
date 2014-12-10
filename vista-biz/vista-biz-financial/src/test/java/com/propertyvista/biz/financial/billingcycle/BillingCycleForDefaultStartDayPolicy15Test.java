/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 15, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billingcycle;

import org.junit.experimental.categories.Category;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.mock.MockConfig;

@Category(FunctionalTests.class)
public class BillingCycleForDefaultStartDayPolicy15Test extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockConfig config = new MockConfig();
        config.defaultBillingCycleSartDay = 15;
        preloadData(config);

    }

    public void testBillingCycleCreation() {

        new BillingCycleTester(BillingCycleTester.ensureBillingCycleForDate(getBuilding(), BillingPeriod.Monthly, "28-Apr-2013")). //
                billingCycleStartDate("15-Apr-2013"). //
                billingCycleEndDate("14-May-2013");

        new BillingCycleTester(BillingCycleTester.ensureBillingCycleForDate(getBuilding(), BillingPeriod.Monthly, "29-Apr-2013")). //
                billingCycleStartDate("15-Apr-2013"). //
                billingCycleEndDate("14-May-2013");

        new BillingCycleTester(BillingCycleTester.ensureBillingCycleForDate(getBuilding(), BillingPeriod.Monthly, "30-Apr-2013")). //
                billingCycleStartDate("15-Apr-2013"). //
                billingCycleEndDate("14-May-2013");

        new BillingCycleTester(BillingCycleTester.ensureBillingCycleForDate(getBuilding(), BillingPeriod.Monthly, "01-May-2013")). //
                billingCycleStartDate("15-Apr-2013"). //
                billingCycleEndDate("14-May-2013");

        new BillingCycleTester(BillingCycleTester.ensureBillingCycleForDate(getBuilding(), BillingPeriod.Monthly, "05-May-2013")). //
                billingCycleStartDate("15-Apr-2013"). //
                billingCycleEndDate("14-May-2013");

        new BillingCycleTester(BillingCycleTester.ensureBillingCycleForDate(getBuilding(), BillingPeriod.Monthly, "27-May-2013")). //
                billingCycleStartDate("15-May-2013"). //
                billingCycleEndDate("14-Jun-2013");
    }

    public void testBillingForDate() {

        createLease("23-Mar-2013", "03-Aug-2013");

        new BillingCycleTester(BillingCycleTester.ensureBillingCycleForDate(getLease(), "14-May-2013")). //
                billingCycleStartDate("15-Apr-2013"). //
                billingCycleEndDate("14-May-2013");

        new BillingCycleTester(BillingCycleTester.ensureBillingCycleForDate(getLease(), "15-May-2013")). //
                billingCycleStartDate("15-May-2013"). //
                billingCycleEndDate("14-Jun-2013");

        new BillingCycleTester(BillingCycleTester.ensureBillingCycleForDate(getLease(), "30-May-2013")). //
                billingCycleStartDate("15-May-2013"). //
                billingCycleEndDate("14-Jun-2013");

        new BillingCycleTester(BillingCycleTester.ensureBillingCycleForDate(getLease(), "14-Jun-2013")). //
                billingCycleStartDate("15-May-2013"). //
                billingCycleEndDate("14-Jun-2013");

        new BillingCycleTester(BillingCycleTester.ensureBillingCycleForDate(getLease(), "15-Jun-2013")). //
                billingCycleStartDate("15-Jun-2013"). //
                billingCycleEndDate("14-Jul-2013");

    }
}
