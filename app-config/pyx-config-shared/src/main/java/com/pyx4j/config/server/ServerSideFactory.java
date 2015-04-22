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
            Class<?> klass = registeredImplementations.get(interfaceCalss);
            if (klass != null) {
                try {
                    if (interfaceCalss.isAssignableFrom(klass)) {
                        return ((Class<T>) klass).newInstance();
                    } else if (FacadeFactory.class.isAssignableFrom(klass)) {
                        FacadeFactory<T> factory = ((Class<FacadeFactory<T>>) klass).newInstance();
                        return factory.getFacade();
                    } else {
                        throw new Error("Invalid registration");
                    }
                } catch (Throwable e) {
                    throw new RuntimeException("Can't create " + klass.getName(), e);
                }
            }
        }
        return createDefaultImplementation(interfaceCalss, false);
    }

    /**
     * Should be used only during unit tests
     */
    @SuppressWarnings("unchecked")
    public static <T> T createDefaultImplementation(Class<T> interfaceCalss, boolean injectInterceptors) {
        String interfaceClassName = interfaceCalss.getName();
        if (interfaceClassName.contains(".shared.")) {
            interfaceClassName = interfaceClassName.replace(".shared.", ".server.");
        }
        T instance;

        FacadeFactory<T> factory = null;
        // TODO Optimize Find Factory with Map
        {
            String factoryClassName = interfaceClassName + "Factory";
            try {
                Class<FacadeFactory<T>> factoryKlass = (Class<FacadeFactory<T>>) Class.forName(factoryClassName);
                factory = factoryKlass.newInstance();
            } catch (ClassNotFoundException ignore) {

            } catch (Throwable e) {
                if (e instanceof RuntimeExceptionSerializable) {
                    throw (RuntimeExceptionSerializable) e;
                } else {
                    throw new RuntimeException("Can't create " + factoryClassName, e);
                }
            }
        }

        if (factory != null) {
            instance = factory.getFacade();
        } else {
            // TODO Optimize Find Class with Map
            if (interfaceCalss.getSimpleName().startsWith("I") && Character.isUpperCase(interfaceCalss.getSimpleName().charAt(1))) {
                interfaceClassName = interfaceClassName.replace(interfaceCalss.getSimpleName(), "Server" + interfaceCalss.getSimpleName().substring(1));
            } else {
                interfaceClassName = interfaceClassName + "Impl";
            }
            try {
                Class<T> klass = (Class<T>) Class.forName(interfaceClassName);
                instance = klass.newInstance();
            } catch (Throwable e) {
                throw new RuntimeException("Can't create " + interfaceClassName, e);
            }
        }
        if (injectInterceptors) {
            // TODO Optimize interceptor creation if required
            return createInterceptorsImplementation(interfaceCalss, instance);
        } else {
            return instance;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createEmptyImplementation(final Class<T> interfaceCalss) {
        try {
            return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { interfaceCalss }, new InvocationHandler() {

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    log.debug("called empty implementation stub of {}.{}", interfaceCalss.getClass().getSimpleName(), method.getName());
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

    @SuppressWarnings("unchecked")
    public static <T> T createInterceptorsImplementation(final Class<T> interfaceCalss, final T instance) {
        try {
            return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { interfaceCalss }, new InvocationHandler() {

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    try {
                        return method.invoke(instance, args);
                    } catch (InvocationTargetException e) {
                        if (!callExceptionHandlers(interfaceCalss, instance, method, e.getCause())) {
                            throw e.getCause();
                        } else {
                            return null;
                        }
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Can't create " + interfaceCalss.getName(), e);
        }
    }

    private static <T> boolean callExceptionHandlers(final Class<T> interfaceCalss, final T instance, Method method, Throwable exception) throws Throwable {
        Class<? extends ExceptionHandler>[] exceptionHandlers = InterceptorsCache.getExceptionHandlers(interfaceCalss, instance, method);
        if (exceptionHandlers == null) {
            return false;
        } else {
            for (Class<? extends ExceptionHandler> exceptionHandler : exceptionHandlers) {
                exceptionHandler.newInstance().handle(exception);
            }
            return false;
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

    /**
     * Should be used only during unit tests
     */
    public static <T> void registerFactory(Class<T> interfaceCalss, Class<? extends FacadeFactory<T>> factoryImplCalss) {
        if (registeredImplementations == null) {
            registeredImplementations = new HashMap<Class<?>, Class<?>>();
        }
        registeredImplementations.put(interfaceCalss, factoryImplCalss);
    }

    public static <T> void unregister(Class<T> interfaceCalss) {
        registeredImplementations.remove(interfaceCalss);
    }
}
