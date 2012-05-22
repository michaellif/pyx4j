/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public class BillingCycleManger {

    static BillingCycle ensureBillingCycle(Lease lease) {
        BillingCycle billingCycle = lease.billingAccount().billingCycle();

        if (billingCycle.isNull()) {
            PaymentFrequency paymentFrequency = lease.paymentFrequency().getValue();
            Integer billingPeriodStartDay = null;
            if (lease.billingAccount().billingPeriodStartDate().isNull()) {
                billingPeriodStartDay = BillDateUtils.calculateBillingCycleStartDay(lease.paymentFrequency().getValue(), lease.leaseFrom().getValue());
            } else {
                billingPeriodStartDay = lease.billingAccount().billingPeriodStartDate().getValue();
            }

            //try to find existing billing cycle    
            EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().paymentFrequency(), paymentFrequency));
            criteria.add(PropertyCriterion.eq(criteria.proto().billingPeriodStartDay(), billingPeriodStartDay));
            billingCycle = Persistence.service().retrieve(criteria);

            if (billingCycle == null) {
                billingCycle = EntityFactory.create(BillingCycle.class);
                billingCycle.paymentFrequency().setValue(paymentFrequency);
                billingCycle.billingPeriodStartDay().setValue(billingPeriodStartDay);
                billingCycle.billingRunTargetDay().setValue(
                        (billingPeriodStartDay + lease.paymentFrequency().getValue().getNumOfCycles() - lease.paymentFrequency().getValue()
                                .getBillRunTargetDayOffset())
                                % lease.paymentFrequency().getValue().getNumOfCycles());

                Persistence.service().persist(billingCycle);
            }
        }
        return billingCycle;
    }

    static BillingRun createNewLeaseFirstBillingRun(BillingCycle cycle, LogicalDate leaseStartDate, boolean useCyclePeriodStartDay) {
        return createBillingRun(cycle, BillDateUtils.calculateFirstBillingRunStartDate(cycle, leaseStartDate, useCyclePeriodStartDay));
    }

    static BillingRun createExistingLeaseInitialBillingRun(BillingCycle cycle, LogicalDate leaseStartDate, LogicalDate leaseActivationDate,
            boolean useCyclePeriodStartDay) {
        if (!leaseStartDate.before(leaseActivationDate)) {
            throw new BillingException("Existing lease should have start date earlier than activation date");
        }
        LogicalDate firstBillingRunStartDate = BillDateUtils.calculateFirstBillingRunStartDate(cycle, leaseStartDate, useCyclePeriodStartDay);
        LogicalDate billingRunStartDate = null;
        LogicalDate nextBillingRunStartDate = firstBillingRunStartDate;
        do {
            billingRunStartDate = nextBillingRunStartDate;
            nextBillingRunStartDate = BillDateUtils.calculateSubsiquentBillingRunStartDate(cycle.paymentFrequency().getValue(), billingRunStartDate);
        } while (nextBillingRunStartDate.compareTo(leaseActivationDate) <= 0);

        return createBillingRun(cycle, billingRunStartDate);
    }

    static BillingRun createSubsiquentBillingRun(BillingCycle cycle, BillingRun previousRun) {
        return createBillingRun(cycle,
                BillDateUtils.calculateSubsiquentBillingRunStartDate(cycle.paymentFrequency().getValue(), previousRun.billingPeriodStartDate().getValue()));
    }

    private static BillingRun createBillingRun(BillingCycle cycle, LogicalDate billingRunStartDate) {
        BillingRun billingRun = EntityFactory.create(BillingRun.class);
        billingRun.status().setValue(BillingRun.BillingRunStatus.Scheduled);
        billingRun.billingCycle().set(cycle);
        billingRun.billingPeriodStartDate().setValue(billingRunStartDate);
        billingRun.billingPeriodEndDate().setValue(BillDateUtils.calculateBillingRunEndDate(cycle.paymentFrequency().getValue(), billingRunStartDate));
        billingRun.executionTargetDate().setValue(BillDateUtils.calculateBillingRunTargetExecutionDate(cycle, billingRunStartDate));
        return billingRun;

    }

}
