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

import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.portal.server.TestUtil;

public class VistaDataGeneratorTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testConsistentDataCreation() {
        final long seed1 = 250;

        VistaDevPreloadConfig config = VistaDevPreloadConfig.createTest();
        config.ptGenerationSeed = 250;
        PTGenerator generator1 = new PTGenerator(config);
        TenantUser user1 = generator1.createUser(1);

        ApplicationSummaryGDO summary1 = generator1.createSummary(user1, null);

        // Do some other data generation
        VistaDevPreloadConfig configX = VistaDevPreloadConfig.createTest();
        configX.ptGenerationSeed = System.currentTimeMillis();
        PTGenerator generatorX = new PTGenerator(configX);
        TenantUser userX = generatorX.createUser(1);
        generatorX.createSummary(userX, null);

        VistaDevPreloadConfig config2 = VistaDevPreloadConfig.createTest();
        config2.ptGenerationSeed = System.currentTimeMillis();
        PTGenerator generator2 = new PTGenerator(config2);
        TenantUser user2 = generator2.createUser(1);
        ApplicationSummaryGDO summary2 = generator2.createSummary(user2, null);

        TestUtil.assertEqual("Same data expected", summary1, summary2);
    }
}
