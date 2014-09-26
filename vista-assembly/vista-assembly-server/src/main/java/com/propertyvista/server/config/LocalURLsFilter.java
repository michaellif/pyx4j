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

import com.propertyvista.domain.security.common.VistaApplication;

public class LocalURLsFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(LocalURLsFilter.class);

    private FilterConfig filterConfig = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        boolean FILTER_ACTIVATED = false;

        if (FILTER_ACTIVATED) {
            // Do something
            map(request, response, chain);
        } else {
            // Do nothing. Let chain continue...
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

        if (serverName.equals("localhost") || isAlreadyMapped(requestPath)) {
            // Request is already mapped (I think this case never happens... Stil have to think about this)
            chain.doFilter(request, response);
        } else {

            // Redirect requests
            String[] serverNameParts = serverName.split("\\.");
            if (serverNameParts.length > 0) {
                String appByDomain = serverNameParts[0];

                VistaApplication app = getAppByDomainOrPath(appByDomain, requestPath);
                if (app != null) {
                    String urlForward = getNewURLRequest(httprequest, app);
                    log.info("forwarding to: " + "\"" + urlForward + "\"");
                    request.getRequestDispatcher(urlForward).forward(request, response);
                }

            }
        }
    }

//    Sample to build the complete URI from from request/httprequest object
//    String newUrl = httprequest.getScheme()
//              + "://"
//              + request.getServerName() +
//              ("http".equals(httprequest.getScheme()) && httprequest.getServerPort() == 80 || "https".equals(httprequest.getScheme())
//                      && httprequest.getServerPort() == 443 ? "" : ":" + httprequest.getServerPort()) + httprequest.getRequestURI()
//              + (httprequest.getQueryString() != null ? "?" + httprequest.getQueryString() : "");
//    }

//    Sample to wrap Request and change some param
//    public class MyRequest extends HttpServletRequestWrapper {
//
//        public MyRequest(HttpServletRequest request) {
//            super(request);
//        }
//
//        @Override
//        public String getContextPath() {
//            return "/vista/crm/"; // TODO: implement accordingly.
//        }
//
//        @Override
//        public String getRequestURI() {
//            return "/vista"; // TODO: implement accordingly.
//        }
//
//    }

    /**
     * Builds the URL to forward to right Vista Application, adding request path
     *
     * @param httprequest
     * @param app
     * @return
     */
    private String getNewURLRequest(HttpServletRequest httprequest, VistaApplication app) {
        String requestPath = httprequest.getServletPath();
        String subRequestPath = "/" + app.toString() + requestPath;
        String newUri = subRequestPath + (httprequest.getQueryString() != null ? "?" + httprequest.getQueryString() : "");

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
        String[] appByDomainTokens = appByDomain.split("-");
        VistaApplication app = null;

        // Domains type : http://XXX.dev.birchwoodsoftwaregroup.com:8888
        if (appByDomainTokens.length == 1) {
            try {
                app = VistaApplication.valueOf(appByDomain);
            } catch (IllegalArgumentException e) {
                // do noghing, app = null
            }
        }

        // Domains type : http://PMC-XXX.dev.birchwoodsoftwaregroup.com:8888
        if (appByDomainTokens.length >= 2) {
            if (appByDomainTokens[1].equalsIgnoreCase("portal")) {
                String[] appByPathTokens = path.split("/");
                // If request path starts with "/prospect", portal is prospect
                if (appByPathTokens.length >= 1) {
                    if (appByPathTokens[0].equalsIgnoreCase("prospect")) {
                        return VistaApplication.prospect;
                    }
                }

                // Default "portal" application is "resident" (no request path required)
                return VistaApplication.resident;
            }

            try {
                app = VistaApplication.valueOf(appByDomainTokens[1]);
            } catch (IllegalArgumentException e) {
                // do noghing, app = null
            }
        }

        return app;
    }

    /**
     * Checks if string path contains some application mapping
     *
     * @param requestPath
     * @return true if has been already mapped. False otherwise.
     */
    private boolean isAlreadyMapped(String requestPath) {

        String[] pathTokens = requestPath.split("/");

        if (pathTokens.length == 0) {
            return false;
        } else {
            String app = pathTokens[1];
            try {
                if (VistaApplication.valueOf(app) != null) {
                    return true;
                }
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        return false;
    }

}
