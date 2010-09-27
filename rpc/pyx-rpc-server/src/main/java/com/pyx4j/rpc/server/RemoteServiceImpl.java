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
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.config.server.rpc.IServiceFilter;
import com.pyx4j.rpc.shared.IsIgnoreSessionTokenService;
import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.rpc.shared.Service;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.SecurityViolationException;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

public class RemoteServiceImpl implements RemoteService {

    private static final Logger log = LoggerFactory.getLogger(RemoteServiceImpl.class);

    private final IServiceFactory serviceFactory;

    public RemoteServiceImpl(String name, IServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Serializable execute(String serviceInterfaceClassName, Serializable serviceRequest) throws RuntimeException {
        SecurityController.assertPermission(new ServiceExecutePermission(serviceInterfaceClassName));
        Class<? extends Service<?, ?>> clazz = ServiceRegistry.getServiceClass(serviceInterfaceClassName);
        if (clazz == null) {
            try {
                clazz = serviceFactory.getServiceClass(serviceInterfaceClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Service " + serviceInterfaceClassName + " not found");
            } catch (Throwable t) {
                log.error("Service call error", t);
                throw new UnRecoverableRuntimeException("Fatal system error");
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
            throw new UnRecoverableRuntimeException("Fatal system error: " + e.getMessage());
        }
        if (!(serviceInstance instanceof IsIgnoreSessionTokenService)) {
            Visit visit = Context.getVisit();
            if ((visit != null) && (!CommonsStringUtils.equals(Context.getRequestHeader(RemoteService.SESSION_TOKEN_HEADER), visit.getSessionToken()))) {
                log.error("X-XSRF error, {} user {}", Context.getSessionId(), visit);
                log.error("X-XSRF tokens: session: {}, request: {}", visit.getSessionToken(), Context.getRequestHeader(RemoteService.SESSION_TOKEN_HEADER));
                throw new SecurityViolationException("Request requires authentication.");
            }
        }
        try {
            List<IServiceFilter> filters = serviceFactory.getServiceFilterChain(clazz);
            if (filters != null) {
                for (IServiceFilter filter : filters) {
                    serviceRequest = filter.filterIncomming(clazz, serviceRequest);
                }
            }
            Serializable returnValue = serviceInstance.execute(serviceRequest);
            if (filters != null) {
                // Run filters in reverse order
                ListIterator<IServiceFilter> li = filters.listIterator(filters.size());
                while (li.hasPrevious()) {
                    returnValue = li.previous().filterOutgoing(clazz, returnValue);
                }
            }
            return returnValue;
        } catch (Throwable e) {
            log.error("Service call error for " + Context.getVisit(), e);
            if (e instanceof RuntimeExceptionSerializable) {
                throw (RuntimeExceptionSerializable) e;
            } else if (e.getMessage() == null) {
                throw new UnRecoverableRuntimeException("System error, contact support");
            } else {
                if (e.getClass().getName().endsWith("DeadlineExceededException")) {
                    // Allow client to recover from GAE startup timeouts
                    throw new RuntimeExceptionSerializable("Request has exceeded the 30 second request deadline. Please try again shortly.");
                } else {
                    // TODO may be don't need to show the actual error to customers.
                    throw new UnRecoverableRuntimeException(e.getMessage());
                }
            }
        }
    }
}
