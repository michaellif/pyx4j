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

import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.RegressionTests;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.Type;
import com.propertyvista.test.mock.MockConfig;

@Category(RegressionTests.class)
@Ignore
public class PadPaymentAmountValidationTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockConfig config = new MockConfig();
        config.billConfirmationMethod = LeaseBillingPolicy.BillConfirmationMethod.automatic;
        preloadData(config);
    }

    public void testScenario() throws Exception {

        setSysDate("31-Mar-2013");

        setBillingBatchProcess();
        setLeaseBatchProcess();
        setDepositBatchProcess();
        setPaymentBatchProcess();

        createLease("1-Apr-2013", "31-Mar-2014", new BigDecimal(977.77), null);
        BillableItem parking = addOutdoorParking("1-Apr-2013", "31-Mar-2014"); // $80
        BillableItem locker = addLargeLocker("1-Apr-2013", "31-Mar-2014"); // $60

        addFeatureAdjustment(parking.uid().getValue(), "-80", Type.monetary, "1-Apr-2013", "31-Mar-2014");
        addFeatureAdjustment(locker.uid().getValue(), "-20", Type.monetary, "1-Apr-2013", "31-Mar-2014");

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

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1139.90"). // 1 locker + 1 month worth of rent + tax
        taxes("122.13").
        totalDueAmount("4600.40"); // 4550.00(previous) + 50(late payment fees)
        // @formatter:on

        advanceSysDate("1-Dec-2013");

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1139.90"). // 1 locker + 1 month worth of rent + tax
        taxes("122.13").
        totalDueAmount("4800.40"); // 4600.40(previous) + 200(4 late payments)
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

        addServiceAdjustment("-80", Type.monetary, "1-Apr-2013", "31-Mar-2014");
        addServiceAdjustment("-20", Type.monetary, "1-Apr-2013", "31-Mar-2014");

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
}