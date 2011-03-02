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
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.adapters.MemberModificationAdapter;
import com.pyx4j.entity.adapters.ReferenceAdapter;
import com.pyx4j.entity.annotations.Adapters;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.dialect.SQLAggregateFunctions;
import com.pyx4j.entity.rdb.mapping.CollectionsTableModel;
import com.pyx4j.entity.rdb.mapping.Mappings;
import com.pyx4j.entity.rdb.mapping.MemberOperationsMeta;
import com.pyx4j.entity.rdb.mapping.ResultSetIterator;
import com.pyx4j.entity.rdb.mapping.TableModel;
import com.pyx4j.entity.server.AdapterFactory;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.IEntityPersistenceServiceExt;
import com.pyx4j.entity.shared.ConcurrentUpdateException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.i18n.shared.I18nFactory;

public class EntityPersistenceServiceRDB implements IEntityPersistenceService, IEntityPersistenceServiceExt {

    private static final Logger log = LoggerFactory.getLogger(EntityPersistenceServiceRDB.class);

    private static I18n i18n = I18nFactory.getI18n();

    private final ConnectionProvider connectionProvider;

    private final Mappings mappings;

    public EntityPersistenceServiceRDB() {
        try {
            IPersistenceConfiguration cfg = ServerSideConfiguration.instance().getPersistenceConfiguration();
            if (cfg == null) {
                throw new RuntimeException("Persistence Configuration is not defined (is null)");
            }
            if (!(cfg instanceof Configuration)) {
                throw new RuntimeException("Invalid RDB configuration class " + cfg);
            }
            connectionProvider = new ConnectionProvider((Configuration) cfg);
        } catch (SQLException e) {
            log.error("RDB initialization error", e);
            throw new RuntimeException(e.getMessage());
        }
        mappings = new Mappings(connectionProvider);
    }

    @Override
    public void dispose() {
        connectionProvider.dispose();
    }

    @Override
    public void deregister() {
        connectionProvider.deregister();
    }

    public void dropTable(Class<? extends IEntity> entityClass) {
        TableModel tm = new TableModel(connectionProvider.getDialect(), EntityFactory.getEntityMeta(entityClass));
        try {
            tm.dropTable(connectionProvider);
        } catch (SQLException e) {
            log.error("drop table error", e);
            throw new RuntimeExceptionSerializable(e);
        }
    }

    private TableModel tableModel(EntityMeta entityMeta) {
        return mappings.ensureTable(connectionProvider.getDialect(), entityMeta);
    }

    @Override
    public void persist(IEntity entity) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            persist(connection, tableModel(entity.getEntityMeta()), entity, new Date());
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
            persist(connection, tableModel(entity.getEntityMeta()), entity, now);
        }
    }

    private void persist(Connection connection, TableModel tm, IEntity entity, Date now) {
        for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            IEntity childEntity = (IEntity) member.getMember(entity);
            if (memberMeta.isOwnedRelationships()) {
                persist(connection, tableModel(childEntity.getEntityMeta()), childEntity, now);
            } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
                mergeReference(connection, memberMeta, childEntity, now);
            }
        }
        String updatedTs = tm.entityMeta().getUpdatedTimestampMember();
        if (updatedTs != null) {
            entity.setMemberValue(updatedTs, now);
        }
        if (entity.getPrimaryKey() == null) {
            insert(connection, tm, entity, now);
        } else {
            if (!update(connection, tm, entity, now, false)) {
                if (tm.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) {
                    insert(connection, tm, entity, now);
                } else {
                    throw new RuntimeException("Entity " + tm.entityMeta().getCaption() + " " + entity.getPrimaryKey() + " NotFound");
                }
            }
        }
    }

    private void insert(Connection connection, TableModel tm, IEntity entity, Date now) {
        tm.insert(connection, entity);
        for (MemberOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
            CollectionsTableModel.validate(entity, member);
            if (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet) {
                MemberMeta memberMeta = member.getMemberMeta();
                @SuppressWarnings("unchecked")
                ICollection<IEntity, ?> iCollectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
                for (IEntity childEntity : iCollectionMember) {
                    if (memberMeta.isOwnedRelationships()) {
                        persist(connection, tableModel(childEntity.getEntityMeta()), childEntity, now);
                    } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
                        mergeReference(connection, memberMeta, childEntity, now);
                    }
                }
            }
            CollectionsTableModel.insert(connection, connectionProvider.getDialect(), entity, member);
        }
    }

    private boolean update(Connection connection, TableModel tm, IEntity entity, Date now, boolean doMerge) {
        for (MemberOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
            CollectionsTableModel.validate(entity, member);
            if (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet) {
                MemberMeta memberMeta = member.getMemberMeta();
                @SuppressWarnings("unchecked")
                ICollection<IEntity, ?> iCollectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
                for (IEntity childEntity : iCollectionMember) {
                    if (memberMeta.isOwnedRelationships()) {
                        if (doMerge) {
                            merge(connection, tableModel(childEntity.getEntityMeta()), childEntity, now);
                        } else {
                            persist(connection, tableModel(childEntity.getEntityMeta()), childEntity, now);
                        }
                    } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
                        mergeReference(connection, memberMeta, childEntity, now);
                    }
                }
            }
        }
        return tm.update(connection, entity);
    }

    @Override
    public <T extends IEntity> void persist(Iterable<T> entityIterable) {

        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            java.util.Iterator<T> it = entityIterable.iterator();

            T entity = entityIterable.iterator().next();
            persist(connection, tableModel(entity.getEntityMeta()), entityIterable, new Date());
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    private <T extends IEntity> void persist(Connection connection, TableModel tm, Iterable<T> entityIterable, Date now) {
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
        String updatedTs = tm.entityMeta().getUpdatedTimestampMember();
        for (T entity : entityIterable) {
            if (updatedTs != null) {
                entity.setMemberValue(updatedTs, now);
            }
        }

        Vector<T> newEntities = new Vector<T>();
        Vector<T> updEntities = new Vector<T>();

        for (T e : entityIterable) {
            if (e.getPrimaryKey() != null)
                updEntities.add(e);
            else
                newEntities.add(e);
        }
        if (newEntities.size() > 0) {
            tm.insert(connection, newEntities);
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
            connection = connectionProvider.getConnection();
            merge(connection, tableModel(entity.getEntityMeta()), entity, new Date());
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean applyModifications(Connection connection, TableModel tm, IEntity baseEntity, IEntity entity) {
        boolean updated = false;
        Class<? extends MemberModificationAdapter<?>>[] entityMemebersModificationAdapters = null;
        Adapters adapters = entity.getEntityMeta().getAnnotation(Adapters.class);
        if (adapters != null) {
            entityMemebersModificationAdapters = adapters.modificationAdapters();
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
                if (entityMemebersModificationAdapters != null) {
                    for (Class<? extends MemberModificationAdapter<?>> adapterClass : entityMemebersModificationAdapters) {
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
                TableModel childTM = tableModel(EntityFactory.getEntityMeta((Class<IEntity>) memberMeta.getValueClass()));
                for (; iterator.hasNext() && baseIterator.hasNext();) {
                    IEntity childEntity = iterator.next();
                    IEntity childBaseEntity = baseIterator.next();
                    updated |= retrieveAndApplyModifications(connection, childTM, childBaseEntity, childEntity);
                }
                updated = true;
            }
        }
        return updated;
    }

    private boolean retrieveAndApplyModifications(Connection connection, TableModel tm, IEntity baseEntity, IEntity entity) {
        if (!tm.retrieve(connection, entity.getPrimaryKey(), baseEntity)) {
            throw new RuntimeException("Entity " + tm.entityMeta().getCaption() + " " + entity.getPrimaryKey() + " NotFound");
        }
        String updatedTs = tm.entityMeta().getUpdatedTimestampMember();
        if (!EqualsHelper.equals(entity.getMemberValue(updatedTs), baseEntity.getMemberValue(updatedTs))) {
            log.debug("Timestamp " + updatedTs + " change {} -> {}", baseEntity.getMemberValue(updatedTs), entity.getMemberValue(updatedTs));
            throw new ConcurrentUpdateException(i18n.tr("{0} updated externally", tm.entityMeta().getCaption()));
        }
        return applyModifications(connection, tm, baseEntity, entity);
    }

    private void merge(Connection connection, TableModel tm, IEntity entity, Date now) {
        final IEntity baseEntity = EntityFactory.create(tm.entityMeta().getEntityClass());
        String updatedTs = tm.entityMeta().getUpdatedTimestampMember();
        boolean updated;
        if (entity.getPrimaryKey() != null) {
            updated = retrieveAndApplyModifications(connection, tm, baseEntity, entity);
        } else {
            updated = true;
        }
        for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            IEntity childEntity = (IEntity) member.getMember(entity);
            IEntity baseChildEntity = (IEntity) member.getMember(baseEntity);
            if (memberMeta.isOwnedRelationships()) {
                if (!EqualsHelper.equals(childEntity.getPrimaryKey(), baseChildEntity.getPrimaryKey())) {
                    if (baseChildEntity.getPrimaryKey() != null) {
                        // Cascade delete
                        delete(baseChildEntity);
                    }
                }
                merge(connection, tableModel(childEntity.getEntityMeta()), childEntity, now);
            } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
                mergeReference(connection, memberMeta, childEntity, now);
            }
        }
        if (updated) {
            if (updatedTs != null) {
                entity.setMemberValue(updatedTs, now);
            }
            if (entity.getPrimaryKey() == null) {
                insert(connection, tm, entity, now);
            } else {
                if (!update(connection, tm, entity, now, true)) {
                    if (tm.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) {
                        insert(connection, tm, entity, now);
                    } else {
                        throw new RuntimeException("Entity " + tm.entityMeta().getCaption() + " " + entity.getPrimaryKey() + " NotFound");
                    }
                }
            }
        }
    }

    @Override
    public <T extends IEntity> T retrieve(Class<T> entityClass, long primaryKey) {
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
            connection = connectionProvider.getConnection();
            return cascadeRetrieve(connection, entity) != null;
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    private <T extends IEntity> T cascadeRetrieve(Connection connection, T entity) {
        if (entity.getPrimaryKey() == null) {
            return null;
        }
        TableModel tm = tableModel(entity.getEntityMeta());
        if (tm.retrieve(connection, entity.getPrimaryKey(), entity)) {
            return cascadeRetrieveMembers(connection, tm, entity);
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
                IEntity childEntity = (IEntity) member.getMember(entity);
                cascadeRetrieve(connection, childEntity);
            } else {
                ICollection<IEntity, ?> iCollectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
                for (IEntity childEntity : iCollectionMember) {
                    cascadeRetrieve(connection, childEntity);
                }
            }
        }
        return entity;
    }

    @Override
    public <T extends IEntity> T retrieve(EntityQueryCriteria<T> criteria) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
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
    public <T extends IEntity> Map<Long, T> retrieve(Class<T> entityClass, Iterable<Long> primaryKeys) {
        Connection connection = null;
        Map<Long, T> entities = new HashMap<Long, T>();
        TableModel tm = null;
        try {
            connection = connectionProvider.getConnection();
            int count = 0;
            for (long pk : primaryKeys) {
                final T entity = EntityFactory.create(entityClass);
                entity.setPrimaryKey(pk);
                if (count == 0) {
                    tm = tableModel(entity.getEntityMeta());
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
        StringBuilder propertyName = new StringBuilder();
        final int pathLength = path.getPathMembers().size();
        EntityMeta em = meta;
        MemberMeta mm = null;
        int count = 0;
        for (String memberName : path.getPathMembers()) {
            //TODO ICollection support
            if (mm != null) {
                Class<?> valueClass = mm.getValueClass();
                if (!(IEntity.class.isAssignableFrom(valueClass))) {
                    throw new RuntimeException("Invalid member in path " + memberName);
                } else {
                    em = EntityFactory.getEntityMeta((Class<? extends IEntity>) valueClass);
                }
            }
            mm = em.getMemberMeta(memberName);
            count++;
            propertyName.append(memberName);
            if (pathLength != count) {
                if (!mm.isEmbedded()) {
                    log.warn("Path {}; not implemented", path);
                    throw new RuntimeException("Invalid member in path " + memberName);
                }
                propertyName.append("_");
            }
        }
        return propertyName.toString();
    }

    @Override
    public <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
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
        final Connection connection = connectionProvider.getConnection();
        final TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
        if (encodedCursorRefference != null) {
            log.info("Received encodedCursorReference:" + encodedCursorRefference + ", will use it");
            // TODO   
        }
        final ResultSetIterator<T> iterable = tm.queryIterable(connection, criteria, -1);

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
    }

    @Override
    public <T extends IEntity> List<Long> queryKeys(EntityQueryCriteria<T> criteria) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
            return tm.queryKeys(connection, criteria, -1);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> ICursorIterator<Long> queryKeys(String encodedCursorRefference, EntityQueryCriteria<T> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IEntity> int count(EntityQueryCriteria<T> criteria) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
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
    public <T extends IEntity> void delete(Class<T> entityClass, long primaryKey) {
        delete(EntityFactory.getEntityMeta(entityClass), primaryKey, null);
    }

    private <T extends IEntity> void delete(EntityMeta entityMeta, long primaryKey, IEntity cascadedeleteDataEntity) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            cascadeDelete(connection, entityMeta, primaryKey, cascadedeleteDataEntity);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    // cascadedeleteDataEntity is consistent with GAE implementation of delete(IEntity entity).
    private <T extends IEntity> void cascadeDelete(Connection connection, EntityMeta entityMeta, long primaryKey, IEntity cascadedeleteDataEntity) {
        TableModel tm = tableModel(entityMeta);

        if (cascadedeleteDataEntity != null) {
            for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembers()) {
                IEntity childEntity = (IEntity) member.getMember(cascadedeleteDataEntity);
                if (childEntity.getPrimaryKey() != null) {
                    cascadeDelete(connection, childEntity.getEntityMeta(), childEntity.getPrimaryKey(), childEntity);
                }
            }
        }

        for (MemberOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
            if ((cascadedeleteDataEntity != null) && member.getMemberMeta().isOwnedRelationships()
                    && (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet)) {
                for (IEntity childEntity : (ICollection<IEntity, ?>) member.getMember(cascadedeleteDataEntity)) {
                    cascadeDelete(connection, childEntity.getEntityMeta(), childEntity.getPrimaryKey(), childEntity);
                }
            }

            // remove join table data
            CollectionsTableModel.delete(connection, primaryKey, member);
        }

        if (!tm.delete(connection, primaryKey)) {
            throw new RuntimeException("Entity " + entityMeta.getCaption() + " " + primaryKey + " NotFound");
        }
    }

    /**
     * This does cascade delete and removes data from Cache so it runs Retrieve first
     */
    @Override
    public <T extends IEntity> int delete(EntityQueryCriteria<T> criteria) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));

            List<T> entities = tm.query(connection, criteria, -1);

            int count = 0;
            if (entities.size() > 0) {
                List<Long> primaryKeys = new Vector<Long>();
                for (T entity : entities) {
                    primaryKeys.add(entity.getPrimaryKey());
                    // TODO optimize
                    cascadeRetrieveMembers(connection, tm, entity);
                }

                // remove data from join tables first, No cascade delete
                for (MemberOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                    //CollectionsTableModel.delete(connection, member, qb, tm.getTableName());
                    if (member.getMemberMeta().isOwnedRelationships() && (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet)) {

                        // TODO optimize
                        for (T entity : entities) {
                            for (IEntity childEntity : (ICollection<IEntity, ?>) member.getMember(entity)) {
                                cascadeDelete(connection, childEntity.getEntityMeta(), childEntity.getPrimaryKey(), childEntity);
                            }
                        }
                    }

                    CollectionsTableModel.delete(connection, primaryKeys, member);
                }

                count = tm.delete(connection, primaryKeys);
                // TODO remove entities from Cache
            }
            return count;
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> void delete(Class<T> entityClass, Iterable<Long> primaryKeys) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
            TableModel tm = tableModel(entityMeta);
            for (MemberOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
                CollectionsTableModel.delete(connection, primaryKeys, member);
            }
            tm.delete(connection, primaryKeys);
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
