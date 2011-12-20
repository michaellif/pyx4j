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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Table.PrimaryKeyStrategy;
import com.pyx4j.entity.rdb.ConnectionProvider;
import com.pyx4j.entity.rdb.ConnectionProvider.ConnectionTarget;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.SQLAggregateFunctions;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.server.contexts.NamespaceManager;

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

    public TableModel(Dialect dialect, Mappings mappings, EntityMeta entityMeta) {
        this.dialect = dialect;
        this.entityMeta = entityMeta;
        if (entityMeta.getEntityClass().getAnnotation(AbstractEntity.class) != null) {
            throw new Error("Persistance of @AbstractEntity " + entityMeta.getEntityClass().getName() + " is not permited");
        }

        Table tableAnnotation = entityMeta.getEntityClass().getAnnotation(Table.class);
        if (tableAnnotation != null) {
            primaryKeyStrategy = tableAnnotation.primaryKeyStrategy();
        } else {
            primaryKeyStrategy = Table.PrimaryKeyStrategy.AUTO;
        }
        tableName = getTableName(dialect, entityMeta);
        entityOperationsMeta = new EntityOperationsMeta(dialect, mappings, entityMeta);

        if (dialect.isSequencesBaseIdentity()) {
            for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                member.setSqlSequenceName(dialect.getNamingConvention().sqlChildTableSequenceName(member.sqlName()));
            }
        }
    }

    public static String getTableName(Dialect dialect, EntityMeta entityMeta) {
        return dialect.getNamingConvention().sqlTableName(entityMeta.getPersistenceName());
    }

    public void ensureExists(Connection connection, Dialect dialect) throws SQLException {
        {
            if (Mappings.traceInit) {
                log.trace(Trace.id() + "getTableMetadata {}", tableName);
            }
            TableMetadata tableMetadata = TableMetadata.getTableMetadata(connection, tableName);
            if (tableMetadata == null) {
                log.debug("table {} does not exists", tableName);
                SQLUtils.execute(connection, TableDDL.sqlCreate(dialect, this));
                if (Mappings.traceInit) {
                    log.trace(Trace.id() + "table created {}", tableName);
                }
            } else {
                SQLUtils.execute(connection, TableDDL.validateAndAlter(dialect, tableMetadata, this));
            }
        }

        for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
            TableMetadata memberTableMetadata = TableMetadata.getTableMetadata(connection, member.sqlName());
            if (memberTableMetadata == null) {
                SQLUtils.execute(connection, TableDDL.sqlCreateCollectionMember(dialect, member));
            } else {
                SQLUtils.execute(connection, TableDDL.validateAndAlterCollectionMember(dialect, memberTableMetadata, member));
            }
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
        Connection connection = connectionProvider.getConnection(ConnectionTarget.forRead);
        try {
            return (TableMetadata.getTableMetadata(connection, tableName) != null);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    public void dropTable(ConnectionProvider connectionProvider) throws SQLException {
        Connection connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
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
        Connection connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
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
                for (String name : member.getValueAdapter().getColumnNames(member.sqlName())) {
                    if (numberOfParams != 0) {
                        sql.append(", ");
                    }
                    sql.append(name);
                    numberOfParams++;
                }
            }

            for (MemberOperationsMeta member : entityOperationsMeta.getIndexMembers()) {
                if (numberOfParams > 0) {
                    sql.append(", ");
                }
                sql.append(member.sqlName());
                numberOfParams++;
            }
            if (dialect.isMultitenant()) {
                if (numberOfParams > 0) {
                    sql.append(", ");
                }
                sql.append("ns");
                numberOfParams++;
            }
            if (!autoGeneratedKeys) {
                if (numberOfParams > 0) {
                    sql.append(", ");
                }
                sql.append("id");
                numberOfParams++;
            } else if (dialect.isSequencesBaseIdentity()) {
                if (numberOfParams > 0) {
                    sql.append(", ");
                }
                sql.append("id");
            }
            sql.append(") VALUES (");
            for (int i = 0; i < numberOfParams; i++) {
                if (i != 0) {
                    sql.append(", ");
                }
                sql.append("?");
            }
            if (autoGeneratedKeys && dialect.isSequencesBaseIdentity()) {
                if (numberOfParams != 0) {
                    sql.append(", ");
                }
                sql.append(dialect.getSequenceNextValSql(dialect.getNamingConvention().sqlTableSequenceName(entityMeta.getPersistenceName())));
            }
            sql.append(")");
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
                for (String name : member.getValueAdapter().getColumnNames(member.sqlName())) {
                    if (first) {
                        first = false;
                    } else {
                        sql.append(", ");
                    }
                    sql.append(name).append(" = ? ");
                }
            }
            for (MemberOperationsMeta member : entityOperationsMeta.getIndexMembers()) {
                sql.append(',').append(member.sqlName()).append(" = ? ");
            }
            sql.append(" WHERE id = ?");
            if (dialect.isMultitenant()) {
                sql.append(" AND ns = ?");
            }
            sqlUpdate = sql.toString();
        }
        return sqlUpdate;
    }

    private void bindParameter(Dialect dialect, PreparedStatement stmt, int parameterIndex, Class<?> valueClass, Object value, MemberMeta memberMeta)
            throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, dialect.getTargetSqlType(valueClass));
        } else {
            stmt.setObject(parameterIndex, encodeValue(valueClass, value), dialect.getTargetSqlType(valueClass));
        }
    }

    private int bindPersistParameters(Dialect dialect, PreparedStatement stmt, IEntity entity) throws SQLException {
        int parameterIndex = 1;
        for (MemberOperationsMeta member : entityOperationsMeta.getColumnMembers()) {
            if (member.getMemberMeta().isEntity()) {
                IEntity childEntity = (IEntity) member.getMember(entity);
                if ((childEntity.getPrimaryKey() == null) && !childEntity.isNull()) {
                    log.error("Saving non persisted reference {}", childEntity);
                    throw new Error("Saving non persisted reference " + member.getMemberMeta().getValueClass().getName() + " "
                            + member.getMemberMeta().getCaption() + " of " + entity.getEntityMeta().getCaption());
                }
                parameterIndex += member.getValueAdapter().bindValue(stmt, parameterIndex, childEntity);
            } else {
                parameterIndex += member.getValueAdapter().bindValue(stmt, parameterIndex, member.getMemberValue(entity));
            }
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
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            if (dialect.databaseType() == DatabaseType.Oracle) {
                stmt = connection.prepareStatement(sql, new String[] { "id" });
            } else {
                stmt = connection.prepareStatement(sql, autoGeneratedKeys);
            }
            int parameterIndex = bindPersistParameters(dialect, stmt, entity);
            if (dialect.isMultitenant()) {
                stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                parameterIndex++;
            }
            if (autoGeneratedKeys == Statement.NO_GENERATED_KEYS) {
                if (entity.getPrimaryKey() == null) {
                    throw new Error("Can't persist Entity without assigned PK");
                }
                stmt.setLong(parameterIndex, entity.getPrimaryKey().asLong());
            }
            stmt.executeUpdate();
            if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
                ResultSet keys = stmt.getGeneratedKeys();
                try {
                    if (!keys.next()) {
                        throw new RuntimeException("Generated Key was not returned");
                    }
                    entity.setPrimaryKey(new Key(keys.getLong(1)));
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

    public boolean update(Connection connection, IEntity entity, List<IEntity> cascadeRemove) {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sqlUpdate());
            // Just in case, used for pooled connections 
            stmt.setMaxRows(1);
            int parameterIndex = bindPersistParameters(dialect, stmt, entity);
            stmt.setLong(parameterIndex, entity.getPrimaryKey().asLong());
            if (dialect.isMultitenant()) {
                parameterIndex++;
                stmt.setString(parameterIndex, NamespaceManager.getNamespace());
            }
            boolean updated = (stmt.executeUpdate() == 1);
            if (updated) {
                for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                    CollectionsTableModel.update(connection, dialect, entity, member, cascadeRemove);
                }
            }
            return updated;
        } catch (SQLException e) {
            log.error("{} SQL {}", tableName, sqlUpdate());
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

    private void retrieveValues(ResultSet rs, IEntity entity) throws SQLException {
        entity.setValuePopulated();
        for (MemberOperationsMeta member : entityOperationsMeta.getColumnMembers()) {
            Object value = member.getValueAdapter().retrieveValue(rs, member.sqlName());
            if (value != null) {
                if (member.getMemberMeta().isEntity()) {
                    IEntity valueEntity = (IEntity) value;
                    IEntity memberValue = (IEntity) member.getMember(entity);
                    if (member.isOwnerColumn()) {
                        // Special handling for recursive retrieve of Owner
                        if ((entity.getOwner() != null) && (entity.getMeta() != null) && entity.getMeta().isOwnedRelationships()) {
                            // verify graph integrity
                            if (!entity.getOwner().getPrimaryKey().equals(valueEntity.getPrimaryKey())) {
                                throw new RuntimeException("Unexpected owner " + member.getMemberPath() + " '" + valueEntity.getDebugExceptionInfoString()
                                        + "' != '" + entity.getOwner().getDebugExceptionInfoString() + "' in entity '" + entity.getDebugExceptionInfoString()
                                        + "'");
                            }
                        } else {
                            memberValue.set(valueEntity);
                        }
                    } else {
                        memberValue.set(valueEntity);
                    }
                } else {
                    member.setMemberValue(entity, value);
                }
            }
        }
    }

    public boolean retrieve(Connection connection, Key primaryKey, IEntity entity) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ").append(tableName).append(" WHERE id = ?");
            if (dialect.isMultitenant()) {
                sql.append(" AND ns = ?");
            }
            stmt = connection.prepareStatement(sql.toString());
            // Just in case, used for pooled connections 
            stmt.setMaxRows(1);

            stmt.setLong(1, primaryKey.asLong());
            if (dialect.isMultitenant()) {
                stmt.setString(2, NamespaceManager.getNamespace());
            }

            rs = stmt.executeQuery();
            if (!rs.next()) {
                return false;
            } else {
                Key key = new Key(rs.getLong("id"));
                if (!primaryKey.equals(key)) {
                    throw new RuntimeException();
                }
                if ((dialect.isMultitenant()) && !rs.getString("ns").equals(NamespaceManager.getNamespace())) {
                    throw new RuntimeException("namespace acess error");
                }
                entity.setPrimaryKey(key);
                retrieveValues(rs, entity);

                for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                    CollectionsTableModel.retrieve(connection, dialect, entity, member);
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
            QueryBuilder<T> qb = new QueryBuilder<T>(connection, dialect, "m1", entityMeta, entityOperationsMeta, criteria);
            sql = "SELECT m1.* FROM " + qb.getSQL(tableName);
            int offset = 0;
            boolean addLimit = false;
            if (criteria instanceof EntityListCriteria) {
                EntityListCriteria<T> c = (EntityListCriteria<T>) criteria;
                if (c.getPageSize() > 0) {
                    offset = c.getPageSize() * c.getPageNumber();
                    if (limit > 0) {
                        limit = Math.min(limit, c.getPageSize());
                    } else {
                        limit = c.getPageSize();
                    }
                    addLimit = true;
                    sql = dialect.applyLimitCriteria(sql);
                }
            }
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = connection.prepareStatement(sql);
            if (limit > 0) {
                stmt.setMaxRows(limit);
            } else {
                // zero means there is no limit, Need for pooled connections 
                stmt.setMaxRows(0);
            }
            int parameterIndex = qb.bindParameters(stmt);
            if (addLimit) {
                if (dialect.limitCriteriaIsRelative()) {
                    stmt.setInt(parameterIndex, limit);
                } else {
                    stmt.setInt(parameterIndex, offset + limit);
                }
                parameterIndex++;
                stmt.setInt(parameterIndex, offset);
            }
            rs = stmt.executeQuery();

            List<T> rc = new Vector<T>();
            while (rs.next()) {
                @SuppressWarnings("unchecked")
                T entity = (T) EntityFactory.create(entityMeta.getEntityClass());
                entity.setPrimaryKey(new Key(rs.getLong("id")));
                if ((dialect.isMultitenant()) && !rs.getString("ns").equals(NamespaceManager.getNamespace())) {
                    throw new RuntimeException("namespace acess error");
                }
                retrieveValues(rs, entity);

                for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                    CollectionsTableModel.retrieve(connection, dialect, entity, member);
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

    public <T extends IEntity> ResultSetIterator<T> queryIterable(final Connection connection, EntityQueryCriteria<T> criteria) {
        String sql = null;
        QueryBuilder<T> qb = new QueryBuilder<T>(connection, dialect, "m1", entityMeta, entityOperationsMeta, criteria);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            sql = "SELECT m1.* FROM " + qb.getSQL(tableName);
            int limit = -1;
            int offset = 0;
            if (criteria instanceof EntityListCriteria) {
                EntityListCriteria<T> c = (EntityListCriteria<T>) criteria;
                if (c.getPageSize() > 0) {
                    offset = c.getPageSize() * c.getPageNumber();
                    limit = c.getPageSize() + 1;
                    sql = dialect.applyLimitCriteria(sql);
                }
            }
            stmt = connection.prepareStatement(sql);
            if (limit > 0) {
                stmt.setMaxRows(limit);
            } else {
                // zero means there is no limit, Need for pooled connections 
                stmt.setMaxRows(0);
            }
            int parameterIndex = qb.bindParameters(stmt);
            if (limit > 0) {
                if (dialect.limitCriteriaIsRelative()) {
                    stmt.setInt(parameterIndex, limit);
                } else {
                    stmt.setInt(parameterIndex, limit + offset);
                }
                parameterIndex++;
                stmt.setInt(parameterIndex, offset);
            }

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
                    entity.setPrimaryKey(new Key(rs.getLong("id")));
                    retrieveValues(rs, entity);
                } catch (SQLException e) {
                    log.error("{} SQL select error", tableName, e);
                    throw new RuntimeException(e);
                }
                for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                    CollectionsTableModel.retrieve(connection, dialect, entity, member);
                }
                return entity;
            }
        };

    }

    public <T extends IEntity> List<Key> queryKeys(Connection connection, EntityQueryCriteria<T> criteria, int limit) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = null;
        try {
            QueryBuilder<T> qb = new QueryBuilder<T>(connection, dialect, "m1", entityMeta, entityOperationsMeta, criteria);
            stmt = connection.prepareStatement(sql = "SELECT m1.id FROM " + qb.getSQL(tableName));
            if (limit > 0) {
                stmt.setMaxRows(limit);
            } else {
                // zero means there is no limit, Need for pooled connections 
                stmt.setMaxRows(0);
            }
            qb.bindParameters(stmt);

            rs = stmt.executeQuery();

            List<Key> rc = new Vector<Key>();
            while (rs.next()) {
                rc.add(new Key(rs.getLong("id")));
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

    public <T extends IEntity> ResultSetIterator<Key> queryKeysIterable(final Connection connection, EntityQueryCriteria<T> criteria) {
        String sql = null;
        QueryBuilder<T> qb = new QueryBuilder<T>(connection, dialect, "m1", entityMeta, entityOperationsMeta, criteria);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            sql = "SELECT m1.id FROM " + qb.getSQL(tableName);
            int limit = -1;
            int offset = 0;
            if (criteria instanceof EntityListCriteria) {
                EntityListCriteria<T> c = (EntityListCriteria<T>) criteria;
                if (c.getPageSize() > 0) {
                    offset = c.getPageSize() * c.getPageNumber();
                    limit = c.getPageSize() + 1;
                    sql = dialect.applyLimitCriteria(sql);
                }
            }
            stmt = connection.prepareStatement(sql);
            if (limit > 0) {
                stmt.setMaxRows(limit);
            } else {
                // zero means there is no limit, Need for pooled connections 
                stmt.setMaxRows(0);
            }
            int parameterIndex = qb.bindParameters(stmt);
            if (limit > 0) {
                if (dialect.limitCriteriaIsRelative()) {
                    stmt.setInt(parameterIndex, limit);
                } else {
                    stmt.setInt(parameterIndex, limit + offset);
                }
                parameterIndex++;
                stmt.setInt(parameterIndex, offset);
            }

            rs = stmt.executeQuery();
        } catch (SQLException e) {
            SQLUtils.closeQuietly(stmt);
            log.error("{} SQL {}", tableName, sql);
            log.error("{} SQL select error", tableName, e);
            throw new RuntimeException(e);
        }

        return new ResultSetIterator<Key>(stmt, rs) {

            @Override
            protected Key retrieve() {
                try {
                    return new Key(rs.getLong("id"));
                } catch (SQLException e) {
                    log.error("{} SQL select error", tableName, e);
                    throw new RuntimeException(e);
                }
            }
        };

    }

    public <T extends IEntity> Object aggregate(Connection connection, EntityQueryCriteria<T> criteria, SQLAggregateFunctions func, String args) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            QueryBuilder<T> qb = new QueryBuilder<T>(connection, dialect, "m1", entityMeta, entityOperationsMeta, criteria);
            stmt = connection.prepareStatement("SELECT " + dialect.sqlFunction(func, args) + " FROM " + qb.getSQL(tableName));
            // Just in case, used for pooled connections 
            stmt.setMaxRows(1);
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

    public boolean delete(Connection connection, Key primaryKey) {
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("DELETE FROM ").append(tableName).append(" WHERE id = ?");
            if (dialect.isMultitenant()) {
                sql.append(" AND ns = ?");
            }
            stmt = connection.prepareStatement(sql.toString());
            stmt.setLong(1, primaryKey.asLong());
            if (dialect.isMultitenant()) {
                stmt.setString(2, NamespaceManager.getNamespace());
            }
            int rc = stmt.executeUpdate();
            return rc >= 1;
        } catch (SQLException e) {
            log.error("{} SQL {}", tableName, sql);
            log.error("{} SQL delete error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    public int delete(Connection connection, Iterable<Key> primaryKeys) {
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("DELETE FROM ").append(tableName).append(" WHERE id = ?");
            if (dialect.isMultitenant()) {
                sql.append(" AND ns = ?");
            }
            stmt = connection.prepareStatement(sql.toString());
            int pkSize = 0;
            for (Key primaryKey : primaryKeys) {
                stmt.setLong(1, primaryKey.asLong());
                if (dialect.isMultitenant()) {
                    stmt.setString(2, NamespaceManager.getNamespace());
                }
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
            log.error("{} SQL {}", tableName, sql);
            log.error("{} SQL delete error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    public void truncate(Connection connection) {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("TRUNCATE TABLE " + tableName);
            stmt.execute();
        } catch (SQLException e) {
            log.error("{} SQL delete error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }

    }

    public <T extends IEntity> boolean insert(Connection connection, Iterable<T> entityIterable) {
        PreparedStatement stmt = null;
        int[] vals = null;
        int autoGeneratedKeys = Statement.RETURN_GENERATED_KEYS;
        try {
            if (getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) {
                autoGeneratedKeys = Statement.NO_GENERATED_KEYS;
            }
            stmt = connection.prepareStatement(sqlInsert(autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS), autoGeneratedKeys);
            for (T entity : entityIterable) {
                int parameterIndex = bindPersistParameters(dialect, stmt, entity);
                if (autoGeneratedKeys == Statement.NO_GENERATED_KEYS) {
                    stmt.setLong(parameterIndex, entity.getPrimaryKey().asLong());
                    parameterIndex++;
                }
                if (dialect.isMultitenant()) {
                    stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                    parameterIndex++;
                }
                stmt.addBatch();
            }
            vals = stmt.executeBatch(); // INSERTs
            for (int i = 0; i < vals.length; i++) {
                if (vals[i] == 0) {
                    // not inserted ???
                }
            }

            if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
                ResultSet keys = null;
                try {
                    keys = stmt.getGeneratedKeys();
                    for (T entity : entityIterable) {
                        keys.next();
                        entity.setPrimaryKey(new Key(keys.getLong(1)));
                    }
                } catch (SQLException e) {
                    log.error("{} SQL {}", tableName, sqlInsert);
                    log.error("{} SQL PrimaryKey retrieval error", tableName, e);
                    throw new RuntimeException(e);
                } finally {
                    SQLUtils.closeQuietly(keys);
                }
            }

            return true; //good, we reached this without exceptions

        } catch (SQLException e) {
            log.error("{} SQL {}", tableName, sqlInsert);
            log.error("{} SQL Batch Insert error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }

    }

    public <T extends IEntity> boolean insertBulk(Connection connection, Iterable<T> entityIterable) {
        PreparedStatement stmt = null;
        int[] vals = null;
        try {
            boolean hasKeys = (getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED);
            stmt = connection.prepareStatement(sqlInsert(!hasKeys));
            for (T entity : entityIterable) {
                int parameterIndex = bindPersistParameters(dialect, stmt, entity);
                if (hasKeys) {
                    stmt.setLong(parameterIndex, entity.getPrimaryKey().asLong());
                    parameterIndex++;
                }
                if (dialect.isMultitenant()) {
                    stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                    parameterIndex++;
                }
                stmt.addBatch();
            }
            vals = stmt.executeBatch(); // INSERTs
            for (int i = 0; i < vals.length; i++) {
                if (vals[i] == 0) {
                    // not inserted ???
                }
            }
            return true; //good, we reached this without exceptions
        } catch (SQLException e) {
            log.error("{} SQL {}", tableName, sqlInsert);
            log.error("{} SQL Batch Insert error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }

    }

    public <T extends IEntity> void persist(Connection connection, Iterable<T> entityIterable, List<T> notUpdated) {
        PreparedStatement stmt = null;
        int[] vals = null;
        Vector<T> all = new Vector<T>();
        try {
            stmt = connection.prepareStatement(sqlUpdate());
            // zero means there is no limit, Need for pooled connections 
            stmt.setMaxRows(0);
            for (T entity : entityIterable) {
                if (entity.getPrimaryKey() == null) {
                    // persist(Connection connection, Iterable<T> entityIterable) should be called on entities with non-NULL PKs
                    // ??? log.error(" persist(Connection connection, Iterable<T> entityIterable) should be called on entities with non-NULL PKs", tableName);
                    throw new RuntimeException();
                }
                int parameterIndex = bindPersistParameters(dialect, stmt, entity);
                stmt.setLong(parameterIndex, entity.getPrimaryKey().asLong());
                if (dialect.isMultitenant()) {
                    parameterIndex++;
                    stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                }
                stmt.addBatch();
                all.add(entity);
            }
            vals = stmt.executeBatch(); // UPDATE
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
            SQLUtils.closeQuietly(stmt);
        }

    }

    public <T extends IEntity> boolean retrieve(Connection connection, Map<Key, T> entities) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(tableName).append(" WHERE id IN (");
        int count = 0;
        for (Key primaryKey : entities.keySet()) {
            if (count != 0) {
                sql.append(',');
            }
            sql.append(primaryKey);
            count++;
        }
        sql.append(')');
        if (dialect.isMultitenant()) {
            sql.append(" AND ns = ?");
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement(sql.toString());
            // zero means there is no limit, Need for pooled connections 
            stmt.setMaxRows(0);
            if (dialect.isMultitenant()) {
                stmt.setString(1, NamespaceManager.getNamespace());
            }

            rs = stmt.executeQuery();
            for (int i = 0; i < count; i++) {
                rs.next();
                Key key = new Key(rs.getLong("id"));
                if (!entities.containsKey(key)) {
                    throw new RuntimeException();
                }
                if ((dialect.isMultitenant()) && !rs.getString("ns").equals(NamespaceManager.getNamespace())) {
                    throw new RuntimeException("namespace acess error");
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
