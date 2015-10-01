/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Sep 22, 2015
 * @author vlads
 */
package com.pyx4j.entity.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.LockFactory;
import com.pyx4j.config.server.events.ServerConfigurationChangeEvent;
import com.pyx4j.config.server.events.ServerEventBus;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

public class GenericCache {

    private static Logger log = LoggerFactory.getLogger(GenericCache.class);

    public static final boolean debug = false;

    private final CacheManager mgr;

    private final Map<String, CacheConfiguration> configurations = new HashMap<>();

    private static String defaultCacheName = GenericCache.class.getName();

    private final LockFactory<Object> lockFactory;

    private GenericCache() {
        lockFactory = new LockFactory<>();
        Configuration config = new Configuration();
        config.setUpdateCheck(false);
        mgr = CacheManager.create(config);
        registerCacheConfiguration(defaultCacheConfiguration());

        ServerEventBus.register(new ServerConfigurationChangeEvent.Handler() {

            @Override
            public void onConfigurationChanged(ServerConfigurationChangeEvent event) {
                reloadConfig();
            }
        });
    }

    private void reloadConfig() {
        reset();
    }

    private static class SingletonHolder {
        public static final GenericCache INSTANCE = new GenericCache();
    }

    static GenericCache instance() {
        return SingletonHolder.INSTANCE;
    }

    @SuppressWarnings("deprecation")
    public static CacheConfiguration defaultCacheConfiguration() {
        int maxElementsInMemory = 10 * 1024;
        CacheConfiguration config = new CacheConfiguration(defaultCacheName, maxElementsInMemory);
        config.setOverflowToDisk(false);
        config.setEternal(false);
        config.setTimeToIdleSeconds(5 * Consts.MIN2SEC);
        config.setTimeToLiveSeconds(5 * Consts.MIN2SEC);
        return config;
    }

    public static void register(CacheConfiguration cacheConfiguration) {
        instance().registerCacheConfiguration(cacheConfiguration);
    }

    private void registerCacheConfiguration(CacheConfiguration cacheConfiguration) {
        assert cacheConfiguration.getName() != null;
        configurations.put(cacheConfiguration.getName(), cacheConfiguration);
    }

    public static Cache cache(String cacheName) {
        return instance().getCache(cacheName);
    }

    private synchronized Cache getCache(String cacheName) {
        Cache cache = mgr.getCache(cacheName);
        if (cache == null) {
            cache = new Cache(configurations.get(cacheName));
            mgr.addCache(cache);
        }
        return cache;
    }

    public static void reset() {
        instance().getCache(defaultCacheName).removeAll();
    }

    public static <K, V> V get(int ttlSeconds, K key, CachedValueFactory<K, V> factory) {
        return getOrCreate(defaultCacheName, ttlSeconds, key, true, factory);
    }

    public static <K, V> V getOrCreate(String cacheName, int ttlSeconds, K key, boolean lockKey, CachedValueFactory<K, V> factory) {
        Cache cache = instance().getCache(cacheName);
        Element element = cache.get(key);
        if (element == null) {
            Lock lock = null;
            if (lockKey) {
                lock = instance().lockFactory.get(key);
                lock.lock();
            }
            try {
                element = cache.get(key);
                if (element == null) {
                    if (debug) {
                        log.info("created value for key {}", key);
                    }
                    element = new Element(key, factory.createValue(key), ttlSeconds, ttlSeconds);
                    cache.put(element);
                }
            } finally {
                if (lockKey) {
                    lock.unlock();
                }
            }
        } else if (debug) {
            log.info("Cached  key {}", key);
        }
        @SuppressWarnings("unchecked")
        V value = (V) element.getObjectValue();
        return value;
    }
}
