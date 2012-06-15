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

import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;

public class ARCreditDebitLinkManagerTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() {

        initLease("01-Mar-2011", "31-Aug-2011");
        addParking(SaveAction.saveAsDraft);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("22-Feb-2011");
        Bill bill = approveApplication();

        bill = confirmBill(bill, true, true);

        // @formatter:off
        new TransactionHistoryTester(retrieveLease().billingAccount()).
        lineItemSize(3).
        notCoveredDebitLineItemSize(3).
        notConsumedCreditInvoiceItemSize(0).
        outstandingDebit(new BigDecimal("1041.94"), 0).
        outstandingDebit(new BigDecimal("89.60"), 1).
        outstandingDebit(new BigDecimal("930.30"), 2);
        // @formatter:on

        SysDateManager.setSysDate("25-Feb-2011");
        PaymentRecord payment = receiveAndPostPayment("25-Feb-2011", "300.00");

        // @formatter:off
        new TransactionHistoryTester(retrieveLease().billingAccount()).
        lineItemSize(4).
        notCoveredDebitLineItemSize(3).
        notConsumedCreditInvoiceItemSize(0).
        outstandingDebit(new BigDecimal("741.94"), 0).
        outstandingDebit(new BigDecimal("89.60"), 1).
        outstandingDebit(new BigDecimal("930.30"), 2);
        // @formatter:on

        //==================== RUN 2 ======================//

        SysDateManager.setSysDate("01-Mar-2011");
        activateLease();

        SysDateManager.setSysDate("18-Mar-2011");
        runBilling(true, false);

        printTransactionHistory(ARTransactionManager.getTransactionHistory(retrieveLease().billingAccount()));

        SysDateManager.setSysDate("25-Mar-2011");
        receiveAndPostPayment("25-Mar-2011", "301.00");

        printTransactionHistory(ARTransactionManager.getTransactionHistory(retrieveLease().billingAccount()));

        //==================== RUN 3 ======================//

        SysDateManager.setSysDate("18-Apr-2011");

        runBilling(true, false);

        printTransactionHistory(ARTransactionManager.getTransactionHistory(retrieveLease().billingAccount()));

        SysDateManager.setSysDate("25-Apr-2011");
        receiveAndPostPayment("25-Apr-2011", "302.00");

        printTransactionHistory(ARTransactionManager.getTransactionHistory(retrieveLease().billingAccount()));

        //==================== RUN 4 ======================//

        SysDateManager.setSysDate("18-May-2011");

        runBilling(true, false);

        printTransactionHistory(ARTransactionManager.getTransactionHistory(retrieveLease().billingAccount()));

        SysDateManager.setSysDate("25-May-2011");
        receiveAndPostPayment("25-May-2011", "100.00");

        printTransactionHistory(ARTransactionManager.getTransactionHistory(retrieveLease().billingAccount()));

        //==================== RUN 5 ======================//

        SysDateManager.setSysDate("27-May-2011");

        rejectPayment(payment, false);

        printTransactionHistory(ARTransactionManager.getTransactionHistory(retrieveLease().billingAccount()));
    }

}
