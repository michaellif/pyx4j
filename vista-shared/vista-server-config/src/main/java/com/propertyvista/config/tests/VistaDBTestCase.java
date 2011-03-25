/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config.tests;

import junit.framework.TestCase;

import com.pyx4j.config.server.ServerSideConfiguration;

public abstract class VistaDBTestCase extends TestCase {

    private static boolean initOnce = true;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (initOnce) {
            ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration());
            initOnce = false;
        }
    }

}
