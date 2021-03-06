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
 * Created on 2011-06-12
 * @author vlads
 */
package com.pyx4j.server.contexts;

import java.util.concurrent.Callable;

import com.pyx4j.log4j.LoggerConfig;

public class NamespaceManager {

    private static final ThreadLocal<String> requestLocal = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return null;
        }
    };

    public static String getNamespace() {
        return requestLocal.get();
    }

    public static void setNamespace(String newNamespace) {
        requestLocal.set(newNamespace);
        LoggerConfig.mdcPut(LoggerConfig.MDC_namespace, newNamespace);
    }

    public static void remove() {
        requestLocal.remove();
        LoggerConfig.mdcRemove(LoggerConfig.MDC_namespace);
    }

    public static <T> T runInTargetNamespace(final String targetNamespace, final Callable<T> task) {
        final String namespace = NamespaceManager.getNamespace();
        try {
            NamespaceManager.setNamespace(targetNamespace);
            try {
                return task.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new Error(e);
                }
            }
        } finally {
            if (namespace != null) {
                NamespaceManager.setNamespace(namespace);
            } else {
                NamespaceManager.remove();
            }
        }
    }

}
