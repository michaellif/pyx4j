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
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.rpc.impl.Serializer;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.rpc.client.RPCStatusChangeEvent.When;
import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.rpc.shared.RemoteServiceAsync;
import com.pyx4j.rpc.shared.Service;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.security.shared.SecurityViolationException;
import com.pyx4j.serialization.client.RemoteServiceSerializer;

public class RPCManager {

    private static final Logger log = LoggerFactory.getLogger(RPCManager.class);

    private static final RemoteServiceAsync service;

    private static final RemoteServiceSerializer serializer;

    private static HandlerManager handlerManager;

    private static int runningServicesCount = 0;

    private static final int RECOVERABLE_CALL_RETRY_MAX = 3;

    private static PyxRpcRequestBuilder requestBuilder;

    static {
        service = (RemoteServiceAsync) GWT.create(RemoteService.class);
        serializer = GWT.create(RPCSerializer.class);
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

    public static Serializer getSerializer() {
        return serializer.getSerializer();
    }

    public static <I extends Serializable, O extends Serializable> void executeBackground(final Class<? extends Service<I, O>> serviceInterface, I request,
            AsyncCallback<O> callback) {
        executeImpl(serviceInterface, request, callback, true, 0);
    }

    public static <I extends Serializable, O extends Serializable> void execute(final Class<? extends Service<I, O>> serviceInterface, I request,
            AsyncCallback<O> callback) {
        executeImpl(serviceInterface, request, callback, false, 0);
    }

    @SuppressWarnings("unchecked")
    private static void executeImpl(final Class<? extends Service> serviceInterface, Serializable request, AsyncCallback<?> callback,
            boolean executeBackground, int retryAttempt) {
        final ServiceHandlingCallback serviceHandlingCallback = new ServiceHandlingCallback(serviceInterface, request, callback, executeBackground,
                retryAttempt);
        try {
            runningServicesCount++;
            fireStatusChangeEvent(When.START, executeBackground, serviceInterface, callback, -1);
            requestBuilder.executing(serviceInterface);
            service.execute(serviceInterface.getName(), request, serviceHandlingCallback);
        } catch (Throwable e) {
            serviceHandlingCallback.onFailure(e);
        }
    }

    public static boolean isRecoverableAppengineFailure(Throwable caught) {
        return ((caught instanceof StatusCodeException) && (((StatusCodeException) caught).getStatusCode()) == Response.SC_INTERNAL_SERVER_ERROR);
    }

    @SuppressWarnings("unchecked")
    private static class ServiceHandlingCallback implements AsyncCallback {

        private final long requestStartTime = System.currentTimeMillis();

        private final boolean executeBackground;

        private final int retryAttempt;

        final Class<? extends Service> serviceInterface;

        private AsyncCallback callback;

        private Serializable request;

        ServiceHandlingCallback(final Class<? extends Service> serviceInterface, Serializable request, AsyncCallback callback, boolean executeBackground,
                int retryAttempt) {
            this.executeBackground = executeBackground;
            this.serviceInterface = serviceInterface;
            this.callback = callback;
            this.request = request;
            this.retryAttempt = retryAttempt;
        }

        @Override
        public void onFailure(Throwable caught) {
            runningServicesCount--;
            try {
                if (caught instanceof IncompatibleRemoteServiceException) {
                    UncaughtHandler.onUnrecoverableError(caught, "RPC." + GWTJava5Helper.getSimpleName(serviceInterface));
                } else if ((!(caught instanceof UnRecoverableRuntimeException)) && (!(caught instanceof SecurityViolationException))
                        && (callback instanceof RecoverableCall) && (RECOVERABLE_CALL_RETRY_MAX >= retryAttempt)) {
                    log.error("Try to recover {} from service invocation error {}", serviceInterface, caught);
                    executeImpl(serviceInterface, request, callback, executeBackground, retryAttempt + 1);
                } else if (callback != null) {
                    this.callback.onFailure(caught);
                } else {
                    log.error("Server error", caught);
                }
            } catch (Throwable e) {
                UncaughtHandler.onUnrecoverableError(e, "UIonF." + GWTJava5Helper.getSimpleName(serviceInterface));
            } finally {
                callback = null;
                request = null;
                fireStatusChangeEvent(When.FAILURE, executeBackground, serviceInterface, callback, System.currentTimeMillis() - requestStartTime);
            }
        }

        @Override
        public void onSuccess(Object result) {
            runningServicesCount--;
            try {
                if (callback != null) {
                    this.callback.onSuccess(result);
                }
            } catch (Throwable e) {
                UncaughtHandler.onUnrecoverableError(e, "UIonS." + GWTJava5Helper.getSimpleName(serviceInterface));
            } finally {
                callback = null;
                request = null;
                fireStatusChangeEvent(When.SUCCESS, executeBackground, serviceInterface, callback, System.currentTimeMillis() - requestStartTime);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void fireStatusChangeEvent(When when, boolean executeBackground, Class<? extends Service> serviceDescriptorClass, Object callbackInstance,
            long requestDuration) {
        if (handlerManager != null) {
            handlerManager.fireEvent(new RPCStatusChangeEvent(when, runningServicesCount == 0, executeBackground, serviceDescriptorClass, callbackInstance,
                    requestDuration));
        }
    }

    public static HandlerRegistration addRPCStatusChangeHandler(RPCStatusChangeHandler handler) {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(null);
        }
        return handlerManager.addHandler(RPCStatusChangeEvent.getType(), handler);
    }
}
