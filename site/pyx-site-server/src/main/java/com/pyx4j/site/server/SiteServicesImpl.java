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

import javax.cache.Cache;
import javax.cache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.shared.EntityCriteria;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.rpc.SiteRequest;
import com.pyx4j.site.rpc.SiteServices;
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
            //TODO reset Cache and change updateTimestamp
            if (entity instanceof Site) {
                setCache((Site) entity);
            }
            return entity;
        }

    }

    private static void setCache(Site site) {
        Cache cache = null;
        try {
            cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
            cache.put(KEY_PREFIX + site.siteId().getValue(), site);
        } catch (Throwable e) {
            log.error("Cache set error", e);
        }
    }

    public static class RetrieveImpl implements SiteServices.Retrieve {

        @Override
        public Site execute(SiteRequest request) {
            Cache cache = null;
            try {
                cache = CacheManager.getInstance().getCacheFactory().createCache(Collections.emptyMap());
                Object cachedSite = cache.get(KEY_PREFIX + request.getSiteId());
                if (cachedSite instanceof Site) {
                    Site site = (Site) cachedSite;
                    if (request.getModificationTime() == site.updateTimestamp().getValue()) {
                        log.debug("Cached site is not updated, send null value");
                        return EntityFactory.create(Site.class);
                    } else {
                        log.debug("Send Cached site value");
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
            if (cache != null) {
                // Store for future use.
                cache.put(KEY_PREFIX + request.getSiteId(), site);
            }

            if (request.getModificationTime() == site.updateTimestamp().getValue()) {
                log.debug("Site is not updated, send null value");
                return siteMeta;
            } else {
                return site;
            }
        }
    }

}
