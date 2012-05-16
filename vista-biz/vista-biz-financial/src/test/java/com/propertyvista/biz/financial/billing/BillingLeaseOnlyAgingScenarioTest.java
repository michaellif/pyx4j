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

public class BillingLeaseOnlyAgingScenarioTest extends FinancialTestBase {

    private long startTime;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void tearDown() throws Exception {
        System.out.println("Execution Time - " + (System.currentTimeMillis() - startTime) + "ms");
        super.tearDown();
    }

    public void testScenario() {

        setLeaseConditions("1-Mar-2011", "31-Aug-2011", 1);

        //==================== RUN 1 ======================//

        SysDateManager.setSysDate("17-Feb-2011");
        setLeaseStatus(Lease.Status.Approved);

        Bill bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).totalDueAmount("1972.24");
        // @formatter:on

        SysDateManager.setSysDate("28-Feb-2011");
        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        SysDateManager.setSysDate("01-Mar-2011");
        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        //==================== RUN 2 ======================//

        SysDateManager.setSysDate("18-Mar-2011");
        setLeaseStatus(Lease.Status.Active);

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).totalDueAmount("3014.18");
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

        //==================== RUN 3 ======================//

        SysDateManager.setSysDate("18-Apr-2011");

        bill = runBilling(true, true);

        // @formatter:off
        new BillTester(bill).totalDueAmount("4056.12");
        // @formatter:on

        printTransactionHistory(ServerSideFactory.create(ARFacade.class).getTransactionHistory(retrieveLease().billingAccount()));

    }

}
