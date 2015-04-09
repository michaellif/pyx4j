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
 */
package com.pyx4j.gwt.server;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class ServletUtils {

    public static final String x_forwarded_protocol = "x-forwarded-protocol";

    public static final String x_forwarded_host = "x-forwarded-host";

    public static final String x_forwarded_context = "x-forwarded-context";

    public static final String x_forwarded_for = "x-forwarded-for";

    public static final String x_forwarded_path = "x-forwarded-path";

    public static final String x_jetty_contextLess = "jetty-rewrite-original-path";

    public static boolean hasForwardedHost(HttpServletRequest request) {
        return (request.getHeader(x_forwarded_host) != null) //
                || (request.getHeader(x_forwarded_protocol) != null);

    }

    public static boolean hasForwardedURL(HttpServletRequest request) {
        return (request.getHeader(x_forwarded_context) != null) //
                || (request.getAttribute(x_jetty_contextLess) != null) //
                || (request.getAttribute(x_forwarded_path) != null);

    }

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

    public static int getRequestServerPort(HttpServletRequest request) {
        if (!hasForwardedHost(request)) {
            return request.getServerPort();
        } else {
            if ("http".equals(getRequestProtocol(request))) {
                return 80;
            } else {
                return 443;
            }
        }
    }

    public static String getActualRequestURL(HttpServletRequest request) {
        return getActualRequestURL(request, false);
    }

    public static String getActualRequestURL(HttpServletRequest request, boolean queryString) {
        StringBuilder receivingURL = new StringBuilder();

        receivingURL.append(getRequestBaseURL(request));

        if (!hasForwardedURL(request)) {
            receivingURL.append(request.getRequestURI());
        } else {
            String uri = request.getRequestURI();

            String forwardedContext = request.getHeader(ServletUtils.x_forwarded_context); // "/warContext/appContext"
            if (forwardedContext != null) {
                assert uri.startsWith(forwardedContext) : uri + " Should start with " + forwardedContext;
                uri = uri.substring(forwardedContext.length());
            } else {
                // Local development
                if (request.getAttribute(ServletUtils.x_jetty_contextLess) == null) {
                    assert uri.startsWith(request.getContextPath()) : uri + " Should start with " + request.getContextPath();
                    uri = uri.substring(request.getContextPath().length()); // + "/warContext"
                }
            }

            String forwardedPath = (String) request.getAttribute(x_forwarded_path);
            if (forwardedPath != null) {
                uri = uri.replaceFirst(forwardedPath, "");
            }
            receivingURL.append(uri);
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
        StringBuilder receivingURL = new StringBuilder();

        String scheme = getRequestProtocol(request);
        receivingURL.append(scheme);
        receivingURL.append("://");
        receivingURL.append(getRequestServerName(request));
        {
            int port = getRequestServerPort(request);
            if (port > 0 && (("http".equalsIgnoreCase(scheme) && port != 80) || ("https".equalsIgnoreCase(scheme) && port != 443))) {
                receivingURL.append(':').append(port);
            }
        }
        return receivingURL.toString();
    }

    public static String getRequestWarBaseURL(HttpServletRequest request) {
        if (hasForwardedURL(request)) {
            return getRequestBaseURL(request);
        } else {
            return getRequestBaseURL(request) + request.getContextPath();
        }
    }

    // This is bridge for RPC RemoteServiceServlet to load serialization policy
    // Created URL can be used in getServletContext().getResourceAsStream(url.getPath() - ContextPath);
    public static String toServletContainerInternalURL(HttpServletRequest request, String externalUrs) {
        if (!hasForwardedURL(request)) {
            return externalUrs;
        } else {
            URL url;
            try {
                url = new URL(externalUrs);
            } catch (MalformedURLException e) {
                throw new Error(e);
            }
            StringBuilder internalURL = new StringBuilder();
            internalURL.append(url.getProtocol()).append("://").append(url.getAuthority());
            internalURL.append(toServletContainerInternalURI(request, url.getPath()));
            return internalURL.toString();
        }
    }

    public static String toServletContainerInternalURI(HttpServletRequest request, String externalPath) {
        StringBuilder internalURI = new StringBuilder();
        String forwardedContext = request.getHeader(ServletUtils.x_forwarded_context);
        if (forwardedContext != null) {
            internalURI.append(forwardedContext);
        } else if (request.getAttribute(ServletUtils.x_jetty_contextLess) != null) { // Local development
            internalURI.append(request.getContextPath()); // + "/warContext"
        }
        String forwardedPath = (String) request.getAttribute(x_forwarded_path);
        if (forwardedPath != null) {
            if (internalURI.length() == 0) {
                externalPath = externalPath.substring(request.getContextPath().length());
                internalURI.append(request.getContextPath());
            }
            internalURI.append(forwardedPath);
        }

        internalURI.append(externalPath);
        return internalURI.toString();
    }

    /**
     * Handle the mapping of '/app/part' to root of web application on Forwarding Server
     */
    public static String getRelativeServletPath(HttpServletRequest request, String servletPath) {
        if (!hasForwardedURL(request)) {
            return request.getContextPath() + servletPath;
        } else {
            StringBuilder receivingURI = new StringBuilder();
            String forwardedContext = request.getHeader(ServletUtils.x_forwarded_context);
            if ((forwardedContext == null) && (request.getAttribute(ServletUtils.x_jetty_contextLess) == null)) {
                receivingURI.append(request.getContextPath());
            } else if (forwardedContext != null) {
                if (!forwardedContext.startsWith(request.getContextPath())) {
                    throw new Error("Unreachable Context" + request.getContextPath() + " when " + forwardedContext + " forwarded");
                }
                forwardedContext = forwardedContext.substring(request.getContextPath().length());
                if (!servletPath.startsWith(forwardedContext)) {
                    throw new Error("Unreachable path" + servletPath + " when " + forwardedContext + " forwarded");
                }
                servletPath = servletPath.substring(forwardedContext.length());
            }

            String forwardedPath = (String) request.getAttribute(x_forwarded_path);
            if (forwardedPath != null) {
                if (!servletPath.startsWith(forwardedPath)) {
                    throw new Error("Unreachable path" + servletPath + " when " + forwardedPath + " forwarded");
                }
                receivingURI.append(servletPath.substring(forwardedPath.length()));
            } else {
                receivingURI.append(servletPath);
            }

            return receivingURI.toString();
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
