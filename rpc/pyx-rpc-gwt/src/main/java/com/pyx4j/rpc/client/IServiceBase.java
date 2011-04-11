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
 * @version $Id$
 */
package com.pyx4j.rpc.client;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.IServiceAdapter;
import com.pyx4j.rpc.shared.IServiceRequest;
import com.pyx4j.rpc.shared.Service;

public abstract class IServiceBase implements IService {

    protected static final Logger log = LoggerFactory.getLogger(IServiceBase.class);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected final void execute(String serviceClassId, String serviceMethodId, AsyncCallback<? extends Serializable> callback, Serializable... args) {
        log.trace("RPC CALL {}.{}", serviceClassId, serviceMethodId);
        RPCManager.execute((Class<? extends Service<IServiceRequest, Serializable>>) IServiceAdapter.class, new IServiceRequest(serviceClassId,
                serviceMethodId, args), (AsyncCallback) callback);
    }

}
