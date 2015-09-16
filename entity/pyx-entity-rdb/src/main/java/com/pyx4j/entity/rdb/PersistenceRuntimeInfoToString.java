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
 * Created on Aug 17, 2015
 * @author vlads
 */
package com.pyx4j.entity.rdb;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;

public class PersistenceRuntimeInfoToString {

    public static String toString(PersistenceRuntimeInfo info) {
        StringBuilder b = new StringBuilder();
        b.append("databaseProductName                               : ").append(info.getDatabaseProductName()).append("\n");
        b.append("databaseProductVersion                            : ").append(info.getDatabaseProductVersion()).append("\n");

        for (ConnectionPoolType ct : ConnectionPoolType.poolable()) {
            ConnectionPoolRuntimeInfo pri = info.connectionPoolRuntimeInfo(ct);
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".NumBusyConnections", 50, ' ')).append(": ").append(pri.getNumBusyConnections()).append("\n");
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".NumIdleConnections", 50, ' ')).append(": ").append(pri.getNumIdleConnections()).append("\n");
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".NumFailedCheckouts", 50, ' ')).append(": ").append(pri.getNumFailedCheckouts()).append("\n");
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".Info", 50, ' ')).append(": ").append(pri.getStatementCacheInfo()).append("\n");
            b.append(CommonsStringUtils.paddingRight(ct.name() + ".StatementCache", 50, ' ')).append(": ").append(pri.getStatementCacheInfo()).append("\n");

        }

        return b.toString();

    }
}
