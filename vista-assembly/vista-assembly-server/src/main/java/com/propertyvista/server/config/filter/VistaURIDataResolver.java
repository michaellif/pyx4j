/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 3, 2014
 * @author ernestog
 * @version $Id$
 */
package com.propertyvista.server.config.filter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.security.common.VistaApplication;

public class VistaURIDataResolver extends URIDataResolver<VistaApplication> {

    private static Logger log = LoggerFactory.getLogger(VistaURIDataResolver.class);

    private static final String regExTwoDigits = "\\d\\d";

    private final VistaURIData URIData;

    public VistaURIDataResolver(ServletRequest request) {
        this((HttpServletRequest) request);
    }

    public VistaURIDataResolver(HttpServletRequest httprequest) {
        super(httprequest);
        this.URIData = initilizeURLInfo();
    }

    public VistaURIData initilizeURLInfo() {
        VistaApplication app = getApplication();
        String namespace = getNamespace(null);

        return new VistaURIData(app, namespace);
    }

    /**
     * Internal use only. To get VistaNamespace object use getVistaNamespace() instead.
     */
    @Override
    public String getNamespace(HttpServletRequest httpRequest) {
        // TODO implement
        return "";
    }

    /**
     * Internal use only. To get VistaApplication object use getVistaApplication() instead.
     */
    @Override
    public VistaApplication getApplication() {
        return getAppByRequest();
    }

    // getters
    public VistaURIData vistaURIData() {
        return this.URIData;
    }

    public VistaApplication getVistaApplication() {
        return this.URIData.getApplication();
    }

    public String getVistaNamespace() {
        return this.URIData.getNamespace();
    }

    protected VistaApplication getAppByRequest() {

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

    /**
     * Gets application name based on domain name and request path
     *
     * @param appByDomain
     *            first part of domain name until first '.'
     * @param path
     *            request path for this request
     * @return Vista application
     */
    protected VistaApplication getAppByDomainOrPath(String appByDomain, String path) {
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

    public boolean isHttpsRedirectionNeeded() {
        boolean redirect = false;

        if (httpRequest.isSecure()) {
            return redirect;
        }

        VistaApplication app = getVistaApplication();
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
    public boolean isRootAppRequest() {
        String servletPath = httpRequest.getServletPath();

        if (servletPath.equals("") || servletPath.equals("/")
                || (servletPath.equals("/" + VistaApplication.prospect) && (getVistaApplication() == VistaApplication.prospect))) {
            return true;
        }
        return false;
    }

    /**
     * Builds the URL to forward to right Vista Application, adding request path
     *
     * @param httprequest
     * @param app
     * @return
     */
    public String getNewURLRequest() {
        String requestPath = httpRequest.getServletPath();
        VistaApplication app = getVistaApplication();

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

    public String getPathToForwarded() {
        // TODO what if ApplicationType = noApp?
        if (getVistaApplication() != VistaApplication.prospect) {
            return "/" + getVistaApplication().name();
        } else {
            return "";
        }
    }

    public String getCompleteURLToForward() {
        StringBuffer uri = new StringBuffer(getServerURL());
        uri.append(getNewURLRequest());

        String queryStr = httpRequest.getQueryString();
        if (queryStr != null && !queryStr.isEmpty()) {
            uri.append("?").append(queryStr);
        }
        return uri.toString();
    }

    protected String getServerURL() {
        return httpRequest.getScheme()
                + "://"
                + httpRequest.getServerName()
                + ("http".equals(httpRequest.getScheme()) && httpRequest.getServerPort() == 80 || "https".equals(httpRequest.getScheme())
                        && httpRequest.getServerPort() == 443 ? "" : ":" + httpRequest.getServerPort());
    }

    public String getCompleteURLNoContextPath() {
        return getCompleteURL(false);
    }

    public String getCompleteURLWithContextPath() {
        return getCompleteURL(true);
    }

    public String getCompleteURL(boolean returnWithContextPath) {
        log.info("requestUri -> " + httpRequest.getRequestURI());
        log.info("contextPath -> " + httpRequest.getContextPath());
        String requestUri = httpRequest.getRequestURI();
        if (!returnWithContextPath && requestUri != null) {
            String contextPath = httpRequest.getContextPath();
            if (contextPath != null) {
                requestUri = requestUri.replaceAll(contextPath, ""); // hack for duplicate context on request
                log.info("updatedRequestUri -> " + requestUri);
            }
        }

        return getServerURL() + requestUri + (httpRequest.getQueryString() != null ? "?" + httpRequest.getQueryString() : "");
    }

    public String getHttpsUrl() {
        return new StringBuffer(getCompleteURLNoContextPath()).replace(0, 4, "https").toString();
    }

}
