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

import com.propertvista.generator.BusinessDataGenerator;
import com.propertvista.generator.PTGenerator;

import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.Application;
import com.propertyvista.domain.PreloadConfig;
import com.propertyvista.portal.domain.ptapp.Tenant;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

public class PortalServicesTest extends VistaDBTestBase {

    private final static Logger log = LoggerFactory.getLogger(PortalServicesTest.class);

    private Application application;

    private Tenant tenantList;

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        TestLifecycle.tearDown();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        new VistaDataPreloaders(PreloadConfig.createTest()).preloadAll(false);
    }

    /**
     * Create application. Do full life cycle test from the beginning.
     */
    public void testFullLifecycle() {
        PTGenerator generator = new PTGenerator(500l, PreloadConfig.createTest());

        final String email = BusinessDataGenerator.createEmail();
        HappyPath.step1createAccount(email);
        application = HappyPath.step2createApplication();
        HappyPath.step3loadUnitSelection();
        subTestTenants(generator, email);
    }

    public void subTestTenants(PTGenerator generator, String email) {
//        tenantList = generator.createPotentialTenantList(application);
//        tenantList.tenants().get(0).person().email().setValue(email);
//
//        // go through tenants
//        //DataDump.dump("generated", tenantList);
//        TenantService tenantService = TestServiceFactory.create(TenantService.class);
//
//        //We the same as UI does, Allow for server to make its creation actions
//        tenantService.retrieve(new UnitTestsAsyncCallback<PotentialTenantList>() {
//            @Override
//            public void onSuccess(PotentialTenantList result) {
//                Assert.assertEquals("We expect first Tenant prepopulated", 1, result.tenants().size());
//                Assert.assertEquals("prepopulated email", tenantList.tenants().get(0).person().email(), result.tenants().get(0).person().email());
//            }
//        }, null);
//
//        tenantService.save(new UnitTestsAsyncCallback<PotentialTenantList>() {
//            @Override
//            public void onSuccess(PotentialTenantList result) {
//                Assert.assertNotNull("Result", result);
//                TestUtil.assertEqual("TenantList", tenantList, result);
//                tenantList = result;
//            }
//        }, tenantList);
//
//        // let's load the tenants to make sure that things are fine there
//        tenantService.retrieve(new UnitTestsAsyncCallback<PotentialTenantList>() {
//            @Override
//            public void onSuccess(PotentialTenantList result) {
//                Assert.assertNotNull("Result", result);
////                log.info(tenantList.)
//                TestUtil.assertEqual("TenantList", tenantList, result);
//                tenantList = result;
//            }
//        }, null);
//
//        subTestTenantInfo();
//
//        subTestTenantFinancial(generator);
    }

    public void subTestTenantInfo() {
//        TenantInfoService tenantInfoService = TestServiceFactory.create(TenantInfoService.class);
//        for (final PotentialTenantInfo tenant : tenantList.tenants()) {
//            log.debug("Tenant {}", tenant);
//            tenantInfoService.retrieve(new UnitTestsAsyncCallback<PotentialTenantInfo>() {
//                @Override
//                public void onSuccess(PotentialTenantInfo result) {
//                    Assert.assertFalse("Result", result.isNull());
//                    log.debug("Retrieved {}", result);
//                    TestUtil.assertEqual("TenantList", tenant, result);
//                }
//            }, tenant.id().getValue());
//        }
    }

//    public void subTestTenantFinancial(PTGenerator generator) {
//        TenantFinancialService tenantFinancialService = TestServiceFactory.create(TenantFinancialService.class);
//        for (final PotentialTenantInfo tenant : tenantList.tenants()) {
//
//            tenantFinancialService.retrieve(new UnitTestsAsyncCallback<PotentialTenantFinancial>() {
//                @Override
//                public void onSuccess(PotentialTenantFinancial result) {
//                    Assert.assertEquals("prepopulated email", tenant.getPrimaryKey(), result.getPrimaryKey());
//                    // ignore created tenant since it is the same as we expect
//                }
//            }, tenant.getPrimaryKey());
//
//            final PotentialTenantFinancial tenantFinancial = generator.createFinancialInfo(tenant);
//            tenantFinancialService.save(new UnitTestsAsyncCallback<PotentialTenantFinancial>() {
//                @Override
//                public void onSuccess(PotentialTenantFinancial result) {
//                    Assert.assertFalse("Result", result.isNull());
//                    log.debug("Saved {}", result);
//                }
//            }, tenantFinancial);
//
//            //            tenantFinancialService.retrieve(new UnitTestsAsyncCallback<PotentialTenantFinancial>() {
//            //                @Override
//            //                public void onSuccess(PotentialTenantFinancial result) {
//            //                    Assert.assertFalse("Result", result.isNull());
//            //                    log.info("Retrieved {}", result);
//            //                }
//            //            }, tenant.id().getValue());
//        }
//    }
}
