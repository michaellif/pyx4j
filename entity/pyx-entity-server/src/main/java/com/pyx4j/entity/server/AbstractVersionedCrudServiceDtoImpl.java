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
 * Created on Mar 16, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import org.apache.commons.lang.Validate;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.core.IVersionedEntity.SaveAction;
import com.pyx4j.entity.rpc.AbstractVersionedCrudService;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.rpc.shared.VoidSerializable;

public abstract class AbstractVersionedCrudServiceDtoImpl<E extends IVersionedEntity<?>, TO extends IVersionedEntity<?>> extends
        AbstractCrudServiceDtoImpl<E, TO> implements AbstractVersionedCrudService<TO> {

    protected AbstractVersionedCrudServiceDtoImpl(Class<E> entityClass, Class<TO> dtoClass) {
        super(entityClass, dtoClass);
    }

    @Override
    protected E retrieve(Key entityId, RetrieveTarget retrieveTarget) {
        Key primaryKey;
        // Force draft for edit
        if (retrieveTarget == RetrieveTarget.Edit) {
            primaryKey = entityId.asDraftKey();
        } else {
            primaryKey = entityId;
        }
        E entity = Persistence.secureRetrieve(boClass, primaryKey);
        if (primaryKey.isDraft() && (entity == null)) {
            entity = super.retrieve(primaryKey.asCurrentKey(), retrieveTarget);
        } else if (primaryKey.getVersion() == Key.VERSION_CURRENT && entity.version().isNull()) {
            entity = super.retrieve(primaryKey.asDraftKey(), retrieveTarget);
        }
        return entity;
    }

    @Override
    protected E retrieveForSave(TO to) {
        Validate.isTrue(to.getPrimaryKey().getVersion() == Key.VERSION_DRAFT);
        E entity = super.retrieveForSave(to);
        if (entity.version().isNull()) {
            to.setPrimaryKey(to.getPrimaryKey().asCurrentKey());
            entity = super.retrieveForSave(to);
            entity.version().set(EntityGraph.businessDuplicate(entity.version()));
            VersionedEntityUtils.setAsDraft(entity.version());
        }
        return entity;
    }

    @Override
    public void retrieve(final AsyncCallback<TO> callback, final Key entityId, final RetrieveTarget retrieveTarget) {
        super.retrieve(new AsyncCallback<TO>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(TO result) {
                // If draft do not exists, we return clone of the data from current version
                if ((retrieveTarget == RetrieveTarget.Edit) && (result.getPrimaryKey().getVersion() == Key.VERSION_CURRENT)) {
                    result = duplicateForDraftEdit(result);
                }
                callback.onSuccess(result);
            }
        }, entityId, retrieveTarget);
    }

    protected TO duplicateForDraftEdit(TO to) {
        to.version().set(EntityGraph.businessDuplicate(to.version()));
        VersionedEntityUtils.setAsDraft(to.version());
        to.setPrimaryKey(to.getPrimaryKey().asDraftKey());
        return to;
    }

    protected void saveAsFinal(E entity) {
        Persistence.secureSave(entity);
    }

    @Override
    public void approveFinal(AsyncCallback<VoidSerializable> callback, Key entityId) {
        E entity = Persistence.secureRetrieve(boClass, entityId);
        if (entity.version().isNull()) {
            throw new Error("There are no draft version to finalize");
        }
        entity.saveAction().setValue(SaveAction.saveAsFinal);
        saveAsFinal(entity);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

}
