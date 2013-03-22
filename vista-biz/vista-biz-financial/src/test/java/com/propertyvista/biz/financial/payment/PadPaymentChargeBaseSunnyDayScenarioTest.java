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

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.RegressionTests;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.billing.Bill;

@Category(RegressionTests.class)
public class PadPaymentChargeBaseSunnyDayScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        setSysDate("10-Mar-2011");

        createLease("1-Apr-2011", "31-Mar-2012");
        addParking();
        addLocker();

        setBillingBatchProcess();
        setLeaseBatchProcess();
        setDepositBatchProcess();
        setPaymentBatchProcess();

        advanceSysDate("12-Mar-2011");

        approveApplication(true);

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

        receiveAndPostPayment("20-Mar-2011", "2269.04"); //2269.04 - 1198.74

        setPreauthorizedPayment(new BigDecimal("1"));

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
        paymentReceivedAmount("-2269.04").
        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        oneTimeFeatureCharges("0.00").
        taxes("128.44").
        totalDueAmount("1198.74");
        // @formatter:on

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

        addBooking("25-May-2011");
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
        oneTimeFeatureCharges("100.00").
        taxes("140.44").
        totalDueAmount("1310.74");
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
        taxes("128.44").
        totalDueAmount("1360.74");
        // @formatter:on

        receiveAndPostPayment("18-Jul-2011", "162.00");

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
        //TODO AR submitted part of one time payment for booking and late payment against lease (lease is oldest arrears)
        //paymentReceivedAmount("-1360.74").
        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        oneTimeFeatureCharges("0.00").
        taxes("128.44");
        //TODO see above 
        //totalDueAmount("1198.74");
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
}