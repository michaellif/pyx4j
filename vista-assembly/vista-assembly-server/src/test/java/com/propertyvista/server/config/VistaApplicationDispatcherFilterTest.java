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

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.unit.server.mock.MockHttpServletResponse;
import com.pyx4j.unit.server.mock.filter.MockFilterChain;
import com.pyx4j.unit.server.mock.filter.MockHttpServletRequestFilter;

import com.propertyvista.domain.security.common.VistaApplication;

public class VistaApplicationDispatcherFilterTest extends TestCase {

    private final static Logger log = LoggerFactory.getLogger(VistaApplicationDispatcherFilterTest.class);

    VistaApplicationDispatcherFilter filterUnderTest;

    MockHttpServletRequestFilter req;

    MockHttpServletResponse resp;

    MockFilterChain mockChain;

    @Override
    @Before
    public void setUp() throws Exception {
        mockChain = new MockFilterChain();
        filterUnderTest = new VistaApplicationDispatcherFilter();
        resp = new MockHttpServletResponse();
        log.info("VistaApplicationDispatcherFilterTest initialized");
    }

    @Override
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
        testMapping("http://onboarding.dev.birchwoodsoftwaregroup.com:8888/", false, VistaApplication.onboarding);

        // Operations
        testMapping("http://operations.dev.birchwoodsoftwaregroup.com:8888/", false, VistaApplication.operations);

        // DB Reset
        testMapping("http://static.dev.birchwoodsoftwaregroup.com:8888/o/db-reset", false, VistaApplication.noApp);

        // SITE
        testMapping("http://vista-site.dev.birchwoodsoftwaregroup.com:8888/", false, VistaApplication.site);

        // CRM
        testMapping("http://vista-crm.dev.birchwoodsoftwaregroup.com:8888/", false, VistaApplication.crm);

        // Resident
        testMapping("http://vista-portal.dev.birchwoodsoftwaregroup.com:8888/", false, VistaApplication.resident);

        // Prospect
        testMapping("http://vista-portal.dev.birchwoodsoftwaregroup.com:8888/prospect", false, VistaApplication.prospect);

        // **************************************************************
        //                       TEST ENVIRONMENTS
        //
        // URL Formats:
        // http://APP-nn.dev.birchwoodsoftwaregroup.com:8888
        // http://PMC-APP-nn.birchwoodsoftwaregroup.com:8888
        // **************************************************************

        // Onboarding
        testMapping("https://onboarding-22.birchwoodsoftwaregroup.com/", false, VistaApplication.onboarding);

        // Operations
        testMapping("https://operations-22.birchwoodsoftwaregroup.com/", false, VistaApplication.operations);

        // DB-Reset
        testMapping("http://static-22.birchwoodsoftwaregroup.com/o/db-reset", false, VistaApplication.noApp);

        // Logs
        testMapping("https://static-22.birchwoodsoftwaregroup.com/logs/", false, VistaApplication.noApp);

        // SITE
        testMapping("https://vista-site-22.birchwoodsoftwaregroup.com/", false, VistaApplication.site);

        // CRM
        testMapping("https://vista-crm-22.birchwoodsoftwaregroup.com/", false, VistaApplication.crm);
        testMapping("http://vista-crm-00.devpv.com/", false, VistaApplication.crm);

        // Resident
        testMapping("https://vista-portal-22.birchwoodsoftwaregroup.com/", false, VistaApplication.resident);

        // Prospect
        testMapping("https://vista-portal-22.birchwoodsoftwaregroup.com/prospect", false, VistaApplication.prospect);

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
    protected void testMapping(String url, boolean followChain, VistaApplication app) throws IOException, ServletException {
        req = new MockHttpServletRequestFilter(url);
        mockChain.setExpectedInvocation(followChain);
        filterUnderTest.map(req, resp, mockChain);
        mockChain.verify();

        if ((!followChain) && (app != null)) {
            String targetUrl = "/" + app;
            if (app == VistaApplication.noApp) {
                targetUrl = req.getRequestURI();
            }
            Assert.assertTrue("Wrong forwarded URL for application '" + app + "'. Forward URL is '" + req.getForwardUrl() + "' and expected URL is '"
                    + targetUrl + "'", req.getForwardUrl().startsWith(targetUrl));
        }
    }

}
