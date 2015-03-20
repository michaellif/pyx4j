/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 14, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter.base;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.unit.server.mock.MockHttpServletRequest;
import com.pyx4j.unit.server.mock.MockHttpServletResponse;
import com.pyx4j.unit.server.mock.filter.MockFilterChain;
import com.pyx4j.unit.server.mock.filter.MockHttpServletRequestFilter;

import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.portal.rpc.shared.SiteWasNotActivatedUserRuntimeException;
import com.propertyvista.server.config.filter.VistaApplicationDispatcherFilter;
import com.propertyvista.server.config.filter.VistaTestDBSetupForNamespace;
import com.propertyvista.server.config.filter.util.PMCTestCreator;

public class VistaApplicationDispatcherFilterTestBase {

    protected final static Logger log = LoggerFactory.getLogger(VistaApplicationDispatcherFilterTestBase.class);

    protected VistaApplicationDispatcherFilter filterUnderTest;

    protected MockHttpServletRequest req;

    protected MockHttpServletResponse resp;

    protected MockFilterChain mockChain;

    @Before
    public void setUp() throws Exception {
        // Init HSQL DB
        VistaTestDBSetupForNamespace.init();
        PMCTestCreator.createPMC("vista", PmcStatus.Active).save();

        mockChain = new MockFilterChain();
        filterUnderTest = new VistaApplicationDispatcherFilter();
        resp = new MockHttpServletResponse();
    }

    @After
    public void tearDown() throws Exception {
        // Reset DB to normal
        VistaTestDBSetupForNamespace.resetDatabase();
    }

    protected void testRedirect(String url, String urlRedirection) throws IOException, ServletException {
        req = new MockHttpServletRequest(url);
        resp = new MockHttpServletResponse();
        mockChain.setExpectedInvocation(false);

        filterUnderTest.map(req, resp, mockChain);

        Assert.assertTrue("Expected redirection from '" + url + "' to " + urlRedirection + "'", resp.getRedirectUrl().equalsIgnoreCase(urlRedirection));
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
        try {
            filterUnderTest.map(req, resp, mockChain);
        } catch (SiteWasNotActivatedUserRuntimeException e) {
            // If followChain, SiteWasNotActivatedUserRuntimeException is expected
            if (!followChain) {
                throw e;
            }
        }

        mockChain.verify();

        if ((!followChain) && (app != null)) {
            String targetUrl = "/" + app;
            if (app == VistaApplication.noApp) {
                targetUrl = req.getRequestURI();
            }
            Assert.assertTrue("Wrong forwarded URL for application '" + app + "'. Forward URL is '" + ((MockHttpServletRequestFilter) req).getForwardUrl()
                    + "' and expected URL is '" + targetUrl + "'", ((MockHttpServletRequestFilter) req).getForwardUrl().startsWith(targetUrl));
        }
    }

    protected String encloseSlash(String url) {
        return "/" + url + "/";
    }

}
