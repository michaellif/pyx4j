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
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Table.PrimaryKeyStrategy;
import com.pyx4j.entity.rdb.ConnectionProvider;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.SQLAggregateFunctions;
import com.pyx4j.entity.rdb.mapping.TableMetadata.ColumnMetadata;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class TableModel {

    public final int ORDINARY_STRING_LENGHT_MAX = 500;

    public final int ENUM_STRING_LENGHT_MAX = 50;

    private static final Logger log = LoggerFactory.getLogger(TableModel.class);

    private final String tableName;

    private final EntityMeta entityMeta;

    private final EntityOperationsMeta entityOperationsMeta;

    private final PrimaryKeyStrategy primaryKeyStrategy;

    private String sqlInsert;

    private String sqlUpdate;

    public TableModel(Dialect dialect, EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
        Table tableAnnotation = entityMeta.getEntityClass().getAnnotation(Table.class);
        if (tableAnnotation != null) {
            primaryKeyStrategy = tableAnnotation.primaryKeyStrategy();
        } else {
            primaryKeyStrategy = Table.PrimaryKeyStrategy.ASSIGNED;
        }
        tableName = dialect.sqlName(entityMeta.getPersistenceName());
        entityOperationsMeta = new EntityOperationsMeta(dialect, entityMeta);
    }

    public void ensureExists(ConnectionProvider connectionProvider) throws SQLException {
        if (!exists(connectionProvider)) {
            execute(connectionProvider, sqlCreate(connectionProvider.getDialect()));
        }
    }

    public EntityMeta entityMeta() {
        return entityMeta;
    }

    public PrimaryKeyStrategy getPrimaryKeyStrategy() {
        return primaryKeyStrategy;
    }

    public EntityOperationsMeta operationsMeta() {
        return entityOperationsMeta;
    }

    public boolean exists(ConnectionProvider connectionProvider) throws SQLException {
        Connection connection = connectionProvider.getConnection();
        ResultSet rs = null;
        try {
            DatabaseMetaData dbMeta = connection.getMetaData();
            rs = dbMeta.getTables(null, null, tableName, null);
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
        sql.append(tableName);
        sql.append(" (");
        sql.append(" id ").append(dialect.getSqlType(Long.class));
        if (getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.AUTO) {
            sql.append(" ").append(dialect.getGeneratedIdColumnString());
        }

        for (MemberOperationsMeta member : entityOperationsMeta.getMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                // For now create a join table
                sqls.add(sqlCreateJoin(dialect, memberMeta));
                continue;
            }
            sql.append(", ").append(member.sqlName()).append(' ');
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
        for (MemberOperationsMeta member : entityOperationsMeta.getMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            ColumnMetadata columnMeta = tableMetadata.getColumn(member.sqlName());
            if (columnMeta == null) {
                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    continue;
                }
                StringBuilder sql = new StringBuilder("alter table ");
                sql.append(tableName);
                sql.append(" add column ");
                sql.append(member.sqlName()).append(' ');
                sql.append(sqlType(connectionProvider.getDialect(), memberMeta));
                alterSqls.add(sql.toString());
            } else {
                if (!connectionProvider.getDialect().isCompatibleType(memberMeta.getValueClass(), columnMeta.getTypeName())) {
                    throw new RuntimeException(tableName + "." + member.sqlName() + " incompatible SQL type " + columnMeta.getTypeName() + " != "
                            + connectionProvider.getDialect().getSqlType(memberMeta.getValueClass()));
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
        sql.append(dialect.sqlName(tableName + "_" + memberMeta.getFieldName()));

        sql.append(" (");

        sql.append(" id ").append(dialect.getSqlType(Long.class)).append(", ");
        sql.append(dialect.sqlName(memberMeta.getFieldName())).append(" ").append(dialect.getSqlType(Long.class));

        sql.append(')');
        return sql.toString();
    }

    public void dropTable(ConnectionProvider connectionProvider) throws SQLException {
        List<String> sqls = new Vector<String>();
        sqls.add("drop table " + tableName);
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
            sql.append(tableName);
            sql.append(" (");
            int numberOfParams = 0;
            for (MemberOperationsMeta member : entityOperationsMeta.getMembers()) {
                MemberMeta memberMeta = member.getMemberMeta();
                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    continue;
                }
                if (numberOfParams != 0) {
                    sql.append(',');
                }
                sql.append(member.sqlName());
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
            sql.append(tableName);
            sql.append(" SET ");
            boolean first = true;
            for (MemberOperationsMeta member : entityOperationsMeta.getMembers()) {
                MemberMeta memberMeta = member.getMemberMeta();
                if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                    continue;
                }
                if (first) {
                    first = false;
                } else {
                    sql.append(',');
                }
                sql.append(member.sqlName()).append(" = ? ");
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

    private int bindPersistParameters(Dialect dialect, PreparedStatement stmt, IEntity entity) throws SQLException {
        int parameterIndex = 1;
        for (MemberOperationsMeta member : entityOperationsMeta.getMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                continue;
            }
            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                Long primaryKey = ((IEntity) member.getMember(entity)).getPrimaryKey();
                if (primaryKey == null) {
                    stmt.setNull(parameterIndex, Types.BIGINT);
                } else {
                    stmt.setLong(parameterIndex, primaryKey);
                }
            } else {
                bindParameter(dialect, stmt, parameterIndex, memberMeta.getValueClass(), member.getMemberValue(entity));
            }
            parameterIndex++;
        }
        return parameterIndex;
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
            int parameterIndex = bindPersistParameters(connectionProvider.getDialect(), stmt, entity);
            if (autoGeneratedKeys == Statement.NO_GENERATED_KEYS) {
                stmt.setLong(parameterIndex, entity.getPrimaryKey());
            }
            stmt.executeUpdate();
            if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
                ResultSet keys = stmt.getGeneratedKeys();
                try {
                    keys.next();
                    entity.setPrimaryKey(keys.getLong(1));
                } finally {
                    SQLUtils.closeQuietly(keys);
                }
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
            int parameterIndex = bindPersistParameters(connectionProvider.getDialect(), stmt, entity);
            stmt.setLong(parameterIndex, entity.getPrimaryKey());
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
        for (MemberOperationsMeta member : entityOperationsMeta.getMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            if (ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                continue;
            }
            Object value = rs.getObject(member.sqlName());
            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                ((IEntity) member.getMember(entity)).setPrimaryKey((Long) value);
            } else {
                member.setMemberValue(entity, decodeValue(value, memberMeta));
            }
        }
    }

    public boolean retrieve(ConnectionProvider connectionProvider, long primaryKey, IEntity entity) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = connectionProvider.getConnection();
            stmt = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE id = ?");

            stmt.setLong(1, primaryKey);

            rs = stmt.executeQuery();
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
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(connection);
        }
    }

    public <T extends IEntity> List<T> query(ConnectionProvider connectionProvider, EntityQueryCriteria<T> criteria, int limit) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = connectionProvider.getConnection();
            QueryBuilder<T> qb = new QueryBuilder<T>(entityMeta, criteria);
            stmt = connection.prepareStatement("SELECT * FROM " + tableName + qb.getWhere());
            if (limit > 0) {
                stmt.setMaxRows(limit);
            }
            qb.bindParameters(stmt);

            rs = stmt.executeQuery();

            List<T> rc = new Vector<T>();
            while (rs.next()) {
                @SuppressWarnings("unchecked")
                T entity = (T) EntityFactory.create(entityMeta.getEntityClass());
                entity.setPrimaryKey(rs.getLong("id"));
                retrieveValues(rs, entity);
                rc.add(entity);
            }
            return rc;
        } catch (SQLException e) {
            log.error("SQL select error", e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(connection);
        }
    }

    public <T extends IEntity> List<Long> queryKeys(ConnectionProvider connectionProvider, EntityQueryCriteria<T> criteria, int limit) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = connectionProvider.getConnection();
            QueryBuilder<T> qb = new QueryBuilder<T>(entityMeta, criteria);
            stmt = connection.prepareStatement("SELECT id FROM " + tableName + qb.getWhere());
            if (limit > 0) {
                stmt.setMaxRows(limit);
            }
            qb.bindParameters(stmt);

            rs = stmt.executeQuery();

            List<Long> rc = new Vector<Long>();
            while (rs.next()) {
                rc.add(rs.getLong("id"));
            }
            return rc;
        } catch (SQLException e) {
            log.error("SQL select error", e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(connection);
        }
    }

    public <T extends IEntity> Object aggregate(ConnectionProvider connectionProvider, EntityQueryCriteria<T> criteria, SQLAggregateFunctions func, String args) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = connectionProvider.getConnection();
            QueryBuilder<T> qb = new QueryBuilder<T>(entityMeta, criteria);
            stmt = connection.prepareStatement("SELECT " + connectionProvider.getDialect().sqlFunction(func, args) + " FROM " + tableName + qb.getWhere());
            qb.bindParameters(stmt);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getObject(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            log.error("SQL select error", e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(connection);
        }
    }

    public boolean delete(ConnectionProvider connectionProvider, long primaryKey) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = connectionProvider.getConnection();
            stmt = connection.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?");

            stmt.setLong(1, primaryKey);

            int rc = stmt.executeUpdate();
            return rc >= 1;
        } catch (SQLException e) {
            log.error("SQL delete error", e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(connection);
        }
    }

    public <T extends IEntity> int delete(ConnectionProvider connectionProvider, EntityQueryCriteria<T> criteria) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = connectionProvider.getConnection();
            QueryBuilder<T> qb = new QueryBuilder<T>(entityMeta, criteria);
            stmt = connection.prepareStatement("DELETE FROM " + tableName + qb.getWhere());
            qb.bindParameters(stmt);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("SQL delete error", e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
            SQLUtils.closeQuietly(connection);
        }
    }
}
