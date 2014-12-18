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
package com.propertyvista.biz.financial.billing.internal;

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class BillExecutionWithManualApprovalTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testNewLease() throws Exception {

        setLeaseBatchProcess();
        setBillingBatchProcess();

        setSysDate("15-Feb-2011");

        createLease("01-Mar-2011", "31-May-2011");

        //==================== CYCLE 1 ======================//

        advanceSysDate("18-Feb-2011");
        approveApplication(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(1).
        billType(Bill.BillType.First).
        billingPeriodStartDate("01-Mar-2011").
        billingPeriodEndDate("31-Mar-2011").
        numOfProductCharges(1);
        // @formatter:on

        //==================== CYCLE 2 ======================//

        advanceSysDate("18-Mar-2011");
        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Apr-2011").
        billingPeriodEndDate("30-Apr-2011").
        numOfProductCharges(1);
        // @formatter:on

        //==================== CYCLE 3 ======================//

        advanceSysDate("18-Apr-2011");
        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(1);
        // @formatter:on

        //==================== CYCLE 4 ======================//

        advanceSysDate("18-May-2011");

        //Billing does't run in the last cycle of lease term
        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(1);
        // @formatter:on

        //==================== FINAL ======================//

        advanceSysDate("05-Jun-2011");
        runBilling(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(4).
        billType(Bill.BillType.Final);
        // @formatter:on

        closeLease();
    }

    public void testExistingLeaseApprovedAfterBillRun() throws Exception {
        setLeaseBatchProcess();
        setBillingBatchProcess();

        setSysDate("15-May-2011");

        createLease("01-Mar-2009", "31-Aug-2011", null, new BigDecimal("300.00"));

        advanceSysDate("22-May-2011");
        approveExistingLease(true);

        //==================== ZERO CYCLE ======================//

        // @formatter:off
        new BillTester(getBill(1)).
        billSequenceNumber(1).
        billType(Bill.BillType.ZeroCycle).
        billingPeriodStartDate("01-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(1);
        // @formatter:on

        //==================== CYCLE 1 ======================//

        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestConfirmedBill()).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Jun-2011").
        billingPeriodEndDate("30-Jun-2011").
        numOfProductCharges(1);
        // @formatter:on

        //==================== CYCLE 2 ======================//

        advanceSysDate("18-Jun-2011");
        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestConfirmedBill()).
        billSequenceNumber(3).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Jul-2011").
        billingPeriodEndDate("31-Jul-2011").
        numOfProductCharges(1);
        // @formatter:on

        //==================== CYCLE 3 ======================//

        advanceSysDate("18-Jul-2011");
        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestBill()).
        billSequenceNumber(4).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Aug-2011").
        billingPeriodEndDate("31-Aug-2011").
        numOfProductCharges(1);
        // @formatter:on

        //==================== FINAL ======================//

        advanceSysDate("05-Sep-2011");

        // @formatter:off
        new BillTester(runBilling()).
        billSequenceNumber(5).
        billType(Bill.BillType.Final);
        // @formatter:on

        closeLease();
    }

    public void testExistingLeaseApprovedBeforeBillRun() throws Exception {
        setLeaseBatchProcess();
        setBillingBatchProcess();

        setSysDate("02-May-2011");

        createLease("01-Mar-2009", "30-Jun-2011", null, new BigDecimal("300.00"));

        advanceSysDate("04-May-2011");
        approveExistingLease(true);

        //==================== ZERO CYCLE ======================//

        // @formatter:off
        new BillTester(getBill(1)).
        billSequenceNumber(1).
        billType(Bill.BillType.ZeroCycle).
        billingPeriodStartDate("01-May-2011").
        billingPeriodEndDate("31-May-2011").
        numOfProductCharges(1);
        // @formatter:on

        //==================== CYCLE 1 ======================//

        advanceSysDate("18-May-2011");
        confirmBill(true);

        // @formatter:off
        new BillTester(getLatestConfirmedBill()).
        billSequenceNumber(2).
        billType(Bill.BillType.Regular).
        billingPeriodStartDate("01-Jun-2011").
        billingPeriodEndDate("30-Jun-2011").
        numOfProductCharges(1);
        // @formatter:on

        //==================== FINAL ======================//

        advanceSysDate("05-Jul-2011");

        // @formatter:off
        new BillTester(runBilling()).
        billSequenceNumber(3).
        billType(Bill.BillType.Final);
        // @formatter:on

        closeLease();
    }
}
