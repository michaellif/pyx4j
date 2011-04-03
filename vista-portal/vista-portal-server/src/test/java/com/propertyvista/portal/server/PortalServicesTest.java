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
package com.propertyvista.portal.server;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.config.tests.VistaDBTestCase;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;
import com.propertyvista.portal.rpc.pt.CurrentApplication;
import com.propertyvista.portal.rpc.pt.services.ActivationService;
import com.propertyvista.portal.rpc.pt.services.ApplicationService;
import com.propertyvista.portal.server.preloader.BusinessDataGenerator;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;
import com.pyx4j.unit.server.mock.TestLifecycle;

public class PortalServicesTest extends VistaDBTestCase {
    private final static Logger log = LoggerFactory.getLogger(PortalServicesTest.class);

    private Application application;

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestLifecycle.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DemoData.MAX_CUSTOMERS = 5;
        new VistaDataPreloaders().preloadAll();
    }

    /**
     * Test invalid email address
     */
    public void testFullLifecycle() {
        // first, create the user
        AccountCreationRequest request = EntityFactory.create(AccountCreationRequest.class);
        final String email = BusinessDataGenerator.createEmail();
        request.email().setValue(email);
        request.password().setValue("1234");
        request.captcha().setValue(TestUtil.createCaptcha());

        ActivationService activationService = TestServiceFactory.create(ActivationService.class);
        activationService.createAccount(new UnitTestsAsyncCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse result) {
                Assert.assertNotNull("Got the visit", result.getUserVisit());
                Assert.assertEquals("Email is correct", email, result.getUserVisit().getEmail());
            }
        }, request);

        // start application process
        UnitSelectionCriteria unitSelectionCriteria = EntityFactory.create(UnitSelectionCriteria.class);
        unitSelectionCriteria.propertyCode().setValue(DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE);
        unitSelectionCriteria.floorplanName().setValue(DemoData.REGISTRATION_DEFAULT_FLOORPLAN);

        ApplicationService applicationService = TestServiceFactory.create(ApplicationService.class);
        applicationService.getCurrentApplication(new UnitTestsAsyncCallback<CurrentApplication>() {
            @Override
            public void onSuccess(CurrentApplication result) {
                Assert.assertNotNull("Application", result.application);
                application = result.application;
            }
        }, unitSelectionCriteria);

        Assert.assertNotNull(application);

//        // apartment service
//        UnitSelection unitSelection = EntityFactory.create(UnitSelection.class);
//        unitSelection.application().set(application);
//        unitSelection.
//        
//        ApartmentServices apartmentService = TestServiceFactory.create(ApartmentServices.class);
//        apartmentService.save(new UnitTestsAsyncCallback<AuthenticationResponse>() {
//            @Override
//            public void onSuccess(AuthenticationResponse result) {
//                Assert.assertNotNull("Got the visit", result.getUserVisit());
//                Assert.assertEquals("Email is correct", email, result.getUserVisit().getEmail());
//            }
//        }, unitSelection);

    }
}
