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
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.RegressionTests;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;

@Category(RegressionTests.class)
public class ARSunnyDayScenarioTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() {

        setLeaseTerms("23-Mar-2011", "03-Aug-2011");
        addParking();
        addParking("23-Apr-2011", "03-Aug-2011");

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("18-Mar-2011");
        approveApplication(true);

        printTransactionHistory(ARTransactionManager.getTransactionHistory(retrieveLease().billingAccount()));

        // @formatter:off
        new TransactionHistoryTester(retrieveLease().billingAccount()).
        lineItemSize(4).
        notCoveredDebitLineItemSize(4).
        notConsumedCreditInvoiceItemSize(0).
        agingBucketsCurrent(new BigDecimal("1338.82"), DebitType.total).
        agingBucketsCurrent(new BigDecimal("302.50"), DebitType.lease).
        agingBucketsCurrent(new BigDecimal("26.02"), DebitType.parking).
        agingBucketsCurrent(new BigDecimal("1010.30"), DebitType.deposit);
        // @formatter:on

        receiveAndPostPayment("22-Mar-2011", "1000.00");

        // @formatter:off
        new TransactionHistoryTester(retrieveLease().billingAccount()).
        lineItemSize(5).
        notCoveredDebitLineItemSize(2).
        notConsumedCreditInvoiceItemSize(0).
        agingBucketsCurrent(new BigDecimal("338.82"), DebitType.total).
        agingBucketsCurrent(new BigDecimal("338.82"), DebitType.deposit);
        // @formatter:on

        //==================== RUN 2 ======================//

        SysDateManager.setSysDate("18-Mar-2011");
        activateLease();

        runBilling(true, false);

        printTransactionHistory(ARTransactionManager.getTransactionHistory(retrieveLease().billingAccount()));

        receiveAndPostPayment("19-Mar-2011", "1067.01");
        receiveAndPostPayment("20-Mar-2011", "100.00");

        //==================== RUN 3 ======================//

        SysDateManager.setSysDate("18-Apr-2011");

        addBooking("25-Apr-2011");
        addBooking("5-May-2011");
        finalizeLeaseAdendum();

        runBilling(true, false);

        receiveAndPostPayment("19-Apr-2011", "1448.50");

        //==================== RUN 4 ======================//

        addBooking("28-Apr-2011");
        finalizeLeaseAdendum();

        SysDateManager.setSysDate("18-May-2011");

        addGoodWillCredit("120.00", false);
        addGoodWillCredit("130.00");

        runBilling(true, false);

        receiveAndPostPayment("19-May-2011", "1086.50");

        //==================== RUN 5 ======================//

        SysDateManager.setSysDate("18-Jun-2011");

        addGoodWillCredit("30.00", false);
        addGoodWillCredit("40.00");

        runBilling(true, false);

        receiveAndPostPayment("19-Jun-2011", "1154.50");

        //==================== RUN 6 ======================//

        SysDateManager.setSysDate("18-Jul-2011");

        runBilling(true, false);

        receiveAndPostPayment("19-Jul-2011", "118.49");

        //==================== RUN final ======================//

        SysDateManager.setSysDate("05-Aug-2011");

        completeLease();

        runBilling(true, false);

        printTransactionHistory(ARTransactionManager.getTransactionHistory(retrieveLease().billingAccount()));

    }

}
