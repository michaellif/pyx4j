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
 * Created on 2010-07-07
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.Trace;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.adapters.EntityModificationAdapter;
import com.pyx4j.entity.adapters.MemberModificationAdapter;
import com.pyx4j.entity.adapters.ReferenceAdapter;
import com.pyx4j.entity.annotations.Adapters;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.rdb.ConnectionProvider.ConnectionTarget;
import com.pyx4j.entity.rdb.PersistenceContext.TransactionType;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.dialect.SQLAggregateFunctions;
import com.pyx4j.entity.rdb.mapping.Mappings;
import com.pyx4j.entity.rdb.mapping.MemberCollectionOperationsMeta;
import com.pyx4j.entity.rdb.mapping.MemberOperationsMeta;
import com.pyx4j.entity.rdb.mapping.ResultSetIterator;
import com.pyx4j.entity.rdb.mapping.TableMetadata;
import com.pyx4j.entity.rdb.mapping.TableModel;
import com.pyx4j.entity.rdb.mapping.TableModelCollections;
import com.pyx4j.entity.rdb.mapping.TableModleVersioned;
import com.pyx4j.entity.rdb.mapping.ValueAdapterEntityPolymorphic;
import com.pyx4j.entity.server.AdapterFactory;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.IEntityPersistenceServiceExt;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.ConcurrentUpdateException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.shared.SecurityViolationException;

/**
 * 
 * @see PersistenceServicesFactory#RDBMS_IMPL_CLASS
 * 
 */
public class EntityPersistenceServiceRDB implements IEntityPersistenceService, IEntityPersistenceServiceExt {

    private static final Logger log = LoggerFactory.getLogger(EntityPersistenceServiceRDB.class);

    private static final I18n i18n = I18n.get(EntityPersistenceServiceRDB.class);

    private final ConnectionProvider connectionProvider;

    private final Mappings mappings;

    public static final boolean trace = false;

    public static final boolean traceSql = false;

    public static final boolean traceWarnings = false;

    private final ThreadLocal<PersistenceContext> threadSessions = new ThreadLocal<PersistenceContext>();

    public EntityPersistenceServiceRDB() {
        this(RDBUtils.getRDBConfiguration());
    }

    public EntityPersistenceServiceRDB(Configuration configuration) {
        synchronized (configuration.getClass()) {
            try {
                connectionProvider = new ConnectionProvider(configuration);
            } catch (SQLException e) {
                log.error("RDB initialization error", e);
                throw new RuntimeException(e.getMessage());
            }
            mappings = new Mappings(connectionProvider, configuration);
            databaseVersion();
        }
    }

    @Override
    public void dispose() {
        connectionProvider.dispose();
    }

    @Override
    public void deregister() {
        connectionProvider.deregister();
        CacheService.shutdown();
    }

    private void startContext(ConnectionTarget reason) {
        if ((reason == ConnectionTarget.forUpdate) && ServerSideConfiguration.instance().datastoreReadOnly()) {
            throw new UserRuntimeException(ServerSideConfiguration.instance().getApplicationMaintenanceMessage());
        }
        PersistenceContext persistenceContext = threadSessions.get();
        if (persistenceContext == null) {
            threadSessions.set(new PersistenceContext(connectionProvider, TransactionType.AutoCommit));
        } else {
            assert (persistenceContext.isExplicitTransaction()) : "PersistenceContext leftover detected ";
        }
    }

    private void endContext() {
        PersistenceContext persistenceContext = threadSessions.get();
        if ((persistenceContext != null) && (!persistenceContext.isExplicitTransaction())) {
            try {
                persistenceContext.close();
            } finally {
                threadSessions.remove();
            }
        }
    }

    private PersistenceContext getPersistenceContext() {
        return threadSessions.get();
    }

    @Override
    public void startTransaction() {
        PersistenceContext persistenceContext = threadSessions.get();
        if (persistenceContext != null) {
            if (persistenceContext.isExplicitTransaction()) {
                persistenceContext.savepointCreate();
                return;
            }
        }
        threadSessions.set(new PersistenceContext(connectionProvider, TransactionType.ExplicitTransaction));
    }

    @Override
    public void startBackgroundProcessTransaction() {
        PersistenceContext persistenceContext = threadSessions.get();
        if (persistenceContext != null) {
            if (persistenceContext.isBackgroundProcessTransaction()) {
                persistenceContext.savepointCreate();
                return;
            }
            endTransaction();

        }
        threadSessions.set(new PersistenceContext(connectionProvider, TransactionType.BackgroundProcess));
    }

    @Override
    public void endTransaction() {
        PersistenceContext persistenceContext = threadSessions.get();
        if (persistenceContext != null) {
            if (persistenceContext.savepointActive()) {
                persistenceContext.savepointRelease();
            } else {
                try {
                    persistenceContext.close();
                } finally {
                    threadSessions.remove();
                }
            }
        }
    }

    @Override
    public void setTransactionSystemTime(Date date) {
        assert getPersistenceContext() != null : "Transaction Context was not started";
        getPersistenceContext().setTimeNow(date);
    }

    @Override
    public void setTransactionUserKey(Key currentUserKey) {
        assert getPersistenceContext() != null : "Transaction Context was not started";
        getPersistenceContext().setCurrentUserKey(currentUserKey);
    }

    @Override
    public void removeThreadLocale() {
        PersistenceContext persistenceContext = threadSessions.get();
        if (persistenceContext != null) {
            try {
                persistenceContext.terminate();
            } finally {
                threadSessions.remove();
            }
        }
    }

    @Override
    public void commit() {
        PersistenceContext persistenceContext = threadSessions.get();
        if ((persistenceContext == null) || (!persistenceContext.isExplicitTransaction())) {
            throw new Error("There are no open transaction");
        }
        persistenceContext.commit();
    }

    @Override
    public void rollback() {
        PersistenceContext persistenceContext = threadSessions.get();
        if ((persistenceContext == null) || (!persistenceContext.isExplicitTransaction())) {
            throw new Error("There are no open transaction");
        }
        persistenceContext.rollback();
    }

    public boolean isTableExists(Class<? extends IEntity> entityClass) {
        try {
            return TableModel.isTableExists(connectionProvider, EntityFactory.getEntityMeta(entityClass));
        } catch (SQLException e) {
            log.error("table exists error", e);
            throw new RuntimeExceptionSerializable(e);
        }
    }

    public void dropTable(Class<? extends IEntity> entityClass) {
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        TableModel tm = new TableModel(connectionProvider.getDialect(), mappings, entityMeta);
        try {
            tm.dropTable(connectionProvider);
            mappings.droppedTable(connectionProvider.getDialect(), entityMeta);
        } catch (SQLException e) {
            log.error("drop table error", e);
            throw new RuntimeExceptionSerializable(e);
        }
    }

    public int dropForeignKeys(Class<? extends IEntity> entityClass) {
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        TableModel tm = new TableModel(connectionProvider.getDialect(), mappings, entityMeta);
        try {
            return tm.dropForeignKeys(connectionProvider);
        } catch (SQLException e) {
            log.error("drop ForeignKeys error", e);
            throw new RuntimeExceptionSerializable(e);
        }
    }

    private void databaseVersion() {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            DatabaseMetaData dbMeta = connection.getMetaData();
            log.debug("DB {} {}", dbMeta.getDatabaseProductName(), dbMeta.getDatabaseProductVersion());
        } catch (SQLException e) {
            log.error("databaseMetaData access error", e);
            throw new RuntimeExceptionSerializable(e);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    TableModel tableModel(EntityMeta entityMeta) {
        return mappings.ensureTable(getPersistenceContext().getConnection(), connectionProvider.getDialect(), entityMeta);
    }

    public TableMetadata getTableMetadata(EntityMeta entityMeta) throws SQLException {
        startContext(ConnectionTarget.forRead);
        try {
            return TableMetadata
                    .getTableMetadata(getPersistenceContext().getConnection(), TableModel.getTableName(connectionProvider.getDialect(), entityMeta));
        } finally {
            endContext();
        }
    }

    @Override
    public void persist(IEntity entity) {
        startContext(ConnectionTarget.forUpdate);
        try {
            entity = entity.cast();
            persist(tableModel(entity.getEntityMeta()), entity);
        } finally {
            endContext();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void mergeReference(MemberMeta meta, IEntity entity) {
        ReferenceAdapter adapter;
        try {
            adapter = meta.getAnnotation(Reference.class).adapter().newInstance();
        } catch (InstantiationException e) {
            throw new Error(e);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
        TableModel tm = tableModel(EntityFactory.getEntityMeta(entity.getValueClass()));
        List<Key> rs = tm.queryKeys(getPersistenceContext(), adapter.getMergeCriteria(entity), 1);
        if (!rs.isEmpty()) {
            entity.setPrimaryKey(rs.get(0));
        } else {
            entity = adapter.onEntityCreation(entity);
            persist(tableModel(entity.getEntityMeta()), entity);
        }
    }

    // Initialize ownership relationship if any, e.g. call  SharedEntityHandler.ensureValue();
    private void ensureEntityValue(IEntity entity) {
        entity.setPrimaryKey(entity.getPrimaryKey());
    }

    private void persist(TableModel tm, IEntity entity) {
        if (entity.isValueDetached()) {
            throw new RuntimeException("Saving detached entity " + entity.getDebugExceptionInfoString());
        }
        ensureEntityValue(entity);
        for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            IEntity childEntity = (IEntity) member.getMember(entity);
            if (!childEntity.isNull() || member.isOwnedForceCreation()) {
                if (memberMeta.isOwnedRelationships()) {
                    if (!childEntity.isValueDetached()) {
                        childEntity = childEntity.cast();
                        persist(tableModel(childEntity.getEntityMeta()), childEntity);
                    }
                } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null)) {
                    mergeReference(memberMeta, childEntity);
                }
            }
        }
        MemberOperationsMeta updatedTs = tm.operationsMeta().getUpdatedTimestampMember();
        if (updatedTs != null) {
            updatedTs.setMemberValue(entity, getPersistenceContext().getTimeNow());
        }
        boolean isNewEntity = ((entity.getPrimaryKey() == null) || ((tm.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) && (!tm.exists(
                getPersistenceContext(), entity.getPrimaryKey()))));
        if (isNewEntity) {
            insert(tm, entity);
        } else {
            if (!update(tm, entity, false)) {
                throw new RuntimeException("Entity " + tm.entityMeta().getCaption() + " " + entity.getPrimaryKey() + " NotFound");
            }
        }
        CacheService.entityCache().put(entity);
    }

    private void insert(TableModel tm, IEntity entity) {
        if (trace) {
            log.info(Trace.enter() + "insert {}", tm.getTableName());
        }
        MemberOperationsMeta createdTs = tm.operationsMeta().getCreatedTimestampMember();
        if ((createdTs != null) && (createdTs.getMemberValue(entity) == null)) {
            createdTs.setMemberValue(entity, getPersistenceContext().getTimeNow());
        }
        try {
            tm.insert(getPersistenceContext(), entity);
            for (MemberCollectionOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                if (!member.getMemberMeta().isCascadePersist()) {
                    // Never update
                    continue;
                }
                TableModelCollections.validate(entity, member);
                if (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet) {
                    MemberMeta memberMeta = member.getMemberMeta();
                    @SuppressWarnings("unchecked")
                    ICollection<IEntity, ?> iCollectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
                    if (iCollectionMember.getAttachLevel() == AttachLevel.Detached) {
                        // Do not update Detached collections.
                        continue;
                    }
                    int orderInParent = 0;
                    for (IEntity childEntity : iCollectionMember) {
                        if (memberMeta.isOwnedRelationships()) {
                            if (childEntity.getPrimaryKey() != null) {
                                log.error("attempt to attach {} to different entity graphs of {}\n" + Trace.getCallOrigin(EntityPersistenceServiceRDB.class)
                                        + "\n", childEntity.getDebugExceptionInfoString(), entity.getDebugExceptionInfoString());
                                if (ApplicationMode.isDevelopment()) {
                                    throw new SecurityViolationException(ApplicationMode.DEV + "attempt to attach to different entity graphs "
                                            + childEntity.getDebugExceptionInfoString());
                                } else {
                                    throw new SecurityViolationException("Permission denied");
                                }
                            }
                            if (!childEntity.isValueDetached()) {
                                if (member.orderMemeberName() != null) {
                                    childEntity.setMemberValue(member.orderMemeberName(), Integer.valueOf(orderInParent));
                                }
                                persist(tableModel(childEntity.getEntityMeta()), childEntity);
                            }
                        } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
                            mergeReference(memberMeta, childEntity);
                        }
                        orderInParent++;
                    }
                }
                if (member.isAutogenerated()) {
                    TableModelCollections.insert(getPersistenceContext(), entity, member);
                }
            }
            for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembersSecondPass()) {
                IEntity childEntity = (IEntity) member.getMember(entity);
                if (!childEntity.isValueDetached() && ((!childEntity.isNull() || member.isOwnedForceCreation()))) {
                    childEntity = childEntity.cast();
                    persist(tableModel(childEntity.getEntityMeta()), childEntity);
                }
            }
            for (MemberOperationsMeta member : tm.operationsMeta().getVersionInfoMembers()) {
                for (IVersionData<IVersionedEntity<?>> memeberEntity : TableModleVersioned.update(getPersistenceContext(), mappings, entity, member)) {
                    merge(tableModel(memeberEntity.getEntityMeta()), memeberEntity);
                }
            }
        } finally {
            if (trace) {
                log.info(Trace.returns() + "insert {}", tm.getTableName());
            }
        }
    }

    private boolean update(TableModel tm, IEntity entity, boolean doMerge) {
        if (trace) {
            log.info(Trace.enter() + "update {} id={}", tm.getTableName(), entity.getPrimaryKey());
        }
        try {
            for (MemberCollectionOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                if (!member.getMemberMeta().isCascadePersist()) {
                    // Never update
                    continue;
                }
                TableModelCollections.validate(entity, member);
                if (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet) {
                    MemberMeta memberMeta = member.getMemberMeta();
                    @SuppressWarnings("unchecked")
                    ICollection<IEntity, ?> iCollectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
                    if (iCollectionMember.getAttachLevel() == AttachLevel.Detached) {
                        // Do not update Detached collections.
                        continue;
                    }
                    for (IEntity childEntity : iCollectionMember) {
                        if (memberMeta.isOwnedRelationships()) {
                            if (!childEntity.isValueDetached()) {
                                if (doMerge) {
                                    merge(tableModel(childEntity.getEntityMeta()), childEntity);
                                } else {
                                    persist(tableModel(childEntity.getEntityMeta()), childEntity);
                                }
                            }
                        } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
                            mergeReference(memberMeta, childEntity);
                        }
                    }
                }
            }
            boolean updated = tm.update(getPersistenceContext(), entity);
            List<IEntity> cascadeRemove = new Vector<IEntity>();
            if (updated) {
                for (MemberCollectionOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                    TableModelCollections.update(getPersistenceContext(), entity, member, cascadeRemove);
                }
            }
            for (IEntity ce : cascadeRemove) {
                cascadeDelete(ce.getEntityMeta(), ce.getPrimaryKey(), ce);
            }
            for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembersSecondPass()) {
                IEntity childEntity = (IEntity) member.getMember(entity);
                if (!childEntity.isValueDetached() && ((!childEntity.isNull() || member.isOwnedForceCreation()))) {
                    childEntity = childEntity.cast();
                    if (doMerge) {
                        merge(tableModel(childEntity.getEntityMeta()), childEntity);
                    } else {
                        persist(tableModel(childEntity.getEntityMeta()), childEntity);
                    }
                }
            }
            for (MemberOperationsMeta member : tm.operationsMeta().getVersionInfoMembers()) {
                for (IVersionData<IVersionedEntity<?>> memeberEntity : TableModleVersioned.update(getPersistenceContext(), mappings, entity, member)) {
                    merge(tableModel(memeberEntity.getEntityMeta()), memeberEntity);
                }
            }
            return updated;
        } finally {
            if (trace) {
                log.info(Trace.returns() + "update {} id={}", tm.getTableName(), entity.getPrimaryKey());
            }
        }
    }

    //TODO remove this function, see proper implementation below
    @Override
    public <T extends IEntity> void persist(Iterable<T> entityIterable) {
        startContext(ConnectionTarget.forUpdate);
        try {
            if (entityIterable.iterator().hasNext()) {
                for (T entity : entityIterable) {
                    persist(tableModel(entity.getEntityMeta()), entity);
                }
            }
        } finally {
            endContext();
        }
    }

    /**
     * This is untested method do not use unless you know what is inside this function!
     * 
     * @deprecated do not use unless told to do so!
     */
    @Deprecated
    public <T extends IEntity> void persistListOneLevel(Iterable<T> entityIterable, boolean returnId) {
        startContext(ConnectionTarget.forUpdate);
        try {
            if (entityIterable.iterator().hasNext()) {
                T entity = entityIterable.iterator().next();
                persist(tableModel(entity.getEntityMeta()), entityIterable, returnId);
            }
        } finally {
            endContext();
        }
    }

    //@Override
    //TODO Fix this to save collection
    public <T extends IEntity> void persist_TODO_FIX(Iterable<T> entityIterable) {
        startContext(ConnectionTarget.forUpdate);
        try {
            if (entityIterable.iterator().hasNext()) {
                T entity = entityIterable.iterator().next();
                persist(tableModel(entity.getEntityMeta()), entityIterable, true);
            }
        } finally {
            endContext();
        }
    }

    @Override
    public <T extends IEntity> void merge(Iterable<T> entityIterable) {
        startContext(ConnectionTarget.forUpdate);
        try {
            if (entityIterable.iterator().hasNext()) {
                for (T entity : entityIterable) {
                    merge(tableModel(entity.getEntityMeta()), entity);
                }
            }
        } finally {
            endContext();
        }
    }

    private <T extends IEntity> void persist(TableModel tm, Iterable<T> entityIterable, boolean returnId) {
        //TODO  //for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembers()) { ... }
        /*
         * for (MemberOperationsMeta member :
         * tm.operationsMeta().getCascadePersistMembers()) { MemberMeta memberMeta =
         * member.getMemberMeta(); for(T entity : entityIterable){ // this loop added by
         * me IEntity childEntity = (IEntity) member.getMember(entity); if
         * (memberMeta.isOwnedRelationships()) { persist(connection,
         * tableModel(childEntity.getEntityMeta()), childEntity, now); } else if
         * ((memberMeta.getAnnotation(Reference.class) != null) &&
         * (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
         * mergeReference(connection, memberMeta, childEntity, now); } } }
         */
        MemberOperationsMeta updatedTs = tm.operationsMeta().getUpdatedTimestampMember();
        if (updatedTs != null) {
            for (T entity : entityIterable) {
                if (updatedTs != null) {
                    updatedTs.setMemberValue(entity, getPersistenceContext().getTimeNow());
                }
            }
        }

        Vector<T> newEntities = new Vector<T>();
        Vector<T> updEntities = new Vector<T>();

        for (T e : entityIterable) {
            if (e.getPrimaryKey() != null) {
                updEntities.add(e);
            } else {
                newEntities.add(e);
            }
        }
        if (newEntities.size() > 0) {
            if (returnId) {
                tm.insert(getPersistenceContext(), newEntities);
            } else {
                tm.insertBulk(getPersistenceContext(), newEntities);
            }
        }

        Vector<T> notUpdated = new Vector<T>();
        if (updEntities.size() > 0) {
            tm.persist(getPersistenceContext(), updEntities, notUpdated);
            if (notUpdated.size() > 0) {
                for (T entity : notUpdated) {
                    //these entities have PKs assigned, that's how they selected to be updEntities.
                    if (tm.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) {
                        insert(tm, entity);
                    } else {
                        // in this case they can't be handled, throw
                        throw new RuntimeException("Entity " + tm.entityMeta().getCaption() + " " + entity.getPrimaryKey() + " NotFound");
                    }

                }
            }
        }

    }

    @Override
    public void merge(IEntity entity) {
        startContext(ConnectionTarget.forUpdate);
        try {
            entity = entity.cast();
            merge(tableModel(entity.getEntityMeta()), entity);
        } finally {
            endContext();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean applyModifications(TableModel tm, IEntity baseEntity, IEntity entity) {
        boolean updated = false;
        Class<? extends MemberModificationAdapter<?>>[] entityMembersModificationAdapters = null;
        Adapters adapters = entity.getEntityMeta().getAnnotation(Adapters.class);
        if (adapters != null) {
            entityMembersModificationAdapters = adapters.memberModificationAdapters();
        }
        for (MemberOperationsMeta member : tm.operationsMeta().getAllMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            Object value;
            Object lastValue;
            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                value = ((IEntity) member.getMember(entity)).getPrimaryKey();
                lastValue = ((IEntity) member.getMember(baseEntity)).getPrimaryKey();
                // TODO // merge incomplete data
            } else {
                value = member.getMemberValue(entity);
                lastValue = member.getMemberValue(baseEntity);
                // merge incomplete data
                if ((value == null) && (lastValue != null) && !member.containsMemberValue(entity)) {
                    member.setMemberValue(entity, lastValue);
                    continue;
                }
            }
            if (!EqualsHelper.equals(lastValue, value)) {
                updated = true;
                if (memberMeta.getAnnotation(ReadOnly.class) != null) {
                    log.error("Changing readonly property [{}] -> [{}]", lastValue, value);
                    throw new Error("Changing readonly property " + memberMeta.getCaption() + " of " + entity.getEntityMeta().getCaption());
                }
                MemberColumn memberColumn = memberMeta.getAnnotation(MemberColumn.class);
                if (memberColumn != null && memberColumn.modificationAdapters() != null) {
                    for (Class<? extends MemberModificationAdapter<?>> adapterClass : memberColumn.modificationAdapters()) {
                        @SuppressWarnings("rawtypes")
                        MemberModificationAdapter adapter = AdapterFactory.getMemberModificationAdapter(adapterClass);
                        if (!adapter.allowModifications(entity, memberMeta, lastValue, value)) {
                            log.error("Forbiden change [{}] -> [{}]", lastValue, value);
                            throw new Error("Forbiden change " + memberMeta.getCaption() + " of " + entity.getEntityMeta().getCaption());
                        }
                    }
                }
                if (entityMembersModificationAdapters != null) {
                    for (Class<? extends MemberModificationAdapter<?>> adapterClass : entityMembersModificationAdapters) {
                        @SuppressWarnings("rawtypes")
                        MemberModificationAdapter adapter = AdapterFactory.getMemberModificationAdapter(adapterClass);
                        if (!adapter.allowModifications(entity, memberMeta, lastValue, value)) {
                            log.error("Forbiden change [{}] -> [{}]", lastValue, value);
                            throw new Error("Forbiden change " + memberMeta.getCaption() + " of " + entity.getEntityMeta().getCaption());
                        }
                    }
                }
            } else if (memberMeta.isOwnedRelationships() && ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                // Special case for child collections update. Collection itself is the same and in the same order.
                ICollection<IEntity, ?> collectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
                Iterator<IEntity> iterator = collectionMember.iterator();
                ICollection<IEntity, ?> baseCollectionMember = (ICollection<IEntity, ?>) member.getMember(baseEntity);
                Iterator<IEntity> baseIterator = baseCollectionMember.iterator();
                for (; iterator.hasNext() && baseIterator.hasNext();) {
                    IEntity childEntity = iterator.next();
                    if (!childEntity.isValueDetached()) {
                        childEntity = childEntity.cast();
                        TableModel childTM = tableModel(EntityFactory.getEntityMeta(childEntity.getValueClass()));
                        IEntity childBaseEntity = baseIterator.next();
                        updated |= retrieveAndApplyModifications(childTM, childBaseEntity, childEntity);
                    }
                }
                updated = true;
            }
        }
        if (updated && (adapters != null) && adapters.entityModificationAdapters() != null) {
            for (Class<? extends EntityModificationAdapter<?>> adapterClass : adapters.entityModificationAdapters()) {
                @SuppressWarnings("rawtypes")
                EntityModificationAdapter adapter = AdapterFactory.getEntityModificationAdapters(adapterClass);
                adapter.onBeforeUpdate(baseEntity, entity);
            }
        }
        return updated;
    }

    private boolean retrieveAndApplyModifications(TableModel tm, IEntity baseEntity, IEntity entity) {
        if (!tm.retrieve(getPersistenceContext(), entity.getPrimaryKey(), baseEntity)) {
            if (tm.getPrimaryKeyStrategy() != Table.PrimaryKeyStrategy.ASSIGNED) {
                throw new RuntimeException("Entity " + tm.entityMeta().getCaption() + " " + entity.getPrimaryKey() + " NotFound");
            } else {
                return true;
            }
        }
        String updatedTs = tm.entityMeta().getUpdatedTimestampMember();
        if (!EqualsHelper.equals(entity.getMemberValue(updatedTs), baseEntity.getMemberValue(updatedTs))) {
            log.debug("Timestamp " + updatedTs + " change {} -> {}", baseEntity.getMemberValue(updatedTs), entity.getMemberValue(updatedTs));
            throw new ConcurrentUpdateException(i18n.tr("{0} Updated Externally", tm.entityMeta().getCaption()));
        }
        String createdTs = tm.entityMeta().getCreatedTimestampMember();
        if (createdTs != null) {
            if (!EqualsHelper.equals(entity.getMemberValue(createdTs), baseEntity.getMemberValue(createdTs))) {
                log.debug("Timestamp " + createdTs + " change {} -> {}", baseEntity.getMemberValue(createdTs), entity.getMemberValue(createdTs));
                throw new SecurityViolationException("Permission Denied");
            }
        }
        return applyModifications(tm, baseEntity, entity);
    }

    @SuppressWarnings("unchecked")
    private void fireModificationAdapters(TableModel tm, IEntity entity) {
        Class<? extends MemberModificationAdapter<?>>[] entityMembersModificationAdapters = null;
        Adapters adapters = entity.getEntityMeta().getAnnotation(Adapters.class);
        if (adapters != null) {
            entityMembersModificationAdapters = adapters.memberModificationAdapters();
        }
        for (MemberOperationsMeta member : tm.operationsMeta().getAllMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            Object value;
            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                value = ((IEntity) member.getMember(entity)).getPrimaryKey();
            } else {
                value = member.getMemberValue(entity);
            }
            MemberColumn memberColumn = memberMeta.getAnnotation(MemberColumn.class);
            if (memberColumn != null && memberColumn.modificationAdapters() != null) {
                for (Class<? extends MemberModificationAdapter<?>> adapterClass : memberColumn.modificationAdapters()) {
                    @SuppressWarnings("rawtypes")
                    MemberModificationAdapter adapter = AdapterFactory.getMemberModificationAdapter(adapterClass);
                    if (!adapter.allowModifications(entity, memberMeta, null, value)) {
                        log.error("Forbidden change -> [{}]", value);
                        throw new Error("Forbidden change '" + memberMeta.getCaption() + " of '" + entity.getEntityMeta().getCaption() + "'");
                    }
                }
            }
            if (entityMembersModificationAdapters != null) {
                for (Class<? extends MemberModificationAdapter<?>> adapterClass : entityMembersModificationAdapters) {
                    @SuppressWarnings("rawtypes")
                    MemberModificationAdapter adapter = AdapterFactory.getMemberModificationAdapter(adapterClass);
                    if (!adapter.allowModifications(entity, memberMeta, null, value)) {
                        log.error("Forbidden change -> [{}]", value);
                        throw new Error("Forbidden change '" + memberMeta.getCaption() + "' of '" + entity.getEntityMeta().getCaption() + "'");
                    }
                }
            }
            if (memberMeta.isOwnedRelationships() && ICollection.class.isAssignableFrom(memberMeta.getObjectClass())) {
                // Special case for child collections update. Collection itself is the same and in the same order.
                ICollection<IEntity, ?> collectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
                if (collectionMember.getAttachLevel() == AttachLevel.Detached) {
                    // Ignore Detached collections.
                    continue;
                }
                Iterator<IEntity> iterator = collectionMember.iterator();
                for (; iterator.hasNext();) {
                    IEntity childEntity = iterator.next();
                    if (!childEntity.isValueDetached()) {
                        childEntity = childEntity.cast();
                        TableModel childTM = tableModel(EntityFactory.getEntityMeta(childEntity.getValueClass()));
                        fireModificationAdapters(childTM, childEntity);
                    }
                }
            }
        }
    }

    private void merge(TableModel tm, IEntity entity) {
        if (entity.isValueDetached()) {
            throw new RuntimeException("Saving detached entity " + entity.getDebugExceptionInfoString());
        }
        final IEntity baseEntity = EntityFactory.create(tm.entityMeta().getEntityClass());

        boolean isNewEntity = ((entity.getPrimaryKey() == null) || ((tm.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) && (!tm.exists(
                getPersistenceContext(), entity.getPrimaryKey()))));
        ensureEntityValue(entity);
        boolean updated;
        if (!isNewEntity) {
            updated = retrieveAndApplyModifications(tm, baseEntity, entity);
        } else {
            fireModificationAdapters(tm, entity);
            updated = true;
        }
        for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembersSecondPass()) {
            // Relationship is managed in CHILD table using PARENT column.
            MemberMeta memberMeta = member.getMemberMeta();
            if (memberMeta.isOwnedRelationships()) {
                IEntity childEntity = (IEntity) member.getMember(entity);
                IEntity baseChildEntity = (IEntity) member.getMember(baseEntity);
                if (!EqualsHelper.equals(childEntity.getPrimaryKey(), baseChildEntity.getPrimaryKey())) {
                    if (childEntity.getPrimaryKey() != null) {
                        log.debug("corrupted entity {}", entity);
                        if (ApplicationMode.isDevelopment()) {
                            throw new SecurityViolationException(ApplicationMode.DEV + "owned entity should not be attached to different entity graph, "
                                    + childEntity.getDebugExceptionInfoString());
                        } else {
                            throw new SecurityViolationException("Permission denied");
                        }
                    } else if (baseChildEntity.getPrimaryKey() != null) {
                        // Cascade delete
                        cascadeDelete(baseChildEntity.getEntityMeta(), baseChildEntity.getPrimaryKey(), baseChildEntity);
                    }
                }
            }
        }
        List<IEntity> cascadeRemove = new Vector<IEntity>();
        for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            IEntity childEntity = (IEntity) member.getMember(entity);
            IEntity baseChildEntity = (IEntity) member.getMember(baseEntity);
            if (memberMeta.isOwnedRelationships()) {
                if (!EqualsHelper.equals(childEntity.getPrimaryKey(), baseChildEntity.getPrimaryKey())) {
                    if (childEntity.getPrimaryKey() != null) {
                        log.debug("corrupted entity {}", entity);
                        if (ApplicationMode.isDevelopment()) {
                            throw new SecurityViolationException(ApplicationMode.DEV + "owned entity should not be attached to different entity graph, "
                                    + childEntity.getDebugExceptionInfoString());
                        } else {
                            throw new SecurityViolationException("Permission denied");
                        }
                    } else if (baseChildEntity.getPrimaryKey() != null) {
                        // Cascade delete
                        cascadeRemove.add(baseChildEntity);
                    }
                }
                if (!childEntity.isValueDetached() && ((!childEntity.isNull() || member.isOwnedForceCreation()))) {
                    childEntity = childEntity.cast();
                    merge(tableModel(childEntity.getEntityMeta()), childEntity);
                }
            } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
                mergeReference(memberMeta, childEntity);
            }
        }
        if (updated) {
            MemberOperationsMeta updatedTs = tm.operationsMeta().getUpdatedTimestampMember();
            if (updatedTs != null) {
                updatedTs.setMemberValue(entity, getPersistenceContext().getTimeNow());
            }
            if (isNewEntity) {
                insert(tm, entity);
            } else {
                if (!update(tm, entity, true)) {
                    throw new RuntimeException("Entity '" + tm.entityMeta().getCaption() + "' " + entity.getPrimaryKey() + " NotFound");
                }
            }
            for (IEntity childEntity : cascadeRemove) {
                IEntity childEntityActual = childEntity.cast();
                cascadeDelete(childEntityActual.getEntityMeta(), childEntityActual.getPrimaryKey(), childEntityActual);
            }
            CacheService.entityCache().put(entity);
        }
    }

    private <T extends IEntity> void clearRetrieveValues(T entity) {
        if (true) {
            return;
        }
        // Clear all values, already in Entity, Retrieve from scratch
        Key pk = entity.getPrimaryKey();

        // Preserve special  VersionedEntity data
        Boolean draft = null;
        Date forDate = null;
        if (entity instanceof IVersionedEntity) {
            draft = ((IVersionedEntity<?>) entity).draft().getValue();
            forDate = ((IVersionedEntity<?>) entity).forDate().getValue();
        }

        entity.clearValues();
        entity.setPrimaryKey(pk);

        if (entity instanceof IVersionedEntity) {
            if (draft != null) {
                ((IVersionedEntity<?>) entity).draft().setValue(draft);
            }
            if (forDate != null) {
                ((IVersionedEntity<?>) entity).forDate().setValue(forDate);
            }
        }
    }

    @Override
    public <T extends IEntity> T retrieve(Class<T> entityClass, Key primaryKey) {
        return retrieve(entityClass, primaryKey, AttachLevel.Attached);
    }

    @Override
    public <T extends IEntity> T retrieve(Class<T> entityClass, Key primaryKey, AttachLevel attachLevel) {
        final T entity = EntityFactory.create(entityClass);
        entity.setPrimaryKey(primaryKey);
        if (retrieve(entity, attachLevel)) {
            return entity;
        } else {
            return null;
        }
    }

    @Override
    public <T extends IEntity> boolean retrieve(T entity) {
        return retrieve(entity, AttachLevel.Attached);
    }

    @Override
    public <T extends IEntity> boolean retrieve(T entity, AttachLevel attachLevel) {
        startContext(ConnectionTarget.forRead);
        try {
            entity = entity.cast();
            clearRetrieveValues(entity);
            return cascadeRetrieve(entity, attachLevel) != null;
        } finally {
            endContext();
        }
    }

    @Override
    public <T extends IEntity> void retrieveMember(T entityMember) {
        retrieveMember(entityMember, AttachLevel.Attached);
    }

    @Override
    public <T extends IEntity> void retrieveMember(T entityMember, AttachLevel attachLevel) {
        switch (entityMember.getAttachLevel()) {
        case Attached:
            throw new RuntimeException("Values of " + entityMember.getPath() + " already Attached");
        case IdOnly:
        case ToStringMembers:
            retrieve(entityMember, attachLevel);
            break;
        case Detached:
            assert (entityMember.getOwner().getPrimaryKey() != null);
            startContext(ConnectionTarget.forRead);
            try {
                TableModel tm = tableModel(entityMember.getOwner().getEntityMeta());
                tm.retrieveMember(getPersistenceContext(), entityMember.getOwner(), entityMember);
                if (cascadeRetrieve(entityMember, attachLevel) == null) {
                    throw new RuntimeException("Entity '" + entityMember.getEntityMeta().getCaption() + "' " + entityMember.getPrimaryKey() + " "
                            + entityMember.getPath() + " NotFound");
                }
            } finally {
                endContext();
            }
        }
    }

    @Override
    public <T extends IEntity> void retrieveMember(ICollection<T, ?> collectionMember) {
        retrieveMember(collectionMember, AttachLevel.Attached);
    }

    @Override
    public <T extends IEntity> void retrieveMember(ICollection<T, ?> collectionMember, AttachLevel attachLevel) {
        switch (collectionMember.getAttachLevel()) {
        case Attached:
            // There are no distinction in IdOnly/Attached  for now  
            //TODO throw new RuntimeException("Values of " + collectionMember.getPath() + " already Attached");
            retrieve(collectionMember);

        case IdOnly: // This is not implemented now.
        case ToStringMembers:
            retrieve(collectionMember, attachLevel);
            collectionMember.setAttachLevel(AttachLevel.Attached);
            break;

        case Detached:
            assert (collectionMember.getOwner().getPrimaryKey() != null);
            startContext(ConnectionTarget.forRead);
            try {
                TableModel tm = tableModel(collectionMember.getOwner().getEntityMeta());
                //TODO collectionMember.setAttachLevel(AttachLevel.IdOnly);
                collectionMember.setAttachLevel(AttachLevel.Attached);
                tm.retrieveMember(getPersistenceContext(), collectionMember.getOwner(), collectionMember);
                for (IEntity childEntity : collectionMember) {
                    if (cascadeRetrieve(childEntity, attachLevel) == null) {
                        throw new RuntimeException("Entity '" + childEntity.getEntityMeta().getCaption() + "' " + childEntity.getPrimaryKey() + " "
                                + childEntity.getPath() + " NotFound");
                    }
                }
            } finally {
                endContext();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends IEntity> T cascadeRetrieve(T entity, AttachLevel attachLevel) {
        if (entity.getPrimaryKey() == null) {
            return null;
        }
        T cachedEntity = (T) CacheService.entityCache().get(entity.getEntityMeta().getEntityClass(), entity.getPrimaryKey());
        if (cachedEntity != null) {
            entity.set(cachedEntity);
            return cachedEntity;
        }

        TableModel tm = tableModel(entity.getEntityMeta());
        if (tm.retrieve(getPersistenceContext(), entity.getPrimaryKey(), entity)) {
            entity = cascadeRetrieveMembers(tm, entity, attachLevel);
            entity.setAttachLevel(attachLevel);
            CacheService.entityCache().put(entity);
            return entity;
        } else {
            return null;
        }
    }

    private <T extends IEntity> T cascadeRetrieveMembers(TableModel tm, T entity, AttachLevel attachLevel) {
        for (MemberOperationsMeta member : tm.operationsMeta().getCascadeRetrieveMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            // Do not retrieve Owner, since already retrieved
            if ((entity.getOwner() != null) && (memberMeta.isOwner())) {
                continue;
            }
            if ((attachLevel == AttachLevel.ToStringMembers) && (!memberMeta.isToStringMember())) {
                continue;
            }
            if (memberMeta.isEntity()) {
                IEntity childEntity = ((IEntity) member.getMember(entity)).cast();
                if (childEntity.getPrimaryKey() != null) {
                    if (cascadeRetrieve(childEntity, member.getMemberMeta().getAttachLevel()) == null) {
                        throw new RuntimeException("Entity '" + memberMeta.getCaption() + "' [primary key =  " + childEntity.getPrimaryKey() + "; path = "
                                + childEntity.getPath() + "] is not found");
                    }
                    if (memberMeta.getAttachLevel() == AttachLevel.ToStringMembers) {
                        childEntity.setAttachLevel(AttachLevel.ToStringMembers);
                    }
                }
            } else {
                @SuppressWarnings("unchecked")
                ICollection<IEntity, ?> iCollectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
                for (IEntity childEntity : iCollectionMember) {
                    if (cascadeRetrieve(childEntity, member.getMemberMeta().getAttachLevel()) == null) {
                        throw new RuntimeException("Entity '" + childEntity.getEntityMeta().getCaption() + "' " + childEntity.getPrimaryKey() + " "
                                + childEntity.getPath() + " NotFound");
                    }
                    if (memberMeta.getAttachLevel() == AttachLevel.ToStringMembers) {
                        childEntity.setAttachLevel(AttachLevel.ToStringMembers);
                    }
                }
            }
        }
        for (MemberOperationsMeta member : tm.operationsMeta().getDetachedMembers()) {
            member.getMember(entity).setAttachLevel(AttachLevel.Detached);
        }
        return entity;
    }

    @Override
    public <T extends IEntity> T retrieve(EntityQueryCriteria<T> criteria) {
        return retrieve(criteria, AttachLevel.Attached);
    }

    @Override
    public <T extends IEntity> T retrieve(EntityQueryCriteria<T> criteria, AttachLevel attachLevel) {
        startContext(ConnectionTarget.forRead);
        try {
            TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
            List<T> rs = tm.query(getPersistenceContext(), criteria, 1);
            if (rs.isEmpty()) {
                return null;
            } else {
                return cascadeRetrieveMembers(tm, rs.get(0), attachLevel);
            }
        } finally {
            endContext();
        }
    }

    @Override
    public <T extends IEntity> void retrieve(Iterable<T> entityIterable) {
        //TODO proper impl
        for (T e : entityIterable) {
            retrieve(e, AttachLevel.Attached);
        }
    }

    @Override
    public <T extends IEntity> void retrieve(Iterable<T> entityIterable, AttachLevel attachLevel) {
        //TODO proper impl
        for (T e : entityIterable) {
            retrieve(e, attachLevel);
        }
    }

    @Override
    public <T extends IEntity> Map<Key, T> retrieve(Class<T> entityClass, Iterable<Key> primaryKeys) {
        startContext(ConnectionTarget.forRead);
        Map<Key, T> entities = new HashMap<Key, T>();
        TableModel tm = null;
        try {
            int count = 0;
            for (Key pk : primaryKeys) {
                final T entity = EntityFactory.create(entityClass);
                entity.setPrimaryKey(pk);
                if (count == 0) {
                    tm = tableModel(entity.getEntityMeta());
                }
                entities.put(pk, entity);
                count++;
            }
            tm.retrieve(getPersistenceContext(), entities);
            return entities;
        } finally {
            endContext();
        }
    }

    @Override
    public String getIndexedPropertyName(EntityMeta meta, Path path) {
        return getPropertyName(meta, path);
    }

    @Override
    public String getPropertyName(EntityMeta meta, Path path) {
        return path.toString();
//        StringBuilder propertyName = new StringBuilder();
//        final int pathLength = path.getPathMembers().size();
//        EntityMeta em = meta;
//        MemberMeta mm = null;
//        int count = 0;
//        for (String memberName : path.getPathMembers()) {
//            //TODO ICollection support
//            if (mm != null) {
//                Class<?> valueClass = mm.getValueClass();
//                if (!(IEntity.class.isAssignableFrom(valueClass))) {
//                    throw new RuntimeException("Invalid member in path " + memberName);
//                } else {
//                    em = EntityFactory.getEntityMeta((Class<? extends IEntity>) valueClass);
//                }
//            }
//            mm = em.getMemberMeta(memberName);
//            count++;
//            propertyName.append(memberName);
//            if (pathLength != count) {
//                if (!mm.isEmbedded()) {
//                    log.warn("Path {}; not implemented", path);
//                    throw new RuntimeException("Invalid member in path " + memberName);
//                }
//                propertyName.append("_");
//            }
//        }
//        return propertyName.toString();
    }

    @Override
    public <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria) {
        return query(criteria, AttachLevel.Attached);
    }

    @Override
    public <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria, AttachLevel attachLevel) {
        startContext(ConnectionTarget.forRead);
        try {
            TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
            List<T> l = tm.query(getPersistenceContext(), criteria, -1);
            for (T entity : l) {
                cascadeRetrieveMembers(tm, entity, attachLevel);
            }
            return l;
        } finally {
            endContext();
        }
    }

    @Override
    public <T extends IEntity> ICursorIterator<T> query(final String encodedCursorReference, EntityQueryCriteria<T> criteria, final AttachLevel attachLevel) {
        startContext(ConnectionTarget.forRead);
        final TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
        if (encodedCursorReference != null) {
            log.info("Received encodedCursorReference:" + encodedCursorReference + ", will use it");
            // TODO
        }
        try {
            final ResultSetIterator<T> iterable = tm.queryIterable(getPersistenceContext(), criteria);

            return new ICursorIterator<T>() {

                @Override
                public boolean hasNext() {
                    return iterable.hasNext();
                }

                @Override
                public T next() {
                    return cascadeRetrieveMembers(tm, iterable.next(), attachLevel);
                }

                @Override
                public void remove() {
                    iterable.remove();
                }

                @Override
                public String encodedCursorReference() {
                    // TODO proper encoded cursor reference has to be passed, this is just temporary
                    return "" + encodedCursorReference + "a";
                }

                @Override
                public void completeRetrieval() {
                    iterable.close();
                    endContext();
                }
            };

        } catch (Throwable e) {
            endContext();
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new Error(e);
            }
        }

    }

    @Override
    public <T extends IEntity> List<Key> queryKeys(EntityQueryCriteria<T> criteria) {
        startContext(ConnectionTarget.forRead);
        try {
            TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
            return tm.queryKeys(getPersistenceContext(), criteria, -1);
        } finally {
            endContext();
        }
    }

    @Override
    public <T extends IEntity> ICursorIterator<Key> queryKeys(final String encodedCursorReference, EntityQueryCriteria<T> criteria) {
        startContext(ConnectionTarget.forRead);
        final TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
        if (encodedCursorReference != null) {
            log.info("Received encodedCursorReference:" + encodedCursorReference + ", will use it");
            // TODO
        }
        try {
            final ResultSetIterator<Key> iterable = tm.queryKeysIterable(getPersistenceContext(), criteria);

            return new ICursorIterator<Key>() {

                @Override
                public boolean hasNext() {
                    return iterable.hasNext();
                }

                @Override
                public Key next() {
                    return iterable.next();
                }

                @Override
                public void remove() {
                    iterable.remove();
                }

                @Override
                public String encodedCursorReference() {
                    // TODO proper encoded cursor reference has to be passed, this is just temporary
                    return "" + encodedCursorReference + "a";
                }

                @Override
                public void completeRetrieval() {
                    iterable.close();
                    endContext();
                }
            };

        } catch (Throwable e) {
            endContext();
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            } else {
                throw new Error(e);
            }
        }
    }

    @Override
    public <T extends IEntity> boolean exists(Class<T> entityClass, Key primaryKey) {
        startContext(ConnectionTarget.forRead);
        try {
            TableModel tm = tableModel(EntityFactory.getEntityMeta(entityClass));
            return tm.exists(getPersistenceContext(), primaryKey);
        } finally {
            endContext();
        }
    }

    @Override
    public <T extends IEntity> boolean exists(EntityQueryCriteria<T> criteria) {
        ICursorIterator<Key> it = queryKeys(null, criteria);
        try {
            return it.hasNext();
        } finally {
            it.completeRetrieval();
        }
    }

    @Override
    public <T extends IEntity> int count(EntityQueryCriteria<T> criteria) {
        startContext(ConnectionTarget.forRead);
        try {
            TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
            Number count = (Number) tm.aggregate(getPersistenceContext(), criteria, SQLAggregateFunctions.COUNT, null);
            if (count == null) {
                return 0;
            } else {
                return count.intValue();
            }
        } finally {
            endContext();
        }
    }

    @Override
    public void delete(IEntity entity) {
        delete(entity.getEntityMeta(), entity.getPrimaryKey(), entity);
    }

    @Override
    public <T extends IEntity> void delete(Class<T> entityClass, Key primaryKey) {
        delete(EntityFactory.getEntityMeta(entityClass), primaryKey, null);
    }

    private <T extends IEntity> void delete(EntityMeta entityMeta, Key primaryKey, IEntity cascadedeleteDataEntity) {
        startContext(ConnectionTarget.forUpdate);
        try {
            cascadeDelete(entityMeta, primaryKey, cascadedeleteDataEntity);
        } finally {
            endContext();
        }
    }

    // cascadedeleteDataEntity is consistent with GAE implementation of delete(IEntity entity).
    private <T extends IEntity> void cascadeDelete(EntityMeta entityMeta, Key primaryKey, IEntity cascadedeleteDataEntity) {
        if (trace) {
            log.info(Trace.enter() + "cascadeDelete {} id={}", entityMeta.getPersistenceName(), primaryKey);
        }
        try {
            TableModel tm = tableModel(entityMeta);

            if (cascadedeleteDataEntity != null) {
                for (MemberOperationsMeta member : tm.operationsMeta().getCascadeDeleteMembers()) {
                    IEntity childEntity = (IEntity) member.getMember(cascadedeleteDataEntity);
                    if (childEntity.getPrimaryKey() != null) {
                        if (trace) {
                            log.info(Trace.id() + "cascadeDelete member {}", member.getMemberName());
                        }
                        cascadeDelete(childEntity.getEntityMeta(), childEntity.getPrimaryKey(), childEntity);
                    }
                }
            }

            for (MemberCollectionOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                if (member.getMemberMeta().isOwnedRelationships() || member.isAutogenerated()) {
                    // remove join table data
                    TableModelCollections.delete(getPersistenceContext(), primaryKey, member);

                    if ((cascadedeleteDataEntity != null) && member.getMemberMeta().isOwnedRelationships()
                            && (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet)) {
                        @SuppressWarnings("unchecked")
                        ICollection<IEntity, ?> collectionMember = (ICollection<IEntity, ?>) member.getMember(cascadedeleteDataEntity);
                        if (collectionMember.getAttachLevel() == AttachLevel.Detached) {
                            collectionMember.setAttachLevel(AttachLevel.Attached);
                            tm.retrieveMember(getPersistenceContext(), collectionMember.getOwner(), collectionMember);
                        }
                        for (IEntity childEntity : collectionMember) {
                            cascadeDelete(childEntity.getEntityMeta(), childEntity.getPrimaryKey(), childEntity);
                        }
                    }
                }
            }

            if (!tm.delete(getPersistenceContext(), primaryKey)) {
                throw new RuntimeException("Entity '" + entityMeta.getCaption() + "' " + primaryKey + " NotFound");
            }
        } finally {
            if (trace) {
                log.info(Trace.returns() + "cascadeDelete {} id={}", entityMeta.getPersistenceName(), primaryKey);
            }
        }
    }

    /**
     * This does cascade delete and removes data from Cache so it runs Retrieve first
     */
    @Override
    public <T extends IEntity> int delete(EntityQueryCriteria<T> criteria) {
        startContext(ConnectionTarget.forUpdate);
        try {
            TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));

            List<T> entities = tm.query(getPersistenceContext(), criteria, -1);

            int count = 0;
            if (entities.size() > 0) {
                List<Key> primaryKeys = new Vector<Key>();
                for (T entity : entities) {
                    primaryKeys.add(entity.getPrimaryKey());
                    // TODO optimize
                    cascadeRetrieveMembers(tm, entity, AttachLevel.Attached);
                }
                if (trace) {
                    log.info(Trace.enter() + "delete {} rows {}", tm.getTableName(), entities.size());
                }
                try {
                    // remove data from join tables first, No cascade delete
                    for (MemberCollectionOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                        if (member.getMemberMeta().isOwnedRelationships() || member.isAutogenerated()) {
                            //CollectionsTableModel.delete(connection, member, qb, tm.getTableName());
                            // TODO delete by Polymorphic Owner
                            if (member.getOwnerValueAdapter() instanceof ValueAdapterEntityPolymorphic) {
                                throw new Error("TODO delete by Polymorphic Owner");
                            }
                            TableModelCollections.delete(getPersistenceContext(), primaryKeys, member);

                            if (member.getMemberMeta().isOwnedRelationships() && (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet)) {
                                if (trace) {
                                    log.info(Trace.id() + "delete owned member [{}]", member.getMemberName());
                                }
                                // TODO optimize
                                for (T entity : entities) {
                                    @SuppressWarnings("unchecked")
                                    ICollection<IEntity, ?> collectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
                                    if (member.getMember(entity).getAttachLevel() == AttachLevel.Detached) {
                                        TableModel memberTableModel = tableModel(collectionMember.getOwner().getEntityMeta());
                                        //TODO collectionMember.setAttachLevel(AttachLevel.IdOnly);
                                        collectionMember.setAttachLevel(AttachLevel.Attached);
                                        memberTableModel.retrieveMember(getPersistenceContext(), collectionMember.getOwner(), collectionMember);
                                    }
                                    for (IEntity childEntity : collectionMember) {
                                        cascadeDelete(childEntity.getEntityMeta(), childEntity.getPrimaryKey(), childEntity);
                                    }
                                }
                            }
                        }
                    }

                    count = tm.delete(getPersistenceContext(), primaryKeys);
                    // TODO remove entities from Cache

                } finally {
                    if (trace) {
                        log.info(Trace.returns() + "delete {}", tm.getTableName());
                    }
                }
            }
            return count;
        } finally {
            endContext();
        }
    }

    @Override
    public <T extends IEntity> void delete(Class<T> entityClass, Iterable<Key> primaryKeys) {
        startContext(ConnectionTarget.forUpdate);
        try {
            EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
            TableModel tm = tableModel(entityMeta);
            for (MemberCollectionOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                if (member.getMemberMeta().isOwnedRelationships()) {
                    if (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet) {
                        @SuppressWarnings("unchecked")
                        Class<? extends IEntity> ownedEntityClass = (Class<IEntity>) member.getMemberMeta().getValueClass();
                        // TODO cascase delete
                    }
                    // TODO delete by Polymorphic Owner
                    if (member.getOwnerValueAdapter() instanceof ValueAdapterEntityPolymorphic) {
                        throw new Error("TODO delete by Polymorphic Owner");
                    }
                    TableModelCollections.delete(getPersistenceContext(), primaryKeys, member);
                }
            }
            tm.delete(getPersistenceContext(), primaryKeys);
        } finally {
            endContext();
        }
    }

    @Override
    public <T extends IEntity> void truncate(Class<T> entityClass) {
        startContext(ConnectionTarget.forUpdate);
        try {
            EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
            TableModel tm = tableModel(entityMeta);
            for (MemberOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                if (member.getMemberMeta().isOwnedRelationships()) {
                    if (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet) {
                        @SuppressWarnings("unchecked")
                        Class<? extends IEntity> ownedEntityClass = (Class<IEntity>) member.getMemberMeta().getValueClass();
                        // TODO use the same connection
                        truncate(ownedEntityClass);
                    }

                    TableModelCollections.truncate(getPersistenceContext(), member);
                }
            }
            tm.truncate(getPersistenceContext());
        } finally {
            endContext();
        }
    }

    @Override
    public void requestsAggregationStart() {

    }

    @Override
    public void requestsAggregationComplete() {

    }

    @Override
    public int getDatastoreCallCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getDatastoreWriteCallCount() {
        return 0;
    }
}
