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
 */
package com.propertyvista.biz.financial.billing.internal;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;

@Category(RegressionTests.class)
public class BillingPADCalculationTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        setSysDate("17-Mar-2011");

        createLease("23-Mar-2011", "03-Aug-2011");

        BillableItem parking1 = addOutdoorParking();

        BillableItem locker1 = addLargeLocker();

        BillableItem pet1 = addCat();

        setLeaseBatchProcess();
        setDepositBatchProcess();

        Bill bill = approveApplication(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        billType(Bill.BillType.First).
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(4).
        paymentReceivedAmount("0.00").
        serviceCharge("270.09").
        recurringFeatureCharges("46.46").
        oneTimeFeatureCharges("0.00").
        depositAmount("1270.30").
        taxes("37.99").
        totalDueAmount("1624.84");
        // @formatter:on

// TODO        assertEquals("PAD Balance", new BigDecimal("348.03"), getPADBalance(bill.billingCycle()));

        //==================== RUN 1 ======================//

        advanceSysDate("18-Mar-2011");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(4).
        paymentReceivedAmount("0.00").
        serviceCharge("930.30").
        recurringFeatureCharges("160.00").
        oneTimeFeatureCharges("0.00").
        taxes("130.84").
        totalDueAmount("2895.98");
        // @formatter:on

// TODO        assertEquals("PAD Balance", new BigDecimal("1244.27"), getPADBalance(bill.billingCycle()));

        //==================== RUN 2 ======================//
        // Negative balance case: PAD amount should be 0.00

        advanceSysDate("18-Apr-2011");

        addGoodWillCredit("5000");

        bill = runBilling(true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(4).
        paymentReceivedAmount("0.00").
        serviceCharge("930.30").
        recurringFeatureCharges("160.00").
        oneTimeFeatureCharges("0.00").
        taxes("130.84").
        totalDueAmount("-832.88"); // no payment required
        // @formatter:on

// TODO        assertTrue("PAD Balance", new BigDecimal("0.00").compareTo(getPADBalance(bill.billingCycle())) == 0);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }
}
