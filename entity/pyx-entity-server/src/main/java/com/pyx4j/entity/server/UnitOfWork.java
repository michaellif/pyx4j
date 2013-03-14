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
 * Created on 2013-02-13
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitOfWork {

    private static final Logger log = LoggerFactory.getLogger(UnitOfWork.class);

    private final TransactionScopeOption transactionScopeOption;

    private final boolean backgroundProcessTransaction;

    /**
     * Start short lived Online Transaction
     */
    public UnitOfWork() {
        this(TransactionScopeOption.Nested);
    }

    /**
     * Start short lived Online Transaction
     */
    public UnitOfWork(TransactionScopeOption transactionScopeOption) {
        this(transactionScopeOption, false);
    }

    /**
     * @param transactionScopeOption
     * @param backgroundProcessTransaction
     *            as opposite to Online Transaction
     */
    public UnitOfWork(TransactionScopeOption transactionScopeOption, boolean backgroundProcessTransaction) {
        this.transactionScopeOption = transactionScopeOption;
        this.backgroundProcessTransaction = backgroundProcessTransaction;
    }

    public <R, E extends Throwable> R execute(final Executable<R, E> task) throws E {
        boolean success = false;
        try {
            Persistence.service().startTransaction(transactionScopeOption, backgroundProcessTransaction);
            Persistence.service().setAssertTransactionManangementCallOrigin();

            try {
                R rv = task.execute();
                if (transactionScopeOption != TransactionScopeOption.Suppress) {
                    Persistence.service().commit();
                }
                success = true;
                return rv;
            } finally {
                if ((!success) && (transactionScopeOption != TransactionScopeOption.Suppress)) {
                    try {
                        Persistence.service().rollback();
                    } catch (Throwable e) {
                        log.error("error during UnitOfWork {} rollback", task, e);
                    }
                }
            }
        } finally {
            Persistence.service().endTransaction();
        }
    }

    public static void addTransactionCompensationHandler(CompensationHandler handler) {
        Persistence.service().addTransactionCompensationHandler(handler);
    }
}
