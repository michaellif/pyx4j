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
 * Created on Apr 11, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.client;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.rpc.impl.Serializer;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.commons.IHaveServiceCallMarker;
import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.rpc.client.RPCStatusChangeEvent.When;
import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.rpc.shared.RemoteServiceAsync;
import com.pyx4j.rpc.shared.RuntimeExceptionNotificationsWrapper;
import com.pyx4j.rpc.shared.Service;
import com.pyx4j.rpc.shared.SystemNotificationsWrapper;
import com.pyx4j.serialization.client.RemoteServiceSerializer;

public class RPCManager {

    private static final Logger log = LoggerFactory.getLogger(RPCManager.class);

    private static final RemoteServiceAsync service;

    private static final RemoteServiceSerializer serializer;

    private static final ServiceNames serviceNames;

    private static EventBus eventBus;

    private static int runningServicesCount = 0;

    private static final int RECOVERABLE_CALL_RETRY_MAX = 3;

    private static PyxRpcRequestBuilder requestBuilder;

    private static String userVisitHashCode;

    static {
        service = (RemoteServiceAsync) GWT.create(RemoteService.class);
        serializer = GWT.create(RPCSerializer.class);
        serviceNames = GWT.create(ServiceNames.class);
        ServiceDefTarget target = (ServiceDefTarget) service;
        target.setRpcRequestBuilder(requestBuilder = new PyxRpcRequestBuilder());
    }

    public static void setServiceEntryPointURL(String url) {
        ServiceDefTarget target = (ServiceDefTarget) service;
        target.setServiceEntryPoint(url);
    }

    public static void enableAppEngineUsageStats() {
        requestBuilder.enableAppEngineUsageStats();
    }

    public static void setSessionToken(String sessionToken, String sessionAclTimeStamp) {
        requestBuilder.setSessionToken(sessionToken, sessionAclTimeStamp);
    }

    public static void setUserVisitHashCode(String userVisitHashCode) {
        RPCManager.userVisitHashCode = userVisitHashCode;
    }

    public static Serializer getSerializer() {
        return serializer.getSerializer();
    }

    public static <I extends Serializable, O extends Serializable> void executeBackground(final Class<? extends Service<I, O>> serviceInterface, I request,
            AsyncCallback<O> callback) {
        executeImpl(ServiceExecutionInfo.BACKGROUND, serviceInterface, request, callback, 0);
    }

    public static <I extends Serializable, O extends Serializable> void execute(final Class<? extends Service<I, O>> serviceInterface, I request,
            AsyncCallback<O> callback) {
        executeImpl(ServiceExecutionInfo.DEFAULT, serviceInterface, request, callback, 0);
    }

    public static <I extends Serializable, O extends Serializable> void execute(ServiceExecutionInfo info,
            final Class<? extends Service<I, O>> serviceInterface, I request, AsyncCallback<O> callback) {
        executeImpl(info, serviceInterface, request, callback, 0);
    }

    private static void executeImpl(final ServiceExecutionInfo info, final Class<? extends Service<?, ?>> serviceInterface, Serializable request,
            AsyncCallback<?> callback, int retryAttempt) {
        final ServiceHandlingCallback serviceHandlingCallback = new ServiceHandlingCallback(info, serviceInterface, request, callback, retryAttempt);
        try {
            runningServicesCount++;
            fireStatusChangeEvent(When.START, info, serviceInterface, callback, -1);
            String name = serviceNames.getServiceName(serviceInterface);
            requestBuilder.executing(name, request);
            service.execute(name, request, userVisitHashCode, serviceHandlingCallback);
        } catch (Throwable e) {
            serviceHandlingCallback.onFailure(e);
        }
    }

    public static boolean isRecoverableAppengineFailure(Throwable caught) {
        return ((caught instanceof StatusCodeException) && (((StatusCodeException) caught).getStatusCode()) == Response.SC_INTERNAL_SERVER_ERROR);
    }

    @SuppressWarnings("unchecked")
    private static class ServiceHandlingCallback implements AsyncCallback<Serializable> {

        private final long requestStartTime = System.currentTimeMillis();

        private final ServiceExecutionInfo info;

        private final int retryAttempt;

        final Class<? extends Service<?, ?>> serviceInterface;

        private AsyncCallback<Serializable> callback;

        private Serializable request;

        ServiceHandlingCallback(final ServiceExecutionInfo info, final Class<? extends Service<?, ?>> serviceInterface, Serializable request,
                AsyncCallback<?> callback, int retryAttempt) {
            this.info = info;
            this.serviceInterface = serviceInterface;
            this.callback = (AsyncCallback<Serializable>) callback;
            this.request = request;
            this.retryAttempt = retryAttempt;
        }

        @Override
        public void onFailure(Throwable caught) {
            runningServicesCount--;
            try {
                if (caught instanceof RuntimeExceptionNotificationsWrapper) {
                    RuntimeExceptionNotificationsWrapper wrapper = (RuntimeExceptionNotificationsWrapper) caught;
                    if (eventBus != null) {
                        for (Serializable systemNotification : wrapper.getSystemNotifications()) {
                            eventBus.fireEvent(new SystemNotificationEvent(systemNotification));
                        }
                    }
                    caught = wrapper.getOriginal();
                }
                if (caught instanceof IncompatibleRemoteServiceException) {
                    UncaughtHandler.onUnrecoverableError(caught, "RPC." + serviceName(request, serviceInterface));
                } else if (!(caught instanceof RuntimeExceptionSerializable) && (callback instanceof RecoverableCall)
                        && (RECOVERABLE_CALL_RETRY_MAX >= retryAttempt)) {
                    log.error("Try to recover {} from service invocation error {}", serviceInterface, caught);
                    executeImpl(info, serviceInterface, request, callback, retryAttempt + 1);
                } else if (callback != null) {
                    this.callback.onFailure(caught);
                } else {
                    log.error("Server error", caught);
                }
            } catch (Throwable e) {
                UncaughtHandler.onUnrecoverableError(e, "UIonF." + serviceName(request, serviceInterface));
            } finally {
                callback = null;
                request = null;
                fireStatusChangeEvent(When.FAILURE, info, serviceInterface, callback, System.currentTimeMillis() - requestStartTime);
            }
        }

        @Override
        public void onSuccess(Serializable result) {
            runningServicesCount--;
            try {
                if (result instanceof SystemNotificationsWrapper) {
                    SystemNotificationsWrapper wrapper = (SystemNotificationsWrapper) result;
                    if (eventBus != null) {
                        for (Serializable systemNotification : wrapper.getSystemNotifications()) {
                            eventBus.fireEvent(new SystemNotificationEvent(systemNotification));
                        }
                    }
                    result = wrapper.getServiceResult();
                }
                if (callback != null) {
                    this.callback.onSuccess(result);
                }
            } catch (Throwable e) {
                UncaughtHandler.onUnrecoverableError(e, "UIonS." + serviceName(request, serviceInterface));
            } finally {
                callback = null;
                request = null;
                fireStatusChangeEvent(When.SUCCESS, info, serviceInterface, callback, System.currentTimeMillis() - requestStartTime);
            }
        }
    }

    private static String serviceName(Serializable request, Class<? extends Service<?, ?>> serviceInterface) {
        String name = GWTJava5Helper.getSimpleName(serviceInterface);
        if (request instanceof IHaveServiceCallMarker) {
            name += "." + ((IHaveServiceCallMarker) request).getServiceCallMarker();
        }
        return name;
    }

    private static void fireStatusChangeEvent(When when, ServiceExecutionInfo info, Class<? extends Service<?, ?>> serviceDescriptorClass,
            Object callbackInstance, long requestDuration) {
        if (eventBus != null) {
            eventBus.fireEvent(new RPCStatusChangeEvent(when, runningServicesCount == 0, info, serviceDescriptorClass, callbackInstance, requestDuration));
        }
    }

    public static HandlerRegistration addRPCStatusChangeHandler(RPCStatusChangeHandler handler) {
        if (eventBus == null) {
            eventBus = new SimpleEventBus();
        }
        return eventBus.addHandler(RPCStatusChangeEvent.getType(), handler);
    }

    public static HandlerRegistration addSystemNotificationHandler(SystemNotificationHandler handler) {
        if (eventBus == null) {
            eventBus = new SimpleEventBus();
        }
        return eventBus.addHandler(SystemNotificationEvent.TYPE, handler);
    }
}
