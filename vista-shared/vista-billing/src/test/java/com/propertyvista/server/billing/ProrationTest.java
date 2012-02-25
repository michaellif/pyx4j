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
import java.text.SimpleDateFormat;

import junit.framework.TestCase;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.server.billing.ProrationUtils;
import com.propertyvista.server.billing.ProrationUtils.Method;

public class ProrationTest extends TestCase {

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");

    public void testProration() throws ParseException {

        assertEquals("Prorate 23/02/2012", new BigDecimal("0.24138"), ProrationUtils.prorate(new LogicalDate(formatter.parse("23-Feb-2012")), Method.Actual));
        assertEquals("Prorate 23/03/2012", new BigDecimal("0.29032"), ProrationUtils.prorate(new LogicalDate(formatter.parse("23-Mar-2012")), Method.Actual));
        assertEquals("Prorate 23/04/2012", new BigDecimal("0.26667"), ProrationUtils.prorate(new LogicalDate(formatter.parse("23-Apr-2012")), Method.Actual));

        assertEquals("Prorate 23/02/2012", new BigDecimal("0.24138"), ProrationUtils.prorate(new LogicalDate(formatter.parse("23-Feb-2012")), Method.Standard));
        assertEquals("Prorate 23/03/2012", new BigDecimal("0.30000"), ProrationUtils.prorate(new LogicalDate(formatter.parse("23-Mar-2012")), Method.Standard));
        assertEquals("Prorate 23/04/2012", new BigDecimal("0.26667"), ProrationUtils.prorate(new LogicalDate(formatter.parse("23-Apr-2012")), Method.Standard));

        assertEquals("Prorate 23/02/2012", new BigDecimal("0.23014"), ProrationUtils.prorate(new LogicalDate(formatter.parse("23-Feb-2012")), Method.Annual));
        assertEquals("Prorate 23/03/2012", new BigDecimal("0.29589"), ProrationUtils.prorate(new LogicalDate(formatter.parse("23-Mar-2012")), Method.Annual));
        assertEquals("Prorate 23/04/2012", new BigDecimal("0.26301"), ProrationUtils.prorate(new LogicalDate(formatter.parse("23-Apr-2012")), Method.Annual));

    }
}
