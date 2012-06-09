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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.FinancialTestsUtils;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public class BillingCycleTest extends VistaDBTestBase {

    public void testFirstBillingCycle() throws ParseException {
        BillingCycle billingCycle = createFirstBillingCycle(FinancialTestsUtils.getDate("23-Feb-2012"), null, false);

        assertEquals("Billing Period Start Date", FinancialTestsUtils.getDate("23-Feb-2012"), billingCycle.billingPeriodStartDate().getValue());
        assertEquals("Billing Period End Date", FinancialTestsUtils.getDate("22-Mar-2012"), billingCycle.billingPeriodEndDate().getValue());
        assertEquals("Billing Execution Target Date", FinancialTestsUtils.getDate("08-Feb-2012"), billingCycle.executionTargetDate().getValue());

    }

    public void testSubsiquentBillingCycle() throws ParseException {
        BillingCycle billingCycle = createFirstBillingCycle(FinancialTestsUtils.getDate("23-Feb-2012"), null, false);
        billingCycle = createSubsiquentBillingCycle(billingCycle);

        assertEquals("Billing Period Start Date", FinancialTestsUtils.getDate("23-Mar-2012"), billingCycle.billingPeriodStartDate().getValue());
        assertEquals("Billing Period End Date", FinancialTestsUtils.getDate("22-Apr-2012"), billingCycle.billingPeriodEndDate().getValue());
        assertEquals("Billing Execution Target Date", FinancialTestsUtils.getDate("08-Mar-2012"), billingCycle.executionTargetDate().getValue());

    }

    public void testExistingLeaseInitialBillingCycle() throws ParseException {
        BillingCycle billingCycle = createExistingLeaseInitialBillingCycle(FinancialTestsUtils.getDate("23-Feb-2011"),
                FinancialTestsUtils.getDate("23-Mar-2012"), null, false);

        assertEquals("Billing Period Start Date", FinancialTestsUtils.getDate("23-Mar-2012"), billingCycle.billingPeriodStartDate().getValue());
        assertEquals("Billing Period End Date", FinancialTestsUtils.getDate("22-Apr-2012"), billingCycle.billingPeriodEndDate().getValue());
        assertEquals("Billing Execution Target Date", FinancialTestsUtils.getDate("08-Mar-2012"), billingCycle.executionTargetDate().getValue());

        billingCycle = createExistingLeaseInitialBillingCycle(FinancialTestsUtils.getDate("23-Feb-2011"), FinancialTestsUtils.getDate("24-Mar-2012"), null,
                false);

        assertEquals("Billing Period Start Date", FinancialTestsUtils.getDate("23-Mar-2012"), billingCycle.billingPeriodStartDate().getValue());
        assertEquals("Billing Period End Date", FinancialTestsUtils.getDate("22-Apr-2012"), billingCycle.billingPeriodEndDate().getValue());
        assertEquals("Billing Execution Target Date", FinancialTestsUtils.getDate("08-Mar-2012"), billingCycle.executionTargetDate().getValue());

        billingCycle = createExistingLeaseInitialBillingCycle(FinancialTestsUtils.getDate("23-Feb-2011"), FinancialTestsUtils.getDate("22-Mar-2012"), null,
                false);

        assertEquals("Billing Period Start Date", FinancialTestsUtils.getDate("23-Feb-2012"), billingCycle.billingPeriodStartDate().getValue());
        assertEquals("Billing Period End Date", FinancialTestsUtils.getDate("22-Mar-2012"), billingCycle.billingPeriodEndDate().getValue());
        assertEquals("Billing Execution Target Date", FinancialTestsUtils.getDate("08-Feb-2012"), billingCycle.executionTargetDate().getValue());

        billingCycle = createExistingLeaseInitialBillingCycle(FinancialTestsUtils.getDate("23-Feb-2011"), FinancialTestsUtils.getDate("15-Mar-2012"), null,
                false);

        assertEquals("Billing Period Start Date", FinancialTestsUtils.getDate("23-Feb-2012"), billingCycle.billingPeriodStartDate().getValue());
        assertEquals("Billing Period End Date", FinancialTestsUtils.getDate("22-Mar-2012"), billingCycle.billingPeriodEndDate().getValue());
        assertEquals("Billing Execution Target Date", FinancialTestsUtils.getDate("08-Feb-2012"), billingCycle.executionTargetDate().getValue());

    }

    public void testFirstBillingCycleForEndOfMonth() throws ParseException {
        BillingCycle billingCycle = createFirstBillingCycle(FinancialTestsUtils.getDate("29-Mar-2012"), null, false);

        assertEquals("Billing Period Start Date", FinancialTestsUtils.getDate("1-Mar-2012"), billingCycle.billingPeriodStartDate().getValue());
        assertEquals("Billing Period End Date", FinancialTestsUtils.getDate("31-Mar-2012"), billingCycle.billingPeriodEndDate().getValue());
        assertEquals("Billing Execution Target Date", FinancialTestsUtils.getDate("15-Feb-2012"), billingCycle.executionTargetDate().getValue());

    }

    public void testSubsiquentBillingCycleForEndOfMonth() throws ParseException {
        BillingCycle billingCycle = createFirstBillingCycle(FinancialTestsUtils.getDate("29-Mar-2012"), null, false);
        billingCycle = createSubsiquentBillingCycle(billingCycle);

        assertEquals("Billing Period Start Date", FinancialTestsUtils.getDate("1-Apr-2012"), billingCycle.billingPeriodStartDate().getValue());
        assertEquals("Billing Period End Date", FinancialTestsUtils.getDate("30-Apr-2012"), billingCycle.billingPeriodEndDate().getValue());
        assertEquals("Billing Execution Target Date", FinancialTestsUtils.getDate("17-Mar-2012"), billingCycle.executionTargetDate().getValue());
    }

    private BillingCycle createFirstBillingCycle(LogicalDate leaseStartDate, Integer billingPeriodStartDay, boolean useCycleLeaseDay) {
        BillingType billingType = EntityFactory.create(BillingType.class);
        billingType.paymentFrequency().setValue(PaymentFrequency.Monthly);
        billingType.billingPeriodStartDay().setValue(billingPeriodStartDay);
        Persistence.service().persist(billingType);

        BillingCycle billingCycle = BillingLifecycleManager.getNewLeaseInitialBillingCycle(billingType, null, leaseStartDate, useCycleLeaseDay);
        return billingCycle;
    }

    private BillingCycle createExistingLeaseInitialBillingCycle(LogicalDate leaseStartDate, LogicalDate leaseCreationDate, Integer billingPeriodStartDay,
            boolean useCycleLeaseDay) {
        BillingType billingType = EntityFactory.create(BillingType.class);
        billingType.paymentFrequency().setValue(PaymentFrequency.Monthly);
        billingType.billingPeriodStartDay().setValue(billingPeriodStartDay);
        Persistence.service().persist(billingType);

        BillingCycle billingCycle = BillingLifecycleManager.getExistingLeaseInitialBillingCycle(billingType, null, leaseStartDate, leaseCreationDate,
                useCycleLeaseDay);
        return billingCycle;
    }

    private BillingCycle createSubsiquentBillingCycle(BillingCycle previousBillingCycle) {
        BillingCycle billingCycle = BillingLifecycleManager.getSubsiquentBillingCycle(previousBillingCycle);
        return billingCycle;
    }

    public void testBillingTypeStartDayForMonthlyFrequency() throws ParseException {
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Monthly, FinancialTestsUtils.getDate("1-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 23,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Monthly, FinancialTestsUtils.getDate("23-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 28,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Monthly, FinancialTestsUtils.getDate("28-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Monthly, FinancialTestsUtils.getDate("29-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 24,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Monthly, FinancialTestsUtils.getDate("24-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 28,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Monthly, FinancialTestsUtils.getDate("28-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Monthly, FinancialTestsUtils.getDate("29-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Monthly, FinancialTestsUtils.getDate("30-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Monthly, FinancialTestsUtils.getDate("31-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Monthly, FinancialTestsUtils.getDate("1-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 23,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Monthly, FinancialTestsUtils.getDate("23-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 28,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Monthly, FinancialTestsUtils.getDate("28-Feb-2013")));

    }

    public void testBillingTypeStartDayForSemimonthlyFrequency() throws ParseException {
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("1-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 14,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("14-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("15-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 9,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("23-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 14,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("28-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("29-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 14,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("14-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("15-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 10,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("24-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 14,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("28-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("29-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("30-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("31-Mar-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("1-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 9,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("23-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 14,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.SemiMonthly, FinancialTestsUtils.getDate("28-Feb-2013")));

    }

    public void testBillingTypeStartDayForWeeklyFrequency() throws ParseException {
        assertEquals("Billing Cycle Start Date", 4,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Weekly, FinancialTestsUtils.getDate("1-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 7,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Weekly, FinancialTestsUtils.getDate("4-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.Weekly, FinancialTestsUtils.getDate("5-Feb-2012")));
    }

    public void testBillingTypeStartDayForBiweeklyFrequency() throws ParseException {
        assertEquals("Billing Cycle Start Date", 4,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.BiWeekly, FinancialTestsUtils.getDate("1-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 7,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.BiWeekly, FinancialTestsUtils.getDate("4-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 8,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.BiWeekly, FinancialTestsUtils.getDate("5-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 14,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.BiWeekly, FinancialTestsUtils.getDate("11-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 1,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.BiWeekly, FinancialTestsUtils.getDate("12-Feb-2012")));
        assertEquals("Billing Cycle Start Date", 6,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.BiWeekly, FinancialTestsUtils.getDate("1-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 9,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.BiWeekly, FinancialTestsUtils.getDate("4-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 10,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.BiWeekly, FinancialTestsUtils.getDate("5-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 2,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.BiWeekly, FinancialTestsUtils.getDate("11-Feb-2013")));
        assertEquals("Billing Cycle Start Date", 3,
                BillDateUtils.calculateBillingTypeStartDay(PaymentFrequency.BiWeekly, FinancialTestsUtils.getDate("12-Feb-2013")));
    }

}
