/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.financial.billing;

import com.pyx4j.commons.LogicalDate;

public class DateUtils {

    public static final DateRange getOverlappingRange(DateRange range1, DateRange range2) {
        LogicalDate fromDate1 = range1.getFromDate() == null ? new LogicalDate(0) : range1.getFromDate();
        LogicalDate toDate1 = range1.getToDate() == null ? new LogicalDate(Long.MAX_VALUE) : range1.getToDate();

        LogicalDate fromDate2 = range2.getFromDate() == null ? new LogicalDate(0) : range2.getFromDate();
        LogicalDate toDate2 = range2.getToDate() == null ? new LogicalDate(Long.MAX_VALUE) : range2.getToDate();

        LogicalDate fromDate;
        LogicalDate toDate;

        if (fromDate1.after(toDate2) || fromDate2.after(toDate1)) {
            return null;
        }

        if (fromDate1.after(fromDate2)) {
            fromDate = fromDate1;
        } else {
            fromDate = fromDate2;
        }

        if (toDate1.before(toDate2)) {
            toDate = toDate1;
        } else {
            toDate = toDate2;
        }

        return new DateRange(fromDate, toDate);
    }
}
