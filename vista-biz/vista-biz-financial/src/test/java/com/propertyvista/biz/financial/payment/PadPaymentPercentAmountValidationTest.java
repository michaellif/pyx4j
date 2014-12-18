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
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;
import com.propertyvista.test.mock.MockConfig;

@Ignore
@Category(RegressionTests.class)
public class PadPaymentPercentAmountValidationTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockConfig config = new MockConfig();
        config.billConfirmationMethod = LeaseBillingPolicy.BillConfirmationMethod.automatic;
        preloadData(config);
    }

    public void testScenario1() throws Exception {

        setSysDate("31-Mar-2013");

        setBillingBatchProcess();
        setLeaseBatchProcess();
        setDepositBatchProcess();
        setPaymentBatchProcess();

        createLease("1-Apr-2013", "31-Mar-2014", new BigDecimal(977.77), null);
        BillableItem parking = addOutdoorParking("1-Apr-2013", "31-Mar-2014"); // $80
        BillableItem locker = addLargeLocker("1-Apr-2013", "31-Mar-2014"); // $60

        addFeatureAdjustment(parking.uuid().getValue(), "-80", ValueType.Monetary, "1-Apr-2013", "31-Mar-2014");
        addFeatureAdjustment(locker.uuid().getValue(), "-20", ValueType.Monetary, "1-Apr-2013", "31-Mar-2014");

        approveApplication(true);

        advanceSysDate("1-Apr-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("0.00").
        taxes("122.13").
        totalDueAmount("2210.20"); // 1070.30(deposit) + 977.77(lease) + 122.13(tax) + 140(locker + parking) - 100(adjustments)
        // @formatter:on

        advanceSysDate("1-May-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("0.00").
        taxes("122.13").
        totalDueAmount("3400.10"); // 2210.20(previous) + 1139.90(monthly charges) + 50(late payment fees)
        // @formatter:on

        setPreauthorizedPayment("1");

        advanceSysDate("1-Jun-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("0.00").
        taxes("122.13").
        totalDueAmount("4590.00"); // 3400.10(previous) + 1139.90(monthly charges) + 50(late payment fees)
        // @formatter:on

        advanceSysDate("1-Jul-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1229.50"). // 3 lockers + 1 month worth of rent + tax
        taxes("122.13").
        totalDueAmount("4550.40"); // 4590.00(previous) + 1139.90(monthly charges) + 50(late payment fees) - 1229.50(received amount)
        // @formatter:on

        advanceSysDate("1-Aug-2013");

//        // @formatter:off
//        new BillTester(getLatestBill()).
//        paymentReceivedAmount("-1139.90"). // 1 locker + 1 month worth of rent + tax
//        taxes("122.13").
//        totalDueAmount("4600.40"); // 4550.00(previous) + 50(late payment fees)
//        // @formatter:on

        advanceSysDate("1-Dec-2013");

//        // @formatter:off
//        new BillTester(getLatestBill()).
//        paymentReceivedAmount("-1139.90"). // 1 locker + 1 month worth of rent + tax
//        taxes("122.13").
//        totalDueAmount("4800.40"); // 4600.40(previous) + 200(4 late payments)
//        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    //TODO yuriyl
    public void OFF_testScenario2() throws Exception {
        setSysDate("31-Mar-2013");

        setBillingBatchProcess();
        setLeaseBatchProcess();
        setDepositBatchProcess();
        setPaymentBatchProcess();

        createLease("1-Apr-2013", "31-Mar-2014", new BigDecimal(977.77), null);
        addOutdoorParking("1-Apr-2013", "31-Mar-2014"); // $80
        addLargeLocker("1-Apr-2013", "31-Mar-2014"); // $60

        addServiceAdjustment("-80", ValueType.Monetary, "1-Apr-2013", "31-Mar-2014");
        addServiceAdjustment("-20", ValueType.Monetary, "1-Apr-2013", "31-Mar-2014");

        approveApplication(true);

        advanceSysDate("1-Apr-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("0.00").
        taxes("122.13").
        totalDueAmount("2210.20"); // 1070.30(deposit) + 977.77(lease) + 122.13(tax) + 140(locker + parking) - 100(adjustments) = 2210.20
        // @formatter:on

        advanceSysDate("1-May-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("0.00").
        taxes("122.13").
        totalDueAmount("3400.10"); // 2210.20(previous) + 1139.90(monthly charges) + 50(late payment fees) = 3400.10
        // @formatter:on

        setPreauthorizedPayment("1");

        advanceSysDate("1-Jun-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("0.00").
        taxes("122.13").
        totalDueAmount("4590.00"); // 3400.10(previous) + 1139.90(monthly charges) + 50(late payment fees) = 4590.00
        // @formatter:on

        advanceSysDate("1-Jul-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1453.50"). // 3 lockers + 1 month worth of rent + tax
        taxes("122.13").
        totalDueAmount("4326.40"); // 4590.00(previous) + 1139.90(monthly charges) + 50(late payment fees) - 1453.50(received amount)
        // @formatter:on

        advanceSysDate("1-Aug-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("1139.90"). // locker + 1 month worth of rent + tax
        taxes("122.13").
        totalDueAmount("4376.40"); // 4326.40(previous) + 50(late payment fees)
        // @formatter:on

        advanceSysDate("2-Dec-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1139.90"). // locker + 1 month worth of rent + tax
        taxes("122.13").
        totalDueAmount("4576.40"); //4376.40(previous) + 200(4 months late fees)
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
    }

    public void testScenario4() throws Exception {
        setSysDate("31-Mar-2013");

        setBillingBatchProcess();
        setLeaseBatchProcess();
        setDepositBatchProcess();
        setPaymentBatchProcess();

        createLease("1-Apr-2013", "31-Mar-2014", new BigDecimal(0), null);
        BillableItem parking = addOutdoorParking("1-Apr-2013", "31-Mar-2014"); // $80

        addFeatureAdjustment(parking.uuid().getValue(), "-80", ValueType.Monetary, "1-Apr-2013", "31-Mar-2014");

        approveApplication(true);

        setPreauthorizedPayment("1");

        advanceSysDate("1-Jun-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("0.00").
        taxes("0.00").
        totalDueAmount("1110.30"); // 930.30(deposit) + 80(locker deposit) + 100(2 * late payment fees)
        // @formatter:on

        advanceSysDate("4-Dec-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("0.00").
        taxes("0.00").
        totalDueAmount("1410.30"); //1110.30(previous) + 300(6 months late fees)
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
    }

    public void testScenario5() throws Exception {
        setSysDate("25-Mar-2013");

        setBillingBatchProcess();
        setLeaseBatchProcess();
        setDepositBatchProcess();
        setPaymentBatchProcess();

        createLease("1-Apr-2013", "31-Mar-2014", new BigDecimal(977.77), null);
        BillableItem parking = addOutdoorParking("1-Apr-2013", "31-Mar-2014"); // $80
        BillableItem cat = addCat("1-Apr-2013", "31-Jul-2013"); // $200 deposit, $20
        BillableItem locker = addLargeLocker("1-May-2013", "31-Aug-2013"); // $60
        addGoodWillCredit("10.00");
        addFeatureAdjustment(parking.uuid().getValue(), "-20", ValueType.Monetary, "1-Apr-2013", "31-Mar-2014");
        addFeatureAdjustment(cat.uuid().getValue(), "-10", ValueType.Monetary, "1-Apr-2013", "30-Jun-2013");
        addFeatureAdjustment(locker.uuid().getValue(), "-35", ValueType.Monetary, "1-Jun-2013", "31-Aug-2013");

        approveApplication(true);

        setPreauthorizedPayment("1");

        advanceSysDate("1-Apr-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("0.00").
        taxes("125.73").
        totalDueAmount("2373.80"); // 930.30(deposit) + 280(item deposits) + 977.77(rent) + 70(billable items) + tax
        // @formatter:on

        advanceSysDate("1-May-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1152.30"). // 977.77 + 60 (cat isn't included in pap policy) + tax - 10 (goodwill credit)
        taxes("132.93").
        totalDueAmount("2572.20"); // 2383.80(previous) + 60(locker deposit) + 977.77(rent) + 130(billable items) + tax + 50(late payment fee) - 1162.30
        // @formatter:on

        advanceSysDate("1-Jun-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1229.50"). // 977.77 + 120 + tax
        taxes("128.73").
        totalDueAmount("2594.20"); // 2572.20(previous) + 977.77(rent) + 95(billable items) + tax + 50(late payment fee) - 1229.50
        // @formatter:on

        advanceSysDate("1-Jul-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1190.30"). // 977.77 + 85 + tax
        taxes("129.93").
        totalDueAmount("2666.60"); // 2594.20(previous) + 977.77(rent) + 105(billable items) + tax + 50(late payment fee) - 1190.30
        // @formatter:on

        advanceSysDate("1-Aug-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1190.30"). // 977.77 + 85 + tax
        taxes("127.53").
        totalDueAmount("2716.60"); // 2666.60(previous) + 977.77(rent) + 85(billable items) + tax + 50(late payment fee) - 1190.30
        // @formatter:on

        advanceSysDate("5-Dec-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1162.30"). // 977.77(rent) + 60(billable items) + tax
        taxes("124.53").
        totalDueAmount("2618.04"); //2716.60(previous) + 1162.30 (September charge) + 50(late fee) * 4 - 208.12(cat deposit + %) - 62.44(locker deposit + %) - 1190.30(September payment)
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
    }
}