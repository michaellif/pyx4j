/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 18, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.adapters;

import junit.framework.Assert;

import org.junit.Test;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.gwt.server.DateUtils;

public class LogicalDateAsIntegerTest {

    private void assertDateToIntConversions(String dateString) {
        LogicalDate date = new LogicalDate(DateUtils.detectDateformat(dateString));
        int days = new LogicalDateAsIntegerPersistenceAdapterImpl().persist(date);
        LogicalDate converted = new LogicalDateAsIntegerPersistenceAdapterImpl().retrieve(days);
        Assert.assertEquals(dateString, date, converted);
    }

    @Test
    public void testDateToIntConversions() {
        assertDateToIntConversions("2000-01-01");
        assertDateToIntConversions("2011-03-17");
        assertDateToIntConversions("2011-03-16");
        assertDateToIntConversions("2015-07-16");

        LogicalDate date = new LogicalDate(DateUtils.detectDateformat("1990-01-01"));
        for (int i = 0; i <= 65050; i++) {
            assertDateToIntConversions(date.toString());
            date = DateUtils.daysAdd(date, 1);
        }
    }
}
