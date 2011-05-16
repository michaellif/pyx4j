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

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.config.tests.VistaDBTestCase;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.PotentialTenant;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.PotentialTenantList;
import com.propertyvista.portal.server.generator.VistaDataPTGenerator;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

import com.pyx4j.unit.server.UnitTestsAsyncCallback;
import com.pyx4j.unit.server.mock.TestLifecycle;

public class TenantServiceTest extends VistaDBTestCase {
    private final static Logger log = LoggerFactory.getLogger(TenantServiceTest.class);

    private String email;

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
        email = HappyPath.step1createAccount();
        application = HappyPath.step2createApplication();
        HappyPath.step3loadUnitSelection();
    }

    //    private ApartmentService createService() {
    //        return TestServiceFactory.create(ApartmentService.class);
    //    }

    public void testHappyPath() {
        happyPath();
        PotentialTenantList tenantList = HappyPath.step4createTenants();
        log.info(tenantList.toString());
        Assert.assertEquals("One tenant", 1, tenantList.tenants().size());

        PotentialTenantInfo tenant = tenantList.tenants().get(0);

        Assert.assertEquals("Applicant status", PotentialTenant.Status.Applicant, tenant.status().getValue());
        Assert.assertEquals("Email", email, tenant.email().getValue());
    }

    public void testLoadAndSave() {
        happyPath();

        VistaDataPTGenerator generator = new VistaDataPTGenerator(10l);

        PotentialTenantList tenantList = HappyPath.step4createTenants();
        PotentialTenantList tenantList2 = generator.createPotentialTenantList(application);

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
