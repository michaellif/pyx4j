/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.config.server.ServerSideConfiguration;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;

/**
 * Avoid in browser cash of index.html for GWT applications when secondary login is enabled
 */
public class GWTCacheFilter extends com.pyx4j.gwt.server.GWTCacheFilter {

    private boolean openIdEnabled;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        openIdEnabled = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).openIdRequired();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (openIdEnabled) {
            if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
                String servletPath = ((HttpServletRequest) request).getServletPath();
                if (servletPath.equals("/crm/")) {
                    ((HttpServletResponse) response).setDateHeader("Expires", System.currentTimeMillis());
                    ((HttpServletResponse) response).setHeader("Pragma", "no-cache");
                    ((HttpServletResponse) response).setHeader("Cache-control", "no-cache, no-store, must-revalidate");
                    chain.doFilter(request, response);
                    return;
                }
            }
        }
        super.doFilter(request, response, chain);
    }
}
