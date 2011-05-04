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

import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.server.TestUtil;

public class VistaDataGeneratorTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testConsistentDataCreation() {
        final long seed1 = 250;

        SharedData.init();
        VistaDataGenerator generator1 = new VistaDataGenerator(seed1);
        User user1 = generator1.createUser(1);
        Application application1 = generator1.createApplication(user1);
        Summary summary1 = generator1.createSummary(application1, null);

        // To some other data generation
        VistaDataGenerator generatorX = new VistaDataGenerator(System.currentTimeMillis());
        generatorX.createSummary(null, null);

        SharedData.init();

        VistaDataGenerator generator2 = new VistaDataGenerator(seed1);
        User user2 = generator1.createUser(1);
        Application application2 = generator2.createApplication(user2);
        Summary summary2 = generator2.createSummary(application2, null);

        TestUtil.assertEqual("Same data expected", summary1, summary2);
    }
}
