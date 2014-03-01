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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;

@Category(RegressionTests.class)
public class ServiceBillPreviewTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        setSysDate("17-Mar-2011");

        createLease("23-Mar-2011", "03-Aug-2011");
        addOutdoorParking();
        addLargeLocker();

        setBillingBatchProcess();
        setLeaseBatchProcess();
        setDepositBatchProcess();

        //==================== RUN 1 ======================//

        approveApplication(true);
        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(1).
        billType(Bill.BillType.First).
        billingCyclePeriodStartDate("01-Mar-2011").
        billingPeriodStartDate("23-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(3).
        paymentReceivedAmount("0.00").
        serviceCharge("270.09").
        recurringFeatureCharges("40.65").
        oneTimeFeatureCharges("0.00").
        depositAmount("1070.30").
        taxes("37.29").
        totalDueAmount("1418.33");
        // @formatter:on

        advanceSysDate("18-Mar-2011");
        runBilling();
        confirmBill(true);
        new BillTester(getLatestBill()).billSequenceNumber(2).totalDueAmount("2667.07").pastDueAmount("1418.33").billingCyclePeriodStartDate("01-Apr-2011");

        advanceSysDate("18-Apr-2011"); // bill auto generated
        new BillTester(getLatestBill()).billSequenceNumber(3).totalDueAmount("3915.81").pastDueAmount("2667.07").billingCyclePeriodStartDate("01-May-2011");

        advanceSysDate("05-May-2011");
        Lease lease = retrieveLease();
        LogicalDate date = SystemDateManager.getLogicalDate();
        BillingCycle cycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(lease, date);
        // @formatter:off
        new BillTester(InternalBillProducer.produceProductBillPreview(cycle, lease)).
        billSequenceNumber(0).
        billType(Bill.BillType.First).
        billingCyclePeriodStartDate("01-May-2011").
        billingPeriodStartDate("01-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(3).
        paymentReceivedAmount("0.00").
        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("0.00").
        taxes("128.44").
        pastDueAmount("0.00").
        totalDueAmount("1198.74");
        // @formatter:on

        BillingCycle nextCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(cycle);
        // @formatter:off
        new BillTester(InternalBillProducer.produceProductBillPreview(nextCycle, lease)).
        billSequenceNumber(0).
        billType(Bill.BillType.First).
        billingCyclePeriodStartDate("01-Jun-2011").
        billingPeriodStartDate("01-Jun-2011").
        billingPeriodEndDate("30-Jun-2011").
        numOfProductCharges(3).
        paymentReceivedAmount("0.00").
        serviceCharge("930.30").
        recurringFeatureCharges("140.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("0.00").
        taxes("128.44").
        pastDueAmount("0.00").
        totalDueAmount("1198.74");
        // @formatter:on

        new BillTester(getLatestBill()).billSequenceNumber(3).totalDueAmount("3915.81").pastDueAmount("2667.07").billingCyclePeriodStartDate("01-May-2011");
    }
}
