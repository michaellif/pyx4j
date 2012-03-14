/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 4, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.billing;

import junit.framework.TestCase;

public class DateUtilsTest extends TestCase {

    public void testOverlap() {
        {
            DateRange dateRange = DateUtils.getOverlappingRange(new DateRange(BillingTestUtils.getDate("1-Jul-2011"), BillingTestUtils.getDate("31-Jul-2011")),
                    new DateRange(BillingTestUtils.getDate("1-Jul-2011"), BillingTestUtils.getDate("31-Jul-2011")));
            assertEquals(BillingTestUtils.getDate("1-Jul-2011"), dateRange.getFromDate());
            assertEquals(BillingTestUtils.getDate("31-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = DateUtils.getOverlappingRange(new DateRange(BillingTestUtils.getDate("5-Jul-2011"), BillingTestUtils.getDate("4-Aug-2011")),
                    new DateRange(BillingTestUtils.getDate("5-Jul-2011"), BillingTestUtils.getDate("31-Jul-2011")));
            assertEquals(BillingTestUtils.getDate("5-Jul-2011"), dateRange.getFromDate());
            assertEquals(BillingTestUtils.getDate("31-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = DateUtils.getOverlappingRange(new DateRange(BillingTestUtils.getDate("8-Jul-2011"), BillingTestUtils.getDate("15-Jul-2011")),
                    new DateRange(BillingTestUtils.getDate("5-Jul-2011"), BillingTestUtils.getDate("31-Jul-2011")));
            assertEquals(BillingTestUtils.getDate("8-Jul-2011"), dateRange.getFromDate());
            assertEquals(BillingTestUtils.getDate("15-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = DateUtils.getOverlappingRange(new DateRange(BillingTestUtils.getDate("8-Jul-2011"), BillingTestUtils.getDate("15-Jul-2011")),
                    new DateRange(BillingTestUtils.getDate("5-Jul-2011"), BillingTestUtils.getDate("31-Jul-2011")));
            assertEquals(BillingTestUtils.getDate("8-Jul-2011"), dateRange.getFromDate());
            assertEquals(BillingTestUtils.getDate("15-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = DateUtils.getOverlappingRange(new DateRange(null, BillingTestUtils.getDate("15-Jul-2011")),
                    new DateRange(BillingTestUtils.getDate("5-Jul-2011"), BillingTestUtils.getDate("31-Jul-2011")));
            assertEquals(BillingTestUtils.getDate("5-Jul-2011"), dateRange.getFromDate());
            assertEquals(BillingTestUtils.getDate("15-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = DateUtils.getOverlappingRange(new DateRange(null, BillingTestUtils.getDate("15-Jul-2011")),
                    new DateRange(BillingTestUtils.getDate("5-Jul-2011"), null));
            assertEquals(BillingTestUtils.getDate("5-Jul-2011"), dateRange.getFromDate());
            assertEquals(BillingTestUtils.getDate("15-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = DateUtils.getOverlappingRange(new DateRange(BillingTestUtils.getDate("8-Jul-2011"), BillingTestUtils.getDate("15-Jul-2011")),
                    new DateRange(BillingTestUtils.getDate("25-Jul-2011"), BillingTestUtils.getDate("31-Jul-2011")));
            assertEquals(null, dateRange);
        }

        {
            DateRange dateRange = DateUtils.getOverlappingRange(new DateRange(BillingTestUtils.getDate("8-Jul-2011"), BillingTestUtils.getDate("15-Jul-2011")),
                    new DateRange(BillingTestUtils.getDate("12-Jul-2011"), BillingTestUtils.getDate("12-Jul-2011")));
            assertEquals(BillingTestUtils.getDate("12-Jul-2011"), dateRange.getFromDate());
            assertEquals(BillingTestUtils.getDate("12-Jul-2011"), dateRange.getToDate());
        }

    }
}
