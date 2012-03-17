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
package com.propertyvista.server.billing;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.ActionType;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.AdjustmentType;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingFeatureEagerScenarioTest extends BillingTestBase {

    public void testSequentialBillingRun() {
        preloadData();
        setLeaseConditions("23-Mar-2011", "3-Aug-2011", 1);

        BillableItem parking1 = addParking();
        addBillableItemAdjustment(parking1.originalId().getValue(), "-10", AdjustmentType.monetary, ActionType.inLease);

        BillableItem parking2 = addParking("23-Apr-2011", null);
        addBillableItemAdjustment(parking2.originalId().getValue(), "-10", AdjustmentType.monetary, ActionType.inLease);

        BillableItem locker1 = addLocker();
        addBillableItemAdjustment(locker1.originalId().getValue(), "-0.2", AdjustmentType.percentage, ActionType.inLease);

        BillableItem pet1 = addPet();
        addBillableItemAdjustment(pet1.originalId().getValue(), "-1", AdjustmentType.percentage, ActionType.inLease);

        //==================== RUN 1 ======================//

        setSysDate("18-Mar-2011");

        setLeaseStatus(Lease.Status.Approved);

        Bill bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfCharges(4).
        numOfChargeAdjustments(3).
        numOfLeaseAdjustments(0).
        serviceCharge("270.09").
        recurringFeatureCharges("46.45").
        totalAdjustments("-12.19").
        oneTimeFeatureCharges("0.00");
        // @formatter:on

        //==================== RUN 2 ======================//

        setSysDate("18-Mar-2011");
        setLeaseStatus(Lease.Status.Active);

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfCharges(5).
        numOfChargeAdjustments(4).
        numOfLeaseAdjustments(0).
        serviceCharge("930.30").
        recurringFeatureCharges("181.33").
        totalAdjustments("-44.67").
        oneTimeFeatureCharges("0.00");
        // @formatter:on

        //==================== RUN 3 ======================//

        setSysDate("18-Apr-2011");
        BillableItem pet2 = addPet("10-Apr-2011", null);
        changeBillableItem(parking1.originalId().getValue(), null, "20-May-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        previousBillSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfCharges(7).
        numOfChargeAdjustments(4).
        numOfLeaseAdjustments(0).
        serviceCharge("930.30").
        recurringFeatureCharges("245.61").
        totalAdjustments("-48.45").
        oneTimeFeatureCharges("0.00");
        // @formatter:on

        //==================== RUN 4 ======================//

        setSysDate("18-May-2011");
        //TODO calculate arrears
        changeBillableItem(parking1.originalId().getValue(), null, "10-May-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(4).
        previousBillSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-June-2011").
        billingPeriodEndDate("30-June-2011").
        numOfCharges(5).
        numOfChargeAdjustments(3).
        numOfLeaseAdjustments(0).
        serviceCharge("930.30").
        recurringFeatureCharges("180.00").
        totalAdjustments("-42.00").
        oneTimeFeatureCharges("0.00");
        // @formatter:on

        //==================== RUN 5 ======================//

        setSysDate("18-Jun-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        previousBillSequenceNumber(4).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        numOfCharges(5).
        numOfChargeAdjustments(3).
        numOfLeaseAdjustments(0).
        serviceCharge("930.30").
        recurringFeatureCharges("180.00").
        totalAdjustments("-42.00").
        oneTimeFeatureCharges("0.00");
        // @formatter:on

        //==================== RUN 6 ======================//

        setSysDate("18-Jul-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        previousBillSequenceNumber(5).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Aug-2011").
        billingPeriodEndDate("03-Aug-2011").
        numOfCharges(5).
        numOfChargeAdjustments(3).
        numOfLeaseAdjustments(0).
        serviceCharge("90.03").
        recurringFeatureCharges("17.42").
        totalAdjustments("-4.06").
        oneTimeFeatureCharges("0.00").
        taxes("12.40");
        // @formatter:on

        //==================== RUN final ======================//

        setSysDate("05-Aug-2011");

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
    }

}
