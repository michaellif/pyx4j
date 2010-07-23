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
 * Created on Jan 19, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.server.contexts;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.security.server.ThrottleConfig;

/**
 * Setup Context thread locals.
 * 
 * Also rejects requests from IPs that are sending too many requests and spend too much
 * application time.
 */
public class LifecycleFilter implements Filter {

    private ThrottleConfig throttleConfig;

    private static long nextIntervalResetTime;

    private static Map<String, AccessCounter> accessByIP = new Hashtable<String, AccessCounter>();

    private static class AccessCounter {

        int requests = 1;

        long duration = 0;

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        throttleConfig = ServerSideConfiguration.instance().getThrottleConfig();
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        AccessCounter counter = null;
        try {
            if (start > nextIntervalResetTime) {
                accessByIP.clear();
                nextIntervalResetTime = start + throttleConfig.getInterval();
            }
            counter = accessByIP.get(request.getRemoteAddr());
            if (counter == null) {
                counter = new AccessCounter();
                accessByIP.put(request.getRemoteAddr(), counter);
            } else {
                counter.requests++;

                if ((counter.requests > throttleConfig.getMaxRequests()) || (counter.duration > throttleConfig.getMaxTimeUsage())) {
                    if (response instanceof HttpServletResponse) {
                        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
                    }
                    return;
                }
            }

            if (!(request instanceof HttpServletRequest)) {
                chain.doFilter(request, response);
            } else {
                HttpServletRequest httprequest = (HttpServletRequest) request;
                // TODO MDC
                Lifecycle.beginRequest(httprequest);
                try {
                    chain.doFilter(request, response);
                } finally {
                    Lifecycle.endRequest();
                }
            }
        } finally {
            counter.duration += System.currentTimeMillis() - start;
        }

    }
}
