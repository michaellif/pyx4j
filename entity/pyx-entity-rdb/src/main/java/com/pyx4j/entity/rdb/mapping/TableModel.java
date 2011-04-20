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

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Table.PrimaryKeyStrategy;
import com.pyx4j.entity.rdb.ConnectionProvider;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
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
            primaryKeyStrategy = Table.PrimaryKeyStrategy.AUTO;
        }
        tableName = dialect.getNamingConvention().sqlTableName(entityMeta.getPersistenceName());
        entityOperationsMeta = new EntityOperationsMeta(dialect.getNamingConvention(), entityMeta);
    }

    public void ensureExists(ConnectionProvider connectionProvider) throws SQLException {
        Connection connection = connectionProvider.getConnection();
        try {
            {
                TableMetadata tableMetadata = TableMetadata.getTableMetadata(connection, tableName);
                if (tableMetadata == null) {
                    SQLUtils.execute(connection, TableDDL.sqlCreate(connectionProvider.getDialect(), this));
                } else {
                    SQLUtils.execute(connection, TableDDL.validateAndAlter(connectionProvider.getDialect(), tableMetadata, this));
                }
            }

            for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                TableMetadata memberTableMetadata = TableMetadata.getTableMetadata(connection, member.sqlName());
                if (memberTableMetadata == null) {
                    SQLUtils.execute(connection, TableDDL.sqlCreateCollectionMember(connectionProvider.getDialect(), this, member));
                } else {
                    SQLUtils.execute(connection,
                            TableDDL.validateAndAlterCollectionMember(connection, connectionProvider.getDialect(), memberTableMetadata, this, member));
                }
            }
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    public String getTableName() {
        return tableName;
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

    public boolean isTableExists(ConnectionProvider connectionProvider) throws SQLException {
        Connection connection = connectionProvider.getConnection();
        try {
            return (TableMetadata.getTableMetadata(connection, tableName) != null);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    public void dropTable(ConnectionProvider connectionProvider) throws SQLException {
        Connection connection = connectionProvider.getConnection();
        List<String> sqls = new Vector<String>();
        try {
            for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                TableMetadata memberTableMetadata = TableMetadata.getTableMetadata(connection, member.sqlName());
                if (memberTableMetadata != null) {
                    sqls.add("drop table " + member.sqlName());
                }
            }
            sqls.add("drop table " + tableName);
            SQLUtils.execute(connection, sqls);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    public void execute(ConnectionProvider connectionProvider, List<String> sqls) throws SQLException {
        Connection connection = connectionProvider.getConnection();
        try {
            SQLUtils.execute(connection, sqls);
        } finally {
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
            } else if (dialect.isSequencesBaseIdentity()) {
                sql.append(", id");
            }
            sql.append(" ) VALUES (");
            for (int i = 0; i < numberOfParams; i++) {
                if (i != 0) {
                    sql.append(", ");
                }
                sql.append("?");
            }
            if (dialect.isSequencesBaseIdentity()) {
                if (numberOfParams != 0) {
                    sql.append(", ");
                }
                sql.append(dialect.getSequenceNextValSql(dialect.getNamingConvention().sqlTableSequenceName(entityMeta.getPersistenceName())));
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

    private void bindParameter(Dialect dialect, PreparedStatement stmt, int parameterIndex, Class<?> valueClass, Object value, MemberMeta memberMeta)
            throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, dialect.getTargetSqlType(valueClass));
        } else {
            if ((memberMeta != null) && (memberMeta.getLength() > 0)) {
                int size = 0;
                if (value instanceof String) {
                    size = ((String) value).length();
                } else if (value.getClass().isArray()) {
                    size = Array.getLength(value);
                }
                if (size > memberMeta.getLength()) {
                    throw new RuntimeException("Member size vialoation member '" + memberMeta.getFieldName() + "' size " + size
                            + " is greater than max allowed " + memberMeta.getLength());
                }
            }
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
                        throw new Error("Saving non persisted reference " + memberMeta.getValueClass() + " " + memberMeta.getCaption() + " of "
                                + entity.getEntityMeta().getCaption());
                    }
                    stmt.setNull(parameterIndex, Types.BIGINT);
                } else {
                    stmt.setLong(parameterIndex, primaryKey);
                }
            } else {
                bindParameter(dialect, stmt, parameterIndex, memberMeta.getValueClass(), member.getMemberValue(entity), memberMeta);
            }
            parameterIndex++;
        }
        for (MemberOperationsMeta member : entityOperationsMeta.getIndexMembers()) {
            bindParameter(dialect, stmt, parameterIndex, member.getIndexValueClass(), member.getIndexedValue(entity), null);
            parameterIndex++;
        }
        return parameterIndex;
    }

    public void insert(Connection connection, IEntity entity) {
        PreparedStatement stmt = null;
        String sql = null;
        try {
            int autoGeneratedKeys = Statement.RETURN_GENERATED_KEYS;
            if (getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) {
                autoGeneratedKeys = Statement.NO_GENERATED_KEYS;
            }
            sql = sqlInsert(autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS);
            if (dialect.databaseType() == DatabaseType.Oracle) {
                stmt = connection.prepareStatement(sql, new String[] { "id" });
            } else {
                stmt = connection.prepareStatement(sql, autoGeneratedKeys);
            }
            int parameterIndex = bindPersistParameters(dialect, stmt, entity);
            if (autoGeneratedKeys == Statement.NO_GENERATED_KEYS) {
                if (entity.getPrimaryKey() == null) {
                    throw new Error("Can't persist Entity without assigned PK");
                }
                stmt.setLong(parameterIndex, entity.getPrimaryKey());
            }
            stmt.executeUpdate();
            if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
                ResultSet keys = stmt.getGeneratedKeys();
                try {
                    if (!keys.next()) {
                        throw new RuntimeException("Generated Key was not returned");
                    }
                    entity.setPrimaryKey(keys.getLong(1));
                } finally {
                    SQLUtils.closeQuietly(keys);
                }
            }
            if (EntityPersistenceServiceRDB.trace) {
                log.info(Trace.id() + "saved {} [{}] ", this.getTableName(), entity.getPrimaryKey());
            }
        } catch (SQLException e) {
            log.error("{} SQL {}", tableName, sql);
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
            boolean updated = (stmt.executeUpdate() == 1);
            if (updated) {
                for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                    CollectionsTableModel.update(connection, dialect, entity, member);
                }
            }
            return updated;
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
        } else if (valueClass.equals(java.util.Date.class)) {
            Calendar c = new GregorianCalendar();
            c.setTime((java.util.Date) value);
            // DB does not store Milliseconds
            c.set(Calendar.MILLISECOND, 0);
            return new java.sql.Timestamp(c.getTimeInMillis());
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
        } else if (java.sql.Date.class.isAssignableFrom(memberMeta.getValueClass())) {
            return value;
        } else if (java.sql.Time.class.isAssignableFrom(memberMeta.getValueClass())) {
            return value;
        } else if (java.util.Date.class.isAssignableFrom(memberMeta.getValueClass())) {
            return new java.util.Date(((java.util.Date) value).getTime());
        } else {
            if (value.getClass().equals(memberMeta.getValueClass())) {
                return value;
            } else {
                // This is manly used for Oracle
                if (Long.class.equals(memberMeta.getValueClass())) {
                    if (value instanceof Long) {
                        return value;
                    } else if (value instanceof Number) {
                        return Long.valueOf(((Number) value).longValue());
                    }
                } else if (Integer.class.equals(memberMeta.getValueClass())) {
                    if (value instanceof Integer) {
                        return value;
                    } else if (value instanceof Number) {
                        return Integer.valueOf(((Number) value).intValue());
                    }
                } else if (Double.class.equals(memberMeta.getValueClass())) {
                    if (value instanceof Double) {
                        return value;
                    } else if (value instanceof Number) {
                        return Double.valueOf(((Number) value).doubleValue());
                    }
                } else if (Float.class.equals(memberMeta.getValueClass())) {
                    if (value instanceof Float) {
                        return value;
                    } else if (value instanceof Number) {
                        return Float.valueOf(((Number) value).floatValue());
                    }
                } else if (Boolean.class.equals(memberMeta.getValueClass())) {
                    if (value instanceof Boolean) {
                        return value;
                    } else if (value instanceof Number) {
                        return Boolean.valueOf(((Number) value).intValue() > 0);
                    }
                }

                throw new Error("Type conversion " + value.getClass() + "->" + memberMeta.getValueClass() + " not implemanted");
            }
        }
    }

    public static Object getValue(ResultSet rs, String columnSqlName, MemberMeta memberMeta) throws SQLException {
        Object value = rs.getObject(columnSqlName);
        if (value == null) {
            return null;
        }
        if (java.util.Date.class.isAssignableFrom(memberMeta.getValueClass())) {
            value = rs.getTimestamp(columnSqlName);
        }
        return decodeValue(value, memberMeta);

    }

    public static Long getLongValue(ResultSet rs, String columnSqlName) throws SQLException {
        Object value = rs.getObject(columnSqlName);
        if (value != null) {
            if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof Number) {
                return ((Number) value).longValue();
            } else {
                return rs.getLong(columnSqlName);
            }
        } else {
            return null;
        }
    }

    private void retrieveValues(ResultSet rs, IEntity entity) throws SQLException {
        for (MemberOperationsMeta member : entityOperationsMeta.getColumnMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                ((IEntity) member.getMember(entity)).setPrimaryKey(getLongValue(rs, member.sqlName()));
            } else {
                member.setMemberValue(entity, getValue(rs, member.sqlName(), memberMeta));
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
            QueryBuilder<T> qb = new QueryBuilder<T>(dialect, "m1", entityMeta, entityOperationsMeta, criteria);
            stmt = connection.prepareStatement(sql = "SELECT m1.* FROM " + qb.getSQL(tableName));
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

    public <T extends IEntity> ResultSetIterator<T> queryIterable(final Connection connection, EntityQueryCriteria<T> criteria, int limit) {
        String sql = null;
        QueryBuilder<T> qb = new QueryBuilder<T>(dialect, "m1", entityMeta, entityOperationsMeta, criteria);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement(sql = "SELECT m1.* FROM " + qb.getSQL(tableName));
            if (limit > 0) {
                stmt.setMaxRows(limit);
            }
            qb.bindParameters(stmt);

            rs = stmt.executeQuery();
        } catch (SQLException e) {
            SQLUtils.closeQuietly(stmt);
            log.error("{} SQL {}", tableName, sql);
            log.error("{} SQL select error", tableName, e);
            throw new RuntimeException(e);
        }

        return new ResultSetIterator<T>(stmt, rs) {

            @Override
            protected T retrieve() {
                @SuppressWarnings("unchecked")
                T entity = (T) EntityFactory.create(entityMeta.getEntityClass());
                try {
                    entity.setPrimaryKey(rs.getLong("id"));
                    retrieveValues(rs, entity);
                } catch (SQLException e) {
                    log.error("{} SQL select error", tableName, e);
                    throw new RuntimeException(e);
                }
                for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                    CollectionsTableModel.retrieve(connection, entity, member);
                }
                return entity;
            }
        };

    }

    public <T extends IEntity> List<Long> queryKeys(Connection connection, EntityQueryCriteria<T> criteria, int limit) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = null;
        try {
            QueryBuilder<T> qb = new QueryBuilder<T>(dialect, "m1", entityMeta, entityOperationsMeta, criteria);
            stmt = connection.prepareStatement(sql = "SELECT m1.id FROM " + qb.getSQL(tableName));
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
            log.error("{} SQL {}", tableName, sql);
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
            QueryBuilder<T> qb = new QueryBuilder<T>(dialect, "m1", entityMeta, entityOperationsMeta, criteria);
            stmt = connection.prepareStatement("SELECT " + dialect.sqlFunction(func, args) + " FROM " + qb.getSQL(tableName));
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

    public int delete(Connection connection, Iterable<Long> primaryKeys) {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?");
            int pkSize = 0;
            for (long primaryKey : primaryKeys) {
                stmt.setLong(1, primaryKey);
                stmt.addBatch();
                pkSize++;
            }
            if (pkSize == 0) {
                return pkSize;
            }
            int[] rc = stmt.executeBatch();
            int count = 0;
            for (int i = 0; i < rc.length; i++) {
                switch (rc[i]) {
                case Statement.EXECUTE_FAILED:
                    throw new RuntimeException("SQL delete failed");
                case Statement.SUCCESS_NO_INFO:
                    count++;
                    break;
                default:
                    count += rc[i];
                }
            }
            return count;
        } catch (SQLException e) {
            log.error("{} SQL delete error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    public <T extends IEntity> boolean insert(Connection connection, Iterable<T> entityIterable) {
        PreparedStatement stmtIns = null;
        int[] vals = null;
        int autoGeneratedKeys = Statement.RETURN_GENERATED_KEYS;
        try {
            if (getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) {
                autoGeneratedKeys = Statement.NO_GENERATED_KEYS;
            }
            stmtIns = connection.prepareStatement(sqlInsert(autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS), autoGeneratedKeys);
            for (T entity : entityIterable) {
                int parameterIndex = bindPersistParameters(dialect, stmtIns, entity);
                if (autoGeneratedKeys == Statement.NO_GENERATED_KEYS) {
                    stmtIns.setLong(parameterIndex, entity.getPrimaryKey());
                }
                stmtIns.addBatch();
            }
            vals = stmtIns.executeBatch(); // INSERTs
            for (int i = 0; i < vals.length; i++) {
                if (vals[i] == 0) {
                    // not inserted ???
                }
            }

            if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
                ResultSet keys = null;
                try {
                    keys = stmtIns.getGeneratedKeys();
                    for (T entity : entityIterable) {
                        keys.next();
                        entity.setPrimaryKey(keys.getLong(1));
                    }
                } catch (SQLException e) {
                    log.error("{} SQL PrimaryKey retrieval error", tableName, e);
                    throw new RuntimeException(e);
                } finally {
                    SQLUtils.closeQuietly(keys);
                }
            }

            return true; //good, we reached this without exceptions

        } catch (SQLException e) {
            log.error("{} SQL Batch Insert error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmtIns);
        }

    }

    public <T extends IEntity> void persist(Connection connection, Iterable<T> entityIterable, List<T> notUpdated) {
        PreparedStatement stmtUpd = null;

        int[] vals = null;
        Vector<T> all = new Vector<T>();
        try {

            stmtUpd = connection.prepareStatement(sqlUpdate());

            for (T entity : entityIterable) {
                if (entity.getPrimaryKey() == null) {
                    // persist(Connection connection, Iterable<T> entityIterable) should be called on entities with non-NULL PKs
                    // ??? log.error(" persist(Connection connection, Iterable<T> entityIterable) should be called on entities with non-NULL PKs", tableName);
                    throw new RuntimeException();
                }
                int parameterIndex = bindPersistParameters(dialect, stmtUpd, entity);
                stmtUpd.setLong(parameterIndex, entity.getPrimaryKey());
                stmtUpd.addBatch();
                all.add(entity);
            }
            vals = stmtUpd.executeBatch(); // UPDATE
            for (int i = 0; i < vals.length; i++) {
                if (vals[i] == Statement.EXECUTE_FAILED) {
                    //////  WE MUST ROLL-BACK ////////////////////////////////
                    // due to executeBatch() may or may not throw BatchUpdateException, (see docs/api/java/sql/Statement.html#executeBatch())
                    // rather than duplicate code, we throw BatchUpdateException and have 
                    // code in a single place to handle failure
                    //log.error("{} executeBatch update error", tableName, e); //  I'd like to log something, to see if a DB throws or not 
                    throw new java.sql.BatchUpdateException();
                }
                if (vals[i] == 0) {
                    notUpdated.add(all.get(i));
                }
            }
        } catch (SQLException e) {
            log.error("{} SQL update error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmtUpd);
        }

    }

    public <T extends IEntity> boolean retrieve(Connection connection, Map<Long, T> entities) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT * FROM ").append(tableName).append(" WHERE id IN (");
        int count = 0;
        for (Long primaryKey : entities.keySet()) {
            if (count != 0) {
                queryStr.append(',');
            }
            queryStr.append(primaryKey);
            count++;
        }
        queryStr.append(')');

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement(queryStr.toString());

            rs = stmt.executeQuery();
            for (int i = 0; i < count; i++) {
                rs.next();
                long key = rs.getLong("id");
                if (!entities.containsKey(key)) {
                    throw new RuntimeException();
                }
                T entity = entities.get(key);
                entity.setPrimaryKey(key);
                retrieveValues(rs, entity);

            }

            /*
             * for (MemberOperationsMeta member :
             * entityOperationsMeta.getCollectionMembers()) {
             * CollectionsTableModel.retrieve(connection, entities, member); }
             */

            return true;

        } catch (SQLException e) {
            log.error("{} SQL select error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

}
