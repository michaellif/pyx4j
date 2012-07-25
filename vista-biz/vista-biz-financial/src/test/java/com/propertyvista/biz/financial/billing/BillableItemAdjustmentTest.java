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
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment.Type;

@Category(FunctionalTests.class)
public class BillableItemAdjustmentTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() {

        setDate("17-Feb-2011");

        setLeaseTerms("01-Mar-2011", "31-Jul-2011");
        BillableItemAdjustment svcAdj_1 = addServiceAdjustment("-30.30", Type.monetary);
        BillableItemAdjustment svcAdj_2 = addServiceAdjustment("-100", Type.monetary, "01-Apr-2011", "30-Apr-2011");
        BillableItemAdjustment svcAdj_3 = addServiceAdjustment("-31", Type.monetary, "15-May-2011", "14-Jun-2011");

        BillableItem park = addParking(SaveAction.saveAsDraft);
        BillableItemAdjustment parkAdj_1 = addFeatureAdjustment(park.uid().getValue(), "-30", Type.monetary);

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

        changeBillableItemAdjustment(parkAdj_1.uid().getValue(), "01-Mar-2011", "31-Mar-2011", SaveAction.saveAsFinal);

        advanceDate("18-Mar-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        serviceCharge("800.00").
        recurringFeatureCharges("80.00"). // parking - full price
        taxes("105.60"). // 12% (800.00 + 80.00)
        totalDueAmount("985.60");
        // @formatter:on

        receiveAndPostPayment("01-Apr-2011", "985.60");
        //==================== RUN 3 ======================//
        // Service = $883.00 after $30.30 + $17.00 adjustment

        advanceDate("18-Apr-2011");

        bill = runBilling(true, true);

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

        changeBillableItemAdjustment(svcAdj_3.uid().getValue(), "01-Jun-2011", "30-Jun-2011", SaveAction.saveAsFinal);

        advanceDate("18-May-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(4).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-June-2011").
        billingPeriodEndDate("30-June-2011").
        serviceCharge("869.00"). // 930.30 - 30.30 - 31 (changed to EOM)
        recurringFeatureCharges("80.00").
        taxes("113.88").
        totalDueAmount("1062.88");
        // @formatter:on

        receiveAndPostPayment("19-May-2011", "1062.88");
        //==================== RUN 5 ======================//
        // Service = $900.00 after $30.30 adjustment

        advanceDate("18-Jun-2011");

        bill = runBilling(true, true);

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

        advanceDate("05-Aug-2011");

        completeLease();

        bill = runBilling(true, true);

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
