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
        return ServletUtils.getRequestServerPort(request);
    }

    @Override
    public String getRemoteAddr() {
        return ServletUtils.getActualRequestRemoteAddr(request);
    }

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
