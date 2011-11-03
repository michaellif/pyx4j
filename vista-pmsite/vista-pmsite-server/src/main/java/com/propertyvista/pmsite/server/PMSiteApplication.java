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

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.IRequestCycleProvider;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.PageProvider;
import org.apache.wicket.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.MountedMapper;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.time.Duration;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;

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

    public static String LocaleParamName = "lang";

    private Exception internalError;

    private final String[] persistParams = { "gwt.codesvr" };

    // custom mapper - supports locale in url and persistent params in dev mode
    private class PMSiteLocalizedMapper extends MountedMapper {
        public PMSiteLocalizedMapper(final String path, Class<? extends IRequestablePage> pageClass) {
            super(path, pageClass);
        }

        @Override
        protected Url buildUrl(UrlInfo info) {
            // will add lang to UrlInfo params to build locale-enabled urls
            PageParameters newParams = info.getPageParameters();
            if (newParams == null) {
                newParams = new PageParameters();
            }
            Request request = RequestCycle.get().getRequest();
            // but don't override lang if already set in the parameters
            if (newParams.get(LocaleParamName).isEmpty()) {
                newParams.set(LocaleParamName, ((PMSiteWebRequest) request).getSiteLocale().lang().getValue().name());
            }
            // DEVELOPMENT MODE: support persistent params
            if (ApplicationMode.isDevelopment()) {
                IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
                for (String pName : persistParams) {
                    org.apache.wicket.util.string.StringValue pValue = params.getParameterValue(pName);
                    if (pValue != null && !pValue.isNull()) {
                        newParams.set(pName, pValue);
                    }
                }
                info = new UrlInfo(info.getPageComponentInfo(), info.getPageClass(), newParams);
            }
            return super.buildUrl(info);
        }

        @Override
        protected UrlInfo parseRequest(Request request) {
            final UrlInfo info = super.parseRequest(request);
            if (info != null && info.getPageParameters() != null) {
                final StringValue lang = info.getPageParameters().get(LocaleParamName);
                if (!lang.isEmpty()) {
                    ((PMSiteWebRequest) request).setSiteLocale(lang.toString());
                }
            }
            return info;
        }

        @Override
        protected boolean redirectFromHomePage() {
            return false;
        }

    }

    private <T extends Page> void customMount(final String path, Class<T> pageClass) {
        String localizedPath = "#{" + LocaleParamName + "}/" + path;
        mount(new PMSiteLocalizedMapper(localizedPath, pageClass));
    }

    @Override
    protected void init() {

        super.init();

        if (ServerSideConfiguration.isRunningInDeveloperEnviroment()) {
            getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
            getResourceSettings().setCachingStrategy(NoOpResourceCachingStrategy.INSTANCE);
        }
        getPageSettings().addComponentResolver(new I18nMessageResolver());
//        getMarkupSettings().setStripWicketTags(true);

        customMount("", LandingPage.class);
        customMount("signin", SignInPage.class);
        customMount("findapt", FindAptPage.class);
        customMount("aptlist", AptListPage.class);
        customMount("aptinfo", AptDetailsPage.class);
        customMount("unitinfo", UnitDetailsPage.class);
        customMount("residents", ResidentsPage.class);
        customMount("cnt" + PMSiteContentManager.PARAMETER_PATH, StaticPage.class);
        customMount("error", InternalErrorPage.class);

        // set exception listener to provide custom error handling
        getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public IRequestHandler onException(RequestCycle cycle, java.lang.Exception e) {
                // store exception for further use by InternalErrorPage
                internalError = e;
                return new RenderPageRequestHandler(new PageProvider(InternalErrorPage.class));
            }
        });
    }

    public Exception getInternalError() {
        return internalError;
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

    @Override
    protected WebRequest newWebRequest(HttpServletRequest servletRequest, String filterPath) {

        return new PMSiteWebRequest(servletRequest, filterPath);

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
