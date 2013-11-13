/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.security.prod;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.mail.Mail;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaTestsDBConfigurationHSQLMemory;
import com.propertyvista.crm.rpc.services.organization.EmployeeCrudService;
import com.propertyvista.operations.rpc.services.PmcCrudService;
import com.propertyvista.portal.rpc.portal.web.services.profile.ResidentProfileCrudService;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationStatusService;
import com.propertyvista.server.config.VistaServerSideConfigurationProd;

public class VistaProdAccessControlListTest {

    private final static Logger log = LoggerFactory.getLogger(VistaProdAccessControlListTest.class);

    @BeforeClass
    public static void init() throws Exception {
        ServerSideConfiguration.setInstance(new VistaServerSideConfigurationProd() {
            @Override
            public IPersistenceConfiguration getPersistenceConfiguration() {
                return new VistaTestsDBConfigurationHSQLMemory();
            }
        });
        Mail.getMailService().setDisabled(true);
    }

    @After
    public void tearDown() {
        TestLifecycle.tearDown();
    }

    void assertPermission(boolean expected, Class<? extends IService> targetServiceInterface) {
        Assert.assertEquals("Allow " + targetServiceInterface.getSimpleName(), expected,
                SecurityController.checkPermission(new IServiceExecutePermission(targetServiceInterface)));
    }

    @Test
    public void publicServicePermissions() {
        TestLifecycle.beginRequest();
        assertPermission(false, ApplicationStatusService.class);

        // Admin
        assertPermission(false, PmcCrudService.class);

        //Crm
        assertPermission(false, EmployeeCrudService.class);

        // Portal
        assertPermission(false, ResidentProfileCrudService.class);

        TestLifecycle.endRequest();
    }
}
