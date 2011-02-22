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
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.server;

import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.server.rpc.SerializationPolicy;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.server.contexts.Context;

@SuppressWarnings("serial")
public class RemoteServiceServlet extends com.google.gwt.user.server.rpc.RemoteServiceServlet implements RemoteService {

    private static final Logger log = LoggerFactory.getLogger(RemoteServiceServlet.class);

    private RemoteService implementation;

    private final transient Map<String, Map<String, String>> servicePolicyCache = new HashMap<String, Map<String, String>>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        IServiceFactory serviceFactory = null;
        if (config != null) {
            String serviceFactoryClassName = config.getInitParameter("serviceFactoryClass");
            if (CommonsStringUtils.isStringSet(serviceFactoryClassName)) {
                try {
                    serviceFactory = (IServiceFactory) Class.forName(serviceFactoryClassName).newInstance();
                } catch (Throwable e) {
                    log.error("GWT RPC ServiceFactory creation error", e);
                    throw new ServletException("RPC ServiceFactory not avalable");
                }
            }
        }
        if (serviceFactory == null) {
            serviceFactory = ServerSideConfiguration.instance().getRPCServiceFactory();
        }
        if (serviceFactory == null) {
            serviceFactory = new ReflectionServiceFactory();
        }
        implementation = new RemoteServiceImpl("GWT", serviceFactory);
    }

    // GWT makes it impossible to Override any other method in RemoteServiceServlet and access moduleBaseURL from RCP request.
    @Override
    protected void onBeforeRequestDeserialized(String serializedRequest) {
        try {
            // 6|1|12|http://localhost:8888/g.site/|2005C2913F3EF6EE0AB1510ECABAE604|_|
            int beginModuleBaseURL = 0;
            for (int i = 0; i < 3; i++) {
                beginModuleBaseURL = serializedRequest.indexOf('|', beginModuleBaseURL) + 1;
            }
            String moduleBaseURL = serializedRequest.substring(beginModuleBaseURL, serializedRequest.indexOf('|', beginModuleBaseURL));
            String contextPath = Context.getRequest().getContextPath();
            //log.debug("moduleBaseURL = [{}] contextPath [{}]", moduleBaseURL, contextPath);
            String modulePath = new URL(moduleBaseURL).getPath();
            String moduleRelativePath;
            if (modulePath.contains(contextPath)) {
                // Allow for redirected requests environments, consider the context is mapped to root.
                moduleRelativePath = modulePath.substring(contextPath.length());
            } else {
                moduleRelativePath = modulePath;
            }

            Map<String, String> servicePolicy = servicePolicyCache.get(moduleRelativePath);
            if (servicePolicy == null) {
                InputStream is = this.getServletContext().getResourceAsStream(moduleRelativePath + "pyx-service-manifest.rpc");
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
            Context.getRequest().setAttribute("pyx.ServicePolicy", servicePolicy);
        } catch (Throwable t) {
            log.error("unable to load service-manifest", t);
            throw new IncompatibleRemoteServiceException();
        }
    }

    @Override
    protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request, String moduleBaseURL, String strongName) {
        // Allow for redirected requests environments, consider the context is mapped to root.
        if (ServerSideConfiguration.instance().isContextLessDeployment()) {
            try {
                //log.debug("moduleBaseURL orig {}", moduleBaseURL);
                URL url = new URL(moduleBaseURL);
                String modulePath = url.getPath();
                //log.debug("modulePath {}", modulePath);
                String contextPath = request.getContextPath();
                if ((modulePath != null) && !modulePath.contains(contextPath)) {
                    moduleBaseURL = url.getProtocol() + "://" + url.getAuthority() + contextPath + modulePath;
                    //log.debug("moduleBaseURL corrected {}", moduleBaseURL);
                }
            } catch (MalformedURLException e) {
                log.error("Malformed moduleBaseURL {} ", moduleBaseURL, e);
            }
        }
        return super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
    }

    @Override
    public Serializable execute(String serviceInterfaceClassName, Serializable serviceRequest, String userVisitHashCode) throws RuntimeException {
        String realServiceName = null;
        @SuppressWarnings("unchecked")
        Map<String, String> servicePolicy = (Map<String, String>) Context.getRequest().getAttribute("pyx.ServicePolicy");
        if (servicePolicy != null) {
            realServiceName = servicePolicy.get(serviceInterfaceClassName);
        }
        if (realServiceName != null) {
            serviceInterfaceClassName = realServiceName;
        } else if ((ServerSideConfiguration.instance().getEnvironmentType() == ServerSideConfiguration.EnvironmentType.GAEDevelopment)
                && (ServerSideConfiguration.instance().isDevelopmentBehavior())) {
            realServiceName = serviceInterfaceClassName;
            log.warn("Using development serveice name {}", serviceInterfaceClassName);
        } else {
            log.error("unable to find service-manifest");
            throw new IncompatibleRemoteServiceException();
        }

        return implementation.execute(realServiceName, serviceRequest, userVisitHashCode);
    }

}
