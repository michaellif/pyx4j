/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2012
 * @author michaellif
 */
package com.propertyvista.biz.financial.billing.internal;

import org.junit.experimental.categories.Category;

import com.pyx4j.entity.shared.IMoneyPercentAmount.ValueType;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class BillingBulkRunTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() {

        setSysDate("17-Mar-2011");

        createLease("23-Mar-2011", "03-Aug-2011");
        addServiceAdjustment("-25", ValueType.Monetary);

        BillableItem parking1 = addOutdoorParking();
        addFeatureAdjustment(parking1.uuid().getValue(), "-10", ValueType.Monetary);

        BillableItem parking2 = addOutdoorParking("23-Apr-2011", "03-Aug-2011");
        addFeatureAdjustment(parking2.uuid().getValue(), "-10", ValueType.Monetary);

        BillableItem locker1 = addLargeLocker();
        addFeatureAdjustment(locker1.uuid().getValue(), "-0.2", ValueType.Percentage);

        BillableItem pet1 = addCat();
        addFeatureAdjustment(pet1.uuid().getValue(), "-1", ValueType.Percentage);

        //==================== RUN 1 ======================//

        long startTime = System.currentTimeMillis();
        int index = 10;

        for (int i = 0; i < index; i++) {

            Bill billPreview = runBillingPreview();

            // @formatter:off
        new BillTester(billPreview).
        billSequenceNumber(0).
        billType(Bill.BillType.First).
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

        }

        System.out.println("Average Billing Time - " + (System.currentTimeMillis() - startTime) / index + "ms");

    }

}