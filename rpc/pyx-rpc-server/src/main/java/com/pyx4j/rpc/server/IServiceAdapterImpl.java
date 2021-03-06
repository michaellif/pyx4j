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
 */
package com.pyx4j.rpc.server;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.config.server.LifecycleListener;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.Trace;
import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.config.server.rpc.IServiceFilter;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.DevInfoUnRecoverableRuntimeException;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.IServiceAdapter;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.IServiceRequest;
import com.pyx4j.rpc.shared.IgnoreSessionToken;
import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.security.annotations.AccessControl;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.SecurityViolationException;
import com.pyx4j.server.contexts.ServerContext;
import com.pyx4j.server.contexts.Visit;

public class IServiceAdapterImpl implements IServiceAdapter {

    private static final Logger log = LoggerFactory.getLogger(IServiceAdapterImpl.class);

    private static final I18n i18n = I18n.get(IServiceAdapterImpl.class);

    @Override
    public Serializable execute(IServiceRequest request) {

        assert (request.getServiceClassId() != null) : "invalid requests, rebuild or restart server";
        assert (request.getServiceMethodId() != null) : "invalid requests, rebuild or restart server";

        String serviceMethodName;

        // decode IService class name and  Method
        String serviceInterfaceClassName = ServicePolicy.decodeServiceInterfaceClassName(request.getServiceClassId());

        serviceMethodName = request.getServiceMethodId();

        SecurityController.assertPermission(new IServiceExecutePermission(serviceInterfaceClassName));

        IServiceFactory serviceFactory = ServerSideConfiguration.instance().getRPCServiceFactory();
        if (serviceFactory == null) {
            serviceFactory = new ReflectionServiceFactory();
        }
        Class<? extends IService> serviceInterfaceClass;
        Class<? extends IService> serviceImplClass;
        try {
            serviceInterfaceClass = (Class<? extends IService>) Class.forName(serviceInterfaceClassName);
            serviceImplClass = serviceFactory.getIServiceClass(serviceInterfaceClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Service " + serviceInterfaceClassName + " not found");
        } catch (Throwable t) {
            log.error("Service call error", t);
            throw new UnRecoverableRuntimeException(i18n.tr("Fatal system error"));
        }
        if (!IService.class.isAssignableFrom(serviceInterfaceClass)) {
            throw new UnRecoverableRuntimeException(i18n.tr("Fatal system error"));
        }

        IService serviceInstance;
        try {
            serviceInstance = serviceImplClass.newInstance();
        } catch (Throwable e) {
            log.error("Fatal system error", e);
            if ((e.getCause() != null) && (e.getCause() != e)) {
                log.error("Fatal system error cause", e.getCause());
            }
            throw new UnRecoverableRuntimeException("Fatal system error: " + e.getMessage());
        }
        filterIncomming(serviceFactory, serviceInterfaceClass, request, serviceInterfaceClassName, serviceInstance);

        for (Method method : serviceInterfaceClass.getMethods()) {
            if ((method.getName().equals(serviceMethodName)) && (request.getServiceMethodSignature() == getMethodSignature(method))) {
                assertToken(serviceImplClass, method);
                assertMethodPermission(method);
                Serializable rc = runMethod(serviceInstance, method, request.getArgs());
                rc = filterOutgoing(serviceFactory, serviceInterfaceClass, request, serviceInterfaceClassName, serviceInstance, rc);
                return rc;
            }
        }
        throw new UnRecoverableRuntimeException("Fatal system error, Method [" + serviceMethodName + "] not found");
    }

    private void assertMethodPermission(Method method) {
        AccessControl accessControl = method.getAnnotation(AccessControl.class);
        if (accessControl != null) {
            SecurityController.assertPermission(new ActionPermission(accessControl.value()));
        }
    }

    private void assertToken(Class<? extends IService> serviceImplClass, Method method) {
        Method implMethod;
        try {
            implMethod = serviceImplClass.getMethod(method.getName(), method.getParameterTypes());
        } catch (Throwable e) {
            log.error("Fatal system error", e);
            throw new UnRecoverableRuntimeException("Fatal system error: " + e.getMessage());
        }

        Visit visit = ServerContext.getVisit();
        if (visit == null) {
            return;
        }
        if ((serviceImplClass.getAnnotation(IgnoreSessionToken.class) != null) || (implMethod.getAnnotation(IgnoreSessionToken.class) != null)) {
            return;
        }
        if (!CommonsStringUtils.equals(ServerContext.getRequestHeader(RemoteService.SESSION_TOKEN_HEADER), visit.getSessionToken())) {
            log.error("X-XSRF error, Srv {}.{}", serviceImplClass.getName(), method.getName());
            log.error("X-XSRF error, {} user {}", ServerContext.getSessionId(), visit);
            log.error("X-XSRF tokens: session: {}, request: {}", visit.getSessionToken(), ServerContext.getRequestHeader(RemoteService.SESSION_TOKEN_HEADER));
            throw new SecurityViolationException("Request requires authentication.");
        }
    }

    public static int getMethodSignature(Method method) {
        int s = 0;
        for (Class<?> paramClass : method.getParameterTypes()) {
            if (IEntity.class.isAssignableFrom(paramClass)) {
                s = s * 31 + 123;
            } else {
                s = s * 31 + paramClass.getSimpleName().hashCode();
            }
        }
        return s;
    }

    private void filterIncomming(IServiceFactory serviceFactory, Class<? extends IService> serviceInterfaceClass, IServiceRequest request,
            String serviceInterfaceClassName, IService serviceInstance) {
        List<IServiceFilter> filters = serviceFactory.getIServiceFilterChain(serviceInterfaceClass);
        if (filters != null) {
            for (IServiceFilter filter : filters) {
                filter.filterIncomming(request, serviceInterfaceClassName, serviceInstance);
            }
        }
    }

    private Serializable filterOutgoing(IServiceFactory serviceFactory, Class<? extends IService> serviceInterfaceClass, IServiceRequest request,
            String serviceInterfaceClassName, IService serviceInstance, Serializable returnValue) {
        // Run filters in reverse order
        List<IServiceFilter> filters = serviceFactory.getIServiceFilterChain(serviceInterfaceClass);
        ListIterator<IServiceFilter> li = filters.listIterator(filters.size());
        while (li.hasPrevious()) {
            returnValue = li.previous().filterOutgoing(request, serviceInterfaceClassName, serviceInstance, returnValue);
        }
        return returnValue;
    }

    static class ServerAsyncCallback implements AsyncCallback<Serializable> {

        Throwable caught;

        Serializable result;

        boolean onSuccessCalled = false;

        @Override
        public void onFailure(Throwable caught) {
            this.caught = caught;
        }

        @Override
        public void onSuccess(Serializable result) {
            this.result = result;
            onSuccessCalled = true;
        }

    }

    private Serializable runMethod(IService serviceInstance, Method method, List<Serializable> args) {
        try {
            log.debug("RPC call > {}.{}", serviceInstance.getClass().getName(), method.getName());

            ServerAsyncCallback callback = new ServerAsyncCallback();
            Object[] methodArgs = new Object[args.size() + 1];
            methodArgs[0] = callback;
            int i = 1;
            for (Serializable a : args) {
                methodArgs[i] = a;
                i++;
            }

            method.invoke(serviceInstance, methodArgs);

            if (callback.caught != null) {
                log.error("Error", callback.caught);
                for (LifecycleListener lifecycleListener : ServerSideConfiguration.instance().getLifecycleListeners()) {
                    lifecycleListener.onRequestError();
                }
                if (callback.caught instanceof RuntimeException) {
                    throw (RuntimeException) callback.caught;
                }
                throw new UnRecoverableRuntimeException(i18n.tr("Fatal system error"));
            }
            if (!callback.onSuccessCalled) {
                log.error("Error forgot to call \"onSuccess\" from method {} in class {}", method.getName(), serviceInstance.getClass().getName());
                throw new UnRecoverableRuntimeException(i18n.tr("Fatal system error"));
            }
            return callback.result;
        } catch (IllegalArgumentException e) {
            log.error("Error", e);
            throw new UnRecoverableRuntimeException(i18n.tr("Fatal system error"));
        } catch (IllegalAccessException e) {
            log.error("Error", e);
            throw new UnRecoverableRuntimeException(i18n.tr("Fatal system error"));
        } catch (InvocationTargetException e) {
            if (RemoteServiceImpl.logStakTrace(e)) {
                log.error("Service call error\n{}\n for user: {}", Trace.clickableClassLocation(serviceInstance.getClass()), ServerContext.getVisit(),
                        e.getCause());
            }
            if (e.getCause() instanceof RuntimeExceptionSerializable) {
                if ((e.getCause() != null) && (e.getCause() != e)) {
                    if (RemoteServiceImpl.logStakTrace(e.getCause())) {
                        log.error("Service call error Cause", e);
                    } else {
                        log.warn("Service call error '{}'", e.getCause().getMessage());
                    }
                }
            }
            for (LifecycleListener lifecycleListener : ServerSideConfiguration.instance().getLifecycleListeners()) {
                lifecycleListener.onRequestError();
            }

            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            } else {
                if (ApplicationMode.isDevelopment()) {
                    throw new DevInfoUnRecoverableRuntimeException(e.getCause());
                } else {
                    throw new UnRecoverableRuntimeException(i18n.tr("Fatal system error"));
                }
            }
        } finally {
            // call Persistence.service().endTransaction();
            for (LifecycleListener lifecycleListener : ServerSideConfiguration.instance().getLifecycleListeners()) {
                boolean success = false;
                try {
                    lifecycleListener.onRequestEnd();
                    success = true;
                } finally {
                    if (!success) {
                        log.error("Service call error\n{}\n", Trace.clickableClassLocation(serviceInstance.getClass()));
                    }
                }
            }
            log.debug("RPC call < {}.{}", serviceInstance.getClass().getName(), method.getName());
        }
    }
}
