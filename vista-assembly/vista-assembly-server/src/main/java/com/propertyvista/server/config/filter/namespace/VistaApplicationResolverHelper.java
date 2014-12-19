/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter.namespace;

import javax.servlet.http.HttpServletRequest;

import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.server.config.filter.utils.HttpRequestUtils;

public class VistaApplicationResolverHelper {
    private static final String regExTwoDigits = "\\d\\d";

    public static VistaApplication getApplication(HttpServletRequest httpRequest) {

        VistaApplication app = null;

        String serverName = httpRequest.getServerName();
        String requestPath = httpRequest.getServletPath();
        String rootServletPath = HttpRequestUtils.getRootServletPath(httpRequest);

        try {
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            app = CacheService.get(HttpRequestUtils.getAppCacheKey(httpRequest));

            if (app != null) {
                return app;
            } else {
                String[] serverNameParts = serverName.split("\\.");
                if (serverNameParts.length > 0) {
                    String appByDomain = serverNameParts[0];
                    app = getAppByDomainOrPath(appByDomain, requestPath, rootServletPath);
                }

                CacheService.put(HttpRequestUtils.getAppCacheKey(httpRequest), app);

                return app;
            }
        } finally {
            NamespaceManager.remove();
        }

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
    private static VistaApplication getAppByDomainOrPath(String appByDomain, String path, String rootServletPath) {
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

                // If request path starts with "/prospect", portal is prospect
                if (rootServletPath.equalsIgnoreCase("prospect")) {
                    return VistaApplication.prospect;
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

    public static boolean isHttpsRedirectionNeeded(HttpServletRequest httpRequest) {
        VistaApplication app = getApplication(httpRequest);
        return isHttpsRedirectionNeeded(httpRequest, app);
    }

    public static boolean isHttpsRedirectionNeeded(HttpServletRequest httpRequest, VistaApplication app) {

        boolean redirect = false;

        if (httpRequest.isSecure()) {
            return redirect;
        }

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
    public static boolean isRootAppRequest(HttpServletRequest httpRequest, VistaApplication app) {
        String servletPath = httpRequest.getServletPath();

        if (servletPath.equals("") || servletPath.equals("/") || (servletPath.equals("/" + VistaApplication.prospect) && (app == VistaApplication.prospect))) {
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
    public static String getNewURLRequest(HttpServletRequest httpRequest, VistaApplication app) {
        String requestPath = httpRequest.getServletPath();

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

        if (httpRequest.getPathInfo() != null) {
            newUri += httpRequest.getPathInfo();
        }

        return newUri;
    }

    public static String getCompleteURLToForward(HttpServletRequest httpRequest, VistaApplication app) {
        StringBuffer uri = new StringBuffer(HttpRequestUtils.getServerURL(httpRequest));
        uri.append(getNewURLRequest(httpRequest, app));

        String queryStr = httpRequest.getQueryString();
        if (queryStr != null && !queryStr.isEmpty()) {
            uri.append("?").append(queryStr);
        }
        return uri.toString();
    }

    public static String getPathToForwarded(VistaApplication app) {
        // TODO what if ApplicationType = noApp?
        if (app != VistaApplication.prospect) {
            return "/" + app.name();
        } else {
            return "";
        }
    }

}
