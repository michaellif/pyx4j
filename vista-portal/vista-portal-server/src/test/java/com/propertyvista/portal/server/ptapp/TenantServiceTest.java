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

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.PTGenerator;

import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseListDTO;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

public class TenantServiceTest extends VistaDBTestBase {
    private final static Logger log = LoggerFactory.getLogger(TenantServiceTest.class);

    private String email;

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
        email = HappyPath.step1createAccount();
        application = HappyPath.step2createApplication();
        HappyPath.step3loadUnitSelection();
    }

    //    private ApartmentService createService() {
    //        return TestServiceFactory.create(ApartmentService.class);
    //    }

    public void testHappyPath() {
        happyPath();
        TenantInLeaseListDTO tenantList = HappyPath.step4createTenants();
        log.info(tenantList.toString());
        Assert.assertEquals("One tenant", 1, tenantList.tenants().size());

        TenantInLease tenant = tenantList.tenants().get(0);

        Assert.assertEquals("Applicant status", TenantInLease.Status.Applicant, tenant.status().getValue());
        Assert.assertEquals("Email", email, tenant.tenant().person().email().getValue());
    }

    public void testLoadAndSave() {
        happyPath();

        PTGenerator generator = new PTGenerator(10l, VistaDevPreloadConfig.createTest());

        TenantInLeaseListDTO tenantList = HappyPath.step4createTenants();
        // PotentialTenantList tenantList2 = generator.createPotentialTenantList(application);

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
        //                TestUtil.assertEqual("TenantList", tenantList, result);
        //                tenantList = result;
        //            }
        //        }, null);

    }
}
