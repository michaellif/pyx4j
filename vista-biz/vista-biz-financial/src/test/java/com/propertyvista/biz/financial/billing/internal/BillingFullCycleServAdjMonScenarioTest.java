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

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.mock.MockConfig;

@Category(FunctionalTests.class)
public class BillingFullCycleServAdjMonScenarioTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockConfig config = new MockConfig();
        config.defaultBillingCycleSartDay = 15;
        preloadData(config);
    }

    public void testScenario() throws Exception {

        createLease("28-Jan-2012", "16-Feb-2013");
        addServiceAdjustment("-55.55", ValueType.Monetary);

        //==================== RUN 1 ======================//

        setSysDate("13-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        billType(Bill.BillType.First).
        billingPeriodStartDate("28-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("0.00").
        serviceCharge("507.92").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("930.30").
        taxes("60.95").
        totalDueAmount("1499.17");
        // @formatter:off
        
        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        //==================== RUN 2 LATE PAYMENT ======================//

        activateLease();

        setSysDate("30-Jan-2012");
        receiveAndPostPayment("30-Jan-2012", "1535.29");

        setSysDate("01-Feb-2012");

        bill = runBilling(true);

        new BillTester(bill).

        // @formatter:off
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Feb-2012").
        billingPeriodEndDate("14-Mar-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-1535.29").
        serviceCharge("874.75").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("104.97").
        totalDueAmount("993.60");
        // @formatter:off

        //==================== RUN 3 LATE PAYMENT ======================//

        setSysDate("16-Feb-2012");
        receiveAndPostPayment("16-Feb-2012", "1041.94");

        setSysDate("01-Mar-2012");

        bill = runBilling(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        
        new BillTester(bill).
        
        billSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Mar-2012").
        billingPeriodEndDate("14-Apr-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-1041.94").
        serviceCharge("874.75").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("104.97").
        totalDueAmount("981.38");
        // @formatter:off

        //==================== RUN 4 ======================//
        setSysDate("15-Mar-2012");
        receiveAndPostPayment("15-Mar-2012", "1141.94");

        setSysDate("01-Apr-2012");

        bill = runBilling(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        
        new BillTester(bill).
        
        billSequenceNumber(4).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Apr-2012").
        billingPeriodEndDate("14-May-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-1141.94").
        serviceCharge("874.75").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("104.97").
        totalDueAmount("819.16");
        // @formatter:off

        //==================== RUN 5 OVER PAYMENT======================//

        setSysDate("14-Apr-2012");
        receiveAndPostPayment("14-Apr-2012", "1341.94");

        setSysDate("01-May-2012");

        bill = runBilling(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        
        new BillTester(bill).
        
        billSequenceNumber(5).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-May-2012").
        billingPeriodEndDate("14-Jun-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-1341.94").
        serviceCharge("874.75").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("104.97").
        totalDueAmount("456.94");
        // @formatter:off

        //==================== RUN 6 OVERPAYMENT!======================//

        setSysDate("13-May-2012");
        receiveAndPostPayment("13-May-2012", "2041.94");

        setSysDate("01-Jun-2012");

        bill = runBilling(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off 
        new BillTester(bill).
        billSequenceNumber(6).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Jun-2012").
        billingPeriodEndDate("14-Jul-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-2041.94").
        serviceCharge("874.75").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("104.97").
        totalDueAmount("-605.28");
        // @formatter:off

        //==================== RUN 7 NO PAYMENT ======================//

        setSysDate("29-Jun-2012");
        receiveAndPostPayment("29-Jun-2012", "0.01");

        setSysDate("01-Jul-2012");

        bill = runBilling(true);

        new BillTester(bill).

        // @formatter:off 
        billSequenceNumber(7).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Jul-2012").
        billingPeriodEndDate("14-Aug-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-0.01").
        serviceCharge("874.75").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("104.97").
        totalDueAmount("374.43");
        // @formatter:off

        //==================== RUN 8 OVER PAYMENT ======================//

        setSysDate("29-Jul-2012");
        receiveAndPostPayment("29-Jul-2012", "2111.00");

        setSysDate("01-Aug-2012");

        bill = runBilling(true);

        // @formatter:off 
        new BillTester(bill).
        billSequenceNumber(8).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Aug-2012").
        billingPeriodEndDate("14-Sep-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-2111.00").
        serviceCharge("874.75").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("104.97").
        totalDueAmount("-706.85");
        // @formatter:off

        //==================== RUN 9 NO PAYMENT ======================//

        setSysDate("29-Aug-2012");

        setSysDate("01-Sep-2012");

        bill = runBilling(true);

        // @formatter:off
        
      
        new BillTester(bill).
        
        billSequenceNumber(9).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Sep-2012").
        billingPeriodEndDate("14-Oct-2012").
        numOfProductCharges(1).
        serviceCharge("874.75").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("104.97").
        totalDueAmount("272.87");
        // @formatter:off

        //==================== RUN 10  ======================//

        setSysDate("15-Sep-2012");
        receiveAndPostPayment("15-Sep-2012", "806.75");
        setSysDate("01-Oct-2012");

        bill = runBilling(true);

        // @formatter:off
 
        new BillTester(bill).
        
        billSequenceNumber(10).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Oct-2012").
        billingPeriodEndDate("14-Nov-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-806.75").
        serviceCharge("874.75").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("104.97").
        totalDueAmount("445.84");
        // @formatter:off

        //==================== RUN 11  ======================//

        setSysDate("15-Oct-2012");
        receiveAndPostPayment("15-Oct-2012", "1141.94");
        setSysDate("01-Nov-2012");

        bill = runBilling(true);

        // @formatter:off
        
          new BillTester(bill).
        
        billSequenceNumber(11).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Nov-2012").
        billingPeriodEndDate("14-Dec-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-1141.94").
        serviceCharge("874.75").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("104.97").
        totalDueAmount("283.62");
        // @formatter:off

        //==================== RUN 12  ======================//

        setSysDate("11-Nov-2012");
        receiveAndPostPayment("11-Nov-2012", "1041.94");
        setSysDate("01-Dec-2012");

        bill = runBilling(true);

        // @formatter:off
        
        new BillTester(bill).
        
        billSequenceNumber(12).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Dec-2012").
        billingPeriodEndDate("14-Jan-2013").
        numOfProductCharges(1).
        paymentReceivedAmount("-1041.94").
        serviceCharge("874.75").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("104.97").
        totalDueAmount("221.40");
        // @formatter:off

        //==================== RUN 13  ======================//

        setSysDate("14-Dec-2012");
        receiveAndPostPayment("14-Dec-2012", "1041.94");
        setSysDate("01-Jan-2013");

        bill = runBilling(true);

        // @formatter:off
        
        new BillTester(bill).
        
        billSequenceNumber(13).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Jan-2013").
        billingPeriodEndDate("14-Feb-2013").
        numOfProductCharges(1).
        paymentReceivedAmount("-1041.94").
        serviceCharge("874.75").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("104.97").
        totalDueAmount("159.18");
        // @formatter:off

        //==================== RUN 14  ======================//

        setSysDate("07-Jan-2013");
        receiveAndPostPayment("07-Jan-2013", "1041.94");
        setSysDate("01-Feb-2013");

        bill = runBilling(true);

        // @formatter:off
        
        new BillTester(bill).
        
        billSequenceNumber(14).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Feb-2013").
        billingPeriodEndDate("16-Feb-2013").
        numOfProductCharges(1).
        paymentReceivedAmount("-1041.94").
        serviceCharge("62.48").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        depositRefundAmount("-930.30").
        taxes("7.50").
        totalDueAmount("-1743.08");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
}
