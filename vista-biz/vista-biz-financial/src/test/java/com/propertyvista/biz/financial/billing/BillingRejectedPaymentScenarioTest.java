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

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.payment.PaymentType;

@Category(FunctionalTests.class)
public class BillingRejectedPaymentScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() {
        setLeaseTerms("01-Apr-2011", "31-Dec-2011");

        //==================== First Bill ======================//

        SysDateManager.setSysDate("17-Mar-2011");
        Bill bill = approveApplication(true);

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
        latePaymentFees("0.00").
        taxes("111.64").
        totalDueAmount("1972.24");
        // @formatter:on

        //==================== Bill 2 (full payment in time scenario) ======================/

        // Add Payment for April (paid in full)
        SysDateManager.setSysDate("31-Mar-2011");
        receiveAndPostPayment("31-Mar-2011", "1972.24");

        SysDateManager.setSysDate("17-Apr-2011");
        activateLease();

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        paymentReceivedAmount("-1972.24").
        serviceCharge("930.30").
        latePaymentFees("0.00").
        taxes("111.64").
        totalDueAmount("1041.94"); // 930.30 + 111.64 = 1041.94
        // @formatter:on

        //==================== Bill 3 (reject last payment scenario) ======================//

        // Add Payment for May
        SysDateManager.setSysDate("01-May-2011");
        receiveAndPostPayment("30-Apr-2011", "1000.00", PaymentType.Echeck);
        PaymentRecord payment = receiveAndPostPayment("30-Apr-2011", "41.94", PaymentType.Check);

        // reject payments
        SysDateManager.setSysDate("05-May-2011");
        rejectPayment(payment, true);

        SysDateManager.setSysDate("17-May-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill, true).
        billingPeriodStartDate("1-Jun-2011").
        billingPeriodEndDate("30-Jun-2011").
        paymentReceivedAmount("-1041.94").
        paymentRejectedAmount("41.94").
        serviceCharge("930.30").
        nsfCharges("30.00").
        taxes("111.64"). // 12% (930.30 + 100) = 123.64
        totalDueAmount("1113.88"); // 930.30 +111.64 +30 +41.94
        // @formatter:on

        //==================== Bill 4 (reject intermediate payment scenario) ======================//

        // Add Payment for June
        SysDateManager.setSysDate("01-Jun-2011");
        payment = receiveAndPostPayment("30-May-2011", "950.00", PaymentType.Echeck);
        receiveAndPostPayment("31-May-2011", "163.88", PaymentType.Check);

        // reject payments
        SysDateManager.setSysDate("05-Jun-2011");
        rejectPayment(payment, true);

        SysDateManager.setSysDate("17-Jun-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill, true).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        paymentReceivedAmount("-1113.88").
        paymentRejectedAmount("950.00").
        serviceCharge("930.30").
        nsfCharges("100.00").
        taxes("111.64"). // 12% (930.30)
        totalDueAmount("2091.94"); // 930.30 +111.64 +100 +950
        // @formatter:on

        //==================== Bill 5 (reject two payments scenario) ======================//

        // Add Payment for July
        SysDateManager.setSysDate("01-Jul-2011");
        payment = receiveAndPostPayment("29-Jun-2011", "900.00", PaymentType.Check);
        PaymentRecord payment2 = receiveAndPostPayment("30-Jun-2011", "1191.94", PaymentType.Echeck);

        // reject payments
        SysDateManager.setSysDate("05-Jul-2011");
        rejectPayment(payment, true);
        SysDateManager.setSysDate("06-Jul-2011");
        rejectPayment(payment2, true);

        SysDateManager.setSysDate("17-Jul-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill, true).
        billingPeriodStartDate("1-Aug-2011").
        billingPeriodEndDate("31-Aug-2011").
        paymentReceivedAmount("-2091.94").
        paymentRejectedAmount("2091.94").
        serviceCharge("930.30").
        nsfCharges("130.00").
        taxes("111.64"). // 12% (930.30)
        totalDueAmount("3263.88"); // 930.30 +111.64 +130 +2091.94
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
}
