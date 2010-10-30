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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.rpc.shared.RemoteService;

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

    @Override
    public Serializable execute(String serviceInterfaceClassName, Serializable serviceRequest, String userVisitHashCode) throws RuntimeException {
        return implementation.execute(serviceInterfaceClassName, serviceRequest, userVisitHashCode);
    }

}
