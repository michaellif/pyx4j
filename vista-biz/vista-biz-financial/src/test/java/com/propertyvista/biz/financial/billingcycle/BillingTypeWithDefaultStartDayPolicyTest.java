/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 4, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billingcycle;

import java.text.ParseException;

import org.junit.experimental.categories.Category;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.test.preloader.PreloadConfig;

@Category(FunctionalTests.class)
public class BillingTypeWithDefaultStartDayPolicyTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreloadConfig config = new PreloadConfig();
        config.defaultBillingCycleSartDay = 5;
        preloadData(config);

    }

    public void testCalculateBillingTypeForGivenLeaseStartDay_Monthly() throws ParseException {
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "01-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "23-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "28-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "29-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "24-Mar-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "28-Mar-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "29-Mar-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "30-Mar-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "31-Mar-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "01-Feb-2013").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "23-Feb-2013").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "28-Feb-2013").billingCycleStartDate(5);
    }

    public void testCalculateBillingTypeForGivenLeaseStartDay_SemiMonthly() throws ParseException {
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "01-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "14-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "15-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "23-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "28-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "29-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "14-Mar-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "15-Mar-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "24-Mar-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "28-Mar-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "29-Mar-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "30-Mar-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "31-Mar-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "01-Feb-2013").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "23-Feb-2013").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "28-Feb-2013").billingCycleStartDate(5);
    }

    public void testCalculateBillingTypeForGivenLeaseStartDay_Weekly() throws ParseException {
        new BillingTypeTester(getBuilding(), BillingPeriod.Weekly, "01-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Weekly, "04-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.Weekly, "05-Feb-2012").billingCycleStartDate(5);
    }

    public void testCalculateBillingTypeForGivenLeaseStartDay_BiWeekly() throws ParseException {
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "01-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "04-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "05-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "11-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "12-Feb-2012").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "01-Feb-2013").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "04-Feb-2013").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "05-Feb-2013").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "11-Feb-2013").billingCycleStartDate(5);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "12-Feb-2013").billingCycleStartDate(5);
    }

}
