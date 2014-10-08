/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pyx4j.unit.server.mock.MockHttpServletResponse;
import com.pyx4j.unit.server.mock.filter.MockFilterChain;
import com.pyx4j.unit.server.mock.filter.MockHttpServletRequestFilter;

import com.propertyvista.server.config.VistaApplicationDispatcherFilter.ApplicationType;

public class VistaApplicationDispatcherFilterTest {

    VistaApplicationDispatcherFilter filterUnderTest;

    MockHttpServletRequestFilter req;

    MockHttpServletResponse resp;

    MockFilterChain mockChain;

    @Before
    public void setUp() throws Exception {
        mockChain = new MockFilterChain();
        filterUnderTest = new VistaApplicationDispatcherFilter();
        resp = new MockHttpServletResponse();
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test well format URL. Should do forward in all cases.
     *
     * @throws IOException
     * @throws ServletException
     */
    @Test
    public final void testDoMaps() throws IOException, ServletException {

        // **************************************************************
        //                      LOCAL ENVIRONMENTS
        //
        // URL Formats:
        // http://APP.dev.birchwoodsoftwaregroup.com:8888
        // http://PMC-APP.dev.birchwoodsoftwaregroup.com:8888
        // **************************************************************

        // Onboarding
        testMapping("http://onboarding.dev.birchwoodsoftwaregroup.com:8888/", false, ApplicationType.onboarding);

        // Operations
        testMapping("http://operations.dev.birchwoodsoftwaregroup.com:8888/", false, ApplicationType.operations);

        // DB Reset
        //testMapping("http://static.dev.birchwoodsoftwaregroup.com:8888/o/db-reset", false, null);

        // SITE
        testMapping("http://vista-site.dev.birchwoodsoftwaregroup.com:8888/", false, ApplicationType.site);

        // CRM
        testMapping("http://vista-crm.dev.birchwoodsoftwaregroup.com:8888/", false, ApplicationType.crm);

        // Resident
        testMapping("http://vista-portal.dev.birchwoodsoftwaregroup.com:8888/", false, ApplicationType.resident);

        // Prospect
        testMapping("http://vista-portal.dev.birchwoodsoftwaregroup.com:8888/prospect", false, ApplicationType.prospect);

        // **************************************************************
        //                       TEST ENVIRONMENTS
        //
        // URL Formats:
        // http://APP-nn.dev.birchwoodsoftwaregroup.com:8888
        // http://PMC-APP-nn.birchwoodsoftwaregroup.com:8888
        // **************************************************************

        // Onboarding
        testMapping("https://onboarding-22.birchwoodsoftwaregroup.com/", false, ApplicationType.onboarding);

        // Operations
        testMapping("https://operations-22.birchwoodsoftwaregroup.com/", false, ApplicationType.operations);

        // DB-Reset
        //testMapping("http://static-22.birchwoodsoftwaregroup.com/o/db-reset", false, VistaApplication.);

        // SITE
        testMapping("https://vista-site-22.birchwoodsoftwaregroup.com/", false, ApplicationType.site);

        // CRM
        testMapping("https://vista-crm-22.birchwoodsoftwaregroup.com/", false, ApplicationType.crm);

        // Resident
        testMapping("https://vista-portal-22.birchwoodsoftwaregroup.com/", false, ApplicationType.resident);

        // Prospect
        testMapping("https://vista-portal-22.birchwoodsoftwaregroup.com/prospect", false, ApplicationType.prospect);

    }

    /**
     * Test wrong addresses. Should do chain in all cases.
     *
     * @throws IOException
     * @throws ServletException
     */

    @Test
    public final void testFollowChain() throws IOException, ServletException {

        testMapping("https://portale-22.birchwoodsoftwaregroup.com/prospect", true, null);

        testMapping("https://portal-vista-22.birchwoodsoftwaregroup.com/", true, null);

        testMapping("https://site-vista-999.birchwoodsoftwaregroup.com/", true, null);

        testMapping("http://onboardingg.dev.birchwoodsoftwaregroup.com:8888/", true, null);

    }

    /**
     * Test mapping function. For one request checks if chain follows or if request is redirected. In the latter case, checks the forwarded url with Application
     * name
     *
     * @param url
     *            to map
     * @param followChain
     *            if you want the chain to continue; false if you expect the request to be forwarded
     * @param app
     *            application
     * @throws IOException
     * @throws ServletException
     */
    protected void testMapping(String url, boolean followChain, ApplicationType app) throws IOException, ServletException {
        req = new MockHttpServletRequestFilter(url);
        mockChain.setExpectedInvocation(followChain);
        filterUnderTest.map(req, resp, mockChain);
        mockChain.verify();

        if ((!followChain) && (app != null)) {
            Assert.assertTrue("Wrong forwarded URL for application '" + app + "'", req.getForwardUrl().startsWith("/" + app));
        }
    }

}
