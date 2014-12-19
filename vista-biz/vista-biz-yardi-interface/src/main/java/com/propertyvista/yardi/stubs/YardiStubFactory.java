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
package com.propertyvista.yardi.stubs;

import java.util.HashMap;
import java.util.Map;

import com.propertyvista.yardi.YardiInterface;

public class YardiStubFactory {

    private static Map<Class<?>, Map<String, Class<?>>> registeredImplementations = null;

    @SuppressWarnings("unchecked")
    public static <T extends YardiInterface> T create(Class<T> interfaceClass) {
        String interfaceClassName = interfaceClass.getName();
        interfaceClassName = interfaceClassName + "Proxy";
        try {
            return (T) Class.forName(interfaceClassName).newInstance();
        } catch (Throwable e) {
            throw new RuntimeException("Can't create " + interfaceClassName, e);
        }
    }

    static <T> T getStub(Class<T> interfaceClass) {
        return getStub(interfaceClass, null);
    }

    @SuppressWarnings("unchecked")
    static <T> T getStub(Class<T> interfaceClass, String version) {
        if (registeredImplementations != null) {
            Map<String, Class<?>> versions = registeredImplementations.get(interfaceClass);
            if (versions != null) {
                Class<T> klass = (Class<T>) versions.get(version);
                if (klass != null) {
                    try {
                        return klass.newInstance();
                    } catch (Throwable e) {
                        throw new RuntimeException("Can't create " + klass.getName(), e);
                    }
                }
            }
        }

        // construct default class name
        String interfaceClassName = interfaceClass.getName();
        interfaceClassName = interfaceClassName + "Impl";
        Class<T> klass = null;
        try {
            if (version != null) {
                try {
                    int lastDot = interfaceClassName.lastIndexOf(".");
                    String interfaceClassNameVer = interfaceClassName.subSequence(0, lastDot) + "." + version + interfaceClassName.substring(lastDot);
                    klass = (Class<T>) Class.forName(interfaceClassNameVer);
                } catch (ClassNotFoundException ignore) {
                }
            }
            // if no class found use generic impl
            if (klass == null) {
                klass = (Class<T>) Class.forName(interfaceClassName);
            }
            return klass.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException("Can't create " + interfaceClassName, e);
        }

    }

    /**
     * Should be used only during unit tests
     */
    public static <T extends YardiInterface> void register(Class<T> interfaceCalss, Class<? extends T> implClass) {
        register(interfaceCalss, implClass, null);
    }

    public static <T extends YardiInterface> void register(Class<T> interfaceClass, Class<? extends T> implClass, String version) {

        if (registeredImplementations == null) {
            registeredImplementations = new HashMap<Class<?>, Map<String, Class<?>>>();
        }
        Map<String, Class<?>> versions = registeredImplementations.get(interfaceClass);
        if (versions == null) {
            registeredImplementations.put(interfaceClass, versions = new HashMap<String, Class<?>>());
        }
        versions.put(version, implClass);
    }

    public static <T> void unregister(Class<T> interfaceCalss) {
        registeredImplementations.remove(interfaceCalss);
    }
}
