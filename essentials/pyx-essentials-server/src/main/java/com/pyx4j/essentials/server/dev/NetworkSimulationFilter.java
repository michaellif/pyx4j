/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Jul 28, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server.dev;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.essentials.rpc.admin.NetworkSimulation;

public class NetworkSimulationFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(NetworkSimulationFilter.class);

    private static NetworkSimulation networkSimulationConfig;

    private static AtomicInteger httpRequests = new AtomicInteger();

    private static AtomicInteger httpRequestsSimulatedCount = new AtomicInteger();

    private final static Object dellayLock = new Object();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if ((!(response instanceof HttpServletResponse)) || networkSimulationConfig == null || !networkSimulationConfig.enabled().getValue(false)) {
            chain.doFilter(request, response);
        } else {
            HttpServletRequest httprequest = (HttpServletRequest) request;
            HttpServletResponse httpresponse = (HttpServletResponse) response;
            String uri = httprequest.getRequestURI();
            if (uri.matches(networkSimulationConfig.httpRequestURIPattern().getValue(""))) {
                if (httpRequests.incrementAndGet() >= networkSimulationConfig.httpRequestStartNumber().getValue(1)) {
                    if (httpRequestsSimulatedCount.incrementAndGet() >= networkSimulationConfig.httpRequestCount().getValue(1)) {
                        httpRequests.set(0);
                        httpRequestsSimulatedCount.set(0);
                    }

                    if (networkSimulationConfig.delay().getValue(0) > 0) {
                        try {
                            synchronized (dellayLock) {
                                log.warn("adding simulated HTTP response delay {}", networkSimulationConfig.delay());
                                dellayLock.wait(networkSimulationConfig.delay().getValue());
                            }
                        } catch (InterruptedException e) {
                        }
                    }

                    if (networkSimulationConfig.httpResponseCode().getValue(0) > 0) {
                        log.warn("sending simulated HTTP response {}", networkSimulationConfig.httpResponseCode());
                        httpresponse.sendError(networkSimulationConfig.httpResponseCode().getValue(), "Simulated code");
                    } else {
                        chain.doFilter(request, response);
                    }
                } else {
                    chain.doFilter(request, response);
                }
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
    }

    public static NetworkSimulation getNetworkSimulationConfig() {
        return networkSimulationConfig;
    }

    public static void setNetworkSimulationConfig(NetworkSimulation networkSimulationConfig) {
        NetworkSimulationFilter.networkSimulationConfig = networkSimulationConfig;
    }
}
