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
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class BillableItemAdjustmentTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        setSysDate("17-Feb-2011");

        createLease("01-Mar-2011", "31-Jul-2011");
        BillableItemAdjustment svcAdj_1 = addServiceAdjustment("-30.30", ValueType.Monetary);
        BillableItemAdjustment svcAdj_2 = addServiceAdjustment("-100", ValueType.Monetary, "01-Apr-2011", "30-Apr-2011");
        BillableItemAdjustment svcAdj_3 = addServiceAdjustment("-31", ValueType.Monetary, "15-May-2011", "14-Jun-2011");

        BillableItem park = addOutdoorParking();
        BillableItemAdjustment parkAdj_1 = addFeatureAdjustment(park.uuid().getValue(), "-30", ValueType.Monetary);

        //==================== RUN 1 ======================//
        // Service = 900 after $30.30 adjustment; parking = 50 after $30 adjustment

        Bill billPreview = runBillingPreview();
        // @formatter:off
        new BillTester(billPreview).
        billSequenceNumber(0).
        billType(Bill.BillType.First).
        billingPeriodStartDate("01-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        serviceCharge("900.00").
        recurringFeatureCharges("50.00"). // parking - 30.00
        depositAmount("1010.30"). // 930.30 + 80.00
        taxes("114.00"). // 12% (900.00 + (80.00-30.00))
        totalDueAmount("2074.30"); // 1010.30 + 900.00 + 50.00 + 114.00
        // @formatter:on

        Bill bill = approveApplication(true);

        receiveAndPostPayment("01-Mar-2011", "2074.30");

        //==================== RUN 2 ======================//
        // Service = $800 after $30.30 + $100 adjustment; parking = $80 ($30 adjustment changed to end early)

        activateLease();

        changeBillableItemAdjustment(parkAdj_1.uuid().getValue(), "01-Mar-2011", "31-Mar-2011");

        advanceSysDate("18-Mar-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        serviceCharge("800.00").
        recurringFeatureCharges("80.00"). // parking - full price
        taxes("105.60"). // 12% (800.00 + 80.00)
        totalDueAmount("985.60");
        // @formatter:on

        receiveAndPostPayment("01-Apr-2011", "985.60");
        //==================== RUN 3 ======================//
        // Service = $883.00 after $30.30 + $17.00 adjustment

        advanceSysDate("18-Apr-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        serviceCharge("883.00"). // 930.30 - 30.30 - 17.00
        recurringFeatureCharges("80.00").
        taxes("115.56").
        totalDueAmount("1078.56");
        // @formatter:on

        receiveAndPostPayment("01-May-2011", "1078.56");
        //==================== RUN 4 ======================//
        // Service = $869.00 after $30.30 + $31.00 adjustment (changed till EOM)

        changeBillableItemAdjustment(svcAdj_3.uuid().getValue(), "01-Jun-2011", "30-Jun-2011");

        advanceSysDate("18-May-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(4).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jun-2011").
        billingPeriodEndDate("30-Jun-2011").
        serviceCharge("1769.00"). // 930.30 - 30.30 - 31 (changed to EOM) + 900 (revised charge for may)
        recurringFeatureCharges("80.00").
        taxes("221.88").
        totalDueAmount("1081.92");
        // @formatter:on

        receiveAndPostPayment("19-May-2011", "1081.92");
        //==================== RUN 5 ======================//
        // Service = $900.00 after $30.30 adjustment

        advanceSysDate("18-Jun-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(5).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        serviceCharge("900.00").
        recurringFeatureCharges("80.00").
        depositRefundAmount("-930.30").
        taxes("117.60").
        totalDueAmount("167.30");
        // @formatter:on

        //==================== RUN final ======================//

        advanceSysDate("05-Aug-2011");

        completeLease();

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(6).
        billType(Bill.BillType.Final).
        billingPeriodStartDate(null).
        billingPeriodEndDate(null);
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));
    }
}
