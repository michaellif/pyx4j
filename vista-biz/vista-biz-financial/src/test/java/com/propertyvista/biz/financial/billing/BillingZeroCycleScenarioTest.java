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
 * Created on Jun 10, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.test.preloader.PreloadConfig;

@Category(FunctionalTests.class)
public class BillingZeroCycleScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreloadConfig config = new PreloadConfig();
        preloadData(config);

    }

    public void testCarryForwardOwingScenario() {

        setLeaseBatchProcess();
        setBillingBatchProcess();

        setDate("17-Mar-2011"); // create existing lease

        // When we create Existing Lease, the tenant is already living in the building
        createLease("03-Mar-2009", "31-Dec-2011", new BigDecimal("900.00"), new BigDecimal("300.00"));

        //==================== CYCLE 1 ======================//

        approveExistingLease(true);

        // @formatter:off
        new BillTester(getBill(1)).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.ZeroCycle).
        billingPeriodStartDate("1-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("0.00").
        serviceCharge("900.00").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("0.00").
        taxes("108.00").
        totalDueAmount("300.00");
        // @formatter:on

        //==================== CYCLE 2 ======================//

        advanceDate("18-Mar-2011");
        receiveAndPostPayment("18-Mar-2011", "300.00");

        advanceDate("22-Mar-2011");
        confirmBill(true, true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("0.00").
        latePaymentFees("50.00"). // fined for 300.00 carry-forward owing
        serviceCharge("900.00").
        depositAmount("0.00").
        taxes("108.00").
        totalDueAmount("1358.00");
        // @formatter:on

        //==================== CYCLE 3 ======================//

        advanceDate("28-Mar-2011");
        receiveAndPostPayment("28-Mar-2011", "1058.00");

        advanceDate("18-Apr-2011");
        confirmBill(true, true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(3).
        previousBillSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("-1358.00").
        latePaymentFees("0.00"). // fined for 300.00 carry-forward owing
        serviceCharge("900.00").
        depositAmount("0.00").
        taxes("108.00").
        totalDueAmount("1008.00");
        // @formatter:on

    }

    public void testCarryForwardOwedScenario() {

        setLeaseBatchProcess();
        setBillingBatchProcess();

        setDate("17-Mar-2011"); // create existing lease

        // try existing lease from just earlier this month
        createLease("03-Mar-2011", "31-Dec-2011", new BigDecimal("900.00"), new BigDecimal("-100.00"));
        addBooking("03-Mar-2011");

        //==================== CYCLE 1 ======================//

        approveExistingLease(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        new BillTester(getBill(1)).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.ZeroCycle).
        billingPeriodStartDate("3-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(2).
        paymentReceivedAmount("0.00").
        serviceCharge("812.90"). // service prorated
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("100.00").
        depositAmount("0.00").
        taxes("109.55").
        totalDueAmount("-100.00");
        // @formatter:on

        //==================== RUN 2 ======================//

        advanceDate("18-Mar-2011");
        confirmBill(true, true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(1).
        serviceCharge("900.00").
        taxes("108.00").
        totalDueAmount("908.00");
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

}
