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

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.billing.Bill;

public class BillingZeroCycleScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SysDateManager.setSysDate("17-Mar-2011"); // create existing lease
        preloadData();
    }

    public void testCarryForwardOwingScenario() {

        SysDateManager.setSysDate("17-Mar-2011"); // create existing lease

        // When we create Existing Lease, the tenant is already living in the building
        initLease("1-Jan-2011", "31-Dec-2011", 1, new BigDecimal("300.00"));

        //==================== RUN 1 ======================//

        Bill bill = verifyExistingLease();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill, true).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.ZeroCycle).
        billingPeriodStartDate("1-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("0.00").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("0.00").
        taxes("111.64").
        totalDueAmount("300.00");
        // @formatter:on

        //==================== RUN 2 ======================//

        activateLease();

        receiveAndPostPayment("17-Mar-2011", "300.00");

        SysDateManager.setSysDate("18-Mar-2011");
        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill, true).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("-300.00").
        latePaymentFees("50.00"). // fined for 300.00 carry-forward owing
        serviceCharge("930.30").
        taxes("111.64").
        totalDueAmount("1091.94");
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
    }

    public void testCarryForwardOwedScenario() {

        SysDateManager.setSysDate("17-Mar-2011"); // create existing lease

        // When we create Existing Lease, the tenant is already living in the building
        initLease("1-Jan-2011", "31-Dec-2011", 1, new BigDecimal("-100.00"));

        //==================== RUN 1 ======================//

        Bill bill = verifyExistingLease();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill, true).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.ZeroCycle).
        billingPeriodStartDate("1-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("0.00").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("0.00").
        taxes("111.64").
        totalDueAmount("-100.00");
        // @formatter:on

        //==================== RUN 2 ======================//

        activateLease();

        SysDateManager.setSysDate("18-Mar-2011");
        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill, true).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(1).
        serviceCharge("930.30").
        taxes("111.64").
        totalDueAmount("941.94"); // 1041.94 -100
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

}
