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

import com.pyx4j.commons.RuntimeExceptionSerializable;

public class ServerSideFactory {

    /**
     * Created new instance of interface implementation by finding class name+Factory first, if not exists finds name+Impl
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> interfaceCalss) {
        String interfaceClassName = interfaceCalss.getName();
        if (interfaceClassName.contains(".shared.")) {
            interfaceClassName = interfaceClassName.replace(".shared.", ".server.");
        }

        String factoryClassName = interfaceClassName + "Factory";
        try {
            Class<FacadeFactory<T>> factoryKlass = (Class<FacadeFactory<T>>) Class.forName(factoryClassName);
            FacadeFactory<T> factory = factoryKlass.newInstance();
            return factory.create();
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
}
