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
public class BillingBulkRunTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() {

        SysDateManager.setSysDate("17-Mar-2011");

        setLeaseTerms("23-Mar-2011", "03-Aug-2011");
        addServiceAdjustment("-25", Type.monetary);

        BillableItem parking1 = addParking();
        addFeatureAdjustment(parking1.uid().getValue(), "-10", Type.monetary);

        BillableItem parking2 = addParking("23-Apr-2011", "03-Aug-2011");
        addFeatureAdjustment(parking2.uid().getValue(), "-10", Type.monetary);

        BillableItem locker1 = addLocker();
        addFeatureAdjustment(locker1.uid().getValue(), "-0.2", Type.percentage);

        BillableItem pet1 = addPet();
        addFeatureAdjustment(pet1.uid().getValue(), "-1", Type.percentage);

        //==================== RUN 1 ======================//

        long startTime = System.currentTimeMillis();
        int index = 10;

        for (int i = 0; i < index; i++) {

            Bill billPreview = runBillingPreview();

            // @formatter:off
        new BillTester(billPreview).
        billSequenceNumber(0).
        previousBillSequenceNumber(null).
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