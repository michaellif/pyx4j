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
 */
package com.propertyvista.biz.financial.billing.internal;

import org.junit.experimental.categories.Category;

import com.propertyvista.biz.financial.LeaseFinancialTestBase;
import com.propertyvista.biz.financial.billing.BillDateUtils;
import com.propertyvista.biz.financial.billing.DateRange;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class DateUtilsTest extends LeaseFinancialTestBase {

    public void testOverlappingRange() {
        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(new DateRange(getDate("01-Jul-2011"), getDate("31-Jul-2011")), new DateRange(
                    getDate("01-Jul-2011"), getDate("31-Jul-2011")));
            assertEquals(getDate("01-Jul-2011"), dateRange.getFromDate());
            assertEquals(getDate("31-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(new DateRange(getDate("05-Jul-2011"), getDate("4-Aug-2011")), new DateRange(
                    getDate("05-Jul-2011"), getDate("31-Jul-2011")));
            assertEquals(getDate("05-Jul-2011"), dateRange.getFromDate());
            assertEquals(getDate("31-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(new DateRange(getDate("08-Jul-2011"), getDate("15-Jul-2011")), new DateRange(
                    getDate("05-Jul-2011"), getDate("31-Jul-2011")));
            assertEquals(getDate("08-Jul-2011"), dateRange.getFromDate());
            assertEquals(getDate("15-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(new DateRange(getDate("08-Jul-2011"), getDate("15-Jul-2011")), new DateRange(
                    getDate("05-Jul-2011"), getDate("31-Jul-2011")));
            assertEquals(getDate("08-Jul-2011"), dateRange.getFromDate());
            assertEquals(getDate("15-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(new DateRange(null, getDate("15-Jul-2011")), new DateRange(getDate("05-Jul-2011"),
                    getDate("31-Jul-2011")));
            assertEquals(getDate("05-Jul-2011"), dateRange.getFromDate());
            assertEquals(getDate("15-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(new DateRange(null, getDate("15-Jul-2011")), new DateRange(getDate("05-Jul-2011"), null));
            assertEquals(getDate("05-Jul-2011"), dateRange.getFromDate());
            assertEquals(getDate("15-Jul-2011"), dateRange.getToDate());
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(new DateRange(getDate("08-Jul-2011"), getDate("15-Jul-2011")), new DateRange(
                    getDate("25-Jul-2011"), getDate("31-Jul-2011")));
            assertEquals(null, dateRange);
        }

        {
            DateRange dateRange = BillDateUtils.getOverlappingRange(new DateRange(getDate("08-Jul-2011"), getDate("15-Jul-2011")), new DateRange(
                    getDate("12-Jul-2011"), getDate("12-Jul-2011")));
            assertEquals(getDate("12-Jul-2011"), dateRange.getFromDate());
            assertEquals(getDate("12-Jul-2011"), dateRange.getToDate());
        }

    }

}
