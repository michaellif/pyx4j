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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.config.tests.VistaDBTestCase;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;
import com.propertyvista.portal.server.TestUtil;
import com.propertyvista.portal.server.generator.PTGenerator;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

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

    private void happyPath() {
        HappyPath.step1createAccount();
        application = HappyPath.step2createApplication();
    }

    private ApartmentService createService() {
        return TestServiceFactory.create(ApartmentService.class);
    }

    public void testHappyPath() {
        happyPath();
        HappyPath.step3loadUnitSelection();
    }

    public void testLoadAndSave() {
        happyPath();

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
        happyPath();
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
        PTGenerator generator = new PTGenerator(1l);
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
                log.debug("Two dates are {} {}", unitSelection.selectionCriteria().availableFrom().getValue().getTime(), result.selectionCriteria()
                        .availableFrom().getValue().getTime());
                TestUtil.assertEqual("UnitSelection", unitSelection, result);
                unitSelection = result; // update local unit
            }
        }, null);
    }
}
