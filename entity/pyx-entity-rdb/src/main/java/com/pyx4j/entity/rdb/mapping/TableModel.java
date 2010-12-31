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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Table.PrimaryKeyStrategy;
import com.pyx4j.entity.rdb.ConnectionProvider;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.mapping.TableMetadata.ColumnMetadata;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class TableModel {

    public final int ORDINARY_STRING_LENGHT_MAX = 500;

    public final int ENUM_STRING_LENGHT_MAX = 50;

    private static final Logger log = LoggerFactory.getLogger(TableModel.class);

    private final EntityMeta entityMeta;

    private final PrimaryKeyStrategy primaryKeyStrategy;

    private String sqlInsert;

    private String sqlUpdate;

    public TableModel(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
        Table tableAnnotation = entityMeta.getEntityClass().getAnnotation(Table.class);
        if (tableAnnotation != null) {
            primaryKeyStrategy = tableAnnotation.primaryKeyStrategy();
        } else {
            primaryKeyStrategy = Table.PrimaryKeyStrategy.ASSIGNED;
        }
    }

    public void ensureExists(ConnectionProvider connectionProvider) throws SQLException {
        if (!exists(connectionProvider)) {
            execute(connectionProvider, sqlCreate(connectionProvider.getDialect()));
        }
    }

    public PrimaryKeyStrategy getPrimaryKeyStrategy() {
        return primaryKeyStrategy;
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

    List<String> sqlCreate(Dialect dialect) {
        List<String> sqls = new Vector<String>();
        StringBuilder sql = new StringBuilder();
        sql.append("create table ");
        sql.append(sqlName(entityMeta.getPersistenceName()));
        sql.append(" (");
        sql.append(" id ").append(dialect.getSqlType(Long.class));
        if (getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.AUTO) {
            sql.append(" ").append(dialect.getGeneratedIdColumnString());
        }

        for (String memberName : entityMeta.getMemberNames()) {
            MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                // For now create a join table
                sqls.add(sqlCreateJoin(dialect, memberMeta));
                continue;
            }

            sql.append(',');

            sql.append(' ').append(sqlName(memberName)).append(' ');
            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                // TODO create FK
            }
            sql.append(sqlType(dialect, memberMeta));
        }

        // TODO other dialects
        sql.append(", PRIMARY KEY (id)");

        sql.append(')');
        sqls.add(sql.toString());

        Collections.reverse(sqls);
        return sqls;
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
                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    continue;
                }
                StringBuilder sql = new StringBuilder("alter table ");
                sql.append(sqlName(entityMeta.getPersistenceName()));
                sql.append(" add column ");
                sql.append(sqlName(memberName)).append(' ');
                sql.append(sqlType(connectionProvider.getDialect(), memberMeta));
                alterSqls.add(sql.toString());
            } else {
                String mappingSqlType = connectionProvider.getDialect().getSqlType(memberMeta.getValueClass());
                if (!mappingSqlType.equalsIgnoreCase(columnMeta.getTypeName())) {
                    throw new RuntimeException(entityMeta.getPersistenceName() + "." + memberName + " incompatible SQL type " + columnMeta.getTypeName()
                            + " != " + mappingSqlType);
                }
            }

        }

        if (alterSqls.size() > 0) {
            execute(connectionProvider, alterSqls);
        }
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

    String sqlCreateJoin(Dialect dialect, MemberMeta memberMeta) {
        StringBuilder sql = new StringBuilder();
        sql.append("create table ");

        // TODO enable join table name
        sql.append(sqlName(entityMeta.getPersistenceName()));
        sql.append("_");
        sql.append(sqlName(memberMeta.getFieldName()));

        sql.append(" (");

        sql.append(" id ").append(dialect.getSqlType(Long.class)).append(", ");
        sql.append(sqlName(memberMeta.getFieldName())).append(" ").append(dialect.getSqlType(Long.class));

        sql.append(')');
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

    private String sqlInsert(boolean autoGeneratedKeys) {
        if (sqlInsert == null) {
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ");
            sql.append(sqlName(entityMeta.getPersistenceName()));
            sql.append(" (");
            int numberOfParams = 0;
            for (String memberName : entityMeta.getMemberNames()) {
                MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
                if (memberMeta.isTransient()) {
                    continue;
                }
                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    continue;
                }
                if (numberOfParams != 0) {
                    sql.append(',');
                }
                sql.append(sqlName(memberName));
                numberOfParams++;
            }
            if (!autoGeneratedKeys) {
                sql.append(", id");
                numberOfParams++;
            }
            sql.append(" ) VALUES (");
            for (int i = 0; i < numberOfParams; i++) {
                if (i != 0) {
                    sql.append(", ");
                }
                sql.append("?");
            }
            sql.append(" )");
            sqlInsert = sql.toString();
        }
        return sqlInsert;
    }

    private String sqlUpdate() {
        if (sqlUpdate == null) {
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE  ");
            sql.append(sqlName(entityMeta.getPersistenceName()));
            sql.append(" SET ");
            boolean first = true;
            for (String memberName : entityMeta.getMemberNames()) {
                MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
                if (memberMeta.isTransient()) {
                    continue;
                }
                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    continue;
                }
                if (first) {
                    first = false;
                } else {
                    sql.append(',');
                }
                sql.append(sqlName(memberName)).append(" = ? ");
            }
            sql.append(" WHERE id = ?");
            sqlUpdate = sql.toString();
        }
        return sqlUpdate;
    }

    private void bindParameter(Dialect dialect, PreparedStatement stmt, int parameterIndex, Class<?> valueClass, Object value) throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, dialect.getTargetSqlType(valueClass));
        } else {
            int targetSqlType = dialect.getTargetSqlType(valueClass);
            if (valueClass.isEnum()) {
                if (value != null) {
                    value = ((Enum<?>) value).name();
                }
            }
            stmt.setObject(parameterIndex, value, targetSqlType);
        }
    }

    private void bindPersistParameters(Dialect dialect, PreparedStatement stmt, IEntity entity, boolean includeKey) throws SQLException {
        int parameterIndex = 1;
        for (String memberName : entityMeta.getMemberNames()) {
            MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                continue;
            }
            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                Long primaryKey = ((IEntity) entity.getMember(memberName)).getPrimaryKey();
                if (primaryKey == null) {
                    stmt.setNull(parameterIndex, Types.BIGINT);
                } else {
                    stmt.setLong(parameterIndex, primaryKey);
                }
            } else {
                bindParameter(dialect, stmt, parameterIndex, memberMeta.getValueClass(), entity.getMemberValue(memberName));
            }
            parameterIndex++;
        }
        if (includeKey) {
            stmt.setLong(parameterIndex, entity.getPrimaryKey());
        }
    }

    public void insert(ConnectionProvider connectionProvider, IEntity entity) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = connectionProvider.getConnection();
            int autoGeneratedKeys = Statement.RETURN_GENERATED_KEYS;
            if (getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) {
                autoGeneratedKeys = Statement.NO_GENERATED_KEYS;
            }
            stmt = connection.prepareStatement(sqlInsert(autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS), autoGeneratedKeys);
            bindPersistParameters(connectionProvider.getDialect(), stmt, entity, autoGeneratedKeys != Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();
            if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
                ResultSet keys = stmt.getGeneratedKeys();
                keys.next();
                entity.setPrimaryKey(keys.getLong(1));
            }
            //TODO We have defaultAutoCommit = true in ConnectionProvider
            //connection.commit();
        } catch (SQLException e) {
            log.error("SQL insert error", e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(connection);
        }
    }

    public boolean update(ConnectionProvider connectionProvider, IEntity entity) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = connectionProvider.getConnection();
            stmt = connection.prepareStatement(sqlUpdate());
            bindPersistParameters(connectionProvider.getDialect(), stmt, entity, true);
            int rc = stmt.executeUpdate();

            //TODO We have defaultAutoCommit = true in ConnectionProvider
            //connection.commit();

            return (rc == 1);
        } catch (SQLException e) {
            log.error("SQL update error", e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(connection);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object decodeValue(Object value, MemberMeta memberMeta) {
        if (value == null) {
            return null;
        } else if (memberMeta.getValueClass().isEnum()) {
            return Enum.valueOf((Class<Enum>) memberMeta.getValueClass(), (String) value);
        } else if (java.util.Date.class.isAssignableFrom(memberMeta.getValueClass())) {
            return new java.util.Date(((java.sql.Timestamp) value).getTime());
        } else {
            return value;
        }
    }

    private void retrieveValues(ResultSet rs, IEntity entity) throws SQLException {
        for (String memberName : entityMeta.getMemberNames()) {
            MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                continue;
            }
            Object value = rs.getObject(sqlName(memberName));
            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                ((IEntity) entity.getMember(memberName)).setPrimaryKey((Long) value);
            } else {
                entity.setMemberValue(memberName, decodeValue(value, memberMeta));
            }
        }
    }

    public boolean retrieve(ConnectionProvider connectionProvider, long primaryKey, IEntity entity) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = connectionProvider.getConnection();
            stmt = connection.prepareStatement("SELECT * FROM " + sqlName(entityMeta.getPersistenceName()) + " WHERE id = ?");

            stmt.setLong(1, primaryKey);

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return false;
            } else {
                long key = rs.getLong("id");
                if (primaryKey != key) {
                    throw new RuntimeException();
                }
                entity.setPrimaryKey(key);
                retrieveValues(rs, entity);
                return true;
            }
        } catch (SQLException e) {
            log.error("SQL select error", e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(connection);
        }
    }
}
