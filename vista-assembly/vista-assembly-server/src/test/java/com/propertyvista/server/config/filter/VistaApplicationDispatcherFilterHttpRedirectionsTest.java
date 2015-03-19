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

import org.junit.Ignore;

import com.propertyvista.server.config.filter.base.VistaApplicationDispatcherFilterTestBase;

public class VistaApplicationDispatcherFilterHttpRedirectionsTest extends VistaApplicationDispatcherFilterTestBase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        log.info("VistaApplicationDispatcherFilterHttpRedirectionsTest initialized");
    }

    /**
     * Test if redirection to HTTPS must be done
     *
     * @throws IOException
     * @throws ServletException
     */
    @Ignore
    public final void testHttpsRedirections() throws IOException, ServletException {
        // Onboarding
        testHttpsRedirect("http://onboarding.dev.birchwoodsoftwaregroup.com:8888/", true);
        testHttpsRedirect("https://onboarding.dev.birchwoodsoftwaregroup.com:8888/", false);

        // Operations
        testHttpsRedirect("http://operations.dev.birchwoodsoftwaregroup.com:8888/", true);
        testHttpsRedirect("https://operations.dev.birchwoodsoftwaregroup.com:8888/", false);

        // DB Reset
        testHttpsRedirect("http://static.dev.birchwoodsoftwaregroup.com:8888/o/db-reset", false);
        testHttpsRedirect("https://static.dev.birchwoodsoftwaregroup.com:8888/o/db-reset", false);

        // SITE
        testHttpsRedirect("http://vista-site.dev.birchwoodsoftwaregroup.com:8888/", false);
        testHttpsRedirect("http://vista-site.dev.birchwoodsoftwaregroup.com:8888/", false);

        // CRM
        testHttpsRedirect("http://vista-crm.dev.birchwoodsoftwaregroup.com:8888/", true);
        testHttpsRedirect("https://vista-crm.dev.birchwoodsoftwaregroup.com:8888/", false);
        testHttpsRedirect("http://vista-crm.dev.birchwoodsoftwaregroup.com/#dashboard/view?Id=-1", true);

        // Resident
        testHttpsRedirect("http://vista-portal.dev.birchwoodsoftwaregroup.com:8888/", true);
        testHttpsRedirect("https://vista-portal.dev.birchwoodsoftwaregroup.com:8888/", false);

        // Prospect
        testHttpsRedirect("http://vista-portal.dev.birchwoodsoftwaregroup.com:8888/prospect", true);
        testHttpsRedirect("https://vista-portal.dev.birchwoodsoftwaregroup.com:8888/prospect", false);

        // Gondor crm
        testHttpsRedirect("http://gondor-crm-99.devpv.com/", true);
        testHttpsRedirect("https://gondor-crm-99.devpv.com/", false);

        // PRODUCTION
        // crm
        testHttpsRedirect("http://ofm.propertyvista.com/", true);
        testHttpsRedirect("https://ofm.propertyvista.com/", false);
        // site
        testHttpsRedirect("https://ofm.residentportalsite.com/", false);
        testHttpsRedirect("http://ofm.residentportalsite.com/", false);
        // resident
        testHttpsRedirect("http://ofm.my-community.co/", true);
        testHttpsRedirect("https://ofm.my-community.co/", false);
        // prospect
        testHttpsRedirect("http://ofm.my-community.co/prospect", true);
        testHttpsRedirect("https://ofm.my-community.co/prospect", false);

        // PRODUCTION DEMO
        testHttpsRedirect("http://demo.propertyvista.com/", true);

    }

}
