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
import java.text.ParseException;

import junit.framework.TestCase;

import com.propertyvista.domain.tenant.lease.LeaseFinancial;

public class ProrationTest extends TestCase {

    public void testProration() throws ParseException {

        assertEquals("Prorate 23/02/2012", new BigDecimal("0.241379"),
                ProrationUtils.prorate(BillingTestUtils.getDate("23-Feb-2012"), BillingTestUtils.getDate("29-Feb-2012"), LeaseFinancial.ProrationMethod.Actual));
        assertEquals("Prorate 23/03/2012", new BigDecimal("0.290323"),
                ProrationUtils.prorate(BillingTestUtils.getDate("23-Mar-2012"), BillingTestUtils.getDate("31-Mar-2012"), LeaseFinancial.ProrationMethod.Actual));
        assertEquals("Prorate 23/04/2012", new BigDecimal("0.266667"),
                ProrationUtils.prorate(BillingTestUtils.getDate("23-Apr-2012"), BillingTestUtils.getDate("30-Apr-2012"), LeaseFinancial.ProrationMethod.Actual));

        assertEquals("Prorate 23/02/2012", new BigDecimal("0.241379"), ProrationUtils.prorate(BillingTestUtils.getDate("23-Feb-2012"),
                BillingTestUtils.getDate("29-Feb-2012"), LeaseFinancial.ProrationMethod.Standard));
        assertEquals("Prorate 23/03/2012", new BigDecimal("0.300000"), ProrationUtils.prorate(BillingTestUtils.getDate("23-Mar-2012"),
                BillingTestUtils.getDate("31-Mar-2012"), LeaseFinancial.ProrationMethod.Standard));
        assertEquals("Prorate 23/04/2012", new BigDecimal("0.266667"), ProrationUtils.prorate(BillingTestUtils.getDate("23-Apr-2012"),
                BillingTestUtils.getDate("30-Apr-2012"), LeaseFinancial.ProrationMethod.Standard));

        assertEquals("Prorate 23/02/2012", new BigDecimal("0.230137"),
                ProrationUtils.prorate(BillingTestUtils.getDate("23-Feb-2012"), BillingTestUtils.getDate("29-Feb-2012"), LeaseFinancial.ProrationMethod.Annual));
        assertEquals("Prorate 23/03/2012", new BigDecimal("0.295890"),
                ProrationUtils.prorate(BillingTestUtils.getDate("23-Mar-2012"), BillingTestUtils.getDate("31-Mar-2012"), LeaseFinancial.ProrationMethod.Annual));
        assertEquals("Prorate 23/04/2012", new BigDecimal("0.263014"),
                ProrationUtils.prorate(BillingTestUtils.getDate("23-Apr-2012"), BillingTestUtils.getDate("30-Apr-2012"), LeaseFinancial.ProrationMethod.Annual));

        try {
            ProrationUtils.prorate(BillingTestUtils.getDate("23-Feb-2012"), BillingTestUtils.getDate("23-Mar-2012"), LeaseFinancial.ProrationMethod.Actual);
            assertTrue("Prorate more than month didn't fail", false);
        } catch (BillingException e) {
        }

    }
}
