/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.financial.billingext;

import java.math.BigDecimal;

import org.junit.Ignore;

import com.propertyvista.biz.financial.ExternalTestBase;

@Ignore
public class ExternalBillingTest extends ExternalTestBase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        preloadData();
    }

    public void testScenatio() {
        setDate("15-May-2011");
        createLease("1-Mar-2009", "31-Aug-2011", null, new BigDecimal("300.00"));

//        ReceivableServiceImpl service = new ReceivableServiceImpl();
//        PropertyFacade f;
    }
}
