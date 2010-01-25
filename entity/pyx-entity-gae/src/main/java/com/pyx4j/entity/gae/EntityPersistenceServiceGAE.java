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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
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

    private static final String SECONDARY_PRROPERTY_SUFIX = "_$";

    private final DatastoreService datastore;

    public EntityPersistenceServiceGAE() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    private static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (Throwable e) {
        }
    }

    private static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (Throwable e) {
        }
    }

    private Blob createBlob(Serializable o) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(buf);
            out.writeObject(o);
        } catch (Throwable t) {
            throw new Error("Unable to serialize " + o.getClass(), t);
        } finally {
            closeQuietly(out);
        }
        Blob blob = new Blob(buf.toByteArray());
        return blob;
    }

    private Object readObject(Blob blob) {
        if (blob == null) {
            return null;
        }
        ByteArrayInputStream b = new ByteArrayInputStream(blob.getBytes());
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(b);
            return in.readObject();
        } catch (Throwable t) {
            throw new Error("Unable to de serialize ", t);
        } finally {
            closeQuietly(in);
            closeQuietly(b);
        }
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
                if (meta.isOwnedRelationships()) {
                    // Save Owned iEntity
                    IEntity<?> childIEntity = (IEntity<?>) iEntity.getMember(me.getKey());
                    value = persistImpl(childIEntity);
                } else {
                    String childKey = (String) ((Map<String, Object>) value).get(IEntity.PRIMARY_KEY);
                    if (childKey == null) {
                        throw new Error("Saving unperisted reference " + meta.getCaption());
                    }
                    value = KeyFactory.stringToKey(childKey);
                }
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
                        Key key = persistImpl(childIEntity);
                        childKeys.add(key);
                    }
                } else {
                    for (Object el : (Set<?>) value) {
                        String childKey = (String) ((Map<String, Object>) el).get(IEntity.PRIMARY_KEY);
                        if (childKey == null) {
                            throw new Error("Saving unperisted reference " + meta.getCaption());
                        }
                        childKeys.add(KeyFactory.stringToKey(childKey));
                    }
                }
                value = childKeys;
            } else if ((IList.class.isAssignableFrom(meta.getObjectClass())) && (value instanceof List<?>)) {
                Set<Key> childKeys = new HashSet<Key>();
                Vector<Long> childKeysOrder = new Vector<Long>();
                if (meta.isOwnedRelationships()) {
                    // Save Owned iEntity
                    IList<IEntity<?>> memberList = (IList<IEntity<?>>) iEntity.getMember(me.getKey());
                    for (IEntity<?> childIEntity : memberList) {
                        Key key = persistImpl(childIEntity);
                        childKeys.add(key);
                        childKeysOrder.add(key.getId());
                    }
                } else {
                    for (Object el : (List<?>) value) {
                        String childKey = (String) ((Map<String, Object>) el).get(IEntity.PRIMARY_KEY);
                        if (childKey == null) {
                            throw new Error("Saving unperisted reference " + meta.getCaption());
                        }
                        Key key = KeyFactory.stringToKey(childKey);
                        childKeys.add(key);
                        childKeysOrder.add(key.getId());
                    }
                }
                entity.setProperty(me.getKey() + SECONDARY_PRROPERTY_SUFIX, createBlob(childKeysOrder));
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
        persistImpl(iEntity);
    }

    private Key persistImpl(IEntity<?> iEntity) {
        if (iEntity.getEntityMeta().isTransient()) {
            throw new Error("Can't persist Transient Entity");
        }
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
        return keyCreated;
    }

    @Override
    public void delete(IEntity<?> iEntity) {
        if (iEntity.getEntityMeta().isTransient()) {
            throw new Error("Can't delete Transient Entity");
        }
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
            } else if (value instanceof Long) {
                if (Integer.class.isAssignableFrom(iEntity.getMemberMeta(me.getKey()).getValueClass())) {
                    value = ((Long) value).intValue();
                }
            } else if (value instanceof List<?>) {
                IObject<?, ?> member = iEntity.getMember(me.getKey());
                if (member instanceof ISet<?>) {
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
                } else if (member instanceof List<?>) {
                    // retrieve order  and sort by this order
                    List<Long> childKeysOrder;
                    childKeysOrder = (List<Long>) readObject((Blob) entity.getProperty(me.getKey() + SECONDARY_PRROPERTY_SUFIX));
                    if (childKeysOrder != null) {
                        Collections.sort(((List<Key>) value), new KeyComparator(childKeysOrder));
                    }

                    for (Key childKey : (List<Key>) value) {
                        IEntity<?> childIEntity = EntityFactory.create((Class<IEntity<?>>) member.getMeta().getValueClass());
                        if (member.getMeta().isDetached()) {
                            childIEntity.setPrimaryKey(KeyFactory.keyToString(childKey));
                        } else {
                            retrieveEntity(childIEntity, childKey);
                        }
                        ((IList) member).add(childIEntity);
                    }
                    continue;
                }
            } else if (me.getKey().endsWith(SECONDARY_PRROPERTY_SUFIX)) {
                continue;
            }
            iEntity.setMemberValue(me.getKey(), value);
        }
    }

    private class KeyComparator implements Comparator<Key> {

        private final List<Long> keyIdOrdered;

        KeyComparator(List<Long> keyIdOrdered) {
            this.keyIdOrdered = keyIdOrdered;
        }

        @Override
        public int compare(Key k1, Key k2) {
            int idx1 = keyIdOrdered.indexOf(k1.getId());
            int idx2 = keyIdOrdered.indexOf(k2.getId());
            return idx1 - idx2;
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
        long start = System.nanoTime();
        Key key = KeyFactory.stringToKey(primaryKey);
        if (!getIEntityKind(entityClass).equals(key.getKind())) {
            throw new RuntimeException("Unexpected IEntity " + getIEntityKind(entityClass) + " Kind " + key.getKind());
        }
        T iEntity = EntityFactory.create(entityClass);
        if (iEntity.getEntityMeta().isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }

        Entity entity;
        try {
            entity = datastore.get(key);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("EntityNotFound");
        }

        updateIEntity(iEntity, entity);

        long duration = System.nanoTime() - start;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running retrieve {} took {}ms", entityClass.getName(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("retrieve {} took {}ms", entityClass.getName(), (int) (duration / Consts.MSEC2NANO));
        }
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
        long start = System.nanoTime();
        Class<T> entityClass = entityClass(criteria);
        if (EntityFactory.getEntityMeta(entityClass).isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityClass, criteria);
        PreparedQuery pq = datastore.prepare(query);

        List<T> rc = new Vector<T>();
        for (Entity entity : pq.asIterable()) {
            T iEntity = EntityFactory.create(entityClass);
            updateIEntity(iEntity, entity);
            rc.add(iEntity);
        }
        long duration = System.nanoTime() - start;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running query {} took {}ms", criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("query {} took {}ms", criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        }
        return rc;
    }

    public <T extends IEntity<?>> List<String> queryKeys(EntityCriteria<T> criteria) {
        long start = System.nanoTime();
        Class<T> entityClass = entityClass(criteria);
        if (EntityFactory.getEntityMeta(entityClass).isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityClass, criteria);
        query.setKeysOnly();
        PreparedQuery pq = datastore.prepare(query);

        List<String> rc = new Vector<String>();
        for (Entity entity : pq.asIterable()) {
            rc.add(KeyFactory.keyToString(entity.getKey()));
        }
        long duration = System.nanoTime() - start;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running queryKeys {} took {}ms", criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("queryKeys {} took {}ms", criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        }
        return rc;
    }

    public <T extends IEntity<?>> int count(EntityCriteria<T> criteria) {
        long start = System.nanoTime();
        Class<T> entityClass = entityClass(criteria);
        if (EntityFactory.getEntityMeta(entityClass).isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityClass, criteria);
        query.setKeysOnly();
        PreparedQuery pq = datastore.prepare(query);
        int rc = pq.countEntities();
        long duration = System.nanoTime() - start;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running countQuery {} took {}ms", criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("countQuery {} took {}ms", criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        }
        return rc;
    }

    public <T extends IEntity<?>> void delete(EntityCriteria<T> criteria) {
        Class<T> entityClass = entityClass(criteria);
        if (EntityFactory.getEntityMeta(entityClass).isTransient()) {
            throw new Error("Can't delete Transient Entity");
        }
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
