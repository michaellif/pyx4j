/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 27, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.crm.server.openapi;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.crm.server.openapi.model.BuildingsRS;

public class OpenApiModelTest {

    private final static Logger log = LoggerFactory.getLogger(OpenApiModelTest.class);

    @Test
    public void testXsdSchema() throws Exception {
        MarshallUtil.printSchema(BuildingsRS.class, System.out, true);
    }

    @Test
    public void testXmlBuildings() throws Exception {

        BuildingsRS buildings = new BuildingsRS();

        String xml = MarshallUtil.marshall(buildings);

        log.info("\n{}\n", xml);
    }
}
