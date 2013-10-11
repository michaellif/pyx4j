/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 10, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.test.mock.models.LeaseBillingPolicyDataModel;

public class PreauthorizedPaymentCyclesTest extends LeaseFinancialTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testPreauthorizedPaymentCyclesRegular() throws Exception {
        setSysDate("2011-01-01");
        createLease("2011-01-01", "2012-03-10");

        Lease lease = getLease();
        PaymentMethodFacade f = ServerSideFactory.create(PaymentMethodFacade.class);

        {
            setSysDate("2011-01-01");
            BillingCycle curentCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(lease,
                    new LogicalDate(SystemDateManager.getDate()));

            assertEquals("billingCycleStartDate", "2011-01-01", curentCycle.billingCycleStartDate().getValue());

            assertEquals("CurrentPreauthorizedPaymentDate", "2011-02-01", f.getCurrentPreauthorizedPaymentDate(lease));
            assertEquals("NextPreauthorizedPaymentDate", "2011-02-01", f.getNextPreauthorizedPaymentDate(lease));
            assertEquals("PreauthorizedPaymentCutOffDate", "2011-01-29", f.getPreauthorizedPaymentCutOffDate(lease));
        }

        {
            setSysDate("2011-01-29");
            BillingCycle curentCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(lease,
                    new LogicalDate(SystemDateManager.getDate()));

            assertEquals("billingCycleStartDate", "2011-01-01", curentCycle.billingCycleStartDate().getValue());

            assertEquals("CurrentPreauthorizedPaymentDate", "2011-02-01", f.getCurrentPreauthorizedPaymentDate(lease));
            assertEquals("NextPreauthorizedPaymentDate", "2011-03-01", f.getNextPreauthorizedPaymentDate(lease)); // <-- changes
            assertEquals("PreauthorizedPaymentCutOffDate", "2011-01-29", f.getPreauthorizedPaymentCutOffDate(lease));
        }

        {
            setSysDate("2011-01-31");
            BillingCycle curentCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(lease,
                    new LogicalDate(SystemDateManager.getDate()));

            assertEquals("billingCycleStartDate", "2011-01-01", curentCycle.billingCycleStartDate().getValue());

            assertEquals("CurrentPreauthorizedPaymentDate", "2011-02-01", f.getCurrentPreauthorizedPaymentDate(lease));
            assertEquals("NextPreauthorizedPaymentDate", "2011-03-01", f.getNextPreauthorizedPaymentDate(lease));
            assertEquals("PreauthorizedPaymentCutOffDate", "2011-01-29", f.getPreauthorizedPaymentCutOffDate(lease));
        }
    }

    public void testPreauthorizedPaymentCyclesPostponed() throws Exception {
        // Setup postponed policy
        {
            LeaseBillingPolicy policy = getDataModel(LeaseBillingPolicyDataModel.class).getItem(0);
            LeaseBillingTypePolicyItem monthlyBillingTypeItem = policy.availableBillingTypes().get(0);
            assert monthlyBillingTypeItem.billingPeriod().getValue() == BillingPeriod.Monthly;

            monthlyBillingTypeItem.padCalculationDayOffset().setValue(2);
            monthlyBillingTypeItem.padExecutionDayOffset().setValue(4);

            Persistence.service().persist(policy);
            ServerSideFactory.create(BillingCycleFacade.class).onLeaseBillingPolicyChange(policy);
        }

        setSysDate("2011-01-01");
        createLease("2011-01-01", "2012-03-10");

        Lease lease = getLease();
        PaymentMethodFacade f = ServerSideFactory.create(PaymentMethodFacade.class);

        {
            setSysDate("2011-01-10");
            BillingCycle curentCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(lease,
                    new LogicalDate(SystemDateManager.getDate()));

            assertEquals("billingCycleStartDate", "2011-01-01", curentCycle.billingCycleStartDate().getValue());

            assertEquals("CurrentPreauthorizedPaymentDate", "2011-02-05", f.getCurrentPreauthorizedPaymentDate(lease));
            assertEquals("NextPreauthorizedPaymentDate", "2011-02-05", f.getNextPreauthorizedPaymentDate(lease));
            assertEquals("PreauthorizedPaymentCutOffDate", "2011-02-03", f.getPreauthorizedPaymentCutOffDate(lease));
        }

        //TODO
        if (false) {
            setSysDate("2011-02-01");
            BillingCycle curentCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(lease,
                    new LogicalDate(SystemDateManager.getDate()));

            assertEquals("billingCycleStartDate", "2011-02-01", curentCycle.billingCycleStartDate().getValue());

            assertEquals("CurrentPreauthorizedPaymentDate", "2011-02-05", f.getCurrentPreauthorizedPaymentDate(lease));
            assertEquals("NextPreauthorizedPaymentDate", "2011-02-05", f.getNextPreauthorizedPaymentDate(lease)); // <-- changes
            assertEquals("PreauthorizedPaymentCutOffDate", "2011-02-03", f.getPreauthorizedPaymentCutOffDate(lease));
        }

        //TODO
        if (false) {
            setSysDate("2011-02-03");
            BillingCycle curentCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(lease,
                    new LogicalDate(SystemDateManager.getDate()));

            assertEquals("billingCycleStartDate", "2011-01-01", curentCycle.billingCycleStartDate().getValue());

            assertEquals("CurrentPreauthorizedPaymentDate", "2011-02-05", f.getCurrentPreauthorizedPaymentDate(lease));
            assertEquals("NextPreauthorizedPaymentDate", "2011-03-05", f.getNextPreauthorizedPaymentDate(lease));
            assertEquals("PreauthorizedPaymentCutOffDate", "2011-02-03", f.getPreauthorizedPaymentCutOffDate(lease));
        }

    }
}
