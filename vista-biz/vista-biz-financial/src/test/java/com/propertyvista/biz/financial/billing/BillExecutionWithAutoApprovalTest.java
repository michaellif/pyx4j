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

import org.junit.experimental.categories.Category;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.test.preloader.PreloadConfig;

@Category(FunctionalTests.class)
public class BillExecutionWithAutoApprovalTest extends FinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreloadConfig config = new PreloadConfig();
        config.billConfirmationMethod = LeaseBillingPolicy.BillConfirmationMethod.automatic;
        preloadData(config);
    }

    public void testNewLease() {

//        setLeaseBatchProcess();
//        setBillingBatchProcess();
//
//        setDate("15-Feb-2011");
//
//        createLease("1-Mar-2011", "31-May-2011");
//
//        //==================== CYCLE 1 ======================//
//
//        advanceDate("17-Feb-2011");
//
//        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(retrieveLease());
//
//        // @formatter:off
//        new BillTester(bill).
//        billSequenceNumber(1).
//        previousBillSequenceNumber(null).
//        billType(Bill.BillType.First).
//        billingPeriodStartDate("1-Mar-2011").
//        billingPeriodEndDate("31-Mar-2011").
//        numOfProductCharges(1);
//        // @formatter:on
//
//        //==================== CYCLE 2 ======================//
//
//        advanceDate("18-Mar-2011");
//        bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(retrieveLease());
//
//        // @formatter:off
//        new BillTester(bill).
//        billSequenceNumber(2).
//        previousBillSequenceNumber(1).
//        billType(Bill.BillType.Regular).
//        billingPeriodStartDate("1-Apr-2011").
//        billingPeriodEndDate("30-Apr-2011").
//        numOfProductCharges(1);
//        // @formatter:on
//
//        //==================== CYCLE 3 ======================//
//
//        advanceDate("18-Apr-2011");
//        bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(retrieveLease());
//
//        // @formatter:off
//        new BillTester(bill).
//        billSequenceNumber(3).
//        previousBillSequenceNumber(2).
//        billType(Bill.BillType.Regular).
//        billingPeriodStartDate("1-May-2011").
//        billingPeriodEndDate("31-May-2011").
//        numOfProductCharges(1);
//        // @formatter:on
//
//        //==================== CYCLE 4 ======================//
//
//        advanceDate("18-May-2011");
//        bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(retrieveLease());
//
//        //Billing does't run in the last cycle of lease term
//        // @formatter:off
//        new BillTester(bill).
//        billSequenceNumber(3).
//        previousBillSequenceNumber(2).
//        billType(Bill.BillType.Regular).
//        billingPeriodStartDate("1-May-2011").
//        billingPeriodEndDate("31-May-2011").
//        numOfProductCharges(1);
//        // @formatter:on
//
//        //==================== FINAL ======================//
//
//        advanceDate("05-Jun-2011");
//        bill = runBilling(true, true);
//
//        // @formatter:off
//        new BillTester(bill).
//        billSequenceNumber(4).
//        previousBillSequenceNumber(3).
//        billType(Bill.BillType.Final);
//        // @formatter:on

    }

}
