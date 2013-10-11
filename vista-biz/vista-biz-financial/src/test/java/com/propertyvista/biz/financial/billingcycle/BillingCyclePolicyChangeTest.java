/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 26, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.financial.billingcycle;

import java.util.Arrays;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.test.mock.MockConfig;

/**
 * Validates that existing BillingCycles are updated properly when the date offsets
 * have been modified in the LeaseBillingPolicy
 */
public class BillingCyclePolicyChangeTest extends LeaseFinancialTestBase {

    private MockConfig config;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        config = new MockConfig();
        config.defaultBillingCycleSartDay = 1;
        preloadData(config);
    }

    public void testExistingPolicyUpdate() {
        setSysDate("01-Apr-2013");
        BillingCycle billingCycle = createBillingCycle();
        // @formatter:off
        new BillingCycleTester(billingCycle)
        .billingCycleStartDate("01-Apr-2013")
        .billingCycleEndDate("30-Apr-2013")
        .billExecutionDate("17-Mar-2013")
        .autopayExecutionDate("01-Apr-2013");
        // @formatter:on

        // CASE 1: Update policy on BEFORE EXECUTION DATE; all new dates are in the future
        //================================================================================
        setSysDate("15-Mar-2013");
        updatePolicy(-14, -2, -1);

        // refresh billing cycle
        Persistence.service().retrieve(billingCycle);
        // validate that all dates have been updated
        // @formatter:off
        new BillingCycleTester(billingCycle)
        .billExecutionDate("18-Mar-2013") // updated
        .autopayExecutionDate("31-Mar-2013"); // updated
        // @formatter:on

        // CASE 2: Update policy BEFORE EXECUTION DATE - new exec day is today
        //====================================================================
        updatePolicy(-17, -3, 0);

        // refresh billing cycle
        Persistence.service().retrieve(billingCycle);
        // validate that all dates have been updated
        // @formatter:off
        new BillingCycleTester(billingCycle)
        .billExecutionDate("18-Mar-2013") // no change
        .autopayExecutionDate("01-Apr-2013"); // updated
        // @formatter:on

        // CASE 3: Update policy AFTER BILL EXEC DATE - new padGen day is in the future
        // Also, validate correct processing if the policy moved to Building-level
        //=============================================================================
        setSysDate("20-Mar-2013");
        updatePolicy(-14, -4, -1);

        // refresh billing cycle
        Persistence.service().retrieve(billingCycle);
        // validate that all dates have been updated
        // @formatter:off
        new BillingCycleTester(billingCycle)
        .billExecutionDate("18-Mar-2013") // no change
        .autopayExecutionDate("31-Mar-2013"); // updated
        // @formatter:on

        // CASE 4: Update policy AFTER PAD GEN DATE - new padExec day is in the future
        //=============================================================================
        setSysDate("30-Mar-2013");
        updatePolicy(-14, -3, 0);

        // refresh billing cycle
        Persistence.service().retrieve(billingCycle);
        // validate that all dates have been updated
        // @formatter:off
        new BillingCycleTester(billingCycle)
        .billExecutionDate("18-Mar-2013") // no change
        .autopayExecutionDate("01-Apr-2013"); // updated
        // @formatter:on

        // CASE 5: Update policy ON PAD EXEC DATE - new padExec day is in the future
        //=============================================================================
        setSysDate("01-Apr-2013");
        updatePolicy(-15, -4, 1);

        // refresh billing cycle
        Persistence.service().retrieve(billingCycle);
        // validate that all dates have been updated
        // @formatter:off
        new BillingCycleTester(billingCycle)
        .billExecutionDate("18-Mar-2013") // no change
        .autopayExecutionDate("01-Apr-2013"); // no change
        // @formatter:on
    }

    public void testNewPolicyUpdatedAndDeleted() {
        setSysDate("01-Apr-2013");
        BillingCycle billingCycle = createBillingCycle();

        // CASE 1: Add new Building-level policy and update BEFORE EXECUTION DATE; all new dates are in the future
        //================================================================================
        setSysDate("15-Mar-2013");
        LeaseBillingPolicy policy = createNewPolicy(getBuilding());
        updatePolicy(-14, -2, -1, policy);

        // refresh billing cycle
        Persistence.service().retrieve(billingCycle);
        // validate that all dates have been updated
        // @formatter:off
        new BillingCycleTester(billingCycle)
        .billingCycleStartDate("01-Apr-2013")
        .billingCycleEndDate("30-Apr-2013")
        .billExecutionDate("18-Mar-2013") // updated
        .autopayExecutionDate("31-Mar-2013"); // updated
        // @formatter:on

        // CASE 2: delete newly created policy and validate the dates are back to original values
        // ===============================================================================
        setSysDate("16-Mar-2013");
        deletePolicy(policy);

        // refresh billing cycle
        Persistence.service().retrieve(billingCycle);
        // @formatter:off
        new BillingCycleTester(billingCycle)
        .billingCycleStartDate("01-Apr-2013")
        .billingCycleEndDate("30-Apr-2013")
        .billExecutionDate("17-Mar-2013")
        .autopayExecutionDate("01-Apr-2013");
        // @formatter:on
    }

    private BillingCycle createBillingCycle() {
        return BillingCycleTester.ensureBillingCycle(getBuilding(), BillingPeriod.Monthly, "05-Apr-2013");
    }

    // default offsets: -15, -3, 0
    private void updatePolicy(int billExecOffset, int padCalcOffset, int padExecOffset) {
        LeaseBillingPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(getBuilding(), LeaseBillingPolicy.class);
        updatePolicy(billExecOffset, padCalcOffset, padExecOffset, policy);
    }

    private void updatePolicy(int billExecOffset, int padCalcOffset, int padExecOffset, LeaseBillingPolicy policy) {
        for (LeaseBillingTypePolicyItem item : policy.availableBillingTypes()) {
            if (item.billingPeriod().getValue().equals(BillingPeriod.Monthly)) {
                item.billExecutionDayOffset().setValue(billExecOffset);
                item.autopayExecutionDayOffset().setValue(padExecOffset);
                break;
            }
        }
        Persistence.service().persist(policy);
        ServerSideFactory.create(BillingCycleFacade.class).onLeaseBillingPolicyChange(policy);
    }

    private LeaseBillingPolicy createNewPolicy(PolicyNode node) {
        LeaseBillingPolicy policy = EntityFactory.create(LeaseBillingPolicy.class);
        policy.node().set(node);
        LeaseBillingTypePolicyItem billingType = EntityFactory.create(LeaseBillingTypePolicyItem.class);
        billingType.billingPeriod().setValue(BillingPeriod.Monthly);
        billingType.billingCycleStartDay().setValue(config.defaultBillingCycleSartDay);
        billingType.paymentDueDayOffset().setValue(0);
        billingType.finalDueDayOffset().setValue(15);
        billingType.billExecutionDayOffset().setValue(-15);
        billingType.autopayExecutionDayOffset().setValue(0);
        policy.availableBillingTypes().add(billingType);
        Persistence.service().persist(policy);
        return policy;
    }

    private void deletePolicy(LeaseBillingPolicy policy) {
        Persistence.service().delete(policy);
        ServerSideFactory.create(BillingCycleFacade.class).onLeaseBillingPolicyDelete(Arrays.asList(getBuilding()));
    }
}
