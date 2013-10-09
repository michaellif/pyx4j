/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import org.junit.experimental.categories.Category;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;
import com.propertyvista.test.integration.PaymentRecordTester;
import com.propertyvista.test.integration.PreauthorizedPaymentBuilder;

@Category(RegressionTests.class)
public class PadPaymentChargeBaseSunnyDayScenarioTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        setSysDate("10-Mar-2011");

        createLease("1-Apr-2011", "31-Mar-2012");
        BillableItem parking = addOutdoorParking();
        BillableItem largeLocker = addLargeLocker();

        setBillingBatchProcess();
        setLeaseBatchProcess();
        setDepositBatchProcess();
        setPaymentBatchProcess();

        advanceSysDate("12-Mar-2011");

        approveApplication(true);

        assertEquals("PAD next target date", new LogicalDate(DateUtils.detectDateformat("01-Apr-2011")), getNextTargetPadExecutionDate());

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(1).
        billType(Bill.BillType.First).
        billStatus(Bill.BillStatus.Confirmed).
        billingCyclePeriodStartDate("01-Apr-2011").
        billingPeriodStartDate("01-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(3).
        paymentReceivedAmount("0.00").
        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        oneTimeFeatureCharges("0.00").
        taxes("128.44").
        totalDueAmount("2269.04");
        // @formatter:on

        advanceSysDate("20-Mar-2011");

        receiveAndPostPayment("20-Mar-2011", eval("2269.04 /*DueAmount*/- 1198.74 /*pap*/")); // DueAmount - Pad = 1070.30

        // PAD will be triggered at the end of this month
        setPreauthorizedPayment(new PreauthorizedPaymentBuilder(). //
                add(getLease().currentTerm().version().leaseProducts().serviceItem(), "1041.94"). // 930.30 + 12%
                add(parking, "89.60"). // 80.00 + 12%
                add(largeLocker, "67.20"). // 60.00 + 12%
                build());

        advanceSysDate("1-Apr-2011");
        // Expect PAD executed, verify amount
        new PaymentRecordTester(getLease().billingAccount()).count(2). //
                lastRecordStatus(PaymentStatus.Queued).lastRecordAmount("1198.74");

        assertEquals("PAD next target date", DateUtils.detectDateformat("01-May-2011"), getNextTargetPadExecutionDate());

        advanceSysDate("18-Apr-2011");

        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billStatus(Bill.BillStatus.Confirmed).
        billingCyclePeriodStartDate("01-May-2011").
        billingPeriodStartDate("01-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(3).
        paymentReceivedAmount(eval("-(930.30 + 80 + 60 + 128.44 /*tax*/) - (2269.04 - 1198.74)")).

        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        oneTimeFeatureCharges("0.00").
        taxes("128.44").
        totalDueAmount(eval("1198.74"));
        // @formatter:on

        // we are in the april cycle and pad date is 3 days before Apr 1
        assertEquals("PAD target date", DateUtils.detectDateformat("29-Mar-2011"), getTargetPadGenerationDate());
        // the pad for Apr 1 has been executed and the next one is for May 1
        assertEquals("PAD execution date", DateUtils.detectDateformat("29-Mar-2011"), getActualPadGenerationDate());
        assertEquals("PAD next target date", DateUtils.detectDateformat("01-May-2011"), getNextTargetPadExecutionDate());
        // if pad did not run - still return the old date for the cycle
        setSysDate("01-May-2011");
        assertEquals("PAD next target date", DateUtils.detectDateformat("01-Jun-2011"), getNextTargetPadExecutionDate());
        // now roll time back and run pad - should see the date for next cycle
        setSysDate("27-Apr-2011");

        advanceSysDate("01-May-2011");
        // Expect PAD executed,
        assertEquals("PAD next target date", DateUtils.detectDateformat("01-Jun-2011"), getNextTargetPadExecutionDate());
        new PaymentRecordTester(getLease().billingAccount()).count(3). //
                lastRecordStatus(PaymentStatus.Queued).lastRecordAmount("1198.74");

        advanceSysDate("18-May-2011");

        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billStatus(Bill.BillStatus.Confirmed).
        billingCyclePeriodStartDate("01-Jun-2011").
        billingPeriodStartDate("1-Jun-2011").
        billingPeriodEndDate("30-Jun-2011").
        numOfProductCharges(3).
        paymentReceivedAmount("-1198.74").
        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        oneTimeFeatureCharges("0.00").
        taxes("128.44").
        totalDueAmount("1198.74");
        // @formatter:on

        // This will update PAP but the amount will not be added
        addBooking("25-May-2011"); // 30.00
        finalizeLeaseAdendum();

        advanceSysDate("18-Jun-2011");

        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(4).
        billType(Bill.BillType.Regular).
        billStatus(Bill.BillStatus.Confirmed).
        billingCyclePeriodStartDate("1-Jul-2011").
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        numOfProductCharges(4).
        paymentReceivedAmount("-1198.74").
        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        oneTimeFeatureCharges("30.00").
        taxes("132.04").
        totalDueAmount("1232.34");
        // @formatter:on

        advanceSysDate("18-Jul-2011");

        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(5).
        billType(Bill.BillType.Regular).
        billStatus(Bill.BillStatus.Confirmed).
        billingCyclePeriodStartDate("1-Aug-2011").
        billingPeriodStartDate("1-Aug-2011").
        billingPeriodEndDate("31-Aug-2011").
        numOfProductCharges(3).
        paymentReceivedAmount("-1198.74").
        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        oneTimeFeatureCharges("0.00").
        latePaymentFees("50.00"). // late fee for booking charge overdue
        taxes("128.44").
        totalDueAmount("1282.34");
        // @formatter:on

        receiveAndPostPayment("18-Jul-2011", "83.60");

        advanceSysDate("18-Aug-2011");

        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(6).
        billType(Bill.BillType.Regular).
        billStatus(Bill.BillStatus.Confirmed).
        billingCyclePeriodStartDate("1-Sep-2011").
        billingPeriodStartDate("1-Sep-2011").
        billingPeriodEndDate("30-Sep-2011").
        numOfProductCharges(3).
        paymentReceivedAmount("-1282.34").
        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        oneTimeFeatureCharges("0.00").
        taxes("128.44").
        totalDueAmount("1198.74");
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
}