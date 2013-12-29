/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils.gottarent;

import junit.framework.TestCase;

import com.propertyvista.ils.gottarent.mapper.GottarentMapperUtils;

public class GottarentMapperUtilsTest extends TestCase {
    public void testScenario() {
        try {
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("123-456-7890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("1-123-456-7890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("+1-123-456-7890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("1-123-456-7890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("11234567890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("1234567890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("(1)123-456-7890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("(+1)123-456-7890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("(+1)123456-7890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("(+1)1234567890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("(+123)4567890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("(+1123)-456-7890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("1(+123)-456-7890")));
            assertTrue("123-456-7890".equalsIgnoreCase(GottarentMapperUtils.formatPhone("1(+123)456-7890")));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
