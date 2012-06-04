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

import com.propertyvista.biz.financial.FinancialTestsUtils;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.financial.BillingAccount;

public class ProrationTest extends VistaDBTestBase {

    public void testProration() throws ParseException {

        // Actual proration method (dividing by actual number of days in the month period starts in)

        assertEquals("Prorate 23/02/2012", new BigDecimal("0.241379"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Feb-2012"),
                FinancialTestsUtils.getDate("29-Feb-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 01/03/2012", new BigDecimal("1"), ProrationUtils.prorate(FinancialTestsUtils.getDate("01-Mar-2012"),
                FinancialTestsUtils.getDate("31-Mar-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 23/03/2012", new BigDecimal("0.290323"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Mar-2012"),
                FinancialTestsUtils.getDate("31-Mar-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 23/04/2012", new BigDecimal("0.266667"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Apr-2012"),
                FinancialTestsUtils.getDate("30-Apr-2012"), BillingAccount.ProrationMethod.Actual));

        assertEquals("Prorate 23/05/2012", new BigDecimal("0.193548"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-May-2012"),
                FinancialTestsUtils.getDate("28-May-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 08/06/2012", new BigDecimal("0.566667"), ProrationUtils.prorate(FinancialTestsUtils.getDate("08-Jun-2012"),
                FinancialTestsUtils.getDate("24-Jun-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 03/07/2012", new BigDecimal("0.870968"), ProrationUtils.prorate(FinancialTestsUtils.getDate("03-Jul-2012"),
                FinancialTestsUtils.getDate("29-Jul-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 16/08/2012", new BigDecimal("0.516129"), ProrationUtils.prorate(FinancialTestsUtils.getDate("16-Aug-2012"),
                FinancialTestsUtils.getDate("31-Aug-2012"), BillingAccount.ProrationMethod.Actual));

        assertEquals("Prorate 02/09/2012", new BigDecimal("0.533333"), ProrationUtils.prorate(FinancialTestsUtils.getDate("02-Sep-2012"),
                FinancialTestsUtils.getDate("17-Sep-2012"), BillingAccount.ProrationMethod.Actual));

        assertEquals("Prorate 11/10/2012", new BigDecimal("0.322581"), ProrationUtils.prorate(FinancialTestsUtils.getDate("11-Oct-2012"),
                FinancialTestsUtils.getDate("20-Oct-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 23/11/2012", new BigDecimal("0.033333"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Nov-2012"),
                FinancialTestsUtils.getDate("23-Nov-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 03/12/2012", new BigDecimal("0.580645"), ProrationUtils.prorate(FinancialTestsUtils.getDate("03-Dec-2012"),
                FinancialTestsUtils.getDate("20-Dec-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 07/01/2013", new BigDecimal("0.225806"), ProrationUtils.prorate(FinancialTestsUtils.getDate("07-Jan-2013"),
                FinancialTestsUtils.getDate("13-Jan-2013"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 23/02/2013", new BigDecimal("0.107143"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Feb-2013"),
                FinancialTestsUtils.getDate("25-Feb-2013"), BillingAccount.ProrationMethod.Actual));

        // Standard proration method (dividing by 30 for all Month other than February)

        assertEquals("Prorate 23/02/2012", new BigDecimal("0.241379"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Feb-2012"),
                FinancialTestsUtils.getDate("29-Feb-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals("Prorate 23/03/2012", new BigDecimal("0.300000"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Mar-2012"),
                FinancialTestsUtils.getDate("31-Mar-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals("Prorate 23/04/2012", new BigDecimal("0.266667"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Apr-2012"),
                FinancialTestsUtils.getDate("30-Apr-2012"), BillingAccount.ProrationMethod.Standard));

        assertEquals("Prorate 23/02/2011", new BigDecimal("0.214286"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Feb-2011"),
                FinancialTestsUtils.getDate("28-Feb-2011"), BillingAccount.ProrationMethod.Standard));

        assertEquals("Prorate 23/05/2012", new BigDecimal("0.200000"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-May-2012"),
                FinancialTestsUtils.getDate("28-May-2012"), BillingAccount.ProrationMethod.Standard));

        assertEquals("Prorate 08/06/2012", new BigDecimal("0.566667"), ProrationUtils.prorate(FinancialTestsUtils.getDate("08-Jun-2012"),
                FinancialTestsUtils.getDate("24-Jun-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals("Prorate 03/07/2012", new BigDecimal("0.900000"), ProrationUtils.prorate(FinancialTestsUtils.getDate("03-Jul-2012"),
                FinancialTestsUtils.getDate("29-Jul-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals("Prorate 16/08/2012", new BigDecimal("0.533333"), ProrationUtils.prorate(FinancialTestsUtils.getDate("16-Aug-2012"),
                FinancialTestsUtils.getDate("31-Aug-2012"), BillingAccount.ProrationMethod.Standard));

        assertEquals("Prorate 02/09/2012", new BigDecimal("0.533333"), ProrationUtils.prorate(FinancialTestsUtils.getDate("02-Sep-2012"),
                FinancialTestsUtils.getDate("17-Sep-2012"), BillingAccount.ProrationMethod.Standard));

        assertEquals("Prorate 11/10/2012", new BigDecimal("0.333333"), ProrationUtils.prorate(FinancialTestsUtils.getDate("11-Oct-2012"),
                FinancialTestsUtils.getDate("20-Oct-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals("Prorate 23/11/2012", new BigDecimal("0.033333"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Nov-2012"),
                FinancialTestsUtils.getDate("23-Nov-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals("Prorate 03/12/2012", new BigDecimal("0.600000"), ProrationUtils.prorate(FinancialTestsUtils.getDate("03-Dec-2012"),
                FinancialTestsUtils.getDate("20-Dec-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals("Prorate 07/01/2013", new BigDecimal("0.233333"), ProrationUtils.prorate(FinancialTestsUtils.getDate("07-Jan-2013"),
                FinancialTestsUtils.getDate("13-Jan-2013"), BillingAccount.ProrationMethod.Standard));

        assertEquals("Prorate 23/02/2013", new BigDecimal("0.107143"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Feb-2013"),
                FinancialTestsUtils.getDate("25-Feb-2013"), BillingAccount.ProrationMethod.Standard));

        assertEquals("Prorate 24/03/2013", new BigDecimal("0.066667"), ProrationUtils.prorate(FinancialTestsUtils.getDate("24-Mar-2013"),
                FinancialTestsUtils.getDate("25-Mar-2013"), BillingAccount.ProrationMethod.Standard));

        /// Annual proration method - devided always by 365 days
        assertEquals("Prorate 23/02/2012", new BigDecimal("0.230137"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Feb-2012"),
                FinancialTestsUtils.getDate("29-Feb-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals("Prorate 23/03/2012", new BigDecimal("0.295890"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Mar-2012"),
                FinancialTestsUtils.getDate("31-Mar-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals("Prorate 23/04/2012", new BigDecimal("0.263014"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Apr-2012"),
                FinancialTestsUtils.getDate("30-Apr-2012"), BillingAccount.ProrationMethod.Annual));

        assertEquals("Prorate 23/05/2012", new BigDecimal("0.197260"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-May-2012"),
                FinancialTestsUtils.getDate("28-May-2012"), BillingAccount.ProrationMethod.Annual));

        assertEquals("Prorate 08/06/2012", new BigDecimal("0.558904"), ProrationUtils.prorate(FinancialTestsUtils.getDate("08-Jun-2012"),
                FinancialTestsUtils.getDate("24-Jun-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals("Prorate 03/07/2012", new BigDecimal("0.887671"), ProrationUtils.prorate(FinancialTestsUtils.getDate("03-Jul-2012"),
                FinancialTestsUtils.getDate("29-Jul-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals("Prorate 16/08/2012", new BigDecimal("0.526027"), ProrationUtils.prorate(FinancialTestsUtils.getDate("16-Aug-2012"),
                FinancialTestsUtils.getDate("31-Aug-2012"), BillingAccount.ProrationMethod.Annual));

        assertEquals("Prorate 02/09/2012", new BigDecimal("0.526027"), ProrationUtils.prorate(FinancialTestsUtils.getDate("02-Sep-2012"),
                FinancialTestsUtils.getDate("17-Sep-2012"), BillingAccount.ProrationMethod.Annual));

        assertEquals("Prorate 11/10/2012", new BigDecimal("0.328767"), ProrationUtils.prorate(FinancialTestsUtils.getDate("11-Oct-2012"),
                FinancialTestsUtils.getDate("20-Oct-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals("Prorate 23/11/2012", new BigDecimal("0.032877"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Nov-2012"),
                FinancialTestsUtils.getDate("23-Nov-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals("Prorate 03/12/2012", new BigDecimal("0.591781"), ProrationUtils.prorate(FinancialTestsUtils.getDate("03-Dec-2012"),
                FinancialTestsUtils.getDate("20-Dec-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals("Prorate 07/01/2013", new BigDecimal("0.230137"), ProrationUtils.prorate(FinancialTestsUtils.getDate("07-Jan-2013"),
                FinancialTestsUtils.getDate("13-Jan-2013"), BillingAccount.ProrationMethod.Annual));

        assertEquals("Prorate 23/02/2013", new BigDecimal("0.098630"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Feb-2013"),
                FinancialTestsUtils.getDate("25-Feb-2013"), BillingAccount.ProrationMethod.Annual));

        assertEquals("Prorate 24/03/2013", new BigDecimal("0.065753"), ProrationUtils.prorate(FinancialTestsUtils.getDate("24-Mar-2013"),
                FinancialTestsUtils.getDate("25-Mar-2013"), BillingAccount.ProrationMethod.Annual));

        try {
            ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Feb-2012"), FinancialTestsUtils.getDate("23-Mar-2012"),
                    BillingAccount.ProrationMethod.Actual);
            assertTrue("Prorate more than month didn't fail", false);
        } catch (BillingException e) {
        }

    }
}
