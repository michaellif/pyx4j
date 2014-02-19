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
import com.pyx4j.entity.core.EntityFactory;
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
        // Force draft for edit
        if ((retrieveTarget == RetrieveTarget.Edit) || (entityId.isDraft())) {
            return Persistence.secureRetrieveDraftForEdit(boClass, entityId);
        } else {
            return Persistence.secureRetrieve(boClass, entityId);
        }
    }

    @Override
    protected E retrieveForSave(TO to) {
        Key boKey = getBOKey(to);
        Validate.isTrue(boKey.getVersion() == Key.VERSION_DRAFT);
        E bo = super.retrieveForSave(to);
        if (bo.version().isNull()) {
            to.setPrimaryKey(to.getPrimaryKey().asCurrentKey());
            bo = super.retrieveForSave(to);
            bo.version().set(EntityGraph.businessDuplicate(bo.version()));
            VersionedEntityUtils.setAsDraft(bo.version());
        }
        return bo;
    }

    protected void saveAsFinal(E entity) {
        Persistence.secureSave(entity);
    }

    @Override
    public void approveFinal(AsyncCallback<VoidSerializable> callback, Key toId) {
        TO to = EntityFactory.createIdentityStub(toClass, toId);
        E bo = Persistence.secureRetrieve(boClass, getBOKey(to).asDraftKey());
        if (bo.version().isNull()) {
            throw new Error("There are no draft version to finalize");
        }
        bo.saveAction().setValue(SaveAction.saveAsFinal);
        saveAsFinal(bo);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

}
