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
package com.propertyvista.biz.financial.ar.internal;

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.ar.TransactionHistoryTester;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.test.integration.IntegrationTestBase.RegressionTests;

@Category(RegressionTests.class)
public class ARSunnyDayScenarioTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        createLease("23-Mar-2011", "03-Aug-2011");
        addOutdoorParking();
        addOutdoorParking("23-Apr-2011", "03-Aug-2011");

        //==================== RUN 1 ======================//

        setSysDate("18-Mar-2011");
        approveApplication(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        new TransactionHistoryTester(retrieveLease().billingAccount()).
        lineItemSize(4).
        notCoveredDebitLineItemSize(4).
        notConsumedCreditInvoiceItemSize(0).
        agingBucketsCurrent(new BigDecimal("1338.82"), null).
        agingBucketsCurrent(new BigDecimal("302.50"), ARCode.Type.Residential).
        agingBucketsCurrent(new BigDecimal("26.02"), ARCode.Type.Parking).
        agingBucketsCurrent(new BigDecimal("1010.30"), ARCode.Type.DepositSecurity);
        // @formatter:on

        // Partial payment - default test ARPolicy rule "oldestDebtFirst" so for same bucket age the smallest amounts covered first
        receiveAndPostPayment("22-Mar-2011", "1040.00");

        // @formatter:off
        new TransactionHistoryTester(retrieveLease().billingAccount()).
        lineItemSize(5).
        notCoveredDebitLineItemSize(1).
        notConsumedCreditInvoiceItemSize(0).
        agingBucketsCurrent(new BigDecimal("298.82"), null).
        agingBucketsCurrent(new BigDecimal("298.82"), ARCode.Type.DepositSecurity);
        // @formatter:on

        //==================== RUN 2 ======================//

        setSysDate("18-Mar-2011");
        activateLease();

        runBilling(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        receiveAndPostPayment("19-Mar-2011", "1067.01");
        receiveAndPostPayment("20-Mar-2011", "100.00");

        //==================== RUN 3 ======================//

        setSysDate("18-Apr-2011");

        addBooking("25-Apr-2011");
        addBooking("05-May-2011");
        finalizeLeaseAdendum();

        runBilling(true);

        receiveAndPostPayment("19-Apr-2011", "1448.50");

        //==================== RUN 4 ======================//

        addBooking("28-Apr-2011");
        finalizeLeaseAdendum();

        setSysDate("18-May-2011");

        addGoodWillCredit("120.00", false);
        addGoodWillCredit("130.00");

        runBilling(true);

        receiveAndPostPayment("19-May-2011", "1086.50");

        //==================== RUN 5 ======================//

        setSysDate("18-Jun-2011");

        addGoodWillCredit("30.00", false);
        addGoodWillCredit("40.00");

        runBilling(true);

        receiveAndPostPayment("19-Jun-2011", "1154.50");

        //==================== RUN 6 ======================//

        setSysDate("18-Jul-2011");

        runBilling(true);

        receiveAndPostPayment("19-Jul-2011", "118.49");

        //==================== RUN final ======================//

        setSysDate("05-Aug-2011");

        completeLease();

        runBilling(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

}
