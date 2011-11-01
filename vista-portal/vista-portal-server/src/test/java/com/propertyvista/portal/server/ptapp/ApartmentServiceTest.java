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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

public class ApartmentServiceTest extends VistaDBTestBase {
    private final static Logger log = LoggerFactory.getLogger(ApartmentServiceTest.class);

//    private UnitSelection unitSelection;

    private Application application;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        new VistaDataPreloaders(VistaDevPreloadConfig.createTest()).preloadAll(false);
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
//        happyPath();
//
//        // now let's load unit selection
//        ApartmentService apartmentService = createService();
//        apartmentService.retrieve(new UnitTestsAsyncCallback<UnitSelection>() {
//            @Override
//            public void onSuccess(UnitSelection result) {
//                Assert.assertNotNull("Unit selection", result);
//                unitSelection = result;
//            }
//        }, null);
//
//        Assert.assertNotNull("Unit selection", unitSelection);
//
//        // save unit selection
//        apartmentService.save(new UnitTestsAsyncCallback<UnitSelection>() {
//            @Override
//            public void onSuccess(UnitSelection result) {
//                Assert.assertFalse("Selected unit", result.selectedUnitId().isNull());
//                TestUtil.assertEqual("UnitSelection", unitSelection, result);
//                unitSelection = result; // update local unit
//            }
//        }, unitSelection);
//
//        Assert.assertFalse("Selected unit", unitSelection.selectedUnitId().isNull());
//        log.info("Successfully loaded unit {}", unitSelection.selectedUnitId());
    }

    public void testLoadAndGenerate() {
//        happyPath();
//        ApartmentService apartmentService = createService();
//
//        // now let's load unit selection
//        apartmentService.retrieve(new UnitTestsAsyncCallback<UnitSelection>() {
//            @Override
//            public void onSuccess(UnitSelection result) {
//                Assert.assertFalse("Result", result.isNull());
//                unitSelection = result; // update local unit
//            }
//        }, null);
//        Assert.assertNotNull("PrimaryKey", unitSelection.id().getValue());
//
//        // select the first unit
//        PTGenerator generator = new PTGenerator(1l, PreloadConfig.createTest());
//        UnitSelection generatedUnitSelection = null;//TODO generator.createUnitSelection(application, null);
//        Assert.assertNull("Id not set yet", generatedUnitSelection.id().getValue());
//        generatedUnitSelection.id().setValue(unitSelection.id().getValue()); // copy ids
//        Assert.assertEquals("Ids", generatedUnitSelection.id().getValue(), unitSelection.id().getValue());
//        Assert.assertNotNull("Id", generatedUnitSelection.id().getValue());
//        //        generatedUnitSelection.setPrimaryKey(unitSelection.getPrimaryKey());
//        unitSelection = generatedUnitSelection; // no longer need older unit selection
//
//        // save unit selection
//        apartmentService.save(new UnitTestsAsyncCallback<UnitSelection>() {
//            @Override
//            public void onSuccess(UnitSelection result) {
//                Assert.assertFalse("Result", result.isNull());
//                Assert.assertTrue("Selected unit", result.selectedUnitId().isNull());
//                TestUtil.assertEqual("UnitSelection", unitSelection, result);
//                unitSelection = result; // update local unit
//            }
//        }, unitSelection);
//
//        // make sure that generated values are correct
//        // now let's load unit selection
//        apartmentService.retrieve(new UnitTestsAsyncCallback<UnitSelection>() {
//            @Override
//            public void onSuccess(UnitSelection result) {
//                Assert.assertFalse("Result", result.isNull());
//                TestUtil.assertEqual("UnitSelection", unitSelection, result);
//                unitSelection = result; // update local unit
//            }
//        }, null);
    }
}
