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

import com.pyx4j.gwt.server.ServletUtils;

public class OAPIFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest wrappedRequest = new SecurityWrapperHttpServletRequest((HttpServletRequest) request);
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
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
            if (true) {
                return ServletUtils.getActualRequestContextPath((HttpServletRequest) super.getRequest());
            }
            // Call to https://static-22.birchwoodsoftwaregroup.com/interfaces/oapi/debug/PropertyService?wsdl
            // Should return proper location value
            // Fixed com.sun.xml.ws.transport.http.servlet.ServletConnectionImpl.getBaseAddress();
            StackTraceElement[] ste = new Throwable().getStackTrace();
            String firstRunnableMethod = (ste[1]).getMethodName();
            if (firstRunnableMethod.equals("getBaseAddress")) {
                return ServletUtils.getActualRequestContextPath((HttpServletRequest) super.getRequest());
            } else {
                return super.getContextPath();
            }
        }
    }
}
