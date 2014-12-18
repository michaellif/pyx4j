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

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class BillingExistingLeaseSunnyDayScenarioTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        setLeaseBatchProcess();
        setBillingBatchProcess();

        setSysDate("15-May-2011");

        createLease("01-Mar-2009", "31-Aug-2011", null, new BigDecimal("300.00"));

        advanceSysDate("22-May-2011");
        approveExistingLease(true);

        //==================== ZERO CYCLE ======================//

        // @formatter:off
        new BillTester(getBill(1)).
        billSequenceNumber(1).
        billType(Bill.BillType.ZeroCycle).
        billingPeriodStartDate("01-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("0.00").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("0.00").
        taxes("111.64").
        totalDueAmount("300.00");
        // @formatter:on

        //==================== CYCLE 1 ======================//

        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jun-2011").
        billingPeriodEndDate("30-Jun-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("0.00").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("0.00").
        taxes("111.64").
        totalDueAmount("1391.94");
        // @formatter:on

        advanceSysDate("25-May-2011");
        receiveAndPostPayment("25-May-2011", "1391.94");

        //==================== CYCLE 2 ======================//

        advanceSysDate("18-Jun-2011");
        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("-1391.94").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("0.00").
        taxes("111.64").
        totalDueAmount("1041.94");
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

}
