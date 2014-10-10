/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 8, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.server.config;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.unit.server.mock.MockHttpServletRequest;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.portal.rpc.shared.SiteWasNotActivatedUserRuntimeException;

public class VistaNamespaceResolverTest extends TestCase {

    private final static Logger log = LoggerFactory.getLogger(VistaNamespaceResolverTest.class);

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

        log.info("VistaNamespaceResolverTest initialized");
    }

    @Override
    @After
    public void tearDown() throws Exception {
        // Reset DB to normal
        VistaTestDBSetupForNamespace.resetDatabase();
    }

    @Test
    public final void testGetNamespace() {

        // TEST REQUESTS WITHOUT CONTEXTPATH IN URL

        testNamespaceNoContextPath("http://localhost:8888/", VistaNamespace.demoNamespace);

        testNamespaceNoContextPath("http://vista-crm.dev.pyx4j.com/crm.nocache.js", VistaNamespace.demoNamespace);

        testNamespaceNoContextPath("http://vista-site.dev.propertyvista.biz:8888/", VistaNamespace.demoNamespace);

        testNamespaceNoContextPath("http://onboarding.dev.birchwoodsoftwaregroup.com:8888/onboarding/", VistaNamespace.noNamespace);

        testNamespaceNoContextPath("http://operations.dev.devpv.com:80/operations/operations.nocache.js", VistaNamespace.operationsNamespace);

        testNamespaceNoContextPath("http://static.dev.birchwoodsoftwaregroup.com:8888/o/db-reset", VistaNamespace.noNamespace);

        // TEST REQUESTS WITHT CONTEXTPATH IN URL

        testNamespace("http://testnamespace-crm.dev.birchwoodsoftwaregroup.com:8888/vista/crm", "testnamespace", "/vista");

        testNamespace("http://testpmcs-crm.dev.birchwoodsoftwaregroup.com:8888/vista/crm", "testpmcs", "/vista");

        testNamespace("http://operations.dev.birchwoodsoftwaregroup.com:8888/vista/operations/operations.nocache.js", VistaNamespace.operationsNamespace,
                "/vista");

        // TEST INACTIVE PMC

        testNamespaceInactive("http://inactivepmc-crm.my-community.co/crm");

    }

    /**
     * Test namespace for inactive PMC
     *
     * @param urlReq
     */

    protected void testNamespaceInactive(String urlReq) {
        try {
            testNamespace("http://inactivepmc-crm.my-community.co/crm", "", null);
            fail("Namespace for this PMC should not be resolved");
        } catch (SiteWasNotActivatedUserRuntimeException e) {
            // Test ok
        }
    }

    /**
     * Test get namespace for url request without contextpath part
     *
     * @param urlReq
     *            requestURL
     * @param expectedNamespace
     *            expected namespace
     */
    protected void testNamespaceNoContextPath(String urlReq, String expectedNamespace) {
        testNamespace(urlReq, expectedNamespace, null);
    }

    /**
     * Test one namespace
     *
     * @param urlReq
     *            urlRequest to test if working with context (Ex. "/vista")
     * @param expectedNamespace
     *            expected namespace
     * @param contextPath
     *            context path for current url request
     */
    protected void testNamespace(String urlReq, String expectedNamespace, String contextPath) {
        req = new MockHttpServletRequest(urlReq);

        // If URL includes contextpath (Ex: "/vista"), set contextpath in mock http request object (Ex: req.setContextPath("vista"))
        if (contextPath != null) {
            req.setContextPath(contextPath);
        }

        String nsResult = VistaServerSideConfiguration.instance().getNamespaceResolver().getNamespace(req);

        Assert.assertTrue("Namespace Resolution error for request'" + urlReq + "'. Expected '" + expectedNamespace + "' and was '" + nsResult + "'",
                nsResult.equalsIgnoreCase(expectedNamespace));

    }

    private void createDefaultPMC(String namespace) {
        createPMC(namespace, PmcStatus.Active);
    }

    private void createInactivePMC(String namespace) {
        createPMC(namespace, PmcStatus.Activating);
    }

    private void createPMC(String namespace, PmcStatus status) {
        Pmc pmc = EntityFactory.create(Pmc.class);

        // Set NOT_NULL property values
        pmc.name().setValue("nameSpaceFor" + namespace);
        pmc.dnsName().setValue(namespace);
        pmc.namespace().setValue(namespace);
        pmc.status().setValue(status);

        Persistence.service().persist(pmc);
    }

}
