/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.InheritableUserContext;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;

public class TaskRunner {

    public static <T> T runInAdminNamespace(final Callable<T> task) {
        final String namespace = NamespaceManager.getNamespace();
        try {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
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
            NamespaceManager.setNamespace(namespace);
        }
    }

    public static <T> T runAutonomousTransation(final Callable<T> task) {
        return runAutonomousTransation(null, task);
    }

    public static <T> T runAutonomousTransation(final String targetNamespace, final Callable<T> task) {
        final InheritableUserContext inheritableUserContext = Context.getInheritableUserContext();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {

            Future<T> futureResult = executorService.submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    Lifecycle.inheritUserContext(inheritableUserContext);
                    try {
                        if (targetNamespace != null) {
                            NamespaceManager.setNamespace(targetNamespace);
                        }
                        Persistence.service().startTransaction();
                        T rv = task.call();
                        return rv;
                    } finally {
                        try {
                            Persistence.service().endTransaction();
                        } finally {
                            Lifecycle.endContext();
                        }
                    }
                }
            });

            try {
                return futureResult.get();
            } catch (InterruptedException e) {
                throw new Error(e);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                } else if (e.getCause() instanceof Error) {
                    throw (Error) e.getCause();
                } else {
                    throw new Error(e);
                }
            }

        } finally {
            executorService.shutdownNow();
        }
    }
}
