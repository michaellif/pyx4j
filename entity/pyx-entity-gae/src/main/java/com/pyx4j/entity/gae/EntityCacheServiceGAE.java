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
 * Created on 2010-10-13
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.gae;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import com.pyx4j.entity.annotations.Cached;
import com.pyx4j.entity.server.EntityCollectionRequest;
import com.pyx4j.entity.server.IEntityCacheService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class EntityCacheServiceGAE implements IEntityCacheService {

    private MemcacheService memcache;

    private static boolean disabled = false;

    public EntityCacheServiceGAE() {
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        EntityCacheServiceGAE.disabled = disabled;
    }

    protected MemcacheService getMemcache() {
        if (this.memcache == null) {
            this.memcache = MemcacheServiceFactory.getMemcacheService();
        }
        return this.memcache;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IEntity> T get(Class<T> entityClass, Long primaryKey) {
        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
        Cached cached = meta.getAnnotation(Cached.class);
        if ((cached == null) || (disabled)) {
            return null;
        }
        return (T) getMemcache().get(meta.getEntityClass().getName() + primaryKey);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IEntity> Map<Long, T> get(Class<T> entityClass, Iterable<Long> primaryKeys) {
        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
        Cached cached = meta.getAnnotation(Cached.class);
        if ((cached == null) || (disabled)) {
            return Collections.emptyMap();
        } else {
            Set<String> keys = new HashSet<String>();
            for (Long primaryKey : primaryKeys) {
                keys.add(meta.getEntityClass().getName() + primaryKey);
            }
            Map<String, Object> raw = getMemcache().getAll(keys);
            if (raw.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<Long, T> ret = new HashMap<Long, T>();
            for (Long primaryKey : primaryKeys) {
                Object ent = raw.get(meta.getEntityClass().getName() + primaryKey);
                if (ent != null) {
                    ret.put(primaryKey, (T) ent);
                }
            }
            return ret;
        }
    }

    @Override
    public Map<EntityCollectionRequest<IEntity>, Map<Long, IEntity>> get(Iterable<EntityCollectionRequest<IEntity>> requests) {
        Map<EntityCollectionRequest<IEntity>, Map<Long, IEntity>> ret = new HashMap<EntityCollectionRequest<IEntity>, Map<Long, IEntity>>();
        Set<String> allCacheKeys = new HashSet<String>();
        for (EntityCollectionRequest<IEntity> request : requests) {
            EntityMeta meta = EntityFactory.getEntityMeta(request.getEntityClass());
            if ((meta.getAnnotation(Cached.class) != null) && (!disabled)) {
                for (Long primaryKey : request.getPrimaryKeys()) {
                    allCacheKeys.add(meta.getEntityClass().getName() + primaryKey);
                }
            }
        }
        Map<String, Object> raw;
        if ((allCacheKeys.size() > 0) && (!disabled)) {
            raw = getMemcache().getAll(allCacheKeys);
        } else {
            raw = Collections.emptyMap();
        }

        for (EntityCollectionRequest<IEntity> request : requests) {
            EntityMeta meta = EntityFactory.getEntityMeta(request.getEntityClass());
            Cached cached = meta.getAnnotation(Cached.class);
            if ((cached == null) || (disabled) || (raw.isEmpty())) {
                ret.put(request, Collections.EMPTY_MAP);
            } else {
                Map<Long, IEntity> responce = new HashMap<Long, IEntity>();
                for (Long primaryKey : request.getPrimaryKeys()) {
                    Object ent = raw.get(meta.getEntityClass().getName() + primaryKey);
                    if (ent != null) {
                        responce.put(primaryKey, (IEntity) ent);
                    }
                }
                ret.put(request, responce);
            }
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IEntity> void put(T entity) {
        EntityMeta meta = entity.getEntityMeta();
        Cached cached = meta.getAnnotation(Cached.class);
        if ((cached == null) || (disabled)) {
            return;
        }
        if ((entity.getParent() != null) || (entity.getOwner() != null)) {
            // Entity delegate its value
            entity = (T) entity.cloneEntity();
        }
        if (cached.expirationSeconds() > 0) {
            getMemcache().put(meta.getEntityClass().getName() + entity.getPrimaryKey(), entity, Expiration.byDeltaSeconds(cached.expirationSeconds()));
        } else {
            getMemcache().put(meta.getEntityClass().getName() + entity.getPrimaryKey(), entity);
        }
    }

    @Override
    public void put(Iterable<IEntity> entityList) {
        Map<Integer, Map<String, IEntity>> rawMaps = new HashMap<Integer, Map<String, IEntity>>();
        for (IEntity entity : entityList) {
            Cached cached = entity.getEntityMeta().getAnnotation(Cached.class);
            if ((cached == null) || (disabled)) {
                continue;
            }

            Map<String, IEntity> rawMap = rawMaps.get(cached.expirationSeconds());
            if (rawMap == null) {
                rawMap = new HashMap<String, IEntity>();
                rawMaps.put(cached.expirationSeconds(), rawMap);
            }
            if ((entity.getParent() != null) || (entity.getOwner() != null)) {
                // Entity delegate its value
                entity = entity.cloneEntity();
            }
            rawMap.put(entity.getEntityMeta().getEntityClass().getName() + entity.getPrimaryKey(), entity);
        }
        for (Map.Entry<Integer, Map<String, IEntity>> me : rawMaps.entrySet()) {
            if (me.getKey() > 0) {
                getMemcache().putAll(me.getValue(), Expiration.byDeltaSeconds(me.getKey()));
            } else {
                getMemcache().putAll(me.getValue());
            }
        }
    }

    @Override
    public <T extends IEntity> void remove(Class<T> entityClass, Long primaryKey) {
        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
        Cached cached = meta.getAnnotation(Cached.class);
        if ((cached != null) && (!disabled)) {
            getMemcache().delete(entityClass.getName() + primaryKey);
        }
    }

    @Override
    public <T extends IEntity> void remove(T entity) {
        EntityMeta meta = entity.getEntityMeta();
        Cached cached = meta.getAnnotation(Cached.class);
        if ((cached != null) && (!disabled)) {
            getMemcache().delete(meta.getEntityClass().getName() + entity.getPrimaryKey());
        }
    }

    @Override
    public <T extends IEntity> void remove(Class<T> entityClass, Iterable<Long> primaryKeys) {
        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
        Cached cached = meta.getAnnotation(Cached.class);
        if ((cached != null) && (!disabled)) {
            List<String> keys = new Vector<String>();
            for (Long primaryKey : primaryKeys) {
                keys.add(meta.getEntityClass().getName() + primaryKey);
            }
            getMemcache().delete(keys);
        }
    }

}
