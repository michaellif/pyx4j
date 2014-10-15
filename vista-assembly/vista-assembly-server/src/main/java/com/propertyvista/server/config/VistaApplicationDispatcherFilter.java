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

    private static final String regExTwoDigits = "\\d\\d";

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

    private String getHttpsUrl(StringBuffer url) {
        return url.replace(0, 4, "https").toString();
    }

    public void map(final ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httprequest = (HttpServletRequest) request;

        // sample url -> http://vista-crm.dev.birchwoodsoftwaregroup.com:8888/vista/crm/tip.png?width=23

        String requestUri = httprequest.getRequestURI(); // sample: /vista/crm/tip.png
        String requestParams = httprequest.getQueryString(); // sample: width=23
        String requestPath = httprequest.getServletPath(); // sample: /crm/tip.png
        String serverName = httprequest.getServerName(); // sample: vista-crm.dev.birchwoodsoftwaregroup.com
        serverName = serverName.toLowerCase(Locale.ENGLISH);

        VistaApplication app = getAppByRequest(httprequest);

        //TODO BASED ON PMC and APP, DO FORWARD OR REDIRECT

        if (app == null) {
            log.info("***ADF*** NOT forwarding");
            chain.doFilter(request, response);
        } else if (isDeploymentHttps && isHttpsRedirectionNeeded(request)) {
            // TODO Redo and redirect only with information about PMC and APP
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String uri = getCompleteURLNoContextPath(httpRequest);
            String httpsUrl = getHttpsUrl(new StringBuffer(uri));
            log.info("***ADF*** redirecting. Sending redirect from 'http' to '{}' to browser", httpsUrl);
            ((HttpServletResponse) response).sendRedirect(httpsUrl);
            return;
        } else if (app == VistaApplication.prospect && isRootAppRequest(httprequest)) {
            String pathToForward = getNewURLRequest(httprequest, app);
            String urlToForward = getCompleteURLToForward(httprequest, pathToForward);
            log.info("***ADF*** redirecting. Sending redirect from '/prospect' to  to '{}' to browser", urlToForward);
            ((HttpServletResponse) response).sendRedirect(urlToForward);
            return;
        } else {
            httprequest.setAttribute(VistaApplication.class.getName(), app);
            String forwardedPath = getPathToForwarded(httprequest, app);
            httprequest.setAttribute(ServletUtils.x_forwarded_path, forwardedPath);
            String urlForward = getNewURLRequest(httprequest, app);
            log.info("***ADF*** \"{}\" forwarding to \"{}\" ", requestUri, urlForward);
            request.getRequestDispatcher(urlForward).forward(request, response);
        }
    }

    public String getCompleteURLNoContextPath(HttpServletRequest httpRequest) {
        return getCompleteURL(httpRequest, false);
    }

    public String getCompleteURL(HttpServletRequest httpRequest, boolean returnWithContextPath) {
        String requestUri = httpRequest.getRequestURI();
        if (!returnWithContextPath) {
            String contextPath = httpRequest.getContextPath();
            requestUri = requestUri.replaceFirst(contextPath, "");
        }

        return getServerURL(httpRequest) + requestUri + (httpRequest.getQueryString() != null ? "?" + httpRequest.getQueryString() : "");
    }

    public String getCompleteURLToForward(HttpServletRequest request, String forwardPath) {
        String uri = getServerURL(request);
        uri += forwardPath;
        return uri;
    }

    public String getServerURL(HttpServletRequest httpRequest) {
        return httpRequest.getScheme()
                + "://"
                + httpRequest.getServerName()
                + ("http".equals(httpRequest.getScheme()) && httpRequest.getServerPort() == 80 || "https".equals(httpRequest.getScheme())
                        && httpRequest.getServerPort() == 443 ? "" : ":" + httpRequest.getServerPort());
    }

    public boolean isHttpsRedirectionNeeded(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        boolean redirect = false;

        if (httpRequest.isSecure()) {
            return redirect;
        }

        VistaApplication app = getAppByRequest(httpRequest);
        if (app != null && app != VistaApplication.site && app != VistaApplication.noApp) {
            redirect = true;
        }

        return redirect;
    }

    /**
     * Checks if it is first request to app (request to root app or only context present in requestURI)
     *
     * @param request
     * @return true if request is first request; false otherwise
     */
    private boolean isRootAppRequest(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if (servletPath.equals("") || servletPath.equals("/")
                || (servletPath.equals("/" + VistaApplication.prospect) && (getAppByRequest(request) == VistaApplication.prospect))) {
            return true;
        }
        return false;
    }

    private boolean isHttp(HttpServletRequest request) {
        return request.getScheme().equalsIgnoreCase("http");
    }

    private VistaApplication getAppByRequest(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        return getAppByRequest(httpRequest);
    }

    private VistaApplication getAppByRequest(HttpServletRequest httpRequest) {

        String serverName = httpRequest.getServerName();
        String requestPath = httpRequest.getServletPath();

        VistaApplication app = null;

        String[] serverNameParts = serverName.split("\\.");
        if (serverNameParts.length > 0) {
            String appByDomain = serverNameParts[0];
            app = getAppByDomainOrPath(appByDomain, requestPath);
        }

        return app;
    }

    private String getPathToForwarded(HttpServletRequest httprequest, VistaApplication app) {
        // TODO what if ApplicationType = noApp?
        if (app != VistaApplication.prospect) {
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
    private String getNewURLRequest(HttpServletRequest httprequest, VistaApplication app) {
        String requestPath = httprequest.getServletPath();
        String subRequestPath = "";

        if (app != VistaApplication.prospect && app != VistaApplication.noApp) {
            subRequestPath += "/" + app.toString();
        }

        subRequestPath += requestPath;

        // Hack in case of first request to Prospect app (default behavior reading directories different in tomcat than jetty)
        if (app == VistaApplication.prospect && requestPath.equalsIgnoreCase("/" + VistaApplication.prospect.name())) {
            subRequestPath += "/";
        }

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
    private VistaApplication getAppByDomainOrPath(String appByDomain, String path) {
        // TODO Extract method to a new class. Create new return type including data from VistaApplication and PMC. Redo vistaNameSpace resolver (common tasks)
        String[] appByDomainTokens = appByDomain.split("-");
        VistaApplication app = null;

        // Domains type : http://XXX.dev.birchwoodsoftwaregroup.com:8888
        if (appByDomainTokens.length == 1) {
            if (appByDomain.equalsIgnoreCase("static")) {
                return VistaApplication.noApp;
            }

            try {
                app = VistaApplication.valueOf(appByDomain);
            } catch (IllegalArgumentException e) {
                // do nothing, app = null
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
                        return VistaApplication.prospect;
                    }
                }

                // Default "portal" application is "resident" (no request path required)
                return VistaApplication.resident;
            }

            try {
                int index = appByDomainTokens[1].matches(regExTwoDigits) ? 0 : 1;
                if (appByDomainTokens[index].equalsIgnoreCase("static")) {
                    return VistaApplication.noApp;
                }
                app = VistaApplication.valueOf(appByDomainTokens[index]);
            } catch (IllegalArgumentException e) {
                // do nothing, app = null
            }

        }

        return app;
    }

}
