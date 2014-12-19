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
public class PadPaymentFixedAmountValidationTest extends LeaseFinancialTestBase {

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

        setPreauthorizedPayment("2000.00");

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
        paymentReceivedAmount("-2000.00").
        taxes("122.13").
        totalDueAmount("3779.90"); // 4590.00(previous) + 1139.90(monthly charges) + 50(late payment fees) - 2000.00(received amount)
        // @formatter:on

        advanceSysDate("1-Aug-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-2000.00").
        taxes("122.13").
        totalDueAmount("2969.80"); // 3779.90(previous) + 1139.90(monthly charges) + 50(late payment fees) - 2000.00(received amount)
        // @formatter:on

        advanceSysDate("1-Oct-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-2000.00").
        latePaymentFees("50.00").
        taxes("122.13").
        totalDueAmount("1349.60"); // 2969.80(previous) + 1139.90(monthly charges) * 2 + 50(late payment fees) * 2 - 2000.00(received amount) * 2
        // @formatter:on

        advanceSysDate("1-Nov-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-2000.00").
        latePaymentFees("0.00").
        taxes("122.13").
        totalDueAmount("489.50"); // 1349.60(previous) + 1139.90(monthly charges) - 2000.00(received amount)
        // @formatter:on

        advanceSysDate("1-Dec-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-2000.00").
        taxes("122.13").
        totalDueAmount("-370.60"); // 489.50(previous) + 1139.90(monthly charges) - 2000.00(received amount)
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario2() throws Exception {
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

        setPreauthorizedPayment("1139.90");

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
        paymentReceivedAmount("-1139.90"). // locker + 1 month worth of rent + tax
        taxes("122.13").
        totalDueAmount("4640.00"); // 4590.00(previous) + 50(late payment fees)
        // @formatter:on

        advanceSysDate("1-Aug-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1139.90"). // locker + 1 month worth of rent + tax
        taxes("122.13").
        totalDueAmount("4690.00"); // 4640.00(previous) + 50(late payment fees)
        // @formatter:on

        advanceSysDate("2-Dec-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1139.90"). // locker + 1 month worth of rent + tax
        taxes("122.13").
        totalDueAmount("4890.00"); //4690.00(previous) + 200(4 months late fees)
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario3() throws Exception {
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

        setPreauthorizedPayment("376.16"); // 1139.90 * .33

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
        paymentReceivedAmount("-376.16").
        taxes("122.13").
        totalDueAmount("5403.74"); // 4590.00(previous) + 1139.90(monthly charges) + 50(late payment fees) - 376.16(received amount)
        // @formatter:on

        setPreauthorizedPayment("3897.48");

        advanceSysDate("1-Aug-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-376.16").
        taxes("122.13").
        totalDueAmount("6217.48"); // 5403.74(previous) + 1139.90(monthly charges) + 50(late payment fees) - 376.16(received amount)
        // @formatter:on

        advanceSysDate("1-Sep-2013");
        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-4273.64").
        taxes("122.13").
        totalDueAmount("3133.74"); // 6217.48(previous) + 1139.90(monthly charges) + 50(late payment fees) - 4273.64(received amount)
        // @formatter:on

        // TODO add ability to cancel/edit PAP for tests, set monthly payment to 1139.90, add the commented-out test back
//        setPreauthorizedPayment("-3133.74"); // totaling 1139.90 now

        advanceSysDate("1-Oct-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-4273.64").
        taxes("122.13").
        totalDueAmount("0.00"); // 3133.74(previous) + 1139.90(monthly charges) - 4273.64(received amount)
        // @formatter:on
        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        advanceSysDate("3-Dec-2013");

        // @formatter:off
//        new BillTester(getLatestBill()).
//        paymentReceivedAmount("-1139.90").
//        taxes("122.13").
//        totalDueAmount("0.00");
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

        advanceSysDate("1-Apr-2013");

        setPreauthorizedPayment("50");

        advanceSysDate("1-Jun-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-50.00").
        taxes("0.00").
        totalDueAmount("1060.30"); // 930.30(deposit) + 80(locker deposit) + 100(2 * late payment fees) - 50 (one payment)
        // @formatter:on

        advanceSysDate("4-Dec-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-50.00").
        taxes("0.00").
        totalDueAmount("1060.30"); //1110.30(previous)
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
    }
}