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
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.PageProvider;
import org.apache.wicket.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.util.time.Duration;

import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.pmsite.server.pages.AptDetailsPage;
import com.propertyvista.pmsite.server.pages.AptListPage;
import com.propertyvista.pmsite.server.pages.FindAptPage;
import com.propertyvista.pmsite.server.pages.InternalErrorPage;
import com.propertyvista.pmsite.server.pages.LandingPage;
import com.propertyvista.pmsite.server.pages.ResidentsPage;
import com.propertyvista.pmsite.server.pages.SignInPage;
import com.propertyvista.pmsite.server.pages.StaticPage;
import com.propertyvista.pmsite.server.pages.UnitDetailsPage;

public class PMSiteApplication extends AuthenticatedWebApplication {

    @Override
    protected void init() {

        super.init();

        if (ServerSideConfiguration.isRunningInDeveloperEnviroment()) {
            getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
            getResourceSettings().setCachingStrategy(NoOpResourceCachingStrategy.INSTANCE);
        }
//        getPageSettings().addComponentResolver(new I18nMessageResolver());

        mountPage("signin", SignInPage.class);

        mountPage("findapt", FindAptPage.class);

        mountPage("aptlist", AptListPage.class);

        mountPage("aptinfo", AptDetailsPage.class);
        mountPage("unitinfo", UnitDetailsPage.class);

        mountPage("residents", ResidentsPage.class);

        mountPage("cnt" + PMSiteContentManager.PARAMETER_PATH, StaticPage.class);

        mountPage("error", InternalErrorPage.class);

        // set exception listener
        getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public IRequestHandler onException(RequestCycle cycle, java.lang.Exception e) {
                return new RenderPageRequestHandler(new PageProvider(new InternalErrorPage(new PageParameters().add("error", e))));
            }
        });
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
        if (ServerSideConfiguration.isRunningInDeveloperEnviroment()) {
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
