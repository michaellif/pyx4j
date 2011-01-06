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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class TableModel {

    public static final int ORDINARY_STRING_LENGHT_MAX = 500;

    public static final int ENUM_STRING_LENGHT_MAX = 50;

    private static final Logger log = LoggerFactory.getLogger(TableModel.class);

    final String tableName;

    private final Dialect dialect;

    private final EntityMeta entityMeta;

    private final EntityOperationsMeta entityOperationsMeta;

    private final PrimaryKeyStrategy primaryKeyStrategy;

    private String sqlInsert;

    private String sqlUpdate;

    public TableModel(Dialect dialect, EntityMeta entityMeta) {
        this.dialect = dialect;
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
        Connection connection = connectionProvider.getConnection();
        try {
            {
                TableMetadata tableMetadata = TableMetadata.getTableMetadata(connection, tableName);
                if (tableMetadata == null) {
                    execute(connection, TableDDL.sqlCreate(connectionProvider.getDialect(), this));
                } else {
                    execute(connection, TableDDL.validateAndAlter(connectionProvider.getDialect(), tableMetadata, this));
                }
            }

            for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                TableMetadata memberTableMetadata = TableMetadata.getTableMetadata(connection, member.sqlName());
                if (memberTableMetadata == null) {
                    execute(connection, TableDDL.sqlCreateCollectionMemeber(connectionProvider.getDialect(), this, member));
                } else {
                    execute(connection,
                            TableDDL.validateAndAlterCollectionMemeber(connection, connectionProvider.getDialect(), memberTableMetadata, this, member));
                }
            }
        } finally {
            SQLUtils.closeQuietly(connection);
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

    public void dropTable(ConnectionProvider connectionProvider) throws SQLException {
        List<String> sqls = new Vector<String>();
        sqls.add("drop table " + tableName);
        execute(connectionProvider, sqls);
    }

    public void execute(ConnectionProvider connectionProvider, List<String> sqls) throws SQLException {
        Connection connection = connectionProvider.getConnection();
        try {
            execute(connection, sqls);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    public void execute(Connection connection, List<String> sqls) throws SQLException {
        if (sqls.size() == 0) {
            return;
        }
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
        }
    }

    private String sqlInsert(boolean autoGeneratedKeys) {
        if (sqlInsert == null) {
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ");
            sql.append(tableName);
            sql.append(" (");
            int numberOfParams = 0;
            for (MemberOperationsMeta member : entityOperationsMeta.getColumnMembers()) {
                if (numberOfParams != 0) {
                    sql.append(',');
                }
                sql.append(member.sqlName());
                numberOfParams++;
            }

            for (MemberOperationsMeta member : entityOperationsMeta.getIndexMembers()) {
                sql.append(',');
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
            for (MemberOperationsMeta member : entityOperationsMeta.getColumnMembers()) {
                if (first) {
                    first = false;
                } else {
                    sql.append(',');
                }
                sql.append(member.sqlName()).append(" = ? ");
            }
            for (MemberOperationsMeta member : entityOperationsMeta.getIndexMembers()) {
                sql.append(',').append(member.sqlName()).append(" = ? ");
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
            stmt.setObject(parameterIndex, encodeValue(valueClass, value), dialect.getTargetSqlType(valueClass));
        }
    }

    private int bindPersistParameters(Dialect dialect, PreparedStatement stmt, IEntity entity) throws SQLException {
        int parameterIndex = 1;
        for (MemberOperationsMeta member : entityOperationsMeta.getColumnMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                IEntity childEntity = (IEntity) member.getMember(entity);
                Long primaryKey = childEntity.getPrimaryKey();
                if (primaryKey == null) {
                    if (!childEntity.isNull()) {
                        log.error("Saving non persisted reference {}", childEntity);
                        throw new Error("Saving non persisted reference " + memberMeta.getValueClass() + " " + memberMeta.getCaption());
                    }
                    stmt.setNull(parameterIndex, Types.BIGINT);
                } else {
                    stmt.setLong(parameterIndex, primaryKey);
                }
            } else {
                bindParameter(dialect, stmt, parameterIndex, memberMeta.getValueClass(), member.getMemberValue(entity));
            }
            parameterIndex++;
        }
        for (MemberOperationsMeta member : entityOperationsMeta.getIndexMembers()) {
            bindParameter(dialect, stmt, parameterIndex, member.getIndexValueClass(), member.getIndexedValue(entity));
            parameterIndex++;
        }
        return parameterIndex;
    }

    public void insert(Connection connection, IEntity entity) {
        PreparedStatement stmt = null;
        try {
            int autoGeneratedKeys = Statement.RETURN_GENERATED_KEYS;
            if (getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) {
                autoGeneratedKeys = Statement.NO_GENERATED_KEYS;
            }
            stmt = connection.prepareStatement(sqlInsert(autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS), autoGeneratedKeys);
            int parameterIndex = bindPersistParameters(dialect, stmt, entity);
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
        } catch (SQLException e) {
            log.error("{} SQL insert error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    public boolean update(Connection connection, IEntity entity) {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sqlUpdate());
            int parameterIndex = bindPersistParameters(dialect, stmt, entity);
            stmt.setLong(parameterIndex, entity.getPrimaryKey());
            int rc = stmt.executeUpdate();

            for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                CollectionsTableModel.update(connection, dialect, entity, member);
            }

            //TODO We have defaultAutoCommit = true in ConnectionProvider
            //connection.commit();

            return (rc == 1);
        } catch (SQLException e) {
            log.error("{} SQL update error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    static Object encodeValue(Class<?> valueClass, Object value) {
        if (valueClass.isEnum()) {
            return ((Enum<?>) value).name();
        } else {
            return value;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static Object decodeValue(Object value, MemberMeta memberMeta) {
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
        for (MemberOperationsMeta member : entityOperationsMeta.getColumnMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            Object value = rs.getObject(member.sqlName());
            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                ((IEntity) member.getMember(entity)).setPrimaryKey((Long) value);
            } else {
                member.setMemberValue(entity, decodeValue(value, memberMeta));
            }
        }
    }

    public boolean retrieve(Connection connection, long primaryKey, IEntity entity) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
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

                for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                    CollectionsTableModel.retrieve(connection, entity, member);
                }

                return true;
            }
        } catch (SQLException e) {
            log.error("{} SQL select error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

    public <T extends IEntity> List<T> query(Connection connection, EntityQueryCriteria<T> criteria, int limit) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = null;
        try {
            QueryBuilder<T> qb = new QueryBuilder<T>(dialect, entityMeta, criteria);
            stmt = connection.prepareStatement(sql = "SELECT * FROM " + tableName + qb.getWhere());
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

                for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                    CollectionsTableModel.retrieve(connection, entity, member);
                }

                rc.add(entity);
            }
            return rc;
        } catch (SQLException e) {
            log.error("{} SQL {}", tableName, sql);
            log.error("{} SQL select error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

    public <T extends IEntity> List<Long> queryKeys(Connection connection, EntityQueryCriteria<T> criteria, int limit) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            QueryBuilder<T> qb = new QueryBuilder<T>(dialect, entityMeta, criteria);
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
            log.error("{} SQL select error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

    public <T extends IEntity> Object aggregate(Connection connection, EntityQueryCriteria<T> criteria, SQLAggregateFunctions func, String args) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            QueryBuilder<T> qb = new QueryBuilder<T>(dialect, entityMeta, criteria);
            stmt = connection.prepareStatement("SELECT " + dialect.sqlFunction(func, args) + " FROM " + tableName + qb.getWhere());
            qb.bindParameters(stmt);

            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getObject(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            log.error("{} SQL select error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

    public boolean delete(Connection connection, long primaryKey) {
        PreparedStatement stmt = null;
        try {
            for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                CollectionsTableModel.delete(connection, primaryKey, member);
            }

            stmt = connection.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?");

            stmt.setLong(1, primaryKey);

            int rc = stmt.executeUpdate();
            return rc >= 1;
        } catch (SQLException e) {
            log.error("{} SQL delete error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    public <T extends IEntity> int delete(Connection connection, EntityQueryCriteria<T> criteria) {
        PreparedStatement stmt = null;
        try {
            QueryBuilder<T> qb = new QueryBuilder<T>(dialect, entityMeta, criteria);
            stmt = connection.prepareStatement("DELETE FROM " + tableName + qb.getWhere());
            qb.bindParameters(stmt);

            // TODO ???
            //            for (MemberOperationsMeta member : entityOperationsMeta.getPrimitiveSetMembers()) {
            //                TableModelPrimitiveSet.delete(connection, primaryKey, member);
            //            }

            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("{} SQL delete error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }
}
