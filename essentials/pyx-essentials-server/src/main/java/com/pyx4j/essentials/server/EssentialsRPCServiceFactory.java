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
 * Created on Feb 12, 2010
 * @author vlads
 */
package com.pyx4j.essentials.server;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.config.server.rpc.IServiceFilter;
import com.pyx4j.config.server.rpc.ServiceFilter;
import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.config.shared.ApplicationBackend.ApplicationBackendType;
import com.pyx4j.essentials.server.dev.NetworkSimulationIServiceFilter;
import com.pyx4j.essentials.server.dev.NetworkSimulationServiceFilter;
import com.pyx4j.essentials.server.dev.RpcEntityDumpServiceFilter;
import com.pyx4j.rpc.server.ReflectionServiceFactory;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.Service;

public class EssentialsRPCServiceFactory extends ReflectionServiceFactory {

    public EssentialsRPCServiceFactory() {
    }

    @Override
    public List<ServiceFilter> getServiceFilterChain(Class<? extends Service<?, ?>> serviceClass) {
        List<ServiceFilter> filters = new ArrayList<>();

        if (ApplicationBackend.getBackendType() == ApplicationBackendType.RDB) {
            filters.add(new RpcEntityDumpServiceFilter());
        }

        filters.addAll(super.getServiceFilterChain(serviceClass));

        if ((NetworkSimulationServiceFilter.getNetworkSimulationConfig() != null)
                && (NetworkSimulationServiceFilter.getNetworkSimulationConfig().enabled().getValue(false))) {
            filters.add(new NetworkSimulationServiceFilter());
        }
        return filters;
    }

    @Override
    public List<IServiceFilter> getIServiceFilterChain(Class<? extends IService> serviceInterfaceClass) {
        List<IServiceFilter> filters = new ArrayList<>();
        filters.addAll(super.getIServiceFilterChain(serviceInterfaceClass));

        if (NetworkSimulationIServiceFilter.getNetworkSimulationConfig().enabled().getValue(false)) {
            filters.add(new NetworkSimulationIServiceFilter());
        }

        return filters;
    }
}
