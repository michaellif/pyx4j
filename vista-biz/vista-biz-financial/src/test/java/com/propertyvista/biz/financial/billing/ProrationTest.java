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
import java.text.ParseException;

import org.junit.experimental.categories.Category;

import com.propertyvista.biz.financial.FinancialTestBase.FunctionalTests;
import com.propertyvista.biz.financial.FinancialTestsUtils;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.portal.rpc.shared.BillingException;

@Category(FunctionalTests.class)
public class ProrationTest extends VistaDBTestBase {

    public void testMonthlyActualProration() throws ParseException {

        // Actual proration method (dividing by actual number of days in the month period starts in)

        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.241379"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Feb-2012"), FinancialTestsUtils.getDate("29-Feb-2012"),
                        FinancialTestsUtils.getDate("23-Feb-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.225806"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Feb-2012"), FinancialTestsUtils.getDate("29-Feb-2012"),
                        FinancialTestsUtils.getDate("30-Jan-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("1"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("01-Mar-2012"), FinancialTestsUtils.getDate("31-Mar-2012"),
                        FinancialTestsUtils.getDate("01-Mar-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.290323"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Mar-2012"), FinancialTestsUtils.getDate("31-Mar-2012"),
                        FinancialTestsUtils.getDate("23-Mar-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.266667"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Apr-2012"), FinancialTestsUtils.getDate("30-Apr-2012"),
                        FinancialTestsUtils.getDate("23-Apr-2012"), BillingAccount.ProrationMethod.Actual));

        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.193548"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-May-2012"), FinancialTestsUtils.getDate("28-May-2012"),
                        FinancialTestsUtils.getDate("23-May-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.566667"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("08-Jun-2012"), FinancialTestsUtils.getDate("24-Jun-2012"),
                        FinancialTestsUtils.getDate("08-Jun-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.870968"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("03-Jul-2012"), FinancialTestsUtils.getDate("29-Jul-2012"),
                        FinancialTestsUtils.getDate("03-Jul-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.516129"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("16-Aug-2012"), FinancialTestsUtils.getDate("31-Aug-2012"),
                        FinancialTestsUtils.getDate("16-Aug-2012"), BillingAccount.ProrationMethod.Actual));

        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.533333"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("02-Sep-2012"), FinancialTestsUtils.getDate("17-Sep-2012"),
                        FinancialTestsUtils.getDate("02-Sep-2012"), BillingAccount.ProrationMethod.Actual));

        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.322581"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("11-Oct-2012"), FinancialTestsUtils.getDate("20-Oct-2012"),
                        FinancialTestsUtils.getDate("11-Oct-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.033333"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Nov-2012"), FinancialTestsUtils.getDate("23-Nov-2012"),
                        FinancialTestsUtils.getDate("23-Nov-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.580645"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("03-Dec-2012"), FinancialTestsUtils.getDate("20-Dec-2012"),
                        FinancialTestsUtils.getDate("03-Dec-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.225806"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("07-Jan-2013"), FinancialTestsUtils.getDate("13-Jan-2013"),
                        FinancialTestsUtils.getDate("07-Jan-2013"), BillingAccount.ProrationMethod.Actual));
        assertEquals(
                "Prorate using Actual method",
                new BigDecimal("0.107143"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Feb-2013"), FinancialTestsUtils.getDate("25-Feb-2013"),
                        FinancialTestsUtils.getDate("23-Feb-2013"), BillingAccount.ProrationMethod.Actual));

        try {
            ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Feb-2012"), FinancialTestsUtils.getDate("23-Mar-2012"),
                    FinancialTestsUtils.getDate("23-Feb-2012"), BillingAccount.ProrationMethod.Actual);
            assertTrue("Prorate period longer than month", false);
        } catch (BillingException e) {
        }
    }

    public void testMonthlyStandardProration() throws ParseException {

        // Standard proration method (dividing by 30 for all Month other than February)

        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.241379"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Feb-2012"), FinancialTestsUtils.getDate("29-Feb-2012"),
                        FinancialTestsUtils.getDate("23-Feb-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.300000"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Mar-2012"), FinancialTestsUtils.getDate("31-Mar-2012"),
                        FinancialTestsUtils.getDate("23-Mar-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.266667"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Apr-2012"), FinancialTestsUtils.getDate("30-Apr-2012"),
                        FinancialTestsUtils.getDate("23-Apr-2012"), BillingAccount.ProrationMethod.Standard));

        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.214286"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Feb-2011"), FinancialTestsUtils.getDate("28-Feb-2011"),
                        FinancialTestsUtils.getDate("23-Feb-2011"), BillingAccount.ProrationMethod.Standard));

        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.200000"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-May-2012"), FinancialTestsUtils.getDate("28-May-2012"),
                        FinancialTestsUtils.getDate("23-May-2012"), BillingAccount.ProrationMethod.Standard));

        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.566667"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("08-Jun-2012"), FinancialTestsUtils.getDate("24-Jun-2012"),
                        FinancialTestsUtils.getDate("08-Jun-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.900000"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("03-Jul-2012"), FinancialTestsUtils.getDate("29-Jul-2012"),
                        FinancialTestsUtils.getDate("03-Jul-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.533333"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("16-Aug-2012"), FinancialTestsUtils.getDate("31-Aug-2012"),
                        FinancialTestsUtils.getDate("16-Aug-2012"), BillingAccount.ProrationMethod.Standard));

        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.533333"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("02-Sep-2012"), FinancialTestsUtils.getDate("17-Sep-2012"),
                        FinancialTestsUtils.getDate("02-Sep-2012"), BillingAccount.ProrationMethod.Standard));

        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.333333"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("11-Oct-2012"), FinancialTestsUtils.getDate("20-Oct-2012"),
                        FinancialTestsUtils.getDate("11-Oct-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.033333"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Nov-2012"), FinancialTestsUtils.getDate("23-Nov-2012"),
                        FinancialTestsUtils.getDate("23-Nov-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.600000"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("03-Dec-2012"), FinancialTestsUtils.getDate("20-Dec-2012"),
                        FinancialTestsUtils.getDate("03-Dec-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.233333"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("07-Jan-2013"), FinancialTestsUtils.getDate("13-Jan-2013"),
                        FinancialTestsUtils.getDate("07-Jan-2013"), BillingAccount.ProrationMethod.Standard));

        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.107143"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Feb-2013"), FinancialTestsUtils.getDate("25-Feb-2013"),
                        FinancialTestsUtils.getDate("23-Feb-2013"), BillingAccount.ProrationMethod.Standard));

        assertEquals(
                "Prorate using Standard method",
                new BigDecimal("0.066667"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("24-Mar-2013"), FinancialTestsUtils.getDate("25-Mar-2013"),
                        FinancialTestsUtils.getDate("24-Mar-2013"), BillingAccount.ProrationMethod.Standard));
    }

    public void testMonthlyAnnualProration() throws ParseException {

        /// Annual proration method - devided always by 365 days
        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.230137"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Feb-2012"), FinancialTestsUtils.getDate("29-Feb-2012"),
                        FinancialTestsUtils.getDate("23-Feb-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.295890"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Mar-2012"), FinancialTestsUtils.getDate("31-Mar-2012"),
                        FinancialTestsUtils.getDate("23-Mar-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.263014"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Apr-2012"), FinancialTestsUtils.getDate("30-Apr-2012"),
                        FinancialTestsUtils.getDate("23-Apr-2012"), BillingAccount.ProrationMethod.Annual));

        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.197260"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-May-2012"), FinancialTestsUtils.getDate("28-May-2012"),
                        FinancialTestsUtils.getDate("23-May-2012"), BillingAccount.ProrationMethod.Annual));

        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.558904"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("08-Jun-2012"), FinancialTestsUtils.getDate("24-Jun-2012"),
                        FinancialTestsUtils.getDate("08-Jun-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.887671"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("03-Jul-2012"), FinancialTestsUtils.getDate("29-Jul-2012"),
                        FinancialTestsUtils.getDate("03-Jul-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.526027"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("16-Aug-2012"), FinancialTestsUtils.getDate("31-Aug-2012"),
                        FinancialTestsUtils.getDate("16-Aug-2012"), BillingAccount.ProrationMethod.Annual));

        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.526027"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("02-Sep-2012"), FinancialTestsUtils.getDate("17-Sep-2012"),
                        FinancialTestsUtils.getDate("02-Sep-2012"), BillingAccount.ProrationMethod.Annual));

        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.328767"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("11-Oct-2012"), FinancialTestsUtils.getDate("20-Oct-2012"),
                        FinancialTestsUtils.getDate("11-Oct-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.032877"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Nov-2012"), FinancialTestsUtils.getDate("23-Nov-2012"),
                        FinancialTestsUtils.getDate("23-Nov-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.591781"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("03-Dec-2012"), FinancialTestsUtils.getDate("20-Dec-2012"),
                        FinancialTestsUtils.getDate("03-Dec-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.230137"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("07-Jan-2013"), FinancialTestsUtils.getDate("13-Jan-2013"),
                        FinancialTestsUtils.getDate("07-Jan-2013"), BillingAccount.ProrationMethod.Annual));

        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.098630"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("23-Feb-2013"), FinancialTestsUtils.getDate("25-Feb-2013"),
                        FinancialTestsUtils.getDate("23-Feb-2013"), BillingAccount.ProrationMethod.Annual));

        assertEquals(
                "Prorate using Annual method",
                new BigDecimal("0.065753"),
                ProrationUtils.prorateMonthlyPeriod(FinancialTestsUtils.getDate("24-Mar-2013"), FinancialTestsUtils.getDate("25-Mar-2013"),
                        FinancialTestsUtils.getDate("24-Mar-2013"), BillingAccount.ProrationMethod.Annual));

    }

    public void testBiWeeklyProration() throws ParseException {
        assertEquals("Prorate using Standard method", new BigDecimal("1.000000"),
                ProrationUtils.prorateNormalPeriod(FinancialTestsUtils.getDate("23-Feb-2012"), FinancialTestsUtils.getDate("29-Feb-2012"), 7));
        assertEquals("Prorate using Standard method", new BigDecimal("0.428571"),
                ProrationUtils.prorateNormalPeriod(FinancialTestsUtils.getDate("23-Feb-2012"), FinancialTestsUtils.getDate("25-Feb-2012"), 7));
    }
}
