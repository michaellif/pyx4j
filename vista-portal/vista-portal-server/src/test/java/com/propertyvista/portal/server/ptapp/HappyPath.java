/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 23, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp;

import org.junit.Assert;

import com.propertvista.generator.BusinessDataGenerator;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.security.rpc.AuthenticationResponse;

import com.propertyvista.domain.Application;
import com.propertyvista.domain.DemoData;
import com.propertyvista.portal.domain.ptapp.Tenant;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.domain.ptapp.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.ptapp.AccountCreationRequest;
import com.propertyvista.portal.rpc.ptapp.CurrentApplication;
import com.propertyvista.portal.server.TestUtil;
import com.propertyvista.portal.server.sync.ActivationServiceSync;
import com.propertyvista.portal.server.sync.ApartmentServiceSync;
import com.propertyvista.portal.server.sync.ApplicationServiceSync;
import com.propertyvista.portal.server.sync.TenantServiceSync;

/**
 * This class includes service utilities that are common between all of the tests. When
 * testing each section of a screen (a step), we need to rely on other steps to be
 * executed properly, this is the place for them
 * 
 * @author dmitry
 */
public class HappyPath {

    public static String step1createAccount() {
        String email = BusinessDataGenerator.createEmail();
        return step1createAccount(email);
    }

    public static String step1createAccount(final String email) {
        AccountCreationRequest request = EntityFactory.create(AccountCreationRequest.class);

        request.email().setValue(email);
        request.password().setValue("1234");
        request.captcha().setValue(TestUtil.createCaptcha());

        ActivationServiceSync service = new ActivationServiceSync();
        AuthenticationResponse response = service.createAccount(request);

        Assert.assertNotNull("Got the visit", response.getUserVisit());
        Assert.assertEquals("Email is correct", email, response.getUserVisit().getEmail());
        return email;
    }

    public static Application step2createApplication() {
        // start application process
        UnitSelectionCriteria unitSelectionCriteria = EntityFactory.create(UnitSelectionCriteria.class);
        unitSelectionCriteria.propertyCode().setValue(DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE);
        unitSelectionCriteria.floorplanName().setValue(DemoData.REGISTRATION_DEFAULT_FLOORPLAN);

        ApplicationServiceSync service = new ApplicationServiceSync();
        CurrentApplication currentApplication = service.getCurrentApplication(unitSelectionCriteria);
        return currentApplication.application;
    }

    public static UnitSelection step3loadUnitSelection() {
        // now let's load unit selection
        ApartmentServiceSync service = new ApartmentServiceSync();
        UnitSelection unitSelection = service.retrieve();

        Assert.assertNotNull("Unit selection", unitSelection);
        Assert.assertNotNull("Retrieved units", unitSelection.availableUnits().units());
        Assert.assertFalse("Found units", unitSelection.availableUnits().units().isEmpty());

        //        for (ApartmentUnit unit : unitSelection.availableUnits().units()) {
        //            log.debug("Found unit {}", unit);
        //        }
        //
        //        // select the first unit
        //        Assert.assertTrue("No unit selected at this point", unitSelection.selectedUnitId().isNull());
        //        unitSelection.selectedUnitId().setValue(unitSelection.availableUnits().units().get(0).id().getValue());
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
        //        log.debug("Successfully loaded unit {}", unitSelection.selectedUnitId());

        return unitSelection;
    }

    public static Tenant step4createTenants() {
        //        PotentialTenantList tenantList = generator.createPotentialTenantList(application);
        //        tenantList.tenants().get(0).email().setValue(email);

        // go through tenants
        //DataDump.dump("generated", tenantList);
        //        TenantService tenantService = TestServiceFactory.create(TenantService.class);
        //
        //        //We the same as UI does, Allow for server to make its creation actions
        //        tenantService.retrieve(new UnitTestsAsyncCallback<PotentialTenantList>() {
        //            @Override
        //            public void onSuccess(PotentialTenantList result) {
        //                Assert.assertEquals("We expect first Tenant prepopulated", 1, result.tenants().size());
        //                Assert.assertEquals("prepopulated email", tenantList.tenants().get(0).email(), result.tenants().get(0).email());
        //            }
        //        }, null);

        TenantServiceSync service = new TenantServiceSync();
        Tenant tenantList = service.retrieve();

        //        Assert.assertEquals("prepopulated email", tenantList.tenants().get(0).email(), result.tenants().get(0).email());
        return tenantList;
    }
}
