/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.security.openId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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
import com.pyx4j.server.contexts.AntiDoS;
import com.pyx4j.server.contexts.DevSession;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.server.common.security.DevelopmentSecurity;

public class OpenIdFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(OpenIdFilter.class);

    static String ACCESS_GRANTED_ATTRIBUTE = DevelopmentSecurity.OPENID_ACCESS_GRANTED_ATTRIBUTE;

    static String REQUESTED_URL_ATTRIBUTE = "access-requested";

    private static boolean enabled;

    private final Collection<String> servletPathNoAuthentication = new HashSet<String>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        enabled = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).openIdRequired();
        createDefaultOpenPathMap();
        if (!ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).openIdRequiredMedia()) {
            servletPathNoAuthentication.add(pathNoSlach(DeploymentConsts.mediaImagesServletMapping));
        }
    }

    void createDefaultOpenPathMap() {
        servletPathNoAuthentication.addAll(allApplicationsUrls("o"));
        servletPathNoAuthentication.addAll(allApplicationsUrls("public"));
        servletPathNoAuthentication.addAll(allApplicationsUrls("debug"));
        servletPathNoAuthentication.add("o");
        servletPathNoAuthentication.add("public");
        servletPathNoAuthentication.add("static");
        servletPathNoAuthentication.add("interfaces");
    }

    private String pathNoSlach(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        } else {
            return url;
        }
    }

    private static Collection<String> allApplicationsUrls(String url) {
        ArrayList<String> arlPatterns = new ArrayList<String>();
        for (VistaApplication application : VistaApplication.values()) {
            arlPatterns.add(application.name() + "/" + url);
        }
        return arlPatterns;
    }

    @Override
    public void destroy() {
    }

    private boolean noAuthenticationRequired(String servletPath) {
        String servletPathParts[] = servletPath.split("/");
        if ((servletPathParts.length >= 2) && servletPathNoAuthentication.contains(servletPathParts[1])) {
            return true;
        } else if ((servletPathParts.length >= 3) && servletPathNoAuthentication.contains(servletPathParts[1] + "/" + servletPathParts[2])) {
            return true;
        } else if (servletPath.endsWith("/robots.txt") || servletPath.endsWith("/favicon.ico")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        DevSession devSession = null;
        if (enabled) {
            devSession = DevSession.getSession();
            if (devSession.getAttribute(ACCESS_GRANTED_ATTRIBUTE) == Boolean.TRUE) {
                chain.doFilter(request, response);
            } else {
                HttpServletRequest httprequest = (HttpServletRequest) request;
                String servletPath = httprequest.getServletPath();
                if (noAuthenticationRequired(servletPath)) {
                    chain.doFilter(request, response);
                } else if (httprequest.getRequestURI().endsWith(".js") || httprequest.getRequestURI().contains("/srv/")) {
                    ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                } else {
                    String receivingURL = httprequest.getRequestURL().toString();
                    String query = httprequest.getQueryString();
                    if (query != null && query.length() > 0) {
                        receivingURL += "?" + query;
                    }

                    log.debug("authentication required for ServletPath [{}] [{}]", httprequest.getServletPath(), receivingURL);
                    if (!devSession.isAlive()) {
                        devSession = DevSession.beginSession();
                    }
                    if (devSession.getAttribute(REQUESTED_URL_ATTRIBUTE) == null) {
                        if (!receivingURL.endsWith("gif") && !receivingURL.endsWith("png") && !receivingURL.endsWith(".siteimgrc")
                                && !receivingURL.contains("/o/openid/")) {
                            devSession.setAttribute(REQUESTED_URL_ATTRIBUTE, receivingURL);
                        }
                    }
                    OpenIdServlet.createResponsePage(
                            httprequest,
                            (HttpServletResponse) response,
                            true,
                            OpenId.getDestinationUrl(ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).openIdDomain(),
                                    ServletUtils.getRequestBaseURL(httprequest)));

                }
            }

        } else {
            chain.doFilter(request, response);
        }

        if (devSession != null) {
            String email = (String) devSession.getAttribute(DevelopmentSecurity.OPENID_USER_EMAIL_ATTRIBUTE);
            if ((email != null) && email.startsWith("tester")) {
                AntiDoS.resetRequestCount(request);
            }
        }

    }
}
