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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.test.mock.MockConfig;

/**
 * Validates that existing BillingCycles are updated properly when the date offsets
 * have been modified in the LeaseBillingPolicy
 */
public class BillingCyclePolicyChangeTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockConfig config = new MockConfig();
        config.defaultBillingCycleSartDay = 1;
        preloadData(config);
    }

    public void testUpdatedBillingCycleDates() {
        setSysDate("01-Apr-2013");
        BillingCycle billingCycle = createBillingCycle();
        // @formatter:off
        new BillingCycleTester(billingCycle)
        .billingCycleStartDate("01-Apr-2013")
        .billingCycleEndDate("30-Apr-2013")
        .billExecutionDate("17-Mar-2013")
        .padGenerationDate("29-Mar-2013")
        .padExecutionDate("01-Apr-2013");
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
        .padGenerationDate("30-Mar-2013") // updated
        .padExecutionDate("31-Mar-2013"); // updated
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
        .padGenerationDate("29-Mar-2013") // updated
        .padExecutionDate("01-Apr-2013"); // updated
        // @formatter:on

        // CASE 3: Update policy AFTER BILL EXEC DATE - new padGen day is in the future
        //=============================================================================
        setSysDate("20-Mar-2013");
        updatePolicy(-14, -4, -1);

        // refresh billing cycle
        Persistence.service().retrieve(billingCycle);
        // validate that all dates have been updated
        // @formatter:off
        new BillingCycleTester(billingCycle)
        .billExecutionDate("18-Mar-2013") // no change
        .padGenerationDate("28-Mar-2013") // updated
        .padExecutionDate("31-Mar-2013"); // updated
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
        .padGenerationDate("28-Mar-2013") // no change
        .padExecutionDate("01-Apr-2013"); // updated
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
        .padGenerationDate("28-Mar-2013") // no change
        .padExecutionDate("01-Apr-2013"); // no change
        // @formatter:on
    }

    private BillingCycle createBillingCycle() {
        return BillingCycleTester.ensureBillingCycle(getBuilding(), BillingPeriod.Monthly, "05-Apr-2013");
    }

    // default offsets: -15, -3, 0
    private void updatePolicy(int billExecOffset, int padCalcOffset, int padExecOffset) {
        LeaseBillingPolicy newPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(getBuilding(), LeaseBillingPolicy.class);
        LeaseBillingPolicy oldPolicy = newPolicy.duplicate();
        for (LeaseBillingTypePolicyItem item : newPolicy.availableBillingTypes()) {
            if (item.billingPeriod().getValue().equals(BillingPeriod.Monthly)) {
                item.billExecutionDayOffset().setValue(billExecOffset);
                item.padCalculationDayOffset().setValue(padCalcOffset);
                item.padExecutionDayOffset().setValue(padExecOffset);
                break;
            }
        }
        Persistence.service().persist(newPolicy);
        ServerSideFactory.create(BillingCycleFacade.class).onLeaseBillingPolicyChange(oldPolicy, newPolicy);
    }
}
