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
import java.util.Locale;

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

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.pmc.PmcDnsName;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.portal.rpc.shared.SiteWasNotActivatedUserRuntimeException;
import com.propertyvista.server.config.filter.namespace.VistaApplicationResolverHelper;
import com.propertyvista.server.config.filter.namespace.VistaNamespaceDataResolver;
import com.propertyvista.server.config.filter.namespace.VistaPmcDnsNameResolverHelper;
import com.propertyvista.server.config.filter.utils.HttpRequestUtils;

public class VistaApplicationDispatcherFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(VistaApplicationDispatcherFilter.class);

    private static String REQUEST_DISPATCHED_REQUEST_ATR = VistaApplicationDispatcherFilter.class.getName();

    private boolean isDeploymentHttps = false;

    private boolean debug = false; // temporary for local development

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).isDepoymentApplicationDispatcher()) {
            if (request.getAttribute(REQUEST_DISPATCHED_REQUEST_ATR) == null) {
                request.setAttribute(REQUEST_DISPATCHED_REQUEST_ATR, Boolean.TRUE);
                if (ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).isDepoymentHttps()) {
                    isDeploymentHttps = true;
                }
                map(request, response, chain);
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    public void map(final ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httprequest = (HttpServletRequest) request;

        VistaNamespaceDataResolver namespaceResolver = VistaNamespaceDataResolver.create(httprequest);

//        if (debug) {
//            log.debug("Complete URL Request -> {}", HttpRequestUtils.getCompleteURLWithContextPath(httprequest));
//            log.debug("Complete URL Request without context -> {}", HttpRequestUtils.getCompleteURLNoContextPath(httprequest));
//        }

        // For sample url -> http://vista-crm.dev.birchwoodsoftwaregroup.com:8888/vista/crm/tip.png?width=23
        String requestUri = httprequest.getRequestURI(); // sample: /vista/crm/tip.png
        String requestParams = httprequest.getQueryString(); // sample: width=23
        String requestPath = httprequest.getServletPath(); // sample: /crm/tip.png
        String serverName = httprequest.getServerName(); // sample: vista-crm.dev.birchwoodsoftwaregroup.com
        serverName = serverName.toLowerCase(Locale.ENGLISH);

        VistaApplication app = namespaceResolver.getNamespaceData().getApplication();
        PmcDnsName customerDnsName = namespaceResolver.getNamespaceData().getCustomerDnsName();

        if (app == null) {
            throw new SiteWasNotActivatedUserRuntimeException(serverName);
        } else if (VistaPmcDnsNameResolverHelper.isCustomerDNSActive(customerDnsName)) {
            String defaultApplicationUrl = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getDefaultApplicationURL(app,
                    customerDnsName.pmc().dnsName().getValue());

            if (debug) {
                log.debug("***ADF*** redirecting. Customer DNS alias active with https enabled. Sending redirect to default https url \"{}\" to browser",
                        defaultApplicationUrl);
            }
            ((HttpServletResponse) response).sendRedirect(defaultApplicationUrl);
            return;
        } else if (isDeploymentHttps && VistaApplicationResolverHelper.isHttpsRedirectionNeeded(httprequest, app)) {
            String httpsUrl = HttpRequestUtils.getHttpsUrl(httprequest);
            if (debug) {
                log.debug("***ADF*** redirecting. Change protocol from 'http' to 'https'. Sending redirect to \"{}\" to browser", httpsUrl);
            }
            ((HttpServletResponse) response).sendRedirect(httpsUrl);
            return;
        } else if (app == VistaApplication.prospect && VistaApplicationResolverHelper.isRootAppRequest(httprequest, app)) {
            String urlToForward = VistaApplicationResolverHelper.getCompleteURLToForward(httprequest, app);
            if (debug) {
                log.debug("***ADF*** redirecting. Sending redirect from '/prospect' to \"{}\" to browser", urlToForward);
            }
            ((HttpServletResponse) response).sendRedirect(urlToForward);
            return;
        } else {
            httprequest.setAttribute(VistaApplication.class.getName(), app);
            String forwardedPath = VistaApplicationResolverHelper.getPathToForwarded(app);
            httprequest.setAttribute(ServletUtils.x_forwarded_path, forwardedPath);
            String urlForward = VistaApplicationResolverHelper.getNewURLRequest(httprequest, app);
            if (debug) {
                log.debug("***ADF*** \"{}\" forwarding to \"{}\" ", requestUri, urlForward);
            }
            request.getRequestDispatcher(urlForward).forward(request, response);
        }
    }
}
