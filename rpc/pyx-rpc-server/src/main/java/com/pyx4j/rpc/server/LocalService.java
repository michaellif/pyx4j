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
 * Created on Feb 15, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.server;

import java.lang.reflect.Proxy;

import com.pyx4j.rpc.shared.IService;

/**
 * Secure factory to invoke services inside the same JVM
 */
public class LocalService {

    /**
     * This function does not throw exceptions, All Exceptions are reported via callback.
     * 
     * @param serviceClass
     * @return proxy to instance of the service that can be called
     */
    public static <E extends IService> E create(Class<E> serviceClass) {
        Class<?>[] allInterfaces = new Class[serviceClass.getInterfaces().length + 1];
        allInterfaces[0] = serviceClass;
        System.arraycopy(serviceClass.getInterfaces(), 0, allInterfaces, 1, serviceClass.getInterfaces().length);
        return serviceClass.cast(Proxy
                .newProxyInstance(Thread.currentThread().getContextClassLoader(), allInterfaces, new ServiceInvocationProxy(serviceClass)));
    }

}
