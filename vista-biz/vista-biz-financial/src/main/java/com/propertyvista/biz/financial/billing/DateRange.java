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
 */
package com.propertyvista.biz.financial.billing;

import com.pyx4j.commons.LogicalDate;

public class DateRange {

    private final LogicalDate fromDate;

    private final LogicalDate toDate;

    public DateRange(LogicalDate fromDate, LogicalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public LogicalDate getFromDate() {
        return fromDate;
    }

    public LogicalDate getToDate() {
        return toDate;
    }

}
