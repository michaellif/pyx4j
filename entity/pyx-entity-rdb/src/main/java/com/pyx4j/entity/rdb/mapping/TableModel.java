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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Table.PrimaryKeyStrategy;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.rdb.dialect.Dialect;
import com.pyx4j.entity.rdb.dialect.SQLAggregateFunctions;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.server.contexts.NamespaceManager;

public class TableModel {

    public static final int ORDINARY_STRING_LENGHT_MAX = 500;

    public static final int ENUM_STRING_LENGHT_MAX = 50;

    private static final Logger log = LoggerFactory.getLogger(TableModel.class);

    private static final I18n i18n = I18n.get(TableModel.class);

    final String tableName;

    private final Dialect dialect;

    private final Mappings mappings;

    private final EntityMeta entityMeta;

    enum ModelType {

        regular,

        subclass,

        superclass

    }

    final ModelType classModel;

    private final EntityOperationsMeta entityOperationsMeta;

    private final PrimaryKeyStrategy primaryKeyStrategy;

    private String sqlInsert;

    private String sqlUpdate;

    public TableModel(Dialect dialect, Mappings mappings, EntityMeta entityMeta) {
        this.dialect = dialect;
        this.mappings = mappings;
        this.entityMeta = entityMeta;

        Inheritance inheritance = entityMeta.getAnnotation(Inheritance.class);
        if ((inheritance != null) && (inheritance.strategy() != Inheritance.InheritanceStrategy.TABLE_PER_CLASS)) {
            classModel = ModelType.superclass;
        } else if (entityMeta.getPerstableSuperClass() != null) {
            classModel = ModelType.subclass;
        } else {
            classModel = ModelType.regular;
        }

        if ((classModel != ModelType.superclass) && entityMeta.getEntityClass().getAnnotation(AbstractEntity.class) != null) {
            throw new Error("Persistence of @AbstractEntity " + entityMeta.getEntityClass().getName() + " is not permitted");
        }

        Table tableAnnotation = entityMeta.getEntityClass().getAnnotation(Table.class);
        if (tableAnnotation != null) {
            primaryKeyStrategy = tableAnnotation.primaryKeyStrategy();
        } else {
            primaryKeyStrategy = Table.PrimaryKeyStrategy.AUTO;
        }
        tableName = getTableName(dialect, entityMeta);
        entityOperationsMeta = new EntityOperationsMeta(dialect, entityMeta);

        if (dialect.isSequencesBaseIdentity()) {
            for (MemberOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
                member.setSqlSequenceName(dialect.getNamingConvention().sqlChildTableSequenceName(member.sqlName()));
            }
        }
    }

    public static String getTableName(Dialect dialect, EntityMeta entityMeta) {
        return dialect.getNamingConvention().sqlTableName(entityMeta.getPersistenceName());
    }

    public void ensureExists(PersistenceContext persistenceContext) throws SQLException {
        {
            if (Mappings.traceInit) {
                log.trace(Trace.id() + "getTableMetadata {}", tableName);
            }
            TableMetadata tableMetadata = TableMetadata.getTableMetadata(persistenceContext, tableName);
            if (tableMetadata == null) {
                log.debug("table {} does not exists", tableName);
                SQLUtils.execute(persistenceContext.getConnection(), TableDDL.sqlCreate(dialect, this));
                if (Mappings.traceInit) {
                    log.trace(Trace.id() + "table created {}", tableName);
                }
            } else {
                SQLUtils.execute(persistenceContext.getConnection(), TableDDL.validateAndAlter(dialect, tableMetadata, this));
            }
        }

        for (MemberOperationsMeta member : entityOperationsMeta.getAutogeneratedCollectionMembers()) {
            TableMetadata memberTableMetadata = TableMetadata.getTableMetadata(persistenceContext, member.sqlName());
            if (memberTableMetadata == null) {
                SQLUtils.execute(persistenceContext.getConnection(), TableDDL.sqlCreateCollectionMember(dialect, member));
            } else {
                SQLUtils.execute(persistenceContext.getConnection(), TableDDL.validateAndAlterCollectionMember(dialect, memberTableMetadata, member));
            }
        }
    }

    public void ensureForeignKeys(PersistenceContext persistenceContext) throws SQLException {
        TableMetadata tableMetadata = TableMetadata.getTableMetadata(persistenceContext, tableName);
        tableMetadata.readForeignKeys(persistenceContext);
        for (MemberOperationsMeta member : operationsMeta().getColumnMembers()) {
            if (member.getMemberMeta().isEntity() && (!(member.getValueAdapter() instanceof ValueAdapterEntityPolymorphic))) {
                @SuppressWarnings("unchecked")
                Class<? extends IEntity> entityClass = (Class<IEntity>) member.getMemberMeta().getValueClass();
                String refSqlTableName = TableModel.getTableName(dialect, EntityFactory.getEntityMeta(entityClass));

                String constraintName = dialect.getNamingConvention().sqlForeignKeyName(this.getTableName(), member.sqlName(), refSqlTableName);
                if (!tableMetadata.hasForeignKey(constraintName)) {
                    SQLUtils.execute(persistenceContext.getConnection(),
                            TableDDL.sqlCreateForeignKey(dialect, this.getTableName(), member.sqlName(), refSqlTableName));
                }
            }
        }

        for (MemberCollectionOperationsMeta member : entityOperationsMeta.getAutogeneratedCollectionMembers()) {
            TableMetadata memberTableMetadata = TableMetadata.getTableMetadata(persistenceContext, member.sqlName());
            memberTableMetadata.readForeignKeys(persistenceContext);
            {
                String constraintName = dialect.getNamingConvention().sqlForeignKeyName(member.sqlName(), member.sqlOwnerName(), tableName);
                if (!memberTableMetadata.hasForeignKey(constraintName)) {
                    SQLUtils.execute(persistenceContext.getConnection(),
                            TableDDL.sqlCreateForeignKey(dialect, member.sqlName(), member.sqlOwnerName(), tableName));
                }
            }

            if (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet) {
                @SuppressWarnings("unchecked")
                Class<? extends IEntity> entityClass = (Class<IEntity>) member.getMemberMeta().getValueClass();
                if (entityClass.getAnnotation(AbstractEntity.class) == null) {
                    String refSqlTableName = TableModel.getTableName(dialect, EntityFactory.getEntityMeta(entityClass));
                    String constraintName = dialect.getNamingConvention().sqlForeignKeyName(member.sqlName(), member.sqlValueName(), refSqlTableName);
                    if (!memberTableMetadata.hasForeignKey(constraintName)) {
                        SQLUtils.execute(persistenceContext.getConnection(),
                                TableDDL.sqlCreateForeignKey(dialect, member.sqlName(), member.sqlValueName(), refSqlTableName));
                    }
                }
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

    public static boolean isTableExists(PersistenceContext persistenceContext, EntityMeta entityMeta) throws SQLException {
        return (TableMetadata.isTableExists(persistenceContext, getTableName(persistenceContext.getDialect(), entityMeta)));
    }

    public void dropTable(PersistenceContext persistenceContext) throws SQLException {
        List<String> sqls = new Vector<String>();
        for (MemberOperationsMeta member : entityOperationsMeta.getAutogeneratedCollectionMembers()) {
            TableMetadata memberTableMetadata = TableMetadata.getTableMetadata(persistenceContext, member.sqlName());
            if (memberTableMetadata != null) {
                memberTableMetadata.readReferenceForeignKeys(persistenceContext);
                for (String fk : memberTableMetadata.getForeignKeyNames()) {
                    sqls.add(persistenceContext.getDialect().sqlDropForeignKey(member.sqlName(), fk));
                }
                for (Map.Entry<String, String> ref : memberTableMetadata.getForeignKeysReference().entrySet()) {
                    sqls.add(persistenceContext.getDialect().sqlDropForeignKey(ref.getValue(), ref.getKey()));
                }
                sqls.add("DROP TABLE " + member.sqlName());
            }
        }
        SQLUtils.execute(persistenceContext.getConnection(), sqls);
        sqls.clear();
        TableMetadata memberTableMetadata = TableMetadata.getTableMetadata(persistenceContext, tableName);
        if (memberTableMetadata != null) {
            memberTableMetadata.readReferenceForeignKeys(persistenceContext);
            for (String fk : memberTableMetadata.getForeignKeyNames()) {
                sqls.add(persistenceContext.getDialect().sqlDropForeignKey(tableName, fk));
            }
            for (Map.Entry<String, String> ref : memberTableMetadata.getForeignKeysReference().entrySet()) {
                sqls.add(persistenceContext.getDialect().sqlDropForeignKey(ref.getValue(), ref.getKey()));
            }
        }
        sqls.add("DROP TABLE " + tableName);
        SQLUtils.execute(persistenceContext.getConnection(), sqls);
    }

    public int dropForeignKeys(PersistenceContext persistenceContext) throws SQLException {
        int count = 0;
        List<String> sqls = new Vector<String>();
        for (MemberOperationsMeta member : entityOperationsMeta.getAutogeneratedCollectionMembers()) {
            TableMetadata memberTableMetadata = TableMetadata.getTableMetadata(persistenceContext, member.sqlName());
            if (memberTableMetadata != null) {
                memberTableMetadata.readReferenceForeignKeys(persistenceContext);
                for (String fk : memberTableMetadata.getForeignKeyNames()) {
                    sqls.add(persistenceContext.getDialect().sqlDropForeignKey(member.sqlName(), fk));
                    count++;
                }
                for (Map.Entry<String, String> ref : memberTableMetadata.getForeignKeysReference().entrySet()) {
                    sqls.add(persistenceContext.getDialect().sqlDropForeignKey(ref.getValue(), ref.getKey()));
                    count++;
                }
            }
        }
        SQLUtils.execute(persistenceContext.getConnection(), sqls);
        sqls.clear();
        TableMetadata memberTableMetadata = TableMetadata.getTableMetadata(persistenceContext, tableName);
        if (memberTableMetadata != null) {
            memberTableMetadata.readReferenceForeignKeys(persistenceContext);
            for (String fk : memberTableMetadata.getForeignKeyNames()) {
                sqls.add(persistenceContext.getDialect().sqlDropForeignKey(tableName, fk));
                count++;
            }
            for (Map.Entry<String, String> ref : memberTableMetadata.getForeignKeysReference().entrySet()) {
                sqls.add(persistenceContext.getDialect().sqlDropForeignKey(ref.getValue(), ref.getKey()));
                count++;
            }
        }
        SQLUtils.execute(persistenceContext.getConnection(), sqls);
        return count;
    }

    private String sqlInsert(boolean autoGeneratedKeys) {
        if (sqlInsert == null) {
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ");
            sql.append(tableName);
            sql.append(" (");
            int numberOfParams = 0;

            if (classModel != ModelType.regular) {
                if (numberOfParams > 0) {
                    sql.append(", ");
                }
                sql.append(dialect.sqlDiscriminatorColumnName());
                numberOfParams++;
            }

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
            if (dialect.isMultitenantSharedSchema()) {
                if (numberOfParams > 0) {
                    sql.append(", ");
                }
                sql.append(dialect.getNamingConvention().sqlNameSpaceColumnName());
                numberOfParams++;
            }
            if (!autoGeneratedKeys) {
                if (numberOfParams > 0) {
                    sql.append(", ");
                }
                sql.append(dialect.getNamingConvention().sqlIdColumnName());
                numberOfParams++;
            } else if (dialect.isSequencesBaseIdentity()) {
                if (numberOfParams > 0) {
                    sql.append(", ");
                }
                sql.append(dialect.getNamingConvention().sqlIdColumnName());
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

            if (classModel != ModelType.regular) {
                sql.append(dialect.sqlDiscriminatorColumnName());
                first = false;
            }

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
            sql.append(" WHERE ").append(dialect.getNamingConvention().sqlIdColumnName()).append(" = ? ");
            if (dialect.isMultitenantSharedSchema()) {
                sql.append(" AND ").append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(" = ? ");
            }
            sqlUpdate = sql.toString();
        }
        return sqlUpdate;
    }

    private void bindParameter(PreparedStatement stmt, int parameterIndex, Class<?> valueClass, Object value, MemberMeta memberMeta) throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, dialect.getTargetSqlType(valueClass));
        } else {
            stmt.setObject(parameterIndex, encodeValue(valueClass, value), dialect.getTargetSqlType(valueClass));
        }
    }

    private int bindPersistParameters(int parameterIndex, PersistenceContext persistenceContext, PreparedStatement stmt, IEntity entity) throws SQLException {
        for (MemberOperationsMeta member : entityOperationsMeta.getColumnMembers()) {
            if (member.getMemberMeta().isEntity()) {
                IEntity childEntity = (IEntity) member.getMember(entity);
                if ((childEntity.getPrimaryKey() == null) && !childEntity.isNull()) {
                    log.error("Saving non persisted reference {}\n{}\n", childEntity, Trace.getCallOrigin(EntityPersistenceServiceRDB.class));
                    throw new Error("Saving non persisted reference " + childEntity.getDebugExceptionInfoString());
                }
                if (member.isOwnerColumn() && childEntity.isNull() && member.getMemberMeta().getAnnotation(NotNull.class) != null) {
                    log.error("Saving empty owner reference {}\n{}\n", childEntity, Trace.getCallOrigin(EntityPersistenceServiceRDB.class));
                    throw new Error("Saving empty owner reference " + childEntity.getDebugExceptionInfoString());
                }
                parameterIndex += member.getValueAdapter().bindValue(persistenceContext, stmt, parameterIndex, childEntity);
            } else {
                parameterIndex += member.getValueAdapter().bindValue(persistenceContext, stmt, parameterIndex, member.getPersistMemberValue(entity));
            }
        }
        for (MemberOperationsMeta member : entityOperationsMeta.getIndexMembers()) {
            bindParameter(stmt, parameterIndex, member.getIndexValueClass(), member.getIndexedValue(entity), null);
            parameterIndex++;
        }
        return parameterIndex;
    }

    public void insert(PersistenceContext persistenceContext, IEntity entity) {
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
                stmt = persistenceContext.getConnection().prepareStatement(sql, new String[] { dialect.getNamingConvention().sqlIdColumnName() });
            } else {
                stmt = persistenceContext.getConnection().prepareStatement(sql, autoGeneratedKeys);
            }
            int parameterIndex = 1;
            if (classModel != ModelType.regular) {
                DiscriminatorValue discriminator = entity.getValueClass().getAnnotation(DiscriminatorValue.class);
                stmt.setString(parameterIndex, discriminator.value());
                parameterIndex++;
            }
            parameterIndex = bindPersistParameters(parameterIndex, persistenceContext, stmt, entity);
            if (dialect.isMultitenantSharedSchema()) {
                stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                parameterIndex++;
            }
            if (autoGeneratedKeys == Statement.NO_GENERATED_KEYS) {
                if (entity.getPrimaryKey() == null) {
                    throw new Error("Can't persist Entity without assigned PK");
                }
                stmt.setLong(parameterIndex, entity.getPrimaryKey().asLong());
            }
            persistenceContext.setUncommittedChanges();
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

    public boolean update(PersistenceContext persistenceContext, IEntity entity) {
        PreparedStatement stmt = null;
        String sql = null;
        try {
            sql = sqlUpdate();
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql);
            // Just in case, used for pooled connections 
            stmt.setMaxRows(1);
            int parameterIndex = 1;
            parameterIndex = bindPersistParameters(parameterIndex, persistenceContext, stmt, entity);
            stmt.setLong(parameterIndex, entity.getPrimaryKey().asLong());
            if (dialect.isMultitenantSharedSchema()) {
                parameterIndex++;
                stmt.setString(parameterIndex, NamespaceManager.getNamespace());
            }
            persistenceContext.setUncommittedChanges();
            boolean updated = (stmt.executeUpdate() == 1);
            if (EntityPersistenceServiceRDB.traceWarnings) {
                SQLUtils.logAndClearWarnings(persistenceContext.getConnection());
            }
            return updated;
        } catch (SQLException e) {
            log.error("{} SQL {}", tableName, sql);
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
                            if (entity.getOwner().getPrimaryKey().asLong() != valueEntity.getPrimaryKey().asLong()) {
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

    private void retrieveExternal(PersistenceContext persistenceContext, IEntity entity) {
        for (MemberCollectionOperationsMeta member : entityOperationsMeta.getCollectionMembers()) {
            if (member.getMemberMeta().getAttachLevel() != AttachLevel.Detached) {
                TableModelCollections.retrieve(persistenceContext, entity, member);
            }
        }
        for (MemberExternalOperationsMeta member : entityOperationsMeta.getExternalMembers()) {
            // Special handling for recursive retrieve of Owner
            if ((entity.getOwner() != null) && (entity.getMeta() != null) && entity.getMeta().isOwnedRelationships()) {
                // verify graph integrity
                continue;
            }
            TableModleExternal.retrieve(persistenceContext, entity, member);
        }

        for (MemberOperationsMeta member : entityOperationsMeta.getVersionInfoMembers()) {
            TableModleVersioned.retrieveVersion(persistenceContext, mappings, entity, member);
        }
    }

    public void retrieveMember(PersistenceContext persistenceContext, IEntity entity, IEntity entityMember) {
        MemberOperationsMeta member = entityOperationsMeta.getMember(new Path(entity.getValueClass(), entityMember.getFieldName()).toString());
        assert (member != null) : "Member " + entityMember.getFieldName() + " not found";
        TableModleExternal.retrieve(persistenceContext, entity, (MemberExternalOperationsMeta) member);
    }

    public void retrieveMember(PersistenceContext persistenceContext, IEntity entity, ICollection<?, ?> collectionMember) {
        MemberOperationsMeta member = entityOperationsMeta.getMember(new Path(entity.getValueClass(), collectionMember.getFieldName()).toString());
        assert (member != null) : "Member " + collectionMember.getFieldName() + " not found";
        TableModelCollections.retrieve(persistenceContext, entity, (MemberCollectionOperationsMeta) member);
    }

    public boolean retrieve(PersistenceContext persistenceContext, Key primaryKey, IEntity entity) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("SELECT * FROM ").append(tableName);
            sql.append(" WHERE ").append(dialect.getNamingConvention().sqlIdColumnName()).append(" = ?");
            if (dialect.isMultitenantSharedSchema()) {
                sql.append(" AND ").append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(" = ?");
            }
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql.toString());
            // Just in case, used for pooled connections 
            stmt.setMaxRows(1);

            stmt.setLong(1, primaryKey.asLong());
            if (dialect.isMultitenantSharedSchema()) {
                stmt.setString(2, NamespaceManager.getNamespace());
            }

            rs = stmt.executeQuery();
            if (!rs.next()) {
                return false;
            } else {
                Key key = new Key(rs.getLong(dialect.getNamingConvention().sqlIdColumnName()));
                // Ignore version in comparison
                if (primaryKey.asLong() != key.asLong()) {
                    throw new RuntimeException();
                }
                if ((dialect.isMultitenantSharedSchema())
                        && !rs.getString(dialect.getNamingConvention().sqlNameSpaceColumnName()).equals(NamespaceManager.getNamespace())) {
                    throw new RuntimeException("namespace access error");
                }
                // Preserve version when retrieving key
                entity.setPrimaryKey(primaryKey);

                if (classModel != ModelType.regular) {
                    String discriminator = rs.getString(dialect.sqlDiscriminatorColumnName());
                    if (classModel == ModelType.superclass) {
                        Class<? extends IEntity> subclass = entityOperationsMeta.impClasses.get(discriminator);
                        TableModel subclassModel = mappings.getTableModel(persistenceContext, subclass);

                        IEntity subclassValue = EntityFactory.create(subclass);
                        subclassValue.setPrimaryKey(primaryKey);
                        entity.set(subclassValue);

                        subclassModel.retrieveValues(rs, subclassValue);
                        subclassModel.retrieveExternal(persistenceContext, subclassValue);
                        return true;
                    } else {
                        //TODO assert discriminator value
                    }
                }
                retrieveValues(rs, entity);
                retrieveExternal(persistenceContext, entity);
                return true;
            }
        } catch (SQLException e) {
            log.error("{} SQL: {}", tableName, sql);
            log.error("{} SQL select error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

    public <T extends IEntity> List<T> query(PersistenceContext persistenceContext, EntityQueryCriteria<T> criteria, int limit) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = null;
        try {
            QueryBuilder<T> qb = new QueryBuilder<T>(persistenceContext, mappings, "m1", entityOperationsMeta, criteria);
            sql = "SELECT " + (qb.addDistinct() ? "DISTINCT" : "") + " m1.* FROM " + qb.getSQL(tableName);
            //log.info("query {}", sql);
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
            stmt = persistenceContext.getConnection().prepareStatement(sql);
            if (limit > 0) {
                stmt.setMaxRows(limit);
            } else {
                // zero means there is no limit, Need for pooled connections 
                stmt.setMaxRows(0);
            }
            int parameterIndex = qb.bindParameters(persistenceContext, stmt);
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
                entity.setPrimaryKey(new Key(rs.getLong(dialect.getNamingConvention().sqlIdColumnName())));
                if (criteria.getVersionedCriteria() == VersionedCriteria.onlyDraft) {
                    entity.setPrimaryKey(entity.getPrimaryKey().asDraftKey());
                }
                if ((dialect.isMultitenantSharedSchema())
                        && !rs.getString(dialect.getNamingConvention().sqlNameSpaceColumnName()).equals(NamespaceManager.getNamespace())) {
                    throw new RuntimeException("namespace access error");
                }
                retrieveValues(rs, entity);
                retrieveExternal(persistenceContext, entity);

                rc.add(entity);
            }
            return rc;
        } catch (SQLException e) {
            log.error("{} SQL: {}", tableName, sql);
            log.error("{} SQL select error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

    public <T extends IEntity> ResultSetIterator<T> queryIterable(final PersistenceContext persistenceContext, final EntityQueryCriteria<T> criteria) {
        String sql = null;
        QueryBuilder<T> qb = new QueryBuilder<T>(persistenceContext, mappings, "m1", entityOperationsMeta, criteria);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            sql = "SELECT " + (qb.addDistinct() ? "DISTINCT" : "") + " m1.* FROM " + qb.getSQL(tableName);
            //log.info("query {}", sql);
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
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql);
            if (limit > 0) {
                stmt.setMaxRows(limit);
            } else {
                // zero means there is no limit, Need for pooled connections 
                stmt.setMaxRows(0);
            }
            int parameterIndex = qb.bindParameters(persistenceContext, stmt);
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
            log.error("{} SQL: {}", tableName, sql);
            log.error("{} SQL select error", tableName, e);
            throw new RuntimeException(e);
        }

        return new ResultSetIterator<T>(stmt, rs) {

            @Override
            protected T retrieve() {
                @SuppressWarnings("unchecked")
                T entity = (T) EntityFactory.create(entityMeta.getEntityClass());
                try {
                    entity.setPrimaryKey(new Key(rs.getLong(dialect.getNamingConvention().sqlIdColumnName())));
                    if (criteria.getVersionedCriteria() == VersionedCriteria.onlyDraft) {
                        entity.setPrimaryKey(entity.getPrimaryKey().asDraftKey());
                    }
                    retrieveValues(rs, entity);
                } catch (SQLException e) {
                    log.error("{} SQL select error", tableName, e);
                    throw new RuntimeException(e);
                }
                retrieveExternal(persistenceContext, entity);
                return entity;
            }
        };

    }

    public boolean exists(PersistenceContext persistenceContext, Key primaryKey) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("SELECT ").append(dialect.getNamingConvention().sqlIdColumnName());
            sql.append(" FROM ").append(tableName).append(" WHERE ").append(dialect.getNamingConvention().sqlIdColumnName()).append(" = ?");
            if (dialect.isMultitenantSharedSchema()) {
                sql.append(" AND ").append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(" = ?");
            }
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql.toString());
            // Just in case, used for pooled connections 
            stmt.setMaxRows(1);

            stmt.setLong(1, primaryKey.asLong());
            if (dialect.isMultitenantSharedSchema()) {
                stmt.setString(2, NamespaceManager.getNamespace());
            }

            rs = stmt.executeQuery();
            if (!rs.next()) {
                return false;
            } else {
                Key key = new Key(rs.getLong(dialect.getNamingConvention().sqlIdColumnName()));
                if (!primaryKey.equals(key)) {
                    throw new RuntimeException();
                }
                return true;
            }
        } catch (SQLException e) {
            log.error("{} SQL: {}", tableName, sql);
            log.error("{} SQL select error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

    public <T extends IEntity> List<Key> queryKeys(PersistenceContext persistenceContext, EntityQueryCriteria<T> criteria, int limit) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = null;
        try {
            QueryBuilder<T> qb = new QueryBuilder<T>(persistenceContext, mappings, "m1", entityOperationsMeta, criteria);
            sql = "SELECT " + (qb.addDistinct() ? "DISTINCT" : "") + " m1." + dialect.getNamingConvention().sqlIdColumnName() + " FROM " + qb.getSQL(tableName);
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql);
            if (limit > 0) {
                stmt.setMaxRows(limit);
            } else {
                // zero means there is no limit, Need for pooled connections 
                stmt.setMaxRows(0);
            }
            qb.bindParameters(persistenceContext, stmt);

            rs = stmt.executeQuery();

            List<Key> rc = new Vector<Key>();
            while (rs.next()) {
                rc.add(new Key(rs.getLong(dialect.getNamingConvention().sqlIdColumnName())));
            }
            return rc;
        } catch (SQLException e) {
            log.error("{} SQL: {}", tableName, sql);
            log.error("{} SQL select error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
        }
    }

    public <T extends IEntity> ResultSetIterator<Key> queryKeysIterable(final PersistenceContext persistenceContext, EntityQueryCriteria<T> criteria) {
        String sql = null;
        QueryBuilder<T> qb = new QueryBuilder<T>(persistenceContext, mappings, "m1", entityOperationsMeta, criteria);
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            sql = "SELECT " + (qb.addDistinct() ? "DISTINCT" : "") + " m1." + dialect.getNamingConvention().sqlIdColumnName() + " FROM " + qb.getSQL(tableName);
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
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql);
            if (limit > 0) {
                stmt.setMaxRows(limit);
            } else {
                // zero means there is no limit, Need for pooled connections 
                stmt.setMaxRows(0);
            }
            int parameterIndex = qb.bindParameters(persistenceContext, stmt);
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
            log.error("{} SQL: {}", tableName, sql);
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

    public <T extends IEntity> Object aggregate(PersistenceContext persistenceContext, EntityQueryCriteria<T> criteria, SQLAggregateFunctions func, String args) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            EntityQueryCriteria<T> criteriaNoSorts = criteria.iclone();
            criteriaNoSorts.setSorts(null);
            QueryBuilder<T> qb = new QueryBuilder<T>(persistenceContext, mappings, "m1", entityOperationsMeta, criteriaNoSorts);
            stmt = persistenceContext.getConnection().prepareStatement("SELECT " + dialect.sqlFunction(func, args) + " FROM " + qb.getSQL(tableName));
            // Just in case, used for pooled connections 
            stmt.setMaxRows(1);
            qb.bindParameters(persistenceContext, stmt);

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

    public boolean delete(PersistenceContext persistenceContext, Key primaryKey) {
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("DELETE FROM ").append(tableName).append(" WHERE ").append(dialect.getNamingConvention().sqlIdColumnName()).append(" = ?");
            if (dialect.isMultitenantSharedSchema()) {
                sql.append(" AND ").append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(" = ?");
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql.toString());
            stmt.setLong(1, primaryKey.asLong());
            if (dialect.isMultitenantSharedSchema()) {
                stmt.setString(2, NamespaceManager.getNamespace());
            }
            persistenceContext.setUncommittedChanges();
            int rc = stmt.executeUpdate();
            return rc >= 1;
        } catch (SQLIntegrityConstraintViolationException e) {
            log.error("{} SQL: {}", tableName, sql);
            log.error("{} SQL delete error", tableName, e);
            throw new UserRuntimeException(i18n.tr("Unable to delete \"{0}\". The record is referenced by another record.", entityMeta().getCaption()));
        } catch (SQLException e) {
            log.error("{} SQL: {}", tableName, sql);
            log.error("{} SQL delete error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    public int delete(PersistenceContext persistenceContext, Iterable<Key> primaryKeys) {
        PreparedStatement stmt = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("DELETE FROM ").append(tableName).append(" WHERE ").append(dialect.getNamingConvention().sqlIdColumnName()).append(" = ?");
            if (dialect.isMultitenantSharedSchema()) {
                sql.append(" AND ").append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(" = ?");
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql.toString());
            int pkSize = 0;
            for (Key primaryKey : primaryKeys) {
                stmt.setLong(1, primaryKey.asLong());
                if (dialect.isMultitenantSharedSchema()) {
                    stmt.setString(2, NamespaceManager.getNamespace());
                }
                stmt.addBatch();
                pkSize++;
            }
            if (pkSize == 0) {
                return pkSize;
            }
            persistenceContext.setUncommittedChanges();
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
            log.error("{} SQL: {}", tableName, sql);
            log.error("{} SQL delete error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }
    }

    public void truncate(PersistenceContext persistenceContext) {
        PreparedStatement stmt = null;
        try {
            stmt = persistenceContext.getConnection().prepareStatement("TRUNCATE TABLE " + tableName);
            stmt.execute();
        } catch (SQLException e) {
            log.error("{} SQL delete error", tableName, e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(stmt);
        }

    }

    public <T extends IEntity> boolean insert(PersistenceContext persistenceContext, Iterable<T> entityIterable) {
        PreparedStatement stmt = null;
        int[] vals = null;
        int autoGeneratedKeys = Statement.RETURN_GENERATED_KEYS;
        try {
            if (getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) {
                autoGeneratedKeys = Statement.NO_GENERATED_KEYS;
            }
            stmt = persistenceContext.getConnection().prepareStatement(sqlInsert(autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS), autoGeneratedKeys);
            for (T entity : entityIterable) {
                int parameterIndex = 1;
                parameterIndex = bindPersistParameters(parameterIndex, persistenceContext, stmt, entity);
                if (autoGeneratedKeys == Statement.NO_GENERATED_KEYS) {
                    stmt.setLong(parameterIndex, entity.getPrimaryKey().asLong());
                    parameterIndex++;
                }
                if (dialect.isMultitenantSharedSchema()) {
                    stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                    parameterIndex++;
                }
                stmt.addBatch();
            }
            persistenceContext.setUncommittedChanges();
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

    public <T extends IEntity> boolean insertBulk(PersistenceContext persistenceContext, Iterable<T> entityIterable) {
        PreparedStatement stmt = null;
        int[] vals = null;
        try {
            boolean hasKeys = (getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED);
            stmt = persistenceContext.getConnection().prepareStatement(sqlInsert(!hasKeys));
            for (T entity : entityIterable) {
                int parameterIndex = 1;
                parameterIndex = bindPersistParameters(parameterIndex, persistenceContext, stmt, entity);
                if (hasKeys) {
                    stmt.setLong(parameterIndex, entity.getPrimaryKey().asLong());
                    parameterIndex++;
                }
                if (dialect.isMultitenantSharedSchema()) {
                    stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                    parameterIndex++;
                }
                stmt.addBatch();
            }
            persistenceContext.setUncommittedChanges();
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

    public <T extends IEntity> void persist(PersistenceContext persistenceContext, Iterable<T> entityIterable, List<T> notUpdated) {
        PreparedStatement stmt = null;
        int[] vals = null;
        Vector<T> all = new Vector<T>();
        try {
            stmt = persistenceContext.getConnection().prepareStatement(sqlUpdate());
            // zero means there is no limit, Need for pooled connections 
            stmt.setMaxRows(0);
            for (T entity : entityIterable) {
                if (entity.getPrimaryKey() == null) {
                    // persist(Connection connection, Iterable<T> entityIterable) should be called on entities with non-NULL PKs
                    // ??? log.error(" persist(Connection connection, Iterable<T> entityIterable) should be called on entities with non-NULL PKs", tableName);
                    throw new RuntimeException();
                }
                int parameterIndex = 1;
                parameterIndex = bindPersistParameters(parameterIndex, persistenceContext, stmt, entity);
                stmt.setLong(parameterIndex, entity.getPrimaryKey().asLong());
                if (dialect.isMultitenantSharedSchema()) {
                    parameterIndex++;
                    stmt.setString(parameterIndex, NamespaceManager.getNamespace());
                }
                stmt.addBatch();
                all.add(entity);
            }
            persistenceContext.setUncommittedChanges();
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

    public <T extends IEntity> boolean retrieve(PersistenceContext persistenceContext, Map<Key, T> entities) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(tableName).append(" WHERE ").append(dialect.getNamingConvention().sqlIdColumnName()).append(" IN (");
        int count = 0;
        for (Key primaryKey : entities.keySet()) {
            if (count != 0) {
                sql.append(',');
            }
            sql.append(primaryKey);
            count++;
        }
        sql.append(')');
        if (dialect.isMultitenantSharedSchema()) {
            sql.append(" AND ").append(dialect.getNamingConvention().sqlNameSpaceColumnName()).append(" = ?");
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            if (EntityPersistenceServiceRDB.traceSql) {
                log.debug(Trace.id() + " {} ", sql);
            }
            stmt = persistenceContext.getConnection().prepareStatement(sql.toString());
            // zero means there is no limit, Need for pooled connections 
            stmt.setMaxRows(0);
            if (dialect.isMultitenantSharedSchema()) {
                stmt.setString(1, NamespaceManager.getNamespace());
            }

            rs = stmt.executeQuery();
            for (int i = 0; i < count; i++) {
                rs.next();
                Key key = new Key(rs.getLong(dialect.getNamingConvention().sqlIdColumnName()));
                if (!entities.containsKey(key)) {
                    throw new RuntimeException();
                }
                if ((dialect.isMultitenantSharedSchema())
                        && !rs.getString(dialect.getNamingConvention().sqlNameSpaceColumnName()).equals(NamespaceManager.getNamespace())) {
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
