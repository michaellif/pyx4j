/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.unit.server.mock.MockHttpServletRequest;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;

public class VistaNamespaceResolverTestBase extends TestCase {

    protected MockHttpServletRequest req;

    @Override
    @Before
    public void setUp() throws Exception {
        // Init HSQL DB
        VistaTestDBSetupForNamespace.init();

        // Create testing PMCs
        createDefaultPMC("vista");
        createDefaultPMC("testnamespace");
        createDefaultPMC("testpmcs");
        createInactivePMC("inactivepmc");

    }

    @Override
    @After
    public void tearDown() throws Exception {
        // Reset DB to normal
        VistaTestDBSetupForNamespace.resetDatabase();
    }

    private void createDefaultPMC(String namespace) {
        createPMC(namespace, PmcStatus.Active);
    }

    private void createInactivePMC(String namespace) {
        createPMC(namespace, PmcStatus.Activating);
    }

    public static void createPMC(String namespace, PmcStatus status) {
        Pmc pmc = EntityFactory.create(Pmc.class);

        // Set NOT_NULL property values
        pmc.name().setValue("nameSpaceFor" + namespace);
        pmc.dnsName().setValue(namespace);
        pmc.namespace().setValue(namespace);
        pmc.status().setValue(status);

        Persistence.service().persist(pmc);
    }
}
