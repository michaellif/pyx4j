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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.google.appengine.api.datastore.FetchOptions;
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
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.gwt.server.IOUtils;

/**
 * 
 * @see PersistenceServicesFactory.GAE_IMPL_CLASS
 * 
 */
public class EntityPersistenceServiceGAE implements IEntityPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(EntityPersistenceServiceGAE.class);

    private final int ORDINARY_STRING_LENGHT_MAX = 500;

    private static final String SECONDARY_PRROPERTY_SUFIX = "-s";

    private static final String EMBEDDED_PRROPERTY_SUFIX = "-e";

    private final DatastoreService datastore;

    private final ThreadLocal<CallStats> datastoreCallStats = new ThreadLocal<CallStats>() {

        @Override
        protected CallStats initialValue() {
            return new CallStats();
        }

    };

    private static class CallStats {
        int count;
    }

    public EntityPersistenceServiceGAE() {
        datastore = DatastoreServiceFactory.getDatastoreService();
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
            IOUtils.closeQuietly(out);
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
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(b);
        }
    }

    private void embedEntityProperties(Entity entity, String prefix, String sufix, IEntity<?> childIEntity) {
        for (Map.Entry<String, Object> me : childIEntity.getValue().entrySet()) {
            if (me.getKey().equals(IEntity.PRIMARY_KEY)) {
                continue;
            }
            MemberMeta meta = childIEntity.getEntityMeta().getMemberMeta(me.getKey());
            if (meta.isTransient()) {
                continue;
            }
            Object value = me.getValue();
            if (IEntity.class.isAssignableFrom(meta.getObjectClass())) {
                if (meta.isEmbedded()) {
                    embedEntityProperties(entity, prefix + "_" + me.getKey(), sufix + EMBEDDED_PRROPERTY_SUFIX, (IEntity<?>) childIEntity
                            .getMember(me.getKey()));
                } else {
                    String kind = EntityFactory.getEntityMeta((Class<? extends IEntity<?>>) meta.getObjectClass()).getPersistenceName();
                    Key key = KeyFactory.createKey(kind, (Long) ((Map) value).get(IEntity.PRIMARY_KEY));
                    entity.setProperty(prefix + "_" + me.getKey() + sufix + EMBEDDED_PRROPERTY_SUFIX, key);
                }
            } else {
                //System.out.println(value + " + save as [" + prefix + "_" + me.getKey() + sufix + EMBEDDED_PRROPERTY_SUFIX + "]");
                entity.setProperty(prefix + "_" + me.getKey() + sufix + EMBEDDED_PRROPERTY_SUFIX, value);
            }
        }
    }

    private void updateEntityProperties(Entity entity, IEntity<?> iEntity, boolean merge) {
        for (Map.Entry<String, Object> me : iEntity.getValue().entrySet()) {
            if (me.getKey().equals(IEntity.PRIMARY_KEY)) {
                continue;
            }
            MemberMeta meta = iEntity.getEntityMeta().getMemberMeta(me.getKey());
            if (meta.isTransient()) {
                continue;
            }
            Object value = me.getValue();
            if (value instanceof Map<?, ?>) {
                if (!meta.isEntity()) {
                    throw new Error("Saving non persisted value " + meta.getCaption());
                } else if (meta.isOwnedRelationships()) {
                    // Save Owned iEntity
                    IEntity<?> childIEntity = (IEntity<?>) iEntity.getMember(me.getKey());
                    if (meta.isEmbedded()) {
                        embedEntityProperties(entity, me.getKey(), "", childIEntity);
                        continue;
                    } else {
                        value = persistImpl(childIEntity, merge);
                    }
                } else {
                    Long childKey = (Long) ((Map<String, Object>) value).get(IEntity.PRIMARY_KEY);
                    if (childKey == null) {
                        throw new Error("Saving non persisted reference " + meta.getCaption());
                    }
                    value = KeyFactory.createKey(EntityFactory.getEntityMeta((Class<? extends IEntity<?>>) meta.getObjectClass()).getPersistenceName(),
                            childKey);
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
                    if (meta.isEmbedded()) {
                        Set childValue = new HashSet();
                        String singleMemeberName = null;
                        for (IEntity<?> childIEntity : memberSet) {
                            if (singleMemeberName == null) {
                                singleMemeberName = childIEntity.getEntityMeta().getMemberNames().iterator().next();
                            }
                            childValue.add(childIEntity.getMemberValue(singleMemeberName));
                        }
                        entity.setProperty(me.getKey(), childValue);
                        continue;
                    } else {
                        for (IEntity<?> childIEntity : memberSet) {
                            Key key = persistImpl(childIEntity, merge);
                            childKeys.add(key);
                        }
                    }
                } else {
                    for (Object el : (Set<?>) value) {
                        Long childKey = (Long) ((Map<String, Object>) el).get(IEntity.PRIMARY_KEY);
                        if (childKey == null) {
                            throw new Error("Saving non persisted reference " + meta.getCaption());
                        }
                        childKeys.add(KeyFactory.createKey(
                                EntityFactory.getEntityMeta((Class<? extends IEntity<?>>) meta.getValueClass()).getPersistenceName(), childKey));
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
                        Key key = persistImpl(childIEntity, merge);
                        childKeys.add(key);
                        childKeysOrder.add(key.getId());
                    }
                } else {
                    for (Object el : (List<?>) value) {
                        Long childKey = (Long) ((Map<String, Object>) el).get(IEntity.PRIMARY_KEY);
                        if (childKey == null) {
                            throw new Error("Saving non persisted reference " + meta.getCaption());
                        }
                        Key key = KeyFactory.createKey(EntityFactory.getEntityMeta((Class<? extends IEntity<?>>) meta.getValueClass()).getPersistenceName(),
                                childKey);
                        childKeys.add(key);
                        childKeysOrder.add(childKey);
                    }
                }
                entity.setProperty(me.getKey() + SECONDARY_PRROPERTY_SUFIX, createBlob(childKeysOrder));
                value = childKeys;
            } else if (value != null) {
                if (value.getClass().isArray()) {
                    //TODO support more arrays
                    value = new Blob((byte[]) value);
                }
            }
            entity.setProperty(me.getKey(), value);
        }
    }

    private String getIEntityKind(IEntity<?> iEntity) {
        return iEntity.getEntityMeta().getPersistenceName();
    }

    //    private <T extends IEntity<?>> String getIEntityKind(Class<T> entityClass) {
    //        return entityClass.getName();
    //    }

    @Override
    public void persist(IEntity<?> iEntity) {
        persistImpl(iEntity, false);
    }

    @Override
    public void merge(IEntity<?> iEntity) {
        persistImpl(iEntity, true);
    }

    private Key persistImpl(IEntity<?> iEntity, boolean merge) {
        if (iEntity.getEntityMeta().isTransient()) {
            throw new Error("Can't persist Transient Entity");
        }
        Entity entity;
        if (iEntity.getPrimaryKey() == null) {
            entity = new Entity(getIEntityKind(iEntity));
        } else {
            Key key = KeyFactory.createKey(getIEntityKind(iEntity), iEntity.getPrimaryKey());
            if (merge) {
                try {
                    datastoreCallStats.get().count++;
                    entity = datastore.get(key);
                } catch (EntityNotFoundException e) {
                    throw new RuntimeException("EntityNotFound");
                }
            } else {
                entity = new Entity(key);
            }
        }
        updateEntityProperties(entity, iEntity, merge);
        datastoreCallStats.get().count++;
        Key keyCreated = datastore.put(entity);
        iEntity.setPrimaryKey(keyCreated.getId());
        return keyCreated;
    }

    @Override
    public void delete(IEntity<?> iEntity) {
        if (iEntity.getEntityMeta().isTransient()) {
            throw new Error("Can't delete Transient Entity");
        }
        datastoreCallStats.get().count++;
        datastore.delete(KeyFactory.createKey(getIEntityKind(iEntity), iEntity.getPrimaryKey()));
    }

    @Override
    public void delete(Class<IEntity<?>> entityClass, long primaryKey) {
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        datastoreCallStats.get().count++;
        datastore.delete(KeyFactory.createKey(entityMeta.getPersistenceName(), primaryKey));
    }

    private Object deserializeValue(IEntity<?> iEntity, String keyName, Object value, Map<Key, IEntity<?>> retrievedMap) {
        if (value instanceof Text) {
            return ((Text) value).getValue();
        } else if (value instanceof Key) {
            IEntity<?> childIEntity = (IEntity<?>) iEntity.getMember(keyName);
            if (childIEntity.getMeta().isDetached()) {
                childIEntity.setPrimaryKey(((Key) value).getId());
            } else {
                retrieveEntity(childIEntity, (Key) value, retrievedMap);
            }
            return childIEntity.getValue();
        }
        return value;
    }

    /**
     * Recursive set child values
     */
    private void setEmbededIEntityValue(IEntity<?> iEntity, String keyName, Object value, Map<Key, IEntity<?>> retrievedMap) {
        String memberName = keyName.substring(0, keyName.indexOf('_'));
        IObject<?, ?> member = iEntity.getMember(memberName);
        String memberValueName = keyName.substring(memberName.length() + 1, keyName.length() - EMBEDDED_PRROPERTY_SUFIX.length());
        if (member instanceof ISet<?>) {
            // Support only singleMemeberName
            throw new Error("Unsupported Embeded type");
        } else {
            IEntity<?> childIEntity = (IEntity<?>) member;
            if (memberValueName.endsWith(EMBEDDED_PRROPERTY_SUFIX)) {
                setEmbededIEntityValue(childIEntity, memberValueName, value, retrievedMap);
            } else {
                childIEntity.setMemberValue(memberValueName, deserializeValue(childIEntity, memberValueName, value, retrievedMap));
            }
        }
    }

    private void updateIEntity(IEntity<?> iEntity, Entity entity, Map<Key, IEntity<?>> retrievedMap) {
        iEntity.setPrimaryKey(entity.getKey().getId());
        for (Map.Entry<String, Object> me : entity.getProperties().entrySet()) {
            Object value = me.getValue();
            String keyName = me.getKey();

            if (keyName.endsWith(SECONDARY_PRROPERTY_SUFIX)) {
                continue;
            } else if (keyName.endsWith(EMBEDDED_PRROPERTY_SUFIX)) {
                // Recursive child values
                setEmbededIEntityValue(iEntity, keyName, value, retrievedMap);
                continue;
            } else if (value instanceof Text) {
                value = ((Text) value).getValue();
            } else if (value instanceof Key) {
                IEntity<?> childIEntity = (IEntity<?>) iEntity.getMember(keyName);
                if (childIEntity.getMeta().isDetached()) {
                    childIEntity.setPrimaryKey(((Key) value).getId());
                } else {
                    retrieveEntity(childIEntity, (Key) value, retrievedMap);
                }
                continue;
            } else if (value instanceof String) {
                Class<?> cls = iEntity.getEntityMeta().getMemberMeta(keyName).getValueClass();
                if (Enum.class.isAssignableFrom(cls)) {
                    value = Enum.valueOf((Class<Enum>) cls, (String) value);
                }
            } else if (value instanceof Long) {
                if (Integer.class.isAssignableFrom(iEntity.getEntityMeta().getMemberMeta(keyName).getValueClass())) {
                    value = ((Long) value).intValue();
                }
            } else if (value instanceof List<?>) {
                IObject<?, ?> member = iEntity.getMember(keyName);
                if (member.getMeta().isEmbedded()) {
                    // We Support only single MemeberName !
                    String singleMemeberName = null;
                    for (Object valueItem : (List) value) {
                        IEntity<?> childIEntity = EntityFactory.create((Class<IEntity<?>>) member.getMeta().getValueClass());
                        if (singleMemeberName == null) {
                            singleMemeberName = childIEntity.getEntityMeta().getMemberNames().iterator().next();
                        }
                        childIEntity.setMemberValue(singleMemeberName, deserializeValue(childIEntity, singleMemeberName, valueItem, retrievedMap));
                        ((ISet) member).add(childIEntity);
                    }
                    continue;
                } else if (member instanceof ISet<?>) {
                    for (Key childKey : (List<Key>) value) {
                        IEntity<?> childIEntity = EntityFactory.create((Class<IEntity<?>>) member.getMeta().getValueClass());
                        if (member.getMeta().isDetached()) {
                            childIEntity.setPrimaryKey(childKey.getId());
                        } else {
                            retrieveEntity(childIEntity, childKey, retrievedMap);
                        }
                        ((ISet) member).add(childIEntity);
                    }
                    continue;
                } else if (member instanceof IList<?>) {
                    // retrieve order  and sort by this order
                    List<Long> childKeysOrder;
                    childKeysOrder = (List<Long>) readObject((Blob) entity.getProperty(keyName + SECONDARY_PRROPERTY_SUFIX));
                    if (childKeysOrder != null) {
                        Collections.sort(((List<Key>) value), new KeyComparator(childKeysOrder));
                    }

                    for (Key childKey : (List<Key>) value) {
                        IEntity<?> childIEntity = EntityFactory.create((Class<IEntity<?>>) member.getMeta().getValueClass());
                        if (member.getMeta().isDetached()) {
                            childIEntity.setPrimaryKey(childKey.getId());
                        } else {
                            retrieveEntity(childIEntity, childKey, retrievedMap);
                        }
                        ((IList) member).add(childIEntity);
                    }
                    continue;
                } else if (member instanceof IPrimitiveSet<?>) {
                    ((IPrimitiveSet) member).addAll((Collection) value);
                    continue;
                }
            } else if (value instanceof Blob) {
                value = ((Blob) value).getBytes();
                //TODO support more types.
            }
            iEntity.setMemberValue(keyName, value);
        }
    }

    private static class KeyComparator implements Comparator<Key> {

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

    private void retrieveEntity(IEntity<?> iEntity, Key key, Map<Key, IEntity<?>> retrievedMap) {
        if (!getIEntityKind(iEntity).equals(key.getKind())) {
            throw new RuntimeException("Unexpected IEntity " + getIEntityKind(iEntity) + " Kind " + key.getKind());
        }
        if (retrievedMap.containsKey(key)) {
            iEntity.setValue(retrievedMap.get(key).getValue());
        } else {
            Entity entity;
            try {
                datastoreCallStats.get().count++;
                entity = datastore.get(key);
            } catch (EntityNotFoundException e) {
                throw new RuntimeException("EntityNotFound");
            }
            updateIEntity(iEntity, entity, retrievedMap);
            retrievedMap.put(key, iEntity);
        }
    }

    @Override
    public <T extends IEntity<?>> T retrieve(Class<T> entityClass, long primaryKey) {
        long start = System.nanoTime();
        T iEntity = EntityFactory.create(entityClass);
        if (iEntity.getEntityMeta().isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Key key = KeyFactory.createKey(iEntity.getEntityMeta().getPersistenceName(), primaryKey);
        Entity entity;
        try {
            datastoreCallStats.get().count++;
            entity = datastore.get(key);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("EntityNotFound");
        }

        updateIEntity(iEntity, entity, new HashMap<Key, IEntity<?>>());

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
            return KeyFactory.createKey(((IEntity<?>) value).getEntityMeta().getPersistenceName(), ((IEntity<?>) value).getPrimaryKey());
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

    private <T extends IEntity<?>> Query buildQuery(EntityMeta entityMeta, EntityCriteria<T> criteria) {
        Query query = new Query(entityMeta.getPersistenceName());
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
    public <T extends IEntity<?>> T retrieve(EntityCriteria<T> criteria) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().count;
        Class<T> entityClass = entityClass(criteria);
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        datastoreCallStats.get().count++;
        PreparedQuery pq = datastore.prepare(query);
        pq.asIterable(FetchOptions.Builder.withLimit(1));

        Map<Key, IEntity<?>> retrievedMap = new HashMap<Key, IEntity<?>>();
        T iEntity = null;
        Iterator<Entity> iterable = pq.asIterable().iterator();
        if (iterable.hasNext()) {
            Entity entity = iterable.next();
            iEntity = EntityFactory.create(entityClass);
            updateIEntity(iEntity, entity, retrievedMap);
        }
        long duration = System.nanoTime() - start;
        int callsCount = datastoreCallStats.get().count - initCount;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running query {} took {}ms; calls " + callsCount, criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("query {} took {}ms; calls " + callsCount, criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        }
        return iEntity;
    }

    @Override
    public <T extends IEntity<?>> List<T> query(EntityCriteria<T> criteria) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().count;
        Class<T> entityClass = entityClass(criteria);
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        datastoreCallStats.get().count++;
        PreparedQuery pq = datastore.prepare(query);

        Map<Key, IEntity<?>> retrievedMap = new HashMap<Key, IEntity<?>>();
        List<T> rc = new Vector<T>();
        for (Entity entity : pq.asIterable()) {
            T iEntity = EntityFactory.create(entityClass);
            updateIEntity(iEntity, entity, retrievedMap);
            rc.add(iEntity);
        }
        long duration = System.nanoTime() - start;
        int callsCount = datastoreCallStats.get().count - initCount;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running query {} took {}ms; calls " + callsCount, criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("query {} took {}ms; calls " + callsCount, criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        }
        return rc;
    }

    public <T extends IEntity<?>> List<String> queryKeys(EntityCriteria<T> criteria) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().count;
        Class<T> entityClass = entityClass(criteria);
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        query.setKeysOnly();
        datastoreCallStats.get().count++;
        PreparedQuery pq = datastore.prepare(query);

        List<String> rc = new Vector<String>();
        for (Entity entity : pq.asIterable()) {
            rc.add(KeyFactory.keyToString(entity.getKey()));
        }
        long duration = System.nanoTime() - start;
        int callsCount = datastoreCallStats.get().count - initCount;
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
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        query.setKeysOnly();
        datastoreCallStats.get().count++;
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
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't delete Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        query.setKeysOnly();
        datastoreCallStats.get().count++;
        PreparedQuery pq = datastore.prepare(query);

        List<Key> keys = new Vector<Key>();
        for (Entity entity : pq.asIterable()) {
            keys.add(entity.getKey());
        }
        datastoreCallStats.get().count++;
        datastore.delete(keys);
    }
}
