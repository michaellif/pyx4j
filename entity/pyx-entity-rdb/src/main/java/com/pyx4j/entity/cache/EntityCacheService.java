/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Nov 1, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.annotations.Cached;
import com.pyx4j.entity.server.EntityCollectionRequest;
import com.pyx4j.entity.server.IEntityCacheService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;

public class EntityCacheService implements IEntityCacheService {

    private static boolean disabled = false;

    public EntityCacheService() {
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        EntityCacheService.disabled = disabled;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IEntity> T get(Class<T> entityClass, com.pyx4j.commons.Key primaryKey) {
        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
        Cached cached = meta.getAnnotation(Cached.class);
        if ((cached == null) || (disabled)) {
            return null;
        }
        T ent = (T) CacheService.get(meta.getEntityClass().getName() + primaryKey);
        if (ent != null) {
            return ent.cloneEntity();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IEntity> Map<com.pyx4j.commons.Key, T> get(Class<T> entityClass, Iterable<com.pyx4j.commons.Key> primaryKeys) {
        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
        Cached cached = meta.getAnnotation(Cached.class);
        if ((cached == null) || (disabled)) {
            return Collections.emptyMap();
        } else {
            Map<com.pyx4j.commons.Key, T> ret = new HashMap<com.pyx4j.commons.Key, T>();
            for (com.pyx4j.commons.Key primaryKey : primaryKeys) {
                Object ent = CacheService.get(meta.getEntityClass().getName() + primaryKey);
                if (ent != null) {
                    ret.put(primaryKey, (T) ((T) ent).cloneEntity());
                }
            }
            return ret;
        }
    }

    @Override
    public Map<EntityCollectionRequest<IEntity>, Map<com.pyx4j.commons.Key, IEntity>> get(Iterable<EntityCollectionRequest<IEntity>> requests) {
        Map<EntityCollectionRequest<IEntity>, Map<com.pyx4j.commons.Key, IEntity>> ret = new HashMap<EntityCollectionRequest<IEntity>, Map<com.pyx4j.commons.Key, IEntity>>();

        for (EntityCollectionRequest<IEntity> request : requests) {
            EntityMeta meta = EntityFactory.getEntityMeta(request.getEntityClass());
            Cached cached = meta.getAnnotation(Cached.class);
            if ((cached == null) || (disabled)) {
                Map<com.pyx4j.commons.Key, IEntity> empty = Collections.emptyMap();
                ret.put(request, empty);
            } else {
                Map<com.pyx4j.commons.Key, IEntity> responce = new HashMap<com.pyx4j.commons.Key, IEntity>();
                for (com.pyx4j.commons.Key primaryKey : request.getPrimaryKeys()) {
                    Object ent = CacheService.get(meta.getEntityClass().getName() + primaryKey);
                    if (ent != null) {
                        responce.put(primaryKey, ((IEntity) ent).cloneEntity());
                    }
                }
                ret.put(request, responce);
            }
        }
        return ret;
    }

    @Override
    public <T extends IEntity> void put(T entity) {
        EntityMeta meta = entity.getEntityMeta();
        Cached cached = meta.getAnnotation(Cached.class);
        if ((cached == null) || (disabled)) {
            return;
        }
        CacheService.put(meta.getEntityClass().getName() + entity.getPrimaryKey(), entity.cloneEntity());
    }

    @Override
    public void put(Iterable<IEntity> entityList) {
        for (IEntity entity : entityList) {
            Cached cached = entity.getEntityMeta().getAnnotation(Cached.class);
            if ((cached == null) || (disabled)) {
                continue;
            }
            CacheService.put(entity.getEntityMeta().getEntityClass().getName() + entity.getPrimaryKey(), entity.cloneEntity());
        }
    }

    @Override
    public <T extends IEntity> void remove(Class<T> entityClass, com.pyx4j.commons.Key primaryKey) {
        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
        Cached cached = meta.getAnnotation(Cached.class);
        if ((cached != null) && (!disabled)) {
            CacheService.remove(entityClass.getName() + primaryKey);
        }
    }

    @Override
    public <T extends IEntity> void remove(T entity) {
        EntityMeta meta = entity.getEntityMeta();
        Cached cached = meta.getAnnotation(Cached.class);
        if ((cached != null) && (!disabled)) {
            CacheService.remove(meta.getEntityClass().getName() + entity.getPrimaryKey());
        }
    }

    @Override
    public <T extends IEntity> void remove(Class<T> entityClass, Iterable<com.pyx4j.commons.Key> primaryKeys) {
        EntityMeta meta = EntityFactory.getEntityMeta(entityClass);
        Cached cached = meta.getAnnotation(Cached.class);
        if ((cached != null) && (!disabled)) {
            for (com.pyx4j.commons.Key primaryKey : primaryKeys) {
                CacheService.remove(meta.getEntityClass().getName() + primaryKey);
            }
        }
    }

}
