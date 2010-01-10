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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.rpc.shared.Service;

public class RemoteServiceImpl implements RemoteService {

    private static final Logger log = LoggerFactory.getLogger(RemoteServiceImpl.class);

    private final IServiceFactory serviceFactory;

    public RemoteServiceImpl(String name, IServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Serializable execute(String serviceInterfaceClassName, Serializable serviceRequest) throws RuntimeException {
        Class<? extends Service> clazz = ServiceRegistry.getServiceClass(serviceInterfaceClassName);
        if (clazz == null) {
            try {
                clazz = serviceFactory.getServiceClass(serviceInterfaceClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Service " + serviceInterfaceClassName + " not found");
            }
            ServiceRegistry.register(serviceInterfaceClassName, clazz);
        }
        Service serviceInstance;
        try {
            serviceInstance = clazz.newInstance();
        } catch (Throwable e) {
            log.error("Fatal system error", e);
            if ((e.getCause() != null) && (e.getCause() != e)) {
                log.error("Fatal system error cause", e.getCause());
            }
            throw new RuntimeException("Fatal system error: " + e.getMessage());
        }
        try {
            return serviceInstance.execute(serviceRequest);
        } catch (RuntimeException e) {
            log.error("Service call error", e);
            throw e;
        }
    }
}
