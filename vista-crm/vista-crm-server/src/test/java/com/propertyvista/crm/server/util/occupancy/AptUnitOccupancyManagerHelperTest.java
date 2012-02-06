/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util.occupancy;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;

public class AptUnitOccupancyManagerHelperTest {

    private static final boolean TEST_ON_MYSQL = false;

    @Before
    public void setUp() {
        if (TEST_ON_MYSQL) {
            ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(true));
        } else {
            VistaTestDBSetup.init();
        }

    }

    @Test
    public void testSplitSegment() throws ParseException {

    }

}
