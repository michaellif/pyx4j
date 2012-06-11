/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 23, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public class ProrationUtils {

    private static final I18n i18n = I18n.get(ProrationUtils.class);

    //TODO implement retrieving ProrationMethod from policy
    public static BigDecimal prorate(LogicalDate from, LogicalDate to, BillingCycle billingCycle) {
        if (billingCycle.billingCycleStartDate().getValue().after(from) || billingCycle.billingCycleEndDate().getValue().before(to)) {
            throw new BillingException(i18n.tr("Proration days are out of cycle boundaries"));
        }
        if (PaymentFrequency.Monthly == billingCycle.billingType().paymentFrequency().getValue()) {
            LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(billingCycle.building(),
                    LeaseBillingPolicy.class);
            return prorateMonthlyPeriod(from, to, billingCycle.billingCycleStartDate().getValue(), leaseBillingPolicy.prorationMethod().getValue());
        } else if (PaymentFrequency.SemiMonthly == billingCycle.billingType().paymentFrequency().getValue()) {
            //TODO
            throw new Error("NOT IMPLEMENTED");
        } else {
            return prorateNormalPeriod(from, to, billingCycle.billingType().paymentFrequency().getValue().getNumOfCycles());
        }
    }

    static BigDecimal prorateMonthlyPeriod(LogicalDate from, LogicalDate to, LogicalDate cycleStartDate, BillingAccount.ProrationMethod method) {
        assert from != null && to != null && cycleStartDate != null;
        Calendar calendarFrom = new GregorianCalendar();
        calendarFrom.setTime(cycleStartDate);
        int daysInMonth = calendarFrom.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (comparePeriodToMonth(from, to) == -1) {
            BigDecimal proration = null;
            switch (method) {
            case Actual:
                proration = new BigDecimal(daysInMonth);
                break;
            case Standard:
                proration = new BigDecimal(30);
                if (daysInMonth < 30) {
                    proration = new BigDecimal(daysInMonth);
                }
                break;
            case Annual:
                proration = new BigDecimal(365).divide(new BigDecimal(12), 6, RoundingMode.HALF_UP);
                break;
            default:

            }
            return new BigDecimal(daysBetween(from, to)).divide(proration, 6, RoundingMode.HALF_UP);
        } else if (comparePeriodToMonth(from, to) == 0) {
            return new BigDecimal("1");
        } else {
            throw new BillingException("proration can't be calculated for a period more than one month, but period was defined as " + from + " - " + to);
        }

    }

    static BigDecimal prorateNormalPeriod(LogicalDate from, LogicalDate to, int cycleLength) {
        BigDecimal proration = new BigDecimal(cycleLength);
        return new BigDecimal(daysBetween(from, to)).divide(proration, 6, RoundingMode.HALF_UP);
    }

    /**
     * 
     * -1 if less than month
     * 0 if equals to month
     * 1 if more than month
     */
    public static int comparePeriodToMonth(LogicalDate periodStart, LogicalDate periodEnd) {
        Calendar oneMonthSinceStart = GregorianCalendar.getInstance();
        oneMonthSinceStart.setTime(periodStart);
        oneMonthSinceStart.add(Calendar.MONTH, 1);
        oneMonthSinceStart.add(Calendar.DATE, -1);
        oneMonthSinceStart.getTime();
        if (periodEnd.before(oneMonthSinceStart.getTime())) {
            return -1;
        } else {
            oneMonthSinceStart.add(Calendar.DATE, 1);
            if (periodEnd.before(oneMonthSinceStart.getTime())) {
                return 0;
            }
        }
        return 1;
    }

    public static int daysBetween(LogicalDate d1, LogicalDate d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24)) + 1;
    }
}
