/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.common;

import com.pyx4j.commons.LogicalDate;

@SuppressWarnings("deprecation")
public class TimeIntervalMonth implements TimeIntervalSize {

    private static final long serialVersionUID = -5981905519961585745L;

    @Override
    public LogicalDate start(LogicalDate includedDate) {
        LogicalDate start = new LogicalDate(includedDate);
        start.setDate(1);
        return start;
    }

    @Override
    public LogicalDate end(LogicalDate includedDate) {
        int[] possibleLastDates = new int[] { 31, 30, 29, 28 };
        int month = includedDate.getMonth();
        LogicalDate end = null;
        for (int possibleLastDate : possibleLastDates) {
            end = new LogicalDate(includedDate);
            end.setDate(possibleLastDate);
            if (end.getMonth() == month) {
                return end;
            }
        }
        throw new Error("end of month was cold not be computed?!?!");
    }
}
