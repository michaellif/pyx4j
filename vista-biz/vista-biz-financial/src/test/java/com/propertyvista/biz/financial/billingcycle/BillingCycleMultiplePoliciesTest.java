/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
package com.propertyvista.biz.financial.billingcycle;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.experimental.categories.Category;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.billing.BillTester;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.BaseFeeType;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.shared.BillingException;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;
import com.propertyvista.test.mock.models.CustomerDataModel;
import com.propertyvista.test.mock.models.LeaseDataModel;

@Category(FunctionalTests.class)
public class BillingCycleMultiplePoliciesTest extends LeaseFinancialTestBase {

    /** Ensure correct cycle is picked out of two created by different LeaseBillingPolicies */
    public void testBillingWithMultipleBillingCycles() throws Exception {
        preloadData();
        setLeaseBatchProcess();

        setSysDate("01-Jan-2012");

        // 1. Create First Lease with default billing type: Monthly-1
        // ----------------------------------------------------------
        createLease("23-Mar-2012", "03-Aug-2012");

        Bill bill = approveApplication(true);

        new BillingCycleTester(bill.billingCycle()). //
                notConfirmedBills(0L). //
                failedBills(0L). //
                rejectedBills(0L). //
                confirmedBills(1L);

        new BillTester(bill). //
                billSequenceNumber(1). //
                billingTypePeriodStartDay(1). //
                billingCyclePeriodStartDate("01-Mar-2012"). //
                billingCyclePeriodEndDate("31-Mar-2012"). //
                billingCycleExecutionTargetDate("15-Feb-2012"). //
                billType(Bill.BillType.First). //
                billingPeriodStartDate("23-Mar-2012"). //
                billingPeriodEndDate("31-Mar-2012");

        String billingTypeId = bill.billingCycle().billingType().id().toString();

        advanceSysDate("17-Mar-2012");

        bill = runBilling(true);

        assertEquals("Expected Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        // 2. create new policy with another billing cycle: Monthly-10
        //    create new lease with the new policy
        // -----------------------------------------------------------
        createNewPolicy(getBuilding(), 10);
        BillingCycle cycle2 = BillingCycleTester.ensureBillingCycleForDate(getBuilding(), BillingPeriod.Monthly, "10-Mar-2012");

        assertNotNull("Billing Cycle is null", cycle2);

        Lease lease2 = createNewLease("23-Mar-2012", "03-Aug-2012");

        // 3. advance date enough to run billing for new policy
        //    ensure billing won't run for old lease
        // -----------------------------------------------------
        advanceSysDate("27-Mar-2012");
        try {
            bill = runBilling(true);
            assertTrue("Expected: BillingException(Regular billing can't run before target execution date)", false);
        } catch (Exception e) {
            assertTrue("Expected: BillingException(Regular billing can't run before target execution date)", e instanceof BillingException);
        }

        // 4. run billing for the second lease
        // -----------------------------------
        bill = approveApplication(lease2);

        assertNotSame("Expected Different Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        new BillingCycleTester(bill.billingCycle()). //
                notConfirmedBills(0L). //
                failedBills(0L). //
                rejectedBills(0L). //
                confirmedBills(1L). //
                billExecutionDate("24-Feb-2012"). //
                billingCycleStartDate("10-Mar-2012"). //
                billingCycleEndDate("9-Apr-2012");

        // 5. double check the fist bill still runs in time
        // ------------------------------------------------
        advanceSysDate("17-Apr-2012");

        bill = runBilling(true);

        assertEquals("Expected Same Billing Cycle", billingTypeId, bill.billingCycle().billingType().id().toString());

        new BillingCycleTester(bill.billingCycle()). //
                notConfirmedBills(0L). //
                failedBills(0L). //
                rejectedBills(0L). //
                confirmedBills(1L). //
                billExecutionDate("16-Apr-2012"). //
                billingCycleStartDate("1-May-2012"). //
                billingCycleEndDate("31-May-2012");
    }

    private LeaseBillingPolicy createNewPolicy(PolicyNode node, int billingCycleStartDay) {
        LeaseBillingPolicy policy = EntityFactory.create(LeaseBillingPolicy.class);
        policy.node().set(node);

        LeaseBillingTypePolicyItem billingType = EntityFactory.create(LeaseBillingTypePolicyItem.class);
        billingType.billingPeriod().setValue(BillingPeriod.Monthly);
        billingType.billingCycleStartDay().setValue(billingCycleStartDay);
        billingType.paymentDueDayOffset().setValue(0);
        billingType.finalDueDayOffset().setValue(15);
        billingType.billExecutionDayOffset().setValue(-15);
        billingType.autopayExecutionDayOffset().setValue(0);
        policy.availableBillingTypes().add(billingType);

        policy.prorationMethod().setValue(BillingAccount.ProrationMethod.Actual);

        policy.lateFee().baseFee().amount().setValue(new BigDecimal(50.00));
        policy.lateFee().baseFeeType().setValue(BaseFeeType.FlatAmount);
        policy.lateFee().maxTotalFee().amount().setValue(new BigDecimal(1000.00));
        policy.lateFee().maxTotalFeeType().setValue(LateFeeItem.MaxTotalFeeType.FlatAmount);

        Persistence.service().persist(policy);
        // PolicyManager uses cache, so we need to reset it...
        CacheService.reset();
        return policy;
    }

    private Lease createNewLease(String leaseFrom, String leaseTo) {
        return getDataModel(LeaseDataModel.class).addLease(getBuilding(), leaseFrom, leaseTo, null, null,
                Arrays.asList(new Customer[] { getDataModel(CustomerDataModel.class).addCustomer() }));

    }

    private Bill approveApplication(Lease lease) {
        ServerSideFactory.create(LeaseFacade.class).approve(lease, null, null);
        Persistence.service().commit();
        Bill bill = ServerSideFactory.create(BillingFacade.class).getLatestBill(lease);
        return bill;
    }
}
