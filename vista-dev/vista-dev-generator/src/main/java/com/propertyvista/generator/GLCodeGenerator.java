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
package com.propertyvista.generator;

import java.util.Random;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.financial.GlCode;

public class GLCodeGenerator { //currently not used, data is preloaded from csv files (27/02/2012)

    public static GlCode createGlCode() {
        GlCode glCode = EntityFactory.create(GlCode.class);
        Random randomGenerator = new Random(1);

        glCode.codeId().setValue(Integer.valueOf((int) (randomGenerator.nextDouble() * 10000)));
        glCode.description().setValue("description...");
        return glCode;
    }
}