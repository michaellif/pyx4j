/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.prospect.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.unit.server.mock.TestLifecycle;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.security.PortalProspectBehavior;

public class ProspectAccessControlListTest {

    @BeforeClass
    public static void init() throws Exception {
        VistaTestDBSetup.init();
    }

    @After
    public void tearDown() {
        TestLifecycle.tearDown();
    }

    @Test
    public void testProspectEntityPermissions() {
        TestLifecycle.testSession(null, PortalProspectBehavior.Prospect);
        TestLifecycle.beginRequest();

        Set<Class<?>> noAccessRules = new HashSet<>();
        noAccessRules.add(Country.class);
        noAccessRules.add(City.class);
        noAccessRules.add(Province.class);

        for (Class<? extends IEntity> entityClass : ServerEntityFactory.getAllEntityClasses()) {
            if (noAccessRules.contains(entityClass)) {
                continue;
            }
            IEntity ent = EntityFactory.create(entityClass);
            if (SecurityController.checkPermission(EntityPermission.permissionRead(ent))) {
                @SuppressWarnings("rawtypes")
                List<DatasetAccessRule> rules = SecurityController.getAccessRules(DatasetAccessRule.class, entityClass);
                if ((rules == null) || rules.size() == 0) {
                    System.err.println(entityClass);
                    Assert.fail("Access to " + entityClass + " should have DatasetAccessRule");
                }
            }
        }
        TestLifecycle.endRequest();
    }
}
