/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2015
 * @author vlads
 */
package com.propertyvista.server.config.filter;

import org.junit.Test;

import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.config.appcontext.DevResolver;
import com.propertyvista.server.config.appcontext.EnvNResolver;
import com.propertyvista.server.config.appcontext.ProdCustomersDemoResolver;
import com.propertyvista.server.config.appcontext.ProdResolver;
import com.propertyvista.server.config.filter.base.VistaNamespaceResolverTestBase;

public class ResolveApplicationTest extends VistaNamespaceResolverTestBase {

    @Test
    public void testDev() {
        setResolver(new DevResolver());
        assertApp("http://start.local.devpv.com", VistaApplication.onboarding);
        assertApp("http://vista-crm.local.devpv.com", VistaApplication.crm);
        setResolver(null);
    }

    @Test
    public void testEnv11() {
        setResolver(new EnvNResolver("-11.devpv.com"));
        assertApp("http://start-11.devpv.com", VistaApplication.onboarding);
        assertApp("https://vista-crm-11.devpv.com/", VistaApplication.crm);
        setResolver(null);
    }

    @Test
    public void testProd() {
        setResolver(new ProdResolver());
        assertApp("https://interfaces.propertyvista.com", VistaApplication.interfaces);
        assertApp("https://vista.propertyvista.com/interfaces", VistaApplication.crm);

        assertApp("https://operations.propertyvista.com", VistaApplication.operations);

        assertApp("https://env.propertyvista.com", VistaApplication.env);

        assertApp("https://env.my-community.co", null);
        assertApp("https://env-staging.propertyvista.net", VistaApplication.env);
        assertApp("https://env.propertyvista.net", null);

        assertApp("https://static.propertyvista.com", VistaApplication.staticContext);
        assertApp("https://static.my-community.co", VistaApplication.staticContext);
        assertApp("https://static-staging.propertyvista.net", VistaApplication.staticContext);
        assertApp("https://static.propertyvista.net", null);

        assertApp("https://vista.propertyvista.com", VistaApplication.crm);
        assertApp("https://vista-crm-staging.propertyvista.net", VistaApplication.crm);
        assertApp("https://one-harder-pmc-name.propertyvista.com", VistaApplication.crm);

        setResolver(null);
    }

    @Test
    public void testProdCustomersDemo() {
        setResolver(new ProdCustomersDemoResolver());
        // Customers demo
        assertApp("https://demo.propertyvista.com", VistaApplication.crm);
        assertApp("https://demo.my-community.co", VistaApplication.resident);
        assertApp("https://demo.my-community.co/prospect", VistaApplication.prospect);
        assertApp("http://demo.residentportalsite.com/en/", VistaApplication.site);

        assertApp("https://demo-crm-cdemo.propertyvista.biz", VistaApplication.crm);
        assertApp("https://demo-site-cdemo.propertyvista.biz", VistaApplication.site);
        assertApp("https://demo-portal-cdemo.propertyvista.biz", VistaApplication.resident);
        assertApp("https://demo-portal-cdemo.propertyvista.biz/portal", VistaApplication.resident);
    }

    @Test
    public void testProdSalesDemo() {
        setResolver(new EnvNResolver(".propertyvista.biz"));

        // Sales demo
        assertApp("https://redridge-crm.propertyvista.biz", VistaApplication.crm);
        assertApp("https://redridge-site.propertyvista.biz", VistaApplication.site);
        assertApp("https://redridge-portal.propertyvista.biz", VistaApplication.resident);
        assertApp("https://redridge-portal.propertyvista.biz/portal", VistaApplication.resident);

        assertApp("https://operations.propertyvista.biz", VistaApplication.operations);
        assertApp("https://start.propertyvista.biz", VistaApplication.onboarding);
        assertApp("https://static.propertyvista.biz", VistaApplication.staticContext);
        assertApp("http://env.propertyvista.biz/o/db-reset", VistaApplication.env);
        setResolver(null);
    }

}
