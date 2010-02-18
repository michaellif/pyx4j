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
 * Created on Feb 4, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.server;

import java.util.Collections;
import java.util.List;

import javax.cache.Cache;
import javax.cache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.rpc.SiteRequest;
import com.pyx4j.site.rpc.SiteServices;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.site.shared.domain.Site;

public class SiteServicesImpl implements SiteServices {

    private static Logger log = LoggerFactory.getLogger(SiteServicesImpl.class);

    private static final String KEY_PREFIX = "pyx.site.";

    public static class SaveImpl implements SiteServices.Save {

        @Override
        public IEntity<?> execute(IEntity<?> request) {
            if (request instanceof Site) {
                ((Site) request).updateTimestamp().setValue(System.currentTimeMillis());
            }
            IEntity<?> entity = new EntityServicesImpl.SaveImpl().execute(request);
            //Update Cache and change updateTimestamp
            if (entity instanceof Site) {
                Site site = PersistenceServicesFactory.getPersistenceService().retrieve(Site.class, entity.getPrimaryKey());
                setCache(site);
                entity = site;
            } else {
                EntityCriteria<Site> criteria = EntityCriteria.create(Site.class);
                List<Site> sites = PersistenceServicesFactory.getPersistenceService().query(criteria);
                for (Site site : sites) {
                    boolean updated = false;
                    if (request instanceof Page) {
                        if (site.pages().contains(request)) {
                            updated = true;
                        }
                    } else if (request instanceof Portlet) {
                        pages: for (Page p : site.pages()) {
                            if ((p.data().leftPortlets().contains(request)) || (p.data().rightPortlets().contains(request))) {
                                updated = true;
                                break pages;
                            }
                        }
                    } else {
                        log.warn("unknown object type", request.getObjectClass());
                        updated = true;
                    }
                    if (updated) {
                        site.updateTimestamp().setValue(System.currentTimeMillis());
                        PersistenceServicesFactory.getPersistenceService().persist(site);
                    }
                    setCache(site);
                }
            }
            return entity;
        }

    }

    @SuppressWarnings("unchecked")
    private static void setCache(Site site) {
        try {
            Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
            cache.put(KEY_PREFIX + site.siteId().getValue(), site);
        } catch (Throwable e) {
            log.error("Cache set error", e);
        }
    }

    public static void resetCache(String siteId) {
        try {
            Cache cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
            cache.remove(KEY_PREFIX + siteId);
        } catch (Throwable e) {
            log.error("Cache set error", e);
        }
    }

    public static class RetrieveImpl implements SiteServices.Retrieve {

        @Override
        public Site execute(SiteRequest request) {
            Cache cache = null;
            try {
                long start = System.nanoTime();
                cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
                Object cachedSite = cache.get(KEY_PREFIX + request.getSiteId());
                if (cachedSite instanceof Site) {
                    Site site = (Site) cachedSite;
                    if (request.getModificationTime() == site.updateTimestamp().getValue()) {
                        log.debug("Cached site is not updated, send null value, took {}ms", (int) (System.nanoTime() - start) / Consts.MSEC2NANO);
                        return EntityFactory.create(Site.class);
                    } else {
                        log.debug("Send Cached site value, took {}ms", (int) (System.nanoTime() - start) / Consts.MSEC2NANO);
                        return site;
                    }
                }
            } catch (Throwable e) {
                log.error("Cache access error", e);
            }

            EntityCriteria<Site> criteria = EntityCriteria.create(Site.class);
            Site siteMeta = EntityFactory.create(Site.class);
            criteria.add(PropertyCriterion.eq(siteMeta.siteId(), request.getSiteId()));
            Site site = (Site) new EntityServicesImpl.RetrieveImpl().execute(criteria);
            if ((site != null) && (cache != null)) {
                // Store for future use.
                try {
                    cache.put(KEY_PREFIX + request.getSiteId(), site);
                } catch (Throwable e) {
                    // GAE read-only mode will produce "Policy prevented put operation"
                    // TODO log once.
                    log.error("Cache set error", e);
                }
            }

            if (site != null) {
                if (site.updateTimestamp().isNull()) {
                    throw new Error("Site data error, updateTimestamp is null");
                } else if (request.getModificationTime() == site.updateTimestamp().getValue()) {
                    log.debug("Site is not updated, send null value");
                    return siteMeta;
                }
            }
            return site;
        }
    }

}
