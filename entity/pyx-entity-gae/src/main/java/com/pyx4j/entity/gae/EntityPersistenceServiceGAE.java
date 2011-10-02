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
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.adapters.MemberModificationAdapter;
import com.pyx4j.entity.adapters.ReferenceAdapter;
import com.pyx4j.entity.annotations.Adapters;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Reference;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.server.AdapterFactory;
import com.pyx4j.entity.server.IEntityCacheService;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.ConcurrentUpdateException;
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
import com.pyx4j.security.shared.SecurityViolationException;

/**
 * 
 * @see PersistenceServicesFactory#GAE_IMPL_CLASS
 * 
 */
public class EntityPersistenceServiceGAE implements IEntityPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(EntityPersistenceServiceGAE.class);

    private static I18n i18n = I18nFactory.getI18n();

    private final int ORDINARY_STRING_LENGTH_MAX = com.pyx4j.config.shared.ApplicationBackend.GAE_ORDINARY_STRING_LENGTH_MAX;

    private static final String SECONDARY_PRROPERTY_SUFIX = IndexAdapter.SECONDARY_PRROPERTY_SUFIX;

    private static final String EMBEDDED_PRROPERTY_SUFIX = "-e";

    private static final String GLOBAL_KEYWORD_PRROPERTY = IndexAdapter.ENTITY_KEYWORD_PRROPERTY;

    final DatastoreService datastore;

    final IEntityCacheService cacheService;

    final ThreadLocal<CallStats> datastoreCallStats = new ThreadLocal<CallStats>() {

        @Override
        protected CallStats initialValue() {
            return new CallStats();
        }

    };

    private final ThreadLocal<RetrieveRequestsAggregator> requestAggregator = new ThreadLocal<RetrieveRequestsAggregator>();

    static class CallStats {

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
        cacheService = new EntityCacheServiceGAE();
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
    private void embedEntityProperties(EntityUpdateWrapper entity, String embeddedPath, String embeddedLevel, IEntity childIEntity, boolean parentIndexed) {
        if (childIEntity.isNull()) {
            // remove all properties
            EntityMeta em = childIEntity.getEntityMeta();
            for (String memberName : em.getMemberNames()) {
                MemberMeta memberMeta = em.getMemberMeta(memberName);
                if ((memberMeta.isEntity()) && (memberMeta.isEmbedded())) {
                    embedEntityProperties(entity, embeddedPath + "_" + memberName, embeddedLevel + EMBEDDED_PRROPERTY_SUFIX,
                            (IEntity) childIEntity.getMember(memberName), parentIndexed && memberMeta.isIndexed());
                } else {
                    entity.removeProperty(embeddedPath + "_" + memberName + EMBEDDED_PRROPERTY_SUFIX);
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
            Object value = me.getValue();
            if (IEntity.class.isAssignableFrom(meta.getObjectClass())) {
                if (meta.isEmbedded()) {
                    embedEntityProperties(entity, embeddedPath + "_" + me.getKey(), embeddedLevel + EMBEDDED_PRROPERTY_SUFIX,
                            (IEntity) childIEntity.getMember(me.getKey()), parentIndexed);
                    continue nextValue;
                } else {
                    String kind = EntityFactory.getEntityMeta((Class<? extends IEntity>) meta.getObjectClass()).getPersistenceName();
                    value = KeyFactory.createKey(kind, ((com.pyx4j.commons.Key) ((Map) value).get(IEntity.PRIMARY_KEY)).asLong());
                }
            } else {
                //TODO Allow to embed other types
                value = convertToGAEValue(value, entity, embeddedPath, childIEntity, meta, parentIndexed);
            }
            entity.setProperty(embeddedPath + "_" + meta.getFieldName() + embeddedLevel + EMBEDDED_PRROPERTY_SUFIX, parentIndexed && meta.isIndexed(), value);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object convertToGAEValue(Object value, EntityUpdateWrapper entity, String embeddedPath, IEntity iEntity, MemberMeta meta, boolean parentIndexed) {
        // Create index
        Indexed index = null;
        if (parentIndexed) {
            index = meta.getAnnotation(Indexed.class);
            if ((index != null) && (index.adapters() != null) && (index.adapters().length > 0)) {
                for (Class<? extends IndexAdapter<?>> adapterClass : index.adapters()) {
                    IndexAdapter adapter = AdapterFactory.getIndexAdapter(adapterClass);
                    Object indexValue = adapter.getIndexedValue(iEntity, meta, value);
                    if (indexValue != null) {
                        String indexedPropertyName = adapter.getIndexedColumnName(embeddedPath, meta);
                        if (indexedPropertyName.equals(GLOBAL_KEYWORD_PRROPERTY)) {
                            if (indexValue instanceof Set) {
                                addGloablIndex(entity, index.global(), (Set<String>) indexValue);
                            } else {
                                addGloablIndex(entity, index.global(), (String) indexValue);
                            }
                        } else {
                            entity.setProperty(indexedPropertyName, true, indexValue);
                        }
                    }
                }
            } else if ((index != null) && ((index.global() != 0) || (index.keywordLength() != 0))) {
                throw new Error("Invalid @Index annotation for " + iEntity.getEntityMeta().getCaption() + "." + meta.getFieldName());
            }
        }
        // Convert value
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            if (meta.getLength() > ORDINARY_STRING_LENGTH_MAX) {
                return new Text((String) value);
            } else {
                return value;
            }
        } else if (value instanceof Number) {
            return value;
        } else if (value instanceof Enum<?>) {
            return ((Enum<?>) value).name();
        } else if (IPrimitiveSet.class.isAssignableFrom(meta.getObjectClass())) {
            if (Enum.class.isAssignableFrom(meta.getValueClass())) {
                Set<String> gValue = new HashSet<String>();
                for (Enum v : (Set<Enum>) value) {
                    gValue.add(v.name());
                }
                return gValue;
            } else if (com.pyx4j.commons.Key.class.isAssignableFrom(meta.getValueClass())) {
                Set<Long> gValue = new HashSet<Long>();
                for (com.pyx4j.commons.Key v : (Set<com.pyx4j.commons.Key>) value) {
                    gValue.add(v.asLong());
                }
                return gValue;
            } else {
                return value;
            }
        } else if (value instanceof Date) {
            if (value instanceof java.sql.Date) {
                value = new Date(((Date) value).getTime());
            }
            return value;
        } else if (value instanceof com.pyx4j.commons.Key) {
            return ((com.pyx4j.commons.Key) value).asLong();
        } else if (value instanceof GeoPoint) {
            GeoPoint geoPoint = (GeoPoint) value;
            return new GeoPt((float) geoPoint.getLat(), (float) geoPoint.getLng());
        } else {
            if (value.getClass().isArray()) {
                //TODO support more arrays
                return new Blob((byte[]) value);
            } else {
                return value;
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Key mergeReference(MemberMeta meta, IEntity entity) {
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
            return KeyFactory.createKey(ent.getEntityMeta().getPersistenceName(), ent.getPrimaryKey().asLong());
        } else {
            entity = adapter.onEntityCreation(entity);
            return persistImpl(entity, false);
        }
    }

    private void updateEntityProperties(EntityUpdateWrapper entity, IEntity iEntity, boolean merge, boolean isUpdate) {
        if (iEntity.isNull()) {
            return;
        }
        Class<? extends MemberModificationAdapter<?>>[] entityMembersModificationAdapters = null;
        Adapters adapters = iEntity.getEntityMeta().getAnnotation(Adapters.class);
        if (adapters != null) {
            entityMembersModificationAdapters = adapters.modificationAdapters();
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
                        Key origKeyValue = (Key) entity.getProperty(propertyName);
                        Long origPk = null;
                        if (origKeyValue != null) {
                            origPk = Long.valueOf(origKeyValue.getId());
                        }
                        if ((childIEntity.getPrimaryKey() != null) && (!EqualsHelper.equals(childIEntity.getPrimaryKey(), origPk))) {
                            // attempt to attach to different entity graphs
                            throw new SecurityViolationException("Permission denied");
                        }
                        value = persistImpl(childIEntity, merge);
                        // Cascade delete
                        if (isUpdate && merge) {
                            if ((origKeyValue != null) && (origKeyValue.equals(value))) {
                                datastoreCallStats.get().writeCount++;
                                datastore.delete(origKeyValue);
                            }
                        }
                    }
                } else if (meta.getAnnotation(Reference.class) != null) {
                    com.pyx4j.commons.Key childKeyId = (com.pyx4j.commons.Key) ((Map<String, Object>) value).get(IEntity.PRIMARY_KEY);
                    if (childKeyId != null) {
                        value = KeyFactory.createKey(EntityFactory.getEntityMeta((Class<? extends IEntity>) meta.getObjectClass()).getPersistenceName(),
                                childKeyId.asLong());
                    } else {
                        value = mergeReference(meta, (IEntity) iEntity.getMember(me.getKey()));
                    }
                } else {
                    Map<String, Object> childValueMap = (Map<String, Object>) value;
                    if (childValueMap.size() == 0) {
                        value = null;
                    } else {
                        com.pyx4j.commons.Key childKeyId = (com.pyx4j.commons.Key) childValueMap.get(IEntity.PRIMARY_KEY);
                        if (childKeyId == null) {
                            log.error("Saving non persisted reference {}", iEntity.getMember(me.getKey()));
                            throw new Error("Saving non persisted reference " + iEntity.getValueClass() + "." + propertyName + "." + meta.getCaption());
                        }
                        value = KeyFactory.createKey(EntityFactory.getEntityMeta((Class<? extends IEntity>) meta.getObjectClass()).getPersistenceName(),
                                childKeyId.asLong());
                    }
                }
                Indexed index = meta.getAnnotation(Indexed.class);
                if ((index != null) && (index.global() != 0)) {
                    addGloablIndex(entity, index.global(), Long.toString(((Key) value).getId()));
                }
            } else if ((ISet.class.isAssignableFrom(meta.getObjectClass())) && (value instanceof Set<?>)) {
                Set<Key> childKeys = new HashSet<Key>();
                if (meta.isOwnedRelationships()) {
                    // Save Owned iEntity
                    ISet<IEntity> memberSet = (ISet<IEntity>) iEntity.getMember(me.getKey());
                    if (meta.isEmbedded()) {
                        Set<Object> childValue = new HashSet<Object>();
                        String singleMemberName = null;
                        for (IEntity childIEntity : memberSet) {
                            if (singleMemberName == null) {
                                singleMemberName = childIEntity.getEntityMeta().getMemberNames().iterator().next();
                            }
                            childValue.add(childIEntity.getMemberValue(singleMemberName));
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
                        com.pyx4j.commons.Key childKey = (com.pyx4j.commons.Key) ((Map<String, Object>) el).get(IEntity.PRIMARY_KEY);
                        if (childKey == null) {
                            throw new Error("Saving non persisted reference " + iEntity.getValueClass() + "." + propertyName + "." + meta.getCaption());
                        }
                        childKeys.add(KeyFactory.createKey(EntityFactory.getEntityMeta((Class<? extends IEntity>) meta.getValueClass()).getPersistenceName(),
                                childKey.asLong()));
                    }
                    value = childKeys;
                }
            } else if ((IList.class.isAssignableFrom(meta.getObjectClass())) && (value instanceof List<?>)) {
                Set<Key> childKeys = new HashSet<Key>();
                StringBuilder childKeysOrder = new StringBuilder();
                if (meta.isOwnedRelationships()) {
                    // Save Owned iEntity
                    IList<IEntity> memberList = (IList<IEntity>) iEntity.getMember(me.getKey());
                    for (IEntity childIEntity : memberList) {
                        Key key = persistImpl(childIEntity, merge);
                        childKeys.add(key);
                        if (childKeysOrder.length() > 0) {
                            childKeysOrder.append(',');
                        }
                        childKeysOrder.append(key.getId());
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
                        com.pyx4j.commons.Key childKey = (com.pyx4j.commons.Key) ((Map<String, Object>) el).get(IEntity.PRIMARY_KEY);
                        if (childKey == null) {
                            throw new Error("Saving non persisted reference " + meta.getCaption());
                        }
                        Key key = KeyFactory.createKey(EntityFactory.getEntityMeta((Class<? extends IEntity>) meta.getValueClass()).getPersistenceName(),
                                childKey.asLong());
                        childKeys.add(key);
                        if (childKeysOrder.length() > 0) {
                            childKeysOrder.append(',');
                        }
                        childKeysOrder.append(childKey);
                    }
                }
                entity.setProperty(me.getKey() + SECONDARY_PRROPERTY_SUFIX, false, childKeysOrder.toString());
                value = childKeys;
            } else {
                if ((value != null) && (!IPrimitiveSet.class.isAssignableFrom(meta.getObjectClass()))
                        && (!(meta.getValueClass().isAssignableFrom(value.getClass())))) {
                    throw new Error("Data type corruption " + meta.getValueClass() + " != " + value.getClass());
                }
                value = convertToGAEValue(value, entity, null, iEntity, meta, true);
            }

            if (entity.setProperty(propertyName, meta.isIndexed(), value) && entity.isUpdate) {
                if (meta.getAnnotation(ReadOnly.class) != null) {
                    log.error("Changing readonly property [{}] -> [{}]", entity.lastValue, value);
                    throw new Error("Changing readonly property " + meta.getCaption() + " of " + iEntity.getEntityMeta().getCaption());
                }
                MemberColumn memberColumn = meta.getAnnotation(MemberColumn.class);
                if (memberColumn != null && memberColumn.modificationAdapters() != null) {
                    for (Class<? extends MemberModificationAdapter<?>> adapterClass : memberColumn.modificationAdapters()) {
                        @SuppressWarnings("rawtypes")
                        MemberModificationAdapter adapter = AdapterFactory.getMemberModificationAdapter(adapterClass);
                        if (!adapter.allowModifications(iEntity, meta, entity.lastValue, value)) {
                            log.error("Forbiden change [{}] -> [{}]", entity.lastValue, value);
                            throw new Error("Forbiden change " + meta.getCaption() + " of " + iEntity.getEntityMeta().getCaption());
                        }
                    }
                }
                if (entityMembersModificationAdapters != null) {
                    for (Class<? extends MemberModificationAdapter<?>> adapterClass : entityMembersModificationAdapters) {
                        @SuppressWarnings("rawtypes")
                        MemberModificationAdapter adapter = AdapterFactory.getMemberModificationAdapter(adapterClass);
                        if (!adapter.allowModifications(iEntity, meta, entity.lastValue, value)) {
                            log.error("Forbiden change [{}] -> [{}]", entity.lastValue, value);
                            throw new Error("Forbiden change " + meta.getCaption() + " of " + iEntity.getEntityMeta().getCaption());
                        }
                    }
                }
            }
        }

        // Special case for values not present in Map, e.g. owner reference
        String ownerMemberName = iEntity.getEntityMeta().getOwnerMemberName();
        if (ownerMemberName != null) {
            MemberMeta meta = iEntity.getEntityMeta().getMemberMeta(ownerMemberName);
            IEntity ownerEntity = (IEntity) iEntity.getMember(ownerMemberName);
            if (!ownerEntity.isNull()) {
                com.pyx4j.commons.Key ownerId = ownerEntity.getPrimaryKey();
                if (ownerId == null) {
                    throw new Error("Saving non persisted reference " + ownerEntity.getEntityMeta().getCaption());
                }
                Key ownerKey = KeyFactory.createKey(EntityFactory.getEntityMeta(ownerEntity.getValueClass()).getPersistenceName(), ownerId.asLong());
                entity.setProperty(ownerMemberName, meta.isIndexed(), ownerKey);
            }
        }
    }

    private void addGloablIndex(EntityUpdateWrapper entity, char prefix, Set<String> newKeys) {
        Collection<String> keys = (Collection<String>) entity.getProperty(GLOBAL_KEYWORD_PRROPERTY);
        if (keys == null) {
            keys = new HashSet<String>();
        }
        for (String key : newKeys) {
            keys.add(prefix + key);
        }
        entity.setProperty(GLOBAL_KEYWORD_PRROPERTY, true, keys);
    }

    private void addGloablIndex(EntityUpdateWrapper entity, char prefix, String newKey) {
        Collection<String> keys = (Collection<String>) entity.getProperty(GLOBAL_KEYWORD_PRROPERTY);
        if (keys == null) {
            keys = new HashSet<String>();
        }
        keys.add(prefix + newKey);
        entity.setProperty(GLOBAL_KEYWORD_PRROPERTY, true, keys);
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
                String ownerMemberName = childEntityMeta.getOwnerMemberName();
                if ((ownerMemberName != null) && (childEntityMeta.getMemberMeta(ownerMemberName).getValueClass().equals(entityMeta.getEntityClass()))) {
                    return true;
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
        Table tableAnnotation = entityMeta.getEntityClass().getAnnotation(Table.class);
        if (iEntity.getPrimaryKey() == null) {
            if ((tableAnnotation != null) && (tableAnnotation.primaryKeyStrategy() == Table.PrimaryKeyStrategy.ASSIGNED)) {
                throw new Error("Can't persist Entity without assigned PK");
            }
            isUpdate = false;
            if (isBidirectionalReferenceRequired(iEntity)) {
                datastoreCallStats.get().readCount++;
                entity = new Entity(datastore.allocateIds(getIEntityKind(iEntity), 1).getStart());
                iEntity.setPrimaryKey(new com.pyx4j.commons.Key(entity.getKey().getId()));
            } else {
                entity = new Entity(getIEntityKind(iEntity));
            }
            String createdTs = entityMeta.getCreatedTimestampMember();
            if (createdTs != null) {
                iEntity.setMemberValue(createdTs, new Date());
            }
        } else {
            Key key = KeyFactory.createKey(getIEntityKind(iEntity), iEntity.getPrimaryKey().asLong());
            if (merge) {
                try {
                    datastoreCallStats.get().readCount++;
                    entity = datastore.get(key);
                } catch (EntityNotFoundException e) {
                    if ((tableAnnotation != null) && (tableAnnotation.primaryKeyStrategy() != Table.PrimaryKeyStrategy.ASSIGNED)) {
                        throw new RuntimeException("Entity " + key.getKind() + " " + key.getId() + " NotFound");
                    } else {
                        entity = new Entity(key);
                        isUpdate = false;
                    }
                }
            } else {
                entity = new Entity(key);
            }
        }
        String updatedTs = entityMeta.getUpdatedTimestampMember();
        if (merge && isUpdate) {
            if ((updatedTs != null) && !EqualsHelper.equals(iEntity.getMemberValue(updatedTs), entity.getProperty(updatedTs))) {
                log.debug("Timestamp change {} -> {}", entity.getProperty(updatedTs), iEntity.getMemberValue(updatedTs));
                throw new ConcurrentUpdateException(i18n.tr("{0} updated externally", entityMeta.getCaption()));
            }
            String createdTs = entityMeta.getCreatedTimestampMember();
            if ((createdTs != null) && !EqualsHelper.equals(iEntity.getMemberValue(createdTs), entity.getProperty(createdTs))) {
                log.debug("Timestamp change {} -> {}", entity.getProperty(createdTs), iEntity.getMemberValue(createdTs));
                throw new SecurityViolationException("Permission denied");
            }
        }

        EntityUpdateWrapper entityUpdateWrapper = new EntityUpdateWrapper(entity, merge && isUpdate);
        updateEntityProperties(entityUpdateWrapper, iEntity, merge, isUpdate);

        if (updatedTs != null) {
            if (entityUpdateWrapper.updated) {
                Date now = new Date();
                iEntity.setMemberValue(updatedTs, now);
                entityUpdateWrapper.setProperty(updatedTs, iEntity.getEntityMeta().getMemberMeta(updatedTs).isIndexed(), now);
            }
        }

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
                iEntity.setPrimaryKey(new com.pyx4j.commons.Key(keyCreated.getId()));
                cacheService.put(iEntity);
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
            List<IEntity> iEntityList = new Vector<IEntity>();
            for (IEntity iEntity : entityIterable) {
                if (entityList.size() >= 500) {
                    datastoreCallStats.get().writeCount++;
                    List<Key> keys = datastore.put(entityList);
                    for (int i = 0; i < iEntityList.size(); i++) {
                        iEntityList.get(i).setPrimaryKey(new com.pyx4j.commons.Key(keys.get(i).getId()));
                    }
                    cacheService.put(iEntityList);
                    entityList.clear();
                    iEntityList.clear();
                }
                EntityUpdateWrapper entity = createEntity(iEntity, false);
                entityList.add(entity.entity);
                iEntityList.add(iEntity);
            }
            if (entityList.size() > 0) {
                datastoreCallStats.get().writeCount++;
                List<Key> keys = datastore.put(entityList);
                for (int i = 0; i < iEntityList.size(); i++) {
                    iEntityList.get(i).setPrimaryKey(new com.pyx4j.commons.Key(keys.get(i).getId()));
                }
                cacheService.put(iEntityList);
            }
        } catch (com.google.apphosting.api.ApiProxy.CapabilityDisabledException e) {
            throw new UnRecoverableRuntimeException(degradeGracefullyMessage());
        }
    }

    @Override
    public <T extends IEntity> void merge(Iterable<T> entityIterable) {
        for (T entity : entityIterable) {
            merge(entity);
        }
    }

    private Object deserializeValue(IEntity iEntity, String keyName, Object value, RetrieveRequestsAggregator aggregator) {
        if (value instanceof Text) {
            return ((Text) value).getValue();
        } else if (value instanceof Key) {
            IEntity childIEntity = (IEntity) iEntity.getMember(keyName);
            if (childIEntity.getMeta().isDetached()) {
                childIEntity.setPrimaryKey(new com.pyx4j.commons.Key(((Key) value).getId()));
            } else {
                if ((iEntity.getOwner() != null) && (iEntity.getEntityMeta().getMemberMeta(keyName).isOwner())) {
                    // Do not retrieve Owner
                } else {
                    childIEntity.setPrimaryKey(new com.pyx4j.commons.Key(((Key) value).getId()));
                    retrieveEntity(childIEntity, (Key) value, aggregator);
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
            } else if (cls.equals(LogicalDate.class)) {
                return new LogicalDate(((Date) value).getTime());
            } else {
                return value;
            }
        } else if (value instanceof Long) {
            Class<?> metaValueClass = iEntity.getEntityMeta().getMemberMeta(keyName).getValueClass();
            if (Integer.class.isAssignableFrom(metaValueClass)) {
                return ((Long) value).intValue();
            } else if (com.pyx4j.commons.Key.class.isAssignableFrom(metaValueClass)) {
                return new com.pyx4j.commons.Key(((Long) value).longValue());
            } else if (Long.class.isAssignableFrom(metaValueClass)) {
                return value;
            } else if (Short.class.isAssignableFrom(metaValueClass)) {
                return ((Long) value).shortValue();
            } else if (Byte.class.isAssignableFrom(metaValueClass)) {
                return ((Long) value).byteValue();
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
    private void setEmbededIEntityValue(IEntity iEntity, String keyName, Object value, RetrieveRequestsAggregator aggregator) {
        String memberName = keyName.substring(0, keyName.indexOf('_'));
        IObject<?> member = iEntity.getMember(memberName);
        String memberValueName = keyName.substring(memberName.length() + 1, keyName.length() - EMBEDDED_PRROPERTY_SUFIX.length());
        if (member instanceof ISet<?>) {
            // Support only singleMemberName
            throw new Error("Unsupported Embeded type");
        } else {
            IEntity childIEntity = (IEntity) member;
            if (memberValueName.endsWith(EMBEDDED_PRROPERTY_SUFIX)) {
                setEmbededIEntityValue(childIEntity, memberValueName, value, aggregator);
            } else {
                childIEntity.setMemberValue(memberValueName, deserializeValue(childIEntity, memberValueName, value, aggregator));
            }
        }
    }

    // Make only one call to DB to get the list
    private void retrieveChildEntityCollection(final ICollection member, final List<Key> keys, final RetrieveRequestsAggregator aggregator) {
        member.clear();
        if (member.getMeta().isDetached()) {
            for (Key childKey : keys) {
                IEntity childIEntity = EntityFactory.create((Class<IEntity>) member.getMeta().getValueClass());
                childIEntity.setPrimaryKey(new com.pyx4j.commons.Key(childKey.getId()));
                member.add(childIEntity);
            }
        } else {
            final EntityMeta entityMeta = EntityFactory.getEntityMeta((Class<IEntity>) member.getMeta().getValueClass());
            for (Key childKey : keys) {
                if (!entityMeta.getPersistenceName().equals(childKey.getKind())) {
                    throw new RuntimeException("Unexpected IEntity " + entityMeta.getPersistenceName() + " Kind " + childKey.getKind());
                }
            }

            aggregator.request(entityMeta, keys, new Runnable() {

                @Override
                public void run() {
                    for (Key key : keys) {
                        IEntity childIEntity = EntityFactory.create(entityMeta.getEntityClass());
                        IEntity cachedEntity = aggregator.getEntity(key);
                        if (cachedEntity != null) {
                            childIEntity.set(cachedEntity);
                        } else {
                            Entity entity = aggregator.getRaw(key);
                            if (entity == null) {
                                throw new RuntimeException("Entity " + key.getKind() + " " + key.getId() + " NotFound");
                            }
                            aggregator.cache(key, childIEntity);
                            updateIEntity(childIEntity, entity, aggregator);
                        }
                        member.add(childIEntity);
                    }
                }

            });
        }
    }

    private void updateIEntity(IEntity iEntity, Entity entity, RetrieveRequestsAggregator aggregator) {
        iEntity.setPrimaryKey(new com.pyx4j.commons.Key(entity.getKey().getId()));
        for (Map.Entry<String, Object> me : entity.getProperties().entrySet()) {
            Object value = me.getValue();
            String keyName = me.getKey();

            if (keyName.endsWith(SECONDARY_PRROPERTY_SUFIX)) {
                continue;
            } else if (keyName.endsWith(EMBEDDED_PRROPERTY_SUFIX)) {
                // Recursive child values
                setEmbededIEntityValue(iEntity, keyName, value, aggregator);
                continue;
            } else if (value instanceof List<?>) {
                IObject<?> member = iEntity.getMember(keyName);
                if (member.getMeta().isEmbedded()) {
                    // We Support only single MemberName !
                    String singleMemberName = null;
                    for (Object valueItem : (List) value) {
                        IEntity childIEntity = EntityFactory.create((Class<IEntity>) member.getMeta().getValueClass());
                        if (singleMemberName == null) {
                            singleMemberName = childIEntity.getEntityMeta().getMemberNames().iterator().next();
                        }
                        childIEntity.setMemberValue(singleMemberName, deserializeValue(childIEntity, singleMemberName, valueItem, aggregator));
                        ((ISet) member).add(childIEntity);
                    }
                    continue;
                } else if (member instanceof ISet<?>) {
                    retrieveChildEntityCollection(((ISet) member), (List<Key>) value, aggregator);
                    continue;
                } else if (member instanceof IList<?>) {
                    // retrieve order  and sort by this order
                    List<Long> childKeysOrder;
                    Object childKeysOrderProperty = entity.getProperty(keyName + SECONDARY_PRROPERTY_SUFIX);
                    if (childKeysOrderProperty instanceof Blob) {
                        childKeysOrder = (List<Long>) readObject((Blob) childKeysOrderProperty);
                    } else {
                        childKeysOrder = readListOfLong((String) childKeysOrderProperty);
                    }
                    if (childKeysOrder != null) {
                        Collections.sort(((List<Key>) value), new KeyComparator(childKeysOrder));
                    }
                    retrieveChildEntityCollection(((IList) member), (List<Key>) value, aggregator);
                    continue;
                } else if (member instanceof IPrimitiveSet<?>) {
                    if (Enum.class.isAssignableFrom(member.getMeta().getValueClass())) {
                        Class<Enum> cls = (Class<Enum>) member.getMeta().getValueClass();
                        for (String v : (Collection<String>) value) {
                            Enum eValue = Enum.valueOf(cls, v);
                            ((IPrimitiveSet) member).add(eValue);
                        }
                    } else if (com.pyx4j.commons.Key.class.isAssignableFrom(member.getMeta().getValueClass())) {
                        for (Long v : (Collection<Long>) value) {
                            ((IPrimitiveSet) member).add(new com.pyx4j.commons.Key(v.longValue()));
                        }
                    } else {
                        ((IPrimitiveSet) member).addAll((Collection) value);
                    }
                    continue;
                }
            } else {
                value = deserializeValue(iEntity, keyName, value, aggregator);
            }
            iEntity.setMemberValue(keyName, value);
        }
    }

    private List<Long> readListOfLong(String value) {
        if (value == null) {
            return null;
        } else {
            List<Long> rc = new Vector<Long>();
            for (String s : value.split(",")) {
                rc.add(Long.valueOf(s));
            }
            return rc;
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
            return keyIdOrdered.indexOf(k1.getId()) - keyIdOrdered.indexOf(k2.getId());
        }

    }

    private void retrieveEntity(final IEntity iEntity, final Key key, final RetrieveRequestsAggregator aggregator) {
        if (!getIEntityKind(iEntity).equals(key.getKind())) {
            throw new RuntimeException("Unexpected IEntity " + getIEntityKind(iEntity) + " Kind " + key.getKind());
        }
        if (aggregator.containsEntity(key)) {
            iEntity.setValue(aggregator.getEntity(key).getValue());
        } else {
            aggregator.request(iEntity.getEntityMeta(), key, new Runnable() {
                @Override
                public void run() {
                    IEntity cachedEntity = aggregator.getEntity(key);
                    if (cachedEntity != null) {
                        iEntity.set(cachedEntity);
                    } else {
                        Entity entity = aggregator.getRaw(key);
                        if (entity == null) {
                            throw new RuntimeException("Entity " + key.getKind() + " " + key.getId() + " NotFound");
                        }
                        aggregator.cache(key, iEntity);
                        updateIEntity(iEntity, entity, aggregator);
                    }
                }
            });
        }
    }

    @Override
    public <T extends IEntity> T retrieve(Class<T> entityClass, com.pyx4j.commons.Key primaryKey) {
        T iEntity = EntityFactory.create(entityClass);
        iEntity.setPrimaryKey(primaryKey);
        if (retrieve(iEntity)) {
            return iEntity;
        } else {
            return null;
        }
    }

    @Override
    public <T extends IEntity> boolean retrieve(final T iEntity) {
        RetrieveRequestsAggregator globalAggregator = requestAggregator.get();
        final RetrieveRequestsAggregator aggregator = (globalAggregator != null) ? globalAggregator : new RetrieveRequestsAggregator(this);

        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().readCount;

        if (iEntity.getEntityMeta().isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        final Key key = KeyFactory.createKey(iEntity.getEntityMeta().getPersistenceName(), iEntity.getPrimaryKey().asLong());
        aggregator.request(iEntity.getEntityMeta(), key, new Runnable() {
            @Override
            public void run() {
                IEntity cachedEntity = aggregator.getEntity(key);
                if (cachedEntity != null) {
                    iEntity.set(cachedEntity);
                } else {
                    Entity entity = aggregator.getRaw(key);
                    if (entity != null) {
                        aggregator.cache(key, iEntity);
                        updateIEntity(iEntity, entity, aggregator);
                    } else {
                        log.debug("Entity " + iEntity.getEntityMeta().getPersistenceName() + " " + key.getId() + " NotFound");
                    }
                }
            }
        });
        if (globalAggregator == null) {
            aggregator.complete();
            long duration = System.nanoTime() - start;
            int callsCount = datastoreCallStats.get().readCount - initCount;
            if (duration > Consts.SEC2NANO) {
                log.warn("Long running retrieve {} took {}ms; calls " + callsCount, iEntity.getValueClass().getName(), (int) (duration / Consts.MSEC2NANO));
            } else {
                log.debug("retrieve {} took {}ms; calls " + callsCount, iEntity.getValueClass().getName(), (int) (duration / Consts.MSEC2NANO));
            }
            if (iEntity.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } else {
            // This is undefined here
            return true;
        }
    }

    @Override
    public <T extends IEntity> void retrieve(Iterable<T> entityIterable) {
        //TODO implement this
        throw new Error("implement this");
    }

    @Override
    public <T extends IEntity> Map<com.pyx4j.commons.Key, T> retrieve(final Class<T> entityClass, Iterable<com.pyx4j.commons.Key> primaryKeys) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().readCount;

        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        RetrieveRequestsAggregator globalAggregator = requestAggregator.get();
        final RetrieveRequestsAggregator aggregator = (globalAggregator != null) ? globalAggregator : new RetrieveRequestsAggregator(this);

        final Map<com.pyx4j.commons.Key, T> ret = new HashMap<com.pyx4j.commons.Key, T>();
        final List<Key> keys = new Vector<Key>();
        for (com.pyx4j.commons.Key primaryKey : primaryKeys) {
            keys.add(KeyFactory.createKey(entityMeta.getPersistenceName(), primaryKey.asLong()));
        }

        aggregator.request(entityMeta, keys, new Runnable() {
            @Override
            public void run() {
                for (Key key : keys) {
                    IEntity cachedEntity = aggregator.getEntity(key);
                    if (cachedEntity != null) {
                        ret.put(new com.pyx4j.commons.Key(key.getId()), (T) cachedEntity);
                    } else {
                        Entity entity = aggregator.getRaw(key);
                        if (entity != null) {
                            T iEntity = EntityFactory.create(entityClass);
                            aggregator.cache(entity.getKey(), iEntity);
                            updateIEntity(iEntity, entity, aggregator);
                            ret.put(new com.pyx4j.commons.Key(key.getId()), iEntity);
                        }
                    }
                }
            }
        });

        if (globalAggregator == null) {
            aggregator.complete();
            long duration = System.nanoTime() - start;
            int callsCount = datastoreCallStats.get().readCount - initCount;
            if (duration > Consts.SEC2NANO) {
                log.warn("Long running retrieve {}s took {}ms; calls " + callsCount, entityClass.getName(), (int) (duration / Consts.MSEC2NANO));
            } else {
                log.debug("retrieve {}s took {}ms; calls " + callsCount, entityClass.getName(), (int) (duration / Consts.MSEC2NANO));
            }
        }
        return ret;
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
            return KeyFactory.createKey(((IEntity) value).getEntityMeta().getPersistenceName(), (((IEntity) value).getPrimaryKey().asLong()));
        } else if (value instanceof com.pyx4j.commons.Key) {
            if (propertyName.equals(IEntity.PRIMARY_KEY)) {
                return KeyFactory.createKey(entityMeta.getPersistenceName(), (((com.pyx4j.commons.Key) value).asLong()));
            } else if (propertyName.endsWith(SECONDARY_PRROPERTY_SUFIX)) {
                return (((com.pyx4j.commons.Key) value).asLong());
            } else {
                MemberMeta mm = entityMeta.getMemberMeta(propertyName);
                if (!mm.isEmbedded() && (mm.isEntity() || (ICollection.class.isAssignableFrom(mm.getObjectClass())))) {
                    return KeyFactory.createKey(EntityFactory.getEntityMeta((Class<? extends IEntity>) mm.getValueClass()).getPersistenceName(),
                            (((com.pyx4j.commons.Key) value).asLong()));
                } else {
                    return (((com.pyx4j.commons.Key) value).asLong());
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
        Object value;
        if (propertyName.equals(IEntity.PRIMARY_KEY)) {
            value = datastoreValue(entityMeta, propertyName, propertyCriterion.getValue());
            propertyName = Entity.KEY_RESERVED_PROPERTY;
        } else if (!propertyName.endsWith(SECONDARY_PRROPERTY_SUFIX)) {
            MemberMeta mm = entityMeta.getMemberMeta(new Path(propertyName));
            // TODO Query by embeded properies
            propertyName = mm.getFieldName();
            value = datastoreValue(entityMeta, propertyName, propertyCriterion.getValue());
            if (!mm.isIndexed()) {
                throw new Error("Query by Unindexed property " + propertyName + " of " + entityMeta.getCaption());
            }
        } else {
            value = datastoreValue(entityMeta, propertyName, propertyCriterion.getValue());
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
            b.append(f.getPropertyName()).append(' ').append(f.getOperator()).append(f.getValue()).append(" ");
        }
        log.debug("{} search by {}", entityMeta.getPersistenceName(), b);

        return query;
    }

    @Override
    public <T extends IEntity> T retrieve(EntityQueryCriteria<T> criteria) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().readCount;
        Class<T> entityClass = criteria.getEntityClass();
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        datastoreCallStats.get().readCount++;
        PreparedQuery pq = datastore.prepare(query);
        pq.asIterable(FetchOptions.Builder.withLimit(1));

        T iEntity = null;
        Entity entity = pq.asSingleEntity();
        if (entity != null) {
            iEntity = EntityFactory.create(entityClass);
            RetrieveRequestsAggregator aggregator = new RetrieveRequestsAggregator(this);
            aggregator.retrieved(entity.getKey(), iEntity);
            updateIEntity(iEntity, entity, aggregator);
            aggregator.complete();
        }
        long duration = System.nanoTime() - start;
        int callsCount = datastoreCallStats.get().readCount - initCount;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running retrieve query {} took {}ms; calls " + callsCount, criteria.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("retrieve query {} took {}ms; calls " + callsCount, criteria.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
        }
        return iEntity;
    }

    @Override
    public <T extends IEntity> List<T> query(EntityQueryCriteria<T> criteria) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().readCount;
        Class<T> entityClass = criteria.getEntityClass();
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        datastoreCallStats.get().readCount++;
        PreparedQuery pq = datastore.prepare(query);

        RetrieveRequestsAggregator aggregator = new RetrieveRequestsAggregator(this);
        List<T> rc = new Vector<T>();
        for (Entity entity : pq.asIterable()) {
            T iEntity = EntityFactory.create(entityClass);
            aggregator.retrieved(entity.getKey(), iEntity);
            updateIEntity(iEntity, entity, aggregator);
            rc.add(iEntity);
        }
        aggregator.complete();
        long duration = System.nanoTime() - start;
        int callsCount = datastoreCallStats.get().readCount - initCount;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running query {} took {}ms; calls " + callsCount, criteria.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("query {} took {}ms; calls " + callsCount, criteria.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
        }
        return rc;
    }

    @Override
    public <T extends IEntity> ICursorIterator<T> query(String encodedCursorRefference, EntityQueryCriteria<T> criteria) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().readCount;
        final Class<T> entityClass = criteria.getEntityClass();
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        datastoreCallStats.get().readCount++;
        PreparedQuery pq = datastore.prepare(query);

        final RetrieveRequestsAggregator aggregator = new RetrieveRequestsAggregator(this);

        final QueryResultIterable<Entity> iterable;
        if (encodedCursorRefference != null) {
            iterable = pq.asQueryResultIterable(FetchOptions.Builder.withStartCursor(Cursor.fromWebSafeString(encodedCursorRefference)).prefetchSize(40));
        } else {
            iterable = pq.asQueryResultIterable(FetchOptions.Builder.withPrefetchSize(40));
        }
        final QueryResultIterator<Entity> iterator = iterable.iterator();
        long duration = System.nanoTime() - start;
        int callsCount = datastoreCallStats.get().readCount - initCount;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running query iterator {} took {}ms; calls " + callsCount, criteria.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("query iterator {} took {}ms; calls " + callsCount, criteria.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
        }

        return new ICursorIterator<T>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public void completeRetrieval() {
                aggregator.complete();
            }

            @Override
            public T next() {
                Entity entity = iterator.next();
                T iEntity = EntityFactory.create(entityClass);
                updateIEntity(iEntity, entity, aggregator);
                return iEntity;
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public String encodedCursorReference() {
                Cursor cursor = iterator.getCursor();
                if (cursor != null) {
                    return cursor.toWebSafeString();
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public <T extends IEntity> List<com.pyx4j.commons.Key> queryKeys(EntityQueryCriteria<T> criteria) {
        long start = System.nanoTime();
        Class<T> entityClass = criteria.getEntityClass();
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        query.setKeysOnly();
        datastoreCallStats.get().readCount++;
        PreparedQuery pq = datastore.prepare(query);

        List<com.pyx4j.commons.Key> rc = new Vector<com.pyx4j.commons.Key>();
        for (Entity entity : pq.asIterable()) {
            rc.add(new com.pyx4j.commons.Key(entity.getKey().getId()));
        }
        long duration = System.nanoTime() - start;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running queryKeys {} took {}ms", criteria.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("queryKeys {} took {}ms", criteria.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
        }
        return rc;
    }

    @Override
    public <T extends IEntity> ICursorIterator<com.pyx4j.commons.Key> queryKeys(String encodedCursorRefference, EntityQueryCriteria<T> criteria) {
        long start = System.nanoTime();
        int initCount = datastoreCallStats.get().readCount;
        final Class<T> entityClass = criteria.getEntityClass();
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
            iterable = pq.asQueryResultIterable(FetchOptions.Builder.withStartCursor(Cursor.fromWebSafeString(encodedCursorRefference)));
        } else {
            iterable = pq.asQueryResultIterable();
        }
        final QueryResultIterator<Entity> iterator = iterable.iterator();
        long duration = System.nanoTime() - start;
        int callsCount = datastoreCallStats.get().readCount - initCount;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running queryKeys iterator {} took {}ms; calls " + callsCount, criteria.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("queryKeys iterator {} took {}ms; calls " + callsCount, criteria.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
        }

        return new ICursorIterator<com.pyx4j.commons.Key>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public void completeRetrieval() {

            }

            @Override
            public com.pyx4j.commons.Key next() {
                Entity entity = iterator.next();
                return new com.pyx4j.commons.Key(entity.getKey().getId());
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public String encodedCursorReference() {
                Cursor cursor = iterator.getCursor();
                if (cursor != null) {
                    return cursor.toWebSafeString();
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public <T extends IEntity> int count(EntityQueryCriteria<T> criteria) {
        long start = System.nanoTime();
        Class<T> entityClass = criteria.getEntityClass();
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        Query query = buildQuery(entityMeta, criteria);
        query.setKeysOnly();
        datastoreCallStats.get().readCount++;
        PreparedQuery pq = datastore.prepare(query);
        int rc = pq.countEntities(FetchOptions.Builder.withDefaults());
        long duration = System.nanoTime() - start;
        if (duration > Consts.SEC2NANO) {
            log.warn("Long running countQuery {} took {}ms", criteria.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
        } else {
            log.debug("countQuery {} took {}ms", criteria.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
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
                    keys.add(KeyFactory.createKey(getIEntityKind(childIEntity), childIEntity.getPrimaryKey().asLong()));
                }
                getAllKeysForDelete(keys, childIEntity);
            } else if ((ICollection.class.isAssignableFrom(meta.getObjectClass())) && (value instanceof Collection<?>)) {
                ICollection<IEntity, ?> memberList = (ICollection<IEntity, ?>) iEntity.getMember(me.getKey());
                for (IEntity childIEntity : memberList) {
                    keys.add(KeyFactory.createKey(getIEntityKind(childIEntity), childIEntity.getPrimaryKey().asLong()));
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
        keys.add(KeyFactory.createKey(getIEntityKind(iEntity), iEntity.getPrimaryKey().asLong()));
        getAllKeysForDelete(keys, iEntity);
        datastoreCallStats.get().writeCount++;
        datastore.delete(keys);
        cacheService.remove(iEntity);
    }

    @Override
    public <T extends IEntity> void delete(Class<T> entityClass, com.pyx4j.commons.Key primaryKey) {
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't retrieve Transient Entity");
        }
        datastoreCallStats.get().writeCount++;
        try {
            datastore.delete(KeyFactory.createKey(entityMeta.getPersistenceName(), primaryKey.asLong()));
        } catch (com.google.apphosting.api.ApiProxy.CapabilityDisabledException e) {
            throw new UnRecoverableRuntimeException(degradeGracefullyMessage());
        }
        cacheService.remove(entityClass, primaryKey);
    }

    @Override
    public <T extends IEntity> int delete(EntityQueryCriteria<T> criteria) {
        Class<T> entityClass = criteria.getEntityClass();
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
                cacheService.remove(entityClass, new com.pyx4j.commons.Key(entity.getKey().getId()));
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
    public <T extends IEntity> void delete(Class<T> entityClass, Iterable<com.pyx4j.commons.Key> primaryKeys) {
        EntityMeta entityMeta = EntityFactory.getEntityMeta(entityClass);
        if (entityMeta.isTransient()) {
            throw new Error("Can't delete Transient Entity");
        }
        List<Key> keys = new Vector<Key>();
        try {
            cacheService.remove(entityClass, primaryKeys);
            for (com.pyx4j.commons.Key primaryKey : primaryKeys) {
                if (keys.size() >= 500) {
                    datastoreCallStats.get().writeCount++;
                    datastore.delete(keys);
                    keys.clear();
                }
                keys.add(KeyFactory.createKey(entityMeta.getPersistenceName(), primaryKey.asLong()));
            }
            datastoreCallStats.get().writeCount++;
            datastore.delete(keys);
        } catch (com.google.apphosting.api.ApiProxy.CapabilityDisabledException e) {
            throw new UnRecoverableRuntimeException(degradeGracefullyMessage());
        }
    }

    @Override
    public <T extends IEntity> void truncate(Class<T> entityClass) {
        delete(new EntityQueryCriteria<T>(entityClass));
    }

    @Override
    public void requestsAggregationStart() {
        RetrieveRequestsAggregator aggregator = requestAggregator.get();
        if (aggregator == null) {
            aggregator = new RetrieveRequestsAggregator(this);
            requestAggregator.set(aggregator);
        }
    }

    @Override
    public void requestsAggregationComplete() {
        RetrieveRequestsAggregator aggregator = requestAggregator.get();
        if (aggregator != null) {
            try {
                aggregator.complete();
            } finally {
                requestAggregator.remove();
            }
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
