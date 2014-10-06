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

    public static final String x_forwarded_protocol = "x-forwarded-protocol";

    public static final String x_forwarded_host = "x-forwarded-host";

    public static final String x_forwarded_context = "x-forwarded-context";

    public static final String x_forwarded_for = "x-forwarded-for";

    public static final String x_forwarded_path = "x-forwarded-path";

    public static final String x_jetty_contextLess = "jetty-rewrite-original-path";

    public static String getForwardedHost(HttpServletRequest request) {
        String host = request.getHeader(x_forwarded_host);
        if (host != null) {
            if (host.contains(", ")) {
                host = host.split(", ")[0];
            }
            return host;
        } else {
            return null;
        }
    }

    public static String getRequestServerName(HttpServletRequest request) {
        String host = getForwardedHost(request);
        if (host != null) {
            return host;
        } else {
            return request.getServerName();
        }
    }

    public static String getForwardedURI(HttpServletRequest request) {
        String applicationDispatcherForwardedPath = (String) request.getAttribute(ServletUtils.x_forwarded_path);
        if (applicationDispatcherForwardedPath != null) {
            StringBuilder forwardedPath = new StringBuilder();

            String forwardedContext = request.getHeader(ServletUtils.x_forwarded_context); // "/warContext/appContext"
            if (forwardedContext != null) {
                forwardedPath.append(forwardedContext);
            } else {
                // Local development
                if (request.getAttribute(ServletUtils.x_jetty_contextLess) != null) {
                    forwardedPath.append(request.getContextPath()); // + "/warContext"
                }
            }
            forwardedPath.append(applicationDispatcherForwardedPath);
            return forwardedPath.toString();
        } else {
            return request.getHeader(x_forwarded_context);
        }
    }

    public static String getActualRequestURL(HttpServletRequest request) {
        return getActualRequestURL(request, false);
    }

    public static String getActualRequestURL(HttpServletRequest request, boolean queryString) {
        StringBuffer receivingURL;
        String forwarded = getForwardedHost(request);
        if (forwarded != null) {
            receivingURL = new StringBuffer();
            String forwardedProtocol = request.getHeader(x_forwarded_protocol);
            if (forwardedProtocol == null) {
                forwardedProtocol = "http";
            }
            receivingURL.append(forwardedProtocol).append("://").append(forwarded);
            String forwardedContext = getForwardedURI(request);
            if (forwardedContext != null) {
                receivingURL.append(request.getRequestURI().substring(forwardedContext.length()));
            } else {
                receivingURL.append(request.getRequestURI());
            }
        } else {
            String forwardedContext = getForwardedURI(request);
            if (forwardedContext != null) {
                receivingURL = new StringBuffer();
                receivingURL.append(request.getScheme()).append("://").append(request.getServerName());

                {
                    int port = request.getServerPort();
                    String scheme = request.getScheme();
                    if (port > 0 && (("http".equalsIgnoreCase(scheme) && port != 80) || ("https".equalsIgnoreCase(scheme) && port != 443))) {
                        receivingURL.append(':').append(port);
                    }
                }
                receivingURL.append(request.getRequestURI().substring(forwardedContext.length()));
            } else {
                receivingURL = request.getRequestURL();
            }
        }
        if (queryString) {
            String query = request.getQueryString();
            if (query != null && query.length() > 0) {
                receivingURL.append("?").append(query);
            }
        }
        return receivingURL.toString();
    }

    public static String getRequestBaseURL(HttpServletRequest request) {
        StringBuilder buf = new StringBuilder();
        String scheme = request.getScheme();
        buf.append(scheme);
        buf.append("://");
        buf.append(request.getServerName());
        // Skip default port
        int port = request.getServerPort();
        if (!(((port == 80) && scheme.equals("http")) || (port == 443) && scheme.equals("https"))) {
            buf.append(':');
            buf.append(request.getServerPort());
        }
        buf.append(request.getContextPath());
        return buf.toString();
    }

    /**
     * Handle the mapping of '/app/part' to root
     */
    public static String getRelativeServletPath(HttpServletRequest request, String servletPath) {
        String forwardedContext = getForwardedURI(request);
        //String forwardedContext = request.getHeader(x_forwarded_context);
        if (forwardedContext == null) {
            return request.getContextPath() + servletPath;
        } else {
            String forwardedPath = request.getHeader(ServletUtils.x_forwarded_path);
            if (forwardedPath != null) {
                if (forwardedPath.startsWith(request.getContextPath())) {
                    forwardedPath = forwardedPath.substring(request.getContextPath().length());
                }
                if (!servletPath.startsWith(forwardedPath)) {
                    throw new Error("Unreachable path" + servletPath + " when " + forwardedPath + " forwarded");
                }
                return servletPath.substring(forwardedPath.length());
            } else {
                String path = request.getContextPath() + servletPath;
                if (!path.startsWith(forwardedContext)) {
                    throw new Error("Unreachable path" + servletPath + " when " + forwardedContext + " forwarded");
                }
                return path.substring(forwardedContext.length());
            }
        }
    }

    public static String getActualRequestRemoteAddr(ServletRequest request) {
        if (request instanceof HttpServletRequest) {
            String forwarded = ((HttpServletRequest) request).getHeader(x_forwarded_for);
            if (forwarded != null) {
                if (forwarded.contains(", ")) {
                    forwarded = forwarded.split(", ")[0];
                }
                return forwarded;
            } else {
                return request.getRemoteAddr();
            }
        } else if (request == null) {
            return null;
        } else {
            return request.getRemoteAddr();
        }
    }

    public static String getRequestProtocol(HttpServletRequest request) {
        String forwarded = getForwardedHost(request);
        if (forwarded != null) {
            String forwardedProtocol = request.getHeader(x_forwarded_protocol);
            if (forwardedProtocol == null) {
                return "http";
            } else {
                return forwardedProtocol;
            }
        } else {
            if (request.isSecure()) {
                return "https";
            } else {
                return "http";
            }
        }
    }
}
