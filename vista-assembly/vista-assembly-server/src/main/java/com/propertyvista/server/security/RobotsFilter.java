/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.shared.config.VistaDemo;

public class RobotsFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(RobotsFilter.class);

    private static final List<String> agentsPaterns = Arrays.asList("googlebot", "crawl", "spider", "msnbot", "bingbot", "twitterbot");

    private boolean enabled;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        enabled = VistaDemo.isDemo() || ApplicationMode.isDevelopment();
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (enabled && (request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            String servletPath = ((HttpServletRequest) request).getServletPath();
            if (servletPath.endsWith("robots.txt")) {
                log.warn("robots access blocked in development");
                request.getServletContext().getRequestDispatcher("/robots-disallow.txt").forward(request, response);
                return;
            }
            String userAgent = ((HttpServletRequest) request).getHeader("User-Agent");
            if (userAgent != null) {
                userAgent = userAgent.toLowerCase(Locale.ENGLISH);
                for (String agentsPatern : agentsPaterns) {
                    if (userAgent.contains(agentsPatern)) {
                        log.warn("robots access blocked in development");
                        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_GONE);
                        return;
                    }
                }
            }

        }
        chain.doFilter(request, response);
    }

}
