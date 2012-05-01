/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.text.ParseException;
import java.util.Date;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.billing.BillingException;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingPeriodsTest extends FinancialTestBase {

    public void testSequentialBillingRunWithGlobalBillingPeriodStartDate() throws ParseException {
        preloadData();
        setLeaseConditions("23-Mar-2011", "3-Aug-2011", 1);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("18-Feb-2011");
        setLeaseStatus(Lease.Status.Approved);

        Bill bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("01-Mar-2011").
        billingRunPeriodEndDate("31-Mar-2011").
        billingRunExecutionTargetDate("14-Feb-2011").
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011");
        // @formatter:on

        String billingCycleId = bill.billingRun().billingCycle().id().toString();

        //==================== RUN 2 ======================//

        setLeaseStatus(Lease.Status.Active);

        SysDateManager.setSysDate("19-Mar-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("1-Apr-2011").
        billingRunPeriodEndDate("30-Apr-2011").
        billingRunExecutionTargetDate("17-Mar-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 3 ======================//

        SysDateManager.setSysDate("16-Apr-2011");

        bill = runBilling(false);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        previousBillSequenceNumber(2).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("1-May-2011").
        billingRunPeriodEndDate("31-May-2011").
        billingRunExecutionTargetDate("16-Apr-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        billStatus(Bill.BillStatus.Rejected);
        // @formatter:on

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(4).
        previousBillSequenceNumber(2).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("1-May-2011").
        billingRunPeriodEndDate("31-May-2011").
        billingRunExecutionTargetDate("16-Apr-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        billStatus(Bill.BillStatus.Confirmed);
        // @formatter:on

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 4 ======================//

        SysDateManager.setSysDate("18-May-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        previousBillSequenceNumber(4).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("1-June-2011").
        billingRunPeriodEndDate("30-June-2011").
        billingRunExecutionTargetDate("17-May-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-June-2011").
        billingPeriodEndDate("30-June-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 5 ======================//

        SysDateManager.setSysDate("18-Jun-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        previousBillSequenceNumber(5).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("1-July-2011").
        billingRunPeriodEndDate("31-July-2011").
        billingRunExecutionTargetDate("16-Jun-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-July-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 6 ======================//

        SysDateManager.setSysDate("18-Jul-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        previousBillSequenceNumber(6).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("1-Aug-2011").
        billingRunPeriodEndDate("31-Aug-2011").
        billingRunExecutionTargetDate("17-Jul-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Aug-2011").
        billingPeriodEndDate("3-Aug-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN final ======================//

        SysDateManager.setSysDate("05-Aug-2011");

        setLeaseStatus(Lease.Status.Completed);

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(8).
        previousBillSequenceNumber(7).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("01-Sep-2011").
        billingRunPeriodEndDate("30-Sep-2011").
        billingRunExecutionTargetDate("17-Aug-2011").
        billType(Bill.BillType.Final).
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 6 ======================//

        try {
            bill = runBilling(true);
            assertTrue("No bills are expected after final bill", false);
        } catch (BillingException e) {
        }
    }

    public void testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDay() throws ParseException {
        SysDateManager.setSysDate((Date) null);

        preloadData();
        setLeaseConditions("23-Mar-2011", "3-Aug-2011", null);

        //==================== RUN 1 ======================//

        setLeaseStatus(Lease.Status.Approved);

        Bill bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billingCyclePeriodStartDay(23).
        billingCycleRunTargetDay(8).
        billingRunPeriodStartDate("23-Mar-2011").
        billingRunPeriodEndDate("22-Apr-2011").
        billingRunExecutionTargetDate("8-Mar-2011").
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("22-Apr-2011");
        // @formatter:on

        //==================== RUN 2 ======================//

        setLeaseStatus(Lease.Status.Active);

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billingCyclePeriodStartDay(23).
        billingCycleRunTargetDay(8).
        billingRunPeriodStartDate("23-Apr-2011").
        billingRunPeriodEndDate("22-May-2011").
        billingRunExecutionTargetDate("8-Apr-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("23-Apr-2011").
        billingPeriodEndDate("22-May-2011");
        // @formatter:on

        //==================== RUN 3 ======================//

        bill = runBilling(false);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        previousBillSequenceNumber(2).
        billingCyclePeriodStartDay(23).
        billingCycleRunTargetDay(8).
        billingRunPeriodStartDate("23-May-2011").
        billingRunPeriodEndDate("22-Jun-2011").
        billingRunExecutionTargetDate("8-May-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("23-May-2011").
        billingPeriodEndDate("22-Jun-2011");
        // @formatter:on

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(4).
        previousBillSequenceNumber(2).
        billingCyclePeriodStartDay(23).
        billingCycleRunTargetDay(8).
        billingRunPeriodStartDate("23-May-2011").
        billingRunPeriodEndDate("22-Jun-2011").
        billingRunExecutionTargetDate("8-May-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("23-May-2011").
        billingPeriodEndDate("22-Jun-2011");
        // @formatter:on

        //==================== RUN 4 ======================//

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        previousBillSequenceNumber(4).
        billingCyclePeriodStartDay(23).
        billingCycleRunTargetDay(8).
        billingRunPeriodStartDate("23-Jun-2011").
        billingRunPeriodEndDate("22-Jul-2011").
        billingRunExecutionTargetDate("8-Jun-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("23-Jun-2011").
        billingPeriodEndDate("22-Jul-2011");
        // @formatter:on

        //==================== RUN 5 ======================//

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        previousBillSequenceNumber(5).
        billingCyclePeriodStartDay(23).
        billingCycleRunTargetDay(8).
        billingRunPeriodStartDate("23-Jul-2011").
        billingRunPeriodEndDate("22-Aug-2011").
        billingRunExecutionTargetDate("8-Jul-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("23-Jul-2011").
        billingPeriodEndDate("3-Aug-2011");
        // @formatter:on

        //==================== RUN Final ======================//

        setLeaseStatus(Lease.Status.Completed);

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        previousBillSequenceNumber(6).
        billingCyclePeriodStartDay(23).
        billingCycleRunTargetDay(8).
        billingRunPeriodStartDate("23-Aug-2011").
        billingRunPeriodEndDate("22-Sep-2011").
        billingRunExecutionTargetDate("8-Aug-2011").
        billType(Bill.BillType.Final).
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        //==================== RUN after Final ======================//

        try {
            bill = runBilling(true);
            assertTrue("No bills are expected after final bill", false);
        } catch (BillingException e) {
        }

    }

    public void testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDayOn29() throws ParseException {
        testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDay(29);
    }

    public void testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDayOn30() throws ParseException {
        testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDay(30);
    }

    public void testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDayOn31() throws ParseException {
        testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDay(31);
    }

    public void testSequentialBillingRunWIthLeaseStartDateAsBillingPeriodStartDay(int day) throws ParseException {
        SysDateManager.setSysDate((Date) null);
        preloadData();
        setLeaseConditions(day + "-Mar-2011", "3-Aug-2011", null);

        //==================== RUN 1 ======================//

        setLeaseStatus(Lease.Status.Approved);

        Bill bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("1-Mar-2011").
        billingRunPeriodEndDate("31-Mar-2011").
        billingRunExecutionTargetDate("14-Feb-2011").
        billType(Bill.BillType.First).
        billingPeriodStartDate(day + "-Mar-2011").
        billingPeriodEndDate("31-Mar-2011");
        // @formatter:on

        String billingCycleId = bill.billingRun().billingCycle().id().toString();

        //==================== RUN 2 ======================//

        setLeaseStatus(Lease.Status.Active);

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("1-Apr-2011").
        billingRunPeriodEndDate("30-Apr-2011").
        billingRunExecutionTargetDate("17-Mar-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 3 ======================//

        bill = runBilling(true);

        //==================== RUN 4 ======================//

        bill = runBilling(true);

        //==================== RUN 5 ======================//

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        previousBillSequenceNumber(4).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("1-Jul-2011").
        billingRunPeriodEndDate("31-Jul-2011").
        billingRunExecutionTargetDate("16-Jun-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN 6 ======================//

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        previousBillSequenceNumber(5).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("1-Aug-2011").
        billingRunPeriodEndDate("31-Aug-2011").
        billingRunExecutionTargetDate("17-Jul-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Aug-2011").
        billingPeriodEndDate("3-Aug-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN final ======================//

        setLeaseStatus(Lease.Status.Completed);

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        previousBillSequenceNumber(6).
        billingCyclePeriodStartDay(1).
        billingCycleRunTargetDay(14).
        billingRunPeriodStartDate("1-Sep-2011").
        billingRunPeriodEndDate("30-Sep-2011").
        billingRunExecutionTargetDate("17-Aug-2011").
        billType(Bill.BillType.Final).
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        assertEquals("Same Billing Cycle", billingCycleId, bill.billingRun().billingCycle().id().toString());

        //==================== RUN after final ======================//

        try {
            bill = runBilling(true);
            assertTrue("No bills are expected after final bill", false);
        } catch (BillingException e) {
        }

    }

}
