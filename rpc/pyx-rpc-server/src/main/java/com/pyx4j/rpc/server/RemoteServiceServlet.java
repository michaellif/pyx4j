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

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

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
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.server.RequestDebug;
import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.rpc.shared.DevInfoUnRecoverableRuntimeException;
import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;

@SuppressWarnings("serial")
public class RemoteServiceServlet extends com.google.gwt.user.server.rpc.RemoteServiceServlet implements RemoteService {

    private static final Logger log = LoggerFactory.getLogger(RemoteServiceServlet.class);

    private RemoteService implementation;

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
                    throw new ServletException("RPC ServiceFactory not available");
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
        // 6|1|12|http://localhost:8888/g.site/|2005C2913F3EF6EE0AB1510ECABAE604|_|
        int beginModuleBaseURL = 0;
        for (int i = 0; i < 3; i++) {
            beginModuleBaseURL = serializedRequest.indexOf('|', beginModuleBaseURL) + 1;
        }
        String moduleBaseURL = serializedRequest.substring(beginModuleBaseURL, serializedRequest.indexOf('|', beginModuleBaseURL));
        String modulePath;
        try {
            modulePath = new URL(moduleBaseURL).getPath();
        } catch (MalformedURLException e) {
            log.error("error", e);
            throw new IncompatibleRemoteServiceException();
        }

        // Allow for redirected requests environments
        String forwardedPath = Context.getRequest().getHeader(ServletUtils.x_forwarded_path);
        if (forwardedPath != null) {
            modulePath = forwardedPath + modulePath;
        }
        String moduleRelativePath;
        String contextPath = Context.getRequest().getContextPath();
        if (modulePath.startsWith(contextPath)) {
            moduleRelativePath = modulePath.substring(contextPath.length());
        } else {
            moduleRelativePath = modulePath;
        }
        ServicePolicy.loadServicePolicyToRequest(this.getServletContext(), moduleRelativePath);
    }

    @Override
    protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request, String moduleBaseURL, String strongName) {
        // Allow for redirected requests environments, consider the context is mapped to root.
        String forwardedPath = request.getHeader(ServletUtils.x_forwarded_path);
        final boolean debug = false;
        if (debug) {
            log.debug("moduleBaseURL orig {}", moduleBaseURL);
            RequestDebug.debug(request);
        }
        if (forwardedPath != null) {
            try {
                URL url = new URL(moduleBaseURL);
                String modulePath = url.getPath();
                moduleBaseURL = url.getProtocol() + "://" + url.getAuthority() + forwardedPath;
                if (modulePath != null) {
                    moduleBaseURL += modulePath;
                }
                if (debug) {
                    log.debug("moduleBaseURL corrected {}", moduleBaseURL);
                }
            } catch (MalformedURLException e) {
                log.error("Malformed moduleBaseURL {} ", moduleBaseURL, e);
            }
        }
        return super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
    }

    @Override
    public Serializable execute(String serviceInterfaceClassName, Serializable serviceRequest, String userVisitHashCode) throws RuntimeException {
        String realServiceName = ServicePolicy.decodeServiceInterfaceClassName(serviceInterfaceClassName);
        if (realServiceName != null) {
            serviceInterfaceClassName = realServiceName;
        } else if ((ServerSideConfiguration.instance().allowToBypassRpcServiceManifest()) // 
                || ((ServerSideConfiguration.instance().getEnvironmentType() == ServerSideConfiguration.EnvironmentType.GAEDevelopment) && (ServerSideConfiguration
                        .instance().isDevelopmentBehavior()))) {
            realServiceName = serviceInterfaceClassName;
            log.warn("Using development service name {}", serviceInterfaceClassName);
        } else {
            log.error("unable to find service-manifest for {}", serviceInterfaceClassName);
            throw new IncompatibleRemoteServiceException();
        }

        try {
            return implementation.execute(realServiceName, serviceRequest, userVisitHashCode);
        } finally {
            try {
                Lifecycle.endRpcRequest();
            } catch (Throwable e) {
                if (ApplicationMode.isDevelopment()) {
                    throw new DevInfoUnRecoverableRuntimeException(e);
                } else {
                    throw new UnRecoverableRuntimeException("Fatal system error");
                }
            }
        }
    }

    /**
     * Log broken request information in application log
     */
    @Override
    protected void doUnexpectedFailure(Throwable failure) {
        log.error("return http error {}", failure);
        if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
            RequestDebug.debug(getThreadLocalRequest());
        }
        super.doUnexpectedFailure(failure);
    }

}
