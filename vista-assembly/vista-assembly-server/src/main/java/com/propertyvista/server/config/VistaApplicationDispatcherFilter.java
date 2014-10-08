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
package com.propertyvista.server.config;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.ServletUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;

public class VistaApplicationDispatcherFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(VistaApplicationDispatcherFilter.class);

    private static String REQUEST_DISPATCHED_REQUEST_ATR = VistaApplicationDispatcherFilter.class.getName();

    public enum ApplicationType {

        development,

        operations,

        crm,

        site,

        resident,

        prospect,

        onboarding;

    }

    private static final String regExTwoDigits = "\\d\\d";

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

        // sample url -> http://vista-crm.dev.birchwoodsoftwaregroup.com:8888/vista/crm/tip.png?width=23

        String requestUri = httprequest.getRequestURI(); // sample: /vista/crm/tip.png
        String requestParams = httprequest.getQueryString(); // sample: width=23
        String requestPath = httprequest.getServletPath(); // sample: /crm/tip.png
        String serverName = httprequest.getServerName(); // sample: vista-crm.dev.birchwoodsoftwaregroup.com
        serverName = serverName.toLowerCase(Locale.ENGLISH);

        // Redirect requests
        ApplicationType app = null;
        String[] serverNameParts = serverName.split("\\.");
        if (serverNameParts.length > 0) {
            String appByDomain = serverNameParts[0];
            app = getAppByDomainOrPath(appByDomain, requestPath);
        }

        if (app != null) {
            httprequest.setAttribute(ApplicationType.class.getName(), app);
            String forwardedPath = getPathToForwarded(httprequest, app);
            httprequest.setAttribute(ServletUtils.x_forwarded_path, forwardedPath);
            String urlForward = getNewURLRequest(httprequest, app);
            log.info("***ADF*** \"{}\" forwarding to \"{}\" ", requestUri, urlForward);
            request.getRequestDispatcher(urlForward).forward(request, response);
        } else {
            log.info("***ADF*** NOT forwarding");
            chain.doFilter(request, response);
        }

    }

    private String getPathToForwarded(HttpServletRequest httprequest, ApplicationType app) {
        // TODO what if ApplicationType = development?
        if (app != ApplicationType.prospect) {
            return "/" + app.name();
        } else {
            return "";
        }
    }

    /**
     * Builds the URL to forward to right Vista Application, adding request path
     *
     * @param httprequest
     * @param app
     * @return
     */
    private String getNewURLRequest(HttpServletRequest httprequest, ApplicationType app) {
        String requestPath = httprequest.getServletPath();
        //String subRequestPath = VistaServerSideConfigurationDev.devContextLess ? "" : VistaServerSideConfigurationDev.devContextPath;
        String subRequestPath = "";

        if ((app == ApplicationType.site) && (!httprequest.getRequestURI().equalsIgnoreCase("/"))) {
            String requestUri = httprequest.getRequestURI();
            if (requestUri.startsWith("/vista/site")) {
                requestPath = requestUri.replaceFirst("/vista/site", "");
            }
        }

        if (app != ApplicationType.prospect && app != ApplicationType.development) {
            subRequestPath += "/" + app.toString();
        }

        subRequestPath += requestPath;

        String newUri = subRequestPath;

        return newUri;
    }

    /**
     * Gets application name based on domain name and request path
     *
     * @param appByDomain
     *            first part of domain name until first '.'
     * @param path
     *            request path for this request
     * @return Vista application
     */
    private ApplicationType getAppByDomainOrPath(String appByDomain, String path) {
        String[] appByDomainTokens = appByDomain.split("-");
        ApplicationType app = null;

        // Domains type : http://XXX.dev.birchwoodsoftwaregroup.com:8888
        if (appByDomainTokens.length == 1) {
            if (appByDomain.equalsIgnoreCase("static")) {
                return ApplicationType.development;
            }

            try {
                app = ApplicationType.valueOf(appByDomain);
            } catch (IllegalArgumentException e) {

            }
        }

        // Domains type : http://PMC-XXX.dev.birchwoodsoftwaregroup.com:8888,
        //http://XXX-nn.dev.birchwoodsoftwaregroup.com:8888 and
        //http://PMC-XXX-nn.birchwoodsoftwaregroup.com:8888
        if (appByDomainTokens.length >= 2) {
            if (appByDomainTokens[1].equalsIgnoreCase("portal")) {
                String[] appByPathTokens = path.split("/");
                // If request path starts with "/prospect", portal is prospect
                if (appByPathTokens.length >= 2) {
                    if (appByPathTokens[1].equalsIgnoreCase("prospect")) {
                        return ApplicationType.prospect;
                    }
                }

                // Default "portal" application is "resident" (no request path required)
                return ApplicationType.resident;
            }

            try {
                int index = appByDomainTokens[1].matches(regExTwoDigits) ? 0 : 1;
                if (appByDomainTokens[index].equalsIgnoreCase("static")) {
                    return ApplicationType.development;
                }
                app = ApplicationType.valueOf(appByDomainTokens[index]);
            } catch (IllegalArgumentException e) {
                // do nothing, app = null
            }

        }

        return app;
    }

}
