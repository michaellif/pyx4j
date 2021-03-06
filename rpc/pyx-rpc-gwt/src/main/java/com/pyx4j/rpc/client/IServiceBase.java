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
package com.pyx4j.rpc.client;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.IServiceAdapter;
import com.pyx4j.rpc.shared.IServiceRequest;
import com.pyx4j.rpc.shared.Service;

public abstract class IServiceBase implements IService {

    protected static final Logger log = LoggerFactory.getLogger(IServiceBase.class);

    private static int rpcCallCount = 0;

    //TODO move to generated implementations and create only when required
    protected static final I18n i18n = I18n.get(IServiceBase.class);

    protected static final ServiceNames serviceNames = GWT.create(ServiceNames.class);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected final void execute(String serviceMethodId, int serviceMethodSignature, AsyncCallback<? extends Serializable> callback, Serializable... args) {
        log.trace("RPC CALL {} #{}", getServiceClassId() + "." + serviceMethodId, ++rpcCallCount);
        RPCManager.execute((Class<? extends Service<IServiceRequest, Serializable>>) IServiceAdapter.class,
                new IServiceRequest(getServiceClassId(), serviceMethodId, serviceMethodSignature, args, rpcCallCount), (AsyncCallback) callback);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected final void executeWithExecutionInfo(ServiceExecutionInfo info, String serviceMethodId, int serviceMethodSignature,
            AsyncCallback<? extends Serializable> callback, Serializable... args) {
        log.trace("RPC CALL {} #{}", getServiceClassId() + "." + serviceMethodId, ++rpcCallCount);
        RPCManager.execute(info, (Class<? extends Service<IServiceRequest, Serializable>>) IServiceAdapter.class,
                new IServiceRequest(getServiceClassId(), serviceMethodId, serviceMethodSignature, args, rpcCallCount), (AsyncCallback) callback);
    }

    protected final void executeCacheable(int timeoutMin, ServiceExecutionInfo info, String serviceMethodId, int serviceMethodSignature,
            AsyncCallback<? extends Serializable> callback, Serializable... args) {

        @SuppressWarnings("unchecked")
        final AsyncCallback<Serializable> callbackUntyped = (AsyncCallback<Serializable>) callback;

        // TODO use MultiKey and use args defined in ServiceCacheKey
        Object key = this.getClass();
        if (ClientCache.containsKey(key)) {
            Serializable value = ClientCache.get(key);
            callbackUntyped.onSuccess(value);
        } else {
            AsyncCallback<Serializable> cacher = new AsyncCallback<Serializable>() {

                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(Serializable result) {
                    ClientCache.put(key, result, timeoutMin);
                    callbackUntyped.onSuccess(result);
                }
            };
            executeWithExecutionInfo(info, serviceMethodId, serviceMethodSignature, cacher, args);
        }
    }

    public abstract String getServiceClassId();

}
