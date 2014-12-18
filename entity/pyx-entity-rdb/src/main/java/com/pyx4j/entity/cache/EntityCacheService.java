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
 */
package com.pyx4j.entity.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.Trace;
import com.pyx4j.entity.annotations.Cached;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.meta.EntityMeta;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.PersistenceTrace;
import com.pyx4j.entity.server.EntityCollectionRequest;
import com.pyx4j.entity.server.IEntityCacheService;

public class EntityCacheService implements IEntityCacheService {

    private static final Logger log = LoggerFactory.getLogger(EntityCacheService.class);

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
            T entity = ent.duplicate();
            if (PersistenceTrace.traceCache) {
                log.info("Cache get {}\n{}", entity.getDebugExceptionInfoString(), PersistenceTrace.getCallOrigin());
            }
            if (PersistenceTrace.traceEntity) {
                if (PersistenceTrace.traceEntityFilter(entity)) {
                    log.info("Cache get {}\n{}", entity.getDebugExceptionInfoString(), PersistenceTrace.getCallOrigin());
                }
            }
            return entity;
        } else {
            if (PersistenceTrace.traceCache) {
                log.info("Cache miss {} {}\n{}", meta.getEntityClass().getSimpleName(), primaryKey, PersistenceTrace.getCallOrigin());
            }
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
                    T entity = ((T) ent).duplicate();
                    ret.put(primaryKey, entity);
                    if (PersistenceTrace.traceCache) {
                        log.info("Cache get {}\n{}", entity.getDebugExceptionInfoString(), PersistenceTrace.getCallOrigin());
                    }
                } else if (PersistenceTrace.traceCache) {
                    log.info("Cache miss {} {}\n{}", meta.getEntityClass().getSimpleName(), primaryKey, PersistenceTrace.getCallOrigin());
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
                        IEntity entity = ((IEntity) ent).duplicate();
                        responce.put(primaryKey, entity);
                        if (PersistenceTrace.traceCache) {
                            log.info("Cache get {}\n{}", entity.getDebugExceptionInfoString(), PersistenceTrace.getCallOrigin());
                        }
                    } else if (PersistenceTrace.traceCache) {
                        log.info("Cache miss {} {}\n{}", meta.getEntityClass().getSimpleName(), primaryKey,
                                PersistenceTrace.getCallOrigin());
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
        if (PersistenceTrace.traceCache) {
            log.info("Cache put {}\n{}", entity.getDebugExceptionInfoString(), PersistenceTrace.getCallOrigin());
        }
        if (PersistenceTrace.traceEntity) {
            if (PersistenceTrace.traceEntityFilter(entity)) {
                log.info("Cache put {}\n{}", entity.getDebugExceptionInfoString(), PersistenceTrace.getCallOrigin());
            }
        }
        CacheService.put(meta.getEntityClass().getName() + entity.getPrimaryKey(), entity.duplicate());
    }

    @Override
    public void put(Iterable<IEntity> entityList) {
        for (IEntity entity : entityList) {
            Cached cached = entity.getEntityMeta().getAnnotation(Cached.class);
            if ((cached == null) || (disabled)) {
                continue;
            }
            if (PersistenceTrace.traceCache) {
                log.info("Cache put {}\n{}", entity.getDebugExceptionInfoString(), PersistenceTrace.getCallOrigin());
            }
            if (PersistenceTrace.traceEntity) {
                if (PersistenceTrace.traceEntityFilter(entity)) {
                    log.info("Cache put {}\n{}", entity.getDebugExceptionInfoString(), PersistenceTrace.getCallOrigin());
                }
            }
            CacheService.put(entity.getEntityMeta().getEntityClass().getSimpleName() + entity.getPrimaryKey(), entity.duplicate());
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
