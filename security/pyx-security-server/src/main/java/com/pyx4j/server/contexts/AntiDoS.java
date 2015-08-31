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
 * Created on 2011-05-15
 * @author vlads
 */
package com.pyx4j.server.contexts;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.gwt.server.RequestDebug;
import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.security.server.ThrottleConfig;

/**
 * Rejects requests from IPs that are sending too many requests and spend too much
 * application time.
 */
public class AntiDoS {

    private static Logger log = LoggerFactory.getLogger(AntiDoS.class);

    public static class AccessCounter {

        int requests;

        long duration;

        @Override
        public String toString() {
            return "requests " + requests + ", duration " + duration;
        }

    }

    private final ThrottleConfig throttleConfig;

    private static long nextIntervalResetTime;

    private static Map<String, AccessCounter> accessByIP = new Hashtable<String, AccessCounter>();

    public AntiDoS() {
        throttleConfig = ServerSideConfiguration.instance().getThrottleConfig();
    }

    public AccessCounter beginRequest(ServletRequest request, long requestStart) {
        if (throttleConfig == null || !throttleConfig.isEnabled()) {
            return new AccessCounter();
        }
        AccessCounter counter;
        // Allow for system date change
        if ((requestStart > nextIntervalResetTime) || (nextIntervalResetTime > requestStart + throttleConfig.getInterval())) {
            accessByIP.clear();
            nextIntervalResetTime = requestStart + throttleConfig.getInterval();
        }
        String remoteAddr = ServletUtils.getActualRequestRemoteAddr(request);
        counter = accessByIP.get(remoteAddr);
        if (counter == null) {
            counter = new AccessCounter();
            accessByIP.put(remoteAddr, counter);
        } else {
            String uri = "";
            if (request instanceof HttpServletRequest) {
                uri = ((HttpServletRequest) request).getRequestURI();
                String[] split = uri.split("/", 4);
                if (split.length >= 3) {
                    String uriPart = "/" + split[1] + "/" + split[2];
                    if (throttleConfig.whiteRequestURIs().contains(uriPart)) {
                        return counter;
                    }
                }
            }

            if ((counter.requests > throttleConfig.getMaxRequests()) || (counter.duration > throttleConfig.getMaxTimeUsage())) {
                if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
                    RequestDebug.debug(request);
                }
                if (counter.requests == throttleConfig.getMaxRequests() || (counter.requests % 1000 == 0)) {
                    log.error("possible denial-of-service attack from {}; {}; {}", remoteAddr, counter, uri);
                }
                counter.requests++;
                return null;
            }
        }
        return counter;
    }

    public String debugRequest(ServletRequest request) {
        String remoteAddr = ServletUtils.getActualRequestRemoteAddr(request);
        AccessCounter counter = accessByIP.get(remoteAddr);
        StringBuilder b = new StringBuilder();
        b.append(remoteAddr);
        if (counter != null) {
            b.append(" ").append(counter.requests).append(" of ").append(throttleConfig.getMaxRequests()).append(" reset at ")
                    .append(new Date(nextIntervalResetTime));
        }
        return b.toString();
    }

    public void endRequest(AccessCounter counter, long requestStart) {
        counter.requests++;
        counter.duration += System.currentTimeMillis() - requestStart;
    }

    /**
     * Used for Selenium tests
     */
    public static void resetRequestCount(ServletRequest request) {
        if (!ServerSideConfiguration.instance().isDevelopmentBehavior()) {
            return;
        }
        AccessCounter counter = accessByIP.get(ServletUtils.getActualRequestRemoteAddr(request));
        if (counter != null) {
            counter.requests = 0;
            counter.duration = 0;
        }
    }
}
