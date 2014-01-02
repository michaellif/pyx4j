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
package com.propertyvista.server;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.InheritableUserContext;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;

public class TaskRunner {

    private static final Logger log = LoggerFactory.getLogger(TaskRunner.class);

    public static <T> T runInOperationsNamespace(final Callable<T> task) {
        return NamespaceManager.runInTargetNamespace(VistaNamespace.operationsNamespace, task);
    }

    public static <T> T runInTargetNamespace(final Pmc pmc, final Callable<T> task) {
        Validate.notEmpty(pmc.namespace().getValue());
        return NamespaceManager.runInTargetNamespace(pmc.namespace().getValue(), task);
    }

    public static <T> T runInTargetNamespace(final String targetNamespace, final Callable<T> task) {
        Validate.notEmpty(targetNamespace);
        return NamespaceManager.runInTargetNamespace(targetNamespace, task);
    }

    public static <R, E extends Exception> R runUnitOfWorkInOperationstNamespace(TransactionScopeOption transactionScopeOption, Executable<R, E> task) {
        return runUnitOfWorkInTargetNamespace(VistaNamespace.operationsNamespace, transactionScopeOption, task);
    }

    public static <R, E extends Exception> R runUnitOfWorkInTargetNamespace(final String targetNamespace, final TransactionScopeOption transactionScopeOption,
            final Executable<R, E> task) {
        return NamespaceManager.runInTargetNamespace(targetNamespace, new Callable<R>() {
            @Override
            public R call() throws E {
                return new UnitOfWork(transactionScopeOption).execute(task);
            }
        });
    }

    public static <T> T runAutonomousTransation(final Callable<T> task) {
        return runAutonomousTransation(null, task);
    }

    public static <T> T runAutonomousTransation(final String targetNamespace, final Callable<T> task) {
        final InheritableUserContext inheritableUserContext = Context.getInheritableUserContext();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            synchronized (task.getClass()) {
                Future<T> futureResult = executorService.submit(new Callable<T>() {
                    @Override
                    public T call() throws Exception {
                        Lifecycle.inheritUserContext(inheritableUserContext);
                        boolean success = false;
                        try {
                            if (targetNamespace != null) {
                                NamespaceManager.setNamespace(targetNamespace);
                            }
                            Persistence.service().startTransaction();
                            try {
                                T rv = task.call();
                                success = true;
                                return rv;
                            } finally {
                                if (!success) {
                                    try {
                                        Persistence.service().rollback();
                                    } catch (Throwable e) {
                                        log.error("error during task {} rollback", task, e);
                                    }
                                }
                            }
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
            }

        } finally {
            executorService.shutdownNow();
        }
    }
}
