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
 * Created on Feb 27, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;

public class TableModleVersioned {

    public static void retrieveVersion(PersistenceContext persistenceContext, Mappings mappings, IEntity entity, MemberOperationsMeta member) {
        IVersionedEntity<?> versionedEntity = (IVersionedEntity<?>) entity;
        //TODO build SQL here for faster performance
        @SuppressWarnings("unchecked")
        Class<? extends IVersionData<?>> targetEntityClass = (Class<? extends IVersionData<?>>) member.getMemberMeta().getValueClass();
        EntityQueryCriteria<? extends IVersionData<?>> criteria = EntityQueryCriteria.create(targetEntityClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().holder(), entity));

        if (versionedEntity.getPrimaryKey().getVersion() == Key.VERSION_DRAFT) {
            criteria.add(PropertyCriterion.isNull(criteria.proto().fromDate()));
            criteria.add(PropertyCriterion.isNull(criteria.proto().toDate()));
        } else {
            Date forDate;
            if (versionedEntity.getPrimaryKey().getVersion() == Key.VERSION_CURRENT) {
                forDate = persistenceContext.getTimeNow();
            } else {
                forDate = new java.util.Date(versionedEntity.getPrimaryKey().getVersion());
            }
            criteria.add(PropertyCriterion.le(criteria.proto().fromDate(), forDate));
            criteria.or(PropertyCriterion.gt(criteria.proto().toDate(), forDate), PropertyCriterion.isNull(criteria.proto().toDate()));
        }
        TableModel tm = mappings.getTableModel(persistenceContext, targetEntityClass);
        List<Key> keys = tm.queryKeys(persistenceContext, criteria, 1);
        IVersionData<?> memberEntity = (IVersionData<?>) member.getMember(entity);

        if (keys.isEmpty()) {
            memberEntity.set(null);
        } else {
            memberEntity.setPrimaryKey(keys.get(0));
            memberEntity.setValueDetached();
        }
    }

    public static List<IVersionData<IVersionedEntity<?>>> update(PersistenceContext persistenceContext, Mappings mappings, IEntity entity, boolean newEntity,
            MemberOperationsMeta member) {

        List<IVersionData<IVersionedEntity<?>>> update = new ArrayList<IVersionData<IVersionedEntity<?>>>();
        IVersionedEntity<?> versionedEntity = (IVersionedEntity<?>) entity;

        @SuppressWarnings("unchecked")
        IVersionData<IVersionedEntity<?>> memberEntity = (IVersionData<IVersionedEntity<?>>) member.getMember(entity);
        // Force creation of the data entity on finalize
        if (memberEntity.isNull() && (versionedEntity.saveAction().getValue() != SaveAction.saveAsFinal)) {
            return update;
        }
        @SuppressWarnings("unchecked")
        Class<? extends IVersionData<IVersionedEntity<?>>> targetEntityClass = (Class<? extends IVersionData<IVersionedEntity<?>>>) member.getMemberMeta()
                .getValueClass();
        TableModel tm = mappings.getTableModel(persistenceContext, targetEntityClass);

        // Find existing Draft
        IVersionData<IVersionedEntity<?>> existingDraft = null;
        EntityQueryCriteria<? extends IVersionData<IVersionedEntity<?>>> draftCriteria = EntityQueryCriteria.create(targetEntityClass);
        draftCriteria.add(PropertyCriterion.eq(draftCriteria.proto().holder(), entity));
        draftCriteria.add(PropertyCriterion.isNull(draftCriteria.proto().fromDate()));
        draftCriteria.add(PropertyCriterion.isNull(draftCriteria.proto().toDate()));
        List<? extends IVersionData<IVersionedEntity<?>>> draftsExisting = tm.query(persistenceContext, draftCriteria, 2);
        if (draftsExisting.size() > 1) {
            throw new Error("Duplicate Draft versions found in " + entity.getDebugExceptionInfoString());
        } else if (draftsExisting.size() > 0) {
            existingDraft = draftsExisting.get(0);
        }

        memberEntity.holder().set(versionedEntity);
        memberEntity.createdByUserKey().setValue(persistenceContext.getCurrentUserKey());

        if (versionedEntity.saveAction().getValue() != SaveAction.saveAsFinal) {
            // Save draft
            memberEntity.fromDate().setValue(null);
            memberEntity.toDate().setValue(null);
            memberEntity.versionNumber().setValue(null);

            if (existingDraft != null) {
                if (!memberEntity.getPrimaryKey().equals(existingDraft.getPrimaryKey())) {
                    throw new Error("Attempt to override draft " + existingDraft.getDebugExceptionInfoString() + " with other data "
                            + memberEntity.getDebugExceptionInfoString());
                }
            } else {
                memberEntity.setPrimaryKey(null);
            }

            //Save using EntityPersistenceService
            update.add(memberEntity);
            entity.setPrimaryKey(entity.getPrimaryKey().asDraftKey());
        } else {
            // Finalize do not creates new IVersionData from draft.
            if (existingDraft != null) {
                if (!memberEntity.getPrimaryKey().equals(existingDraft.getPrimaryKey())) {
                    memberEntity = EntityGraph.businessDuplicate(memberEntity);
                    memberEntity.setPrimaryKey(null);
                    memberEntity.holder().set(versionedEntity);
                    ((IEntity) member.getMember(versionedEntity)).set(memberEntity);
                }
                //TODO make it work
            } else if (false && !newEntity && existingDraft == null) {
                throw new Error("Can't finalize version when Draft was not created");
            } else if (memberEntity.getPrimaryKey() != null) {
                // TODO optimize new item creation if no data changed; for now Finalize create new data anyway, 
                memberEntity = EntityGraph.businessDuplicate(memberEntity);
                memberEntity.setPrimaryKey(null);
                memberEntity.holder().set(versionedEntity);
                ((IEntity) member.getMember(versionedEntity)).set(memberEntity);
            }
            memberEntity.fromDate().setValue(persistenceContext.getTimeNow());
            memberEntity.toDate().setValue(null);

            //Save using EntityPersistenceService
            update.add(memberEntity);

            EntityQueryCriteria<? extends IVersionData<IVersionedEntity<?>>> criteriaCurrent = EntityQueryCriteria.create(targetEntityClass);
            criteriaCurrent.add(PropertyCriterion.eq(criteriaCurrent.proto().holder(), entity));
            criteriaCurrent.add(PropertyCriterion.isNotNull(criteriaCurrent.proto().fromDate()));
            criteriaCurrent.add(PropertyCriterion.isNull(criteriaCurrent.proto().toDate()));

            List<? extends IVersionData<IVersionedEntity<?>>> existing = tm.query(persistenceContext, criteriaCurrent, 1);
            if (existing.size() > 0) {
                IVersionData<IVersionedEntity<?>> memberEntityExisting = existing.get(0);

                // End effective period of currently active entity
                memberEntityExisting.toDate().setValue(persistenceContext.getTimeNow());
                update.add(memberEntityExisting);

                memberEntity.versionNumber().setValue(memberEntityExisting.versionNumber().getValue() + 1);

            } else {
                // Initial item creation
                memberEntity.versionNumber().setValue(1);
            }
            entity.setPrimaryKey(entity.getPrimaryKey().asCurrentKey());
        }
        versionedEntity.saveAction().setValue(null);
        return update;
    }
}
