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
 * Created on 2010-09-22
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.config.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.RuntimeExceptionSerializable;

public class ServerSideFactory {

    private static final Logger log = LoggerFactory.getLogger(ServerSideFactory.class);

    private static Map<Class<?>, Class<?>> registeredImplementations = null;

    /**
     * Created new instance of interface implementation by finding class name+Factory first, if not exists finds name+Impl
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> interfaceCalss) {
        if (registeredImplementations != null) {
            Class<T> klass = (Class<T>) registeredImplementations.get(interfaceCalss);
            if (klass != null) {
                try {
                    return klass.newInstance();
                } catch (Throwable e) {
                    throw new RuntimeException("Can't create " + klass.getName(), e);
                }
            }
        }
        return createDefaultImplementation(interfaceCalss);
    }

    /**
     * Should be used only during unit tests
     */
    @SuppressWarnings("unchecked")
    public static <T> T createDefaultImplementation(Class<T> interfaceCalss) {
        String interfaceClassName = interfaceCalss.getName();
        if (interfaceClassName.contains(".shared.")) {
            interfaceClassName = interfaceClassName.replace(".shared.", ".server.");
        }

        String factoryClassName = interfaceClassName + "Factory";
        try {
            Class<FacadeFactory<T>> factoryKlass = (Class<FacadeFactory<T>>) Class.forName(factoryClassName);
            FacadeFactory<T> factory = factoryKlass.newInstance();
            return factory.getFacade();
        } catch (ClassNotFoundException ignore) {

        } catch (Throwable e) {
            if (e instanceof RuntimeExceptionSerializable) {
                throw (RuntimeExceptionSerializable) e;
            } else {
                throw new RuntimeException("Can't create " + factoryClassName, e);
            }
        }

        if (interfaceCalss.getSimpleName().startsWith("I") && Character.isUpperCase(interfaceCalss.getSimpleName().charAt(1))) {
            interfaceClassName = interfaceClassName.replace(interfaceCalss.getSimpleName(), "Server" + interfaceCalss.getSimpleName().substring(1));
        } else {
            interfaceClassName = interfaceClassName + "Impl";
        }
        try {
            Class<T> klass = (Class<T>) Class.forName(interfaceClassName);
            return klass.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException("Can't create " + interfaceClassName, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createEmptyImplementation(Class<T> interfaceCalss) {
        try {
            return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { interfaceCalss }, new InvocationHandler() {

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return null;
                }
            });
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Can't create " + interfaceCalss.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createSafeImplementation(final Class<T> interfaceCalss, final T instance) {
        try {
            return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { interfaceCalss }, new InvocationHandler() {

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    try {
                        return method.invoke(instance, args);
                    } catch (InvocationTargetException e) {
                        log.error("failed to call {}.{}", instance.getClass(), method.getName(), e.getCause());
                        return null;
                    } catch (Throwable e) {
                        log.error("failed to call {}.{}", instance.getClass(), method.getName(), e);
                        return null;
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Can't create " + interfaceCalss.getName(), e);
        }
    }

    /**
     * Should be used only during unit tests
     */
    public static <T> void register(Class<T> interfaceCalss, Class<? extends T> implCalss) {
        if (registeredImplementations == null) {
            registeredImplementations = new HashMap<Class<?>, Class<?>>();
        }
        registeredImplementations.put(interfaceCalss, implCalss);
    }

    public static <T> void unregister(Class<T> interfaceCalss) {
        registeredImplementations.remove(interfaceCalss);
    }
}
