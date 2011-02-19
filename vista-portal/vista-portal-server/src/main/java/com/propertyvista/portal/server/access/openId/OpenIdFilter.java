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
package com.propertyvista.portal.server.access.openId;

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

import com.propertyvista.portal.server.VistaServerSideConfiguration;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.security.shared.SecurityController;

public class OpenIdFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(OpenIdFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (SecurityController.checkBehavior(CoreBehavior.USER)) {
            chain.doFilter(request, response);
        } else {
            HttpServletRequest httprequest = (HttpServletRequest) request;
            if ((!((VistaServerSideConfiguration) ServerSideConfiguration.instance()).openIdrequired()) || httprequest.getServletPath().startsWith("/o/")) {
                chain.doFilter(request, response);
            } else {
                log.debug("authentication required for ServletPath [{}]", httprequest.getServletPath());
                OpenIdServlet.createResponsePage((HttpServletResponse) response, true, "Login via Google Apps", OpenId.getDestinationUrl(OpenIdServlet.DOMAIN));
            }
        }
    }

}
