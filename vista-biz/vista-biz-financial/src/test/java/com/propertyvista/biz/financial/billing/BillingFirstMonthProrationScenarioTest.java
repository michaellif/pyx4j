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

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.preload.PreloadConfig;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.Type;

public class BillingFirstMonthProrationScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreloadConfig config = new PreloadConfig();
        config.defaultBillingCycleSartDay = 15;
        preloadData(config);
    }

    public void testScenario1() {

        setLeaseTerms("15-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("15-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("874.75").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("104.97").
        totalDueAmount("979.72");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario2() {

        setLeaseTerms("16-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("16-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("846.53").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("101.58").
        totalDueAmount("948.11");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
    }

    public void testScenario3()

    {

        setLeaseTerms("17-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("17-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("818.31").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("98.20").
        totalDueAmount("916.51");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario4() {

        setLeaseTerms("18-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("18-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("790.10").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("94.81").
        totalDueAmount("884.91");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario5() {

        setLeaseTerms("19-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("19-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("761.88").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("91.43").
        totalDueAmount("853.31");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario6()

    {

        setLeaseTerms("20-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("20-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("733.66").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("88.04").
        totalDueAmount("821.70");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario7()

    {

        setLeaseTerms("21-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("21-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("705.44").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("84.65").
        totalDueAmount("790.09");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario8()

    {

        setLeaseTerms("22-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("22-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("677.22").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("81.27").
        totalDueAmount("758.49");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario9()

    {

        setLeaseTerms("23-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("649.01").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("77.88").
        totalDueAmount("726.89");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario10()

    {

        setLeaseTerms("24-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("24-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("620.79").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("74.49").
        totalDueAmount("695.28");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario11()

    {

        setLeaseTerms("25-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("25-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("592.57").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("71.11").
        totalDueAmount("663.68");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario12()

    {

        setLeaseTerms("26-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("26-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("564.35").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("67.72").
        totalDueAmount("632.07");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario13()

    {

        setLeaseTerms("27-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("27-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("536.13").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("64.34").
        totalDueAmount("600.47");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario14()

    {

        setLeaseTerms("28-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("28-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("507.92").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("60.95").
        totalDueAmount("568.87");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario15()

    {

        setLeaseTerms("29-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("29-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("479.70").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("57.56").
        totalDueAmount("537.26");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario16()

    {

        setLeaseTerms("30-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("30-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("451.48").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("54.18").
        totalDueAmount("505.66");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario17()

    {

        setLeaseTerms("31-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("31-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("423.27").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("50.79").
        totalDueAmount("474.06");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario18()

    {

        setLeaseTerms("01-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("01-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("395.05").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("47.41").
        totalDueAmount("442.46");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario19()

    {

        setLeaseTerms("02-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("02-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("366.83").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("44.02").
        totalDueAmount("410.85");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario20()

    {

        setLeaseTerms("02-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("02-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("366.83").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("44.02").
        totalDueAmount("410.85");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario21()

    {

        setLeaseTerms("03-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("03-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("338.62").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("40.63").
        totalDueAmount("379.25");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario22()

    {

        setLeaseTerms("04-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("04-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("310.40").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("37.25").
        totalDueAmount("347.65");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario23()

    {

        setLeaseTerms("05-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("05-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("282.18").
    depositAmount("930.30").
    depositRefundAmount("-930.30").
    taxes("33.86").
    totalDueAmount("316.04");
    // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario24()

    {

        setLeaseTerms("06-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("06-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("253.96").
    depositAmount("930.30").
    depositRefundAmount("-930.30").
    taxes("30.48").
    totalDueAmount("284.44");
    // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario25()

    {

        setLeaseTerms("07-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("07-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("225.74").
    depositAmount("930.30").
    depositRefundAmount("-930.30").
    taxes("27.09").
    totalDueAmount("252.83");
    // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario26()

    {

        setLeaseTerms("08-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("08-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("197.53").
    depositAmount("930.30").
    depositRefundAmount("-930.30").
    taxes("23.70").
    totalDueAmount("221.23");
    // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario27()

    {

        setLeaseTerms("09-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("09-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("169.31").
    depositAmount("930.30").
    depositRefundAmount("-930.30").
    taxes("20.32").
    totalDueAmount("189.63");
    // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario28()

    {

        setLeaseTerms("10-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("10-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("141.09").
    depositAmount("930.30").
    depositRefundAmount("-930.30").
    taxes("16.93").
    totalDueAmount("158.02");
    // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario29()

    {

        setLeaseTerms("11-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("11-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("112.87").
    depositAmount("930.30").
    depositRefundAmount("-930.30").
    taxes("13.54").
    totalDueAmount("126.41");
    // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario30()

    {

        setLeaseTerms("12-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("12-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("84.65").
    depositAmount("930.30").
    depositRefundAmount("-930.30").
    taxes("10.16").
    totalDueAmount("94.81");
    // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario31()

    {

        setLeaseTerms("13-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("13-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("56.44").
    depositAmount("930.30").
    depositRefundAmount("-930.30").
    taxes("6.77").
    totalDueAmount("63.21");
    // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario32()

    {

        setLeaseTerms("14-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", Type.monetary);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("14-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("28.22").
        depositAmount("930.30").
        depositRefundAmount("-930.30").
        taxes("3.39").
        totalDueAmount("31.61");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
}
