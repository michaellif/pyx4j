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
 * Created on Nov 8, 2012
 * @author stanp
 */
package com.propertyvista.biz.financial.billing.internal;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;

@Category(RegressionTests.class)
public class BillingLeaseTerminationTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testTerminateGracefully() throws Exception {

        setSysDate("17-Mar-2011");

        createLease("23-Mar-2011", "03-Aug-2011");

        addOutdoorParking();

        addLargeLocker();

        setLeaseBatchProcess();
        setDepositBatchProcess();
        //==================== RUN 1 ======================//

        Bill bill = approveApplication(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(3).
        paymentReceivedAmount("0.00").
        serviceCharge("270.09").
        recurringFeatureCharges("40.65").
        depositAmount("1070.30").
        taxes("37.29").
        totalDueAmount("1418.33");
        // @formatter:on

        //==================== RUN 2 ======================//

        advanceSysDate("18-Mar-2011");

        receiveAndPostPayment("18-Mar-2011", "1418.33");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(3).
        paymentReceivedAmount("-1418.33").
        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        oneTimeFeatureCharges("0.00").
        taxes("128.44").
        totalDueAmount("1198.74");
        // @formatter:on

        receiveAndPostPayment("19-Mar-2011", "1198.74");

        //==================== RUN 3 ======================//

        // terminate lease
        advanceSysDate("18-Apr-2011");
        terminateLease(CompletionType.Termination, "10-May-2011");

        bill = runBilling(true);

        // Make sure we only charge for the appropriate period
        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        billType(Bill.BillType.Regular).
        previousChargeAdjustments("0.00").
        depositRefundAmount("0.00").
        serviceCharge("300.10"). // service charge for 10 days of may
        recurringFeatureCharges("45.16"). // 10 day feature charges (140/3)
        taxes("41.43").
        totalDueAmount("386.69").
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("10-May-2011");
        // @formatter:on

        //==================== RUN 4 ======================//
        advanceSysDate("18-May-2011");

        bill = runBilling(true);

        // Make sure we get back the deposits
        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(4).
        billType(Bill.BillType.Final).
        previousChargeAdjustments("0.00").
        depositRefundAmount("-1091.82").
        serviceCharge("0.00").
        recurringFeatureCharges("0.00").
        taxes("0.00").
        totalDueAmount("-655.13").
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        closeLease();

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
    }

    public void testTerminateNow() throws Exception {

        setSysDate("17-Mar-2011");

        createLease("23-Mar-2011", "03-Aug-2011");

        addOutdoorParking();

        addLargeLocker();

        setLeaseBatchProcess();
        setDepositBatchProcess();
        //==================== RUN 1 ======================//

        Bill bill = approveApplication(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(3).
        paymentReceivedAmount("0.00").
        serviceCharge("270.09").
        recurringFeatureCharges("40.65").
        depositAmount("1070.30").
        taxes("37.29").
        totalDueAmount("1418.33");
        // @formatter:on

        //==================== RUN 2 ======================//

        advanceSysDate("18-Mar-2011");

        receiveAndPostPayment("18-Mar-2011", "1418.33");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(3).
        paymentReceivedAmount("-1418.33").
        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        oneTimeFeatureCharges("0.00").
        taxes("128.44").
        totalDueAmount("1198.74");
        // @formatter:on

        receiveAndPostPayment("19-Mar-2011", "1198.74");

        //==================== RUN 3 ======================//

        // terminate lease
        advanceSysDate("18-Apr-2011");
        terminateLease(CompletionType.Termination);

        bill = runBilling(true);

        // Make sure we get back deposits and reminder of charges
        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        billType(Bill.BillType.Final).
        previousChargeAdjustments("-1198.74").
        depositRefundAmount("-939.60").
        serviceCharge("558.18"). // revised service charge for april
        recurringFeatureCharges("84.00"). // revised feature charges
        taxes("77.06"). // revised taxes
        totalDueAmount("-1419.10").
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        closeLease();

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
    }
}
