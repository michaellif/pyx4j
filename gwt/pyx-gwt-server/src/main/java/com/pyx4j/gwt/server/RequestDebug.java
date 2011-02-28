/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2010-07-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.server;

import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestDebug {

    private final static Logger log = LoggerFactory.getLogger(RequestDebug.class);

    public static void debug(ServletRequest request) {
        StringBuilder buf = new StringBuilder();
        buf.append('\n');
        buf.append("protocol           ").append(request.getProtocol()).append('\n');
        buf.append("scheme             ").append(request.getScheme()).append('\n');
        buf.append("serverName         ").append(request.getServerName()).append('\n');
        buf.append("serverPort         ").append(request.getServerPort()).append('\n');
        buf.append("localAddr          ").append(request.getLocalAddr()).append('\n');
        buf.append("localPort          ").append(request.getLocalPort()).append('\n');
        buf.append("remoteAddr         ").append(request.getRemoteAddr()).append('\n');
        buf.append("remotePort         ").append(request.getRemotePort()).append('\n');

        buf.append("contentLength      ").append(request.getContentLength()).append('\n');
        buf.append("contentType        ").append(request.getContentType()).append('\n');
        buf.append("characterEncoding  ").append(request.getCharacterEncoding()).append('\n');

        if (request instanceof HttpServletRequest) {
            HttpServletRequest hrequest = (HttpServletRequest) request;
            buf.append("method             ").append(hrequest.getMethod()).append('\n');
            buf.append("contextPath        ").append(hrequest.getContextPath()).append('\n');
            buf.append("pathInfo           ").append(hrequest.getPathInfo()).append('\n');
            buf.append("requestURI         ").append(hrequest.getRequestURI()).append('\n');
            buf.append("requestURL         ").append(hrequest.getRequestURL().toString()).append('\n');
            buf.append("serverName         ").append(hrequest.getServerName()).append('\n');
            buf.append("servletPath        ").append(hrequest.getServletPath()).append('\n');
            buf.append("queryString        ").append(hrequest.getQueryString()).append('\n');
            buf.append("remoteUser         ").append(hrequest.getRemoteUser()).append('\n');
            buf.append("requestedSessionId ").append(hrequest.getRequestedSessionId()).append('\n');

            buf.append("HTTP Headers:\n");
            for (Enumeration<?> en = hrequest.getHeaderNames(); en.hasMoreElements();) {
                String name = (String) en.nextElement();
                buf.append('\t').append(name).append('=');
                buf.append(hrequest.getHeader(name));
                buf.append('\n');
            }

            Cookie cookies[] = hrequest.getCookies();
            for (int i = 0; (cookies != null) && (i < cookies.length); i++) {
                buf.append("cookie ").append(cookies[i].getName()).append('=').append(cookies[i].getValue()).append('\n');
            }
        }

        buf.append("Parameters:\n");
        for (Enumeration<?> en = request.getParameterNames(); en.hasMoreElements();) {
            String name = (String) en.nextElement();
            buf.append('\t').append(name).append('=');
            String[] arr = request.getParameterValues(name);
            if (arr != null) {
                for (int i = 0; i < arr.length; i++) {
                    buf.append(arr[i]);
                }
            } else {
                buf.append("{null}");
            }
            buf.append('\n');
        }
        log.debug("{}", buf.toString());
    }
}
