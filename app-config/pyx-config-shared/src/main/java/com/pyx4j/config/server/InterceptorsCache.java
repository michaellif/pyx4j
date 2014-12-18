/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jul 31, 2014
 * @author vlads
 */
package com.pyx4j.config.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

class InterceptorsCache {

    @SuppressWarnings("unchecked")
    private static Class<? extends ExceptionHandler>[] NOTHING = new Class[0];

    private static class InstanceCache {

        private final Map<Method, Class<? extends ExceptionHandler>[]> cache = new HashMap<>();

        Class<? extends ExceptionHandler>[] get(final Class<?> interfaceCalss, final Class<?> instance, Method method) {

            Class<? extends ExceptionHandler>[] exceptionHandlers = cache.get(method);
            if (exceptionHandlers == null) {
                exceptionHandlers = build(interfaceCalss, instance, method);
                cache.put(method, exceptionHandlers);
            }
            if (exceptionHandlers.length == 0) {
                return null;
            } else {
                return exceptionHandlers;
            }
        }

        Class<? extends ExceptionHandler>[] build(final Class<?> interfaceCalss, final Class<?> instance, Method method) {
            Method implMethod;
            try {
                implMethod = instance.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                throw new Error(e);
            }
            Interceptors interceptors = implMethod.getAnnotation(Interceptors.class);
            if (interceptors != null) {
                return interceptors.value();
            } else {
                interceptors = method.getAnnotation(Interceptors.class);
                if (interceptors != null) {
                    return interceptors.value();
                } else {
                    return NOTHING;
                }
            }
        }
    }

    private static Map<Class<?>, InstanceCache> cache = new HashMap<>();

    static <T> Class<? extends ExceptionHandler>[] getExceptionHandlers(final Class<T> interfaceCalss, final T instance, Method method) {
        InstanceCache instanceCache = cache.get(instance.getClass());
        if (instanceCache == null) {
            instanceCache = new InstanceCache();
            cache.put(instance.getClass(), instanceCache);
        }

        return instanceCache.get(interfaceCalss, instance.getClass(), method);
    }
}
