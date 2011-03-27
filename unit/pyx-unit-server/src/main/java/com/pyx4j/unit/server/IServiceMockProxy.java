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
 * Created on 2011-03-25
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.server;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.rpc.server.IServiceAdapterImpl;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.IServiceRequest;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;

class IServiceMockProxy implements java.lang.reflect.InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(IServiceMockProxy.class);

    private final Class<? extends IService> serviceInterfaceClass;

    IServiceMockProxy(Class<? extends IService> serviceInterfaceClass) {
        this.serviceInterfaceClass = serviceInterfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        @SuppressWarnings({ "unchecked" })
        AsyncCallback<Serializable> callback = (AsyncCallback<Serializable>) args[0];

        Serializable[] serviceArgs = new Serializable[args.length - 1];
        System.arraycopy(args, 1, serviceArgs, 0, args.length - 1);
        IServiceRequest request = new IServiceRequest(serviceInterfaceClass.getName(), method.getName(), serviceArgs);

        IServiceAdapterImpl srv = new IServiceAdapterImpl();

        try {
            Serializable result = srv.execute(request);
            callback.onSuccess(result);
        } catch (Throwable e) {
            log.info("service {} call error", request.getServiceCallMarker(), e);
            if (e instanceof RuntimeExceptionSerializable) {
                callback.onFailure(e);
            } else {
                callback.onFailure(new UnRecoverableRuntimeException(e.getMessage()));
            }
        }

        return null;
    }
}
