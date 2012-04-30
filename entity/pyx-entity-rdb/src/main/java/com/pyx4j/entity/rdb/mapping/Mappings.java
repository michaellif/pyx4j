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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.rdb.ConnectionProvider;
import com.pyx4j.entity.rdb.ConnectionProvider.ConnectionTarget;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.rdb.SQLUtils;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class Mappings {

    private static final Logger log = LoggerFactory.getLogger(Mappings.class);

    private final ConnectionProvider connectionProvider;

    private final Configuration configuration;

    private final Map<Class<? extends IEntity>, TableModel> tables = new Hashtable<Class<? extends IEntity>, TableModel>();

    private final Set<String> usedTableNames = new HashSet<String>();

    private Set<String> sequences = null;

    private static final Map<Class<? extends IEntity>, Object> entityLocks = new Hashtable<Class<? extends IEntity>, Object>();

    public static final boolean traceInit = false;

    public Mappings(ConnectionProvider connectionProvider, Configuration configuration) {
        this.connectionProvider = connectionProvider;
        this.configuration = configuration;
    }

    public void reset() {
        tables.clear();
        usedTableNames.clear();
        sequences = null;
    }

    public TableModel getTableModel(PersistenceContext persistenceContext, Class<? extends IEntity> entityClass) {
        return ensureTable(persistenceContext, EntityFactory.getEntityMeta(entityClass));
    }

    public EntityOperationsMeta getEntityOperationsMeta(PersistenceContext persistenceContext, Class<? extends IEntity> entityClass) {
        return getTableModel(persistenceContext, entityClass).operationsMeta();
    }

    public static void assertPersistableEntity(EntityMeta entityMeta) {
        if (entityMeta.isTransient()) {
            throw new Error("Can't operate on Transient Entity " + entityMeta.getEntityClass().getName());
        }
        if (entityMeta.getAnnotation(EmbeddedEntity.class) != null) {
            throw new Error("Can't operate on Embedded Entity " + entityMeta.getEntityClass().getName());
        }
    }

    public static Collection<Class<? extends IEntity>> getPersistableAssignableFrom(Class<? extends IEntity> entityClass) {
        List<Class<? extends IEntity>> allAssignableClasses = new ArrayList<Class<? extends IEntity>>();
        for (Class<? extends IEntity> ec : ServerEntityFactory.getAllAssignableFrom(entityClass)) {
            if ((ec.getAnnotation(Transient.class) == null) && (ec.getAnnotation(AbstractEntity.class) == null)) {
                allAssignableClasses.add(ec);
            }
        }
        return allAssignableClasses;
    }

    public TableModel ensureTable(PersistenceContext persistenceContext, EntityMeta entityMeta) {
        TableModel model = tables.get(entityMeta.getEntityClass());
        if (model != null) {
            return model;
        }
        assertPersistableEntity(entityMeta);
        Inheritance inheritance = entityMeta.getAnnotation(Inheritance.class);
        if ((entityMeta.getAnnotation(AbstractEntity.class) != null) && (entityMeta.getPerstableSuperClass() == null)) {
            if ((inheritance == null) || (inheritance.strategy() == Inheritance.InheritanceStrategy.TABLE_PER_CLASS)) {
                throw new Error("Can't operate on Abstract Entity " + entityMeta.getEntityClass().getName());
            }
        }

        // Avoid lock on EntityClass
        Object entityTypeLock;
        synchronized (entityLocks) {
            entityTypeLock = entityLocks.get(entityMeta.getEntityClass());
            if (entityTypeLock == null) {
                entityTypeLock = new Object();
                entityLocks.put(entityMeta.getEntityClass(), entityTypeLock);
            }
        }

        if (traceInit) {
            log.trace(Trace.enter() + "ensureTable {} lock {}", entityMeta.getPersistenceName(), System.identityHashCode(entityTypeLock));
        }

        boolean initReferencedTables = false;
        synchronized (entityTypeLock) {
            if (traceInit) {
                log.trace(Trace.id() + "ensureTable {} obtained lock {}", entityMeta.getPersistenceName(), System.identityHashCode(entityTypeLock));
            }

            // Got the lock, see if model already created
            model = tables.get(entityMeta.getEntityClass());
            if (model == null) {
                model = new TableModel(persistenceContext.getDialect(), this, entityMeta);
                initReferencedTables = true;
                if (entityMeta.getPerstableSuperClass() == null) {
                    if (usedTableNames.contains(model.getTableName().toLowerCase(Locale.ENGLISH))) {
                        log.warn("redefining/extending table {} for class {}", model.getTableName(), entityMeta.getEntityClass());
                    }
                    for (MemberOperationsMeta member : model.operationsMeta().getAutogeneratedCollectionMembers()) {
                        if (usedTableNames.contains(member.sqlName().toLowerCase(Locale.ENGLISH))) {
                            log.warn("redefining/extending table {} for member {}", member.sqlName(), member.getMemberPath());
                        }
                    }
                    try {
                        model.ensureExists(persistenceContext);
                    } catch (SQLException e) {
                        log.error("table creation error", e);
                        throw new RuntimeException(e);
                    }
                    if (persistenceContext.getDialect().isSequencesBaseIdentity()) {
                        if (model.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.AUTO) {
                            ensureSequence(persistenceContext,
                                    persistenceContext.getDialect().getNamingConvention().sqlTableSequenceName(entityMeta.getPersistenceName()));
                        }
                        for (MemberOperationsMeta member : model.operationsMeta().getAutogeneratedCollectionMembers()) {
                            ensureSequence(persistenceContext, member.getSqlSequenceName());
                        }
                    }
                    usedTableNames.add(model.getTableName().toLowerCase(Locale.ENGLISH));
                    for (MemberOperationsMeta member : model.operationsMeta().getAutogeneratedCollectionMembers()) {
                        usedTableNames.add(member.sqlName().toLowerCase(Locale.ENGLISH));
                    }
                }
                tables.put(entityMeta.getEntityClass(), model);
            } else if (traceInit) {
                log.trace(Trace.id() + "ensureTable {} TableModel already created", entityMeta.getPersistenceName());
            }
        }
        if (traceInit) {
            log.trace(Trace.returns() + "ensureTable {}", entityMeta.getPersistenceName());
        }

        if (initReferencedTables && (entityMeta.getPerstableSuperClass() != null)) {
            this.getTableModel(persistenceContext, entityMeta.getPerstableSuperClass());
        } else if (initReferencedTables) {
            for (MemberCollectionOperationsMeta member : model.operationsMeta().getAutogeneratedCollectionMembers()) {
                if (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet) {
                    @SuppressWarnings("unchecked")
                    Class<? extends IEntity> entityClass = (Class<IEntity>) member.getMemberMeta().getValueClass();
                    if (entityClass.getAnnotation(AbstractEntity.class) == null) {
                        this.getTableModel(persistenceContext, entityClass);
                    } else {
                        for (Class<? extends IEntity> persistableEntityClass : getPersistableAssignableFrom(entityClass)) {
                            this.getTableModel(persistenceContext, persistableEntityClass);
                        }
                    }
                }
            }
            for (MemberCollectionOperationsMeta member : model.operationsMeta().getJoinTablesCollectionMembers()) {
                if (member.joinTableClass().getAnnotation(AbstractEntity.class) == null) {
                    this.getTableModel(persistenceContext, member.joinTableClass());
                } else {
                    for (Class<? extends IEntity> persistableEntityClass : getPersistableAssignableFrom(member.joinTableClass())) {
                        this.getTableModel(persistenceContext, persistableEntityClass);
                    }
                }
            }
            for (MemberExternalOperationsMeta member : model.operationsMeta().getExternalMembers()) {
                if (member.joinTableClass().getAnnotation(AbstractEntity.class) == null) {
                    this.getTableModel(persistenceContext, member.joinTableClass());
                } else {
                    for (Class<? extends IEntity> persistableEntityClass : getPersistableAssignableFrom(member.joinTableClass())) {
                        this.getTableModel(persistenceContext, persistableEntityClass);
                    }
                }
            }

            // create all references tables
            for (MemberOperationsMeta member : model.operationsMeta().getColumnMembers()) {
                if (member.getMemberMeta().isEntity()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends IEntity> entityClass = (Class<IEntity>) member.getMemberMeta().getValueClass();
                    if (entityClass.getAnnotation(AbstractEntity.class) == null) {
                        this.getTableModel(persistenceContext, entityClass);
                    } else {
                        for (Class<? extends IEntity> persistableEntityClass : getPersistableAssignableFrom(entityClass)) {
                            this.getTableModel(persistenceContext, persistableEntityClass);
                        }
                    }
                }
            }

            if (configuration.createForeignKeys()) {
                synchronized (entityTypeLock) {
                    try {
                        model.ensureForeignKeys(persistenceContext);
                    } catch (SQLException e) {
                        log.error("Foreign Keys creation error", e);
                        throw new RuntimeException(e);
                    }
                }
            }

        }

        return model;
    }

    private void ensureSequence(PersistenceContext persistenceContext, String sequenceName) {
        // Verify Sequence already exists
        initSequences(persistenceContext);
        if (!sequences.contains(sequenceName.toLowerCase(Locale.ENGLISH))) {
            try {
                if (configuration.sharedSequencesSchema() != null) {
                    createSharedSequence(persistenceContext, configuration.sharedSequencesSchema() + "." + sequenceName);
                } else {
                    createSequence(persistenceContext, sequenceName);
                }
            } catch (SQLException e) {
                log.error("sequence creation error", e);
                throw new RuntimeException(e);
            }
            sequences.add(sequenceName.toLowerCase(Locale.ENGLISH));
        }
    }

    private void createSequence(PersistenceContext persistenceContext, String sequenceName) throws SQLException {
        SQLUtils.execute(persistenceContext.getConnection(), persistenceContext.getDialect().getCreateSequenceSql(sequenceName));
    }

    private void createSharedSequence(PersistenceContext persistenceContext, String sequenceName) throws SQLException {
        Connection connection = connectionProvider.getConnection(ConnectionTarget.forDDL);
        try {
            SQLUtils.execute(connection, persistenceContext.getDialect().getCreateSequenceSql(sequenceName));
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    public void dropTable(PersistenceContext persistenceContext, EntityMeta entityMeta) throws SQLException {
        TableModel model = tables.get(entityMeta.getEntityClass());
        if (model == null) {
            model = new TableModel(persistenceContext.getDialect(), this, entityMeta);
        }
        model.dropTable(persistenceContext);
        usedTableNames.remove(model.getTableName().toLowerCase(Locale.ENGLISH));
        tables.remove(entityMeta.getEntityClass());
        for (MemberOperationsMeta member : model.operationsMeta().getAutogeneratedCollectionMembers()) {
            usedTableNames.remove(member.sqlName().toLowerCase(Locale.ENGLISH));
        }

        if (persistenceContext.getDialect().isSequencesBaseIdentity() && (configuration.sharedSequencesSchema() == null)) {
            if (model.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.AUTO) {
                droppedSequence(persistenceContext, persistenceContext.getDialect().getNamingConvention().sqlTableSequenceName(entityMeta.getPersistenceName()));
            }
            for (MemberOperationsMeta member : model.operationsMeta().getAutogeneratedCollectionMembers()) {
                droppedSequence(persistenceContext, member.getSqlSequenceName());
            }
        }
    }

    private void droppedSequence(PersistenceContext persistenceContext, String sequenceName) {
        initSequences(persistenceContext);
        if (sequences.contains(sequenceName.toLowerCase(Locale.ENGLISH))) {
            try {
                SQLUtils.execute(persistenceContext.getConnection(), persistenceContext.getDialect().getDropSequenceSql(sequenceName));
            } catch (SQLException e) {
                log.error("sequence deletion error", e);
                throw new RuntimeException(e);
            }
            sequences.remove(sequenceName.toLowerCase(Locale.ENGLISH));
        }
    }

    private void initSequences(PersistenceContext persistenceContext) {
        if (sequences != null) {
            return;
        }
        sequences = new HashSet<String>();
        Connection connection = null;
        if (configuration.sharedSequencesSchema() != null) {
            connection = connectionProvider.getConnection(ConnectionTarget.forRead);
        } else {
            connection = persistenceContext.getConnection();
        }
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(persistenceContext.getDialect().sqlSequenceMetaData());
            while (rs.next()) {
                sequences.add(rs.getString(1).trim().toLowerCase(Locale.ENGLISH));
            }
        } catch (SQLException e) {
            log.error("query sequences metadata error", e);
            throw new RuntimeException(e);
        } finally {
            SQLUtils.closeQuietly(rs);
            SQLUtils.closeQuietly(stmt);
            if (configuration.sharedSequencesSchema() != null) {
                SQLUtils.closeQuietly(connection);
            }
        }
    }

}
