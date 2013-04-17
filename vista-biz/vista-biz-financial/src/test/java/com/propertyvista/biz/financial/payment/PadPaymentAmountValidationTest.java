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
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.Type;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.mock.MockConfig;

@Category(RegressionTests.class)
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
        addOutdoorParking("1-Apr-2013", "31-Mar-2014"); // $80
        addLargeLocker("1-Apr-2013", "31-Mar-2014"); // $60

        Lease lease = getLease();
        addServiceAdjustment("-80", Type.monetary, "1-Apr-2013", "31-Mar-2014");
        addServiceAdjustment("-20", Type.monetary, "1-Apr-2013", "31-Mar-2014");

        approveApplication(true);

        advanceSysDate("1-Apr-2013");

        Bill bill = getLatestBill();

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("0.00").
        taxes("122.13").
        totalDueAmount("2210.20"); // 1070.30(deposit) + 977.77(lease) + 122.13(tax) + 140(locker + parking) - 100(adjustments) = 2210.20
        // @formatter:on

        advanceSysDate("1-May-2013");

        bill = getLatestBill();

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("0.00").
        taxes("122.13").
        totalDueAmount("3400.10"); // 2210.20(previous) + 1139.90(monthly charges and adjustments all in one) + 50(late payment fees) = 3400.10
        // @formatter:on

        setPreauthorizedPayment("1");

        advanceSysDate("1-Jun-2013");

        bill = getLatestBill();

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("0.00").
        taxes("122.13").
        totalDueAmount("4590.00"); // 3400.10(previous) + 1139.90(monthly charges and adjustments all in one) + 50(late payment fees) = 4590.00
        // @formatter:on

        advanceSysDate("1-Jul-2013");

        bill = getLatestBill();

        // @formatter:off
        new BillTester(getLatestBill()).
        paymentReceivedAmount("-1453.50"). // why?
        taxes("122.13").
        totalDueAmount("4326.40"); // 4590.00(previous) + 1139.90(monthly charges and adjustments all in one) + 50(late payment fees) - ....
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
}