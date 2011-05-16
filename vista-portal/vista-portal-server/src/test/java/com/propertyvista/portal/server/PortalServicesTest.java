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

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.config.tests.VistaDBTestCase;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.PotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.PotentialTenantList;
import com.propertyvista.portal.rpc.ptapp.services.TenantFinancialService;
import com.propertyvista.portal.rpc.ptapp.services.TenantInfoService;
import com.propertyvista.portal.rpc.ptapp.services.TenantService;
import com.propertyvista.portal.server.generator.VistaDataPTGenerator;
import com.propertyvista.portal.server.preloader.BusinessDataGenerator;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

import com.pyx4j.unit.server.TestServiceFactory;
import com.pyx4j.unit.server.UnitTestsAsyncCallback;
import com.pyx4j.unit.server.mock.TestLifecycle;

public class PortalServicesTest extends VistaDBTestCase {

    private final static Logger log = LoggerFactory.getLogger(PortalServicesTest.class);

    private Application application;

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
        VistaDataPTGenerator generator = new VistaDataPTGenerator(500l);

        final String email = BusinessDataGenerator.createEmail();
        HappyPath.step1createAccount(email);
        application = HappyPath.step2createApplication();
        HappyPath.step3loadUnitSelection();
        subTestTenants(generator, email);
    }

    public void subTestTenants(VistaDataPTGenerator generator, String email) {
        tenantList = generator.createPotentialTenantList(application);
        tenantList.tenants().get(0).email().setValue(email);

        // go through tenants
        //DataDump.dump("generated", tenantList);
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

    public void subTestTenantFinancial(VistaDataPTGenerator generator) {
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
