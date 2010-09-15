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
 * Created on 2010-09-15
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.dev;

import java.io.Serializable;

import com.pyx4j.config.server.rpc.IServiceFilter;
import com.pyx4j.essentials.rpc.admin.IsIgnoreNetworkSimulationService;
import com.pyx4j.essentials.rpc.admin.NetworkSimulation;
import com.pyx4j.rpc.shared.Service;

public class NetworkSimulationServiceFilter implements IServiceFilter {

    private static NetworkSimulation networkSimulationConfig;

    private final static Object dellayLock = new Object();

    @Override
    public Serializable filterIncomming(Class<? extends Service<?, ?>> serviceClass, Serializable request) {
        return request;
    }

    @Override
    public Serializable filterOutgoing(Class<? extends Service<?, ?>> serviceClass, Serializable response) {
        if (IsIgnoreNetworkSimulationService.class.isAssignableFrom(serviceClass)) {
            return response;
        }

        if (networkSimulationConfig.delay().isBooleanTrue()) {
            try {
                synchronized (dellayLock) {
                    dellayLock.wait(networkSimulationConfig.delay().getValue());
                }
            } catch (InterruptedException e) {
            }
        }

        return response;
    }

    public static NetworkSimulation getNetworkSimulationConfig() {
        return networkSimulationConfig;
    }

    public static void setNetworkSimulationConfig(NetworkSimulation networkSimulationConfig) {
        NetworkSimulationServiceFilter.networkSimulationConfig = networkSimulationConfig;
    }

}
