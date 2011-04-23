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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.security.rpc.AuthenticationResponse;
import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;
import com.pyx4j.unit.server.mock.TestLifecycle;

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

        final String email = BusinessDataGenerator.createEmail();
        subTestActivation(email);
        subTestApplication();
        subTestApartment();
        subTestTenants(generator, email);
    }

    public void subTestActivation(final String email) {
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
    }

    public void subTestApplication() {
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
    }

    public void subTestApartment() {
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
        log.debug("Working with unit selection {}", unitSelection);

        for (ApartmentUnit unit : unitSelection.availableUnits().units()) {
            log.debug("Found unit {}", unit);
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
        log.debug("Successfully loaded unit {}", unitSelection.selectedUnitId());
    }

    public void subTestTenants(VistaDataGenerator generator, String email) {
        tenantList = generator.createPotentialTenantList(application);
        tenantList.tenants().get(0).email().setValue(email);

        // go through tenants
        DataDump.dump("generated", tenantList);
        TenantService tenantService = TestServiceFactory.create(TenantService.class);

        //We the same as UI does, Allow for server to make its creation actions
        tenantService.retrieve(new UnitTestsAsyncCallback<PotentialTenantList>() {
            @Override
            public void onSuccess(PotentialTenantList result) {
                Assert.assertEquals("We expect first Tenant prepopulated", 1, result.tenants().size());
                Assert.assertEquals("prepopulated email", tenantList.tenants().get(0).email(), result.tenants().get(0).email());
            }
        }, null);

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
            log.debug("Tenant {}", tenant);
            tenantInfoService.retrieve(new UnitTestsAsyncCallback<PotentialTenantInfo>() {
                @Override
                public void onSuccess(PotentialTenantInfo result) {
                    Assert.assertFalse("Result", result.isNull());
                    log.debug("Retrieved {}", result);
                    TestUtil.assertEqual("TenantList", tenant, result);
                }
            }, tenant.id().getValue());
        }
    }

    public void subTestTenantFinancial(VistaDataGenerator generator) {
        TenantFinancialService tenantFinancialService = TestServiceFactory.create(TenantFinancialService.class);
        for (final PotentialTenantInfo tenant : tenantList.tenants()) {

            tenantFinancialService.retrieve(new UnitTestsAsyncCallback<PotentialTenantFinancial>() {
                @Override
                public void onSuccess(PotentialTenantFinancial result) {
                    Assert.assertEquals("prepopulated email", tenant.getPrimaryKey(), result.getPrimaryKey());
                    // ignore created tenant since it is the same as we expect
                }
            }, tenant.getPrimaryKey());

            final PotentialTenantFinancial tenantFinancial = generator.createFinancialInfo(tenant);
            tenantFinancialService.save(new UnitTestsAsyncCallback<PotentialTenantFinancial>() {
                @Override
                public void onSuccess(PotentialTenantFinancial result) {
                    Assert.assertFalse("Result", result.isNull());
                    log.debug("Saved {}", result);
                }
            }, tenantFinancial);

            //            tenantFinancialService.retrieve(new UnitTestsAsyncCallback<PotentialTenantFinancial>() {
            //                @Override
            //                public void onSuccess(PotentialTenantFinancial result) {
            //                    Assert.assertFalse("Result", result.isNull());
            //                    log.info("Retrieved {}", result);
            //                }
            //            }, tenant.id().getValue());
        }
    }
}
