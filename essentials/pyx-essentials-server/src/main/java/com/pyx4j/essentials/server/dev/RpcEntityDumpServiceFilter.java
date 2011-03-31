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
 * Created on 2011-03-18
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.dev;

import java.io.Serializable;

import com.pyx4j.config.server.rpc.IServiceFilter;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.rpc.shared.IServiceRequest;
import com.pyx4j.rpc.shared.Service;

public class RpcEntityDumpServiceFilter implements IServiceFilter {

    @Override
    public Serializable filterIncomming(Class<? extends Service<?, ?>> serviceClass, Serializable request) {
        if (request instanceof IEntity) {
            DataDump.dump("got", (IEntity) request);
        } else if (request instanceof IServiceRequest) {
            for (Serializable arg : ((IServiceRequest) request).getArgs()) {
                if (arg instanceof IEntity) {
                    DataDump.dump("got", (IEntity) arg);
                }
            }
        }
        return request;
    }

    @Override
    public Serializable filterOutgoing(Class<? extends Service<?, ?>> serviceClass, Serializable response) {
        if (response instanceof IEntity) {
            DataDump.dump("send", (IEntity) response);
        }
        return response;
    }

}
