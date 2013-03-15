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
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.ar.TransactionHistoryTester;
import com.propertyvista.domain.financial.InternalBillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.DebitCreditLink;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;

@Category(FunctionalTests.class)
public class ARCreditDebitLinkManagerTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenario() throws Exception {

        createLease("01-Mar-2011", "31-Aug-2011");
        addParking();

        //==================== RUN 1 ======================//

        setSysDate("22-Feb-2011");
        Bill bill = approveApplication(true);

        InvoiceDebit invoiceDebit = null;
        InvoiceDebit invoiceDebitParking = null;
        InvoiceDebit invoiceDebitLease = null;
        for (InvoiceLineItem item : bill.lineItems()) {
            if (item.isInstanceOf(InvoiceDebit.class)) {
                invoiceDebit = item.cast();
                if (invoiceDebit.debitType().getValue().compareTo(InvoiceDebit.DebitType.parking) == 0) {
                    invoiceDebitParking = invoiceDebit;
                } else if (invoiceDebit.debitType().getValue().compareTo(InvoiceDebit.DebitType.lease) == 0) {
                    invoiceDebitLease = invoiceDebit;
                }
            }
        }

        // @formatter:off
        new TransactionHistoryTester(retrieveLease().billingAccount()).
        lineItemSize(4).
        notCoveredDebitLineItemSize(4).
        notConsumedCreditInvoiceItemSize(0).
        outstandingDebit(new BigDecimal("1041.94"), 0).
        outstandingDebit(new BigDecimal("89.60"), 1).
        outstandingDebit(new BigDecimal("930.30"), 2);
        // @formatter:on

        setSysDate("25-Feb-2011");
        PaymentRecord payment = receiveAndPostPayment("25-Feb-2011", "300.00");

        // @formatter:off
        new TransactionHistoryTester(retrieveLease().billingAccount()).
        lineItemSize(5).
        notCoveredDebitLineItemSize(4).
        notConsumedCreditInvoiceItemSize(0).
        outstandingDebit(new BigDecimal("741.94"), 0).
        outstandingDebit(new BigDecimal("89.60"), 1).
        outstandingDebit(new BigDecimal("930.30"), 2);
        // @formatter:on

        //==================== RUN 2 ======================//

        setSysDate("01-Mar-2011");
        activateLease();

        setSysDate("18-Mar-2011");
        runBilling(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class)
                .getTransactionHistory(retrieveLease().billingAccount().<InternalBillingAccount> cast()));

        setSysDate("25-Mar-2011");

        PaymentRecord payment2 = receiveAndPostPayment("25-Mar-2011", "301.00");
        Persistence.service().retrieve(invoiceDebitParking);
        DebitCreditLink link = createHardDebitCreditLink(payment2, invoiceDebitParking, "89.60");
        Persistence.service().retrieve(invoiceDebitLease);
        createHardDebitCreditLink(payment, invoiceDebitLease, "149.00");

        printTransactionHistory(ServerSideFactory.create(ARFacade.class)
                .getTransactionHistory(retrieveLease().billingAccount().<InternalBillingAccount> cast()));

        setSysDate("31-Mar-2011");

        removeHardLink(link);
        Persistence.service().retrieve(invoiceDebitLease);
        createHardDebitCreditLink(payment, invoiceDebitLease, "151.00");

        printTransactionHistory(ServerSideFactory.create(ARFacade.class)
                .getTransactionHistory(retrieveLease().billingAccount().<InternalBillingAccount> cast()));

        //==================== RUN 3 ======================//

        setSysDate("18-Apr-2011");

        runBilling(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class)
                .getTransactionHistory(retrieveLease().billingAccount().<InternalBillingAccount> cast()));

        setSysDate("25-Apr-2011");
        receiveAndPostPayment("25-Apr-2011", "302.00");

        printTransactionHistory(ServerSideFactory.create(ARFacade.class)
                .getTransactionHistory(retrieveLease().billingAccount().<InternalBillingAccount> cast()));

        //==================== RUN 4 ======================//

        setSysDate("18-May-2011");

        runBilling(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class)
                .getTransactionHistory(retrieveLease().billingAccount().<InternalBillingAccount> cast()));

        setSysDate("25-May-2011");
        receiveAndPostPayment("25-May-2011", "100.00");

        printTransactionHistory(ServerSideFactory.create(ARFacade.class)
                .getTransactionHistory(retrieveLease().billingAccount().<InternalBillingAccount> cast()));

        //==================== RUN 5 ======================//

        setSysDate("27-May-2011");

        rejectPayment(payment, false);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class)
                .getTransactionHistory(retrieveLease().billingAccount().<InternalBillingAccount> cast()));
    }

}
