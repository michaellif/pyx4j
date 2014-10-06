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
 * Created on 2012-11-21
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.server.contexts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.pyx4j.gwt.server.ServletUtils;

public class DeploymentContextHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final HttpServletRequest request;

    public DeploymentContextHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public String getScheme() {
        return ServletUtils.getRequestProtocol(request);
    }

    @Override
    public boolean isSecure() {
        return "https".equals(getScheme());
    }

    @Override
    public String getServerName() {
        return ServletUtils.getRequestServerName(request);
    }

    @Override
    public int getServerPort() {
        String forwardedProtocol = request.getHeader(ServletUtils.x_forwarded_protocol);
        if (forwardedProtocol == null) {
            return super.getServerPort();
        } else {
            if ("http".equals(getScheme())) {
                return 80;
            } else {
                return 443;
            }
        }
    }

    @Override
    public String getRemoteAddr() {
        return ServletUtils.getActualRequestRemoteAddr(request);
    }

    @Override
    public String getContextPath() {
        String applicationDispatcherForwardedPath = (String) request.getAttribute(ServletUtils.x_forwarded_path);
        if (applicationDispatcherForwardedPath != null) {
            // new
            return request.getContextPath();
        } else {
            // Old
            String forwardedContext = request.getHeader(ServletUtils.x_forwarded_context);
            if (forwardedContext == null) {
                return request.getContextPath();
            } else {
                return "";
            }
        }
    }

    @Override
    public String getHeader(String name) {
        if (ServletUtils.x_forwarded_path.equals(name)) {
            // This is bridge for RPC RemoteServiceServlet to load serialization policy
            // No attempts are made to cleanup old configuration

            // This new Deployment All Can be done in application.
            // append X-Forwarded-Context "/warContext"
            // RewriteRule ^/(.*)  http://localhost:8080/warContext/$1 [P,L]
            // ApplicationDispatcherFilter
            String applicationDispatcherForwardedPath = (String) request.getAttribute(ServletUtils.x_forwarded_path);
            if (applicationDispatcherForwardedPath != null) {
                StringBuilder forwardedPath = new StringBuilder();

                String forwardedContext = request.getHeader(ServletUtils.x_forwarded_context); // "/warContext/appContext"
                if (forwardedContext != null) {
                    forwardedPath.append(forwardedContext);
                } else {
                    // Local development
                    if (request.getAttribute(ServletUtils.x_jetty_contextLess) != null) {
                        forwardedPath.append(getContextPath()); // + "/warContext"
                    }
                }
                forwardedPath.append(applicationDispatcherForwardedPath);
                return forwardedPath.toString();
            } else {
                String forwardedContext = request.getHeader(ServletUtils.x_forwarded_context);
                if (forwardedContext != null) {
                    // This is Old Apache RequestHeader
                    // append X-Forwarded-Context "/warContext/appContext"
                    // RewriteRule ^/(.*)  http://localhost:8080/warContext/appContext/$1 [P,L]

                    int p = forwardedContext.indexOf('/', 1);
                    if (p != -1) {
                        return forwardedContext.substring(p);
                    } else {
                        // this is not expected
                    }
                }
                return null;
            }
        }
        return request.getHeader(name);
    }

    @Override
    public String getRequestURI() {
        String uri = super.getRequestURI();
//        if (request.getAttribute(ServletUtils.x_jetty_contextLess) != null) {
//            //uri = request.getContextPath() + uri;
//        }
        String forwardedContext = request.getHeader(ServletUtils.x_forwarded_context);
        if (forwardedContext != null) {
            uri = uri.substring(request.getContextPath().length());
        }
//        String applicationDispatcherForwardedPath = (String) request.getAttribute(ServletUtils.x_forwarded_path);
//        if (applicationDispatcherForwardedPath != null) {
//            //uri = uri.substring(applicationDispatcherForwardedPath.length());
//        }
        return uri;
    }

    //TODO this does not work according to specs in forwarded case
    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(ServletUtils.getActualRequestURL(request));
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        String scheme = this.getScheme();
        buf.append(scheme);
        buf.append("://");
        buf.append(this.getServerName());
        buf.append(':');
        buf.append(this.getServerPort());
        buf.append(this.getRequestURI());
        String query = this.getQueryString();
        if (query != null && query.length() > 0) {
            buf.append("?").append(query);
        }
        return buf.toString();
    }
}
