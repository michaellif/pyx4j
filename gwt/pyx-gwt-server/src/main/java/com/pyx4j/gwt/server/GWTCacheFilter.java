/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 27, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.commons.Consts;

public class GWTCacheFilter implements Filter {

    private int cacheExpiresHours = 24;

    private String cacheControl;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String rate = filterConfig.getInitParameter("cacheExpiresHours");
        if (rate != null) {
            cacheExpiresHours = Integer.parseInt(rate);
        }
        cacheControl = "public, max-age=" + ((long) Consts.HOURS2SEC * cacheExpiresHours);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
            String uri = ((HttpServletRequest) request).getRequestURI();
            if (uri.indexOf(".cache.") != -1) {
                ((HttpServletResponse) response).setDateHeader("Expires", System.currentTimeMillis() + Consts.HOURS2MSEC * cacheExpiresHours);
                ((HttpServletResponse) response).setHeader("Cache-control", cacheControl);
            } else if ((uri.indexOf(".nocache.") != -1) || (uri.indexOf("test") != -1)) {
                ((HttpServletResponse) response).setDateHeader("Expires", System.currentTimeMillis());
                ((HttpServletResponse) response).setHeader("Pragma", "no-cache");
                ((HttpServletResponse) response).setHeader("Cache-control", "no-cache, no-store, must-revalidate");
            }
        }
        chain.doFilter(request, response);
    }

}
