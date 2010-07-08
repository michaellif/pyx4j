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
package com.pyx4j.entity.rdb.mapping;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.rdb.ConnectionProvider;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class TableModel {

    public final int ORDINARY_STRING_LENGHT_MAX = 500;

    private static final Logger log = LoggerFactory.getLogger(TableModel.class);

    private final EntityMeta entityMeta;

    public TableModel(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
    }

    public void ensureExists(ConnectionProvider connectionProvider) throws SQLException {
        execute(connectionProvider, sqlCreate(connectionProvider.getDialect()));
    }

    String sqlCreate(Dialect dialect) {
        StringBuilder sql = new StringBuilder();
        sql.append("create table ");
        sql.append(entityMeta.getPersistenceName());
        sql.append(" (");
        boolean first = true;
        for (String memberName : entityMeta.getMemberNames()) {
            MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                sql.append(',');
            }

            sql.append(' ').append(memberName).append(' ');
            appendSqlType(sql, dialect, memberMeta);
        }
        sql.append(')');
        return sql.toString();
    }

    void appendSqlType(StringBuilder sql, Dialect dialect, MemberMeta memberMeta) {
        if (Enum.class.isAssignableFrom(memberMeta.getValueClass())) {
            sql.append(dialect.getSqlType(String.class)).append("(50)");
        } else {
            sql.append(dialect.getSqlType(memberMeta.getValueClass()));
            if (String.class == memberMeta.getValueClass()) {
                sql.append('(').append((memberMeta.getStringLength() == 0) ? ORDINARY_STRING_LENGHT_MAX : memberMeta.getStringLength()).append(')');
            }
        }
    }

    public void dropTable(ConnectionProvider connectionProvider) throws SQLException {
        execute(connectionProvider, "drop table " + entityMeta.getPersistenceName());
    }

    public void execute(ConnectionProvider connectionProvider, String sql) throws SQLException {
        Connection connection = connectionProvider.getConnection();
        Statement stmt = null;
        boolean success = false;
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            success = true;
        } finally {
            if (!success) {
                log.error("Error executing SQL {}", sql);
            }
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(connection);
        }
    }
}
