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
 * Created on Feb 15, 2012
 * @author vlads
 */
package com.pyx4j.rpc.server;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.Trace;
import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.server.IServiceAdapterImpl.ServerAsyncCallback;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.ServerContext;

class ServiceInvocationProxy implements java.lang.reflect.InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(ServiceInvocationProxy.class);

    private static final I18n i18n = I18n.get(ServiceInvocationProxy.class);

    private final Class<? extends IService> serviceInterfaceClass;

    ServiceInvocationProxy(Class<? extends IService> serviceInterfaceClass) {
        this.serviceInterfaceClass = serviceInterfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] serviceArgs) throws Throwable {
        @SuppressWarnings({ "unchecked" })
        AsyncCallback<Serializable> callback = (AsyncCallback<Serializable>) serviceArgs[0];
        assert callback != null : "AsyncCallback, First argument is required for method " + serviceInterfaceClass.getName() + "." + method.getName();
        ServerAsyncCallback verificationCallback = new ServerAsyncCallback();
        Class<? extends IService> serviceImplClass = null;
        try {
            SecurityController.assertPermission(new IServiceExecutePermission(serviceInterfaceClass));

            if (!IService.class.isAssignableFrom(serviceInterfaceClass)) {
                throw new UnRecoverableRuntimeException(i18n.tr("Fatal system error"));
            }

            // Create service instance 
            IServiceFactory serviceFactory = ServerSideConfiguration.instance().getRPCServiceFactory();
            if (serviceFactory == null) {
                serviceFactory = new ReflectionServiceFactory();
            }
            serviceImplClass = serviceFactory.getIServiceClass(serviceInterfaceClass.getName());
            IService serviceInstance;
            try {
                serviceInstance = serviceImplClass.newInstance();
            } catch (Throwable e) {
                log.error("Fatal system error", e);
                if ((e.getCause() != null) && (e.getCause() != e)) {
                    log.error("Fatal system error cause", e.getCause());
                }
                throw new UnRecoverableRuntimeException(i18n.tr("Fatal system error"));
            }

            // invoke the service method and return the result using callback
            Object[] methodArgs = Arrays.copyOf(serviceArgs, serviceArgs.length);
            methodArgs[0] = verificationCallback;
            method.invoke(serviceInstance, methodArgs);

        } catch (InvocationTargetException e) {
            log.error("Service call error\n{}\n for user:" + ServerContext.getVisit(), Trace.clickableClassLocation(serviceImplClass), e.getCause());
            callback.onFailure(e.getCause());
            return null;
        } catch (Throwable e) {
            callback.onFailure(e);
            return null;
        }

        if (verificationCallback.caught != null) {
            log.error("Error", verificationCallback.caught);
            callback.onFailure(verificationCallback.caught);
        } else if (!verificationCallback.onSuccessCalled) {
            log.error("Error forgot to call \"onSuccess\" from method {} in class {}", method.getName(), serviceInterfaceClass.getName());
            callback.onFailure(new UnRecoverableRuntimeException(i18n.tr("Fatal system error")));
        } else {
            callback.onSuccess(verificationCallback.result);
        }
        return null;
    }
}
