/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.generator;

import junit.framework.TestCase;

import com.propertvista.generator.PTGenerator;
import com.propertvista.generator.gdo.ApplicationSummaryGDO;

import com.propertyvista.domain.PreloadConfig;
import com.propertyvista.domain.User;
import com.propertyvista.portal.server.TestUtil;
import com.propertyvista.server.common.reference.SharedData;

public class VistaDataGeneratorTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testConsistentDataCreation() {
        final long seed1 = 250;

        SharedData.init();
        PTGenerator generator1 = new PTGenerator(seed1, PreloadConfig.createTest());
        User user1 = generator1.createUser(1);

        ApplicationSummaryGDO summary1 = generator1.createSummary(user1, null);

        // Do some other data generation
        PTGenerator generatorX = new PTGenerator(System.currentTimeMillis(), PreloadConfig.createTest());
        User userX = generatorX.createUser(1);
        generatorX.createSummary(userX, null);

        SharedData.init();

        PTGenerator generator2 = new PTGenerator(seed1, PreloadConfig.createTest());
        User user2 = generator2.createUser(1);
        ApplicationSummaryGDO summary2 = generator2.createSummary(user2, null);

        TestUtil.assertEqual("Same data expected", summary1, summary2);
    }
}
