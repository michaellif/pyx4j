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
import org.xnap.commons.i18n.I18n;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.search.IndexedEntitySearch;
import com.pyx4j.entity.server.search.SearchResultIterator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.i18n.shared.I18nFactory;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;

public class EntityServicesImpl {

    private static final Logger log = LoggerFactory.getLogger(EntityServicesImpl.class);

    private static I18n i18n = I18nFactory.getI18n();

    public static String applicationReadOnlyMessage() {
        return i18n.tr("Application is in read-only due to short maintenance.\nPlease try again in one hour");
    }

    public static class SaveImpl implements EntityServices.Save {

        @Override
        public IEntity execute(IEntity request) {
            if (ServerSideConfiguration.instance().datastoreReadOnly()) {
                throw new UnRecoverableRuntimeException(applicationReadOnlyMessage());
            }
            if (request.getPrimaryKey() == null) {
                SecurityController.assertPermission(EntityPermission.permissionCreate(request));
            } else {
                SecurityController.assertPermission(EntityPermission.permissionUpdate(request));
            }
            PersistenceServicesFactory.getPersistenceService().persist(request);
            return request;
        }
    }

    public static class MergeSaveImpl implements EntityServices.MergeSave {

        @Override
        public IEntity execute(IEntity request) {
            if (ServerSideConfiguration.instance().datastoreReadOnly()) {
                throw new UnRecoverableRuntimeException(applicationReadOnlyMessage());
            }
            if (request.getPrimaryKey() == null) {
                SecurityController.assertPermission(EntityPermission.permissionCreate(request));
            } else {
                SecurityController.assertPermission(EntityPermission.permissionUpdate(request));
            }
            PersistenceServicesFactory.getPersistenceService().merge(request);
            return request;
        }
    }

    public static class SaveListImpl implements EntityServices.SaveList {

        @Override
        public Vector<? extends IEntity> execute(Vector<? extends IEntity> request) {
            if (ServerSideConfiguration.instance().datastoreReadOnly()) {
                throw new UnRecoverableRuntimeException(applicationReadOnlyMessage());
            }
            for (IEntity ent : request) {
                if (ent.getPrimaryKey() == null) {
                    SecurityController.assertPermission(EntityPermission.permissionCreate(ent));
                } else {
                    SecurityController.assertPermission(EntityPermission.permissionUpdate(ent));
                }
            }
            PersistenceServicesFactory.getPersistenceService().persist(request);

            return request;
        }

    }

    public static class QueryImpl implements EntityServices.Query {

        @SuppressWarnings("unchecked")
        @Override
        public Vector execute(EntityQueryCriteria request) {
            SecurityController.assertPermission(new EntityPermission(request.getEntityClass(), EntityPermission.READ));
            List<IEntity> rc = PersistenceServicesFactory.getPersistenceService().query(request);
            Vector<IEntity> v = new Vector<IEntity>();
            for (IEntity ent : rc) {
                SecurityController.assertPermission(EntityPermission.permissionRead(ent));
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

            SecurityController.assertPermission(new EntityPermission(request.getEntityClass(), EntityPermission.READ));
            IndexedEntitySearch search = new IndexedEntitySearch(request);
            search.buildQueryCriteria();
            EntitySearchResult<IEntity> r = new EntitySearchResult<IEntity>();
            SearchResultIterator<IEntity> it = search.getResult(request.getEncodedCursorReference());
            r.setEncodedCursorReference(it.encodedCursorReference());
            while (it.hasNext()) {
                IEntity ent = it.next();
                SecurityController.assertPermission(EntityPermission.permissionRead(ent));
                r.add(ent);
                if (System.nanoTime() > start + 20L * Consts.SEC2NANO) {
                    r.setQuotaExceeded(true);
                    break;
                }
            }
            r.hasMoreData(it.hasMoreData());
            it.completeRetrieval();

            if (r.getData().size() != 0) {
                log.debug("got {} ", r.getData().get(0));
            }

            long duration = System.nanoTime() - start;
            int callsCount = PersistenceServicesFactory.getPersistenceService().getDatastoreCallCount() - initCount;
            if (duration > Consts.SEC2NANO) {
                log.warn("Long running search {} took {}ms; calls " + callsCount, request.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
            } else {
                log.debug("search {} took {}ms; calls " + callsCount, request.getEntityClass(), (int) (duration / Consts.MSEC2NANO));
            }
            return r;
        }
    }

    public static class RetrieveImpl implements EntityServices.Retrieve {

        @Override
        public IEntity execute(EntityQueryCriteria<?> request) {
            SecurityController.assertPermission(new EntityPermission(request.getEntityClass(), EntityPermission.READ));
            IEntity ent;
            if (request instanceof EntityCriteriaByPK) {
                ent = PersistenceServicesFactory.getPersistenceService().retrieve(request.getEntityClass(), ((EntityCriteriaByPK<?>) request).getPrimaryKey());
            } else {
                ent = PersistenceServicesFactory.getPersistenceService().retrieve(request);
            }
            if (ent != null) {
                SecurityController.assertPermission(EntityPermission.permissionRead(ent));
            }
            return ent;
        }
    }

    public static class RetrieveByPKImpl implements EntityServices.RetrieveByPK {

        @Override
        public IEntity execute(EntityCriteriaByPK<?> request) {
            SecurityController.assertPermission(new EntityPermission(request.getEntityClass(), EntityPermission.READ));
            IEntity ent = PersistenceServicesFactory.getPersistenceService().retrieve(request.getEntityClass(), request.getPrimaryKey());
            if (ent != null) {
                SecurityController.assertPermission(EntityPermission.permissionRead(ent));
            }
            return ent;
        }
    }

    public static class DeleteImpl implements EntityServices.Delete {

        @Override
        public VoidSerializable execute(IEntity entity) {
            if (ServerSideConfiguration.instance().datastoreReadOnly()) {
                throw new UnRecoverableRuntimeException(applicationReadOnlyMessage());
            }
            SecurityController.assertPermission(new EntityPermission(entity.getObjectClass(), EntityPermission.DELETE));
            PersistenceServicesFactory.getPersistenceService().delete(entity);
            return null;
        }

    }

    public static class MergeDeleteImpl implements EntityServices.MergeDelete {

        @Override
        public VoidSerializable execute(IEntity entity) {
            if (ServerSideConfiguration.instance().datastoreReadOnly()) {
                throw new UnRecoverableRuntimeException(applicationReadOnlyMessage());
            }
            SecurityController.assertPermission(new EntityPermission(entity.getObjectClass(), EntityPermission.DELETE));
            IEntity actualEntity = PersistenceServicesFactory.getPersistenceService().retrieve(entity.getEntityMeta().getEntityClass(), entity.getPrimaryKey());
            SecurityController.assertPermission(EntityPermission.permissionRead(actualEntity));
            PersistenceServicesFactory.getPersistenceService().delete(entity);
            return null;
        }

    }
}
