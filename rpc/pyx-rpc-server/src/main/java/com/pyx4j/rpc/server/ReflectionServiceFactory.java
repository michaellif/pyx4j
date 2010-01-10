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
 * @version $Id$
 */
package com.pyx4j.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.rpc.shared.Service;

/**
 * Default factory. Finds Service implementation in '.server.' package and adds 'Impl' to
 * interface name.
 */
public class ReflectionServiceFactory implements IServiceFactory {

    private static final Logger log = LoggerFactory.getLogger(ReflectionServiceFactory.class);

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Service<?, ?>> getServiceClass(String serviceInterfaceClassName) throws ClassNotFoundException {
        String serviceImplClassName;
        if (serviceInterfaceClassName.contains(".shared.")) {
            serviceImplClassName = serviceInterfaceClassName.replace(".shared.", ".server.") + "Impl";
        } else if (serviceInterfaceClassName.contains(".client.")) {
            serviceImplClassName = serviceInterfaceClassName.replace(".client.", ".server.") + "Impl";
        } else if (serviceInterfaceClassName.contains(".rpc.")) {
            serviceImplClassName = serviceInterfaceClassName.replace(".rpc.", ".server.") + "Impl";
        } else {
            serviceImplClassName = serviceInterfaceClassName + "Impl";
        }
        serviceImplClassName = serviceImplClassName.replace("$", "Impl$");

        Class<? extends Service<?, ?>> serviceClass = null;
        try {
            serviceClass = (Class<? extends Service<?, ?>>) Class.forName(serviceImplClassName);
        } catch (Throwable e) {
            log.error("RPC Service Class load error", e);
            throw new ClassNotFoundException("RPC Service " + serviceImplClassName + " not avalable");
        }
        return serviceClass;
    }

}
