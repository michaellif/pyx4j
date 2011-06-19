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

import com.propertyvista.common.domain.User;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.Summary;
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
        PTGenerator generator1 = new PTGenerator(seed1);
        User user1 = generator1.createUser(1);
        Application application1 = generator1.createApplication(user1);
        Summary summary1 = generator1.createSummary(application1, null);

        // To some other data generation
        PTGenerator generatorX = new PTGenerator(System.currentTimeMillis());
        User userX = generatorX.createUser(1);
        Application applicationX = generatorX.createApplication(userX);
        generatorX.createSummary(applicationX, null);

        SharedData.init();

        PTGenerator generator2 = new PTGenerator(seed1);
        User user2 = generator2.createUser(1);
        Application application2 = generator2.createApplication(user2);
        Summary summary2 = generator2.createSummary(application2, null);

        TestUtil.assertEqual("Same data expected", summary1, summary2);
    }
}
