/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-02-25
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

/**
 * {@link http://docs.oracle.com/javaee/5/tutorial/doc/bncij.html}
 */
public enum TransactionScopeOption {

    /**
     * Start new transaction if not exists,
     * Creates save point within existing transaction.
     * 
     * This is the default value.
     */
    Nested,

    /**
     * @deprecated TODO VladS implement this
     * 
     *             A transaction is required by the scope. It uses an ambient transaction if one already exists. Otherwise, it creates a new transaction before
     *             entering the
     *             scope.
     * 
     *             Do not create save point if transaction exists.
     * 
     *             If started within another transaction commit is ignored.
     */
    @Deprecated
    Required,

    /**
     * A new transaction is always created for the scope.
     * The ambient transaction context is suppressed when creating the scope.
     */
    RequiresNew,

    /**
     * If the client is running within exiting transaction context. If the transaction context is not
     * associated with a transaction, the container throws the TransactionRequiredException.
     */
    Mandatory,

    /**
     * The ambient transaction context is suppressed when creating the scope. All operations within the scope are done without an ambient transaction context.
     * This translates to JDBC auto-commit.
     */
    Suppress;

}
