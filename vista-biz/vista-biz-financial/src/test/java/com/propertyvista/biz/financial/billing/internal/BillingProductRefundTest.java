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
package com.propertyvista.biz.financial.billing.internal;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;

@Category(RegressionTests.class)
public class BillingProductRefundTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        setSysDate("17-Mar-2011");

        createLease("23-Mar-2011", "03-Aug-2011");

        BillableItem parking = addOutdoorParking();

        setLeaseBatchProcess(); // activates and completes lease
        setDepositBatchProcess(); // background deposit processing
        //==================== RUN 1 ======================//

        Bill bill = approveApplication(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(2).
        paymentReceivedAmount("0.00").
        serviceCharge("270.09").
        recurringFeatureCharges("23.23").
        oneTimeFeatureCharges("0.00").
        previousChargeAdjustments("0.00").
        depositAmount("1010.30").
        taxes("35.20").
        totalDueAmount("1338.82");
        // @formatter:on

        //==================== RUN 2 ======================//

        advanceSysDate("18-Mar-2011");

        receiveAndPostPayment("18-Mar-2011", "1338.82");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(2).
        paymentReceivedAmount("-1338.82").
        serviceCharge("930.30").
        recurringFeatureCharges("80.00").
        previousChargeAdjustments("0.00").
        taxes("121.24").
        totalDueAmount("1131.54");
        // @formatter:on

        receiveAndPostPayment("19-Mar-2011", "1131.54");

        // Current period adjustment
        advanceSysDate("20-Mar-2011");
        addFeatureAdjustment(parking.uid().getValue(), "-10", BillableItemAdjustment.Type.monetary, "20-Mar-2011", "10-Apr-2011");

        //==================== RUN 3 ======================//

        advanceSysDate("18-Apr-2011");

        finalizeLeaseAdendum();

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(4). // lease and 3 x parking (next, curr, prev)
        paymentReceivedAmount("-1131.54").
        serviceCharge("930.30").
        recurringFeatureCharges("177.00"). // 80.00 + 76.67 (apr) + 20.33 (mar)
        previousChargeAdjustments("-115.62"). // 80.00 (apr) + 23.23 (mar) + 12%
        taxes("132.88"). // 12% of (930.30 + 177.00)
        totalDueAmount("1124.56");
        // @formatter:on

        receiveAndPostPayment("19-Apr-2011", "1124.56");

        // cancel feature for previous period
        advanceSysDate("20-Apr-2011");
        cancelBillableItem(parking.uid().getValue(), "21-Apr-2011");

        //==================== RUN 4 ======================//

        advanceSysDate("18-May-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(4).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jun-2011").
        billingPeriodEndDate("30-Jun-2011").
        numOfProductCharges(2). // lease + parking (prev)
        paymentReceivedAmount("-1124.56").
        serviceCharge("930.30").
        recurringFeatureCharges("52.67"). // revised parking (prev)
        depositRefundAmount("-80.80"). // parking cancellation
        previousChargeAdjustments("-175.47"). // parking 80.00 (may) + 76.67 (apr) + 12%
        taxes("117.96"). // 12% of (930.30 + 52.67)
        totalDueAmount("844.66");
        // @formatter:on

        receiveAndPostPayment("19-May-2011", "844.66");

        //==================== RUN 5 ======================//

        advanceSysDate("18-Jun-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("-844.66").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        previousChargeAdjustments("0.00").
        taxes("111.64").
        totalDueAmount("1041.94");
        // @formatter:on

        receiveAndPostPayment("19-Jun-2011", "1041.94");

        //==================== RUN 6 ======================//

        advanceSysDate("18-Jul-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Aug-2011").
        billingPeriodEndDate("03-Aug-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("-1041.94").
        serviceCharge("90.03").
        recurringFeatureCharges("0.00").
        previousChargeAdjustments("0.00").
        depositRefundAmount("-968.07").
        taxes("10.80").
        totalDueAmount("-867.24");
        // @formatter:on

        //==================== RUN final ======================//

        advanceSysDate("05-Aug-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        billType(Bill.BillType.Final).
        immediateAccountAdjustments("0.00").
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
}
