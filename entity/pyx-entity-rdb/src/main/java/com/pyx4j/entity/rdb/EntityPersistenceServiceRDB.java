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
import java.util.List;
import java.util.Map;

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
            if (memberMeta.isEntity()) {
                IEntity childEntity = (IEntity) member.getMember(entity);
                if (memberMeta.isOwnedRelationships()) {
                    persist(connection, tableModel(childEntity.getEntityMeta()), childEntity, now);
                } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
                    mergeReference(connection, memberMeta, childEntity, now);
                }
            }
        }
        String updatedTs = tm.entityMeta().getUpdatedTimestampMember();
        if (updatedTs != null) {
            entity.setMemberValue(updatedTs, now);
        }
        if (entity.getPrimaryKey() == null) {
            insert(connection, tm, entity, now);
        } else {
            if (!tm.update(connection, entity)) {
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
            if (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet) {
                MemberMeta memberMeta = member.getMemberMeta();
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

    //    private void update(Connection connection, TableModel tm, IEntity entity, Date now) {
    //        for (MemberOperationsMeta member : tm.operationsMeta().getCollectionMembers()) {
    //            if (member.getMemberMeta().getObjectClassType() != ObjectClassType.PrimitiveSet) {
    //                MemberMeta memberMeta = member.getMemberMeta();
    //                ICollection<IEntity, ?> iCollectionMember = (ICollection<IEntity, ?>) member.getMember(entity);
    //                for (IEntity childEntity : iCollectionMember) {
    //                    if (memberMeta.isOwnedRelationships()) {
    //                        persist(connection, tableModel(childEntity.getEntityMeta()), childEntity, now);
    //                    } else if ((memberMeta.getAnnotation(Reference.class) != null) && (childEntity.getPrimaryKey() == null) && (!childEntity.isNull())) {
    //                        mergeReference(connection, memberMeta, childEntity, now);
    //                    }
    //                }
    //            }
    //        }
    //        tm.update(connection, entity);
    //    }

    @Override
    public <T extends IEntity> void persist(Iterable<T> entityIterable) {
        // TODO Auto-generated method stub
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
    private boolean applyModifications(TableModel tm, IEntity baseEntity, IEntity entity) {
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
            }
        }
        return updated;
    }

    private void merge(Connection connection, TableModel tm, IEntity entity, Date now) {
        final IEntity baseEntity = EntityFactory.create(tm.entityMeta().getEntityClass());
        String updatedTs = tm.entityMeta().getUpdatedTimestampMember();
        boolean updated;
        if (entity.getPrimaryKey() != null) {
            if (!tm.retrieve(connection, entity.getPrimaryKey(), baseEntity)) {
                throw new RuntimeException("Entity " + tm.entityMeta().getCaption() + " " + entity.getPrimaryKey() + " NotFound");
            }
            if (!EqualsHelper.equals(entity.getMemberValue(updatedTs), baseEntity.getMemberValue(updatedTs))) {
                log.debug("Timestamp change {} -> {}", baseEntity.getMemberValue(updatedTs), entity.getMemberValue(updatedTs));
                throw new ConcurrentUpdateException(i18n.tr("{0} updated externally", tm.entityMeta().getCaption()));
            }
            updated = applyModifications(tm, baseEntity, entity);
        } else {
            updated = true;
        }
        for (MemberOperationsMeta member : tm.operationsMeta().getCascadePersistMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            if (memberMeta.isEntity()) {
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
            } else {
                //TODO Collections  
            }
        }
        if (updated) {
            if (updatedTs != null) {
                entity.setMemberValue(updatedTs, now);
            }
            if (entity.getPrimaryKey() == null) {
                insert(connection, tm, entity, now);
            } else {
                if (!tm.update(connection, entity)) {
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
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            final T entity = EntityFactory.create(entityClass);
            TableModel tm = tableModel(entity.getEntityMeta());
            if (tm.retrieve(connection, primaryKey, entity)) {
                return cascadeRetrieve(connection, tm, entity);
            } else {
                return null;
            }

        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    private <T extends IEntity> T cascadeRetrieve(Connection connection, TableModel tm, T entity) {
        for (MemberOperationsMeta member : tm.operationsMeta().getCascadeRetrieveMembers()) {
            MemberMeta memberMeta = member.getMemberMeta();
            // Do not retrieve Owner, since already retrieved
            if ((entity.getOwner() != null) && (memberMeta.isOwner())) {
                continue;
            }
            if (memberMeta.isEntity()) {
                IEntity childEntity = (IEntity) member.getMember(entity);
                if (childEntity.getPrimaryKey() != null) {
                    TableModel ctm = tableModel(childEntity.getEntityMeta());
                    if (ctm.retrieve(connection, childEntity.getPrimaryKey(), childEntity)) {
                        cascadeRetrieve(connection, ctm, childEntity);
                    }
                }
            } else {
                //TODO Collections                
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
                return cascadeRetrieve(connection, tm, rs.get(0));
            }
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> Map<Long, T> retrieve(Class<T> entityClass, Iterable<Long> primaryKeys) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getIndexedPropertyName(EntityMeta meta, Path path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPropertyName(EntityMeta meta, Path path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
            List<T> l = tm.query(connection, criteria, -1);
            for (T entity : l) {
                cascadeRetrieve(connection, tm, entity);
            }
            return l;
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> ICursorIterator<T> query(String encodedCursorRefference, EntityQueryCriteria<T> criteria) {
        // TODO Auto-generated method stub
        return null;
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

    @SuppressWarnings("unchecked")
    @Override
    public void delete(IEntity entity) {
        delete((Class<IEntity>) entity.getObjectClass(), entity.getPrimaryKey());
    }

    @Override
    public <T extends IEntity> void delete(Class<T> entityClass, long primaryKey) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
            TableModel tm = tableModel(entityMeta);
            if (!tm.delete(connection, primaryKey)) {
                throw new RuntimeException("Entity " + entityMeta.getCaption() + " " + primaryKey + " NotFound");
            }
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> int delete(EntityQueryCriteria<T> criteria) {
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
            return tm.delete(connection, criteria);
        } finally {
            SQLUtils.closeQuietly(connection);
        }
    }

    @Override
    public <T extends IEntity> void delete(Class<T> entityClass, Iterable<Long> primaryKeys) {
        // TODO Auto-generated method stub
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
