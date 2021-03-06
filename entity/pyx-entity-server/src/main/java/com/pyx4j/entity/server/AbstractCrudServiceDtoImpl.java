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
 * Created on Nov 6, 2011
 * @author vlads
 */
package com.pyx4j.entity.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.shared.utils.BindingContext;
import com.pyx4j.entity.shared.utils.BindingContext.BindingType;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.security.shared.SecurityController;

public abstract class AbstractCrudServiceDtoImpl<BO extends IEntity, TO extends IEntity> extends AbstractListServiceDtoImpl<BO, TO>
        implements AbstractCrudService<TO> {

    private static final Logger log = LoggerFactory.getLogger(AbstractCrudServiceDtoImpl.class);

    private static final I18n i18n = I18n.get(AbstractCrudServiceDtoImpl.class);

    protected AbstractCrudServiceDtoImpl(Class<BO> boClass, Class<TO> toClass) {
        super(boClass, toClass);
    }

    protected AbstractCrudServiceDtoImpl(EntityBinder<BO, TO> binder) {
        super(binder);
    }

    protected BO retrieve(Key entityId, RetrieveOperation retrieveOperation) {
        BO bo = Persistence.secureRetrieve(boClass, entityId);
        if (bo == null) {
            log.error("Entity {} {} not found", boClass, entityId);
            throw new UnRecoverableRuntimeException(i18n.tr("{0} not found", EntityFactory.getEntityMeta(boClass).getCaption()));
        }
        return bo;
    }

    protected TO init(InitializationData initializationData) {
        if (strictDataModelPermissions || toProto.getEntityMeta().isAnnotationPresent(SecurityEnabled.class)) {
            SecurityController.assertPermission(DataModelPermission.permissionCreate(toClass));
        }
        return EntityFactory.create(toClass);
    }

    protected TO duplicate(DuplicateData duplicateData) {
        if (strictDataModelPermissions || toProto.getEntityMeta().isAnnotationPresent(SecurityEnabled.class)) {
            SecurityController.assertPermission(DataModelPermission.permissionCreate(toClass));
        }
        Key entityKeyToDuplicate = duplicateData.originalEntityKey().getValue();
        BO bo = retrieve(getBOKey(EntityFactory.createIdentityStub(toClass, entityKeyToDuplicate)), RetrieveOperation.Edit);
        if (bo != null) {
            Persistence.retrieveOwned(bo);
            onBeforeBind(bo, RetrieveOperation.Edit);
        }
        TO to = binder.createTO(bo, new BindingContext(BindingType.Edit));

        // Allow  for TO to be calculated base on original input
        to.setPrimaryKey(entityKeyToDuplicate);
        to.setPrimaryKey(getTOKey(bo, to));

        onAfterBind(bo, to, RetrieveOperation.Edit);
        if (strictDataModelPermissions || toProto.getEntityMeta().isAnnotationPresent(SecurityEnabled.class)) {
            SecurityController.assertPermission(to, DataModelPermission.permissionRead(to.getValueClass()));
        }
        return EntityGraph.businessDuplicate(to);
    }

    /**
     * Default implementation calls persist(...)
     *
     * @param bo
     * @param to
     */
    protected void create(BO bo, TO to) {
        persist(bo, to);
    }

    /**
     * Default implementation calls persist(...)
     *
     * @param bo
     * @param to
     * @return true is entity was updated/changed in DB
     */
    protected boolean save(BO bo, TO to) {
        return persist(bo, to);
    }

    /**
     *
     * @param bo
     * @param to
     * @return true is entity was updated/changed in DB
     */
    protected boolean persist(BO bo, TO to) {
        return Persistence.secureSave(bo);
    }

    @Override
    public final void retrieve(AsyncCallback<TO> callback, Key toId, RetrieveOperation retrieveOperation) {
        if (strictDataModelPermissions || toProto.getEntityMeta().isAnnotationPresent(SecurityEnabled.class)) {
            SecurityController.assertPermission(DataModelPermission.permissionRead(toClass));
        }
        BO bo = retrieve(getBOKey(EntityFactory.createIdentityStub(toClass, toId)), retrieveOperation);
        if (bo != null) {
            onBeforeBind(bo, retrieveOperation);
        }
        TO to = binder.createTO(bo, new BindingContext(retrieveOperation.getBindingType()));

        // Allow  for TO to be calculated base on original input
        to.setPrimaryKey(toId);
        to.setPrimaryKey(getTOKey(bo, to));

        onAfterBind(bo, to, retrieveOperation);
        if (strictDataModelPermissions || toProto.getEntityMeta().isAnnotationPresent(SecurityEnabled.class)) {
            SecurityController.assertPermission(to, DataModelPermission.permissionRead(to.getValueClass()));
        }
        callback.onSuccess(to);
    }

    @Override
    public final void init(AsyncCallback<TO> callback, InitializationData initializationData) {
        if (strictDataModelPermissions || toProto.getEntityMeta().isAnnotationPresent(SecurityEnabled.class)) {
            SecurityController.assertPermission(EntityPermission.permissionCreate(boClass));
        }
        if (initializationData != null && initializationData.isInstanceOf(DuplicateData.class)) {
            callback.onSuccess(duplicate((DuplicateData) initializationData));
        } else {
            callback.onSuccess(init(initializationData));
        }
    }

    @Override
    public final void create(AsyncCallback<Key> callback, TO to) {
        if (strictDataModelPermissions || toProto.getEntityMeta().isAnnotationPresent(SecurityEnabled.class)) {
            SecurityController.assertPermission(to, DataModelPermission.permissionCreate(to.getValueClass()));
        }
        BO bo = binder.createBO(to, new BindingContext(BindingType.Save));
        create(bo, to);
        Persistence.service().commit();
        callback.onSuccess(getTOKey(bo, to));
    }

    protected BO retrieveForSave(TO to) {
        BO bo = Persistence.secureRetrieve(boClass, getBOKey(to));
        if (bo == null) {
            bo = EntityFactory.create(boClass);
        }
        return bo;
    }

    @Override
    public final void save(AsyncCallback<Key> callback, TO to) {
        if (strictDataModelPermissions || toProto.getEntityMeta().isAnnotationPresent(SecurityEnabled.class)) {
            SecurityController.assertPermission(to, DataModelPermission.permissionUpdate(to.getValueClass()));
        }
        BO bo = retrieveForSave(to);
        onBeforeBind(bo, RetrieveOperation.Save);
        binder.copyTOtoBO(to, bo, new BindingContext(BindingType.Save));
        save(bo, to);
        Persistence.service().commit();
        callback.onSuccess(getTOKey(bo, to));
    }

}
