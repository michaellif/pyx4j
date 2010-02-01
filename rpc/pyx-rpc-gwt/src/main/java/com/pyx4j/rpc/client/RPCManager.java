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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.rpc.client.RPCStatusChangeEvent.When;
import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.rpc.shared.RemoteServiceAsync;
import com.pyx4j.rpc.shared.Service;

public class RPCManager {

    private static final Logger log = LoggerFactory.getLogger(RPCManager.class);

    private static final RemoteServiceAsync service;

    private static HandlerManager handlerManager;

    private static int runningServicesCount = 0;

    static {
        service = (RemoteServiceAsync) GWT.create(RemoteService.class);
    }

    public static void setServiceEntryPointURL(String url) {
        ServiceDefTarget target = (ServiceDefTarget) service;
        target.setServiceEntryPoint(url);
    }

    public static void enableAppEngineUsageStats() {
        ServiceDefTarget target = (ServiceDefTarget) service;
        target.setRpcRequestBuilder(new AppEngineUsageProcessingRpcRequestBuilder());
    }

    @SuppressWarnings("unchecked")
    public static <I extends Serializable, O extends Serializable> void executeBackground(final Class<? extends Service<I, O>> serviceInterface, I request,
            AsyncCallback<O> callback) {
        final ServiceHandlingCallback serviceHandlingCallback = new ServiceHandlingCallback(true, serviceInterface, callback);
        try {
            runningServicesCount++;
            fireStatusChangeEvent(When.START, true, serviceInterface, -1);
            service.execute(serviceInterface.getName(), request, serviceHandlingCallback);
        } catch (Throwable e) {
            serviceHandlingCallback.onFailure(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <I extends Serializable, O extends Serializable> void execute(final Class<? extends Service<I, O>> serviceInterface, I request,
            AsyncCallback<O> callback) {
        final ServiceHandlingCallback serviceHandlingCallback = new ServiceHandlingCallback(false, serviceInterface, callback);
        try {
            runningServicesCount++;
            fireStatusChangeEvent(When.START, false, serviceInterface, -1);
            service.execute(serviceInterface.getName(), request, serviceHandlingCallback);
        } catch (Throwable e) {
            serviceHandlingCallback.onFailure(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static class ServiceHandlingCallback implements AsyncCallback {

        private final long requestStartTime = System.currentTimeMillis();

        private final boolean executeBackground;

        final Class<? extends Service> serviceInterface;

        final AsyncCallback callback;

        ServiceHandlingCallback(boolean executeBackground, final Class<? extends Service> serviceInterface, AsyncCallback callback) {
            this.executeBackground = executeBackground;
            this.serviceInterface = serviceInterface;
            this.callback = callback;
        }

        @Override
        public void onFailure(Throwable caught) {
            runningServicesCount--;
            try {
                if (callback != null) {
                    this.callback.onFailure(caught);
                } else {
                    log.error("Server error", caught);
                }
            } catch (Throwable e) {
                UncaughtHandler.onUnrecoverableError(e, "UIonF." + GWTJava5Helper.getSimpleName(serviceInterface));
            } finally {
                fireStatusChangeEvent(When.FAILURE, executeBackground, serviceInterface, System.currentTimeMillis() - requestStartTime);
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
                fireStatusChangeEvent(When.SUCCESS, executeBackground, serviceInterface, System.currentTimeMillis() - requestStartTime);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void fireStatusChangeEvent(When when, boolean executeBackground, Class<? extends Service> serviceDescriptorClass, long requestDuration) {
        if (handlerManager != null) {
            handlerManager.fireEvent(new RPCStatusChangeEvent(when, runningServicesCount == 0, executeBackground, serviceDescriptorClass, requestDuration));
        }
    }

    public static HandlerRegistration addRPCStatusChangeHandler(RPCStatusChangeHandler handler) {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(null);
        }
        return handlerManager.addHandler(RPCStatusChangeEvent.getType(), handler);
    }
}
