/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.RegressionTests;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.Type;

@Ignore
@Category(RegressionTests.class)
public class PadPaymentChargeBaseSunnyDayScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        setSysDate("17-Mar-2011");

        createLease("23-Mar-2011", "03-Aug-2011");
        addServiceAdjustment("-25", Type.monetary);

        BillableItem parking1 = addParking();
        addFeatureAdjustment(parking1.uid().getValue(), "-10", Type.monetary);

        BillableItem parking2 = addParking("23-Apr-2011", "03-Aug-2011");
        addFeatureAdjustment(parking2.uid().getValue(), "-10", Type.monetary);

        BillableItem locker1 = addLocker();
        addFeatureAdjustment(locker1.uid().getValue(), "-0.2", Type.percentage);

        setLeaseBatchProcess();
        setDepositBatchProcess();

        //==================== RUN 1 ======================//

        Bill bill = approveApplication(true);

        // @formatter:off
            new BillTester(bill).
            billSequenceNumber(1).
            billType(Bill.BillType.First).
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

        bill = runBilling(true);

        // @formatter:off
            new BillTester(bill).
            billSequenceNumber(2).
            billType(Bill.BillType.Regular).
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

        //==================== RUN 3 ======================//

        advanceSysDate("18-Apr-2011");

        bill = runBilling(true);

        // @formatter:off
            new BillTester(bill).
            billSequenceNumber(3).
            billType(Bill.BillType.Regular).
            billingCyclePeriodStartDate("01-May-2011").
            billingPeriodStartDate("1-May-2011").
            billingPeriodEndDate("31-May-2011").
            numOfProductCharges(7).
            paymentReceivedAmount("-1247.00").
            serviceCharge("905.30").
            recurringFeatureCharges("188.00").
            oneTimeFeatureCharges("200.00").
            taxes("155.20").
            totalDueAmount("1448.50");
            // @formatter:on

        receiveAndPostPayment("19-Apr-2011", "1448.50");

        //==================== RUN 4 ======================//

        advanceSysDate("18-May-2011");

        bill = runBilling(true);

        // @formatter:off
            new BillTester(bill).
            billSequenceNumber(4).
            billType(Bill.BillType.Regular).
            billingCyclePeriodStartDate("01-Jun-2011").
            billingPeriodStartDate("1-Jun-2011").
            billingPeriodEndDate("30-Jun-2011").
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

        advanceSysDate("18-Jun-2011");

        bill = runBilling(true);

        // @formatter:off
            new BillTester(bill).
            billSequenceNumber(5).
            billType(Bill.BillType.Regular).
            billingCyclePeriodStartDate("01-Jul-2011").
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

        advanceSysDate("18-Jul-2011");

        bill = runBilling(true);

        // @formatter:off
            new BillTester(bill).
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

        bill = runBilling(true);

        // @formatter:off
            new BillTester(bill).
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