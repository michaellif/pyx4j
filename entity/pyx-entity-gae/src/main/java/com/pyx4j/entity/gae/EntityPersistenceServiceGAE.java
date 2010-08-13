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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.Text;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.security.MemberModificationAdapter;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.IndexString;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;

/**
 * 
 * @see PersistenceServicesFactory.GAE_IMPL_CLASS
 * 
 */
public class EntityPersistenceServiceGAE implements IEntityPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(EntityPersistenceServiceGAE.class);

    private static I18n i18n = I18nFactory.getI18n();

    private final int ORDINARY_STRING_LENGHT_MAX = 500;

    private static final String SECONDARY_PRROPERTY_SUFIX = "-s";

    private static final String EMBEDDED_PRROPERTY_SUFIX = "-e";

    private static final String GLOBAL_KEYWORD_PRROPERTY = "keys" + SECONDARY_PRROPERTY_SUFIX;

    private final DatastoreService datastore;

    private final ThreadLocal<CallStats> datastoreCallStats = new ThreadLocal<CallStats>() {

        @Override
        protected CallStats initialValue() {
            return new CallStats();
        }

    };

    private static class CallStats {

        int readCount;

        int writeCount;
    }

    private static class EntityUpdateWrapper {

        boolean isUpdate;

        Entity entity;

        boolean updated;

        Object lastValue;

        EntityUpdateWrapper(Entity entity, boolean isUpdate) {
            this.entity = entity;
            this.isUpdate = isUpdate;
            if (!isUpdate) {
                updated = true;
            }
        }

        public Object getProperty(String propertyName) {
            return entity.getProperty(propertyName);
        }

        private static boolean equals(Object value1, Object value2) {
            if ((value1 instanceof Integer) || (value2 instanceof Integer)) {
                if (value1 == null || value2 == null) {
                    return false;
                }
                return ((Number) value1).longValue() == ((Number) value2).longValue();
            } else {
                return EqualsHelper.equals(value1, value2);
            }
        }

        public boolean setProperty(String propertyName, boolean indexed, Object value) {
            if (isUpdate) {
                if (!equals(value, lastValue = entity.getProperty(propertyName))) {
                    if (indexed) {
                        entity.setProperty(propertyName, value);
                    } else {
                        entity.setUnindexedProperty(propertyName, value);
                    }
                    //                    log.debug("data change " + propertyName + " [{}] -> [{}]", lastValue, value);
                    //                    log.debug("data type change " + propertyName + " [{}] -> [{}]", (lastValue == null) ? "null" : lastValue.getClass(),
                    //                            (value == null) ? "null" : value.getClass());
                    updated = true;
                    return true;
                } else {
                    return false;
                }
            } else {
                if (indexed) {
                    entity.setProperty(propertyName, value);
                } else {
                    entity.setUnindexedProperty(propertyName, value);
                }
                return true;
            }
        }

        public boolean removeProperty(String propertyName) {
            if (isUpdate) {
                if ((lastValue = entity.getProperty(propertyName)) != null) {
                    entity.removeProperty(propertyName);
                    updated = true;
                    return true;
                } else {
                    return false;
                }
            } else {
                entity.removeProperty(propertyName);
                return true;
            }
        }
    }

    public EntityPersistenceServiceGAE() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    private String degradeGracefullyMessage() {
        return i18n.tr("Application is in read-only due to short maintenance.\nPlease try again in one hour");
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

    //TODO support readonly values
    private void embedEntityProperties(EntityUpdateWrapper entity, String prefix, String sufix, IEntity childIEntity, boolean parentIndexed) {
        if (childIEntity.isNull()) {
            // remove all properties
            EntityMeta em = childIEntity.getEntityMeta();
            for (String memberName : em.getMemberNames()) {
                MemberMeta memberMeta = em.getMemberMeta(memberName);
                if ((memberMeta.isEntity()) && (memberMeta.isEmbedded())) {
                    embedEntityProperties(entity, prefix + "_" + memberName, sufix + EMBEDDED_PRROPERTY_SUFIX, (IEntity) childIEntity.getMember(memberName),
                            parentIndexed && memberMeta.isIndexed());
                } else {
                    entity.removeProperty(prefix + "_" + memberName + sufix + EMBEDDED_PRROPERTY_SUFIX);
                }
            }
            return;
        }
        nextValue: for (Map.Entry<String, Object> me : childIEntity.getValue().entrySet()) {
            if (me.getKey().equals(IEntity.PRIMARY_KEY)) {
                continue nextValue;
            }
            MemberMeta meta = childIEntity.getEntityMeta().getMemberMeta(me.getKey());
            if (meta.isTransient()) {
                continue nextValue;
            }
            String propertyName = prefix + "_" + meta.getFieldName() + sufix + EMBEDDED_PRROPERTY_SUFIX;
            Object value = me.getValue();
            if (IEntity.class.isAssignableFrom(meta.getObjectClass())) {
                if (meta.isEmbedded()) {
                    embedEntityProperties(entity, prefix + "_" + me.getKey(), sufix + EMBEDDED_PRROPERTY_SUFIX, (IEntity) childIEntity.getMember(me.getKey()),
                            parentIndexed);
                    continue nextValue;
                } else {
                    String kind = EntityFactory.getEntityMeta((Class<? extends IEntity>) meta.getObjectClass()).getPersistenceName();
                    value = KeyFactory.createKey(kind, (Long) ((Map) value).get(IEntity.PRIMARY_KEY));
                }
            } else {
                value = convertToGAEValue(value, entity, propertyName, meta, parentIndexed);
            }
            //TODO Allow to embed other types

            entity.setProperty(propertyName, parentIndexed && meta.isIndexed(), value);
        }
    }

    private Object convertToGAEValue(Object value, EntityUpdateWrapper entity, String propertyName, MemberMeta meta, boolean parentIndexed) {
        Indexed index = null;
        if (parentIndexed) {
            index = meta.getAnnotation(Indexed.class);
        }
        if (value instanceof String) {
            if (meta.getStringLength() > ORDINARY_STRING_LENGHT_MAX) {
                return new Text((String) value);
            } else {
                if ((index != null) && (index.keywordLenght() > 0)) {
                    if (index.global() != 0) {
                        addGloablIndex(entity, index.global(), createStringKeywordIndex(index.keywordLenght(), (String) value));
                    } else {
                        entity.setProperty(getIndexedPropertyName(propertyName), true, createStringKeywordIndex(index.keywordLenght(), (String) value));
                    }
                }
                return value;
            }
        } else if (value instanceof Enum<?>) {
            if ((index != null) && (index.global() != 0)) {
                addGloablIndex(entity, index.global(), ((Enum<?>) value).name());
            }
            return ((Enum<?>) value).name();
        } else if (IPrimitiveSet.class.isAssignableFrom(meta.getObjectClass())) {
            if (Enum.class.isAssignableFrom(meta.getValueClass())) {
                Set<String> gValue = new HashSet<String>();
                for (Enum v : (Set<Enum>) value) {
                    gValue.add(v.name());
                }
                if ((index != null) && (index.global() != 0)) {
                    addGloablIndex(entity, index.global(), gValue);
                }
                return gValue;
            } else {
                return value;
            }
        } else if (value instanceof Date) {
            if (value instanceof java.sql.Date) {
                value = new Date(((Date) value).getTime());
            }
            if (index != null) {
                // TODO move values like month and week
                Date v = TimeUtils.dayStart((Date) value);
                entity.setProperty(getIndexedPropertyName(propertyName), true, v);
            }
            return value;
        } else if (value instanceof GeoPoint) {
            GeoPoint geoPoint = (GeoPoint) value;
            if (index != null) {
                entity.setProperty(getIndexedPropertyName(propertyName), true, geoPoint.getCells());
            }
            return new GeoPt((float) geoPoint.getLat(), (float) geoPoint.getLng());
        } else if (value != null) {
            if (value.getClass().isArray()) {
                //TODO support more arrays
                return new Blob((byte[]) value);
            } else {
                return value;
            }
        } else {
            return value;
        }
    }

    private void updateEntityProperties(EntityUpdateWrapper entity, IEntity iEntity, boolean merge, boolean isUpdate) {
        if (iEntity.isNull()) {
            return;
        }
        nextValue: for (Map.Entry<String, Object> me : iEntity.getValue().entrySet()) {
            if (me.getKey().equals(IEntity.PRIMARY_KEY)) {
                continue nextValue;
            }
            MemberMeta meta = iEntity.getEntityMeta().getMemberMeta(me.getKey());
            String propertyName = meta.getFieldName();
            if (meta.isTransient()) {
                continue nextValue;
            }
            Object value = me.getValue();
            if (value instanceof Map<?, ?>) {
                if (!meta.isEntity()) {
                    throw new Error("Saving non persisted value " + meta.getCaption());
                } else if (meta.isOwnedRelationships()) {
                    // Save Owned iEntity
                    IEntity childIEntity = (IEntity) iEntity.getMember(me.getKey());
                    if (meta.isEmbedded()) {
                        embedEntityProperties(entity, me.getKey(), "", childIEntity, meta.isIndexed());
                        continue nextValue;
                    } else {
                        value = persistImpl(childIEntity, merge);
                        // Cascade delete
                        if (isUpdate && merge) {
                            Object origValue = entity.getProperty(propertyName);
                            if ((origValue != null) && (origValue.equals(value))) {
                                datastoreCallStats.get().writeCount++;
                                datastore.delete((Key) origValue);
                            }
                        }
                    }
                } else {
                    Long childKeyId = (Long) ((Map<String, Object>) value).get(IEntity.PRIMARY_KEY);
                    if (childKeyId == null) {
                        log.error("Saving non persisted reference {}", iEntity.getMember(me.getKey()));
                        throw new Error("Saving non persisted reference " + meta.getCaption());
                    }
                    value = KeyFactory
                            .createKey(EntityFactory.getEntityMeta((Class<? extends IEntity>) meta.getObjectClass()).getPersistenceName(), childKeyId);
                }
            } else if ((ISet.class.isAssignableFrom(meta.getObjectClass())) && (value instanceof Set<?>)) {
                Set<Key> childKeys = new HashSet<Key>();
                if (meta.isOwnedRelationships()) {
                    // Save Owned iEntity
                    ISet<IEntity> memberSet = (ISet<IEntity>) iEntity.getMember(me.getKey());
                    if (meta.isEmbedded()) {
                        Set<Object> childValue = new HashSet<Object>();
                        String singleMemeberName = null;
                        for (IEntity childIEntity : memberSet) {
                            if (singleMemeberName == null) {
                                singleMemeberName = childIEntity.getEntityMeta().getMemberNames().iterator().next();
                            }
                            childValue.add(childIEntity.getMemberValue(singleMemeberName));
                        }
                        value = childValue;
                    } else {
                        for (IEntity childIEntity : memberSet) {
                            Key key = persistImpl(childIEntity, merge);
                            childKeys.add(key);
                        }
                        value = childKeys;
                    }

                    // Cascade delete
                    if (isUpdate && merge) {
                        Object origValue = entity.getProperty(propertyName);
                        if (origValue != null) {
                            Vector<Key> removedChildKeys = new Vector<Key>();
                            for (Key childKey : (List<Key>) origValue) {
                                if (!childKeys.contains(childKey)) {
                                    removedChildKeys.add(childKey);
                                }
                            }
                            if (removedChildKeys.size() > 0) {
                                datastoreCallStats.get().writeCount++;
                                datastore.delete(removedChildKeys);
                            }
                        }
                    }

                } else {
                    for (Object el : (Set<?>) value) {
                        Long childKey = (Long) ((Map<String, Object>) el).get(IEntity.PRIMARY_KEY);
                        if (childKey == null) {
                            throw new Error("Saving non persisted reference " + meta.getCaption());
                        }
                        childKeys.add(KeyFactory.createKey(EntityFactory.getEntityMeta((Class<? extends IEntity>) meta.getValueClass()).getPersistenceName(),
                                childKey));
                    }
                    value = childKeys;
                }
            } else if ((IList.class.isAssignableFrom(meta.getObjectClass())) && (value instanceof List<?>)) {
                Set<Key> childKeys = new HashSet<Key>();
                Vector<Long> childKeysOrder = new Vector<Long>();
                if (meta.isOwnedRelationships()) {
                    // Save Owned iEntity
                    IList<IEntity> memberList = (IList<IEntity>) iEntity.getMember(me.getKey());
                    for (IEntity childIEntity : memberList) {
                        Key key = persistImpl(childIEntity, merge);
                        childKeys.add(key);
                        childKeysOrder.add(key.getId());
                    }

                    // Cascade delete
                    if (isUpdate && merge) {
                        Object origValue = entity.getProperty(propertyName);
                        if (origValue != null) {
                            Vector<Key> removedChildKeys = new Vector<Key>();
                            for (Key childKey : (List<Key>) origValue) {
                                if (!childKeys.contains(childKey)) {
                                    removedChildKeys.add(childKey);
                                }
                            }
                            if (removedChildKeys.size() > 0) {
                                datastoreCallStats.get().writeCount++;
                                datastore.delete(removedChildKeys);
                            }
                        }
                    }

                } else {
                    for (Object el : (List<?>) value) {
                        Long childKey = (Long) ((Map<String, Object>) el).get(IEntity.PRIMARY_KEY);
                        if (childKey == null) {
                            throw new Error("Saving non persisted reference " + meta.getCaption());
                        }
                        Key key = KeyFactory.createKey(EntityFactory.getEntityMeta((Class<? extends IEntity>) meta.getValueClass()).getPersistenceName(),
                                childKey);
                        childKeys.add(key);
                        childKeysOrder.add(childKey);
                    }
                }
                entity.setProperty(me.getKey() + SECONDARY_PRROPERTY_SUFIX, false, createBlob(childKeysOrder));
                value = childKeys;
            } else {
                value = convertToGAEValue(value, entity, propertyName, meta, true);
            }

            if (entity.setProperty(propertyName, meta.isIndexed(), value) && entity.isUpdate) {
                if (meta.getAnnotation(ReadOnly.class) != null) {
                    log.error("Changing readonly property [{}] -> [{}]", entity.lastValue, value);
                    throw new Error("Changing readonly property " + meta.getCaption() + " of " + iEntity.getEntityMeta().getCaption());
                }
                MemberColumn memberColumn = meta.getAnnotation(MemberColumn.class);
                if (memberColumn != null && memberColumn.modificationAdapter() != null) {
                    MemberModificationAdapter adapter;
                    try {
                        adapter = memberColumn.modificationAdapter().newInstance();
                    } catch (InstantiationException e) {
                        throw new Error(e);
                    } catch (IllegalAccessException e) {
                        throw new Error(e);
                    }
                    if (!adapter.allowModifications(iEntity, meta, entity.lastValue, value)) {
                        log.error("Forbiden change [{}] -> [{}]", entity.lastValue, value);
                        throw new Error("Forbiden change " + meta.getCaption() + " of " + iEntity.getEntityMeta().getCaption());
                    }
                }
            }
        }

        // Special case for values not present in Map, e.g. owner reference
        for (String memberName : iEntity.getEntityMeta().getBidirectionalReferenceMemberNames()) {
            MemberMeta meta = iEntity.getEntityMeta().getMemberMeta(memberName);
            IEntity ownerEntity = (IEntity) iEntity.getMember(memberName);
            if (ownerEntity.isNull()) {
                continue;
            }
            Object ownerId = ownerEntity.getPrimaryKey();
            if (ownerId == null) {
                throw new Error("Saving non persisted reference " + ownerEntity.getEntityMeta().getCaption());
            }
            Key ownerKey = KeyFactory.createKey(EntityFactory.getEntityMeta(ownerEntity.getValueClass()).getPersistenceName(), (Long) ownerId);
            entity.setProperty(memberName, meta.isIndexed(), ownerKey);
        }
    }

    private void addGloablIndex(EntityUpdateWrapper entity, char prefix, Set<String> newKeys) {
        Set<String> keys = (Set<String>) entity.getProperty(GLOBAL_KEYWORD_PRROPERTY);
        if (keys == null) {
            keys = new HashSet<String>();
        }
        for (String key : newKeys) {
            keys.add(prefix + key);
        }
        entity.setProperty(GLOBAL_KEYWORD_PRROPERTY, true, keys);
    }

    private void addGloablIndex(EntityUpdateWrapper entity, char prefix, String newKey) {
        Set<String> keys = (Set<String>) entity.getProperty(GLOBAL_KEYWORD_PRROPERTY);
        if (keys == null) {
            keys = new HashSet<String>();
        }
        keys.add(prefix + newKey);
        entity.setProperty(GLOBAL_KEYWORD_PRROPERTY, true, keys);
    }

    private String getIndexedPropertyName(String propertyName) {
        return propertyName + SECONDARY_PRROPERTY_SUFIX;
    }

    /**
     * We store the indexed values in the same entity
     */
    @Override
    public String getIndexedPropertyName(EntityMeta meta, Path path) {
        Indexed index = meta.getMemberMeta(path).getAnnotation(Indexed.class);
        if ((index != null) && (index.global() != 0)) {
            return GLOBAL_KEYWORD_PRROPERTY;
        }

        StringBuilder propertyName = new StringBuilder();
        final int pathLength = path.getPathMembers().size();
        EntityMeta em = meta;
        MemberMeta mm = null;
        int count = 0;
        int embeddedCount = 0;
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
                } else {
                    embeddedCount++;
                }
                propertyName.append("_");
            }
        }
        while (embeddedCount > 0) {
            embeddedCount--;
            propertyName.append(EMBEDDED_PRROPERTY_SUFIX);
        }
        propertyName.append(SECONDARY_PRROPERTY_SUFIX);
        return propertyName.toString();
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

    private Set<String> createStringKeywordIndex(int keywordLenght, String value) {
        return IndexString.getIndexKeys(keywordLenght, value);
    }

    private String getIEntityKind(IEntity iEntity) {
        return iEntity.getEntityMeta().getPersistenceName();
    }

    @Override
    public void persist(IEntity iEntity) {
        persistImpl(iEntity, false);
    }

    @Override
    public void merge(IEntity iEntity) {
        persistImpl(iEntity, true);
    }

    private boolean isBidirectionalReferenceRequired(IEntity iEntity) {
        EntityMeta entityMeta = iEntity.getEntityMeta();
        for (String memberName : entityMeta.getMemberNames()) {
            MemberMeta meta = entityMeta.getMemberMeta(memberName);
            if (IEntity.class.isAssignableFrom(meta.getValueClass())) {
                EntityMeta childEntityMeta = EntityFactory.getEntityMeta((Class<? extends IEntity>) meta.getValueClass());
                for (String childMemberName : childEntityMeta.getBidirectionalReferenceMemberNames()) {
                    if (childEntityMeta.getMemberMeta(childMemberName).getValueClass().equals(entityMeta.getEntityClass())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private EntityUpdateWrapper createEntity(IEntity iEntity, boolean merge) {
        EntityMeta entityMeta = iEntity.getEntityMeta();
        if (entityMeta.isTransient()) {
            throw new Error("Can't persist Transient Entity");
        }
        Entity entity;
        boolean isUpdate = true;
        if (iEntity.getPrimaryKey() == null) {
            Table tableAnnotation = entityMeta.getEntityClass().getAnnotation(Table.class);
            if ((tableAnnotation != null) && (tableAnnotation.primaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED)) {
                throw new Error("Can't persist Entity without assigned PK");
            }
            isUpdate = false;
            if (isBidirectionalReferenceRequired(iEntity)) {
                datastoreCallStats.get().readCount++;
                entity = new Entity(datastore.allocateIds(getIEntityKind(iEntity), 1).getStart());
                iEntity.setPrimaryKey(entity.getKey().getId());
            } else {
                entity = new Entity(getIEntityKind(iEntity));
            }
            String createdTs = entityMeta.getCreatedTimestampMember();
            if (createdTs != null) {
                iEntity.setMemberValue(createdTs, new Date());
            }
        } else {
            Key key = KeyFactory.createKey(getIEntityKind(iEntity), iEntity.getPrimaryKey());
            if (merge) {
                try {
                    datastoreCallStats.get().readCount++;
                    entity = datastore.get(key);
                } catch (EntityNotFoundException e) {
                    throw new RuntimeException("EntityNotFound");
                }
            } else {
                entity = new Entity(key);
            }
        }
        EntityUpdateWrapper entityUpdateWrapper = new EntityUpdateWrapper(entity, merge && isUpdate);
        String updatedTs = entityMeta.getUpdatedTimestampMember();
        if (updatedTs != null) {
            iEntity.setMemberValue(updatedTs, new Date());
        }
        updateEntityProperties(entityUpdateWrapper, iEntity, merge, isUpdate);

        return entityUpdateWrapper;
    }

    private Key persistImpl(IEntity iEntity, boolean merge) {
        EntityUpdateWrapper entity = createEntity(iEntity, merge);
        if (!entity.updated) {
            // no update required
            return entity.entity.getKey();
        } else {
            datastoreCallStats.get().writeCount++;
            try {
                Key keyCreated = datastore.put(entity.entity);
                iEntity.setPrimaryKey(keyCreated.getId());

                return keyCreated;
            } catch (com.google.apphosting.api.ApiProxy.CapabilityDisabledException e) {
                throw new UnRecoverableRuntimeException(degradeGracefullyMessage());
            }
        }
    }

    @Override
    public <T extends IEntity> void persist(Iterable<T> entityIterable) {
        try {
            List<Entity> entityList = new Vector<Entity>();
            for (IEntity iEntity : entityIterable) {
                if (entityList.size() >= 500) {
                    datastoreCallStats.get().writeCount++;
                    datastore.put(entityList);
                    entityList.clear();
                }
                EntityUpdateWrapper entity = createEntity(iEntity, false);
                entityList.add(entity.entity);
            }
            datastoreCallStats.get().writeCount++;
            datastore.put(entityList);
        } catch (com.google.apphosting.api.ApiProxy.CapabilityDisabledException e) {
            throw new UnRecoverableRuntimeException(degradeGracefullyMessage());
        }
    }

    private Object deserializeValue(IEntity iEntity, String keyName, Object value, Map<Key, IEntity> retrievedMap) {
        if (value instanceof Text) {
            return ((Text) value).getValue();
        } else if (value instanceof Key) {
            IEntity childIEntity = (IEntity) iEntity.getMember(keyName);
            if (childIEntity.getMeta().isDetached()) {
                childIEntity.setPrimaryKey(((Key) value).getId());
            } else {
                if ((iEntity.getOwner() != null) && (iEntity.getEntityMeta().getMemberMeta(keyName).isOwner())) {
                    // Do not retrieve Owner
                } else {
                    retrieveEntity(childIEntity, (Key) value, retrievedMap);
                }
            }
            return childIEntity.getValue();
        } else if (value instanceof String) {
            Class<?> cls = iEntity.getEntityMeta().getMemberMeta(keyName).getValueClass();
            if (Enum.class.isAssignableFrom(cls)) {
                return Enum.valueOf((Class<Enum>) cls, (String) value);
            } else {
                return value;
            }
        } else if (value instanceof Date) {
            Class<?> cls = iEntity.getEntityMeta().getMemberMeta(keyName).getValueClass();
            if (cls.equals(java.sql.Date.class)) {
                return new java.sql.Date(((Date) value).getTime());
            } else {
                return value;
            }
        } else if (value instanceof Long) {
            if (Integer.class.isAssignableFrom(iEntity.getEntityMeta().getMemberMeta(keyName).getValueClass())) {
                return ((Long) value).intValue();
            } else {
                return value;
            }
        } else if (value instanceof Blob) {
            //TODO support more types.
            return ((Blob) value).getBytes();
        } else if (value instanceof GeoPt) {
            return new GeoPoint(((GeoPt) value).getLatitude(), ((GeoPt) value).getLongitude());
        } else {
            return value;
        }
    }

    /**
     * Recursive set child values
     */
    private void setEmbededIEntityValue(IEntity iEntity, String keyName, Object value, Map<Key, IEntity> retrievedMap) {
        String memberName = keyName.substring(0, keyName.indexOf('_'));
        IObject<?> member = iEntity.getMember(memberName);
        String memberValueName = keyName.substring(memberName.length() + 1, keyName.length() - EMBEDDED_PRROPERTY_SUFIX.length());
        if (member instanceof ISet<?>) {
            // Support only singleMemeberName
            throw new Error("Unsupported Embeded type");
        } else {
            IEntity childIEntity = (IEntity) member;
            if (memberValueName.endsWith(EMBEDDED_PRROPERTY_SUFIX)) {
                setEmbededIEntityValue(childIEntity, memberValueName, value, retrievedMap);
            } else {
                childIEntity.setMemberValue(memberValueName, deserializeValue(childIEntity, memberValueName, value, retrievedMap));
            }
        }
    }

    // Make only one call to DB to get the list
    private void retrieveChildEntityCollection(ICollection member, List<Key> value, Map<Key, IEntity> retrievedMap) {
        if (member.getMeta().isDetached()) {
            for (Key childKey : value) {
                IEntity childIEntity = EntityFactory.create((Class<IEntity>) member.getMeta().getValueClass());
                childIEntity.setPrimaryKey(childKey.getId());
                member.add(childIEntity);
            }
        } else {
            List<Key> needToGet = new Vector<Key>();
            EntityMeta entityMeta = EntityFactory.getEntityMeta((Class<IEntity>) member.getMeta().getValueClass());
            for (Key childKey : value) {
                if (!entityMeta.getPersistenceName().equals(childKey.getKind())) {
                    throw new RuntimeException("Unexpected IEntity " + entityMeta.getPersistenceName() + " Kind " + childKey.getKind());
                }
                if (!retrievedMap.containsKey(childKey)) {
                    needToGet.add(childKey);
                }
            }

            datastoreCallStats.get().readCount++;
            Map<Key, Entity> gotData = datastore.get(needToGet);
            for (Map.Entry<Key, Entity> me : gotData.entrySet()) {
                IEntity childIEntity = EntityFactory.create(entityMeta.getEntityClass());
                retrievedMap.put(me.getKey(), childIEntity);
                updateIEntity(childIEntity, me.getValue(), retrievedMap);
            }

            for (Key childKey : value) {
                IEntity childIEntity = retrievedMap.get(childKey);
                if (childIEntity == null) {
                    throw new RuntimeException("Entity " + childKey.getKind() + " " + childKey.getId() + " NotFound");
                }
                member.add(childIEntity);
            }
        }
    }

    private void updateIEntity(IEntity iEntity, Entity entity, Map<Key, IEntity> retrievedMap) {
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
            } else if (value instanceof List<?>) {
                IObject<?> member = iEntity.getMember(keyName);
                if (member.getMeta().isEmbedded()) {
                    // We Support only single MemeberName !
                    String singleMemeberName = null;
                    for (Object valueItem : (List) value) {
                        IEntity childIEntity = EntityFactory.create((Class<IEntity>) member.getMeta().getValueClass());
                        if (singleMemeberName == null) {
                            singleMemeberName = childIEntity.getEntityMeta().getMemberNames().iterator().next();
                        }
                        childIEntity.setMemberValue(singleMemeberName, deserializeValue(childIEntity, singleMemeberName, valueItem, retrievedMap));
                        ((ISet) member).add(childIEntity);
                    }
                    continue;
                } else if (member instanceof ISet<?>) {
                    retrieveChildEntityCollection(((ISet) member), (List<Key>) value, retrievedMap);
                    continue;
                } else if (member instanceof IList<?>) {
                    // retrieve order  and sort by this order
                    List<Long> childKeysOrder;
                    childKeysOrder = (List<Long>) readObject((Blob) entity.getProperty(keyName + SECONDARY_PRROPERTY_SUFIX));
                    if (childKeysOrder != null) {
                        Collections.sort(((List<Key>) value), new KeyComparator(childKeysOrder));
                    }
                    retrieveChildEntityCollection(((IList) member), (List<Key>) value, retrievedMap);
                    continue;
                } else if (member instanceof IPrimitiveSet<?>) {
                    if (Enum.class.isAssignableFrom(member.getMeta().getValueClass())) {
                        Class<Enum> cls = (Class<Enum>) member.getMeta().getValueClass();
                        for (String v : (Collection<String>) value) {
                            Enum eValue = Enum.valueOf(cls, v);
                            ((IPrimitiveSet) member).add(eValue);
                        }
                    } else {
                        ((IPrimitiveSet) member).addAll((Collection) value);
                    }
                    continue;
                }
            } else {
                value = deserializeValue(iEntity, keyName, value, retrievedMap);
            }
            iEntity.setMemberValue(keyName, value);
        }
    }

    @SuppressWarnings("serial")
    private static class KeyComparator implements Comparator<Key>, Serializable {

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

    private void retrieveEntity(IEntity iEntity, Key key, Map<Key, IEntity> retrievedMap) {
        if (!getIEntityKind(iEntity).equals(key.getKind())) {
            throw new RuntimeException("Unexpected IEntity " + getIEntityKind(iEntity) + " Kind " + key.getKind());
        }
        if (retrievedMap.containsKey(key)) {
            iEntity.setValue(retrievedMap.get(key).getValue());
        } else {
            Entity entity;
            try {
                datastoreCallStats.get().readCount++;
                entity = datastore.get(key);
            } catch (EntityNotFoundException e) {
                throw new RuntimeException("Entity " + key.getKind() + " " + key.getId() + " NotFound");
            }
            retrievedMap.put(key, iEntity);
            updateIEntity(iEntity, entity, retrievedMap);
        }
    }

    @Override
    public <T extends IEntity> T retrieve(Class<T> entityClass, long primaryKey) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().readCount;

        T iEntity = EntityFactory.create(entityClass);
        if (iEntity.getEntityMeta().isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Key key = KeyFactory.createKey(iEntity.getEntityMeta().getPersistenceName(), primaryKey);
        Entity entity;
        try {
            datastoreCallStats.get().readCount++;
            entity = datastore.get(key);
        } catch (EntityNotFoundException e) {
            log.debug("Entity " + entityClass.getSimpleName() + " " + primaryKey + " " + " NotFound");
            entity = null;
        }

        if (entity != null) {
            updateIEntity(iEntity, entity, new HashMap<Key, IEntity>());
        } else {
            iEntity = null;
        }

        long duration = System.nanoTime() - start;
        int callsCount = datastoreCallStats.get().readCount - initCount;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running retrieve {} took {}ms; calls " + callsCount, entityClass.getName(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("retrieve {} took {}ms; calls " + callsCount, entityClass.getName(), (int) (duration / Consts.MSEC2NANO));
        }
        return iEntity;
    }

    private <T extends IEntity> Class<T> entityClass(EntityQueryCriteria<T> criteria) {
        return ServerEntityFactory.entityClass(criteria.getDomainName());
    }

    private static Query.FilterOperator operator(PropertyCriterion.Restriction restriction) {
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

    @SuppressWarnings("unchecked")
    private static Object datastoreValue(EntityMeta entityMeta, String propertyName, Serializable value) {
        if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name();
        } else if (value instanceof IEntity) {
            return KeyFactory.createKey(((IEntity) value).getEntityMeta().getPersistenceName(), ((IEntity) value).getPrimaryKey());
        } else if (value instanceof Long) {
            if (propertyName.equals(IEntity.PRIMARY_KEY)) {
                return KeyFactory.createKey(entityMeta.getPersistenceName(), (Long) value);
            } else {
                MemberMeta mm = entityMeta.getMemberMeta(propertyName);
                if (mm.isEntity()) {
                    return KeyFactory.createKey(EntityFactory.getEntityMeta((Class<? extends IEntity>) mm.getValueClass()).getPersistenceName(), (Long) value);
                } else {
                    return value;
                }
            }
        } else if (value instanceof Collection<?>) {
            Collection rc;
            if (value instanceof Collection<?>) {
                rc = new Vector();
            } else {
                rc = new HashSet();
            }
            for (Object item : (Collection<?>) value) {
                rc.add(datastoreValue(entityMeta, propertyName, (Serializable) item));
            }
            return rc;
        } else {
            return value;
        }
    }

    private Query.FilterOperator addFilter(Query query, EntityMeta entityMeta, PropertyCriterion propertyCriterion) {
        String propertyName = propertyCriterion.getPropertyName();
        Object value = datastoreValue(entityMeta, propertyName, propertyCriterion.getValue());
        if (propertyName.equals(IEntity.PRIMARY_KEY)) {
            propertyName = Entity.KEY_RESERVED_PROPERTY;
        } else if ((!propertyName.endsWith(SECONDARY_PRROPERTY_SUFIX)) && !entityMeta.getMemberMeta(propertyName).isIndexed()) {
            throw new Error("Query by Unindexed property " + propertyName + " of " + entityMeta.getCaption());
        }
        Query.FilterOperator oprator = operator(propertyCriterion.getRestriction());
        query.addFilter(propertyName, oprator, value);
        return oprator;
    }

    private <T extends IEntity> Query buildQuery(EntityMeta entityMeta, EntityQueryCriteria<T> criteria) {
        Query query = new Query(entityMeta.getPersistenceName());
        boolean allowSort = true;
        int keyFilter = 0;
        if (criteria.getFilters() != null) {
            for (Criterion cr : criteria.getFilters()) {
                if (cr instanceof PropertyCriterion) {
                    if (GLOBAL_KEYWORD_PRROPERTY.equals(((PropertyCriterion) cr).getPropertyName())) {
                        if ((keyFilter >= 2) && allowSort && (criteria.getSorts() != null)) {
                            break;
                        }
                        keyFilter++;
                    }
                    if (addFilter(query, entityMeta, (PropertyCriterion) cr) == Query.FilterOperator.IN) {
                        allowSort = false;
                    }
                }
            }
        }
        if (allowSort && (criteria.getSorts() != null)) {
            for (EntityQueryCriteria.Sort sort : criteria.getSorts()) {
                query.addSort(sort.getPropertyName(), sort.isDescending() ? Query.SortDirection.DESCENDING : Query.SortDirection.ASCENDING);
            }
            log.debug("sort by {}", query.getSortPredicates());
        }

        StringBuilder b = new StringBuilder();
        for (Query.FilterPredicate f : query.getFilterPredicates()) {
            b.append(f.getPropertyName()).append(f.getOperator()).append(f.getValue()).append(" ");
        }
        log.debug("{} search by {}", entityMeta.getPersistenceName(), b);

        return query;
    }

    @Override
    public <T extends IEntity> T retrieve(EntityQueryCriteria<T> criteria) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().readCount;
        Class<T> entityClass = entityClass(criteria);
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        datastoreCallStats.get().readCount++;
        PreparedQuery pq = datastore.prepare(query);
        pq.asIterable(FetchOptions.Builder.withLimit(1));

        Map<Key, IEntity> retrievedMap = new HashMap<Key, IEntity>();
        T iEntity = null;
        Entity entity = pq.asSingleEntity();
        if (entity != null) {
            iEntity = EntityFactory.create(entityClass);
            updateIEntity(iEntity, entity, retrievedMap);
        }
        long duration = System.nanoTime() - start;
        int callsCount = datastoreCallStats.get().readCount - initCount;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running retrieve query {} took {}ms; calls " + callsCount, criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("retrieve query {} took {}ms; calls " + callsCount, criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        }
        return iEntity;
    }

    @Override
    public <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().readCount;
        Class<T> entityClass = entityClass(criteria);
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        datastoreCallStats.get().readCount++;
        PreparedQuery pq = datastore.prepare(query);

        Map<Key, IEntity> retrievedMap = new HashMap<Key, IEntity>();
        List<T> rc = new Vector<T>();
        for (Entity entity : pq.asIterable()) {
            T iEntity = EntityFactory.create(entityClass);
            updateIEntity(iEntity, entity, retrievedMap);
            rc.add(iEntity);
        }
        long duration = System.nanoTime() - start;
        int callsCount = datastoreCallStats.get().readCount - initCount;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running query {} took {}ms; calls " + callsCount, criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("query {} took {}ms; calls " + callsCount, criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        }
        return rc;
    }

    @Override
    public <T extends IEntity> ICursorIterator<T> query(String encodedCursorRefference, EntityQueryCriteria<T> criteria) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().readCount;
        final Class<T> entityClass = entityClass(criteria);
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        datastoreCallStats.get().readCount++;
        PreparedQuery pq = datastore.prepare(query);

        final Map<Key, IEntity> retrievedMap = new HashMap<Key, IEntity>();

        final QueryResultIterable<Entity> iterable;
        if (encodedCursorRefference != null) {
            iterable = pq.asQueryResultIterable(FetchOptions.Builder.withCursor(Cursor.fromWebSafeString(encodedCursorRefference)));
        } else {
            iterable = pq.asQueryResultIterable();
        }
        final QueryResultIterator<Entity> iterator = iterable.iterator();
        long duration = System.nanoTime() - start;
        int callsCount = datastoreCallStats.get().readCount - initCount;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running query iterator {} took {}ms; calls " + callsCount, criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("query iterator {} took {}ms; calls " + callsCount, criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        }

        return new ICursorIterator<T>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                Entity entity = iterator.next();
                T iEntity = EntityFactory.create(entityClass);
                updateIEntity(iEntity, entity, retrievedMap);
                return iEntity;
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public String encodedCursorRefference() {
                return iterator.getCursor().toWebSafeString();
            }
        };
    }

    @Override
    public <T extends IEntity> List<Long> queryKeys(EntityQueryCriteria<T> criteria) {
        long start = System.nanoTime();
        Class<T> entityClass = entityClass(criteria);
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        query.setKeysOnly();
        datastoreCallStats.get().readCount++;
        PreparedQuery pq = datastore.prepare(query);

        List<Long> rc = new Vector<Long>();
        for (Entity entity : pq.asIterable()) {
            rc.add(entity.getKey().getId());
        }
        long duration = System.nanoTime() - start;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running queryKeys {} took {}ms", criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("queryKeys {} took {}ms", criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        }
        return rc;
    }

    @Override
    public <T extends IEntity> ICursorIterator<Long> queryKeys(String encodedCursorRefference, EntityQueryCriteria<T> criteria) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().readCount;
        final Class<T> entityClass = entityClass(criteria);
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        query.setKeysOnly();
        datastoreCallStats.get().readCount++;
        PreparedQuery pq = datastore.prepare(query);

        final QueryResultIterable<Entity> iterable;
        if (encodedCursorRefference != null) {
            iterable = pq.asQueryResultIterable(FetchOptions.Builder.withCursor(Cursor.fromWebSafeString(encodedCursorRefference)));
        } else {
            iterable = pq.asQueryResultIterable();
        }
        final QueryResultIterator<Entity> iterator = iterable.iterator();
        long duration = System.nanoTime() - start;
        int callsCount = datastoreCallStats.get().readCount - initCount;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running queryKeys iterator {} took {}ms; calls " + callsCount, criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("queryKeys iterator {} took {}ms; calls " + callsCount, criteria.getDomainName(), (int) (duration / Consts.MSEC2NANO));
        }

        return new ICursorIterator<Long>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Long next() {
                Entity entity = iterator.next();
                return entity.getKey().getId();
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public String encodedCursorRefference() {
                return iterator.getCursor().toWebSafeString();
            }
        };
    }

    @Override
    public <T extends IEntity> int count(EntityQueryCriteria<T> criteria) {
        long start = System.nanoTime();
        Class<T> entityClass = entityClass(criteria);
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        query.setKeysOnly();
        datastoreCallStats.get().readCount++;
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

    private void getAllKeysForDelete(List<Key> keys, IEntity iEntity) {
        nextValue: for (Map.Entry<String, Object> me : iEntity.getValue().entrySet()) {
            if (me.getKey().equals(IEntity.PRIMARY_KEY)) {
                continue nextValue;
            }
            MemberMeta meta = iEntity.getEntityMeta().getMemberMeta(me.getKey());
            if (!meta.isOwnedRelationships()) {
                continue nextValue;
            }
            Object value = me.getValue();
            if (value instanceof Map<?, ?>) {
                IEntity childIEntity = (IEntity) iEntity.getMember(me.getKey());
                if (!meta.isEmbedded()) {
                    keys.add(KeyFactory.createKey(getIEntityKind(childIEntity), childIEntity.getPrimaryKey()));
                }
                getAllKeysForDelete(keys, childIEntity);
            } else if ((ICollection.class.isAssignableFrom(meta.getObjectClass())) && (value instanceof Collection<?>)) {
                ICollection<IEntity, ?> memberList = (ICollection<IEntity, ?>) iEntity.getMember(me.getKey());
                for (IEntity childIEntity : memberList) {
                    keys.add(KeyFactory.createKey(getIEntityKind(childIEntity), childIEntity.getPrimaryKey()));
                    getAllKeysForDelete(keys, childIEntity);
                }
            }
        }
    }

    @Override
    public void delete(IEntity iEntity) {
        if (iEntity.getEntityMeta().isTransient()) {
            throw new Error("Can't delete Transient Entity");
        }
        List<Key> keys = new Vector<Key>();
        keys.add(KeyFactory.createKey(getIEntityKind(iEntity), iEntity.getPrimaryKey()));
        getAllKeysForDelete(keys, iEntity);
        datastoreCallStats.get().writeCount++;
        datastore.delete(keys);
    }

    @Override
    public <T extends IEntity> void delete(Class<T> entityClass, long primaryKey) {
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        datastoreCallStats.get().writeCount++;
        try {
            datastore.delete(KeyFactory.createKey(entityMeta.getPersistenceName(), primaryKey));
        } catch (com.google.apphosting.api.ApiProxy.CapabilityDisabledException e) {
            throw new UnRecoverableRuntimeException(degradeGracefullyMessage());
        }
    }

    @Override
    public <T extends IEntity> int delete(EntityQueryCriteria<T> criteria) {
        Class<T> entityClass = entityClass(criteria);
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't delete Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        query.setKeysOnly();
        datastoreCallStats.get().readCount++;
        PreparedQuery pq = datastore.prepare(query);

        try {
            int removedCount = 0;
            List<Key> keys = new Vector<Key>();
            for (Entity entity : pq.asIterable()) {
                if (keys.size() >= 500) {
                    datastoreCallStats.get().writeCount++;
                    datastore.delete(keys);
                    removedCount += keys.size();
                    keys.clear();
                }
                keys.add(entity.getKey());
            }
            datastoreCallStats.get().writeCount++;
            datastore.delete(keys);
            removedCount += keys.size();
            return removedCount;
        } catch (com.google.apphosting.api.ApiProxy.CapabilityDisabledException e) {
            throw new UnRecoverableRuntimeException(degradeGracefullyMessage());
        }
    }

    @Override
    public <T extends IEntity> void delete(Class<T> entityClass, List<Long> primaryKeys) {
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't delete Transient Entity");
        }
        List<Key> keys = new Vector<Key>();
        try {
            for (Long primaryKey : primaryKeys) {
                if (keys.size() >= 500) {
                    datastoreCallStats.get().writeCount++;
                    datastore.delete(keys);
                    keys.clear();
                }
                keys.add(KeyFactory.createKey(entityMeta.getPersistenceName(), primaryKey));
            }
            datastoreCallStats.get().writeCount++;
            datastore.delete(keys);
        } catch (com.google.apphosting.api.ApiProxy.CapabilityDisabledException e) {
            throw new UnRecoverableRuntimeException(degradeGracefullyMessage());
        }
    }

    @Override
    public int getDatastoreCallCount() {
        return datastoreCallStats.get().readCount + datastoreCallStats.get().writeCount;
    }

    @Override
    public int getDatastoreWriteCallCount() {
        return datastoreCallStats.get().writeCount;
    }
}
