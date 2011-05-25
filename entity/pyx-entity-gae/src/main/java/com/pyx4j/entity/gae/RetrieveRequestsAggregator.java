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
 * Created on Oct 17, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.gae;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

import com.pyx4j.entity.server.EntityCollectionRequest;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;

class RetrieveRequestsAggregator {

    private static final Logger log = LoggerFactory.getLogger(RetrieveRequestsAggregator.class);

    private static final boolean trace = false;

    private final EntityPersistenceServiceGAE srv;

    private int datastoreReadCount;

    final private Map<Key, IEntity> retrievedMap;

    final private List<IEntity> retrievedToCache = new Vector<IEntity>();

    private final Map<Key, Entity> rawData;

    private final ThreadLocal<Map<Key, IEntity>> requestScopeCache = new ThreadLocal<Map<Key, IEntity>>() {

        @Override
        protected Map<Key, IEntity> initialValue() {
            return new HashMap<Key, IEntity>();
        }

    };

    private class AsyncRequest extends EntityCollectionRequest<IEntity> {

        private final List<Key> keys;

        private final Runnable onResponceReady;

        @SuppressWarnings("unchecked")
        public AsyncRequest(Class<?> entityClass, Iterable<String> primaryKeys, List<Key> keys, Runnable onResponceReady) {
            super((Class<IEntity>) entityClass, primaryKeys);
            this.keys = keys;
            this.onResponceReady = onResponceReady;
        }

    }

    //aggregated
    private List<AsyncRequest> requests;

    RetrieveRequestsAggregator(EntityPersistenceServiceGAE srv) {
        retrievedMap = requestScopeCache.get();
        this.srv = srv;
        this.rawData = new HashMap<Key, Entity>();
    }

    public IEntity getEntity(Key key) {
        return retrievedMap.get(key);
    }

    public boolean containsEntity(Key key) {
        return retrievedMap.containsKey(key);
    }

    public Entity getRaw(Key key) {
        return rawData.get(key);
    }

    public void request(EntityMeta entityMeta, Key key, Runnable onResponceReady) {
        if (retrievedMap.containsKey(key)) {
            if (trace) {
                log.trace("got from request cache {}", key);
            }
            onResponceReady.run();
        } else {
            List<String> missingKeys = new Vector<String>();
            missingKeys.add(String.valueOf(key.getId()));
            List<Key> keys = new Vector<Key>();
            keys.add(key);
            if (requests == null) {
                requests = new Vector<AsyncRequest>();
            }
            requests.add(new AsyncRequest(entityMeta.getEntityClass(), missingKeys, keys, onResponceReady));
        }
    }

    public void request(EntityMeta entityMeta, List<Key> keys, Runnable onResponceReady) {
        List<String> missingKeys = new Vector<String>();
        for (Key key : keys) {
            if (!retrievedMap.containsKey(key)) {
                missingKeys.add(String.valueOf(key.getId()));
            } else if (trace) {
                log.trace("got from request cache {}", key);
            }
        }
        if (missingKeys.isEmpty()) {
            onResponceReady.run();
        } else {
            if (requests == null) {
                requests = new Vector<AsyncRequest>();
            }
            requests.add(new AsyncRequest(entityMeta.getEntityClass(), missingKeys, keys, onResponceReady));
        }

    }

    public void cache(Key key, IEntity entity) {
        retrievedMap.put(key, entity);
        retrievedToCache.add(entity);
    }

    public void retrieved(Key key, IEntity entity) {
        retrievedMap.put(key, entity);
    }

    public void complete() {
        while (requests != null) {
            List<AsyncRequest> processing = requests;
            requests = null;
            retrieve(processing);
        }
        commitCache();
    }

    private void retrieve(List<AsyncRequest> requests) {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Map<EntityCollectionRequest<IEntity>, Map<String, IEntity>> cached = srv.cacheService.get((Iterable) requests);
        List<Key> needToGet = new Vector<Key>();
        for (Map.Entry<EntityCollectionRequest<IEntity>, Map<String, IEntity>> me : cached.entrySet()) {
            AsyncRequest request = (AsyncRequest) me.getKey();
            for (Key key : request.keys) {
                if (!retrievedMap.containsKey(key)) {
                    IEntity cachedEntity = me.getValue().get(key.getId());
                    if (cachedEntity != null) {
                        retrievedMap.put(key, cachedEntity);
                        if (trace) {
                            log.trace("got from gae cache {}", key);
                        }
                    } else {
                        needToGet.add(key);
                    }
                }
            }
        }
        if (needToGet.size() > 0) {
            srv.datastoreCallStats.get().readCount++;
            rawData.putAll(srv.datastore.get(needToGet));
            if (trace) {
                datastoreReadCount++;
                for (Key key : needToGet) {
                    log.trace("got from datastore call# {} {}", datastoreReadCount, key);
                }
            }
        }
        for (AsyncRequest request : requests) {
            request.onResponceReady.run();
        }
    }

    private void commitCache() {
        srv.cacheService.put(retrievedToCache);
        retrievedToCache.clear();
    }
}
