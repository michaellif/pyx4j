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
import com.propertyvista.portal.domain.pt.ApartmentUnit;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.AccountCreationRequest;
import com.propertyvista.portal.rpc.pt.CurrentApplication;
import com.propertyvista.portal.rpc.pt.services.ActivationService;
import com.propertyvista.portal.rpc.pt.services.ApartmentService;
import com.propertyvista.portal.rpc.pt.services.ApplicationService;
import com.propertyvista.portal.rpc.pt.services.TenantFinancialService;
import com.propertyvista.portal.rpc.pt.services.TenantInfoService;
import com.propertyvista.portal.rpc.pt.services.TenantService;
import com.propertyvista.portal.server.generator.VistaDataGenerator;
import com.propertyvista.portal.server.preloader.BusinessDataGenerator;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;
import com.pyx4j.unit.server.mock.TestLifecycle;

public class PortalServicesTest extends VistaDBTestCase {
    private final static Logger log = LoggerFactory.getLogger(PortalServicesTest.class);

    private Application application;

    private UnitSelection unitSelection;

    private PotentialTenantList tenantList;

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestLifecycle.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DemoData.MAX_CUSTOMERS = 5;
        new VistaDataPreloaders().preloadAll(false);
    }

    /**
     * Create application. Do full life cycle test from the beginning.
     */
    public void testFullLifecycle() {
        VistaDataGenerator generator = new VistaDataGenerator(500l);

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

        // now let's load unit selection
        ApartmentService apartmentService = TestServiceFactory.create(ApartmentService.class);
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
        log.info("Working with unit selection {}", unitSelection);

        for (ApartmentUnit unit : unitSelection.availableUnits().units()) {
            log.info("Found unit {}", unit);
        }

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

        subTestTenants(generator);
    }

    public void subTestTenants(VistaDataGenerator generator) {
        // go through tenants
        tenantList = generator.createPotentialTenantList(application);
        DataDump.dump("generated", tenantList);
        TenantService tenantService = TestServiceFactory.create(TenantService.class);
        tenantService.save(new UnitTestsAsyncCallback<PotentialTenantList>() {
            @Override
            public void onSuccess(PotentialTenantList result) {
                Assert.assertNotNull("Result", result);
                TestUtil.assertEqual("TenantList", tenantList, result);
                tenantList = result;
            }
        }, tenantList);

        // let's load the tenants to make sure that things are fine there
        tenantService.retrieve(new UnitTestsAsyncCallback<PotentialTenantList>() {
            @Override
            public void onSuccess(PotentialTenantList result) {
                Assert.assertNotNull("Result", result);
                TestUtil.assertEqual("TenantList", tenantList, result);
                tenantList = result;
            }
        }, null);

        subTestTenantInfo();
        subTestTenantFinancial(generator);
    }

    public void subTestTenantInfo() {
        TenantInfoService tenantInfoService = TestServiceFactory.create(TenantInfoService.class);
        for (final PotentialTenantInfo tenant : tenantList.tenants()) {
            log.info("Tenant {}", tenant);
            tenantInfoService.retrieve(new UnitTestsAsyncCallback<PotentialTenantInfo>() {
                @Override
                public void onSuccess(PotentialTenantInfo result) {
                    Assert.assertFalse("Result", result.isNull());
                    log.info("Retrieved {}", result);
                    TestUtil.assertEqual("TenantList", tenant, result);
                }
            }, tenant.id().getValue());
        }
    }

    public void subTestTenantFinancial(VistaDataGenerator generator) {
        //        generator.createTenantFinancials(tenantFinancials, tenants)

        // financials
        TenantFinancialService tenantFinancialService = TestServiceFactory.create(TenantFinancialService.class);
        for (final PotentialTenantInfo tenant : tenantList.tenants()) {
            tenantFinancialService.retrieve(new UnitTestsAsyncCallback<PotentialTenantFinancial>() {
                @Override
                public void onSuccess(PotentialTenantFinancial result) {
                    Assert.assertFalse("Result", result.isNull());
                    log.info("Retrieved {}", result);
                }
            }, tenant.id().getValue());
        }
    }
}
