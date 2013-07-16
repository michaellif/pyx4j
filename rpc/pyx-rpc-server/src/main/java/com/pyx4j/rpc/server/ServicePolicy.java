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
 * Created on 2012-06-20
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.server;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.Context;

public class ServicePolicy {

    private static final Logger log = LoggerFactory.getLogger(ServicePolicy.class);

    private final static Map<String, Map<String, String>> servicePolicyCache = new Hashtable<String, Map<String, String>>();

    public static String SERVICE_INTERFACE_CLASSNAMES_REQUEST_ATTRIBUTE = "pyx.ServicePolicy";

    public static void loadServicePolicyToRequest(ServletContext servletContext, String moduleRelativePath) {
        Map<String, String> servicePolicy = servicePolicyCache.get(moduleRelativePath);
        try {
            if (servicePolicy == null) {
                InputStream is = servletContext.getResourceAsStream(moduleRelativePath + "pyx-service-manifest.rpc");
                if (is == null) {
                    log.warn("service-manifest {} not found", moduleRelativePath);
                } else {
                    try {
                        Properties prop = new Properties();
                        prop.load(is);
                        if (prop.size() == 0) {
                            log.warn("service-manifest is empty");
                        } else {
                            log.debug("{} service-manifest has {} service", moduleRelativePath, prop.size());
                        }
                        servicePolicy = new HashMap<String, String>();
                        for (Map.Entry<Object, Object> me : prop.entrySet()) {
                            if ((me.getKey() instanceof String) && (me.getValue() instanceof String)) {
                                servicePolicy.put((String) me.getKey(), (String) me.getValue());
                            }
                        }
                        servicePolicyCache.put(moduleRelativePath, servicePolicy);
                        log.trace("servicePolicy {}", servicePolicy);
                    } finally {
                        IOUtils.closeQuietly(is);
                    }
                }
            }
            Context.getRequest().setAttribute(SERVICE_INTERFACE_CLASSNAMES_REQUEST_ATTRIBUTE, servicePolicy);
        } catch (Throwable t) {
            log.error("unable to load service-manifest", t);
            throw new IncompatibleRemoteServiceException();
        }
    }

    public static String decodeServiceInterfaceClassName(String serviceClassId) {
        @SuppressWarnings("unchecked")
        Map<String, String> servicePolicy = (Map<String, String>) Context.getRequest().getAttribute(SERVICE_INTERFACE_CLASSNAMES_REQUEST_ATTRIBUTE);

        String realServiceName = null;
        if (servicePolicy != null) {
            realServiceName = servicePolicy.get(serviceClassId);
        }
        if (realServiceName != null) {
            return realServiceName;
        } else if ((realServiceName == null) && (servicePolicy != null) && ApplicationMode.isDevelopment()) {
            return serviceClassId;
        } else if ((ServerSideConfiguration.instance().allowToBypassRpcServiceManifest()) // 
                || ((ServerSideConfiguration.instance().getEnvironmentType() == ServerSideConfiguration.EnvironmentType.GAEDevelopment) && (ServerSideConfiguration
                        .instance().isDevelopmentBehavior()))) {
            log.warn("Using development service name {}", serviceClassId);
            return serviceClassId;
        } else {
            log.error("unable to find service-manifest for {}", serviceClassId);
            throw new IncompatibleRemoteServiceException();
        }
    }
}
