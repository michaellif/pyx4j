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
package com.propertyvista.biz.financial.billing;

import java.text.ParseException;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public class BillingCycleMangerTest extends VistaDBTestBase {

    public void testBillingPeriodStartDate() throws ParseException {
        BillingRun billingRun = createFirstBillingRun(BillingTestUtils.getDate("23-Feb-2012"), null, false);

        assertEquals("Billing Period Start Date", BillingTestUtils.getDate("23-Feb-2012"), billingRun.billingPeriodStartDate().getValue());
        assertEquals("Billing Period End Date", BillingTestUtils.getDate("22-Mar-2012"), billingRun.billingPeriodEndDate().getValue());
        assertEquals("Billing Execution Target Date", BillingTestUtils.getDate("08-Feb-2012"), billingRun.executionTargetDate().getValue());

        billingRun = createSubsiquentBillingRun(billingRun);

        assertEquals("Billing Period Start Date", BillingTestUtils.getDate("23-Mar-2012"), billingRun.billingPeriodStartDate().getValue());
        assertEquals("Billing Period End Date", BillingTestUtils.getDate("22-Apr-2012"), billingRun.billingPeriodEndDate().getValue());
        assertEquals("Billing Execution Target Date", BillingTestUtils.getDate("08-Mar-2012"), billingRun.executionTargetDate().getValue());
    }

    public void testBillingPeriodStartDateForEndOfMonth() throws ParseException {
        BillingRun billingRun = createFirstBillingRun(BillingTestUtils.getDate("29-Mar-2012"), null, false);

        assertEquals("Billing Period Start Date", BillingTestUtils.getDate("1-Mar-2012"), billingRun.billingPeriodStartDate().getValue());
        assertEquals("Billing Period End Date", BillingTestUtils.getDate("31-Mar-2012"), billingRun.billingPeriodEndDate().getValue());
        assertEquals("Billing Execution Target Date", BillingTestUtils.getDate("15-Feb-2012"), billingRun.executionTargetDate().getValue());

        billingRun = createSubsiquentBillingRun(billingRun);

        assertEquals("Billing Period Start Date", BillingTestUtils.getDate("1-Apr-2012"), billingRun.billingPeriodStartDate().getValue());
        assertEquals("Billing Period End Date", BillingTestUtils.getDate("30-Apr-2012"), billingRun.billingPeriodEndDate().getValue());
        assertEquals("Billing Execution Target Date", BillingTestUtils.getDate("17-Mar-2012"), billingRun.executionTargetDate().getValue());
    }

    private BillingRun createFirstBillingRun(LogicalDate leaseStartDate, Integer billingCycleStartDate, boolean useCycleLeaseDay) {
        BillingCycle billingCycle = EntityFactory.create(BillingCycle.class);
        billingCycle.paymentFrequency().setValue(PaymentFrequency.Monthly);
        billingCycle.billingPeriodStartDay().setValue(billingCycleStartDate);

        BillingRun billingRun = BillingCycleManger.createFirstBillingRun(billingCycle, leaseStartDate, useCycleLeaseDay);
        return billingRun;
    }

    private BillingRun createSubsiquentBillingRun(BillingRun previousBillingRun) {
        BillingRun billingRun = BillingCycleManger.createSubsiquentBillingRun(previousBillingRun.billingCycle(), previousBillingRun);
        return billingRun;
    }

    public void testBillingCycleStartDayForMonthlyFrequency() throws ParseException {
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Monthly, BillingTestUtils.getDate("1-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 23,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Monthly, BillingTestUtils.getDate("23-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 28,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Monthly, BillingTestUtils.getDate("28-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Monthly, BillingTestUtils.getDate("29-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 24,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Monthly, BillingTestUtils.getDate("24-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 28,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Monthly, BillingTestUtils.getDate("28-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Monthly, BillingTestUtils.getDate("29-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Monthly, BillingTestUtils.getDate("30-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Monthly, BillingTestUtils.getDate("31-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Monthly, BillingTestUtils.getDate("1-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 23,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Monthly, BillingTestUtils.getDate("23-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 28,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Monthly, BillingTestUtils.getDate("28-Feb-2013")));

    }

    public void testBillingCycleStartDayForSemimonthlyFrequency() throws ParseException {
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("1-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 14,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("14-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("15-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 9,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("23-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 14,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("28-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("29-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 14,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("14-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("15-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 10,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("24-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 14,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("28-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("29-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("30-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("31-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("1-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 9,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("23-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 14,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.SemiMonthly, BillingTestUtils.getDate("28-Feb-2013")));

    }

    public void testBillingCycleStartDayForWeeklyFrequency() throws ParseException {
        assertEquals("Billing Cycle Start Date", 4,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Weekly, BillingTestUtils.getDate("1-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 7,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Weekly, BillingTestUtils.getDate("4-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.Weekly, BillingTestUtils.getDate("5-Feb-2012")));
    }

    public void testBillingCycleStartDayForBiweeklyFrequency() throws ParseException {
        assertEquals("Billing Cycle Start Date", 4,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.BiWeekly, BillingTestUtils.getDate("1-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 7,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.BiWeekly, BillingTestUtils.getDate("4-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 8,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.BiWeekly, BillingTestUtils.getDate("5-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 14,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.BiWeekly, BillingTestUtils.getDate("11-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.BiWeekly, BillingTestUtils.getDate("12-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 6,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.BiWeekly, BillingTestUtils.getDate("1-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 9,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.BiWeekly, BillingTestUtils.getDate("4-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 10,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.BiWeekly, BillingTestUtils.getDate("5-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 2,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.BiWeekly, BillingTestUtils.getDate("11-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 3,
                BillingCycleManger.calculateBillingCycleStartDay(PaymentFrequency.BiWeekly, BillingTestUtils.getDate("12-Feb-2013")));
    }

}
