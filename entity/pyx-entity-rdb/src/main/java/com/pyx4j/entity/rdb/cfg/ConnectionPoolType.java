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
 * Created on 2013-04-13
 * @author vlads
 */
package com.pyx4j.entity.rdb.cfg;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.server.ConnectionTarget;

/**
 * This defines what connection pool would be used.
 */
public enum ConnectionPoolType {

    /**
     * Short lived web request transactions. 1 minute.
     */
    Web,

    /**
     * Transactions started from BackgroundProcess.
     * Transaction duration is limited to ~10 minutes to accommodate external systems connection and complex internal processing.
     */
    TransactionProcessing,

    /**
     * Long lived transactions, may take days.
     */
    BackgroundProcess,

    /**
     * Internal administrative transactions.
     * Table structure update and verification is done in this connection.
     * This corresponds to UnPooled DataSource.
     */
    DDL;

    public static Collection<ConnectionPoolType> poolable() {
        return EnumSet.of(ConnectionPoolType.Web, ConnectionPoolType.BackgroundProcess, ConnectionPoolType.TransactionProcessing);
    }

    public static ConnectionPoolType translate(ConnectionTarget connectionTarget) {
        switch (connectionTarget) {
        case Web:
            return ConnectionPoolType.Web;
        case TransactionProcessing:
            return ConnectionPoolType.TransactionProcessing;
        case BackgroundProcess:
            return ConnectionPoolType.BackgroundProcess;
        }
        throw new IllegalArgumentException();
    }

}
