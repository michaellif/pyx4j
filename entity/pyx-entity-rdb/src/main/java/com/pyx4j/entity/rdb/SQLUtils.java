/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-07-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.rdb.dialect.Dialect;

public class SQLUtils {

    private static final Logger log = LoggerFactory.getLogger(SQLUtils.class);

    public static void closeQuietly(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Throwable e) {
        }
    }

    public static void closeQuietly(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (Throwable e) {
        }
    }

    public static void closeQuietly(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (Throwable e) {
        }
    }

    public static String debugInfo(Dialect dialect, ResultSet resultSet) throws SQLException {
        return "'" + resultSet.getMetaData().getTableName(1) + "'" + " id = " + resultSet.getLong(dialect.getNamingConvention().sqlIdColumnName());
    }

    public static void logAndClearWarnings(Connection connection) throws SQLException {
        boolean warns = false;
        for (SQLWarning w = connection.getWarnings(); w != null; w = w.getNextWarning()) {
            log.warn(w.getMessage(), w);
            warns = true;
        }
        if (warns) {
            log.info("Called from {}", Trace.getCallOrigin(EntityPersistenceServiceRDB.class));
            connection.clearWarnings();
        }

    }

    public static void execute(Connection connection, String sql) throws SQLException {
        List<String> sqls = new Vector<String>();
        sqls.add(sql);
        execute(connection, sqls);
    }

    public static void execute(Connection connection, List<String> sqls) throws SQLException {
        if (sqls.size() == 0) {
            return;
        }
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            for (String sql : sqls) {
                if (sql == null) {
                    continue;
                }
                boolean success = false;
                log.debug("exec: {}", sql);
                try {
                    stmt.executeUpdate(sql);
                    success = true;
                } finally {
                    if (!success) {
                        log.error("Error executing SQL '{}'", sql);
                    }
                }
            }
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

}
