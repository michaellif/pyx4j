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

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.ILooseVersioning;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.core.IVersionedEntity.SaveAction;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rdb.PersistenceContext;
import com.pyx4j.entity.shared.utils.EntityGraph;

public class TableModleVersioned {

    public static void retrieveVersion(PersistenceContext persistenceContext, Mappings mappings, IEntity entity, MemberOperationsMeta member) {
        IVersionedEntity<?> versionedEntity = (IVersionedEntity<?>) entity;
        //TODO build SQL here for faster performance
        @SuppressWarnings("unchecked")
        Class<? extends IVersionData<?>> targetEntityClass = (Class<? extends IVersionData<?>>) member.getMemberMeta().getValueClass();
        EntityQueryCriteria<? extends IVersionData<?>> criteria = EntityQueryCriteria.create(targetEntityClass);
        criteria.add(PropertyCriterion.eq(criteria.proto().holder(), entity));

        if ((versionedEntity.getPrimaryKey().getVersion() == Key.VERSION_DRAFT)) {
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

    public static SaveAction getSaveAction(IVersionedEntity<?> versionedEntity) {
        SaveAction defaultSaveAction = SaveAction.saveAsDraft;
        if (versionedEntity instanceof ILooseVersioning) {
            defaultSaveAction = SaveAction.saveNonVersioned;
        }
        return versionedEntity.saveAction().getValue(defaultSaveAction);
    }

    public static List<IVersionData<IVersionedEntity<?>>> update(PersistenceContext persistenceContext, Mappings mappings, IEntity entity, boolean newEntity,
            MemberOperationsMeta versionDataMember) {
        return new VersionDataUpdater(persistenceContext, mappings, entity, versionDataMember).update(newEntity);
    }

    private static class VersionDataUpdater {

        private final PersistenceContext persistenceContext;

        private final List<IVersionData<IVersionedEntity<?>>> update = new ArrayList<IVersionData<IVersionedEntity<?>>>();

        private final IVersionedEntity<?> versionedEntity;

        private final MemberOperationsMeta versionDataMember;

        private IVersionData<IVersionedEntity<?>> versionData;

        private final Class<? extends IVersionData<IVersionedEntity<?>>> versionDataEntityClass;

        private final TableModel tm;

        @SuppressWarnings("unchecked")
        VersionDataUpdater(PersistenceContext persistenceContext, Mappings mappings, IEntity entity, MemberOperationsMeta member) {
            this.persistenceContext = persistenceContext;
            this.versionedEntity = (IVersionedEntity<?>) entity;
            this.versionDataMember = member;
            this.versionData = (IVersionData<IVersionedEntity<?>>) member.getMember(entity);
            this.versionDataEntityClass = (Class<? extends IVersionData<IVersionedEntity<?>>>) member.getMemberMeta().getValueClass();
            this.tm = mappings.getTableModel(persistenceContext, versionDataEntityClass);
        }

        private List<IVersionData<IVersionedEntity<?>>> update(boolean newEntity) {

            versionData.holder().set(versionedEntity);
            versionData.createdByUserKey().setValue(persistenceContext.getCurrentUserKey());

            switch (getSaveAction(versionedEntity)) {
            case saveNonVersioned: {
                if (versionData.fromDate().isNull()) {
                    versionData.fromDate().setValue(persistenceContext.getTimeNow());
                }
                if (versionData.getPrimaryKey() == null) {
                    updateExistingCurrent();
                }

                //Save using EntityPersistenceService
                update.add(versionData);
            }
                break;
            case saveAsDraft: {
                // Save draft
                versionData.fromDate().setValue(null);
                versionData.toDate().setValue(null);
                versionData.versionNumber().setValue(null);

                // Find existing Draft
                IVersionData<IVersionedEntity<?>> existingDraft = getExistingDraft();

                if (existingDraft != null) {
                    if (!EqualsHelper.equals(versionData.getPrimaryKey(), existingDraft.getPrimaryKey())) {
                        throw new Error("Attempt to override draft " + existingDraft.getDebugExceptionInfoString() + " with other data "
                                + versionData.getDebugExceptionInfoString());
                    }
                } else {
                    versionData.setPrimaryKey(null);
                }

                //Save using EntityPersistenceService
                update.add(versionData);
                versionedEntity.setPrimaryKey(versionedEntity.getPrimaryKey().asDraftKey());
            }
                break;
            case saveAsFinal: {
                // Finalize do not creates new IVersionData from draft.

                // Find existing Draft
                IVersionData<IVersionedEntity<?>> existingDraft = getExistingDraft();

                if (existingDraft != null) {
                    if (!EqualsHelper.equals(versionData.getPrimaryKey(), existingDraft.getPrimaryKey())) {
                        versionData = EntityGraph.businessDuplicate(versionData);
                        versionData.setPrimaryKey(null);
                        versionData.holder().set(versionedEntity);
                        ((IEntity) versionDataMember.getMember(versionedEntity)).set(versionData);
                    }
                    //TODO make it work
                } else if (false && !newEntity && existingDraft == null) {
                    throw new Error("Can't finalize version when Draft was not created");
                } else if (versionData.getPrimaryKey() != null) {
                    // TODO optimize new item creation if no data changed; for now Finalize create new data anyway, 
                    versionData = EntityGraph.businessDuplicate(versionData);
                    versionData.setPrimaryKey(null);
                    versionData.holder().set(versionedEntity);
                    ((IEntity) versionDataMember.getMember(versionedEntity)).set(versionData);
                }
                versionData.fromDate().setValue(persistenceContext.getTimeNow());
                versionData.toDate().setValue(null);

                //Save using EntityPersistenceService
                update.add(versionData);

                updateExistingCurrent();

                versionedEntity.setPrimaryKey(versionedEntity.getPrimaryKey().asCurrentKey());
            }
                break;
            }
            versionedEntity.saveAction().setValue(null);
            return update;
        }

        private IVersionData<IVersionedEntity<?>> getExistingDraft() {
            EntityQueryCriteria<? extends IVersionData<IVersionedEntity<?>>> criteria = EntityQueryCriteria.create(versionDataEntityClass);
            criteria.add(PropertyCriterion.eq(criteria.proto().holder(), versionedEntity));
            criteria.isDraft(criteria.proto());
            List<? extends IVersionData<IVersionedEntity<?>>> draftsExisting = tm.query(persistenceContext, criteria, 2, AttachLevel.IdOnly);
            if (draftsExisting.size() > 1) {
                throw new Error("Duplicate Draft versions found in " + versionedEntity.getDebugExceptionInfoString());
            } else if (draftsExisting.size() > 0) {
                return draftsExisting.get(0);
            } else {
                return null;
            }
        }

        private void updateExistingCurrent() {
            IVersionData<IVersionedEntity<?>> memberEntityExisting = getExistingCurrent();
            if (memberEntityExisting != null) {
                // End effective period of currently active entity
                memberEntityExisting.toDate().setValue(persistenceContext.getTimeNow());
                update.add(memberEntityExisting);
                versionData.versionNumber().setValue(memberEntityExisting.versionNumber().getValue() + 1);
            } else {
                // Initial item creation
                versionData.versionNumber().setValue(1);
            }
        }

        private IVersionData<IVersionedEntity<?>> getExistingCurrent() {
            EntityQueryCriteria<? extends IVersionData<IVersionedEntity<?>>> criteria = EntityQueryCriteria.create(versionDataEntityClass);
            criteria.add(PropertyCriterion.eq(criteria.proto().holder(), versionedEntity));
            criteria.isCurrent(criteria.proto());
            List<? extends IVersionData<IVersionedEntity<?>>> existing = tm.query(persistenceContext, criteria, 1, AttachLevel.Attached);
            if (existing.size() > 0) {
                return existing.get(0);
            } else {
                return null;
            }

        }
    }
}
