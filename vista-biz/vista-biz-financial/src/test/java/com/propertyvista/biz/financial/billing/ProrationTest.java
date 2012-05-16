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

        assertEquals("Prorate 23/02/2012", new BigDecimal("0.241379"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Feb-2012"),
                FinancialTestsUtils.getDate("29-Feb-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 01/03/2012", new BigDecimal("1"), ProrationUtils.prorate(FinancialTestsUtils.getDate("01-Mar-2012"),
                FinancialTestsUtils.getDate("31-Mar-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 23/03/2012", new BigDecimal("0.290323"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Mar-2012"),
                FinancialTestsUtils.getDate("31-Mar-2012"), BillingAccount.ProrationMethod.Actual));
        assertEquals("Prorate 23/04/2012", new BigDecimal("0.266667"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Apr-2012"),
                FinancialTestsUtils.getDate("30-Apr-2012"), BillingAccount.ProrationMethod.Actual));

        assertEquals("Prorate 23/02/2012", new BigDecimal("0.241379"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Feb-2012"),
                FinancialTestsUtils.getDate("29-Feb-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals("Prorate 23/03/2012", new BigDecimal("0.300000"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Mar-2012"),
                FinancialTestsUtils.getDate("31-Mar-2012"), BillingAccount.ProrationMethod.Standard));
        assertEquals("Prorate 23/04/2012", new BigDecimal("0.266667"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Apr-2012"),
                FinancialTestsUtils.getDate("30-Apr-2012"), BillingAccount.ProrationMethod.Standard));

        assertEquals("Prorate 23/02/2012", new BigDecimal("0.230137"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Feb-2012"),
                FinancialTestsUtils.getDate("29-Feb-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals("Prorate 23/03/2012", new BigDecimal("0.295890"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Mar-2012"),
                FinancialTestsUtils.getDate("31-Mar-2012"), BillingAccount.ProrationMethod.Annual));
        assertEquals("Prorate 23/04/2012", new BigDecimal("0.263014"), ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Apr-2012"),
                FinancialTestsUtils.getDate("30-Apr-2012"), BillingAccount.ProrationMethod.Annual));

        try {
            ProrationUtils.prorate(FinancialTestsUtils.getDate("23-Feb-2012"), FinancialTestsUtils.getDate("23-Mar-2012"),
                    BillingAccount.ProrationMethod.Actual);
            assertTrue("Prorate more than month didn't fail", false);
        } catch (BillingException e) {
        }

    }
}
