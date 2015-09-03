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

import com.pyx4j.entity.rdb.cfg.ConnectionPoolType;

public class PersistenceRuntimeInfo {

    private ConnectionProvider connectionProvider;

    String databaseProductVersion;

    String databaseProductName;

    PersistenceRuntimeInfo(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public String getDatabaseProductVersion() {
        return databaseProductVersion;
    }

    public ConnectionPoolRuntimeInfo connectionPoolRuntimeInfo(ConnectionPoolType connectionType) {
        return connectionProvider.connectionPoolRuntimeInfo(connectionType);
    }

    @Override
    public String toString() {
        return PersistenceRuntimeInfoToString.toString(this);
    }
}
