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
import com.propertyvista.domain.security.common.VistaApplication;

public class VistaApplicationDispatcherFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(VistaApplicationDispatcherFilter.class);

    private static String REQUEST_DISPATCHED_REQUEST_ATR = VistaApplicationDispatcherFilter.class.getName();

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
        VistaApplication app = null;
        String[] serverNameParts = serverName.split("\\.");
        if (serverNameParts.length > 0) {
            String appByDomain = serverNameParts[0];
            app = getAppByDomainOrPath(appByDomain, requestPath);
        }

        if (app != null) {
            httprequest.setAttribute(VistaApplication.class.getName(), app);
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

    private String getPathToForwarded(HttpServletRequest httprequest, VistaApplication app) {
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
        //String subRequestPath = VistaServerSideConfigurationDev.devContextLess ? "" : VistaServerSideConfigurationDev.devContextPath;
        String subRequestPath = "";

        if (app != VistaApplication.prospect) {
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

        // Domains type : http://PMC-XXX.dev.birchwoodsoftwaregroup.com:8888 and http://PMC-XXX-nn.birchwoodsoftwaregroup.com:8888
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
                app = VistaApplication.valueOf(appByDomainTokens[1]);
            } catch (IllegalArgumentException e) {
                // do noghing, app = null
            }
        }

        return app;
    }

}
