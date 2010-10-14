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
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import com.pyx4j.entity.annotations.Cached;
import com.pyx4j.entity.server.IEntityCacheService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class EntityCacheServiceGAE implements IEntityCacheService {

    public static final String MEMCACHE_NAMESPACE = "PyxEntityCache";

    private MemcacheService memcache;

    public EntityCacheServiceGAE() {
    }

    protected MemcacheService getMemcache() {
        if (this.memcache == null) {
            this.memcache = MemcacheServiceFactory.getMemcacheService(MEMCACHE_NAMESPACE);
        }
        return this.memcache;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IEntity> T get(Class<T> entityClass, Long primaryKey) {
        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
        Cached cached = meta.getAnnotation(Cached.class);
        if (cached == null) {
            return null;
        }
        return (T) getMemcache().get(meta.getEntityClass().getName() + primaryKey);
    }

    @Override
    public <T extends IEntity> Map<Long, T> get(Class<T> entityClass, Iterable<Long> primaryKeys) {
        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
        Cached cached = meta.getAnnotation(Cached.class);
        if (cached == null) {
            return Collections.emptyMap();
        } else {
            List<String> keys = new Vector<String>();
            for (Long primaryKey : primaryKeys) {
                keys.add(meta.getEntityClass().getName() + primaryKey);
            }
            @SuppressWarnings("unchecked")
            Map<String, T> raw = (Map<String, T>) getMemcache().getAll(keys);
            if (raw.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<Long, T> ret = new HashMap<Long, T>();
            for (Long primaryKey : primaryKeys) {
                T ent = raw.get(meta.getEntityClass().getName() + primaryKey);
                if (ent != null) {
                    ret.put(primaryKey, ent);
                }
            }
            return ret;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IEntity> void put(T entity) {
        EntityMeta meta = entity.getEntityMeta();
        Cached cached = meta.getAnnotation(Cached.class);
        if (cached == null) {
            return;
        }
        if ((entity.getParent() != null) || (entity.getOwner() != null)) {
            // Entity delegate its value
            entity = (T) entity.cloneEntity();
        }
        getMemcache().put(meta.getEntityClass().getName() + entity.getPrimaryKey(), entity);
    }

    @Override
    public <T extends IEntity> void put(Iterable<T> entityList) {
        for (T ent : entityList) {
            put(ent);
        }
    }

    @Override
    public <T extends IEntity> void remove(Class<T> entityClass, Long primaryKey) {
        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
        Cached cached = meta.getAnnotation(Cached.class);
        if (cached != null) {
            getMemcache().delete(entityClass.getName() + primaryKey);
        }
    }

    @Override
    public <T extends IEntity> void remove(T entity) {
        EntityMeta meta = entity.getEntityMeta();
        Cached cached = meta.getAnnotation(Cached.class);
        if (cached != null) {
            getMemcache().delete(meta.getEntityClass().getName() + entity.getPrimaryKey());
        }
    }

    @Override
    public <T extends IEntity> void remove(Class<T> entityClass, Iterable<Long> primaryKeys) {
        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
        Cached cached = meta.getAnnotation(Cached.class);
        if (cached != null) {
            List<String> keys = new Vector<String>();
            for (Long primaryKey : primaryKeys) {
                keys.add(meta.getEntityClass().getName() + primaryKey);
            }
            getMemcache().delete(keys);
        }
    }

}
