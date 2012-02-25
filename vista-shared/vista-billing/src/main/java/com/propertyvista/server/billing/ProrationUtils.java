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

public class ProrationUtils {

    enum Method {
        Actual, Standard, Annual
    }

    public static BigDecimal prorate(LogicalDate date, Method method) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
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
            proration = new BigDecimal(365).divide(new BigDecimal(12), 5, RoundingMode.HALF_UP);
            break;
        default:

        }
        return new BigDecimal(daysInMonth - dayOfMonth + 1).divide(proration, 5, RoundingMode.HALF_UP);

    }
}
