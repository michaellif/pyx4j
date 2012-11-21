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
package com.propertyvista.server.oapi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.gwt.server.ServletUtils;

public class OAPIFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(OAPIFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("Inside OAPIFilter.doFilter()");
        HttpServletRequest wrappedRequest = new SecurityWrapperHttpServletRequest((HttpServletRequest) request);
        log.info(HttpUtils.getRequestURL(wrappedRequest).toString());

        chain.doFilter(wrappedRequest, response);
    }

    private static class SecurityWrapperHttpServletRequest extends HttpServletRequestWrapper {

        public SecurityWrapperHttpServletRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getScheme() {
            return ServletUtils.getRequestProtocol((HttpServletRequest) super.getRequest());
        }

        @Override
        public String getServerName() {
            return ServletUtils.getRequestServerName((HttpServletRequest) super.getRequest());
        }

        @Override
        public int getServerPort() {
            String forwardedProtocol = ((HttpServletRequest) super.getRequest()).getHeader("x-forwarded-protocol");
            if (forwardedProtocol == null) {
                return super.getServerPort();
            } else {
                if ("http".equals(getScheme())) {
                    return 80;
                } else {
                    return 433;
                }
            }
        }

        @Override
        public String getContextPath() {
            return ServletUtils.getActualRequestContextPath((HttpServletRequest) super.getRequest(), "");
        }
    }
}
