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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.rdb.ConnectionProvider;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.mapping.TableMetadata.ColumnMetadata;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class TableModel {

    public final int ORDINARY_STRING_LENGHT_MAX = 500;

    public final int ENUM_STRING_LENGHT_MAX = 50;

    private static final Logger log = LoggerFactory.getLogger(TableModel.class);

    private final EntityMeta entityMeta;

    public TableModel(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
    }

    public void ensureExists(ConnectionProvider connectionProvider) throws SQLException {
        if (!exists(connectionProvider)) {
            execute(connectionProvider, sqlCreate(connectionProvider.getDialect()));
        }
    }

    public static String sqlName(String name) {
        return name.toUpperCase(Locale.ENGLISH);
    }

    public boolean exists(ConnectionProvider connectionProvider) throws SQLException {
        Connection connection = connectionProvider.getConnection();
        ResultSet rs = null;
        try {
            DatabaseMetaData dbMeta = connection.getMetaData();
            rs = dbMeta.getTables(null, null, sqlName(entityMeta.getPersistenceName()), null);
            if (rs.next()) {
                validateAndAlter(connectionProvider, new TableMetadata(rs, dbMeta));
                return true;
            }
            return false;
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(connection);
        }
    }

    private void validateAndAlter(ConnectionProvider connectionProvider, TableMetadata tableMetadata) throws SQLException {
        List<String> alterSqls = new Vector<String>();
        for (String memberName : entityMeta.getMemberNames()) {
            MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            ColumnMetadata columnMeta = tableMetadata.getColumn(memberName);
            if (columnMeta == null) {
                StringBuilder sql = new StringBuilder("alter table ");
                sql.append(sqlName(entityMeta.getPersistenceName()));
                sql.append(" add column ");
                sql.append(sqlName(memberName)).append(' ');
                sql.append(sqlType(connectionProvider.getDialect(), memberMeta));
                alterSqls.add(sql.toString());
            } else {
                String mappingSqlType = connectionProvider.getDialect().getSqlType(memberMeta.getValueClass());
                if (!mappingSqlType.equalsIgnoreCase(columnMeta.getTypeName())) {
                    throw new RuntimeException(entityMeta.getPersistenceName() + "." + memberName + " incompatible SQL type " + columnMeta.getTypeName());
                }
            }

        }

        if (alterSqls.size() > 0) {
            execute(connectionProvider, alterSqls);
        }
    }

    List<String> sqlCreate(Dialect dialect) {
        List<String> sqls = new Vector<String>();
        StringBuilder sql = new StringBuilder();
        sql.append("create table ");
        sql.append(sqlName(entityMeta.getPersistenceName()));
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

            sql.append(' ').append(sqlName(memberName)).append(' ');
            sql.append(sqlType(dialect, memberMeta));
        }
        sql.append(')');
        sqls.add(sql.toString());

        return sqls;
    }

    String sqlType(Dialect dialect, MemberMeta memberMeta) {
        StringBuilder sql = new StringBuilder();
        sql.append(dialect.getSqlType(memberMeta.getValueClass()));
        if (Enum.class.isAssignableFrom(memberMeta.getValueClass())) {
            sql.append("(" + ENUM_STRING_LENGHT_MAX + ")");
        } else if (String.class == memberMeta.getValueClass()) {
            sql.append('(').append((memberMeta.getStringLength() == 0) ? ORDINARY_STRING_LENGHT_MAX : memberMeta.getStringLength()).append(')');
        }
        return sql.toString();
    }

    public void dropTable(ConnectionProvider connectionProvider) throws SQLException {
        List<String> sqls = new Vector<String>();
        sqls.add("drop table " + sqlName(entityMeta.getPersistenceName()));
        execute(connectionProvider, sqls);
    }

    public void execute(ConnectionProvider connectionProvider, List<String> sqls) throws SQLException {
        Connection connection = connectionProvider.getConnection();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            for (String sql : sqls) {
                boolean success = false;
                log.debug("exec: {}", sql);
                try {
                    stmt.executeUpdate(sql);
                    success = true;
                } finally {
                    if (!success) {
                        log.error("Error executing SQL {}", sql);
                    }
                }
            }
        } finally {
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(connection);
        }
    }

}
