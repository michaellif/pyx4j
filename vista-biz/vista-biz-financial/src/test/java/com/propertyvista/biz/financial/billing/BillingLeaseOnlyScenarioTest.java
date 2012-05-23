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

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingLeaseOnlyScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() {

        setLeaseConditions("1-Mar-2011", "31-Aug-2011", 1);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("17-Feb-2011");
        setLeaseStatus(Lease.Status.Approved);

        Bill bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.First).
        billingPeriodStartDate("1-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("0.00").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("930.30").
        taxes("111.64").
        totalDueAmount("1972.24");
        // @formatter:on

        //==================== RUN 2 ======================//

        SysDateManager.setSysDate("18-Mar-2011");
        receiveAndPostPayment("18-Mar-2011", "1972.24");

        setLeaseStatus(Lease.Status.Active);

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(2).
        previousBillSequenceNumber(1).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("-1972.24").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("1091.94");
        // @formatter:on

        SysDateManager.setSysDate("25-Mar-2011");
        receiveAndPostPayment("25-Mar-2011", "1041.94");

        //==================== RUN 3 ======================//

        SysDateManager.setSysDate("18-Apr-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(3).
        previousBillSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("-1041.94").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        taxes("111.64").
        totalDueAmount("1141.94");
        // @formatter:on

        SysDateManager.setSysDate("25-Apr-2011");
        receiveAndPostPayment("25-Apr-2011", "1041.94");

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

}
