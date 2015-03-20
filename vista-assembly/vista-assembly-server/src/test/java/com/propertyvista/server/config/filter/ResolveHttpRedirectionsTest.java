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
package com.propertyvista.server.config.filter;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.unit.server.mock.MockHttpServletRequest;

import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.config.appcontext.DevResolver;
import com.propertyvista.server.config.appcontext.EnvNResolver;
import com.propertyvista.server.config.appcontext.ProdCustomersDemoResolver;
import com.propertyvista.server.config.appcontext.ProdResolver;
import com.propertyvista.server.config.filter.base.VistaNamespaceResolverTestBase;
import com.propertyvista.server.config.filter.namespace.VistaApplicationResolverHelper;

public class ResolveHttpRedirectionsTest extends VistaNamespaceResolverTestBase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        log.info("VistaApplicationDispatcherFilterHttpRedirectionsTest initialized");
    }

    @Test
    public final void testHttpsRedirectionsDev() throws IOException, ServletException {

        setResolver(new DevResolver());

        // Onboarding
        testHttpsRedirect("http://start.local.devpv.com:8888/", true);
        testHttpsRedirect("https://start.local.devpv.com:8888/", false);

        // Operations
        testHttpsRedirect("http://operations.local.devpv.com:8888/", true);
        testHttpsRedirect("https://operations.local.devpv.com:8888/", false);

        // DB Reset
        testHttpsRedirect("http://static.local.devpv.com:8888/o/db-reset", true); // should expect redirection??
        testHttpsRedirect("https://static.local.devpv.com:8888/o/db-reset", false);

        // SITE
        testHttpsRedirect("http://vista-site.local.devpv.com:8888/", false);
        testHttpsRedirect("http://vista-site.local.devpv.com:8888/", false);

        // CRM
        testHttpsRedirect("http://vista-crm.local.devpv.com:8888/", true);
        testHttpsRedirect("https://vista-crm.local.devpv.com:8888/", false);
        testHttpsRedirect("http://vista-crm.local.devpv.com/#dashboard/view?Id=-1", true);

        // Resident
        testHttpsRedirect("http://vista-portal.local.devpv.com:8888/", true);
        testHttpsRedirect("https://vista-portal.local.devpv.com:8888/", false);

        // Prospect
        testHttpsRedirect("http://vista-portal.local.devpv.com:8888/prospect", true);
        testHttpsRedirect("https://vista-portal.local.devpv.com:8888/prospect", false);

        setResolver(null);
    }

    @Test
    public final void testHttpsRedirectionsEnv99() throws IOException, ServletException {

        setResolver(new EnvNResolver("-99.devpv.com"));

        // onboarding
        testHttpsRedirect("http://start-99.devpv.com/", true);
        testHttpsRedirect("https://start-99.devpv.com/", false);

        testHttpsRedirect("https://operations-99.devpv.com/", false);
        testHttpsRedirect("http://operations-99.devpv.com/", true);

        testHttpsRedirect("http://static-99.devpv.com/o/db-reset", true); // should expect redirection??
        testHttpsRedirect("https://static-99.devpv.com/o/db-reset", false);

        // crm
        testHttpsRedirect("http://redridge-crm-99.devpv.com/", true);
        testHttpsRedirect("https://redridge-crm-99.devpv.com/", false);

        // site
        testHttpsRedirect("https://redridge-site-99.devpv.com/", false);
        testHttpsRedirect("http://redridge-site-99.devpv.com/", false);
        // resident
        testHttpsRedirect("http://redridge-portal-99.devpv.com/", true);
        testHttpsRedirect("https://redridge-portal-99.devpv.com/", false);
        // prospect
        testHttpsRedirect("http://redridge-portal-99.devpv.com/prospect", true);
        testHttpsRedirect("https://redridge-portal-99.devpv.com/prospect", false);

        setResolver(null);
    }

    @Test
    public final void testHttpsRedirectionsProdCustomerDemo() throws IOException, ServletException {

        setResolver(new ProdCustomersDemoResolver());

        //
        // PROD CUSTOMERS DEMO
        // crm
        testHttpsRedirect("http://demo.propertyvista.com/", true);
        testHttpsRedirect("https://demo.propertyvista.com/", false);

        // site
        testHttpsRedirect("http://demo.residentportalsite.com/", false);
        testHttpsRedirect("https://demo.residentportalsite.com/", false);

        // resident
        testHttpsRedirect("https://demo.my-community.co/", false);
        testHttpsRedirect("http://demo.my-community.co/", true);

        // prospect
        testHttpsRedirect("https://demo.my-community.co/prospect", false);
        testHttpsRedirect("http://demo.my-community.co/prospect", true);

        setResolver(null);
    }

    @Test
    public final void testHttpsRedirectionsProdSalesDemo() throws IOException, ServletException {

        setResolver(new EnvNResolver(".propertyvista.biz"));

        // PROD SALES DEMO
//        testHttpsRedirect("http://onboarding.propertyvista.biz/", true);
//        testHttpsRedirect("https://onboarding.propertyvista.biz/", false);

        testHttpsRedirect("http://static.propertyvista.biz/o/db-reset", true); // should expect redirection??
        testHttpsRedirect("https://static.propertyvista.biz/o/db-reset", false);

        // crm
        testHttpsRedirect("https://vista-crm.propertyvista.biz/", false);
        testHttpsRedirect("http://vista-crm.propertyvista.biz/", true);

        // site
        testHttpsRedirect("https://vista-site.propertyvista.biz/", false);
        testHttpsRedirect("http://vista-site.propertyvista.biz/", false);

        // resident
        testHttpsRedirect("https://vista-portal.propertyvista.biz/", false);
        testHttpsRedirect("http://vista-portal.propertyvista.biz/", true);

        // prospect
        testHttpsRedirect("https://vista-portal.propertyvista.biz/prospect", false);
        testHttpsRedirect("http://vista-portal.propertyvista.biz/prospect", true);

        setResolver(null);
    }

    @Test
    public final void testHttpsRedirectionsProd() throws IOException, ServletException {

        setResolver(new ProdResolver());

        // PRODUCTION
        // crm
        testHttpsRedirect("http://testnamespace.propertyvista.com/", true);
        testHttpsRedirect("https://testnamespace.propertyvista.com/", false);
        // site
        testHttpsRedirect("https://testnamespace.residentportalsite.com/", false);
        testHttpsRedirect("http://testnamespace.residentportalsite.com/", false);
        // resident
        testHttpsRedirect("http://testnamespace.my-community.co/", true);
        testHttpsRedirect("https://testnamespace.my-community.co/", false);
        // prospect
        testHttpsRedirect("http://testnamespace.my-community.co/prospect", true);
        testHttpsRedirect("https://testnamespace.my-community.co/prospect", false);

        // crm
//        testHttpsRedirect("https://one-harder-pmc-name-crm.propertyvista.biz/", false);
//        testHttpsRedirect("http://one-harder-pmc-name-crm.propertyvista.biz/", true);
//
//        // site
//        testHttpsRedirect("https://one-harder-pmc-name-site.propertyvista.biz/", false);
//        testHttpsRedirect("http://one-harder-pmc-name-site.propertyvista.biz/", false);
//
//        // resident
//        testHttpsRedirect("https://one-harder-pmc-name-portal.propertyvista.biz/", false);
//        testHttpsRedirect("http://one-harder-pmc-name-portal.propertyvista.biz/", true);
//
//        // prospect
//        testHttpsRedirect("https://one-harder-pmc-name-portal.propertyvista.biz/prospect", false);
//        testHttpsRedirect("http://one-harder-pmc-name-portal.propertyvista.biz/prospect", true);

        setResolver(null);
    }

    /**
     * Tests if a redirection to https should be done
     */
    protected void testHttpsRedirect(String requestURL, boolean redirectExpected) throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest(requestURL);
        VistaApplication resolvedApplication = getContextResolver().resolve(request).getApplication();

        Assert.assertTrue("Redirection " + (redirectExpected ? "expected" : "not expected") + " for url '" + requestURL + "'",
                VistaApplicationResolverHelper.isHttpsRedirectionNeeded(request, resolvedApplication) == redirectExpected);

    }

}
