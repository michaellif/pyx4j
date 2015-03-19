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

import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.config.filter.base.VistaApplicationDispatcherFilterTestBase;

public class VistaApplicationDispatcherFilterDoMappingsTest extends VistaApplicationDispatcherFilterTestBase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        log.info("VistaApplicationDispatcherFilterDoMappingsTest initialized");
    }

    /**
     * Test mappings for URL request. In case of first request to Prospect application without "/" at the end, it should redirect to client instead of
     * forwarding.
     *
     * @throws IOException
     * @throws ServletException
     */
    @Ignore
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
        String prospectUrlHttp = "http://vista-portal.dev.birchwoodsoftwaregroup.com:8888/prospect";
        testRedirect(prospectUrlHttp, getProspectRedirectUrl(prospectUrlHttp));
        testForward("http://vista-portal.dev.birchwoodsoftwaregroup.com:8888/prospect/", VistaApplication.prospect);

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
        testForward("http://vista-crm-00.devpv.com/", VistaApplication.crm);

        // Resident
        testForward("https://vista-portal-22.birchwoodsoftwaregroup.com/", VistaApplication.resident);

        // Prospect
        String prospectUrlHttps = "https://vista-portal-22.birchwoodsoftwaregroup.com/prospect";
        testRedirect(prospectUrlHttps, getProspectRedirectUrl(prospectUrlHttps));

        testForward("https://vista-portal-99.devpv.com/prospect/", VistaApplication.prospect);

        prospectUrlHttps = "https://vista-portal-99.devpv.com/prospect";
        testRedirect(prospectUrlHttps, getProspectRedirectUrl(prospectUrlHttps));
    }

    // In case of "prospect", filter should send to browser absolute redirect instead of relative
    protected String getProspectRedirectUrl(String url) {
        return url.replace("/prospect", "/prospect/");
    }
}
