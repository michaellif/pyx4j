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
import com.pyx4j.entity.rpc.AbstractVersionedCrudService;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.rpc.shared.VoidSerializable;

public abstract class AbstractVersionedCrudServiceDtoImpl<E extends IVersionedEntity<?>, DTO extends IVersionedEntity<?>> extends
        AbstractCrudServiceDtoImpl<E, DTO> implements AbstractVersionedCrudService<DTO> {

    protected AbstractVersionedCrudServiceDtoImpl(Class<E> entityClass, Class<DTO> dtoClass) {
        super(entityClass, dtoClass);
    }

    @Override
    protected E retrieve(Key entityId, RetrieveTraget retrieveTraget) {
        Key primaryKey;
        // Force draft for edit
        if (retrieveTraget == RetrieveTraget.Edit) {
            primaryKey = entityId.asDraftKey();
        } else {
            primaryKey = entityId;
        }
        E entity = super.retrieve(primaryKey, retrieveTraget);
        if (primaryKey.getVersion() == Key.VERSION_DRAFT && entity.version().isNull()) {
            entity = super.retrieve(primaryKey.asCurrentKey(), retrieveTraget);
        } else if (primaryKey.getVersion() == Key.VERSION_CURRENT && entity.version().isNull()) {
            entity = super.retrieve(primaryKey.asDraftKey(), retrieveTraget);
        }
        return entity;
    }

    @Override
    protected E retrieveForSave(DTO dto) {
        Validate.isTrue(dto.getPrimaryKey().getVersion() == Key.VERSION_DRAFT);
        E entity = super.retrieveForSave(dto);
        if (entity.version().isNull()) {
            dto.setPrimaryKey(dto.getPrimaryKey().asCurrentKey());
            entity = super.retrieveForSave(dto);
            entity.version().set(EntityGraph.businessDuplicate(entity.version()));
            VersionedEntityUtils.setAsDraft(entity.version());
        }
        return entity;
    }

    @Override
    public void retrieve(final AsyncCallback<DTO> callback, final Key entityId, final RetrieveTraget retrieveTraget) {
        super.retrieve(new AsyncCallback<DTO>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DTO result) {
                // If draft do not exists, we return clone of the data from current version
                if ((result.getPrimaryKey().getVersion() == Key.VERSION_CURRENT)
                        && ((entityId.getVersion() == Key.VERSION_DRAFT) || (retrieveTraget == RetrieveTraget.Edit))) {
                    result.version().set(EntityGraph.businessDuplicate(result.version()));
                    VersionedEntityUtils.setAsDraft(result.version());
                    result.setPrimaryKey(entityId.asDraftKey());
                }
                callback.onSuccess(result);
            }
        }, entityId, retrieveTraget);
    }

    protected void saveAsFinal(E entity) {
        Persistence.secureSave(entity);
    }

    @Override
    public void approveFinal(AsyncCallback<VoidSerializable> callback, Key entityId) {
        E entity = Persistence.secureRetrieveDraft(entityClass, entityId);
        entity.saveAction().setValue(SaveAction.saveAsFinal);
        saveAsFinal(entity);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

}
