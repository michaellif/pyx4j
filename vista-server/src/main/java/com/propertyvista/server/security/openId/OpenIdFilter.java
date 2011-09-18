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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.server.dev.DevSession;
import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.server.contexts.AntiDoS;

import com.propertyvista.server.common.security.DevelopmentSecurity;
import com.propertyvista.server.config.VistaServerSideConfiguration;

public class OpenIdFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(OpenIdFilter.class);

    static String ACCESS_GRANTED_ATTRIBUTE = DevelopmentSecurity.OPENID_ACCESS_GRANTED_ATTRIBUTE;

    static String REQUESTED_URL_ATTRIBUTE = "access-requested";

    private boolean enabled;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        enabled = ((VistaServerSideConfiguration) ServerSideConfiguration.instance()).openIdrequired();
    }

    @Override
    public void destroy() {
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
                if (servletPath.startsWith("/o/") || servletPath.startsWith("/public/") || servletPath.equals("/favicon.ico")) {
                    chain.doFilter(request, response);
                } else if (httprequest.getRequestURI().endsWith(".js") || httprequest.getRequestURI().contains("/srv/")) {
                    ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                } else {
                    String receivingURL = ServletUtils.getActualRequestURL(httprequest, true);
                    if (!receivingURL.startsWith(ServerSideConfiguration.instance().getMainApplicationURL())
                            && (!receivingURL.matches("http://\\w+\\.dev\\.birchwoodsoftwaregroup\\.com:\\d+/.+"))) {
                        StringBuffer properUrl = new StringBuffer(ServerSideConfiguration.instance().getMainApplicationURL());
                        properUrl.append(servletPath.substring(1));
                        if (CommonsStringUtils.isStringSet(httprequest.getQueryString())) {
                            properUrl.append("?").append(httprequest.getQueryString());
                        }
                        ((HttpServletResponse) response).sendRedirect(properUrl.toString());
                    } else {
                        log.debug("authentication required for ServletPath [{}] [{}]", httprequest.getServletPath(), receivingURL);
                        if (!devSession.isAlive()) {
                            devSession = DevSession.beginSession();
                        }
                        if (devSession.getAttribute(REQUESTED_URL_ATTRIBUTE) == null) {
                            devSession.setAttribute(REQUESTED_URL_ATTRIBUTE, receivingURL);
                        }
                        OpenIdServlet.createResponsePage((HttpServletResponse) response, true, "Login via Google Apps",
                                OpenId.getDestinationUrl(OpenIdServlet.DOMAIN, receivingURL));

                    }
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
