/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 24, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.config.deployment.VistaApplicationContext;
import com.propertyvista.config.deployment.VistaNamespaceResolver;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.portal.rpc.shared.SiteWasNotActivatedUserRuntimeException;
import com.propertyvista.server.config.filter.utils.HttpRequestUtils;

public class VistaApplicationContextDispatcherFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(VistaApplicationContextDispatcherFilter.class);

    private static final I18n i18n = I18n.get(VistaApplicationContextDispatcherFilter.class);

    private static String REQUEST_DISPATCHED_REQUEST_ATR = VistaApplicationContextDispatcherFilter.class.getName();

    private boolean isDeploymentHttps = false;

    private boolean debug = true; // temporary for local development

    private boolean enabled;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        enabled = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).isDepoymentApplicationDispatcher();
        isDeploymentHttps = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).isDepoymentHttps();
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (enabled) {
            if (request.getAttribute(REQUEST_DISPATCHED_REQUEST_ATR) == null) {
                request.setAttribute(REQUEST_DISPATCHED_REQUEST_ATR, Boolean.TRUE);
                map(request, response, chain);
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    public void map(final ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (debug) {
            log.info("Complete URL Request -> {}", HttpRequestUtils.getCompleteURLWithContextPath(httpRequest));
            log.info("Complete URL Request without context -> {}", HttpRequestUtils.getCompleteURLNoContextPath(httpRequest));
        }

        VistaApplicationContext context = VistaNamespaceResolver.instance().resolve(httpRequest);

        if (context.getApplication() == null) {
            throw new SiteWasNotActivatedUserRuntimeException(i18n.tr("This property management site was not activated yet"));
        }

        if (debug) {
            log.info("Resolved App '{}', NameSpace '{}'", context.getApplication(), context.getNamespace());
        }

        if (isDeploymentHttps && !httpRequest.isSecure() && context.getApplication().requireHttps()) {
            String defaultApplicationUrl = VistaDeployment.getBaseApplicationURL(context.getCurrentPmc(), context.getApplication(), true);
            ((HttpServletResponse) response).sendRedirect(defaultApplicationUrl);
        } else {
            httpRequest.setAttribute(VistaApplication.class.getName(), context.getApplication()); // Where do we use this ??

            if (context.getApplication() != VistaApplication.prospect) {
                httpRequest.setAttribute(ServletUtils.x_forwarded_path, "/" + context.getApplication().getInternalMappingName());
            }

            String urlForward = buildInternalForwardUri(httpRequest, context.getApplication());
            if (debug) {
                log.info("***ADF*** \"{}\" forwarding to \"{}\" ", httpRequest.getRequestURI(), urlForward);
            }

            request.getRequestDispatcher(urlForward).forward(request, response);

        }

//        if (VistaPmcDnsNameResolverHelper.isCustomerDNSActive(customerDnsName)) {
//            String defaultApplicationUrl = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getDefaultApplicationURL(app,
//                    customerDnsName.pmc().dnsName().getValue());
//
//            if (debug) {
//                log.debug("***ADF*** redirecting. Customer DNS alias active with https enabled. Sending redirect to default https url \"{}\" to browser",
//                        defaultApplicationUrl);
//            }
//            ((HttpServletResponse) response).sendRedirect(defaultApplicationUrl);
//            return;
//        } else if (isDeploymentHttps && VistaApplicationResolverHelper.isHttpsRedirectionNeeded(httprequest, app)) {
//            String httpsUrl = HttpRequestUtils.getHttpsUrl(httprequest);
//            if (debug) {
//                log.debug("***ADF*** redirecting. Change protocol from 'http' to 'https'. Sending redirect to \"{}\" to browser", httpsUrl);
//            }
//            ((HttpServletResponse) response).sendRedirect(httpsUrl);
//            return;
//        } else if (app == VistaApplication.prospect && VistaApplicationResolverHelper.isRootAppRequest(httprequest, app)) {
//            String urlToForward = VistaApplicationResolverHelper.getCompleteURLToForward(httprequest, app);
//            if (debug) {
//                log.debug("***ADF*** redirecting. Sending redirect from '/prospect' to \"{}\" to browser", urlToForward);
//            }
//            ((HttpServletResponse) response).sendRedirect(urlToForward);
//            return;
//        }
    }

    public static String buildInternalForwardUri(HttpServletRequest httpRequest, VistaApplication app) {
        String uri = "";

        String internalMappingRoot = "/" + app.getInternalMappingName();
        if (app != VistaApplication.prospect) {
            uri += internalMappingRoot;
        }

        String requestPath = httpRequest.getServletPath();
        uri += requestPath;

        // Hack in case of first request to Prospect app (default behavior reading directories different in tomcat than jetty)
//        if (app == VistaApplication.prospect && requestPath.equalsIgnoreCase(internalMappingRoot)) {
//            uri += "/";
//        }

        if (httpRequest.getPathInfo() != null) {
            uri += httpRequest.getPathInfo();
        }

        return uri;
    }
}
