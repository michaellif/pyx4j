/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp;

import org.junit.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.PreloadConfig;
import com.propertyvista.portal.domain.ptapp.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.ptapp.CurrentApplication;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationService;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

public class ApplicationServiceTest extends VistaDBTestBase {
    //    private final static Logger log = LoggerFactory.getLogger(ApplicationServiceTest.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        new VistaDataPreloaders(PreloadConfig.createTest()).preloadAll(false);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestLifecycle.tearDown();
    }

    private ApplicationService createService() {
        return TestServiceFactory.create(ApplicationService.class);
    }

    public void testGetCurrentApplicationNew() {
        HappyPath.step1createAccount();
        HappyPath.step2createApplication();
    }

    public void testGetCurrentApplicationNewBadCriteria() {
        HappyPath.step1createAccount();

        UnitSelectionCriteria unitSelectionCriteria = EntityFactory.create(UnitSelectionCriteria.class);

        ApplicationService applicationService = createService();
        applicationService.getCurrentApplication(new UnitTestsAsyncCallback<CurrentApplication>() {
            @Override
            public void onSuccess(CurrentApplication result) {
                Assert.fail("Expect exception");
            }

            @Override
            public void onFailure(Throwable error) {
                Assert.assertNotNull("Error", error);
                Assert.assertEquals(UserRuntimeException.class, error.getClass());
            }
        }, unitSelectionCriteria);
    }
}
