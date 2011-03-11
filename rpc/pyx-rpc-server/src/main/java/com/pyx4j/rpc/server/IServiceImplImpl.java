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
 * Created on 2011-03-11
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.server;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.IServiceImpl;
import com.pyx4j.rpc.shared.IServiceRequest;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.security.shared.SecurityController;

public class IServiceImplImpl implements IServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(IServiceImplImpl.class);

    @Override
    public Serializable execute(IServiceRequest request) {

        String serviceInterfaceClassName;

        String serviceMethodName;

        //TODO decode IService class name and  Method
        serviceInterfaceClassName = request.getServiceClassId();
        serviceMethodName = request.getServiceMethodId();

        SecurityController.assertPermission(new IServiceExecutePermission(serviceInterfaceClassName));

        IServiceFactory serviceFactory = ServerSideConfiguration.instance().getRPCServiceFactory();
        if (serviceFactory == null) {
            serviceFactory = new ReflectionServiceFactory();
        }
        Class<? extends IService> clazz;
        try {
            clazz = serviceFactory.getIServiceClass(serviceInterfaceClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Service " + serviceInterfaceClassName + " not found");
        } catch (Throwable t) {
            log.error("Service call error", t);
            throw new UnRecoverableRuntimeException("Fatal system error");
        }

        IService serviceInstance;
        try {
            serviceInstance = clazz.newInstance();
        } catch (Throwable e) {
            log.error("Fatal system error", e);
            if ((e.getCause() != null) && (e.getCause() != e)) {
                log.error("Fatal system error cause", e.getCause());
            }
            throw new UnRecoverableRuntimeException("Fatal system error: " + e.getMessage());
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(serviceMethodName)) {
                return runMethod(serviceInstance, method, request.getArgs());
            }
        }
        throw new UnRecoverableRuntimeException("Fatal system error");
    }

    private Serializable runMethod(IService serviceInstance, Method method, Serializable[] args) {
        try {
            return (Serializable) method.invoke(serviceInstance, (Object[]) args);
        } catch (IllegalArgumentException e) {
            log.error("Error", e);
            throw new UnRecoverableRuntimeException("Fatal system error");
        } catch (IllegalAccessException e) {
            log.error("Error", e);
            throw new UnRecoverableRuntimeException("Fatal system error");
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                log.error("Error", e);
                throw new UnRecoverableRuntimeException("Fatal system error");
            }
        }
    }

}
