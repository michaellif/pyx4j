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
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.shared.DatastoreReadOnlyRuntimeException;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.EntityGraph.ApplyMemberMethod;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.security.shared.SecurityController;

public class Persistence {

    public static synchronized IEntityPersistenceService service() {
        return PersistenceServicesFactory.getPersistenceService();
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEntity> void applyDatasetAccessRule(EntityQueryCriteria<T> criteria) {
        @SuppressWarnings("rawtypes")
        List<DatasetAccessRule> rules = SecurityController.getAccessRules(DatasetAccessRule.class, criteria.getEntityClass());
        if (rules != null) {
            for (DatasetAccessRule<T> rule : rules) {
                rule.applyRule(criteria);
            }
        }
    }

    public static <T extends IEntity> ICursorIterator<T> secureQuery(String encodedCursorReference, EntityQueryCriteria<T> criteria, AttachLevel attachLevel) {
        SecurityController.assertPermission(new EntityPermission(criteria.getEntityClass(), EntityPermission.READ));
        applyDatasetAccessRule(criteria);
        final ICursorIterator<T> unfiltered = service().query(encodedCursorReference, criteria, AttachLevel.Attached);
        return new ICursorIterator<T>() {

            @Override
            public boolean hasNext() {
                return unfiltered.hasNext();
            }

            @Override
            public T next() {
                T entity = unfiltered.next();
                SecurityController.assertPermission(EntityPermission.permissionRead(entity));
                return entity;
            }

            @Override
            public void remove() {
                unfiltered.remove();
            }

            @Override
            public String encodedCursorReference() {
                return unfiltered.encodedCursorReference();
            }

            @Override
            public void close() {
                unfiltered.close();
            }
        };
    }

    public static <T extends IEntity> EntitySearchResult<T> secureQuery(EntityListCriteria<T> criteria) {
        EntitySearchResult<T> r = new EntitySearchResult<T>();
        final ICursorIterator<T> unfiltered = secureQuery(null, criteria, AttachLevel.Attached);
        try {
            while (unfiltered.hasNext()) {
                T ent = unfiltered.next();
                r.add(ent);
                if ((criteria.getPageSize() > 0) && r.getData().size() >= criteria.getPageSize()) {
                    break;
                }
            }
            // The position is important, hasNext may retrieve one more row.
            r.setEncodedCursorReference(unfiltered.encodedCursorReference());
            r.hasMoreData(unfiltered.hasNext());
        } finally {
            unfiltered.close();
        }
        r.setTotalRows(service().count(criteria));
        return r;
    }

    public static <T extends IEntity> Vector<T> secureQuery(EntityQueryCriteria<T> criteria, AttachLevel attachLevel) {
        SecurityController.assertPermission(new EntityPermission(criteria.getEntityClass(), EntityPermission.READ));
        applyDatasetAccessRule(criteria);
        List<T> rc = service().query(criteria, attachLevel);
        Vector<T> v = new Vector<T>();
        for (T ent : rc) {
            SecurityController.assertPermission(EntityPermission.permissionRead(ent));
            v.add(ent);
        }
        return v;
    }

    public static <T extends IEntity> Vector<T> secureQuery(EntityQueryCriteria<T> criteria) {
        return secureQuery(criteria, AttachLevel.Attached);
    }

    public static <T extends IEntity> T secureRetrieve(EntityQueryCriteria<T> criteria) {
        SecurityController.assertPermission(new EntityPermission(criteria.getEntityClass(), EntityPermission.READ));
        applyDatasetAccessRule(criteria);
        T ent = service().retrieve(criteria);
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
            throw new DatastoreReadOnlyRuntimeException(ServerSideConfiguration.instance().getApplicationMaintenanceMessage());
        }
        if (entity.getPrimaryKey() == null) {
            SecurityController.assertPermission(EntityPermission.permissionCreate(entity));
        } else {
            SecurityController.assertPermission(EntityPermission.permissionUpdate(entity));
        }

        //TODO we should apply DatasetAccessRule

        Persistence.service().merge(entity);
    }

    public static <T extends IVersionedEntity<?>> T retriveFinalOrDraft(Class<T> entityClass, Key primaryKey, AttachLevel attachLevel) {
        T entity = service().retrieve(entityClass, primaryKey.asCurrentKey());
        if ((entity != null) && !entity.version().isNull()) {
            return entity;
        } else {
            return service().retrieve(entityClass, primaryKey.asDraftKey());
        }
    }

    @Deprecated
    public static <T extends IVersionedEntity<?>> T secureRetrieveDraft(Class<T> entityClass, Key primaryKey) {
        // TODO  vlads
        return retrieveDraftForEdit(entityClass, primaryKey);
    }

    public static <T extends IEntity> T retrieveUnique(EntityQueryCriteria<T> criteria, AttachLevel attachLevel) throws UniqueConstraintUserRuntimeException {
        ICursorIterator<T> cursor = service().query(null, criteria, attachLevel);
        T enttity = null;
        try {
            if (cursor.hasNext()) {
                enttity = cursor.next();
                if (cursor.hasNext()) {
                    throw new UniqueConstraintUserRuntimeException(SimpleMessageFormat.format("More then one {0} found matching search criteria {1}", criteria
                            .proto().getEntityMeta().getCaption(), criteria.toStringForUser()), criteria.proto());
                }
            }
            return enttity;
        } finally {
            cursor.close();
        }
    }

    public static <T extends IVersionedEntity<?>> T secureRetrieveDraftForEdit(Class<T> entityClass, Key primaryKey) {
        T entity = secureRetrieve(entityClass, primaryKey.asDraftKey());
        if ((entity == null) || entity.version().isNull()) {
            entity = secureRetrieve(entityClass, primaryKey.asCurrentKey());
            retrieveOwned(entity.version());
            entity.version().set(EntityGraph.businessDuplicate(entity.version()));
            VersionedEntityUtils.setAsDraft(entity.version());
            entity.setPrimaryKey(primaryKey.asDraftKey());
        }
        return entity;
    }

    public static <T extends IVersionedEntity<?>> T retrieveDraftForEdit(Class<T> entityClass, Key primaryKey) {
        T entity = service().retrieve(entityClass, primaryKey.asDraftKey());
        if ((entity == null) || entity.version().isNull()) {
            entity = service().retrieve(entityClass, primaryKey.asCurrentKey());
            retrieveOwned(entity.version());
            entity.version().set(EntityGraph.businessDuplicate(entity.version()));
            VersionedEntityUtils.setAsDraft(entity.version());
            entity.setPrimaryKey(primaryKey.asDraftKey());
        }
        return entity;
    }

    public static <T extends IEntity> void retrieveOwned(final T rootEntity) {
        EntityGraph.applyRecursively(rootEntity, new ApplyMemberMethod() {
            @Override
            public boolean apply(IEntity entity) {
                if ((rootEntity == entity) || (entity.getMeta().isOwnedRelationships() && entity.getMeta().isCascadePersist())) {
                    if (entity.isValueDetached()) {
                        service().retrieve(entity);
                    }
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean apply(ICollection<IEntity, ?> memberCollection) {
                if (memberCollection.getMeta().isOwnedRelationships() && memberCollection.getMeta().isCascadePersist()) {
                    ensureRetrieve(memberCollection, AttachLevel.Attached);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public static <T extends IEntity> void ensureRetrieve(T entityMember, AttachLevel attachLevel) {
        if ((entityMember.getAttachLevel() == AttachLevel.Detached) && (entityMember.getOwner() != null) && (entityMember.getOwner().isValueDetached())) {
            ensureRetrieve(entityMember.getOwner(), AttachLevel.Attached);
        }
        if (entityMember.getAttachLevel() == AttachLevel.Detached) {
            service().retrieveMember(entityMember, attachLevel);
        } else {
            if (entityMember.getAttachLevel().ordinal() < attachLevel.ordinal()) {
                service().retrieve(entityMember, attachLevel, false);
            }
        }
    }

    public static <T extends IEntity> void ensureRetrieve(ICollection<T, ?> collectionMember, AttachLevel attachLevel) {
        if ((collectionMember.getAttachLevel() == AttachLevel.Detached) && (collectionMember.getOwner() != null)
                && (collectionMember.getOwner().isValueDetached())) {
            ensureRetrieve(collectionMember.getOwner(), AttachLevel.Attached);
        }
        if (collectionMember.getAttachLevel() == AttachLevel.Detached) {
            service().retrieveMember(collectionMember, attachLevel);
        } else {
            if (collectionMember.getAttachLevel().ordinal() < attachLevel.ordinal()) {
                service().retrieve(collectionMember, attachLevel);
            }
            for (T entity : collectionMember) {
                ensureRetrieve(entity, attachLevel);
            }
        }
    }
}
