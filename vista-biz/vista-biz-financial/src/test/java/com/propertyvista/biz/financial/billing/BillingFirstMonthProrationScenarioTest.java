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
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.AdjustmentType;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.ExecutionType;

public class BillingFirstMonthProrationScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreloadConfig config = new PreloadConfig();
        config.defaultBillingCycleSartDay = 15;
        preloadData(config);
    }

    public void testScenario1() {

        initLease("15-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

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
        taxes("104.97").
        totalDueAmount("1910.02");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario2() {

        initLease("16-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

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
        taxes("101.58").
        totalDueAmount("1878.41");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
    }

    public void testScenario3()

    {

        initLease("17-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).previousBillSequenceNumber(null).billType(Bill.BillType.First).billingPeriodStartDate("17-Jan-2012")
                .billingPeriodEndDate("14-Feb-2012").numOfProductCharges(1).serviceCharge("818.31").depositAmount("930.30").taxes("98.20")
                .totalDueAmount("1846.81");

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario4() {

        initLease("18-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

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
        taxes("94.81").
        totalDueAmount("1815.21");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario5() {

        initLease("19-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

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
        taxes("91.43").
        totalDueAmount("1783.61");
        // @formatter:on

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario6()

    {

        initLease("20-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).previousBillSequenceNumber(null).billType(Bill.BillType.First).billingPeriodStartDate("20-Jan-2012")
                .billingPeriodEndDate("14-Feb-2012").numOfProductCharges(1).serviceCharge("733.66").depositAmount("930.30").taxes("88.04")
                .totalDueAmount("1752.00");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario7()
    
    {

        initLease("21-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("21-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("705.44").
        depositAmount("930.30").
        taxes("84.65").
        totalDueAmount("1720.39");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario8()
    
    {

        initLease("22-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("22-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("677.22").
        depositAmount("930.30").
        taxes("81.27").
        totalDueAmount("1688.79");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario9() 
    
    {

        initLease("23-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("649.01").
        depositAmount("930.30").
        taxes("77.88").
        totalDueAmount("1657.19");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario10() 
    
    {

        initLease("24-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("24-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("620.79").
        depositAmount("930.30").
        taxes("74.49").
        totalDueAmount("1625.58");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario11() 
    
    {

        initLease("25-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("25-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("592.57").
        depositAmount("930.30").
        taxes("71.11").
        totalDueAmount("1593.98");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario12() 
    
    {

        initLease("26-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("26-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("564.35").
        depositAmount("930.30").
        taxes("67.72").
        totalDueAmount("1562.37");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario13() 
    
    {

        initLease("27-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("27-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("536.13").
        depositAmount("930.30").
        taxes("64.34").
        totalDueAmount("1530.77");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario14() 
    
    {

        initLease("28-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("28-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("507.92").
        depositAmount("930.30").
        taxes("60.95").
        totalDueAmount("1499.17");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario15() 
    
    {

        initLease("29-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("29-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("479.70").
        depositAmount("930.30").
        taxes("57.56").
        totalDueAmount("1467.56");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario16()
    
    {

        initLease("30-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("30-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("451.48").
        depositAmount("930.30").
        taxes("54.18").
        totalDueAmount("1435.96");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario17() 
    
    {

        initLease("31-Jan-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("31-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("423.27").
        depositAmount("930.30").
        taxes("50.79").
        totalDueAmount("1404.36");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario18()
    
    {

        initLease("01-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("01-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("395.05").
        depositAmount("930.30").
        taxes("47.41").
        totalDueAmount("1372.76");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario19() 
    
    {

        initLease("02-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("02-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("366.83").
        depositAmount("930.30").
        taxes("44.02").
        totalDueAmount("1341.15");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    public void testScenario20()
    
    {

        initLease("02-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("02-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("366.83").
        depositAmount("930.30").
        taxes("44.02").
        totalDueAmount("1341.15");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
 
    public void testScenario21()
    
    {

        initLease("03-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("03-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("338.62").
        depositAmount("930.30").
        taxes("40.63").
        totalDueAmount("1309.55");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
    
public void testScenario22()
    
    {

        initLease("04-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("04-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("310.40").
        depositAmount("930.30").
        taxes("37.25").
        totalDueAmount("1277.95");
        // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
public void testScenario23()

{

    initLease("05-Feb-2012", "14-Feb-2012");
    addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

    SysDateManager.setSysDate("01-Jan-2012");
    Bill bill = approveApplication();

    bill = confirmBill(bill, true, true);

    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("05-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("282.18").
    depositAmount("930.30").
    taxes("33.86").
    totalDueAmount("1246.34");
    // @formatter:off

    // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

}
public void testScenario24()

{

    initLease("06-Feb-2012", "14-Feb-2012");
    addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

    SysDateManager.setSysDate("01-Jan-2012");
    Bill bill = approveApplication();

    bill = confirmBill(bill, true, true);

    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("06-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("253.96").
    depositAmount("930.30").
    taxes("30.48").
    totalDueAmount("1214.74");
    // @formatter:off

    // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

}
public void testScenario25()

{

    initLease("07-Feb-2012", "14-Feb-2012");
    addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

    SysDateManager.setSysDate("01-Jan-2012");
    Bill bill = approveApplication();

    bill = confirmBill(bill, true, true);

    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("07-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("225.74").
    depositAmount("930.30").
    taxes("27.09").
    totalDueAmount("1183.13");
    // @formatter:off

    // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

}
public void testScenario26()

{

    initLease("08-Feb-2012", "14-Feb-2012");
    addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

    SysDateManager.setSysDate("01-Jan-2012");
    Bill bill = approveApplication();

    bill = confirmBill(bill, true, true);

    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("08-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("197.53").
    depositAmount("930.30").
    taxes("23.70").
    totalDueAmount("1151.53");
    // @formatter:off

    // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

}
public void testScenario27()

{

    initLease("09-Feb-2012", "14-Feb-2012");
    addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

    SysDateManager.setSysDate("01-Jan-2012");
    Bill bill = approveApplication();

    bill = confirmBill(bill, true, true);

    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("09-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("169.31").
    depositAmount("930.30").
    taxes("20.32").
    totalDueAmount("1119.93");
    // @formatter:off

    // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

}
public void testScenario28()

{

    initLease("10-Feb-2012", "14-Feb-2012");
    addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

    SysDateManager.setSysDate("01-Jan-2012");
    Bill bill = approveApplication();

    bill = confirmBill(bill, true, true);

    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("10-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("141.09").
    depositAmount("930.30").
    taxes("16.93").
    totalDueAmount("1088.32");
    // @formatter:off

    // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

}
public void testScenario29()

{

    initLease("11-Feb-2012", "14-Feb-2012");
    addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

    SysDateManager.setSysDate("01-Jan-2012");
    Bill bill = approveApplication();

    bill = confirmBill(bill, true, true);

    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("11-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("112.87").
    depositAmount("930.30").
    taxes("13.54").
    totalDueAmount("1056.71");
    // @formatter:off

    // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

}
public void testScenario30()

{

    initLease("12-Feb-2012", "14-Feb-2012");
    addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

    SysDateManager.setSysDate("01-Jan-2012");
    Bill bill = approveApplication();

    bill = confirmBill(bill, true, true);

    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("12-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("84.65").
    depositAmount("930.30").
    taxes("10.16").
    totalDueAmount("1025.11");
    // @formatter:off

    // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

}
public void testScenario31()

{

    initLease("13-Feb-2012", "14-Feb-2012");
    addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

    SysDateManager.setSysDate("01-Jan-2012");
    Bill bill = approveApplication();

    bill = confirmBill(bill, true, true);

    new BillTester(bill).billSequenceNumber(1).
    previousBillSequenceNumber(null).
    billType(Bill.BillType.First).
    billingPeriodStartDate("13-Feb-2012").
    billingPeriodEndDate("14-Feb-2012").
    numOfProductCharges(1).
    serviceCharge("56.44").
    depositAmount("930.30").
    taxes("6.77").
    totalDueAmount("993.51");
    // @formatter:off

        // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

    public void testScenario32()

    {

        initLease("14-Feb-2012", "14-Feb-2012");
        addServiceAdjustment("-55.55", AdjustmentType.monetary, ExecutionType.inLease);

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        new BillTester(bill).billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("14-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(1).
        serviceCharge("28.22").
        depositAmount("930.30").
        taxes("3.39").
        totalDueAmount("961.91");
        // @formatter:off

    // printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

}
}
