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
 * @version $Id$
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
import com.propertyvista.domain.security.common.VistaApplication;

public class VistaApplicationDispatcherFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(VistaApplicationDispatcherFilter.class);

    private static String REQUEST_DISPATCHED_REQUEST_ATR = VistaApplicationDispatcherFilter.class.getName();

    private boolean isDeploymentHttps = false;

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

        VistaURIDataResolver vistaURLDataResolver = new VistaURIDataResolver(request);

        HttpServletRequest httprequest = (HttpServletRequest) request;

//        log.info("Complete URL Request -> {}", vistaURLDataResolver.getCompleteURLWithContextPath());
//        log.info("Complete URL Request without context -> {}", vistaURLDataResolver.getCompleteURLNoContextPath());

        // For sample url -> http://vista-crm.dev.birchwoodsoftwaregroup.com:8888/vista/crm/tip.png?width=23
        String requestUri = httprequest.getRequestURI(); // sample: /vista/crm/tip.png
        String requestParams = httprequest.getQueryString(); // sample: width=23
        String requestPath = httprequest.getServletPath(); // sample: /crm/tip.png
        String serverName = httprequest.getServerName(); // sample: vista-crm.dev.birchwoodsoftwaregroup.com
        serverName = serverName.toLowerCase(Locale.ENGLISH);

        VistaApplication app = vistaURLDataResolver.getVistaApplication();

        //TODO BASED ON PMC and APP, DO FORWARD OR REDIRECT

        log.info(">>>>>>>>>>>>>>>>>>>> NAMESPACE: {} <<<<<<<<<<<<<<<<< ", vistaURLDataResolver.getVistaNamespace());

        if (app == null) {
            log.info("***ADF*** NOT forwarding");
            chain.doFilter(request, response);
        } else if (isDeploymentHttps && vistaURLDataResolver.isHttpsRedirectionNeeded()) {
            // TODO Redo and redirect only with information about PMC and APP
            String httpsUrl = vistaURLDataResolver.getHttpsUrl();
            log.info("***ADF*** redirecting. Change protocol from 'http' to 'https'. Sending redirect to \"{}\" to browser", httpsUrl);
            ((HttpServletResponse) response).sendRedirect(httpsUrl);
            return;
        } else if (app == VistaApplication.prospect && vistaURLDataResolver.isRootAppRequest()) {
            String urlToForward = vistaURLDataResolver.getCompleteURLToForward();
            log.info("***ADF*** redirecting. Sending redirect from '/prospect' to \"{}\" to browser", urlToForward);
            ((HttpServletResponse) response).sendRedirect(urlToForward);
            return;
        } else {
            httprequest.setAttribute(VistaApplication.class.getName(), app);
            String forwardedPath = vistaURLDataResolver.getPathToForwarded();
            httprequest.setAttribute(ServletUtils.x_forwarded_path, forwardedPath);
            String urlForward = vistaURLDataResolver.getNewURLRequest();
            log.info("***ADF*** \"{}\" forwarding to \"{}\" ", requestUri, urlForward);
            request.getRequestDispatcher(urlForward).forward(request, response);
        }
    }

}
