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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.config.appcontext.DevResolver;
import com.propertyvista.server.config.appcontext.EnvNResolver;
import com.propertyvista.server.config.appcontext.ProdCustomersDemoResolver;
import com.propertyvista.server.config.appcontext.ProdResolver;
import com.propertyvista.server.config.filter.base.VistaNamespaceResolverTestBase;

public class ResolveNamespaceDataTest extends VistaNamespaceResolverTestBase {

    protected final static Logger log = LoggerFactory.getLogger(ResolveNamespaceDataTest.class);

    @Test
    public void testDev() {
        setResolver(new DevResolver());

        testResolveNamespace("http://vista-crm.local.devpv.com:8888/vista/crm/srv/IServiceAdapter/BuildingCrudService.list", VistaApplication.crm, "vista");

        testResolveNamespace("http://start.local.devpv.com:8888/", VistaApplication.onboarding, VistaNamespace.noNamespace);

        testResolveNamespace("http://operations.local.devpv.com:80/operations/operations.nocache.js", VistaApplication.operations,
                VistaNamespace.operationsNamespace);

        testResolveNamespace("http://vista-crm.local.devpv.com:8888/", VistaApplication.crm, "vista");

        testResolveNamespace("http://vista-site.local.devpv.com:8888/", VistaApplication.site, "vista");

        testResolveNamespace("http://vista-portal.local.devpv.com:8888/", VistaApplication.resident, "vista");

//        testResolveNamespace("http://one-harder-pmc-name-portal.local.devpv.com:8888/prospect", VistaApplication.prospect, "one-harder-pmc-name");
//
//        testResolveNamespace("http://one-harder-pmc-name-crm.local.devpv.com:8888/", VistaApplication.crm, "one-harder-pmc-name");
//
//        testResolveNamespace("http://one-harder-pmc-name-site.local.devpv.com:8888/", VistaApplication.site, "one-harder-pmc-name");
//
//        testResolveNamespace("http://one-harder-pmc-name-portal.local.devpv.com:8888/", VistaApplication.resident, "one-harder-pmc-name");
//
//        testResolveNamespace("http://one-harder-pmc-name-portal.local.devpv.com:8888/prospect", VistaApplication.prospect, "one-harder-pmc-name");

        setResolver(null);
    }

    @Test
    public void testEnv11() {
        setResolver(new EnvNResolver("-11.devpv.com"));

        testResolveNamespace("https://start-11.devpv.com/", VistaApplication.onboarding, VistaNamespace.noNamespace);

        testResolveNamespace("https://operations-11.devpv.com/", VistaApplication.operations, VistaNamespace.operationsNamespace);

        testResolveNamespace("http://static-11.devpv.com/o/db-reset", VistaApplication.staticContext, VistaNamespace.noNamespace);

        // vista PMC
        testResolveNamespace("https://vista-crm-11.devpv.com/", VistaApplication.crm, "vista");

        testResolveNamespace("https://vista-site-11.devpv.com/", VistaApplication.site, "vista");

        testResolveNamespace("https://vista-portal-11.devpv.com/", VistaApplication.resident, "vista");

        testResolveNamespace("https://vista-portal-11.devpv.com/prospect", VistaApplication.prospect, "vista");

        // one-harder-pmc-name
//        testResolveNamespace("https://one-harder-pmc-name-crm-11.devpv.com/", VistaApplication.crm, "one-harder-pmc-name");
//
//        testResolveNamespace("https://one-harder-pmc-name-site-11.devpv.com/", VistaApplication.site, "one-harder-pmc-name");
//
//        testResolveNamespace("https://one-harder-pmc-name-portal-11.devpv.com/", VistaApplication.resident, "one-harder-pmc-name");
//
//        testResolveNamespace("https://one-harder-pmc-name-portal-11.devpv.com/prospect", VistaApplication.prospect, "one-harder-pmc-name");
    }

    @Test
    public void testProd() {
        setResolver(new ProdResolver());
        // New tests URLs to deal with staging
        // VISTA STAGING
        testResolveNamespace("https://vista-crm-staging.propertyvista.net/", VistaApplication.crm, "vista");

        testResolveNamespace("https://vista-site-staging.propertyvista.net/", VistaApplication.site, "vista");

        testResolveNamespace("https://vista-portal-staging.propertyvista.net/", VistaApplication.resident, "vista");

        testResolveNamespace("https://vista-portal-staging.propertyvista.net/prospect", VistaApplication.prospect, "vista");

        // Onboarding
        testResolveNamespace("https://start-staging.propertyvista.net/", VistaApplication.onboarding, VistaNamespace.noNamespace);

        // Operations
        testResolveNamespace("https://operations-staging.propertyvista.net/", VistaApplication.operations, VistaNamespace.operationsNamespace);

        // DB Reset
        testResolveNamespace("http://static-staging.propertyvista.net/o/db-reset", VistaApplication.staticContext, VistaNamespace.noNamespace);

        // REDRIDGE STAGING
        testResolveNamespace("https://redridge-crm-staging.propertyvista.net/", VistaApplication.crm, "redridge");

        testResolveNamespace("https://redridge-site-staging.propertyvista.net/", VistaApplication.site, "redridge");

        testResolveNamespace("https://redridge-portal-staging.propertyvista.net/", VistaApplication.resident, "redridge");

        testResolveNamespace("https://redridge-portal-staging.propertyvista.net/prospect", VistaApplication.prospect, "redridge");

        //
        // PRODUCTION
        // crm
        testResolveNamespace("https://redridge.propertyvista.com/", VistaApplication.crm, "redridge");

        // site
        testResolveNamespace("http://redridge.residentportalsite.com/", VistaApplication.site, "redridge");

        // resident
        testResolveNamespace("https://redridge.my-community.co/", VistaApplication.resident, "redridge");

        // prospect
//        testResolveNamespace("https://redridge.my-community.co/prospect", VistaApplication.prospect, "redridge");

        // prospect
//        testResolveNamespace("https://redridge.my-community.co/prospect/", VistaApplication.prospect, "redridge");

        // proddemo static
//        testResolveNamespace("https://proddemo.propertyvista.biz/vista-prod-demo/", VistaApplication.staticContext, VistaNamespace.noNamespace);

        setResolver(null);
    }

    @Test
    public void testProdCustomersDemo() {
        setResolver(new ProdCustomersDemoResolver());

        //
        // PROD CUSTOMERS DEMO
        // crm
        testResolveNamespace("https://demo.propertyvista.com/", VistaApplication.crm, "demo");

        // site
        testResolveNamespace("http://demo.residentportalsite.com/", VistaApplication.site, "demo");

        // resident
        testResolveNamespace("https://demo.my-community.co/", VistaApplication.resident, "demo");

        // prospect
//        testResolveNamespace("https://demo.my-community.co/prospect", VistaApplication.prospect, "demo");

//        testResolveNamespace("https://demo.my-community.co/prospect/", VistaApplication.prospect, "demo");

        //
        // PROD SALES DEMO
//        testResolveNamespace("https://onboarding.propertyvista.biz/", VistaApplication.onboarding, VistaNamespace.noNamespace);

//        testResolveNamespace("https://operations.propertyvista.biz/", VistaApplication.operations, VistaNamespace.operationsNamespace);
//
//        testResolveNamespace("http://static.propertyvista.biz/o/db-reset", VistaApplication.staticContext, VistaNamespace.noNamespace);
//
//        // crm
//        testResolveNamespace("https://vista-crm.propertyvista.biz/", VistaApplication.crm, "vista");
//
//        // site
//        testResolveNamespace("https://vista-site.propertyvista.biz/", VistaApplication.site, "vista");
//
//        // resident
//        testResolveNamespace("https://vista-portal.propertyvista.biz/", VistaApplication.resident, "vista");
//
//        // prospect
//        testResolveNamespace("https://vista-portal.propertyvista.biz/prospect", VistaApplication.prospect, "vista");

//        testResolveNamespace("https://demo.my-community.co/prospect/", VistaApplication.prospect, "vista");

        // crm
//        testResolveNamespace("https://one-harder-pmc-name-crm.propertyvista.biz/", VistaApplication.crm, "one-harder-pmc-name");
//
//        // site
//        testResolveNamespace("https://one-harder-pmc-name-site.propertyvista.biz/", VistaApplication.site, "one-harder-pmc-name");
//
//        // resident
//        testResolveNamespace("https://one-harder-pmc-name-portal.propertyvista.biz/", VistaApplication.resident, "one-harder-pmc-name");
//
//        // prospect
//        testResolveNamespace("https://one-harder-pmc-name-portal.propertyvista.biz/prospect", VistaApplication.prospect, "one-harder-pmc-name");

        setResolver(null);
    }

    @Test
    public final void testDnsAliases() {
        setResolver(new ProdResolver());

        // test active PMC with active DNS Alias for CRM
        testDNSAlias("http://custom.crm.server.canada.com:80/index.html", VistaApplication.crm);

        // test active PMC with active DNS Alias for PORTAL - RESIDENT
        testDNSAlias("http://portalito.canada.com:8888/index.html", VistaApplication.resident);

        // test active PMC with active DNS Alias for SITE
        testDNSAlias("http://mysite-bestseller.canada.com:8990/srv/request.html", VistaApplication.site);

        // test Inactive PMC with active DNS Alias
        testDNSAlias("http://customizableportal.server.canada.com:8990/robots.txt", null);

        // test Active PMC with Inactive DNS Alias
        testDNSAlias("http://customer.site.client-custom.canada.com:8990/robots.txt", null);

        setResolver(null);
    }

    private void testResolveNamespace(String requestURL, VistaApplication app, String ns) {
        assertApp(requestURL, app);
        assertNamespace(requestURL, ns);
        assertPmc(requestURL, ns);
    }

    private void testDNSAlias(String requestURL, VistaApplication app) {
        assertApp(requestURL, app);
    }

}
