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
import com.pyx4j.entity.annotations.Adapters;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.dialect.SQLAggregateFunctions;
import com.pyx4j.entity.rdb.mapping.Mappings;
import com.pyx4j.entity.rdb.mapping.TableModel;
import com.pyx4j.entity.server.AdapterFactory;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.IEntityPersistenceServiceExt;
import com.pyx4j.entity.shared.ConcurrentUpdateException;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
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
        persist(tableModel(entity.getEntityMeta()), entity, new Date());
    }

    private void persist(TableModel tm, IEntity entity, Date now) {
        for (MemberMeta memberMeta : tm.operationsMeta().getCascadePersistMembers()) {
            if (memberMeta.isEntity()) {
                IEntity childEntity = (IEntity) entity.getMember(memberMeta.getFieldName());
                persist(tableModel(childEntity.getEntityMeta()), childEntity, now);
            } else {
                //TODO Collections  
            }
        }
        String updatedTs = tm.entityMeta().getUpdatedTimestampMember();
        if (updatedTs != null) {
            entity.setMemberValue(updatedTs, now);
        }
        if (entity.getPrimaryKey() == null) {
            tm.insert(connectionProvider, entity);
        } else {
            if (!tm.update(connectionProvider, entity)) {
                if (tm.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) {
                    tm.insert(connectionProvider, entity);
                } else {
                    throw new RuntimeException("Entity " + tm.entityMeta().getCaption() + " " + entity.getPrimaryKey() + " NotFound");
                }
            }
        }
    }

    @Override
    public <T extends IEntity> void persist(Iterable<T> entityIterable) {
        // TODO Auto-generated method stub
    }

    @Override
    public void merge(IEntity entity) {
        merge(tableModel(entity.getEntityMeta()), entity, new Date());
    }

    @SuppressWarnings("unchecked")
    private boolean applyModifications(IEntity baseEntity, IEntity entity) {
        boolean updated = false;
        Class<? extends MemberModificationAdapter<?>>[] entityMemebersModificationAdapters = null;
        Adapters adapters = entity.getEntityMeta().getAnnotation(Adapters.class);
        if (adapters != null) {
            entityMemebersModificationAdapters = adapters.modificationAdapters();
        }
        for (String memberName : entity.getEntityMeta().getMemberNames()) {
            MemberMeta memberMeta = entity.getEntityMeta().getMemberMeta(memberName);
            if (memberMeta.isTransient()) {
                continue;
            }
            Object value;
            Object lastValue;
            if (IEntity.class.isAssignableFrom(memberMeta.getObjectClass())) {
                value = ((IEntity) entity.getMember(memberMeta.getFieldName())).getPrimaryKey();
                lastValue = ((IEntity) baseEntity.getMember(memberMeta.getFieldName())).getPrimaryKey();
            } else {
                value = entity.getMemberValue(memberName);
                lastValue = baseEntity.getMemberValue(memberName);
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

    private void merge(TableModel tm, IEntity entity, Date now) {
        final IEntity baseEntity = EntityFactory.create(tm.entityMeta().getEntityClass());
        String updatedTs = tm.entityMeta().getUpdatedTimestampMember();
        boolean updated;
        if (entity.getPrimaryKey() != null) {
            if (!tm.retrieve(connectionProvider, entity.getPrimaryKey(), baseEntity)) {
                throw new RuntimeException("Entity " + tm.entityMeta().getCaption() + " " + entity.getPrimaryKey() + " NotFound");
            }
            if (!EqualsHelper.equals(entity.getMemberValue(updatedTs), baseEntity.getMemberValue(updatedTs))) {
                log.debug("Timestamp change {} -> {}", baseEntity.getMemberValue(updatedTs), entity.getMemberValue(updatedTs));
                throw new ConcurrentUpdateException(i18n.tr("{0} updated externally", tm.entityMeta().getCaption()));
            }
            updated = applyModifications(baseEntity, entity);
        } else {
            updated = true;
        }
        for (MemberMeta memberMeta : tm.operationsMeta().getCascadePersistMembers()) {
            if (memberMeta.isEntity()) {
                IEntity childEntity = (IEntity) entity.getMember(memberMeta.getFieldName());
                IEntity baseChildEntity = (IEntity) baseEntity.getMember(memberMeta.getFieldName());
                if (!EqualsHelper.equals(childEntity.getPrimaryKey(), baseChildEntity.getPrimaryKey())) {
                    if (baseChildEntity.getPrimaryKey() != null) {
                        // Cascade delete
                        delete(baseChildEntity);
                    }
                }
                merge(tableModel(childEntity.getEntityMeta()), childEntity, now);
            } else {
                //TODO Collections  
            }
        }
        if (updated) {
            if (updatedTs != null) {
                entity.setMemberValue(updatedTs, now);
            }
            if (entity.getPrimaryKey() == null) {
                tm.insert(connectionProvider, entity);
            } else {
                if (!tm.update(connectionProvider, entity)) {
                    if (tm.getPrimaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED) {
                        tm.insert(connectionProvider, entity);
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
        TableModel tm = tableModel(entity.getEntityMeta());
        if (tm.retrieve(connectionProvider, primaryKey, entity)) {
            return cascadeRetrieve(tm, entity);
        } else {
            return null;
        }
    }

    private <T extends IEntity> T cascadeRetrieve(TableModel tm, T entity) {
        for (MemberMeta memberMeta : tm.operationsMeta().getCascadeRetrieveMembers()) {
            // Do not retrieve Owner, since already retrieved
            if ((entity.getOwner() != null) && (memberMeta.isOwner())) {
                continue;
            }
            if (memberMeta.isEntity()) {
                IEntity childEntity = (IEntity) entity.getMember(memberMeta.getFieldName());
                if (childEntity.getPrimaryKey() != null) {
                    TableModel ctm = tableModel(childEntity.getEntityMeta());
                    if (ctm.retrieve(connectionProvider, childEntity.getPrimaryKey(), childEntity)) {
                        cascadeRetrieve(ctm, childEntity);
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
        TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
        List<T> rs = tm.query(connectionProvider, criteria, 1);
        if (rs.isEmpty()) {
            return null;
        } else {
            return cascadeRetrieve(tm, rs.get(0));
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
        TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
        List<T> l = tm.query(connectionProvider, criteria, -1);
        for (T entity : l) {
            cascadeRetrieve(tm, entity);
        }
        return l;
    }

    @Override
    public <T extends IEntity> ICursorIterator<T> query(String encodedCursorRefference, EntityQueryCriteria<T> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IEntity> List<Long> queryKeys(EntityQueryCriteria<T> criteria) {
        TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
        return tm.queryKeys(connectionProvider, criteria, -1);
    }

    @Override
    public <T extends IEntity> ICursorIterator<Long> queryKeys(String encodedCursorRefference, EntityQueryCriteria<T> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IEntity> int count(EntityQueryCriteria<T> criteria) {
        TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
        Number count = (Number) tm.aggregate(connectionProvider, criteria, SQLAggregateFunctions.COUNT, null);
        if (count == null) {
            return 0;
        } else {
            return count.intValue();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void delete(IEntity entity) {
        delete((Class<IEntity>) entity.getObjectClass(), entity.getPrimaryKey());
    }

    @Override
    public <T extends IEntity> void delete(Class<T> entityClass, long primaryKey) {
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        TableModel tm = tableModel(entityMeta);
        if (!tm.delete(connectionProvider, primaryKey)) {
            throw new RuntimeException("Entity " + entityMeta.getCaption() + " " + primaryKey + " NotFound");
        }
    }

    @Override
    public <T extends IEntity> int delete(EntityQueryCriteria<T> criteria) {
        TableModel tm = tableModel(EntityFactory.getEntityMeta(criteria.getEntityClass()));
        return tm.delete(connectionProvider, criteria);
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
