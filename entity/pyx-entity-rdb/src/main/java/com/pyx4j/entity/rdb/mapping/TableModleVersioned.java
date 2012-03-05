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

        if (versionedEntity.draft().isBooleanTrue()) {
            criteria.add(PropertyCriterion.isNull(criteria.proto().fromDate()));
            criteria.add(PropertyCriterion.isNull(criteria.proto().toDate()));
        } else {
            Date forDate = versionedEntity.forDate().getValue();
            if (forDate == null) {
                forDate = persistenceContext.getTimeNow();
                versionedEntity.forDate().setValue(forDate);
            }
            criteria.add(PropertyCriterion.le(criteria.proto().fromDate(), forDate));
            criteria.or(PropertyCriterion.gt(criteria.proto().toDate(), forDate), PropertyCriterion.isNull(criteria.proto().toDate()));
        }
        TableModel tm = mappings.getTableModel(persistenceContext.getConnection(), targetEntityClass);
        List<Key> keys = tm.queryKeys(persistenceContext, criteria, 1);

        IVersionData<?> memeberEntity = (IVersionData<?>) member.getMember(entity);
        if (keys.isEmpty()) {
            memeberEntity.set(null);
        } else {
            memeberEntity.setPrimaryKey(keys.get(0));
            memeberEntity.setValueDetached();
        }
    }

    public static List<IVersionData<IVersionedEntity<?>>> update(PersistenceContext persistenceContext, Mappings mappings, IEntity entity,
            MemberOperationsMeta member) {
        List<IVersionData<IVersionedEntity<?>>> update = new ArrayList<IVersionData<IVersionedEntity<?>>>();
        @SuppressWarnings("unchecked")
        IVersionData<IVersionedEntity<?>> memeberEntity = (IVersionData<IVersionedEntity<?>>) member.getMember(entity);
        if (memeberEntity.isNull()) {
            return update;
        }
        @SuppressWarnings("unchecked")
        Class<? extends IVersionData<IVersionedEntity<?>>> targetEntityClass = (Class<? extends IVersionData<IVersionedEntity<?>>>) member.getMemberMeta()
                .getValueClass();
        TableModel tm = mappings.getTableModel(persistenceContext.getConnection(), targetEntityClass);

        // Find existing Draft
        IVersionData<IVersionedEntity<?>> existingDraft = null;
        EntityQueryCriteria<? extends IVersionData<IVersionedEntity<?>>> draftCriteria = EntityQueryCriteria.create(targetEntityClass);
        draftCriteria.add(PropertyCriterion.eq(draftCriteria.proto().holder(), entity));
        draftCriteria.add(PropertyCriterion.isNull(draftCriteria.proto().fromDate()));
        draftCriteria.add(PropertyCriterion.isNull(draftCriteria.proto().toDate()));
        List<? extends IVersionData<IVersionedEntity<?>>> draftsExisting = tm.query(persistenceContext, draftCriteria, 1);
        if (draftsExisting.size() > 1) {
            throw new Error("Duplicate Draft versions found in " + entity.getDebugExceptionInfoString());
        } else if (draftsExisting.size() > 0) {
            existingDraft = draftsExisting.get(0);
        }

        IVersionedEntity<?> versionedEntity = (IVersionedEntity<?>) entity;
        memeberEntity.holder().set(versionedEntity);
        memeberEntity.createdByUserKey().setValue(persistenceContext.getCurrentUserKey());

        if (versionedEntity.draft().isBooleanTrue()) {
            // Save draft
            memeberEntity.fromDate().setValue(null);
            memeberEntity.toDate().setValue(null);
            memeberEntity.versionNumber().setValue(null);

            if (existingDraft != null) {
                memeberEntity.setPrimaryKey(existingDraft.getPrimaryKey());
            } else {
                memeberEntity.setPrimaryKey(null);
            }

            //Save using EntityPersistenceService
            update.add(memeberEntity);

        } else {
            // Finalize creates new IVersionData every time.
            memeberEntity.setPrimaryKey(null);
            memeberEntity.fromDate().setValue(persistenceContext.getTimeNow());
            memeberEntity.toDate().setValue(null);

            //Save using EntityPersistenceService
            update.add(memeberEntity);

            EntityQueryCriteria<? extends IVersionData<IVersionedEntity<?>>> criteria = EntityQueryCriteria.create(targetEntityClass);
            criteria.add(PropertyCriterion.eq(criteria.proto().holder(), entity));
            criteria.add(PropertyCriterion.isNotNull(criteria.proto().fromDate()));
            criteria.add(PropertyCriterion.isNull(criteria.proto().toDate()));

            List<? extends IVersionData<IVersionedEntity<?>>> existing = tm.query(persistenceContext, criteria, 1);
            if (existing.size() > 0) {
                IVersionData<IVersionedEntity<?>> memeberEntityExisting = existing.get(0);

                // End effective period of currently active entity
                memeberEntityExisting.toDate().setValue(persistenceContext.getTimeNow());
                update.add(memeberEntityExisting);

                memeberEntity.versionNumber().setValue(memeberEntityExisting.versionNumber().getValue() + 1);

                // Create new draft on Finalize
                IVersionData<IVersionedEntity<?>> newDraft = EntityGraph.businessDuplicate(memeberEntity);
                newDraft.fromDate().setValue(null);
                newDraft.toDate().setValue(null);
                newDraft.versionNumber().setValue(null);
                if (existingDraft != null) {
                    newDraft.setPrimaryKey(existingDraft.getPrimaryKey());
                }
                update.add(newDraft);

            } else {
                // Initial item creation
                memeberEntity.versionNumber().setValue(0);
                // Create new draft on Finalize
                IVersionData<IVersionedEntity<?>> newDraft = EntityGraph.businessDuplicate(memeberEntity);
                if (existingDraft != null) {
                    newDraft.setPrimaryKey(existingDraft.getPrimaryKey());
                }
                newDraft.fromDate().setValue(null);
                newDraft.toDate().setValue(null);
                newDraft.versionNumber().setValue(null);
                update.add(newDraft);
            }
        }
        return update;
    }
}
