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
package com.propertyvista.server.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.financial.BillingAccount;

public class ProrationUtils {

    public static BigDecimal prorate(LogicalDate from, LogicalDate to, BillingAccount.ProrationMethod method) {
        assert from != null && to != null;
        Calendar calendarFrom = new GregorianCalendar();
        calendarFrom.setTime(from);
        int daysInMonth = calendarFrom.getActualMaximum(Calendar.DAY_OF_MONTH);
        int monthFrom = calendarFrom.get(Calendar.MONTH);
        int dayOfMonthFrom = calendarFrom.get(Calendar.DAY_OF_MONTH);

        Calendar calendarTo = new GregorianCalendar();
        calendarTo.setTime(to);
        int monthTo = calendarTo.get(Calendar.MONTH);
        int dayOfMonthTo = calendarTo.get(Calendar.DAY_OF_MONTH);

        int between = daysBetween(from, to);

        if (((monthTo - monthFrom == 0) && (dayOfMonthFrom <= dayOfMonthTo)) || ((monthTo - monthFrom == 1) && (dayOfMonthFrom > dayOfMonthTo))) {
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
        } else {
            throw new BillingException("proration can't be calculated for a period more than one month, but period was defined as " + from + " - " + to);
        }

    }

    public static int daysBetween(LogicalDate d1, LogicalDate d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24)) + 1;
    }
}
