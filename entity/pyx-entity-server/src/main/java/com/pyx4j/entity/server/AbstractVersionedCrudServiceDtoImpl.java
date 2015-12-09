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
 */
package com.pyx4j.entity.server;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.core.IVersionedEntity.SaveAction;
import com.pyx4j.entity.rpc.AbstractVersionedCrudService;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.rpc.shared.VoidSerializable;

public abstract class AbstractVersionedCrudServiceDtoImpl<BO extends IVersionedEntity<?>, TO extends IVersionedEntity<?>>
        extends AbstractCrudServiceDtoImpl<BO, TO> implements AbstractVersionedCrudService<TO> {

    private static final Logger log = LoggerFactory.getLogger(AbstractVersionedCrudServiceDtoImpl.class);

    private static final I18n i18n = I18n.get(AbstractVersionedCrudServiceDtoImpl.class);

    protected AbstractVersionedCrudServiceDtoImpl(Class<BO> entityClass, Class<TO> dtoClass) {
        super(entityClass, dtoClass);
    }

    protected AbstractVersionedCrudServiceDtoImpl(EntityBinder<BO, TO> binder) {
        super(binder);
    }

    @Override
    protected TO init(InitializationData initializationData) {
        TO duplicate = super.init(initializationData);

        if (initializationData.isInstanceOf(DuplicateData.class)) {
            duplicate.versions().clear();

            duplicate.version().versionNumber().setValue(null);

            duplicate.version().fromDate().setValue(null);
            duplicate.version().toDate().setValue(null);

            duplicate.version().createdByUserKey().setValue(null);
            duplicate.version().createdByUser().setValue(null);
        }

        return duplicate;
    }

    @Override
    protected BO retrieve(Key entityId, RetrieveOperation retrieveOperation) {
        // Force draft for edit
        BO bo;
        if ((retrieveOperation == RetrieveOperation.Edit) || (entityId.isDraft())) {
            bo = Persistence.secureRetrieveDraftForEdit(boClass, entityId);
        } else {
            bo = Persistence.secureRetrieve(boClass, entityId);
        }
        if (bo == null) {
            log.error("Entity {} {} not found", boClass, entityId);
            throw new UnRecoverableRuntimeException(i18n.tr("{0} not found", EntityFactory.getEntityMeta(boClass).getCaption()));
        }
        return bo;
    }

    @Override
    protected BO retrieveForSave(TO to) {
        Key boKey = getBOKey(to);
        Validate.isTrue(boKey.getVersion() == Key.VERSION_DRAFT);
        BO bo = super.retrieveForSave(to);
        if (bo.version().isNull()) {
            to.setPrimaryKey(to.getPrimaryKey().asCurrentKey());
            bo = super.retrieveForSave(to);
            bo.version().set(EntityGraph.businessDuplicate(bo.version()));
            VersionedEntityUtils.setAsDraft(bo.version());
        }
        return bo;
    }

    protected void saveAsFinal(BO entity) {
        Persistence.secureSave(entity);
    }

    @Override
    public void approveFinal(AsyncCallback<VoidSerializable> callback, Key toId) {
        TO to = EntityFactory.createIdentityStub(toClass, toId);
        BO bo = Persistence.secureRetrieve(boClass, getBOKey(to).asDraftKey());
        if (bo.version().isNull()) {
            throw new Error("There are no draft version to finalize");
        }
        bo.saveAction().setValue(SaveAction.saveAsFinal);
        saveAsFinal(bo);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

}
