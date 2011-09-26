/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import org.apache.wicket.IRequestCycleProvider;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.time.Duration;

import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.pmsite.server.pages.AptDetailsPage;
import com.propertyvista.pmsite.server.pages.AptListPage;
import com.propertyvista.pmsite.server.pages.FindAptPage;
import com.propertyvista.pmsite.server.pages.LandingPage;
import com.propertyvista.pmsite.server.pages.ResidentsPage;
import com.propertyvista.pmsite.server.pages.SignInPage;
import com.propertyvista.pmsite.server.pages.StaticPage;

public class PMSiteApplication extends AuthenticatedWebApplication {

    @Override
    protected void init() {

        super.init();

        if (ApplicationMode.isDevelopment()) {
            getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
        }
//        getPageSettings().addComponentResolver(new I18nMessageResolver());

        mountPage("findapt", FindAptPage.class);

        mountPage("aptlist", AptListPage.class);

        mountPage("aptinfo", AptDetailsPage.class);

        mountPage("residents", ResidentsPage.class);

        mountPage("cnt" + PMSiteContentManager.PARAMETER_PATH, StaticPage.class);

//        mountResource("js", new ResourceReference(JSResources.class, "js") {
//            private static final long serialVersionUID = 1L;
//
//            JSResources jsResources = new JSResources();
//
//            @Override
//            public IResource getResource() {
//                return jsResources;
//            }
//        });
    }

    @Override
    public IRequestCycleProvider getRequestCycleProvider() {
        return super.getRequestCycleProvider();
    }

    @Override
    public Class<LandingPage> getHomePage() {
        return LandingPage.class;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new PMSiteSession(request);
    }

    public static PMSiteApplication get() {
        return (PMSiteApplication) WebApplication.get();
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        if (ApplicationMode.isDevelopment()) {
            return RuntimeConfigurationType.DEVELOPMENT;
        } else {
            return RuntimeConfigurationType.DEPLOYMENT;
        }
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return PMSiteSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return SignInPage.class;
    }
}
