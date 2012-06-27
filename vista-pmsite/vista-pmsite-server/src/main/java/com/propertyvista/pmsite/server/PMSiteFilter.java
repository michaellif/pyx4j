/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 27, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PMSiteFilter extends WicketFilter {

    private static final Logger log = LoggerFactory.getLogger(PMSiteFilter.class);

    private final String IGNORE_URLS_PARAM = "ignoreUrls";

    private final ArrayList<String> ignoreUrls = new ArrayList<String>();

    @Override
    public void init(final boolean isServlet, final FilterConfig filterConfig) throws ServletException {
        super.init(isServlet, filterConfig);
        initIgnoreUrls(filterConfig);
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (shouldIgnoreUrl((HttpServletRequest) request)) {
            log.trace("Ignoring request {}", ((HttpServletRequest) request).getRequestURL());
            chain.doFilter(request, response);
        } else {
            super.doFilter(request, response, chain);
        }
    }

    private boolean shouldIgnoreUrl(final HttpServletRequest request) {
        if (ignoreUrls.size() < 1) {
            return false;
        }
        String url = getRelativePath(request);
        if (Strings.isEmpty(url)) {
            return false;
        }
        url = "/" + url;
        for (String pattern : ignoreUrls) {
            if (url.matches(pattern)) {
                return true;
            }
        }

        return false;
    }

    private void initIgnoreUrls(final FilterConfig filterConfig) {
        String urls = filterConfig.getInitParameter(IGNORE_URLS_PARAM);
        if (Strings.isEmpty(urls) == false) {
            String[] parts = Strings.split(urls, ',');
            for (String url : parts) {
                // make a regex from servlet path pattern
                String regex = url.trim().replace(".", "\\.").replace("*", ".*");
                if (regex.length() > 0) {
                    ignoreUrls.add(regex);
                }
            }
        }
    }
}
