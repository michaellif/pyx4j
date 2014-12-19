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
 */
package com.propertyvista.biz.financial.billing.internal;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class BillingLatePaymentScenarioTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        createLease("01-Apr-2011", "31-Oct-2011");

        //==================== First Bill ======================//

        setSysDate("17-Mar-2011");
        Bill bill = approveApplication(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        billType(Bill.BillType.First).
        billingPeriodStartDate("01-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        paymentReceivedAmount("0.00").
        depositAmount("930.30").
        serviceCharge("930.30").
        latePaymentFees("0.00").
        taxes("111.64").
        totalDueAmount("1972.24");
        // @formatter:on

        //==================== Bill 2 (full payment in time scenario) ======================/

        // Add Payment for April (paid in full)
        setSysDate("31-Mar-2011");
        receiveAndPostPayment("31-Mar-2011", "1972.24");

        setSysDate("17-Apr-2011");
        activateLease();

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-May-2011").
        billingPeriodEndDate("31-May-2011").
        paymentReceivedAmount("-1972.24").
        serviceCharge("930.30").
        latePaymentFees("0.00").
        taxes("111.64").
        totalDueAmount("1041.94"); // 930.30 + 111.64 = 1041.94
        // @formatter:on

        //==================== Bill 3 (unpaid immediate charges scenario) ======================//

        // Add Payment for May (full)
        setSysDate("30-Apr-2011");
        receiveAndPostPayment("30-Apr-2011", "1041.94");
        // add some immediate charges (taxable) - should see late payment fee
        addBooking("28-Apr-2011"); // $30.00
        finalizeLeaseAdendum();

        addAccountCharge("100.00");
        // post credit after due date - too late to avoid late charges
        setSysDate("02-May-2011");
        addGoodWillCredit("300.00");

        setSysDate("17-May-2011");

        bill = runBilling(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        new BillTester(bill).
        billingPeriodStartDate("01-Jun-2011").
        billingPeriodEndDate("30-Jun-2011").
        paymentReceivedAmount("-1041.94").
        serviceCharge("930.30").
        oneTimeFeatureCharges("30.00").
        immediateAccountAdjustments("-188.00"). // -300 +112
        latePaymentFees("50.00").
        taxes("115.24"). // 12% (930.30 + 30) = 115.24
        totalDueAmount("937.54"); // 930.30 +30 +112 +115.24 -300 +50
        // @formatter:on

        //==================== Bill 4 (partial late payment scenario) ======================//

        // Add Payment for May (partial)
        setSysDate("30-May-2011");
        receiveAndPostPayment("30-May-2011", "900.00");

        // Add Payment for May (final) after due date - should see late fee
        setSysDate("02-Jun-2011");
        receiveAndPostPayment("30-May-2011", "37.54");

        setSysDate("17-Jun-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        paymentReceivedAmount("-937.54").
        serviceCharge("930.30").
        latePaymentFees("50.00").
        taxes("111.64").
        totalDueAmount("1091.94"); // 930.30 +111.64 +50
        // @formatter:on

        //==================== Bill 5 (unpaid immediate charge tax scenario) ======================//

        // Add Payment for July (full)
        setSysDate("30-Jun-2011");
        receiveAndPostPayment("30-Jun-2011", "1091.94");
        // add some immediate charges (taxable)
        addAccountCharge("100.00");
        // add payment for the charge w/o tax - should see late payment fee
        setSysDate("01-Jul-2011");
        receiveAndPostPayment("01-Jul-2011", "100.00");

        // run bill
        setSysDate("17-Jul-2011");
        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        paymentReceivedAmount("-1191.94").
        serviceCharge("930.30").
        taxes("111.64"). // 12% of (930.30)
        latePaymentFees("50.00").
        totalDueAmount("1103.94"); // 930.30 +100 +123.64 +50 -100
        // @formatter:on

        //==================== Bill 6 (not-immediate charge scenario) ======================//

        // Add Payment for July (full)
        setSysDate("30-Jul-2011");
        receiveAndPostPayment("30-Jul-2011", "1103.94");
        // add non-immediate charge (taxable) - should not generate late fee
        addAccountCharge("200.00", false);

        // run bill
        setSysDate("17-Aug-2011");
        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        paymentReceivedAmount("-1103.94").
        serviceCharge("930.30").
        taxes("135.64"). // 12% of (930.30 + 200)
        latePaymentFees("0.00").
        totalDueAmount("1265.94"); // 930.30 +200 +135.64
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

}
