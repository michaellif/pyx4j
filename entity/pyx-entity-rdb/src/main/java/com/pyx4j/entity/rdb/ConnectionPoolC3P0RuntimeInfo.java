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

import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.PooledDataSource;

class ConnectionPoolC3P0RuntimeInfo implements ConnectionPoolRuntimeInfo {

    private DataSource dataSource;

    ConnectionPoolC3P0RuntimeInfo(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int getNumBusyConnections() {
        if (dataSource instanceof PooledDataSource) {
            try {
                return ((PooledDataSource) dataSource).getNumBusyConnectionsDefaultUser();
            } catch (SQLException e) {
                return -2;
            }
        } else {
            return -1;
        }
    }

    @Override
    public int getNumIdleConnections() {
        if (dataSource instanceof PooledDataSource) {
            try {
                return ((PooledDataSource) dataSource).getNumIdleConnectionsDefaultUser();
            } catch (SQLException e) {
                return -2;
            }
        } else {
            return -1;
        }
    }

    @Override
    public long getNumFailedCheckouts() {
        if (dataSource instanceof PooledDataSource) {
            try {
                return ((PooledDataSource) dataSource).getNumFailedCheckoutsDefaultUser();
            } catch (SQLException e) {
                return -2;
            }
        } else {
            return -1;
        }
    }

    @Override
    public String getInfo() {
        if (dataSource instanceof PooledDataSource) {
            try {
                PooledDataSource ds = ((PooledDataSource) dataSource);
                StringBuilder b = new StringBuilder();
                b.append("Active Threads ").append(ds.getThreadPoolNumActiveThreads()).append("; ");
                b.append("Tasks Pending ").append(ds.getThreadPoolNumTasksPending()).append("; ");
                return b.toString();
            } catch (SQLException e) {
                return "n/a";
            }
        } else {
            return null;
        }
    }

    @Override
    public String getStatementCacheInfo() {
        if (dataSource instanceof PooledDataSource) {
            try {
                PooledDataSource ds = ((PooledDataSource) dataSource);
                StringBuilder b = new StringBuilder();
                b.append("Cached ").append(ds.getStatementCacheNumStatementsDefaultUser()).append("; ");
                b.append("Checked Out ").append(ds.getStatementCacheNumCheckedOutDefaultUser()).append("; ");
                b.append("Destroyer Tasks Pending ").append(ds.getStatementDestroyerNumTasksPending()).append("; ");
                return b.toString();
            } catch (SQLException e) {
                return "n/a";
            }
        } else {
            return null;
        }
    }

}
