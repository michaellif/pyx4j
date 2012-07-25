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

import org.junit.experimental.categories.Category;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.preload.PreloadConfig;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.portal.rpc.shared.BillingException;

@Category(FunctionalTests.class)
public class BillingPeriodsTest extends FinancialTestBase {

    public void testSequentialBillingCycleWithGlobalBillingPeriodStartDate() throws ParseException {
        preloadData();

        setLeaseTerms("23-Mar-2011", "3-Aug-2011");

        //==================== RUN 1 ======================//

        Bill bill = approveApplication(true);

        // @formatter:off
        new BillingCycleTester(bill.billingCycle()).
        notConfirmedBills(0L).
        failedBills(0L).
        rejectedBills(0L).
        confirmedBills(1L);

        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("01-Mar-2011").
        billingCyclePeriodEndDate("31-Mar-2011").
        billingCycleExecutionTargetDate("14-Feb-2011").
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011");
        // @formatter:on

        String billingTypeId = bill.billingCycle().billingType().id().toString();

        //==================== RUN 2 ======================//

        activateLease();

        SysDateManager.setSysDate("19-Mar-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("1-Apr-2011").
        billingCyclePeriodEndDate("30-Apr-2011").
        billingCycleExecutionTargetDate("17-Mar-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN 3 ======================//

        SysDateManager.setSysDate("16-Apr-2011");

        bill = runBilling(false);

        // @formatter:off
        new BillingCycleTester(bill.billingCycle()).
        notConfirmedBills(0L).
        failedBills(0L).
        rejectedBills(1L).
        confirmedBills(0L);

        new BillTester(bill).
        billSequenceNumber(3).
        previousBillSequenceNumber(2).
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("1-May-2011").
        billingCyclePeriodEndDate("31-May-2011").
        billingCycleExecutionTargetDate("16-Apr-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        billStatus(Bill.BillStatus.Rejected);
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        bill = runBilling(true);

        // @formatter:off
        new BillingCycleTester(bill.billingCycle()).
        notConfirmedBills(0L).
        failedBills(0L).
        rejectedBills(0L).
        confirmedBills(1L);

        new BillTester(bill).
        billSequenceNumber(4).
        previousBillSequenceNumber(2).
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("1-May-2011").
        billingCyclePeriodEndDate("31-May-2011").
        billingCycleExecutionTargetDate("16-Apr-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        billStatus(Bill.BillStatus.Confirmed);
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN 4 ======================//

        SysDateManager.setSysDate("18-May-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        previousBillSequenceNumber(4).
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("1-June-2011").
        billingCyclePeriodEndDate("30-June-2011").
        billingCycleExecutionTargetDate("17-May-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-June-2011").
        billingPeriodEndDate("30-June-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN 5 ======================//

        SysDateManager.setSysDate("18-Jun-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        previousBillSequenceNumber(5).
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("1-July-2011").
        billingCyclePeriodEndDate("31-July-2011").
        billingCycleExecutionTargetDate("16-Jun-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-July-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN 6 ======================//

        SysDateManager.setSysDate("18-Jul-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        previousBillSequenceNumber(6).
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("1-Aug-2011").
        billingCyclePeriodEndDate("31-Aug-2011").
        billingCycleExecutionTargetDate("17-Jul-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Aug-2011").
        billingPeriodEndDate("3-Aug-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN final ======================//

        SysDateManager.setSysDate("05-Aug-2011");

        completeLease();

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(8).
        previousBillSequenceNumber(7).
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("01-Sep-2011").
        billingCyclePeriodEndDate("30-Sep-2011").
        billingCycleExecutionTargetDate("17-Aug-2011").
        billType(Bill.BillType.Final).
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        closeLease();

        //==================== RUN 6 ======================//

        try {
            bill = runBilling(true);
            assertTrue("No bills are expected after final bill", false);
        } catch (BillingException e) {
        }
    }

    public void testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDay() throws ParseException {
        SysDateManager.setSysDate((Date) null);

        PreloadConfig config = new PreloadConfig();
        config.defaultBillingCycleSartDay = null;
        preloadData(config);

        setDate("01-Jan-2012");

        setLeaseTerms("23-Mar-2011", "3-Aug-2011");

        //==================== RUN 1 ======================//

        Bill bill = approveApplication(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billingTypePeriodStartDay(23).
        billingTypePeriodTargetDay(8).
        billingCyclePeriodStartDate("23-Mar-2011").
        billingCyclePeriodEndDate("22-Apr-2011").
        billingCycleExecutionTargetDate("8-Mar-2011").
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("22-Apr-2011");
        // @formatter:on

        //==================== RUN 2 ======================//

        activateLease();

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billingTypePeriodStartDay(23).
        billingTypePeriodTargetDay(8).
        billingCyclePeriodStartDate("23-Apr-2011").
        billingCyclePeriodEndDate("22-May-2011").
        billingCycleExecutionTargetDate("8-Apr-2011").
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
        billingTypePeriodStartDay(23).
        billingTypePeriodTargetDay(8).
        billingCyclePeriodStartDate("23-May-2011").
        billingCyclePeriodEndDate("22-Jun-2011").
        billingCycleExecutionTargetDate("8-May-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("23-May-2011").
        billingPeriodEndDate("22-Jun-2011");
        // @formatter:on

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(4).
        previousBillSequenceNumber(2).
        billingTypePeriodStartDay(23).
        billingTypePeriodTargetDay(8).
        billingCyclePeriodStartDate("23-May-2011").
        billingCyclePeriodEndDate("22-Jun-2011").
        billingCycleExecutionTargetDate("8-May-2011").
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
        billingTypePeriodStartDay(23).
        billingTypePeriodTargetDay(8).
        billingCyclePeriodStartDate("23-Jun-2011").
        billingCyclePeriodEndDate("22-Jul-2011").
        billingCycleExecutionTargetDate("8-Jun-2011").
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
        billingTypePeriodStartDay(23).
        billingTypePeriodTargetDay(8).
        billingCyclePeriodStartDate("23-Jul-2011").
        billingCyclePeriodEndDate("22-Aug-2011").
        billingCycleExecutionTargetDate("8-Jul-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("23-Jul-2011").
        billingPeriodEndDate("3-Aug-2011");
        // @formatter:on

        //==================== RUN Final ======================//

        completeLease();

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        previousBillSequenceNumber(6).
        billingTypePeriodStartDay(23).
        billingTypePeriodTargetDay(8).
        billingCyclePeriodStartDate("23-Aug-2011").
        billingCyclePeriodEndDate("22-Sep-2011").
        billingCycleExecutionTargetDate("8-Aug-2011").
        billType(Bill.BillType.Final).
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        closeLease();

        //==================== RUN after Final ======================//

        try {
            bill = runBilling(true);
            assertTrue("No bills are expected after final bill", false);
        } catch (BillingException e) {
        }

    }

    public void testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDayOn29() throws ParseException {
        testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDay(29);
    }

    public void testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDayOn30() throws ParseException {
        testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDay(30);
    }

    public void testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDayOn31() throws ParseException {
        testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDay(31);
    }

    public void testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDay(int day) throws ParseException {

        PreloadConfig config = new PreloadConfig();
        config.defaultBillingCycleSartDay = null;
        preloadData(config);

        setDate("01-Jan-2012");

        setLeaseTerms(day + "-Mar-2011", "3-Aug-2011");

        //==================== RUN 1 ======================//

        Bill bill = approveApplication(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("1-Mar-2011").
        billingCyclePeriodEndDate("31-Mar-2011").
        billingCycleExecutionTargetDate("14-Feb-2011").
        billType(Bill.BillType.First).
        billingPeriodStartDate(day + "-Mar-2011").
        billingPeriodEndDate("31-Mar-2011");
        // @formatter:on

        String billingTypeId = bill.billingCycle().billingType().id().toString();

        //==================== RUN 2 ======================//

        activateLease();

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("1-Apr-2011").
        billingCyclePeriodEndDate("30-Apr-2011").
        billingCycleExecutionTargetDate("17-Mar-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

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
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("1-Jul-2011").
        billingCyclePeriodEndDate("31-Jul-2011").
        billingCycleExecutionTargetDate("16-Jun-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN 6 ======================//

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        previousBillSequenceNumber(5).
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("1-Aug-2011").
        billingCyclePeriodEndDate("31-Aug-2011").
        billingCycleExecutionTargetDate("17-Jul-2011").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Aug-2011").
        billingPeriodEndDate("3-Aug-2011");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN final ======================//

        completeLease();

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        previousBillSequenceNumber(6).
        billingTypePeriodStartDay(1).
        billingTypePeriodTargetDay(14).
        billingCyclePeriodStartDate("1-Sep-2011").
        billingCyclePeriodEndDate("30-Sep-2011").
        billingCycleExecutionTargetDate("17-Aug-2011").
        billType(Bill.BillType.Final).
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        closeLease();

        //==================== RUN after final ======================//

        try {
            bill = runBilling(true);
            assertTrue("No bills are expected after final bill", false);
        } catch (BillingException e) {
        }

    }

}
