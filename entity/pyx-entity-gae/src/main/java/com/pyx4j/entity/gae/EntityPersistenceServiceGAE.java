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
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.gae;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.meta.MemberMeta;

/**
 * 
 * @see PersistenceServicesFactory.GAE_IMPL_CLASS
 * 
 */
public class EntityPersistenceServiceGAE implements IEntityPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(EntityPersistenceServiceGAE.class);

    private final int ORDINARY_STRING_LENGHT_MAX = 500;

    private final DatastoreService datastore;

    public EntityPersistenceServiceGAE() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    private void updateEntityProperties(Entity entity, IEntity<?> iEntity) {
        for (Map.Entry<String, Object> me : iEntity.getValue().entrySet()) {
            if (me.getKey().equals(IEntity.PRIMARY_KEY)) {
                continue;
            }
            MemberMeta meta = iEntity.getMemberMeta(me.getKey());
            if (meta.isTransient()) {
                continue;
            }
            Object value = me.getValue();
            if (value instanceof Map<?, ?>) {
                String childKey;
                if (meta.isOwnedRelationships()) {
                    // Save Owned iEntity
                    IEntity<?> childIEntity = (IEntity<?>) iEntity.getMember(me.getKey());
                    persist(childIEntity);
                    childKey = childIEntity.getPrimaryKey();
                } else {
                    childKey = (String) ((Map<String, Object>) value).get(IEntity.PRIMARY_KEY);
                    if (childKey == null) {
                        throw new Error("Saving unperisted reference " + meta.getCaption());
                    }
                }
                value = KeyFactory.stringToKey(childKey);
            } else if (value instanceof String) {
                if (meta.getStringLength() > ORDINARY_STRING_LENGHT_MAX) {
                    value = new Text((String) value);
                }
            } else if (value instanceof Enum<?>) {
                value = ((Enum<?>) value).name();
            } else if ((ISet.class.isAssignableFrom(meta.getObjectClass())) && (value instanceof Set<?>)) {
                Set<Key> childKeys = new HashSet<Key>();
                if (meta.isOwnedRelationships()) {
                    // Save Owned iEntity
                    ISet<IEntity<?>> memberSet = (ISet<IEntity<?>>) iEntity.getMember(me.getKey());
                    for (IEntity<?> childIEntity : memberSet) {
                        persist(childIEntity);
                        childKeys.add(KeyFactory.stringToKey(childIEntity.getPrimaryKey()));
                    }
                } else {
                    for (Object el : (Set) value) {
                        String childKey = (String) ((Map<String, Object>) el).get(IEntity.PRIMARY_KEY);
                        if (childKey == null) {
                            throw new Error("Saving unperisted reference " + meta.getCaption());
                        }
                        childKeys.add(KeyFactory.stringToKey(childKey));
                    }
                }
                value = childKeys;
            } else {
                System.out.println("Else:" + value.getClass().getName());
            }
            entity.setProperty(me.getKey(), value);
        }
    }

    private String getIEntityKind(IEntity<?> iEntity) {
        return iEntity.getObjectClass().getName();
    }

    private <T extends IEntity<?>> String getIEntityKind(Class<T> entityClass) {
        return entityClass.getName();
    }

    @Override
    public void persist(IEntity<?> iEntity) {
        Entity entity;
        if (iEntity.getPrimaryKey() == null) {
            entity = new Entity(getIEntityKind(iEntity));
        } else {
            Key key = KeyFactory.stringToKey(iEntity.getPrimaryKey());
            entity = new Entity(key);
        }
        updateEntityProperties(entity, iEntity);
        Key keyCreated = datastore.put(entity);
        iEntity.setPrimaryKey(KeyFactory.keyToString(keyCreated));
    }

    @Override
    public void delete(IEntity<?> iEntity) {
        datastore.delete(KeyFactory.stringToKey(iEntity.getPrimaryKey()));
    }

    @Override
    public void delete(Class<?> entityClass, String primaryKey) {
        datastore.delete(KeyFactory.stringToKey(primaryKey));
    }

    private void updateIEntity(IEntity<?> iEntity, Entity entity) {
        iEntity.setPrimaryKey(KeyFactory.keyToString(entity.getKey()));
        for (Map.Entry<String, Object> me : entity.getProperties().entrySet()) {
            Object value = me.getValue();
            if (value instanceof Text) {
                value = ((Text) value).getValue();
            } else if (value instanceof Key) {
                IEntity<?> childIEntity = (IEntity<?>) iEntity.getMember(me.getKey());
                if (childIEntity.getMeta().isDetached()) {
                    childIEntity.setPrimaryKey(KeyFactory.keyToString((Key) value));
                } else {
                    retrieveEntity(childIEntity, (Key) value);
                }
                continue;
            } else if (value instanceof String) {
                Class<?> cls = iEntity.getMemberMeta(me.getKey()).getValueClass();
                if (Enum.class.isAssignableFrom(cls)) {
                    value = Enum.valueOf((Class<Enum>) cls, (String) value);
                }
            } else if (value instanceof List<?>) {
                IObject<?, ?> member = iEntity.getMember(me.getKey());
                if (member instanceof ISet) {
                    for (Key childKey : (List<Key>) value) {
                        IEntity<?> childIEntity = EntityFactory.create((Class<IEntity<?>>) member.getMeta().getValueClass());
                        if (member.getMeta().isDetached()) {
                            childIEntity.setPrimaryKey(KeyFactory.keyToString(childKey));
                        } else {
                            retrieveEntity(childIEntity, childKey);
                        }
                        ((ISet) member).add(childIEntity);
                    }
                    continue;
                }
            }
            iEntity.setMemberValue(me.getKey(), value);
        }
    }

    private void retrieveEntity(IEntity<?> iEntity, Key key) {
        if (!getIEntityKind(iEntity).equals(key.getKind())) {
            throw new RuntimeException("Unexpected IEntity " + getIEntityKind(iEntity) + " Kind " + key.getKind());
        }
        Entity entity;
        try {
            entity = datastore.get(key);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("EntityNotFound");
        }
        updateIEntity(iEntity, entity);
    }

    @Override
    public <T extends IEntity<?>> T retrieve(Class<T> entityClass, String primaryKey) {
        Key key = KeyFactory.stringToKey(primaryKey);
        if (!getIEntityKind(entityClass).equals(key.getKind())) {
            throw new RuntimeException("Unexpected IEntity " + getIEntityKind(entityClass) + " Kind " + key.getKind());
        }
        Entity entity;
        try {
            entity = datastore.get(key);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("EntityNotFound");
        }

        T iEntity = EntityFactory.create(entityClass);
        updateIEntity(iEntity, entity);
        return iEntity;
    }

    public static Query.FilterOperator operator(PropertyCriterion.Restriction restriction) {
        switch (restriction) {
        case LESS_THAN:
            return Query.FilterOperator.LESS_THAN;
        case LESS_THAN_OR_EQUAL:
            return Query.FilterOperator.LESS_THAN_OR_EQUAL;
        case GREATER_THAN:
            return Query.FilterOperator.GREATER_THAN;
        case GREATER_THAN_OR_EQUAL:
            return Query.FilterOperator.GREATER_THAN_OR_EQUAL;
        case EQUAL:
            return Query.FilterOperator.EQUAL;
        case NOT_EQUAL:
            return Query.FilterOperator.NOT_EQUAL;
        case IN:
            return Query.FilterOperator.IN;
        default:
            throw new RuntimeException("Unsupported Operator " + restriction);
        }
    }

    public static Object datastoreValue(Serializable value) {
        if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name();
        } else if (value instanceof IEntity<?>) {
            return KeyFactory.stringToKey(((IEntity<?>) value).getPrimaryKey());
        } else {
            return value;
        }
    }

    private void addFilter(Query query, PropertyCriterion propertyCriterion) {
        query.addFilter(propertyCriterion.getPropertyName(), operator(propertyCriterion.getRestriction()), datastoreValue(propertyCriterion.getValue()));
    }

    @SuppressWarnings("unchecked")
    private <T extends IEntity<?>> Class<T> entityClass(EntityCriteria<T> criteria) {
        try {
            return (Class<T>) Class.forName(criteria.getDomainName(), true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Not an Entity");
        }
    }

    private <T extends IEntity<?>> Query buildQuery(Class<T> entityClass, EntityCriteria<T> criteria) {
        Query query = new Query(getIEntityKind(entityClass));
        if (criteria.getFilters() != null) {
            for (Criterion cr : criteria.getFilters()) {
                if (cr instanceof PropertyCriterion) {
                    addFilter(query, (PropertyCriterion) cr);
                }
            }
        }
        return query;
    }

    @Override
    public <T extends IEntity<?>> List<T> query(EntityCriteria<T> criteria) {
        Class<T> entityClass = entityClass(criteria);
        Query query = buildQuery(entityClass, criteria);
        PreparedQuery pq = datastore.prepare(query);

        List<T> rc = new Vector<T>();
        for (Entity entity : pq.asIterable()) {
            T iEntity = EntityFactory.create(entityClass);
            updateIEntity(iEntity, entity);
            rc.add(iEntity);
        }
        return rc;
    }

    public <T extends IEntity<?>> List<String> queryKeys(EntityCriteria<T> criteria) {
        Class<T> entityClass = entityClass(criteria);
        Query query = buildQuery(entityClass, criteria);
        query.setKeysOnly();
        PreparedQuery pq = datastore.prepare(query);

        List<String> rc = new Vector<String>();
        for (Entity entity : pq.asIterable()) {
            rc.add(KeyFactory.keyToString(entity.getKey()));
        }
        return rc;
    }

    public <T extends IEntity<?>> void delete(EntityCriteria<T> criteria) {
        Class<T> entityClass = entityClass(criteria);
        Query query = buildQuery(entityClass, criteria);
        query.setKeysOnly();
        PreparedQuery pq = datastore.prepare(query);

        List<Key> keys = new Vector<Key>();
        for (Entity entity : pq.asIterable()) {
            keys.add(entity.getKey());
        }
        datastore.delete(keys);
    }
}
