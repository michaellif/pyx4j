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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.rpc.shared.RemoteServiceAsync;
import com.pyx4j.rpc.shared.Service;

public class RPCManager {

    private static final RemoteServiceAsync service;

    private static HandlerManager handlerManager;

    static {
        service = (RemoteServiceAsync) GWT.create(RemoteService.class);
    }

    public static void setServiceEntryPointURL(String url) {
        ServiceDefTarget target = (ServiceDefTarget) service;
        target.setServiceEntryPoint(url);
    }

    @SuppressWarnings("unchecked")
    public static <I extends Serializable, O extends Serializable> void execute(final Class<? extends Service<I, O>> serviceInterface, I request,
            AsyncCallback<O> callback) {
        final ServiceHandlingCallback serviceHandlingCallback = new ServiceHandlingCallback(serviceInterface, callback);
        try {
            fireStatusChangeEvent(serviceInterface, -1);
            service.execute(serviceInterface.getName(), request, serviceHandlingCallback);
        } catch (Throwable e) {
            serviceHandlingCallback.onFailure(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static class ServiceHandlingCallback implements AsyncCallback {

        final Class<? extends Service> serviceInterface;

        final AsyncCallback callback;

        ServiceHandlingCallback(final Class<? extends Service> serviceInterface, AsyncCallback callback) {
            this.serviceInterface = serviceInterface;
            this.callback = callback;
        }

        @Override
        public void onFailure(Throwable caught) {
            this.callback.onFailure(caught);
        }

        @Override
        public void onSuccess(Object result) {
            this.callback.onSuccess(result);
        }
    }

    private static void fireStatusChangeEvent(Class<? extends Service<?, ?>> serviceDescriptorClass, long requestDuration) {
        if (handlerManager != null) {
            handlerManager.fireEvent(new RPCStatusChangeEvent(true, serviceDescriptorClass, requestDuration));
        }
    }

    public static HandlerRegistration addRPCStatusChangeHandler(RPCStatusChangeHandler handler) {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(null);
        }
        return handlerManager.addHandler(RPCStatusChangeEvent.getType(), handler);
    }
}
