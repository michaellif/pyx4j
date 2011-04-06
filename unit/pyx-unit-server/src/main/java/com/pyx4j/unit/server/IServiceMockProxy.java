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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.rpc.server.IServiceAdapterImpl;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.IServiceRequest;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.unit.server.mock.TestLifecycle;

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
        try {
            for (int i = 1; i < args.length; i++) {
                serviceArgs[i - 1] = serializationClone((Serializable) args[i]);
            }
        } catch (Throwable e) {
            callback.onFailure(new UnRecoverableRuntimeException(e.getMessage()));
        }
        IServiceRequest request = new IServiceRequest(serviceInterfaceClass.getName(), method.getName(), serviceArgs);

        TestLifecycle.beginRequest();

        IServiceAdapterImpl srv = new IServiceAdapterImpl();

        try {

            Serializable result;
            try {
                result = srv.execute(request);
            } finally {
                TestLifecycle.endRequest();
            }

            callback.onSuccess(serializationClone(result));
        } catch (Throwable e) {
            if (e instanceof AssertionError) {
                throw (AssertionError) e;
            }
            log.info("service {} call error", request.getServiceCallMarker(), e);
            if (e instanceof RuntimeExceptionSerializable) {
                callback.onFailure((Throwable) serializationClone(e));
            } else {
                callback.onFailure(new UnRecoverableRuntimeException(e.getMessage()));
            }
        }

        return null;
    }

    private static Serializable serializationClone(Serializable o) {
        try {
            return (Serializable) unzip(zip(o));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error("Error", e);
            throw new UnRecoverableRuntimeException("Serialization error");
        }
    }

    private static byte[] zip(Serializable o) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(buf);
            out.writeObject(o);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(buf);
        }
        return buf.toByteArray();
    }

    private static Object unzip(byte[] buf) throws IOException {
        ByteArrayInputStream b = new ByteArrayInputStream(buf);
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(b);
            return in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(b);
        }
    }
}
