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
 * Created on Sep 27, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.RegressionTests;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.Type;

@Category(RegressionTests.class)
public class BillingProductRefundTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() {

        setDate("17-Mar-2011");

        createLease("23-Mar-2011", "03-Aug-2011");

        BillableItem parking = addParking();

        setLeaseBatchProcess(); // activates and completes lease
        setDepositBatchProcess(); // background deposit processing
        //==================== RUN 1 ======================//

        Bill billPreview = runBillingPreview();
        // @formatter:off
        new BillTester(billPreview).
        billSequenceNumber(0).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(2).
        paymentReceivedAmount("0.00").
        serviceCharge("270.09").
        recurringFeatureCharges("23.23").
        oneTimeFeatureCharges("0.00").
        depositAmount("1010.30").
        taxes("35.20").
        totalDueAmount("1338.82");
        // @formatter:on

        Bill bill = approveApplication(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(2).
        paymentReceivedAmount("0.00").
        serviceCharge("270.09").
        recurringFeatureCharges("23.23").
        oneTimeFeatureCharges("0.00").
        depositAmount("1010.30").
        taxes("35.20").
        totalDueAmount("1338.82");
        // @formatter:on

        //==================== RUN 2 ======================//

        advanceDate("18-Mar-2011");

        receiveAndPostPayment("18-Mar-2011", "1338.82");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(2).
        paymentReceivedAmount("-1338.82").
        serviceCharge("930.30").
        recurringFeatureCharges("80.00").
        taxes("121.24").
        totalDueAmount("1131.54");
        // @formatter:on

        receiveAndPostPayment("19-Mar-2011", "1131.54");

        // Current period adjustment
        advanceDate("20-Mar-2011");
        addFeatureAdjustment(parking.uid().getValue(), "-10", Type.monetary, "20-Mar-2011", "10-Apr-2011");

        //==================== RUN 3 ======================//

        advanceDate("18-Apr-2011");

        finalizeLeaseAdendum();

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        previousBillSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(4).
        paymentReceivedAmount("-1131.54").
        serviceCharge("930.30").
        recurringFeatureCharges("177.00").
        taxes("132.88").
        totalDueAmount("1240.18");
        // @formatter:on

        receiveAndPostPayment("19-Apr-2011", "1240.18");

        // cancel feature for previous period
        advanceDate("20-Apr-2011");
        cancelBillableItem(parking.uid().getValue(), "21-Apr-2011");

        //==================== RUN 4 ======================//

        advanceDate("18-May-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(4).
        previousBillSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-June-2011").
        billingPeriodEndDate("30-June-2011").
        numOfProductCharges(2).
        paymentReceivedAmount("-1240.18").
        serviceCharge("930.30").
        recurringFeatureCharges("52.67").
        taxes("117.96").
        totalDueAmount("1020.13");
        // @formatter:on

        receiveAndPostPayment("19-May-2011", "1020.13");

        //==================== RUN 5 ======================//

        advanceDate("18-Jun-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        previousBillSequenceNumber(4).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("-1020.13").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("1041.94");
        // @formatter:on

        receiveAndPostPayment("19-Jun-2011", "1041.94");

        //==================== RUN 6 ======================//

        advanceDate("18-Jul-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        previousBillSequenceNumber(5).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Aug-2011").
        billingPeriodEndDate("03-Aug-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("-1041.94").
        serviceCharge("90.03").
        recurringFeatureCharges("0.00").
        depositRefundAmount("-968.07").
        taxes("10.80").
        totalDueAmount("-867.24");
        // @formatter:on

        //==================== RUN final ======================//

        advanceDate("05-Aug-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        previousBillSequenceNumber(6).
        billType(Bill.BillType.Final).
        immediateAccountAdjustments("156.80").
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
}
