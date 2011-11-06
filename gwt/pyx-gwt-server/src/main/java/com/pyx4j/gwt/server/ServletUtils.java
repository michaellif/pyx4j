/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-02-21
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.gwt.server;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class ServletUtils {

    public static String getRequestServerName(HttpServletRequest request) {
        String host = request.getHeader("x-forwarded-host");
        if (host != null) {
            return host;
        } else {
            return request.getServerName();
        }
    }

    public static String getActualRequestURL(HttpServletRequest request) {
        return getActualRequestURL(request, false);
    }

    public static String getActualRequestURL(HttpServletRequest request, boolean queryString) {
        StringBuffer receivingURL;
        String forwarded = request.getHeader("x-forwarded-host");
        if (forwarded != null) {
            receivingURL = new StringBuffer();
            receivingURL.append("http://").append(forwarded);
            String forwardedContext = request.getHeader("x-forwarded-context");
            if (forwardedContext != null) {
                receivingURL.append(request.getRequestURI().substring(forwardedContext.length()));
            } else {
                receivingURL.append(request.getRequestURI());
            }
        } else {
            receivingURL = request.getRequestURL();
        }
        if (queryString) {
            String query = request.getQueryString();
            if (query != null && query.length() > 0) {
                receivingURL.append("?").append(query);
            }
        }
        return receivingURL.toString();
    }

    public static String getActualRequestBaseURL(HttpServletRequest request) {
        String receivingURL;
        String forwarded = request.getHeader("x-forwarded-host");
        if (forwarded != null) {
            receivingURL = "http://" + forwarded;
            String forwardedContext = request.getHeader("x-forwarded-context");
            if (forwardedContext == null) {
                receivingURL += request.getContextPath();
            }
        } else {
            receivingURL = request.getRequestURL().toString();
            if (request.getServletPath().length() > 1) {
                int idx = receivingURL.indexOf(request.getServletPath());
                if (idx > 0) {
                    receivingURL = receivingURL.substring(0, idx);
                }
            }
        }
        return receivingURL;
    }

    public static String getActualRequestRemoteAddr(ServletRequest request) {
        if (request instanceof HttpServletRequest) {
            String forwarded = ((HttpServletRequest) request).getHeader("x-forwarded-for");
            if (forwarded != null) {
                return forwarded;
            } else {
                return request.getRemoteAddr();
            }
        } else {
            return request.getRemoteAddr();
        }
    }
}
