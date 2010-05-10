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
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.search.IndexedEntitySearch;
import com.pyx4j.entity.server.search.SearchResultIterator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.security.shared.SecurityController;

public class EntityServicesImpl {

    private static final Logger log = LoggerFactory.getLogger(EntityServicesImpl.class);

    public static class SaveImpl implements EntityServices.Save {

        @Override
        public IEntity execute(IEntity request) {
            if (request.getPrimaryKey() == null) {
                SecurityController.assertPermission(EntityPermission.permissionCreate(request.getObjectClass()));
            } else {
                SecurityController.assertPermission(EntityPermission.permissionUpdate(request.getObjectClass()));
            }
            PersistenceServicesFactory.getPersistenceService().persist(request);
            return request;
        }
    }

    public static class QueryImpl implements EntityServices.Query {

        @SuppressWarnings("unchecked")
        @Override
        public Vector execute(EntityQueryCriteria request) {
            SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));
            List<IEntity> rc = PersistenceServicesFactory.getPersistenceService().query(request);
            Vector<IEntity> v = new Vector<IEntity>();
            for (IEntity ent : rc) {
                SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
                v.add(ent);
            }
            return v;
        }
    }

    /**
     * Implementations is application specific and in most cases should be overridden.
     * Class IndexedEntitySearch was made the most generic and its logic should be
     * reusable.
     */
    public static class SearchImpl implements EntityServices.Search {

        @Override
        public EntitySearchResult<?> execute(EntitySearchCriteria<?> request) {
            long start = System.nanoTime();
            int initCount = PersistenceServicesFactory.getPersistenceService().getDatastoreCallCount();

            SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));
            IndexedEntitySearch search = new IndexedEntitySearch(request);
            search.buildQueryCriteria();
            EntitySearchResult<IEntity> r = new EntitySearchResult<IEntity>();
            SearchResultIterator<IEntity> it = search.getResult(null);
            while (it.hasNext()) {
                IEntity ent = it.next();
                SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
                r.add(ent);
                if (System.nanoTime() > start + Consts.SEC2NANO * 20) {
                    r.setQuotaExceeded(true);
                    break;
                }
            }
            r.hasMoreData(it.hasMoreData());

            long duration = System.nanoTime() - start;
            int callsCount = PersistenceServicesFactory.getPersistenceService().getDatastoreCallCount() - initCount;
            if (duration > Consts.SEC2NANO) {
                log.warn("Long running search {} took {}ms; calls " + callsCount, request.getDomainName(), (int) (duration / Consts.MSEC2NANO));
            } else {
                log.debug("search {} took {}ms; calls " + callsCount, request.getDomainName(), (int) (duration / Consts.MSEC2NANO));
            }
            return r;
        }
    }

    public static class RetrieveImpl implements EntityServices.Retrieve {

        @SuppressWarnings("unchecked")
        @Override
        public IEntity execute(EntityQueryCriteria request) {
            SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));
            IEntity ent;
            if (request instanceof EntityCriteriaByPK) {
                ent = PersistenceServicesFactory.getPersistenceService().retrieve(ServerEntityFactory.entityClass(request.getDomainName()),
                        ((EntityCriteriaByPK) request).getPrimaryKey());
            } else {
                ent = PersistenceServicesFactory.getPersistenceService().retrieve(request);
            }
            if (ent != null) {
                SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
            }
            return ent;
        }
    }

    public static class RetrieveByPKImpl implements EntityServices.RetrieveByPK {

        @SuppressWarnings("unchecked")
        @Override
        public IEntity execute(EntityCriteriaByPK request) {
            SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));
            IEntity ent = PersistenceServicesFactory.getPersistenceService().retrieve(ServerEntityFactory.entityClass(request.getDomainName()),
                    request.getPrimaryKey());
            if (ent != null) {
                SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
            }
            return ent;
        }
    }

}
