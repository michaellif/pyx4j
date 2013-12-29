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
 * Created on 2012-03-05
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.core.IVersionedEntity.SaveAction;
import com.pyx4j.entity.rpc.AbstractVersionedCrudService;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.rpc.shared.VoidSerializable;

public abstract class AbstractVersionedCrudServiceImpl<E extends IVersionedEntity<?>> extends AbstractCrudServiceImpl<E> implements
        AbstractVersionedCrudService<E> {

    public AbstractVersionedCrudServiceImpl(Class<E> entityClass) {
        super(entityClass);
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
        } else if (primaryKey.getVersion() == Key.VERSION_CURRENT && ((entity == null) || entity.version().isNull())) {
            entity = super.retrieve(primaryKey.asDraftKey(), retrieveTarget);
        }
        return entity;
    }

    @Override
    public void retrieve(final AsyncCallback<E> callback, final Key entityId, final RetrieveTarget retrieveTarget) {
        super.retrieve(new AsyncCallback<E>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(E result) {
                // If draft do not exists, we return clone of the data from current version
                if ((retrieveTarget == RetrieveTarget.Edit) && (result.getPrimaryKey().getVersion() == Key.VERSION_CURRENT)) {
                    result = duplicateForDraftEdit(result);
                }
                callback.onSuccess(result);
            }
        }, entityId, retrieveTarget);
    }

    protected E duplicateForDraftEdit(E bo) {
        bo.version().set(EntityGraph.businessDuplicate(bo.version()));
        VersionedEntityUtils.setAsDraft(bo.version());
        bo.setPrimaryKey(bo.getPrimaryKey().asDraftKey());
        return bo;
    }

    @Override
    public void approveFinal(AsyncCallback<VoidSerializable> callback, Key entityId) {
        E entity = Persistence.secureRetrieve(boClass, entityId.asDraftKey());
        if (entity.version().isNull()) {
            throw new Error("There are no draft version to finalize");
        }
        entity.saveAction().setValue(SaveAction.saveAsFinal);
        persist(entity, null);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

}
