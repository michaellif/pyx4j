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

import java.text.ParseException;

import org.junit.experimental.categories.Category;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.test.mock.MockConfig;

@Category(FunctionalTests.class)
public class BillingCycleForDefaultStartDayPolicy28Test extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockConfig config = new MockConfig();
        config.defaultBillingCycleSartDay = 28;
        preloadData(config);
    }

    public void testBillingCycleCreation() throws ParseException {
        // @formatter:off
        new BillingCycleTester(BillingCycleTester.ensureBillingCycle(getBuilding(), BillingPeriod.Monthly,  "28-Apr-2013")).
        billingCycleStartDate("28-Apr-2013").
        billingCycleEndDate("27-May-2013");
        // @formatter:on

        // @formatter:off
        new BillingCycleTester(BillingCycleTester.ensureBillingCycle(getBuilding(), BillingPeriod.Monthly,  "29-Apr-2013")).
        billingCycleStartDate("28-Apr-2013").
        billingCycleEndDate("27-May-2013");
        // @formatter:on

        // @formatter:off
        new BillingCycleTester(BillingCycleTester.ensureBillingCycle(getBuilding(), BillingPeriod.Monthly,  "30-Apr-2013")).
        billingCycleStartDate("28-Apr-2013").
        billingCycleEndDate("27-May-2013");
        // @formatter:on

        // @formatter:off
        new BillingCycleTester(BillingCycleTester.ensureBillingCycle(getBuilding(), BillingPeriod.Monthly,  "01-May-2013")).
        billingCycleStartDate("28-Apr-2013").
        billingCycleEndDate("27-May-2013");
        // @formatter:on

        // @formatter:off
        new BillingCycleTester(BillingCycleTester.ensureBillingCycle(getBuilding(), BillingPeriod.Monthly,  "05-May-2013")).
        billingCycleStartDate("28-Apr-2013").
        billingCycleEndDate("27-May-2013");
        // @formatter:on

        // @formatter:off
        new BillingCycleTester(BillingCycleTester.ensureBillingCycle(getBuilding(), BillingPeriod.Monthly,  "27-May-2013")).
        billingCycleStartDate("28-Apr-2013").
        billingCycleEndDate("27-May-2013");
        // @formatter:on

    }

}
