/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 */
package com.propertyvista.biz.financial.billing.internal;

import org.junit.experimental.categories.Category;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.biz.financial.billingcycle.BillingCycleTester;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.portal.rpc.shared.BillingException;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.mock.MockConfig;

@Category(FunctionalTests.class)
public class BillingPeriodsTest extends LeaseFinancialTestBase {

    public void testSequentialBillingCycleWithGlobalBillingPeriodStartDate() throws Exception {
        preloadData();

        setSysDate("01-Jan-2012");

        createLease("23-Mar-2012", "03-Aug-2012");

        setLeaseBatchProcess();
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
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("01-Mar-2012").
        billingCyclePeriodEndDate("31-Mar-2012").
        billingCycleExecutionTargetDate("15-Feb-2012").
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2012").
        billingPeriodEndDate("31-Mar-2012");
        // @formatter:on

        String billingTypeId = bill.billingCycle().billingType().id().toString();

        //==================== RUN 2 ======================//

        advanceSysDate("19-Mar-2012");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("1-Apr-2012").
        billingCyclePeriodEndDate("30-Apr-2012").
        billingCycleExecutionTargetDate("17-Mar-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2012").
        billingPeriodEndDate("30-Apr-2012");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN 3 ======================//

        advanceSysDate("16-Apr-2012");

        bill = runBilling(false);

        // @formatter:off
        new BillingCycleTester(bill.billingCycle()).
        notConfirmedBills(0L).
        failedBills(0L).
        rejectedBills(1L).
        confirmedBills(0L);

        new BillTester(bill).
        billSequenceNumber(3).
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("1-May-2012").
        billingCyclePeriodEndDate("31-May-2012").
        billingCycleExecutionTargetDate("16-Apr-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2012").
        billingPeriodEndDate("31-May-2012").
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
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("1-May-2012").
        billingCyclePeriodEndDate("31-May-2012").
        billingCycleExecutionTargetDate("16-Apr-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2012").
        billingPeriodEndDate("31-May-2012").
        billStatus(Bill.BillStatus.Confirmed);
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN 4 ======================//

        advanceSysDate("18-May-2012");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("1-Jun-2012").
        billingCyclePeriodEndDate("30-Jun-2012").
        billingCycleExecutionTargetDate("17-May-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jun-2012").
        billingPeriodEndDate("30-Jun-2012");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN 5 ======================//

        advanceSysDate("18-Jun-2012");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("1-Jul-2012").
        billingCyclePeriodEndDate("31-Jul-2012").
        billingCycleExecutionTargetDate("16-Jun-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jul-2012").
        billingPeriodEndDate("31-Jul-2012");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN 6 ======================//

        advanceSysDate("18-Jul-2012");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("1-Aug-2012").
        billingCyclePeriodEndDate("31-Aug-2012").
        billingCycleExecutionTargetDate("17-Jul-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Aug-2012").
        billingPeriodEndDate("3-Aug-2012");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN final ======================//

        advanceSysDate("05-Aug-2012");

//-->        completeLease();

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(8).
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("01-Sep-2012").
        billingCyclePeriodEndDate("30-Sep-2012").
        billingCycleExecutionTargetDate("17-Aug-2012").
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

    public void testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDay() throws Exception {

        MockConfig config = new MockConfig();
        config.defaultBillingCycleSartDay = null;
        preloadData(config);

        setSysDate("01-Jan-2012");

        createLease("23-Mar-2012", "03-Aug-2012");

        setLeaseBatchProcess();
        setDepositBatchProcess();
        //==================== RUN 1 ======================//

        Bill bill = approveApplication(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        billingTypePeriodStartDay(23).
        billingCyclePeriodStartDate("23-Mar-2012").
        billingCyclePeriodEndDate("22-Apr-2012").
        billingCycleExecutionTargetDate("08-Mar-2012").
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2012").
        billingPeriodEndDate("22-Apr-2012");
        // @formatter:on

        //==================== RUN 2 ======================//

        advanceSysDate("10-Apr-2012");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        billingTypePeriodStartDay(23).
        billingCyclePeriodStartDate("23-Apr-2012").
        billingCyclePeriodEndDate("22-May-2012").
        billingCycleExecutionTargetDate("08-Apr-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("23-Apr-2012").
        billingPeriodEndDate("22-May-2012");
        // @formatter:on

        //==================== RUN 3 ======================//

        advanceSysDate("10-May-2012");

        bill = runBilling(false);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        billingTypePeriodStartDay(23).
        billingCyclePeriodStartDate("23-May-2012").
        billingCyclePeriodEndDate("22-Jun-2012").
        billingCycleExecutionTargetDate("08-May-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("23-May-2012").
        billingPeriodEndDate("22-Jun-2012");
        // @formatter:on

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(4).
        billingTypePeriodStartDay(23).
        billingCyclePeriodStartDate("23-May-2012").
        billingCyclePeriodEndDate("22-Jun-2012").
        billingCycleExecutionTargetDate("08-May-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("23-May-2012").
        billingPeriodEndDate("22-Jun-2012");
        // @formatter:on

        //==================== RUN 4 ======================//

        advanceSysDate("10-Jun-2012");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        billingTypePeriodStartDay(23).
        billingCyclePeriodStartDate("23-Jun-2012").
        billingCyclePeriodEndDate("22-Jul-2012").
        billingCycleExecutionTargetDate("08-Jun-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("23-Jun-2012").
        billingPeriodEndDate("22-Jul-2012");
        // @formatter:on

        //==================== RUN 5 ======================//

        advanceSysDate("10-Jul-2012");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        billingTypePeriodStartDay(23).
        billingCyclePeriodStartDate("23-Jul-2012").
        billingCyclePeriodEndDate("22-Aug-2012").
        billingCycleExecutionTargetDate("08-Jul-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("23-Jul-2012").
        billingPeriodEndDate("3-Aug-2012");
        // @formatter:on

        //==================== RUN Final ======================//

        advanceSysDate("10-Aug-2012");
//-->        completeLease();

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        billingTypePeriodStartDay(23).
        billingCyclePeriodStartDate("23-Aug-2012").
        billingCyclePeriodEndDate("22-Sep-2012").
        billingCycleExecutionTargetDate("08-Aug-2012").
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

    public void testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDayOn29() throws Exception {
        testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDay(29);
    }

    public void testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDayOn30() throws Exception {
        testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDay(30);
    }

    public void testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDayOn31() throws Exception {
        testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDay(31);
    }

    public void testSequentialBillingCycleWIthLeaseStartDateAsBillingPeriodStartDay(int day) throws Exception {

        MockConfig config = new MockConfig();
        config.defaultBillingCycleSartDay = null;
        preloadData(config);

        setSysDate("01-Jan-2012");

        createLease(day + "-Mar-2012", "03-Aug-2012");

        setLeaseBatchProcess();
        setDepositBatchProcess();
        //==================== RUN 1 ======================//

        Bill bill = approveApplication(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("1-Mar-2012").
        billingCyclePeriodEndDate("31-Mar-2012").
        billingCycleExecutionTargetDate("15-Feb-2012").
        billType(Bill.BillType.First).
        billingPeriodStartDate(day + "-Mar-2012").
        billingPeriodEndDate("31-Mar-2012");
        // @formatter:on

        String billingTypeId = bill.billingCycle().billingType().id().toString();

        //==================== RUN 2 ======================//

        advanceSysDate("18-Mar-2012");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("1-Apr-2012").
        billingCyclePeriodEndDate("30-Apr-2012").
        billingCycleExecutionTargetDate("17-Mar-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2012").
        billingPeriodEndDate("30-Apr-2012");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN 3 ======================//

        advanceSysDate("18-Apr-2012");
        bill = runBilling(true);

        //==================== RUN 4 ======================//

        advanceSysDate("18-May-2012");
        bill = runBilling(true);

        //==================== RUN 5 ======================//

        advanceSysDate("18-Jun-2012");
        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("1-Jul-2012").
        billingCyclePeriodEndDate("31-Jul-2012").
        billingCycleExecutionTargetDate("16-Jun-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jul-2012").
        billingPeriodEndDate("31-Jul-2012");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN 6 ======================//

        advanceSysDate("18-Jul-2012");
        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("1-Aug-2012").
        billingCyclePeriodEndDate("31-Aug-2012").
        billingCycleExecutionTargetDate("17-Jul-2012").
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Aug-2012").
        billingPeriodEndDate("3-Aug-2012");
        // @formatter:on

        assertEquals("Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        //==================== RUN final ======================//

        advanceSysDate("18-Aug-2012");
//-->        completeLease();

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        billingTypePeriodStartDay(1).
        billingCyclePeriodStartDate("1-Sep-2012").
        billingCyclePeriodEndDate("30-Sep-2012").
        billingCycleExecutionTargetDate("17-Aug-2012").
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
