/*
f * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Oct 9, 2014
 * @author vlads
 */
package com.pyx4j.server.contexts;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeploymentContextFilter implements Filter {

    public static String PARAM_developmentDebugResponse = "developmentDebugResponse";

    private boolean developmentDebugResponse = false;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        developmentDebugResponse = "true".equals(filterConfig.getInitParameter(PARAM_developmentDebugResponse));
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if ((!(request instanceof HttpServletRequest)) || ((request instanceof DeploymentContextHttpServletRequestWrapper))) {
            chain.doFilter(request, response);
        } else {
            HttpServletRequest httprequest = new DeploymentContextHttpServletRequestWrapper((HttpServletRequest) request);
            HttpServletResponse httpresponse = (HttpServletResponse) response;
            if (developmentDebugResponse) {
                httpresponse = new DevDebugHttpServletResponseWrapper(httpresponse);
            }
            chain.doFilter(httprequest, response);
        }
    }

}
