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

import java.util.Calendar;
import java.util.GregorianCalendar;

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

    private static final long MILIS_IN_DAY = 1000 * 60 * 60 * 24;

    /**
     * Use 1-Jan-2012 as odd week Sunday ref to calculate odd/even week
     */
    private static final long REF_SUNDAY = new LogicalDate(112, 0, 1).getTime();

    static BillingCycle ensureBillingCycle(Lease lease) {
        BillingCycle billingCycle = lease.billingAccount().billingCycle();

        if (billingCycle.isNull()) {
            PaymentFrequency paymentFrequency = lease.paymentFrequency().getValue();
            Integer billingPeriodStartDay = null;
            if (lease.billingAccount().billingPeriodStartDate().isNull()) {
                billingPeriodStartDay = calculateBillingCycleStartDay(lease.paymentFrequency().getValue(), lease.leaseFrom().getValue());
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

    /**
     * For {@see BillingPeriodCorrelationMethod.LeaseStart}
     * 
     * When billing period required to start on lease start date we have one special case:
     * - for 'monthly' or 'semimonthly' PaymentFrequency and if lease date starts on 29, 30, or 31 we correspond this lease to cycle
     * with billingPeriodStartDay = 1 and prorate days of 29/30/31.
     */
    static int calculateBillingCycleStartDay(PaymentFrequency frequency, LogicalDate leaseStartDate) {
        int billingPeriodStartDay = 0;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(leaseStartDate);
        switch (frequency) {
        case Monthly:
            billingPeriodStartDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (billingPeriodStartDay > 28) {
                billingPeriodStartDay = 1;
            }
            break;
        case Weekly:
            billingPeriodStartDay = calendar.get(Calendar.DAY_OF_WEEK);
            break;
        case BiWeekly:
            billingPeriodStartDay = calendar.get(Calendar.DAY_OF_WEEK);
            if ((leaseStartDate.getTime() - REF_SUNDAY) / MILIS_IN_DAY % 14 >= 7) {
                billingPeriodStartDay += 7;
            }
            break;
        case SemiMonthly:
            billingPeriodStartDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (billingPeriodStartDay > 28) {
                billingPeriodStartDay = 1;
            } else if (billingPeriodStartDay > 14) {
                billingPeriodStartDay = billingPeriodStartDay - 14;
            }
            break;
        case SemiAnnyally:
        case Annually:
            throw new Error("Not implemented");
        }
        return billingPeriodStartDay;
    }

    private static LogicalDate calculateFirstBillingRunStartDate(BillingCycle cycle, LogicalDate leaseStartDate, boolean useCyclePeriodStartDay) {
        LogicalDate billingRunStartDate = null;
        if (useCyclePeriodStartDay) {
            switch (cycle.paymentFrequency().getValue()) {
            case Monthly:
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(leaseStartDate);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                if (cycle.billingPeriodStartDay().getValue() < 1 || cycle.billingPeriodStartDay().getValue() > 28) {
                    throw new BillingException("Wrong billing period start day");
                }
                while (dayOfMonth != cycle.billingPeriodStartDay().getValue()) {
                    calendar.add(Calendar.DATE, -1);
                    dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                }
                billingRunStartDate = new LogicalDate(calendar.getTime());
                break;
            case Weekly:
            case BiWeekly:
            case SemiMonthly:
            case SemiAnnyally:
            case Annually:
                //TODO
                throw new Error("Not implemented");
            }
        } else {
            if (PaymentFrequency.Monthly.equals(cycle.paymentFrequency().getValue())) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(leaseStartDate);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                if (dayOfMonth > 28) {
                    calendar.add(Calendar.DATE, -dayOfMonth + 1);
                }
                billingRunStartDate = new LogicalDate(calendar.getTime());
            } else {
                billingRunStartDate = leaseStartDate;
            }
        }

        return billingRunStartDate;
    }

    private static LogicalDate calculateSubsiquentBillingRunStartDate(BillingCycle cycle, LogicalDate previousBillingRunStartDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(previousBillingRunStartDate);
        switch (cycle.paymentFrequency().getValue()) {
        case Monthly:
            // TODO use proper bill day
            calendar.add(Calendar.MONTH, 1);
            break;
        //TODO
        case Weekly:
        case BiWeekly:
        case SemiMonthly:
        case Annually:
            throw new Error("Not implemented");
        }
        return new LogicalDate(calendar.getTime());
    }

    static BillingRun createNewLeaseFirstBillingRun(BillingCycle cycle, LogicalDate leaseStartDate, boolean useCyclePeriodStartDay) {
        return createBillingRun(cycle, calculateFirstBillingRunStartDate(cycle, leaseStartDate, useCyclePeriodStartDay));
    }

    static BillingRun createExistingLeaseInitialBillingRun(BillingCycle cycle, LogicalDate leaseStartDate, LogicalDate leaseActivationDate,
            boolean useCyclePeriodStartDay) {
        if (!leaseStartDate.before(leaseActivationDate)) {
            throw new BillingException("Existing lease should have start date earlier than activation date");
        }
        LogicalDate firstBillingRunStartDate = calculateFirstBillingRunStartDate(cycle, leaseStartDate, useCyclePeriodStartDay);
        LogicalDate billingRunStartDate = null;
        LogicalDate nextBillingRunStartDate = firstBillingRunStartDate;
        do {
            billingRunStartDate = nextBillingRunStartDate;
            nextBillingRunStartDate = calculateSubsiquentBillingRunStartDate(cycle, billingRunStartDate);
        } while (nextBillingRunStartDate.compareTo(leaseActivationDate) <= 0);

        return createBillingRun(cycle, billingRunStartDate);
    }

    static BillingRun createSubsiquentBillingRun(BillingCycle cycle, BillingRun previousRun) {
        return createBillingRun(cycle, calculateSubsiquentBillingRunStartDate(cycle, previousRun.billingPeriodStartDate().getValue()));
    }

    private static BillingRun createBillingRun(BillingCycle cycle, LogicalDate billingRunStartDate) {
        BillingRun billingRun = EntityFactory.create(BillingRun.class);
        billingRun.status().setValue(BillingRun.BillingRunStatus.Scheduled);
        billingRun.billingCycle().set(cycle);
        billingRun.billingPeriodStartDate().setValue(billingRunStartDate);
        billingRun.billingPeriodEndDate().setValue(calculateBillingRunEndDate(cycle.paymentFrequency().getValue(), billingRunStartDate));
        billingRun.executionTargetDate().setValue(calculateBillingRunTargetExecutionDate(cycle, billingRunStartDate));
        return billingRun;

    }

    private static LogicalDate calculateBillingRunEndDate(PaymentFrequency frequency, LogicalDate billingRunStartDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(billingRunStartDate);
        switch (frequency) {
        case Monthly:
            calendar.add(Calendar.MONTH, 1);
            break;
        case Weekly:
        case BiWeekly:
        case SemiMonthly:
        case Annually:
            throw new Error("Not implemented");
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return new LogicalDate(calendar.getTime());
    }

    private static LogicalDate calculateBillingRunTargetExecutionDate(BillingCycle cycle, LogicalDate billingRunStartDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(billingRunStartDate);
        calendar.add(Calendar.DATE, -cycle.paymentFrequency().getValue().getBillRunTargetDayOffset());
        return new LogicalDate(calendar.getTime());
    }
}
