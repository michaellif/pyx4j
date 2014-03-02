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

import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.IPersistenceConfiguration;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.server.IEntityCacheService;
import com.pyx4j.server.contexts.NamespaceManager;

public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);

    private static final IEntityCacheService entityCacheService = ServerSideFactory.create(IEntityCacheService.class);

    private static boolean shutdown = false;

    private static Configuration configuration = createCacheConfiguration();

    private static boolean disabled = false;

    private static boolean namespaceAware = readMultitenancyConfig();

    private static Cache getCache() {
        if (shutdown) {
            throw new Error("Cache already shutdown");
        }
        CacheManager mgr = CacheManager.create(configuration);

        String cacheName;
        if (namespaceAware) {
            cacheName = NamespaceManager.getNamespace();
        } else {
            cacheName = "pyx";
        }
        Cache cache = mgr.getCache(cacheName);
        if (cache == null) {
            synchronized (CacheService.class) {
                cache = mgr.getCache(cacheName);
                if (cache == null) {
                    int maxElementsInMemory = 10 * 1024;
                    int timeToLiveSeconds = 2 * Consts.HOURS2SEC;
                    cache = new Cache(cacheName, maxElementsInMemory, false, false, timeToLiveSeconds, timeToLiveSeconds);
                    mgr.addCache(cache);
                }
            }
        }
        return cache;
    }

    private static boolean readMultitenancyConfig() {
        IPersistenceConfiguration cfg = ServerSideConfiguration.instance().getPersistenceConfiguration();
        if (cfg == null) {
            throw new RuntimeException("Persistence Configuration is not defined (is null) in class " + ServerSideConfiguration.instance().getClass().getName());
        }
        if (cfg instanceof com.pyx4j.entity.rdb.cfg.Configuration) {
            return ((com.pyx4j.entity.rdb.cfg.Configuration) cfg).getMultitenancyType() != MultitenancyType.SingleTenant;
        } else {
            return false;
        }
    }

    private static Configuration createCacheConfiguration() {
        Configuration config = new Configuration();
        config.setUpdateCheck(false);
        return config;
    }

    public static IEntityCacheService entityCache() {
        return entityCacheService;
    }

    public static boolean isDisabled() {
        return disabled;
    }

    public static void setDisabled(boolean disabled) {
        CacheService.disabled = disabled;
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Object key) {
        if (disabled) {
            return null;
        }
        Element element = getCache().get(key);
        if (element != null) {
            return (T) element.getObjectValue();
        } else {
            return null;
        }
    }

    public static void put(Object key, Object value) {
        if (!disabled) {
            getCache().put(new Element(key, value));
        }
    }

    public static void remove(Object key) {
        getCache().remove(key);
    }

    public static void reset() {
        getCache().removeAll();
    }

    public static void resetAll() {
        List<CacheManager> knownCacheManagers = CacheManager.ALL_CACHE_MANAGERS;
        for (CacheManager cm : knownCacheManagers) {
            cm.removalAll();
        }
    }

    public static void shutdown() {
        log.info("CacheManager.shutdown");
        shutdown = true;
        List<CacheManager> knownCacheManagers = CacheManager.ALL_CACHE_MANAGERS;
        while (!knownCacheManagers.isEmpty()) {
            CacheManager.ALL_CACHE_MANAGERS.get(0).shutdown();
        }
    }
}
