/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 21, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.onboarding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.junit.Test;

import com.pyx4j.essentials.server.xml.XMLEntitySchemaWriter;

public class OnboardingSchemaTest {

    @Test
    public void testOnboardingSchema() throws FileNotFoundException {
        XMLEntitySchemaWriter.printSchema(new FileOutputStream(new File("target", "onboarding.xsd")), false, RequestMessageIO.class, ResponseMessageIO.class);
    }
}
