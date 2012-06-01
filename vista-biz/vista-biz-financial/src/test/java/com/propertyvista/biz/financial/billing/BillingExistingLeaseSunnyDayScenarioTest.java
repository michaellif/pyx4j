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

import java.math.BigDecimal;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.billing.Bill;

public class BillingExistingLeaseSunnyDayScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SysDateManager.setSysDate("17-May-2011");
        preloadData();
    }

    public void testScenario() {

        SysDateManager.setSysDate("17-May-2011");

        initLease("1-Mar-2011", "31-Aug-2011", 1, new BigDecimal("300.00"));

        Bill bill = verifyExistingLease();

        bill = confirmBill(bill, true, true);

        //==================== RUN 1 ======================//

        // @formatter:off
        new BillTester(bill).
        billSequenceNumber(1).
        previousBillSequenceNumber(null).
        billType(Bill.BillType.ZeroCycle).
        billingPeriodStartDate("1-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(1).
        paymentReceivedAmount("0.00").
        serviceCharge("930.30").
        recurringFeatureCharges("0.00").
        oneTimeFeatureCharges("0.00").
        depositAmount("930.30").
        taxes("111.64").
        totalDueAmount("1972.24");
        // @formatter:on

        activateLease();

        //==================== RUN 2 ======================//

        SysDateManager.setSysDate("18-Mar-2011");
        receiveAndPostPayment("18-Mar-2011", "1972.24");

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

}
