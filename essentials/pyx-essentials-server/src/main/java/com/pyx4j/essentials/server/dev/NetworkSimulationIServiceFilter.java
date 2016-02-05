/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Feb 4, 2016
 * @author vlads
 */
package com.pyx4j.essentials.server.dev;

import java.io.Serializable;

import com.pyx4j.config.server.rpc.IServiceFilter;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.rpc.admin.IsIgnoreNetworkSimulationService;
import com.pyx4j.essentials.rpc.admin.NetworkServiceSimulation;
import com.pyx4j.essentials.rpc.admin.NetworkServiceSimulation.InterfaceClassNamePattern;
import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.IServiceRequest;

public class NetworkSimulationIServiceFilter implements IServiceFilter {

    private static NetworkServiceSimulation networkSimulationConfig = EntityFactory.create(NetworkServiceSimulation.class);

    private final static Object dellayLock = new Object();

    public static NetworkServiceSimulation getNetworkSimulationConfig() {
        return networkSimulationConfig;
    }

    public static void setNetworkSimulationConfig(NetworkServiceSimulation networkSimulationConfig) {
        NetworkSimulationIServiceFilter.networkSimulationConfig = networkSimulationConfig;
    }

    @Override
    public void filterIncomming(IServiceRequest request, String serviceInterfaceClassName, IService serviceInstance) {
        if (serviceInstance instanceof IsIgnoreNetworkSimulationService) {
            return;
        }
        if (!networkSimulationConfig.enabled().getValue(false)) {
            return;
        }

        boolean match = false;

        if (networkSimulationConfig.interfacePatterns().isEmpty()) {
            match = true;
        } else {
            for (InterfaceClassNamePattern interfacePattern : networkSimulationConfig.interfacePatterns()) {
                if (!interfacePattern.classNamePattern().isNull()) {
                    if (!serviceInterfaceClassName.matches(interfacePattern.classNamePattern().getValue())) {
                        continue;
                    }
                }

                if (!interfacePattern.methodNamePattern().isNull()) {
                    if (!request.getServiceMethodId().matches(interfacePattern.methodNamePattern().getValue())) {
                        continue;
                    }
                }
                match = true;
                break;
            }
        }

        if (!match) {
            return;
        }

        if (networkSimulationConfig.delay().getValue(0) > 0) {
            try {
                synchronized (dellayLock) {
                    dellayLock.wait(networkSimulationConfig.delay().getValue());
                }
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public Serializable filterOutgoing(IServiceRequest request, String serviceInterfaceClassName, IService serviceInstance, Serializable result) {
        return result;
    }

}
