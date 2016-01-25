/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on May 22, 2015
 * @author vlads
 */
package com.pyx4j.entity.server;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.server.contexts.InheritableUserContext;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.contexts.ServerContext;

public final class Executables {

    public static <R, E extends Throwable> Executable<R, E> wrapInTargetNamespace(final String targetNamespace, final Executable<R, E> task) {
        return new Executable<R, E>() {

            @Override
            public R execute() throws E {
                final String namespace = NamespaceManager.getNamespace();
                try {
                    NamespaceManager.setNamespace(targetNamespace);
                    return task.execute();
                } finally {
                    if (namespace != null) {
                        NamespaceManager.setNamespace(namespace);
                    } else {
                        NamespaceManager.remove();
                    }
                }
            }
        };
    }

    public static <R, E extends Throwable> Executable<R, E> wrapInEntityNamespace(Class<? extends IEntity> entityClass, final Executable<R, E> task) {
        String targetNamespace = Persistence.getNamespace(entityClass);
        if (targetNamespace == null) {
            return task;
        } else {
            return wrapInTargetNamespace(targetNamespace, task);
        }
    }

    public static <R, E extends Throwable> R runInTargetNamespace(final String targetNamespace, final Executable<R, E> task) throws E {
        return wrapInTargetNamespace(targetNamespace, task).execute();
    }

    public static <R, E extends Exception> R runUnitOfWorkInTargetNamespace(final String targetNamespace, final TransactionScopeOption transactionScopeOption,
            final Executable<R, E> task) throws E {
        return runInTargetNamespace(targetNamespace, new Executable<R, E>() {
            @Override
            public R execute() throws E {
                return new UnitOfWork(transactionScopeOption).execute(task);
            }
        });
    }

    public static <R, E extends Throwable> Executable<R, E> wrapInCurrentUserContext(final Executable<R, E> task) {
        final InheritableUserContext inheritableUserContext = ServerContext.getInheritableUserContext();
        return new Executable<R, E>() {

            @Override
            public R execute() throws E {
                try {
                    Lifecycle.inheritUserContext(inheritableUserContext);
                    return task.execute();
                } finally {
                    Lifecycle.endContext();
                }
            }
        };
    }

    public static <R, E extends Throwable> Executable<R, E> wrapInElevatedUserContext(final Executable<R, E> task) {
        return new Executable<R, E>() {

            @Override
            public R execute() throws E {
                Lifecycle.startElevatedUserContext();
                try {
                    return task.execute();
                } finally {
                    Lifecycle.endElevatedUserContext();
                }
            }
        };
    }

}
