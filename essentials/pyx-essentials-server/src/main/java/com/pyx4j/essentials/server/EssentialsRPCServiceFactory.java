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
 * @version $Id$
 */
package com.pyx4j.essentials.server;

import java.util.List;
import java.util.Vector;

import com.pyx4j.config.server.rpc.IServiceFilter;
import com.pyx4j.essentials.server.dev.NetworkSimulationServiceFilter;
import com.pyx4j.rpc.server.ReflectionServiceFactory;
import com.pyx4j.rpc.shared.Service;

public class EssentialsRPCServiceFactory extends ReflectionServiceFactory {

    @Override
    public List<IServiceFilter> getServiceFilterChain(Class<? extends Service<?, ?>> serviceClass) {
        if ((NetworkSimulationServiceFilter.getNetworkSimulationConfig() != null)
                && (NetworkSimulationServiceFilter.getNetworkSimulationConfig().enabled().isBooleanTrue())) {
            List<IServiceFilter> filters = new Vector<IServiceFilter>();
            filters.addAll(super.getServiceFilterChain(serviceClass));
            filters.add(new NetworkSimulationServiceFilter());
            return filters;
        } else {
            return super.getServiceFilterChain(serviceClass);
        }
    }
}
