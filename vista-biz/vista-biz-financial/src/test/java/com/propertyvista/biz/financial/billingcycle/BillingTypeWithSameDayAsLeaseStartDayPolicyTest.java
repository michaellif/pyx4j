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
public class BillingTypeWithSameDayAsLeaseStartDayPolicyTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreloadConfig config = new PreloadConfig();
        config.defaultBillingCycleSartDay = null;
        preloadData(config);

    }

//    public void testFirstBillingCycle() throws ParseException {
//        BillingCycle billingCycle = createFirstBillingCycle(getDate("23-Feb-2012"), 23);
//
//        // @formatter:off
//        new BillingCycleTester(billingCycle).
//        billingCycleStartDate("23-Feb-2012").
//        billingCycleEndDate("22-Mar-2012").
//        billExecutionDate("08-Feb-2012");
//        // @formatter:on
//
//    }
//
//    public void testSubsiquentBillingCycle() throws ParseException {
//        BillingCycle billingCycle = createFirstBillingCycle(getDate("23-Feb-2012"), 23);
//        billingCycle = createSubsiquentBillingCycle(billingCycle);
//
//        // @formatter:off
//        new BillingCycleTester(billingCycle).
//        billingCycleStartDate("23-Mar-2012").
//        billingCycleEndDate("22-Apr-2012").
//        billExecutionDate("08-Mar-2012");
//        // @formatter:on
//
//    }
//
//    public void testExistingLeaseInitialBillingCycle() throws ParseException {
//        BillingCycle billingCycle = createExistingLeaseInitialBillingCycle(getDate("23-Feb-2011"),
//                getDate("23-Mar-2012"), 23);
//
//        assertEquals("Billing Period Start Date", getDate("23-Mar-2012"), billingCycle.billingCycleStartDate().getValue());
//        assertEquals("Billing Period End Date", getDate("22-Apr-2012"), billingCycle.billingCycleEndDate().getValue());
//        assertEquals("Billing Execution Target Date", getDate("08-Mar-2012"), billingCycle.billExecutionDate().getValue());
//
//        billingCycle = createExistingLeaseInitialBillingCycle(getDate("23-Feb-2011"), getDate("24-Mar-2012"), 23);
//
//        assertEquals("Billing Period Start Date", getDate("23-Mar-2012"), billingCycle.billingCycleStartDate().getValue());
//        assertEquals("Billing Period End Date", getDate("22-Apr-2012"), billingCycle.billingCycleEndDate().getValue());
//        assertEquals("Billing Execution Target Date", getDate("08-Mar-2012"), billingCycle.billExecutionDate().getValue());
//
//        billingCycle = createExistingLeaseInitialBillingCycle(getDate("23-Feb-2011"), getDate("22-Mar-2012"), 23);
//
//        assertEquals("Billing Period Start Date", getDate("23-Feb-2012"), billingCycle.billingCycleStartDate().getValue());
//        assertEquals("Billing Period End Date", getDate("22-Mar-2012"), billingCycle.billingCycleEndDate().getValue());
//        assertEquals("Billing Execution Target Date", getDate("08-Feb-2012"), billingCycle.billExecutionDate().getValue());
//
//        billingCycle = createExistingLeaseInitialBillingCycle(getDate("23-Feb-2011"), getDate("15-Mar-2012"), 23);
//
//        assertEquals("Billing Period Start Date", getDate("23-Feb-2012"), billingCycle.billingCycleStartDate().getValue());
//        assertEquals("Billing Period End Date", getDate("22-Mar-2012"), billingCycle.billingCycleEndDate().getValue());
//        assertEquals("Billing Execution Target Date", getDate("08-Feb-2012"), billingCycle.billExecutionDate().getValue());
//
//    }
//
//    public void testFirstBillingCycleForEndOfMonth() throws ParseException {
//        BillingCycle billingCycle = createFirstBillingCycle(getDate("29-Mar-2012"), 1);
//
//        // @formatter:off
//        new BillingCycleTester(billingCycle).
//        billingCycleStartDate("01-Mar-2012").
//        billingCycleEndDate("31-Mar-2012").
//        billExecutionDate("15-Feb-2012");
//        // @formatter:on
//
//    }

//    public void testSubsiquentBillingCycleForEndOfMonth() throws ParseException {
//        BillingCycle billingCycle = createFirstBillingCycle(getDate("29-Mar-2012"), 1);
//        billingCycle = createSubsiquentBillingCycle(billingCycle);
//
//        assertEquals("Billing Period Start Date", getDate("01-Apr-2012"), billingCycle.billingCycleStartDate().getValue());
//        assertEquals("Billing Period End Date", getDate("30-Apr-2012"), billingCycle.billingCycleEndDate().getValue());
//        assertEquals("Billing Execution Target Date", getDate("17-Mar-2012"), billingCycle.billExecutionDate().getValue());
//    }
//
//    private BillingCycle createFirstBillingCycle(LogicalDate leaseStartDate, Integer billingCycleStartDay) {
//        BillingType billingType = BillingCycleManager.instance().ensureBillingType(BillingPeriod.Monthly, billingCycleStartDay);
//        BillingCycle billingCycle = BillingCycleManager.instance().ensureBillingCycle(billingType, buildingDataModel.getBuilding(), leaseStartDate);
//        return billingCycle;
//    }
//
//    private BillingCycle createExistingLeaseInitialBillingCycle(LogicalDate leaseStartDate, LogicalDate leaseCreationDate, Integer billingCycleStartDay) {
//        BillingType billingType = BillingCycleManager.instance().ensureBillingType(BillingPeriod.Monthly, billingCycleStartDay);
//        BillingCycle billingCycle = BillingCycleManager.instance().ensureBillingCycle(billingType, buildingDataModel.getBuilding(), leaseStartDate,
//                leaseCreationDate);
//        return billingCycle;
//    }
//
//    private BillingCycle createSubsiquentBillingCycle(BillingCycle previousBillingCycle) {
//        BillingCycle billingCycle = BillingCycleManager.instance().ensureSubsiquentBillingCycle(previousBillingCycle);
//        return billingCycle;
//    }

    public void testCalculateBillingTypeForGivenLeaseStartDay_Monthly() throws ParseException {
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "01-Feb-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "23-Feb-2012").billingCycleStartDate(23);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "28-Feb-2012").billingCycleStartDate(28);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "29-Feb-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "24-Mar-2012").billingCycleStartDate(24);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "28-Mar-2012").billingCycleStartDate(28);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "29-Mar-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "30-Mar-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "31-Mar-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "01-Feb-2013").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "23-Feb-2013").billingCycleStartDate(23);
        new BillingTypeTester(getBuilding(), BillingPeriod.Monthly, "28-Feb-2013").billingCycleStartDate(28);
    }

    public void testCalculateBillingTypeForGivenLeaseStartDay_SemiMonthly() throws ParseException {
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "01-Feb-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "14-Feb-2012").billingCycleStartDate(14);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "15-Feb-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "23-Feb-2012").billingCycleStartDate(9);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "28-Feb-2012").billingCycleStartDate(14);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "29-Feb-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "14-Mar-2012").billingCycleStartDate(14);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "15-Mar-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "24-Mar-2012").billingCycleStartDate(10);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "28-Mar-2012").billingCycleStartDate(14);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "29-Mar-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "30-Mar-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "31-Mar-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "01-Feb-2013").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "23-Feb-2013").billingCycleStartDate(9);
        new BillingTypeTester(getBuilding(), BillingPeriod.SemiMonthly, "28-Feb-2013").billingCycleStartDate(14);
    }

    public void testCalculateBillingTypeForGivenLeaseStartDay_Weekly() throws ParseException {
        new BillingTypeTester(getBuilding(), BillingPeriod.Weekly, "01-Feb-2012").billingCycleStartDate(4);
        new BillingTypeTester(getBuilding(), BillingPeriod.Weekly, "04-Feb-2012").billingCycleStartDate(7);
        new BillingTypeTester(getBuilding(), BillingPeriod.Weekly, "05-Feb-2012").billingCycleStartDate(1);
    }

    public void testCalculateBillingTypeForGivenLeaseStartDay_BiWeekly() throws ParseException {
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "01-Feb-2012").billingCycleStartDate(4);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "04-Feb-2012").billingCycleStartDate(7);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "05-Feb-2012").billingCycleStartDate(8);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "11-Feb-2012").billingCycleStartDate(14);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "12-Feb-2012").billingCycleStartDate(1);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "01-Feb-2013").billingCycleStartDate(6);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "04-Feb-2013").billingCycleStartDate(9);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "05-Feb-2013").billingCycleStartDate(10);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "11-Feb-2013").billingCycleStartDate(2);
        new BillingTypeTester(getBuilding(), BillingPeriod.BiWeekly, "12-Feb-2013").billingCycleStartDate(3);
    }

}
