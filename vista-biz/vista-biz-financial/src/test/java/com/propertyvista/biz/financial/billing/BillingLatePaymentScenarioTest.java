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

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingLatePaymentScenarioTest extends FinancialTestBase {

    private long startTime;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void tearDown() throws Exception {
        System.out.println("Execution Time - " + (System.currentTimeMillis() - startTime) + "ms");
        super.tearDown();
    }

    public void testScenario() {

        setLeaseConditions("01-Apr-2011", "31-Aug-2011", 1);

        //==================== First Bill ======================//

        SysDateManager.setSysDate("17-Mar-2011");
        setLeaseStatus(Lease.Status.Approved);

        Bill bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("01-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        paymentReceivedAmount("0.00").
        depositAmount("930.30").
        serviceCharge("930.30").
        latePaymentFee("0.00").
        taxes("111.64").
        totalDueAmount("1972.24");
        // @formatter:on

        //==================== Bill 2 (full payment in time scenario) ======================/

        // Add Payment for April (paid in full)
        SysDateManager.setSysDate("31-Mar-2011");
        receiveAndPostPayment("31-Mar-2011", "1972.24");

        SysDateManager.setSysDate("17-Apr-2011");
        setLeaseStatus(Lease.Status.Active);

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        paymentReceivedAmount("-1972.24").
        serviceCharge("930.30").
        latePaymentFee("0.00").
        taxes("111.64").
        totalDueAmount("1041.94"); // 930.30 + 111.64 = 1041.94
        // @formatter:on

        //==================== Bill 3 (unpaid immediate charges scenario) ======================//

        // Add Payment for May (full)
        SysDateManager.setSysDate("30-Apr-2011");
        receiveAndPostPayment("30-Apr-2011", "1041.94");
        // add some immediate charges - should see late payment fee
        addBooking("28-Apr-2011");
        addAccountCharge("100.00", "28-Apr-2011", true);

        SysDateManager.setSysDate("02-May-2011");
        addGoodWillCredit("300.00", "28-Apr-2011", true);

        SysDateManager.setSysDate("17-May-2011");

        bill = runBilling(true, true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        new BillTester(bill).
        billingPeriodStartDate("1-Jun-2011").
        billingPeriodEndDate("30-Jun-2011").
        paymentReceivedAmount("-1041.94").
        serviceCharge("930.30").
        oneTimeFeatureCharges("100.00").
        latePaymentFee("50.00").
        taxes("123.64"). // 12% (930.30 + 100) = 123.64
        totalDueAmount("1003.94"); // 930.30 +100 +123.64 +100 -300 +50
        // @formatter:on

        //==================== Bill 4 (partial late payment scenario) ======================//

        // Add Payment for May (partial)
        SysDateManager.setSysDate("30-May-2011");
        receiveAndPostPayment("30-May-2011", "1000.00");

        // Add Payment for May (final) - should see late fee
        SysDateManager.setSysDate("02-Jun-2011");
        receiveAndPostPayment("30-May-2011", "3.94");

        SysDateManager.setSysDate("17-Jun-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        paymentReceivedAmount("-1003.94").
        serviceCharge("930.30").
        latePaymentFee("50.00").
        taxes("111.64").
        totalDueAmount("1091.94"); // 1041.94 + 50
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

}
