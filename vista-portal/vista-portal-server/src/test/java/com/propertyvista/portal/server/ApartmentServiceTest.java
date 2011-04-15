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
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;
import com.propertyvista.portal.rpc.pt.CurrentApplication;
import com.propertyvista.portal.rpc.pt.services.ActivationService;
import com.propertyvista.portal.rpc.pt.services.ApartmentService;
import com.propertyvista.portal.rpc.pt.services.ApplicationService;
import com.propertyvista.portal.server.generator.VistaDataGenerator;
import com.propertyvista.portal.server.preloader.BusinessDataGenerator;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;
import com.pyx4j.unit.server.mock.TestLifecycle;

public class ApartmentServiceTest extends VistaDBTestCase {
    private final static Logger log = LoggerFactory.getLogger(ApartmentServiceTest.class);

    private UnitSelection unitSelection;

    private Application application;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DemoData.MAX_CUSTOMERS = 5;
        new VistaDataPreloaders().preloadAll(false);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestLifecycle.tearDown();
    }

    private void createAccountAndApplication() {
        final String email = BusinessDataGenerator.createEmail();

        // first, create the user
        AccountCreationRequest request = EntityFactory.create(AccountCreationRequest.class);
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

        UnitSelectionCriteria unitSelectionCriteria = EntityFactory.create(UnitSelectionCriteria.class);
        unitSelectionCriteria.propertyCode().setValue(DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE);
        unitSelectionCriteria.floorplanName().setValue(DemoData.REGISTRATION_DEFAULT_FLOORPLAN);

        ApplicationService applicationService = TestServiceFactory.create(ApplicationService.class);
        applicationService.getCurrentApplication(new UnitTestsAsyncCallback<CurrentApplication>() {
            @Override
            public void onSuccess(CurrentApplication result) {
                Assert.assertNotNull("Application", result.application);
                Assert.assertFalse("Application", result.application.isNull());
                log.info("Received {}", result);
                application = result.application; // we will need this for testing
            }
        }, unitSelectionCriteria);

        log.info("\n\n\n");
    }

    private ApartmentService createService() {
        return TestServiceFactory.create(ApartmentService.class);
    }

    public void testLoadAndSave() {
        createAccountAndApplication();

        // now let's load unit selection
        ApartmentService apartmentService = createService();
        apartmentService.retrieve(new UnitTestsAsyncCallback<UnitSelection>() {
            @Override
            public void onSuccess(UnitSelection result) {
                Assert.assertNotNull("Unit selection", result);
                unitSelection = result;
            }
        }, null);

        Assert.assertNotNull("Unit selection", unitSelection);
        Assert.assertNotNull("Retrieved units", unitSelection.availableUnits().units());
        Assert.assertFalse("Found units", unitSelection.availableUnits().units().isEmpty());

        // select the first unit
        Assert.assertTrue("No unit selected at this point", unitSelection.selectedUnitId().isNull());
        unitSelection.selectedUnitId().setValue(unitSelection.availableUnits().units().get(0).id().getValue());

        // save unit selection
        apartmentService.save(new UnitTestsAsyncCallback<UnitSelection>() {
            @Override
            public void onSuccess(UnitSelection result) {
                Assert.assertFalse("Selected unit", result.selectedUnitId().isNull());
                TestUtil.assertEqual("UnitSelection", unitSelection, result);
                unitSelection = result; // update local unit
            }
        }, unitSelection);

        Assert.assertFalse("Selected unit", unitSelection.selectedUnitId().isNull());
        log.info("Successfully loaded unit {}", unitSelection.selectedUnitId());
    }

    public void testLoadAndGenerate() {
        createAccountAndApplication();
        ApartmentService apartmentService = createService();

        // now let's load unit selection
        apartmentService.retrieve(new UnitTestsAsyncCallback<UnitSelection>() {
            @Override
            public void onSuccess(UnitSelection result) {
                Assert.assertFalse("Result", result.isNull());
                unitSelection = result; // update local unit
            }
        }, null);
        Assert.assertNotNull("PrimaryKey", unitSelection.id().getValue());

        // select the first unit
        VistaDataGenerator generator = new VistaDataGenerator(1l);
        UnitSelection generatedUnitSelection = generator.createUnitSelection(application, null);
        Assert.assertNull("Id not set yet", generatedUnitSelection.id().getValue());
        generatedUnitSelection.id().setValue(unitSelection.id().getValue()); // copy ids
        Assert.assertEquals("Ids", generatedUnitSelection.id().getValue(), unitSelection.id().getValue());
        Assert.assertNotNull("Id", generatedUnitSelection.id().getValue());
        //        generatedUnitSelection.setPrimaryKey(unitSelection.getPrimaryKey());
        unitSelection = generatedUnitSelection; // no longer need older unit selection

        // save unit selection
        apartmentService.save(new UnitTestsAsyncCallback<UnitSelection>() {
            @Override
            public void onSuccess(UnitSelection result) {
                Assert.assertFalse("Result", result.isNull());
                Assert.assertTrue("Selected unit", result.selectedUnitId().isNull());
                TestUtil.assertEqual("UnitSelection", unitSelection, result);
                unitSelection = result; // update local unit
            }
        }, unitSelection);

        Assert.assertNotNull("Retrieved units", unitSelection.availableUnits().units());
        Assert.assertFalse("Found units", unitSelection.availableUnits().units().isEmpty());

        // make sure that generated values are correct
        // now let's load unit selection
        apartmentService.retrieve(new UnitTestsAsyncCallback<UnitSelection>() {
            @Override
            public void onSuccess(UnitSelection result) {
                Assert.assertFalse("Result", result.isNull());

                // TODO VLAD, this did not work as well

                //                TestUtil.assertEqual("UnitSelection", unitSelection, result);
                unitSelection = result; // update local unit
            }
        }, null);
    }
}
