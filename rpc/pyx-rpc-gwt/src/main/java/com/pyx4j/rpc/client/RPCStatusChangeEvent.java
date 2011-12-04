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
 * Created on Jan 22, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.client;

import com.google.gwt.event.shared.GwtEvent;

import com.pyx4j.rpc.shared.Service;
import com.pyx4j.rpc.shared.ServiceExecution;

public class RPCStatusChangeEvent extends GwtEvent<RPCStatusChangeHandler> {

    private static final Type<RPCStatusChangeHandler> TYPE = new Type<RPCStatusChangeHandler>();

    private final boolean rpcIdle;

    public static enum When {
        START, FAILURE, SUCCESS
    }

    private final When when;

    private final ServiceExecutionInfo serviceExecutionInfo;

    private final Class<? extends Service<?, ?>> serviceDescriptorClass;

    private final Object callbackInstance;

    private final long requestDuration;

    public RPCStatusChangeEvent(When when, boolean rpcIdle, ServiceExecutionInfo info, Class<? extends Service<?, ?>> serviceDescriptorClass,
            Object callbackInstance, long requestDuration) {
        super();
        this.when = when;
        this.rpcIdle = rpcIdle;
        this.serviceExecutionInfo = info;
        this.serviceDescriptorClass = serviceDescriptorClass;
        this.callbackInstance = callbackInstance;
        this.requestDuration = requestDuration;
    }

    static Type<RPCStatusChangeHandler> getType() {
        return TYPE;
    }

    public boolean isRpcIdle() {
        return rpcIdle;
    }

    public Class<? extends Service<?, ?>> getServiceDescriptorClass() {
        return serviceDescriptorClass;
    }

    public ServiceExecutionInfo getServiceExecutionInfo() {
        return serviceExecutionInfo;
    }

    public boolean isRecoverableServiceCall() {
        return callbackInstance instanceof RecoverableCall;
    }

    public boolean isBlockingServiceCall() {
        return (callbackInstance instanceof BlockingCall) || (serviceExecutionInfo.operationType() == ServiceExecution.OperationType.SemiTransparent);
    }

    public long getRequestDuration() {
        return requestDuration;
    }

    @Override
    protected void dispatch(RPCStatusChangeHandler handler) {
        handler.onRPCStatusChange(this);
    }

    @Override
    public GwtEvent.Type<RPCStatusChangeHandler> getAssociatedType() {
        return TYPE;
    }

    public When getWhen() {
        return when;
    }

    public boolean isExecuteBackground() {
        return serviceExecutionInfo.operationType() == ServiceExecution.OperationType.NonBlocking;
    }

}
