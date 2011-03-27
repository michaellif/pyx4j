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
 * Created on 2011-03-25
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.server;

import java.lang.reflect.Proxy;

import com.pyx4j.rpc.shared.IService;

public class TestServiceFactory {

    public static <T extends IService> T create(Class<T> classLiteral) {
        Class<?>[] allInterfaces = new Class[classLiteral.getInterfaces().length + 1];
        allInterfaces[0] = classLiteral;
        System.arraycopy(classLiteral.getInterfaces(), 0, allInterfaces, 1, classLiteral.getInterfaces().length);
        return classLiteral.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), allInterfaces, new IServiceMockProxy(classLiteral)));
    }

}
