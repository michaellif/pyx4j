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
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.AdjustmentType;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.ExecutionType;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingSunnyDayScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() {

        setLeaseConditions("23-Mar-2011", "03-Aug-2011", 1);
        addServiceAdjustment("-25", AdjustmentType.monetary, ExecutionType.inLease);

        BillableItem parking1 = addParking();
        addFeatureAdjustment(parking1.uid().getValue(), "-10", AdjustmentType.monetary, ExecutionType.inLease);

        BillableItem parking2 = addParking("23-Apr-2011", "03-Aug-2011");
        addFeatureAdjustment(parking2.uid().getValue(), "-10", AdjustmentType.monetary, ExecutionType.inLease);

        BillableItem locker1 = addLocker();
        addFeatureAdjustment(locker1.uid().getValue(), "-0.2", AdjustmentType.percentage, ExecutionType.inLease);

        BillableItem pet1 = addPet();
        addFeatureAdjustment(pet1.uid().getValue(), "-1", AdjustmentType.percentage, ExecutionType.inLease);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("17-Mar-2011");
        setLeaseStatus(Lease.Status.Approved);

        Bill bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(4).
        paymentReceivedAmount("0.00").
        serviceCharge("262.83").
        recurringFeatureCharges("34.26").
        oneTimeFeatureCharges("0.00").
        depositAmount("1130.30").
        taxes("35.65").
        totalDueAmount("1463.04");
        // @formatter:on

        //==================== RUN 2 ======================//

        SysDateManager.setSysDate("18-Mar-2011");

        receiveAndPostPayment("18-Mar-2011", "1463.04");

        setLeaseStatus(Lease.Status.Active);

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(5).
        paymentReceivedAmount("-1463.04").
        serviceCharge("905.30").
        recurringFeatureCharges("136.67").
        oneTimeFeatureCharges("0.00").
        taxes("125.04").
        totalDueAmount("1167.01");
        // @formatter:on

        receiveAndPostPayment("19-Mar-2011", "1067.01");
        receiveAndPostPayment("20-Mar-2011", "100.00");

        //==================== RUN 3 ======================//

        SysDateManager.setSysDate("18-Apr-2011");

        addBooking("25-Apr-2011");
        addBooking("5-May-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        previousBillSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(7).
        paymentReceivedAmount("-1167.01").
        serviceCharge("905.30").
        recurringFeatureCharges("188.00").
        oneTimeFeatureCharges("200.00").
        taxes("155.20").
        totalDueAmount("1448.50");
        // @formatter:on

        receiveAndPostPayment("19-Apr-2011", "1448.50");

        //==================== RUN 4 ======================//

        addBooking("28-Apr-2011");

        addGoodWillCredit("20.00", "19-Apr-2011");
        addGoodWillCredit("30.00");

        SysDateManager.setSysDate("18-May-2011");

        addGoodWillCredit("120.00", "18-May-2011");
        addGoodWillCredit("130.00");

        bill = runBilling(true, true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(4).
        previousBillSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-June-2011").
        billingPeriodEndDate("30-June-2011").
        numOfProductCharges(6).
        paymentReceivedAmount("-1448.50").
        serviceCharge("905.30").
        recurringFeatureCharges("188.00").
        oneTimeFeatureCharges("100.00").
        taxes("143.20").
        totalDueAmount("1036.50");
        // @formatter:on

        receiveAndPostPayment("19-May-2011", "1036.50");

        //==================== RUN 5 ======================//

        SysDateManager.setSysDate("18-Jun-2011");

        addGoodWillCredit("30.00", "1-Jul-2011");
        addGoodWillCredit("40.00");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        previousBillSequenceNumber(4).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        numOfProductCharges(5).
        paymentReceivedAmount("-1036.50").
        serviceCharge("905.30").
        recurringFeatureCharges("188.00").
        oneTimeFeatureCharges("0.00").
        taxes("131.20").
        totalDueAmount("1154.50");
        // @formatter:on

        receiveAndPostPayment("19-Jun-2011", "1154.50");

        //==================== RUN 6 ======================//

        SysDateManager.setSysDate("18-Jul-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        previousBillSequenceNumber(5).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Aug-2011").
        billingPeriodEndDate("03-Aug-2011").
        numOfProductCharges(5).
        paymentReceivedAmount("-1154.50").
        serviceCharge("87.61").
        recurringFeatureCharges("18.19").
        oneTimeFeatureCharges("0.00").
        taxes("12.69").
        totalDueAmount("118.49");
        // @formatter:on

        receiveAndPostPayment("19-Jul-2011", "118.49");

        //==================== RUN final ======================//

        SysDateManager.setSysDate("05-Aug-2011");

        setLeaseStatus(Lease.Status.Completed);

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        previousBillSequenceNumber(6).
        billType(Bill.BillType.Final).
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

}
