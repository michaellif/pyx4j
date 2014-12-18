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
package com.propertyvista.biz.financial.ar.internal;

import java.util.List;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.policy.policies.ARPolicy;
import com.propertyvista.domain.policy.policies.ARPolicy.CreditDebitRule;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.mock.models.ARPolicyDataModel;

@Category(FunctionalTests.class)
public class ARNotCoveredDebitInvoiceLineItemListTest extends LeaseFinancialTestBase {

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
        addOutdoorParking();

        //==================== RUN 1 ======================//

        setSysDate("22-Feb-2011");
        approveApplication(true);

        //==================== RUN 2 ======================//

        setSysDate("01-Mar-2011");
        activateLease();

        setSysDate("18-Mar-2011");
        runBilling(true);

        //==================== RUN 3 ======================//

        setSysDate("18-Apr-2011");
        runBilling(true);

        //==================== RUN 4 ======================//

        setSysDate("18-May-2011");
        runBilling(true);

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        //
        ARPolicy policy = getDataModel(ARPolicyDataModel.class).getItem(0);
        policy.creditDebitRule().setValue(CreditDebitRule.rentDebtLast);
        Persistence.service().persist(policy);
        Persistence.service().commit();

        List<InvoiceDebit> debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(retrieveLease().billingAccount());

        policy = getDataModel(ARPolicyDataModel.class).getItem(0);
        policy.creditDebitRule().setValue(CreditDebitRule.oldestDebtFirst);
        Persistence.service().persist(policy);
        Persistence.service().commit();

        debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(retrieveLease().billingAccount());

    }

}
