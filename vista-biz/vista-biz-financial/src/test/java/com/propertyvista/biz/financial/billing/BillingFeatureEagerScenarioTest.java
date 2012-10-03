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

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.Type;

@Category(FunctionalTests.class)
public class BillingFeatureEagerScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testSequentialBillingCycle() {

        setDate("17-Mar-2011");

        createLease("23-Mar-2011", "3-Aug-2011");

        BillableItem parking1 = addParking();
        addFeatureAdjustment(parking1.uid().getValue(), "-10", Type.monetary);

        BillableItem parking2 = addParking("23-Apr-2011", null);
        addFeatureAdjustment(parking2.uid().getValue(), "-10", Type.monetary);

        BillableItem locker1 = addLocker();
        addFeatureAdjustment(locker1.uid().getValue(), "-0.2", Type.percentage);

        BillableItem pet1 = addPet();
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("18-Mar-2011");

        Bill bill = approveApplication(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(4).
        serviceCharge("270.09").
        recurringFeatureCharges("34.27").
        oneTimeFeatureCharges("0.00");
        // @formatter:on

        //==================== RUN 2 ======================//

        SysDateManager.setSysDate("18-Mar-2011");
        activateLease();

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(5).
        serviceCharge("930.30").
        recurringFeatureCharges("136.66").
        oneTimeFeatureCharges("0.00");
        // @formatter:on

        //==================== RUN 3 ======================//

        SysDateManager.setSysDate("18-Apr-2011");
        addPet("10-Apr-2011", null);
        changeBillableItem(parking1.uid().getValue(), null, "20-May-2011");
        finalizeLeaseAdendum();

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        previousBillSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(7).
        serviceCharge("930.30").
        recurringFeatureCharges("197.16").
        oneTimeFeatureCharges("0.00");
        // @formatter:on

        //==================== RUN 4 ======================//

        SysDateManager.setSysDate("18-May-2011");
        //TODO calculate arrears
        changeBillableItem(parking1.uid().getValue(), null, "10-May-2011");
        finalizeLeaseAdendum();

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(4).
        previousBillSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-June-2011").
        billingPeriodEndDate("30-June-2011").
        numOfProductCharges(6).
        serviceCharge("930.30").
        recurringFeatureCharges("160.58").
        oneTimeFeatureCharges("0.00");
        // @formatter:on

        //==================== RUN 5 ======================//

        SysDateManager.setSysDate("18-Jun-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        previousBillSequenceNumber(4).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        numOfProductCharges(5).
        serviceCharge("930.30").
        recurringFeatureCharges("138.00").
        oneTimeFeatureCharges("0.00");
        // @formatter:on

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
        serviceCharge("90.03").
        recurringFeatureCharges("13.36").
        oneTimeFeatureCharges("0.00").
        taxes("12.41");
        // @formatter:on

        //==================== RUN final ======================//

        SysDateManager.setSysDate("05-Aug-2011");

        completeLease();

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(7).
        previousBillSequenceNumber(6).
        billType(Bill.BillType.Final).
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on
    }
}
