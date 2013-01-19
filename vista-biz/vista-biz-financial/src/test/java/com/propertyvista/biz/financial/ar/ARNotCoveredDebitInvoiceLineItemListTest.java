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

import java.util.List;

import org.junit.experimental.categories.Category;

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.InternalBillingAccount;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.policy.policies.ARPolicy;
import com.propertyvista.domain.policy.policies.ARPolicy.CreditDebitRule;

@Category(FunctionalTests.class)
public class ARNotCoveredDebitInvoiceLineItemListTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testScenario() {

        createLease("01-Mar-2011", "31-Aug-2011");
        addParking();

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("22-Feb-2011");
        approveApplication(true);

        //==================== RUN 2 ======================//

        SysDateManager.setSysDate("01-Mar-2011");
        activateLease();

        SysDateManager.setSysDate("18-Mar-2011");
        runBilling(true);

        //==================== RUN 3 ======================//

        SysDateManager.setSysDate("18-Apr-2011");
        runBilling(true);

        //==================== RUN 4 ======================//

        SysDateManager.setSysDate("18-May-2011");
        runBilling(true);

        printTransactionHistory(ARTransactionManager.getTransactionHistory(retrieveLease().billingAccount().<InternalBillingAccount> cast()));

        //
        ARPolicy policy = arPolicyDataModel.getPolicy();
        policy.creditDebitRule().setValue(CreditDebitRule.byDueDate);
        Persistence.service().persist(policy);
        Persistence.service().commit();

        List<InvoiceDebit> debits = ARTransactionManager.getNotCoveredDebitInvoiceLineItems(retrieveLease().billingAccount().<InternalBillingAccount> cast());

        policy = arPolicyDataModel.getPolicy();
        policy.creditDebitRule().setValue(CreditDebitRule.byDebitType);
        Persistence.service().persist(policy);
        Persistence.service().commit();

        debits = ARTransactionManager.getNotCoveredDebitInvoiceLineItems(retrieveLease().billingAccount().<InternalBillingAccount> cast());

        policy = arPolicyDataModel.getPolicy();
        policy.creditDebitRule().setValue(CreditDebitRule.byAgingBucketAndDebitType);
        Persistence.service().persist(policy);
        Persistence.service().commit();

        debits = ARTransactionManager.getNotCoveredDebitInvoiceLineItems(retrieveLease().billingAccount().<InternalBillingAccount> cast());

    }

}
