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
 * Created on Dec 29, 2009
 * @author vlads
 */
package com.pyx4j.config.server.rpc;

import java.util.List;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.Service;

public interface IServiceFactory {

    public Class<? extends Service<?, ?>> getServiceClass(String serviceInterfaceClassName) throws ClassNotFoundException;

    public Class<? extends IService> getIServiceClass(String serviceInterfaceClassName) throws ClassNotFoundException;

    public List<ServiceFilter> getServiceFilterChain(Class<? extends Service<?, ?>> serviceClass);

    public List<IServiceFilter> getIServiceFilterChain(Class<? extends IService> serviceInterfaceClass);
}
