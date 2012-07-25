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

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.preload.PreloadConfig;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.Type;

@Category(FunctionalTests.class)
public class BillingAllFeaturesAdjustmentsFirstMonthProration extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreloadConfig config = new PreloadConfig();
        config.defaultBillingCycleSartDay = 15;
        preloadData(config);
    }

    public void testScenario1() {

        setLeaseTerms("15-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("01-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("15-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("735.20").
        recurringFeatureCharges("188.78").
        depositAmount("1350.30").
        taxes("110.88").
        totalDueAmount("2385.16");
        // @formatter:on

        if (true) {
            printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
        }

    }

    public void testScenario2() {

        setLeaseTerms("16-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("02-Jan-2013");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("16-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("711.49").
        recurringFeatureCharges("182.69").
        depositAmount("1350.30").
        taxes("107.30").
        //totalDueAmount("1940.81");
        totalDueAmount("2351.78");
        // @formatter:on

    }

    public void testScenario3() {

        setLeaseTerms("17-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("17-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("687.77").
        recurringFeatureCharges("176.61").
        depositAmount("1350.30").
        taxes("103.73").
        totalDueAmount("2318.41");
        // @formatter:on

    }

    public void testScenario4() {

        setLeaseTerms("18-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("18-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("664.06").
        recurringFeatureCharges("170.51").
        depositAmount("1350.30").
        taxes("100.15").
        totalDueAmount("2285.02");
        // @formatter:on

    }

    public void testScenario5() {

        setLeaseTerms("19-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("19-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("640.34").
        recurringFeatureCharges("164.43").
        depositAmount("1350.30").
        taxes("96.57").
        totalDueAmount("2251.64");
        // @formatter:on

    }

    public void testScenario6() {

        setLeaseTerms("20-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("20-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("616.62").
        recurringFeatureCharges("158.34").
        depositAmount("1350.30").
        taxes("93.00").
        totalDueAmount("2218.26");
        // @formatter:on

    }

    public void testScenario7() {

        setLeaseTerms("21-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("21-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("592.90").
        recurringFeatureCharges("152.25").
        depositAmount("1350.30").
        taxes("89.42").
        totalDueAmount("2184.87");
        // @formatter:on

    }

    public void testScenario8() {

        setLeaseTerms("22-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("22-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("569.19").
        recurringFeatureCharges("146.15").
        depositAmount("1350.30").
        taxes("85.84").
        totalDueAmount("2151.48");
        // @formatter:on

    }

    public void testScenario9() {

        setLeaseTerms("23-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("545.48").
        recurringFeatureCharges("140.05").
        depositAmount("1350.30").
        taxes("82.26").
        totalDueAmount("2118.09");
        // @formatter:on

    }

    public void testScenario10() {

        setLeaseTerms("24-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("24-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("521.76").
        recurringFeatureCharges("133.96").
        depositAmount("1350.30").
        taxes("78.69").
        totalDueAmount("2084.71");
        // @formatter:on

    }

    public void testScenario11() {

        setLeaseTerms("25-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("25-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("498.04").
        recurringFeatureCharges("127.88").
        depositAmount("1350.30").
        taxes("75.11").
        totalDueAmount("2051.33");
        // @formatter:on

    }

    public void testScenario12() {

        setLeaseTerms("26-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("26-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("474.32").
        recurringFeatureCharges("121.79").
        depositAmount("1350.30").
        taxes("71.53").
        totalDueAmount("2017.94");
        // @formatter:on

    }

    public void testScenario13() {

        setLeaseTerms("27-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("27-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("450.60").
        recurringFeatureCharges("115.69").
        depositAmount("1350.30").
        taxes("67.95").
        totalDueAmount("1984.54");
        // @formatter:on

    }

    public void testScenario14() {

        setLeaseTerms("28-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
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
        numOfProductCharges(5).
        serviceCharge("426.89").
        recurringFeatureCharges("109.61").
        depositAmount("1350.30").
        taxes("64.38").
        totalDueAmount("1951.18");
        // @formatter:on

    }

    public void testScenario15() {

        setLeaseTerms("29-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("29-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("403.18").
        recurringFeatureCharges("103.52").
        depositAmount("1350.30").
        taxes("60.80").
        totalDueAmount("1917.80");
        // @formatter:on

    }

    public void testScenario16() {

        setLeaseTerms("30-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("30-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("379.46").
        recurringFeatureCharges("97.44").
        depositAmount("1350.30").
        taxes("57.23").
        totalDueAmount("1884.43");
        // @formatter:on

    }

    public void testScenario17() {

        setLeaseTerms("31-Jan-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("31-Jan-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("355.75").
        recurringFeatureCharges("91.34").
        depositAmount("1350.30").
        taxes("53.65").
        totalDueAmount("1851.04");
        // @formatter:on

    }

    public void testScenario18() {

        setLeaseTerms("01-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("01-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("332.03").
        recurringFeatureCharges("85.26").
        depositAmount("1350.30").
        taxes("50.07").
        totalDueAmount("1817.66");
        // @formatter:on

    }

    public void testScenario19() {

        setLeaseTerms("02-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("02-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("308.31").
        recurringFeatureCharges("79.17").
        depositAmount("1350.30").
        taxes("46.50").
        totalDueAmount("1784.28");
        // @formatter:on

    }

    public void testScenario20() {

        setLeaseTerms("03-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("03-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("284.60").
        recurringFeatureCharges("73.09").
        depositAmount("1350.30").
        taxes("42.92").
        totalDueAmount("1750.91");
        // @formatter:on

    }

    public void testScenario21() {

        setLeaseTerms("04-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("04-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("260.88").
        recurringFeatureCharges("66.99").
        depositAmount("1350.30").
        taxes("39.34").
        totalDueAmount("1717.51");
        // @formatter:on

    }

    public void testScenario22() {

        setLeaseTerms("05-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("05-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("237.17").
        recurringFeatureCharges("60.90").
        depositAmount("1350.30").
        taxes("35.77").
        totalDueAmount("1684.14");
        
        // @formatter:on

    }

    public void testScenario23() {

        setLeaseTerms("06-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("06-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("213.45").
        recurringFeatureCharges("54.82").
        depositAmount("1350.30").
        taxes("32.19").
        totalDueAmount("1650.76");
        // @formatter:on

    }

    public void testScenario24() {

        setLeaseTerms("07-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("07-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("189.73").
        recurringFeatureCharges("48.73").
        depositAmount("1350.30").
        taxes("28.62").
        totalDueAmount("1617.38");
        // @formatter:on

    }

    public void testScenario25() {

        setLeaseTerms("08-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("08-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("166.02").
        recurringFeatureCharges("42.63").
        depositAmount("1350.30").
        taxes("25.04").
        totalDueAmount("1583.99");
        // @formatter:on

    }

    public void testScenario26() {

        setLeaseTerms("09-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("09-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("142.30").
        recurringFeatureCharges("36.53").
        depositAmount("1350.30").
        taxes("21.46").
        totalDueAmount("1550.59");
        // @formatter:on

    }

    public void testScenario27() {

        setLeaseTerms("10-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("10-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("118.58").
        recurringFeatureCharges("30.44").
        depositAmount("1350.30").
        taxes("17.88").
        totalDueAmount("1517.20");
        // @formatter:on

    }

    public void testScenario28() {

        setLeaseTerms("11-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("11-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("94.86").
        recurringFeatureCharges("24.35").
        depositAmount("1350.30").
        taxes("14.31").
        totalDueAmount("1483.82");
        // @formatter:on

    }

    public void testScenario29() {

        setLeaseTerms("12-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("12-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("71.15").
        recurringFeatureCharges("18.27").
        depositAmount("1350.30").
        taxes("10.73").
        totalDueAmount("1450.45");
        // @formatter:on

    }

    public void testScenario30() {

        setLeaseTerms("13-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("13-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("47.44").
        recurringFeatureCharges("12.17").
        depositAmount("1350.30").
        taxes("7.15").
        totalDueAmount("1417.06");
        // @formatter:on

    }

    public void testScenario31() {

        setLeaseTerms("14-Feb-2012", "14-Feb-2013");
        addServiceAdjustment("-55.55", Type.monetary);
        addServiceAdjustment("-0.15", Type.percentage);
        BillableItem parking1 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking1.uid().getValue(), "-3.03", Type.monetary);
        BillableItem parking2 = addParking(SaveAction.saveAsDraft);
        addFeatureAdjustment(parking2.uid().getValue(), "-17.99", Type.monetary);
        BillableItem locker1 = addLocker(SaveAction.saveAsDraft);
        addFeatureAdjustment(locker1.uid().getValue(), "-0.17", Type.percentage);
        BillableItem pet1 = addPet(SaveAction.saveAsDraft);
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("03-Jan-2012");
        Bill bill = approveApplication(true);

        // @formatter:off
        ///debug mode:
        /// new BillTester(bill, true).
         
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("14-Feb-2012").
        billingPeriodEndDate("14-Feb-2012").
        numOfProductCharges(5).
        serviceCharge("23.72").
        recurringFeatureCharges("6.09").
        depositAmount("1350.30").
        taxes("3.58").
        totalDueAmount("1383.69");
        // @formatter:on

    }

}
