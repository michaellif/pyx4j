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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityCacheService;
import com.pyx4j.server.contexts.NamespaceManager;

public class CacheService {

    private static final IEntityCacheService entityCacheService = ServerSideFactory.create(IEntityCacheService.class);

    private static Cache getCache() {
        Cache cache = CacheManager.getInstance().getCache(NamespaceManager.getNamespace());
        if (cache == null) {
            synchronized (CacheService.class) {
                cache = CacheManager.getInstance().getCache(NamespaceManager.getNamespace());
                if (cache == null) {
                    int maxElementsInMemory = 10 * 1024;
                    int timeToLiveSeconds = 2 * Consts.HOURS2SEC;
                    cache = new Cache(NamespaceManager.getNamespace(), maxElementsInMemory, false, false, timeToLiveSeconds, timeToLiveSeconds);
                    CacheManager.getInstance().addCache(cache);
                }
            }
        }
        return cache;
    }

    public static IEntityCacheService entityCache() {
        return entityCacheService;
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Object key) {
        Element element = getCache().get(key);
        if (element != null) {
            return (T) element.getObjectValue();
        } else {
            return null;
        }
    }

    public static void put(Object key, Object value) {
        getCache().put(new Element(key, value));
    }

    public static void remove(Object key) {
        getCache().remove(key);
    }

    public static void reset() {
        getCache().removeAll();
    }
}
