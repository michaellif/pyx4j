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
import com.propertyvista.biz.financial.preload.PreloadConfig;
import com.propertyvista.domain.financial.billing.Bill;

public class BillingFullCycleScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreloadConfig config = new PreloadConfig();
        config.defaultBillingCycleSartDay = 15;
        preloadData(config);
    }

    public void testScenario() {

        setLeaseTerms("28-Jan-2012", "16-Feb-2013");

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("13-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("28-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("0.00").
        serviceCharge("540.17").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("930.30").
        taxes("64.82").
        totalDueAmount("1535.29");
        // @formatter:on

        //==================== RUN 2 LATE PAYMENT ======================//

        activateLease();

        SysDateManager.setSysDate("30-Jan-2012");
        receiveAndPostPayment("30-Jan-2012", "1535.29");

        SysDateManager.setSysDate("01-Feb-2012");

        bill = runBilling(true, true);

        new BillTester(bill).

        // @formatter:off
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Feb-2012").
        billingPeriodEndDate("14-Mar-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-1535.29").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("1091.94");
        // @formatter:on

        //==================== RUN 3 LATE PAYMENT ======================//

        SysDateManager.setSysDate("16-Feb-2012");
        receiveAndPostPayment("16-Feb-2012", "1041.94");

        SysDateManager.setSysDate("01-Mar-2012");

        bill = runBilling(true, true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        
        new BillTester(bill).
        
        billSequenceNumber(3).
        previousBillSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Mar-2012").
        billingPeriodEndDate("14-Apr-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-1041.94").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("1141.94");
        // @formatter:on

        //==================== RUN 4 ======================//
        SysDateManager.setSysDate("15-Mar-2012");
        receiveAndPostPayment("15-Mar-2012", "1141.94");

        SysDateManager.setSysDate("01-Apr-2012");

        bill = runBilling(true, true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        
        new BillTester(bill).
        
        billSequenceNumber(4).
        previousBillSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Apr-2012").
        billingPeriodEndDate("14-May-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-1141.94").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("1041.94");
        // @formatter:on

        //==================== RUN 5 OVER PAYMENT======================//

        SysDateManager.setSysDate("14-Apr-2012");
        receiveAndPostPayment("14-Apr-2012", "1341.94");

        SysDateManager.setSysDate("01-May-2012");

        bill = runBilling(true, true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        
        new BillTester(bill).
        
        billSequenceNumber(5).
        previousBillSequenceNumber(4).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-May-2012").
        billingPeriodEndDate("14-Jun-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-1341.94").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("741.94");
        // @formatter:on

        //==================== RUN 6 OVERPAYMENT!======================//

        SysDateManager.setSysDate("13-May-2012");
        receiveAndPostPayment("13-May-2012", "2041.94");

        SysDateManager.setSysDate("01-Jun-2012");

        bill = runBilling(true, true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off 
        new BillTester(bill).
        billSequenceNumber(6).
        previousBillSequenceNumber(5).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Jun-2012").
        billingPeriodEndDate("14-Jul-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-2041.94").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("-258.06");
        // @formatter:on

        //==================== RUN 7 NO PAYMENT ======================//

        SysDateManager.setSysDate("29-Jun-2012");
        receiveAndPostPayment("29-Jun-2012", "0.01");

        SysDateManager.setSysDate("01-Jul-2012");

        bill = runBilling(true, true);

        new BillTester(bill).

        // @formatter:off 
        billSequenceNumber(7).
        previousBillSequenceNumber(6).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Jul-2012").
        billingPeriodEndDate("14-Aug-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-0.01").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("783.87");
        // @formatter:on

        //==================== RUN 8 OVER PAYMENT ======================//

        SysDateManager.setSysDate("29-Jul-2012");
        receiveAndPostPayment("29-Jul-2012", "2111.00");

        SysDateManager.setSysDate("01-Aug-2012");

        bill = runBilling(true, true);

        // @formatter:off 
        new BillTester(bill).
        billSequenceNumber(8).
        previousBillSequenceNumber(7).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Aug-2012").
        billingPeriodEndDate("14-Sep-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-2111.00").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("-235.19");
        // @formatter:on

        //==================== RUN 9 NO PAYMENT ======================//

        SysDateManager.setSysDate("29-Aug-2012");

        SysDateManager.setSysDate("01-Sep-2012");

        bill = runBilling(true, true);

        // @formatter:off
        
      
        new BillTester(bill).
        
        billSequenceNumber(9).
        previousBillSequenceNumber(8).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Sep-2012").
        billingPeriodEndDate("14-Oct-2012").
        numOfProductCharges(1).
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("806.75");
        // @formatter:on

        //==================== RUN 10  ======================//

        SysDateManager.setSysDate("15-Sep-2012");
        receiveAndPostPayment("15-Sep-2012", "806.75");
        SysDateManager.setSysDate("01-Oct-2012");

        bill = runBilling(true, true);

        // @formatter:off
 
        new BillTester(bill).
        
        billSequenceNumber(10).
        previousBillSequenceNumber(9).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Oct-2012").
        billingPeriodEndDate("14-Nov-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-806.75").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("1041.94");
        // @formatter:on

        //==================== RUN 11  ======================//

        SysDateManager.setSysDate("15-Oct-2012");
        receiveAndPostPayment("15-Oct-2012", "1141.94");
        SysDateManager.setSysDate("01-Nov-2012");

        bill = runBilling(true, true);

        // @formatter:off
        
          new BillTester(bill).
        
        billSequenceNumber(11).
        previousBillSequenceNumber(10).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Nov-2012").
        billingPeriodEndDate("14-Dec-2012").
        numOfProductCharges(1).
        paymentReceivedAmount("-1141.94").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("941.94");
        // @formatter:on

        //==================== RUN 12  ======================//

        SysDateManager.setSysDate("11-Nov-2012");
        receiveAndPostPayment("11-Nov-2012", "1041.94");
        SysDateManager.setSysDate("01-Dec-2012");

        bill = runBilling(true, true);

        // @formatter:off
        
        new BillTester(bill).
        
        billSequenceNumber(12).
        previousBillSequenceNumber(11).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Dec-2012").
        billingPeriodEndDate("14-Jan-2013").
        numOfProductCharges(1).
        paymentReceivedAmount("-1041.94").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("941.94");
        // @formatter:on

        //==================== RUN 13  ======================//

        SysDateManager.setSysDate("14-Dec-2012");
        receiveAndPostPayment("14-Dec-2012", "1041.94");
        SysDateManager.setSysDate("01-Jan-2013");

        bill = runBilling(true, true);

        // @formatter:off
        
        new BillTester(bill).
        
        billSequenceNumber(13).
        previousBillSequenceNumber(12).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Jan-2013").
        billingPeriodEndDate("14-Feb-2013").
        numOfProductCharges(1).
        paymentReceivedAmount("-1041.94").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("941.94");
        // @formatter:on

        //==================== RUN 14  ======================//

        SysDateManager.setSysDate("07-Jan-2012");
        receiveAndPostPayment("07-Dec-2013", "1041.94");
        SysDateManager.setSysDate("01-Feb-2013");

        bill = runBilling(true, true);

        // @formatter:off
        
        new BillTester(bill).
        
        billSequenceNumber(14).
        previousBillSequenceNumber(13).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("15-Feb-2013").
        billingPeriodEndDate("16-Feb-2013").
        numOfProductCharges(1).
        paymentReceivedAmount("-1041.94").
        serviceCharge("66.45").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        depositRefundAmount("-930.30").
        taxes("7.97").
        totalDueAmount("-955.88");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
}
