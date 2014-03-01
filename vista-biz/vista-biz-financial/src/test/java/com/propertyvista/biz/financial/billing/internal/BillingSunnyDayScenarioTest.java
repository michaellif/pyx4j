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
package com.propertyvista.biz.financial.billing.internal;

import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.Type;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;

@Ignore
@Category(RegressionTests.class)
public class BillingSunnyDayScenarioTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        setSysDate("17-Mar-2011");

        createLease("23-Mar-2011", "03-Aug-2011");
        addServiceAdjustment("-25", Type.monetary);

        BillableItem parking1 = addOutdoorParking();
        addFeatureAdjustment(parking1.uid().getValue(), "-10", Type.monetary);

        BillableItem parking2 = addOutdoorParking("23-Apr-2011", "03-Aug-2011");
        addFeatureAdjustment(parking2.uid().getValue(), "-10", Type.monetary);

        BillableItem locker1 = addLargeLocker();
        addFeatureAdjustment(locker1.uid().getValue(), "-0.2", Type.percentage);

        setBillingBatchProcess();
        setLeaseBatchProcess();
        setDepositBatchProcess();

        //==================== RUN 1 ======================//

        // @formatter:off
        new BillTester(runBillingPreview()).
        billSequenceNumber(0).
        billType(Bill.BillType.First).
        billStatus(Bill.BillStatus.Finished).
        billingCyclePeriodStartDate("01-Mar-2011").
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(3).
        paymentReceivedAmount("0.00").
        serviceCharge("262.83").
        recurringFeatureCharges("34.27").
        oneTimeFeatureCharges("0.00").
        depositAmount("1070.30").
        taxes("35.65").
        totalDueAmount("1403.05");
        // @formatter:on

        BillableItem pet1 = addCat();
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        // @formatter:off
        new BillTester(runBillingPreview()).
        billSequenceNumber(0).
        billType(Bill.BillType.First).
        billStatus(Bill.BillStatus.Finished).
        billingCyclePeriodStartDate("01-Mar-2011").
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(4).
        paymentReceivedAmount("0.00").
        serviceCharge("262.83").
        recurringFeatureCharges("34.27").
        oneTimeFeatureCharges("0.00").
        depositAmount("1270.30").
        taxes("35.65").
        totalDueAmount("1603.05");
        // @formatter:on

        // @formatter:off
        new BillTester(approveApplication(true)).
        billSequenceNumber(1).
        billType(Bill.BillType.First).
        billStatus(Bill.BillStatus.Confirmed).
        billingCyclePeriodStartDate("01-Mar-2011").
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(4).
        paymentReceivedAmount("0.00").
        serviceCharge("262.83").
        recurringFeatureCharges("34.27").
        oneTimeFeatureCharges("0.00").
        depositAmount("1270.30").
        taxes("35.65").
        totalDueAmount("1603.05");
        // @formatter:on

        //==================== RUN 2 ======================//

        advanceSysDate("18-Mar-2011");

        receiveAndPostPayment("18-Mar-2011", "1603.05");

        //TODO that bill should be triggered by approval process instead of implicit call to run?!
        runBilling();

        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billStatus(Bill.BillStatus.Confirmed).
        billingCyclePeriodStartDate("01-Apr-2011").
        billingPeriodStartDate("01-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(5).
        paymentReceivedAmount("-1603.05").
        serviceCharge("905.30").
        recurringFeatureCharges("136.66").
        oneTimeFeatureCharges("0.00").
        taxes("125.04").
        totalDueAmount("1247.00");
        // @formatter:on

        receiveAndPostPayment("19-Mar-2011", "1067.00");
        receiveAndPostPayment("20-Mar-2011", "180.00");

        //==================== RUN 3 ======================//

        addBooking("25-Apr-2011"); // 30.00
        addBooking("5-May-2011"); // 30.00
        finalizeLeaseAdendum();

        advanceSysDate("18-Apr-2011");

        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billStatus(Bill.BillStatus.Confirmed).
        billingCyclePeriodStartDate("01-May-2011").
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(7).
        paymentReceivedAmount("-1247.00").
        serviceCharge("905.30").
        recurringFeatureCharges("188.00").
        oneTimeFeatureCharges("60.00").
        taxes("138.40").
        totalDueAmount("1291.70");
        // @formatter:on

        receiveAndPostPayment("19-Apr-2011", "1291.70");

        //==================== RUN 4 ======================//

        addBooking("28-Apr-2011"); // 30.00
        finalizeLeaseAdendum();

        addGoodWillCredit("20.00", false);
        addGoodWillCredit("30.00");

        addGoodWillCredit("120.00", false);
        addGoodWillCredit("130.00");

        advanceSysDate("20-May-2011");

        confirmBill(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(4).
        billType(Bill.BillType.Regular).
        billStatus(Bill.BillStatus.Confirmed).
        billingCyclePeriodStartDate("01-Jun-2011").
        billingPeriodStartDate("1-Jun-2011").
        billingPeriodEndDate("30-Jun-2011").
        numOfProductCharges(6).
        paymentReceivedAmount("-1291.70").
        serviceCharge("905.30").
        recurringFeatureCharges("188.00").
        oneTimeFeatureCharges("30.00").
        taxes("134.80").
        totalDueAmount("958.10");
        // @formatter:on

        receiveAndPostPayment("19-May-2011", "958.10");

        //==================== RUN 5 ======================//

        addGoodWillCredit("30.00", false);
        addGoodWillCredit("40.00");

        advanceSysDate("18-Jun-2011");

        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(5).
        billType(Bill.BillType.Regular).
        billingCyclePeriodStartDate("01-Jul-2011").
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        numOfProductCharges(5).
        paymentReceivedAmount("-958.10").
        serviceCharge("905.30").
        recurringFeatureCharges("188.00").
        oneTimeFeatureCharges("0.00").
        taxes("131.20").
        totalDueAmount("1154.50");
        // @formatter:on

        receiveAndPostPayment("19-Jun-2011", "1154.50");

        //==================== RUN 6 ======================//

        advanceSysDate("18-Jul-2011");

        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(6).
        billType(Bill.BillType.Regular).
        billingCyclePeriodStartDate("01-Aug-2011").
        billingPeriodStartDate("01-Aug-2011").
        billingPeriodEndDate("03-Aug-2011").
        numOfProductCharges(5).
        paymentReceivedAmount("-1154.50").
        serviceCharge("87.61").
        recurringFeatureCharges("18.19").
        oneTimeFeatureCharges("0.00").
        depositRefundAmount("-968.07").
        taxes("12.70").
        totalDueAmount("-849.57");
        // @formatter:on

        //==================== RUN final ======================//

        advanceSysDate("05-Aug-2011");

        addAccountCharge("140.00");

        // @formatter:off
        new BillTester(runBilling(true)).
        billSequenceNumber(7).
        billType(Bill.BillType.Final).
        billingCyclePeriodStartDate("01-Sep-2011").
        immediateAccountAdjustments("156.80").
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
}
