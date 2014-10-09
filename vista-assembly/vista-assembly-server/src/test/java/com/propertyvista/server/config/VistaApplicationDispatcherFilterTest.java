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
        testForward("http://onboarding.dev.birchwoodsoftwaregroup.com:8888/", VistaApplication.onboarding);

        // Operations
        testForward("http://operations.dev.birchwoodsoftwaregroup.com:8888/", VistaApplication.operations);

        // DB Reset
        testForward("http://static.dev.birchwoodsoftwaregroup.com:8888/o/db-reset", VistaApplication.noApp);

        // SITE
        testForward("http://vista-site.dev.birchwoodsoftwaregroup.com:8888/", VistaApplication.site);

        // CRM
        testForward("http://vista-crm.dev.birchwoodsoftwaregroup.com:8888/", VistaApplication.crm);

        // Resident
        testForward("http://vista-portal.dev.birchwoodsoftwaregroup.com:8888/", VistaApplication.resident);

        // Prospect
        testForward("http://vista-portal.dev.birchwoodsoftwaregroup.com:8888/prospect", VistaApplication.prospect);

        // **************************************************************
        //                       TEST ENVIRONMENTS
        //
        // URL Formats:
        // http://APP-nn.dev.birchwoodsoftwaregroup.com:8888
        // http://PMC-APP-nn.birchwoodsoftwaregroup.com:8888
        // **************************************************************

        // Onboarding
        testForward("https://onboarding-22.birchwoodsoftwaregroup.com/", VistaApplication.onboarding);

        // Operations
        testForward("https://operations-22.birchwoodsoftwaregroup.com/", VistaApplication.operations);

        // DB-Reset
        testForward("http://static-22.birchwoodsoftwaregroup.com/o/db-reset", VistaApplication.noApp);

        // Logs
        testForward("https://static-22.birchwoodsoftwaregroup.com/logs/", VistaApplication.noApp);

        // SITE
        testForward("https://vista-site-22.birchwoodsoftwaregroup.com/", VistaApplication.site);

        // CRM
        testForward("https://vista-crm-22.birchwoodsoftwaregroup.com/", VistaApplication.crm);
        testMapping("http://vista-crm-00.devpv.com/", false, VistaApplication.crm);

        // Resident
        testForward("https://vista-portal-22.birchwoodsoftwaregroup.com/", VistaApplication.resident);

        // Prospect
        testForward("https://vista-portal-22.birchwoodsoftwaregroup.com/prospect", VistaApplication.prospect);

    }

    /**
     * Test wrong addresses. Should do chain in all cases.
     *
     * @throws IOException
     * @throws ServletException
     */

    @Test
    public final void testFollowChain() throws IOException, ServletException {

        testChain("https://portale-22.birchwoodsoftwaregroup.com/prospect");

        testChain("https://portal-vista-22.birchwoodsoftwaregroup.com/");

        testChain("https://site-vista-999.birchwoodsoftwaregroup.com/");

        testChain("http://onboardingg.dev.birchwoodsoftwaregroup.com:8888/");

    }

    /**
     * Test forward function
     *
     * @param url
     *            to map
     * @param app
     *            application
     * @throws IOException
     * @throws ServletException
     */
    protected void testForward(String url, VistaApplication app) throws IOException, ServletException {
        testMapping(url, false, app);
    }

    /**
     * Test doChain function
     *
     * @param url
     *            to map
     * @param app
     *            application
     * @throws IOException
     * @throws ServletException
     */
    protected void testChain(String url) throws IOException, ServletException {
        testMapping(url, true, null);
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
