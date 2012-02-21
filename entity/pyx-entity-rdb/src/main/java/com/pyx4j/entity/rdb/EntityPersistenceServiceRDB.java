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
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.dialect.SQLAggregateFunctions;
import com.pyx4j.entity.rdb.mapping.Mappings;
import com.pyx4j.entity.rdb.mapping.MemberCollectionOperationsMeta;
import com.pyx4j.entity.rdb.mapping.MemberOperationsMeta;
import com.pyx4j.entity.rdb.mapping.ResultSetIterator;
import com.pyx4j.entity.rdb.mapping.TableModel;
import com.pyx4j.entity.rdb.mapping.TableModelCollections;
import com.pyx4j.entity.server.AdapterFactory;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.IEntityPersistenceServiceExt;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.ConcurrentUpdateException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;
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
            mappings = new Mappings(connectionProvider);
            databaseVersion();
        }
    }

    public Connection getConnection() {
        return connectionProvider.getConnection(ConnectionTarget.forUpdate);
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

    //TODO implement thread locale
    void setTimeNow(Date date) {

    }

    public boolean isTableExists(Class<? extends IEntity> entityClass) {
        TableModel tm = new TableModel(connectionProvider.getDialect(), mappings, EntityFactory.getEntityMeta(entityClass));
        try {
            return tm.isTableExists(connectionProvider);
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

    private void databaseVersion() {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection(ConnectionTarget.forRead);
            DatabaseMetaData dbMeta = connection.getMetaData();
            log.debug("DB {} {}", dbMeta.getDatabaseProductName(), dbMeta.getDatabaseProductVersion());
        } catch (SQLException e) {
            log.error("databaseMetaData access error", e);
            throw new RuntimeExceptionSerializable(e);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    TableModel tableModel(Connection connection, EntityMeta entityMeta) {
        return mappings.ensureTable(connection, connectionProvider.getDialect(), entityMeta);
    }

    @Override
    public void persist(IEntity entity) {
        Connection connection = null;
        try {
            entity = entity.cast();
            connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
            persist(connection, tableModel(connection, entity.getEntityMeta()), entity, DateUtils.getRoundedNow());
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void mergeReference(Connection connection, MemberMeta meta, IEntity entity, Date now) {
        ReferenceAdapter adapter;
        try {
            adapter = meta.getAnnotation(Reference.class).adapter().newInstance();
        } catch (InstantiationException e) {
            throw new Error(e);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
        IEntity ent = retrieve(adapter.getMergeCriteria(entity));
        if (ent != null) {
            entity.setPrimaryKey(ent.getPrimaryKey());
        } else {
            entity = adapter.onEntityCreation(entity);
            persist(connection, tableModel(connection, entity.getEntityMeta()), entity, now);
        }
    }

    // Initialize ownership relationship if any, e.g. call  SharedEntityHandler.ensureValue();
    private void ensureEntityValue(IEntity entity) {
        entity.setPrimaryKey(entity.getPrimaryKey());
    }

    private void persist(Connection connection, TableModel tm, IEntity entity, Date now) {
        if (entity.isValueDetached()) {
            throw new RuntimeException("Saving detached entity " + entity.getDebugExceptionInfoString());
        }
        ensureEntityValue(entity);
        for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            IEntity childEntity = (IEntity) member.getMember(entity);
            if (!childEntity.isNull()) {
                if (memberMeta.isOwnedRelationships()) {
                    if (!childEntity.isValueDetached()) {
                        childEntity = childEntity.cast();
                        persist(connection, tableModel(connection, childEntity.getEntityMeta()), childEntity, now);
                    }
                } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null)) {
                    mergeReference(connection, memberMeta, childEntity, now);
                }
            }
        }
        MemberOperationsMeta updatedTs = tm.operationsMeta().getUpdatedTimestampMember();
        if (updatedTs != null) {
            updatedTs.setMemberValue(entity, now);
        }
        boolean isNewEntity = ((entity.getPrimaryKey() == null) || ((tm.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) && (!tm.exists(
                connection, entity.getPrimaryKey()))));
        if (isNewEntity) {
            insert(connection, tm, entity, now);
        } else {
            if (!update(connection, tm, entity, now, false)) {
                throw new RuntimeException("Entity " + tm.entityMeta().getCaption() + " " + entity.getPrimaryKey() + " NotFound");
            }
        }
        CacheService.entityCache().put(entity);
    }

    private void insert(Connection connection, TableModel tm, IEntity entity, Date now) {
        if (trace) {
            log.info(Trace.enter() + "insert {}", tm.getTableName());
        }
        MemberOperationsMeta createdTs = tm.operationsMeta().getCreatedTimestampMember();
        if ((createdTs != null) && (createdTs.getMemberValue(entity) == null)) {
            createdTs.setMemberValue(entity, now);
        }
        try {
            tm.insert(connection, entity);
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
                                persist(connection, tableModel(connection, childEntity.getEntityMeta()), childEntity, now);
                            }
                        } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
                            mergeReference(connection, memberMeta, childEntity, now);
                        }
                    }
                }
                //TODO TMP hack to make it work
                if (member.isAutogenerated()) {
                    TableModelCollections.insert(connection, connectionProvider.getDialect(), entity, member);
                }
            }
            for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembersSecondPass()) {
                IEntity childEntity = (IEntity) member.getMember(entity);
                if ((!childEntity.isNull()) && (!childEntity.isValueDetached())) {
                    childEntity = childEntity.cast();
                    persist(connection, tableModel(connection, childEntity.getEntityMeta()), childEntity, now);
                }
            }
        } finally {
            if (trace) {
                log.info(Trace.returns() + "insert {}", tm.getTableName());
            }
        }
    }

    private boolean update(Connection connection, TableModel tm, IEntity entity, Date now, boolean doMerge) {
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
                                    merge(connection, tableModel(connection, childEntity.getEntityMeta()), childEntity, now);
                                } else {
                                    persist(connection, tableModel(connection, childEntity.getEntityMeta()), childEntity, now);
                                }
                            }
                        } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
                            mergeReference(connection, memberMeta, childEntity, now);
                        }
                    }
                }
            }
            boolean updated = tm.update(connection, entity);
            List<IEntity> cascadeRemove = new Vector<IEntity>();
            if (updated) {
                for (MemberCollectionOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                    TableModelCollections.update(connection, connectionProvider.getDialect(), entity, member, cascadeRemove);
                }
            }
            for (IEntity ce : cascadeRemove) {
                cascadeDelete(connection, ce.getEntityMeta(), ce.getPrimaryKey(), ce);
            }
            for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembersSecondPass()) {
                IEntity childEntity = (IEntity) member.getMember(entity);
                if ((!childEntity.isNull()) && (!childEntity.isValueDetached())) {
                    childEntity = childEntity.cast();
                    if (doMerge) {
                        merge(connection, tableModel(connection, childEntity.getEntityMeta()), childEntity, now);
                    } else {
                        persist(connection, tableModel(connection, childEntity.getEntityMeta()), childEntity, now);
                    }
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
        Connection connection = null;
        try {
            if (entityIterable.iterator().hasNext()) {
                connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
                for (T entity : entityIterable) {
                    persist(connection, tableModel(connection, entity.getEntityMeta()), entity, DateUtils.getRoundedNow());
                }
            }
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    /**
     * This is untested method do not use unless you know what is inside this function!
     * 
     * @deprecated do not use unless told to do so!
     */
    @Deprecated
    public <T extends IEntity> void persistListOneLevel(Iterable<T> entityIterable, boolean returnId) {
        Connection connection = null;
        try {
            if (entityIterable.iterator().hasNext()) {
                connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
                T entity = entityIterable.iterator().next();
                persist(connection, tableModel(connection, entity.getEntityMeta()), entityIterable, DateUtils.getRoundedNow(), returnId);
            }
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    //@Override
    //TODO Fix this to save collection
    public <T extends IEntity> void persist_TODO_FIX(Iterable<T> entityIterable) {
        Connection connection = null;
        try {
            if (entityIterable.iterator().hasNext()) {
                connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
                T entity = entityIterable.iterator().next();
                persist(connection, tableModel(connection, entity.getEntityMeta()), entityIterable, DateUtils.getRoundedNow(), true);
            }
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> void merge(Iterable<T> entityIterable) {
        Connection connection = null;
        try {
            if (entityIterable.iterator().hasNext()) {
                connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
                for (T entity : entityIterable) {
                    merge(connection, tableModel(connection, entity.getEntityMeta()), entity, DateUtils.getRoundedNow());
                }
            }
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    private <T extends IEntity> void persist(Connection connection, TableModel tm, Iterable<T> entityIterable, Date now, boolean returnId) {
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
                    updatedTs.setMemberValue(entity, now);
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
                tm.insert(connection, newEntities);
            } else {
                tm.insertBulk(connection, newEntities);
            }
        }

        Vector<T> notUpdated = new Vector<T>();
        if (updEntities.size() > 0) {
            tm.persist(connection, updEntities, notUpdated);
            if (notUpdated.size() > 0) {
                for (T entity : notUpdated) {
                    //these entities have PKs assigned, that's how they selected to be updEntities.
                    if (tm.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) {
                        insert(connection, tm, entity, now);
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
        Connection connection = null;
        try {
            entity = entity.cast();
            connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
            merge(connection, tableModel(connection, entity.getEntityMeta()), entity, DateUtils.getRoundedNow());
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean applyModifications(Connection connection, TableModel tm, IEntity baseEntity, IEntity entity) {
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
                        TableModel childTM = tableModel(connection, EntityFactory.getEntityMeta(childEntity.getValueClass()));
                        IEntity childBaseEntity = baseIterator.next();
                        updated |= retrieveAndApplyModifications(connection, childTM, childBaseEntity, childEntity);
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

    private boolean retrieveAndApplyModifications(Connection connection, TableModel tm, IEntity baseEntity, IEntity entity) {
        if (!tm.retrieve(connection, entity.getPrimaryKey(), baseEntity)) {
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
        return applyModifications(connection, tm, baseEntity, entity);
    }

    @SuppressWarnings("unchecked")
    private void fireModificationAdapters(Connection connection, TableModel tm, IEntity entity) {
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
                Iterator<IEntity> iterator = collectionMember.iterator();
                for (; iterator.hasNext();) {
                    IEntity childEntity = iterator.next();
                    if (!childEntity.isValueDetached()) {
                        childEntity = childEntity.cast();
                        TableModel childTM = tableModel(connection, EntityFactory.getEntityMeta(childEntity.getValueClass()));
                        fireModificationAdapters(connection, childTM, childEntity);
                    }
                }
            }
        }
    }

    private void merge(Connection connection, TableModel tm, IEntity entity, Date now) {
        if (entity.isValueDetached()) {
            throw new RuntimeException("Saving detached entity " + entity.getDebugExceptionInfoString());
        }
        final IEntity baseEntity = EntityFactory.create(tm.entityMeta().getEntityClass());

        boolean isNewEntity = ((entity.getPrimaryKey() == null) || ((tm.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) && (!tm.exists(
                connection, entity.getPrimaryKey()))));
        ensureEntityValue(entity);
        boolean updated;
        if (!isNewEntity) {
            updated = retrieveAndApplyModifications(connection, tm, baseEntity, entity);
        } else {
            fireModificationAdapters(connection, tm, entity);
            updated = true;
        }
        for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            IEntity childEntity = (IEntity) member.getMember(entity);
            IEntity baseChildEntity = (IEntity) member.getMember(baseEntity);
            if (memberMeta.isOwnedRelationships()) {
                if (!EqualsHelper.equals(childEntity.getPrimaryKey(), baseChildEntity.getPrimaryKey())) {
                    if (childEntity.getPrimaryKey() != null) {
                        if (ApplicationMode.isDevelopment()) {
                            throw new SecurityViolationException(ApplicationMode.DEV + "owned entity should not be attached to different entity graph, "
                                    + childEntity.getDebugExceptionInfoString());
                        } else {
                            throw new SecurityViolationException("Permission denied");
                        }
                    } else if (baseChildEntity.getPrimaryKey() != null) {
                        // Cascade delete
                        cascadeDelete(connection, baseChildEntity.getEntityMeta(), baseChildEntity.getPrimaryKey(), baseChildEntity);
                    }
                }
                if (!childEntity.isValueDetached() && (!childEntity.isNull())) {
                    childEntity = childEntity.cast();
                    merge(connection, tableModel(connection, childEntity.getEntityMeta()), childEntity, now);
                }
            } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
                mergeReference(connection, memberMeta, childEntity, now);
            }
        }
        if (updated) {
            MemberOperationsMeta updatedTs = tm.operationsMeta().getUpdatedTimestampMember();
            if (updatedTs != null) {
                updatedTs.setMemberValue(entity, now);
            }
            if (isNewEntity) {
                insert(connection, tm, entity, now);
            } else {
                if (!update(connection, tm, entity, now, true)) {
                    throw new RuntimeException("Entity '" + tm.entityMeta().getCaption() + "' " + entity.getPrimaryKey() + " NotFound");
                }
            }
            CacheService.entityCache().put(entity);
        }
    }

    @Override
    public <T extends IEntity> T retrieve(Class<T> entityClass, Key primaryKey) {
        final T entity = EntityFactory.create(entityClass);
        entity.setPrimaryKey(primaryKey);
        if (retrieve(entity)) {
            return entity;
        } else {
            return null;
        }
    }

    @Override
    public <T extends IEntity> boolean retrieve(T entity) {
        Connection connection = null;
        try {
            entity = entity.cast();
            connection = connectionProvider.getConnection(ConnectionTarget.forRead);
            return cascadeRetrieve(connection, entity) != null;
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> void retrieveMember(T entityMember) {
        switch (entityMember.getAttachLevel()) {
        case Attached:
            throw new RuntimeException("Values of " + entityMember.getPath() + " already Attached");
        case IdOnly:
        case ToStringMembers:
            retrieve(entityMember);
            break;
        case Detached:
            assert (entityMember.getOwner().getPrimaryKey() != null);
            Connection connection = null;
            try {
                connection = connectionProvider.getConnection(ConnectionTarget.forRead);
                TableModel tm = tableModel(connection, entityMember.getOwner().getEntityMeta());
                tm.retrieveMember(connection, entityMember.getOwner(), entityMember);
                if (cascadeRetrieve(connection, entityMember) == null) {
                    throw new RuntimeException("Entity '" + entityMember.getEntityMeta().getCaption() + "' " + entityMember.getPrimaryKey() + " "
                            + entityMember.getPath() + " NotFound");
                }
            } finally {
                SQLUtils.closeQuietly(connection);
            }
        }
    }

    @Override
    public <T extends IEntity> void retrieveMember(ICollection<T, ?> collectionMember) {
        switch (collectionMember.getAttachLevel()) {
        case Attached:
            // There are no distinction in IdOnly/Attached  for now  
            //TODO throw new RuntimeException("Values of " + collectionMember.getPath() + " already Attached");
            retrieve(collectionMember);

        case IdOnly: // This is not implemented now.
        case ToStringMembers:
            retrieve(collectionMember);
            collectionMember.setAttachLevel(AttachLevel.Attached);
            break;

        case Detached:
            assert (collectionMember.getOwner().getPrimaryKey() != null);
            Connection connection = null;
            try {
                connection = connectionProvider.getConnection(ConnectionTarget.forRead);
                TableModel tm = tableModel(connection, collectionMember.getOwner().getEntityMeta());
                //TODO collectionMember.setAttachLevel(AttachLevel.IdOnly);
                collectionMember.setAttachLevel(AttachLevel.Attached);
                tm.retrieveMember(connection, collectionMember.getOwner(), collectionMember);
                for (IEntity childEntity : collectionMember) {
                    if (cascadeRetrieve(connection, childEntity) == null) {
                        throw new RuntimeException("Entity '" + childEntity.getEntityMeta().getCaption() + "' " + childEntity.getPrimaryKey() + " "
                                + childEntity.getPath() + " NotFound");
                    }
                }
                collectionMember.setAttachLevel(AttachLevel.Attached);
            } finally {
                SQLUtils.closeQuietly(connection);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends IEntity> T cascadeRetrieve(Connection connection, T entity) {
        if (entity.getPrimaryKey() == null) {
            return null;
        }
        T cachedEntity = (T) CacheService.entityCache().get(entity.getEntityMeta().getEntityClass(), entity.getPrimaryKey());
        if (cachedEntity != null) {
            entity.set(cachedEntity);
            return cachedEntity;
        }

        TableModel tm = tableModel(connection, entity.getEntityMeta());
        if (tm.retrieve(connection, entity.getPrimaryKey(), entity)) {
            entity = cascadeRetrieveMembers(connection, tm, entity);
            CacheService.entityCache().put(entity);
            return entity;
        } else {
            return null;
        }
    }

    private <T extends IEntity> T cascadeRetrieveMembers(Connection connection, TableModel tm, T entity) {
        for (MemberOperationsMeta member : tm.operationsMeta().getCascadeRetrieveMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            // Do not retrieve Owner, since already retrieved
            if ((entity.getOwner() != null) && (memberMeta.isOwner())) {
                continue;
            }
            if (memberMeta.isEntity()) {
                IEntity childEntity = ((IEntity) member.getMember(entity)).cast();
                if (childEntity.getPrimaryKey() != null) {
                    if (cascadeRetrieve(connection, childEntity) == null) {
                        throw new RuntimeException("Entity '" + memberMeta.getCaption() + "' [primary key =  " + childEntity.getPrimaryKey() + "; path = "
                                + childEntity.getPath() + "] is not found");
                    }
                }
            } else {
                @SuppressWarnings("unchecked")
                ICollection<IEntity, ?> iCollectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
                for (IEntity childEntity : iCollectionMember) {
                    if (cascadeRetrieve(connection, childEntity) == null) {
                        throw new RuntimeException("Entity '" + childEntity.getEntityMeta().getCaption() + "' " + childEntity.getPrimaryKey() + " "
                                + childEntity.getPath() + " NotFound");
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
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection(ConnectionTarget.forRead);
            TableModel tm = tableModel(connection, EntityFactory.getEntityMeta(criteria.getEntityClass()));
            List<T> rs = tm.query(connection, criteria, 1);
            if (rs.isEmpty()) {
                return null;
            } else {
                return cascadeRetrieveMembers(connection, tm, rs.get(0));
            }
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> void retrieve(Iterable<T> entityIterable) {
        //TODO proper impl
        for (T e : entityIterable) {
            retrieve(e);
        }
    }

    @Override
    public <T extends IEntity> Map<Key, T> retrieve(Class<T> entityClass, Iterable<Key> primaryKeys) {
        Connection connection = null;
        Map<Key, T> entities = new HashMap<Key, T>();
        TableModel tm = null;
        try {
            connection = connectionProvider.getConnection(ConnectionTarget.forRead);
            int count = 0;
            for (Key pk : primaryKeys) {
                final T entity = EntityFactory.create(entityClass);
                entity.setPrimaryKey(pk);
                if (count == 0) {
                    tm = tableModel(connection, entity.getEntityMeta());
                }
                entities.put(pk, entity);
                count++;
            }
            tm.retrieve(connection, entities);
            return entities;
        } finally {
            SQLUtils.closeQuietly(connection);
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
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection(ConnectionTarget.forRead);
            TableModel tm = tableModel(connection, EntityFactory.getEntityMeta(criteria.getEntityClass()));
            List<T> l = tm.query(connection, criteria, -1);
            for (T entity : l) {
                cascadeRetrieveMembers(connection, tm, entity);
            }
            return l;
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> ICursorIterator<T> query(final String encodedCursorRefference, EntityQueryCriteria<T> criteria) {
        final Connection connection = connectionProvider.getConnection(ConnectionTarget.forRead);
        final TableModel tm = tableModel(connection, EntityFactory.getEntityMeta(criteria.getEntityClass()));
        if (encodedCursorRefference != null) {
            log.info("Received encodedCursorReference:" + encodedCursorRefference + ", will use it");
            // TODO
        }
        try {
            final ResultSetIterator<T> iterable = tm.queryIterable(connection, criteria);

            return new ICursorIterator<T>() {

                @Override
                public boolean hasNext() {
                    return iterable.hasNext();
                }

                @Override
                public T next() {
                    return cascadeRetrieveMembers(connection, tm, iterable.next());
                }

                @Override
                public void remove() {
                    iterable.remove();
                }

                @Override
                public String encodedCursorReference() {
                    // TODO proper encoded cursor reference has to be passed, this is just temporary
                    return "" + encodedCursorRefference + "a";
                }

                @Override
                public void completeRetrieval() {
                    iterable.close();
                    SQLUtils.closeQuietly(connection);
                }
            };

        } catch (Throwable e) {
            SQLUtils.closeQuietly(connection);
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
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection(ConnectionTarget.forRead);
            TableModel tm = tableModel(connection, EntityFactory.getEntityMeta(criteria.getEntityClass()));
            return tm.queryKeys(connection, criteria, -1);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> ICursorIterator<Key> queryKeys(final String encodedCursorRefference, EntityQueryCriteria<T> criteria) {
        final Connection connection = connectionProvider.getConnection(ConnectionTarget.forRead);
        final TableModel tm = tableModel(connection, EntityFactory.getEntityMeta(criteria.getEntityClass()));
        if (encodedCursorRefference != null) {
            log.info("Received encodedCursorReference:" + encodedCursorRefference + ", will use it");
            // TODO
        }
        try {
            final ResultSetIterator<Key> iterable = tm.queryKeysIterable(connection, criteria);

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
                    return "" + encodedCursorRefference + "a";
                }

                @Override
                public void completeRetrieval() {
                    iterable.close();
                    SQLUtils.closeQuietly(connection);
                }
            };

        } catch (Throwable e) {
            SQLUtils.closeQuietly(connection);
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
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection(ConnectionTarget.forRead);
            TableModel tm = tableModel(connection, EntityFactory.getEntityMeta(entityClass));
            return tm.exists(connection, primaryKey);
        } finally {
            SQLUtils.closeQuietly(connection);
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
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection(ConnectionTarget.forRead);
            TableModel tm = tableModel(connection, EntityFactory.getEntityMeta(criteria.getEntityClass()));
            Number count = (Number) tm.aggregate(connection, criteria, SQLAggregateFunctions.COUNT, null);
            if (count == null) {
                return 0;
            } else {
                return count.intValue();
            }
        } finally {
            SQLUtils.closeQuietly(connection);
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
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
            cascadeDelete(connection, entityMeta, primaryKey, cascadedeleteDataEntity);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    // cascadedeleteDataEntity is consistent with GAE implementation of delete(IEntity entity).
    private <T extends IEntity> void cascadeDelete(Connection connection, EntityMeta entityMeta, Key primaryKey, IEntity cascadedeleteDataEntity) {
        if (trace) {
            log.info(Trace.enter() + "cascadeDelete {} id={}", entityMeta.getPersistenceName(), primaryKey);
        }
        try {
            TableModel tm = tableModel(connection, entityMeta);

            if (cascadedeleteDataEntity != null) {
                for (MemberOperationsMeta member : tm.operationsMeta().getCascadeDeleteMembers()) {
                    IEntity childEntity = (IEntity) member.getMember(cascadedeleteDataEntity);
                    if (childEntity.getPrimaryKey() != null) {
                        if (trace) {
                            log.info(Trace.id() + "cascadeDelete member {}", member.getMemberName());
                        }
                        cascadeDelete(connection, childEntity.getEntityMeta(), childEntity.getPrimaryKey(), childEntity);
                    }
                }
            }

            for (MemberCollectionOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                if (member.getMemberMeta().isOwnedRelationships()) {
                    // remove join table data
                    TableModelCollections.delete(connection, connectionProvider.getDialect(), primaryKey, member);

                    if ((cascadedeleteDataEntity != null) && member.getMemberMeta().isOwnedRelationships()
                            && (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet)) {
                        for (IEntity childEntity : (ICollection<IEntity, ?>) member.getMember(cascadedeleteDataEntity)) {
                            cascadeDelete(connection, childEntity.getEntityMeta(), childEntity.getPrimaryKey(), childEntity);
                        }
                    }
                }
            }

            if (!tm.delete(connection, primaryKey)) {
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
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
            TableModel tm = tableModel(connection, EntityFactory.getEntityMeta(criteria.getEntityClass()));

            List<T> entities = tm.query(connection, criteria, -1);

            int count = 0;
            if (entities.size() > 0) {
                List<Key> primaryKeys = new Vector<Key>();
                for (T entity : entities) {
                    primaryKeys.add(entity.getPrimaryKey());
                    // TODO optimize
                    cascadeRetrieveMembers(connection, tm, entity);
                }
                if (trace) {
                    log.info(Trace.enter() + "delete {} rows {}", tm.getTableName(), entities.size());
                }
                try {
                    // remove data from join tables first, No cascade delete
                    for (MemberCollectionOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                        if (member.getMemberMeta().isOwnedRelationships()) {
                            //CollectionsTableModel.delete(connection, member, qb, tm.getTableName());
                            TableModelCollections.delete(connection, connectionProvider.getDialect(), primaryKeys, member);

                            if (member.getMemberMeta().isOwnedRelationships() && (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet)) {
                                if (trace) {
                                    log.info(Trace.id() + "delete owned member [{}]", member.getMemberName());
                                }
                                // TODO optimize
                                for (T entity : entities) {
                                    @SuppressWarnings("unchecked")
                                    ICollection<IEntity, ?> collectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
                                    if (member.getMember(entity).getAttachLevel() == AttachLevel.Detached) {
                                        TableModel memberTableModel = tableModel(connection, collectionMember.getOwner().getEntityMeta());
                                        //TODO collectionMember.setAttachLevel(AttachLevel.IdOnly);
                                        collectionMember.setAttachLevel(AttachLevel.Attached);
                                        memberTableModel.retrieveMember(connection, collectionMember.getOwner(), collectionMember);
                                    }
                                    for (IEntity childEntity : collectionMember) {
                                        cascadeDelete(connection, childEntity.getEntityMeta(), childEntity.getPrimaryKey(), childEntity);
                                    }
                                }
                            }
                        }
                    }

                    count = tm.delete(connection, primaryKeys);
                    // TODO remove entities from Cache

                } finally {
                    if (trace) {
                        log.info(Trace.returns() + "delete {}", tm.getTableName());
                    }
                }
            }
            return count;
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> void delete(Class<T> entityClass, Iterable<Key> primaryKeys) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
            EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
            TableModel tm = tableModel(connection, entityMeta);
            for (MemberCollectionOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                if (member.getMemberMeta().isOwnedRelationships()) {
                    if (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet) {
                        @SuppressWarnings("unchecked")
                        Class<? extends IEntity> ownedEntityClass = (Class<IEntity>) member.getMemberMeta().getValueClass();
                        // TODO cascase delete
                    }
                    TableModelCollections.delete(connection, connectionProvider.getDialect(), primaryKeys, member);
                }
            }
            tm.delete(connection, primaryKeys);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> void truncate(Class<T> entityClass) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection(ConnectionTarget.forUpdate);
            EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
            TableModel tm = tableModel(connection, entityMeta);
            for (MemberOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                if (member.getMemberMeta().isOwnedRelationships()) {
                    if (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet) {
                        @SuppressWarnings("unchecked")
                        Class<? extends IEntity> ownedEntityClass = (Class<IEntity>) member.getMemberMeta().getValueClass();
                        // TODO use the same connection
                        truncate(ownedEntityClass);
                    }

                    TableModelCollections.truncate(connection, member);
                }
            }
            tm.truncate(connection);
        } finally {
            SQLUtils.closeQuietly(connection);
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
