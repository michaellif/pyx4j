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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.IRequestCycleProvider;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.SystemMapper;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.handler.PageProvider;
import org.apache.wicket.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.MountedMapper;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.security.rpc.AuthenticationService;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.pmsite.server.pages.AptDetailsPage;
import com.propertyvista.pmsite.server.pages.AptListPage;
import com.propertyvista.pmsite.server.pages.FindAptPage;
import com.propertyvista.pmsite.server.pages.InquiryPage;
import com.propertyvista.pmsite.server.pages.InquirySuccessPage;
import com.propertyvista.pmsite.server.pages.InternalErrorPage;
import com.propertyvista.pmsite.server.pages.LandingPage;
import com.propertyvista.pmsite.server.pages.PwdChangePage;
import com.propertyvista.pmsite.server.pages.PwdResetPage;
import com.propertyvista.pmsite.server.pages.ResidentsPage;
import com.propertyvista.pmsite.server.pages.SignInPage;
import com.propertyvista.pmsite.server.pages.StaticPage;
import com.propertyvista.pmsite.server.pages.TermsAcceptancePage;
import com.propertyvista.pmsite.server.pages.TermsDeclinedPage;
import com.propertyvista.pmsite.server.pages.UnitDetailsPage;
import com.propertyvista.shared.CompiledLocale;

public class PMSiteApplication extends AuthenticatedWebApplication {

    private static final Logger log = LoggerFactory.getLogger(PMSiteApplication.class);

    private Exception internalError;

    private final String[] persistParams = { "gwt.codesvr" };

    public static String ParamNameLang = "lang";

    public static String ParamNameTarget = "target";

    public static String ParamNameBuilding = "propId";

    public static String ParamNameFloorplan = "fpId";

    private static final Map<String, Class<? extends Page>> MountMap = new HashMap<String, Class<? extends Page>>();

    static {
        MountMap.put("", LandingPage.class);
        MountMap.put("signin", SignInPage.class);
        MountMap.put("findapt", FindAptPage.class);
        MountMap.put("aptlist", AptListPage.class);
        MountMap.put("aptinfo", AptDetailsPage.class);
        MountMap.put("unitinfo", UnitDetailsPage.class);
        MountMap.put("residents", ResidentsPage.class);
        MountMap.put("pwdreset", PwdResetPage.class);
        MountMap.put("pwdchange", PwdChangePage.class);
        MountMap.put("termsaccept", TermsAcceptancePage.class);
        MountMap.put("termsdecline", TermsDeclinedPage.class);
        MountMap.put("inquiry", InquiryPage.class);
        MountMap.put("inquiryok", InquirySuccessPage.class);
        MountMap.put("cnt" + PMSiteContentManager.PARAMETER_PATH, StaticPage.class);
        MountMap.put("error", InternalErrorPage.class);
    }

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
            if (newParams.get(ParamNameLang).isEmpty()) {
                newParams.set(ParamNameLang, ((PMSiteWebRequest) request).getSiteLocale().lang().getValue().name());
            }
            // DEVELOPMENT MODE: support persistent params
            if (ApplicationMode.isDevelopment()) {
                IRequestParameters params = RequestCycle.get().getRequest().getRequestParameters();
                for (String pName : persistParams) {
                    StringValue pValue = params.getParameterValue(pName);
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
            UrlInfo info = super.parseRequest(request);
            if (info != null && info.getPageParameters() != null) {
                StringValue lang = info.getPageParameters().get(ParamNameLang);
                if (!lang.isEmpty()) {
                    ((PMSiteWebRequest) request).setSiteLocale(lang.toString());
                }
                // If given lang is not supported the above method will set the default locale,
                // and we will need to redirect to corresponding url
                String siteLang = ((PMSiteWebRequest) request).getSiteLocale().lang().getValue().getLanguage();
                if (lang.isEmpty() || !lang.toString().equalsIgnoreCase(siteLang)) {
                    PageParameters newParams = new PageParameters(info.getPageParameters());
                    newParams.set(ParamNameLang, siteLang);
                    info = new UrlInfo(info.getPageComponentInfo(), info.getPageClass(), newParams);
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
        String localizedPath = "#{" + ParamNameLang + "}/" + path;
        mount(new PMSiteLocalizedMapper(localizedPath, pageClass));
    }

    @Override
    protected void init() {

        super.init();

        setRootRequestMapper(new SystemMapper(this) {
            @Override
            public IRequestHandler mapRequest(final Request request) {

                class MapperScore implements Comparable<MapperScore> {
                    protected final IRequestMapper mapper;

                    protected final int score;

                    public MapperScore(IRequestMapper mapper) {
                        this.mapper = mapper;
                        this.score = mapper.getCompatibilityScore(request);
                    }

                    @Override
                    public int compareTo(final MapperScore o) {
                        return o.score - score;
                    }
                }

                List<MapperScore> list = new ArrayList<MapperScore>();

                for (Iterator<IRequestMapper> iterator = iterator(); iterator.hasNext();) {
                    list.add(new MapperScore(iterator.next()));
                }

                Collections.sort(list);

                for (MapperScore mapperWithScore : list) {
                    IRequestHandler handler = mapperWithScore.mapper.mapRequest(request);
                    if (handler != null) {
                        return handler;
                    }
                }

                // default catch-all mapping - will render error page if url is not recognized
                log.debug("Resource Not Found: " + request.getUrl().toString());
                boolean pageRequested = false;
                MapperScore bestMatch = list.get(0);
                if (bestMatch.mapper instanceof MountedMapper) {
                    pageRequested = true;
                } else {
                    for (MapperScore ms : list) {
                        if (ms.score < bestMatch.score) {
                            break;
                        } else if (ms.mapper instanceof MountedMapper) {
                            pageRequested = true;
                            break;
                        }
                    }
                }

                if (pageRequested) {
                    // if url has no query part and no trailing slash, try to fix by appending slash
                    if (request.getUrl().getQueryString().length() == 0) {
                        List<String> segments = request.getUrl().getSegments();
                        String last = segments.get(segments.size() - 1);
                        for (CompiledLocale locale : CompiledLocale.values()) {
                            if (locale.getLanguage().equals(last)) {
                                // no trailing slash - respond with redirect
                                return new RedirectRequestHandler(last + "/");
                            }
                        }
                    }
                    // not our page - show error
                    internalError = new Exception("Page Not Found: " + request.getUrl().toString());
                    return new RenderPageRequestHandler(new PageProvider(InternalErrorPage.class));
                }

                return null;
            }

        });

        if (ServerSideConfiguration.isRunningInDeveloperEnviroment()) {
            getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
            getResourceSettings().setCachingStrategy(NoOpResourceCachingStrategy.INSTANCE);
        }
        getPageSettings().addComponentResolver(new I18nMessageResolver());
//        getMarkupSettings().setStripWicketTags(true);

        // mount site urls
        for (String path : MountMap.keySet()) {
            Class<? extends Page> pageClass = MountMap.get(path);
            customMount(path, pageClass);
        }

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

    public static String getAptDetailsPagePath(String propCode, String lang) {
        for (String path : MountMap.keySet()) {
            Class<? extends Page> pageClass = MountMap.get(path);
            if (AptDetailsPage.class.equals(pageClass)) {
                Url url = Url.parse(path);
                url.addQueryParameter(ParamNameBuilding, propCode);
                url.addQueryParameter(ParamNameLang, lang);
                return url.toString();
            }
        }

        return null;
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
    // super implementation uses HttpServletResponse.encodeURL() to append jsessionid to url
    // we don't want that as we rely on cookies
    protected WebResponse newWebResponse(final WebRequest webRequest, final HttpServletResponse httpServletResponse) {
        return new ServletWebResponse((ServletWebRequest) webRequest, httpServletResponse) {
            @Override
            public String encodeURL(CharSequence url) {
                return url.toString();
            }

            @Override
            public String encodeRedirectURL(CharSequence url) {
                return url.toString();
            }
        };
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return PMSiteSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return SignInPage.class;
    }

    protected Class<? extends WebPage> getPwdChangePage() {
        return PwdChangePage.class;
    }

    protected String getReturnToTargetUrl() {
        Url target = RequestCycle.get().getRequest().getUrl();
        // remove auth token if present
        target.removeQueryParameters(AuthenticationService.AUTH_TOKEN_ARG);
        return target.toString();
    }

    // get path to context root
    private String getPathToRoot() {
        HttpServletRequest req = ((ServletWebRequest) RequestCycle.get().getRequest()).getContainerRequest();
        return req.getServletPath().replaceAll("/+[^/]*", "../").replaceFirst("../", "");
    }

    public void redirectToTarget() {
        String targetUrl = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(PMSiteApplication.ParamNameTarget).toString();
        if (targetUrl == null || targetUrl.length() == 0) {
            throw new RestartResponseException(getHomePage());
        } else {
            // get path relative to context root
            String toRoot = getPathToRoot();
            String servletPath = RequestCycle.get().getRequest().getFilterPath();
            if (servletPath.startsWith("/")) {
                servletPath = servletPath.substring(1);
            }
            toRoot += servletPath + "/" + targetUrl;
            throw new RedirectToUrlException(toRoot + "");
        }

    }

    // we want to use target url parameter to avoid storing it in session
    @Override
    public void restartResponseAtSignInPage() {
        PageParameters pp = new PageParameters();
        pp.add(ParamNameTarget, getReturnToTargetUrl());
        throw new RestartResponseException(getSignInPageClass(), pp);
    }

    @Override
    protected void onUnauthorizedPage(final Page page) {
        if (hasAnyRole(new Roles(PMSiteSession.PasswordChangeRequiredRole))) {
            // redirect to Change Password page
            PageParameters pp = new PageParameters();
            pp.add(ParamNameTarget, getReturnToTargetUrl());
            throw new RestartResponseException(getPwdChangePage(), pp);
        } else if (hasAnyRole(new Roles(PMSiteSession.VistaTermsAcceptanceRequiredRole))) {
            // redirect to LegalAcceptancePage
            PageParameters pp = new PageParameters();
            pp.add(ParamNameTarget, getReturnToTargetUrl());
            throw new RestartResponseException(TermsAcceptancePage.class, pp);
        } else {
            // redirect to home page
            throw new RestartResponseException(getHomePage());
        }
    }

    public static void onSecurePage(Request request) {
        HttpServletRequest httpServletRequest = ((ServletWebRequest) request).getContainerRequest();
        // redirect if not secure
        String secureBaseUrl = VistaDeployment.getBaseApplicationURL(VistaBasicBehavior.TenantPortal, true);
        String requestUrl = ServletUtils.getActualRequestURL(httpServletRequest, false);
        log.debug("request: {}; configured: {}", requestUrl, secureBaseUrl);
        if (!requestUrl.startsWith(secureBaseUrl)) {
            String secureUrl = secureBaseUrl + request.getUrl().toString();
            log.info("secure redirect {}", secureUrl);
            throw new RedirectToUrlException(secureUrl);
        }
    }

}
