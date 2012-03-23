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
 * Created on Aug 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.EntityGraph.ApplyMethod;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.shared.SecurityController;

public class Persistence {

    public static synchronized IEntityPersistenceService service() {
        return PersistenceServicesFactory.getPersistenceService();
    }

    public static <T extends IEntity> EntitySearchResult<T> secureQuery(EntityListCriteria<T> criteria) {
        SecurityController.assertPermission(new EntityPermission(criteria.getEntityClass(), EntityPermission.READ));
        @SuppressWarnings({ "rawtypes" })
        List<DatasetAccessRule> rules = SecurityController.getAccessRules(DatasetAccessRule.class, criteria.getEntityClass());
        if (rules != null) {
            for (DatasetAccessRule<T> rule : rules) {
                rule.applyRule(criteria);
            }
        }
        EntitySearchResult<T> r = new EntitySearchResult<T>();
        final ICursorIterator<T> unfiltered = service().query(null, criteria, AttachLevel.Attached);
        try {
            while (unfiltered.hasNext()) {
                T ent = unfiltered.next();
                SecurityController.assertPermission(EntityPermission.permissionRead(ent));
                r.add(ent);
                if ((criteria.getPageSize() > 0) && r.getData().size() >= criteria.getPageSize()) {
                    break;
                }
            }
            // The position is important, hasNext may retrieve one more row. 
            r.setEncodedCursorReference(unfiltered.encodedCursorReference());
            r.hasMoreData(unfiltered.hasNext());
        } finally {
            unfiltered.completeRetrieval();
        }

        r.setTotalRows(service().count(criteria));

        return r;
    }

    public static <T extends IEntity> Vector<T> secureQuery(EntityQueryCriteria<T> criteria) {
        SecurityController.assertPermission(new EntityPermission(criteria.getEntityClass(), EntityPermission.READ));

        @SuppressWarnings({ "rawtypes" })
        List<DatasetAccessRule> rules = SecurityController.getAccessRules(DatasetAccessRule.class, criteria.getEntityClass());
        if (rules != null) {
            for (DatasetAccessRule<T> rule : rules) {
                rule.applyRule(criteria);
            }
        }

        List<T> rc = PersistenceServicesFactory.getPersistenceService().query(criteria);
        Vector<T> v = new Vector<T>();
        for (T ent : rc) {
            SecurityController.assertPermission(EntityPermission.permissionRead(ent));
            v.add(ent);
        }
        return v;
    }

    public static <T extends IEntity> T secureRetrieve(EntityQueryCriteria<T> criteria) {
        SecurityController.assertPermission(new EntityPermission(criteria.getEntityClass(), EntityPermission.READ));
        @SuppressWarnings({ "rawtypes" })
        List<DatasetAccessRule> rules = SecurityController.getAccessRules(DatasetAccessRule.class, criteria.getEntityClass());
        if (rules != null) {
            criteria = new EntityQueryCriteria<T>(criteria);
            for (DatasetAccessRule<T> rule : rules) {
                rule.applyRule(criteria);
            }
        }

        T ent;
        if (criteria instanceof EntityCriteriaByPK) {
            ent = PersistenceServicesFactory.getPersistenceService().retrieve(criteria.getEntityClass(), ((EntityCriteriaByPK<?>) criteria).getPrimaryKey());
        } else {
            ent = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        }
        if (ent != null) {
            SecurityController.assertPermission(EntityPermission.permissionRead(ent));
        }
        return ent;
    }

    public static <T extends IEntity> T secureRetrieve(Class<T> entityClass, Key primaryKey) {
        return secureRetrieve(EntityCriteriaByPK.create(entityClass, primaryKey));
    }

    public static <T extends IEntity> void secureSave(T entity) {
        if (ServerSideConfiguration.instance().datastoreReadOnly()) {
            throw new UserRuntimeException(ServerSideConfiguration.instance().getApplicationMaintenanceMessage());
        }
        if (entity.getPrimaryKey() == null) {
            SecurityController.assertPermission(EntityPermission.permissionCreate(entity));
        } else {
            SecurityController.assertPermission(EntityPermission.permissionUpdate(entity));
        }

        //TODO we should apply DatasetAccessRule

        Persistence.service().merge(entity);
    }

    public static <T extends IVersionedEntity<?>> T secureRetrieveDraft(Class<T> entityClass, Key primaryKey) {
        // TODO  vlads
        return retrieveDraft(entityClass, primaryKey);
    }

    public static <T extends IVersionedEntity<?>> T retrieveDraft(Class<T> entityClass, Key primaryKey) {
        T entity = service().retrieve(entityClass, primaryKey.asDraftKey());
        if (entity.version().isNull()) {
            entity = service().retrieve(entityClass, primaryKey.asCurrentKey());
            retrieveOwned(entity.version());
            entity.version().set(EntityGraph.businessDuplicate(entity.version()));
            VersionedEntityUtils.setAsDraft(entity.version());
            entity.setPrimaryKey(primaryKey.asDraftKey());
        }
        return entity;
    }

    public static <T extends IEntity> void retrieveOwned(final T rootEntity) {
        EntityGraph.applyRecursively(rootEntity, new ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                if ((rootEntity == entity) || entity.getMeta().isOwnedRelationships()) {
                    if (entity.isValueDetached()) {
                        service().retrieve(entity);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

}
