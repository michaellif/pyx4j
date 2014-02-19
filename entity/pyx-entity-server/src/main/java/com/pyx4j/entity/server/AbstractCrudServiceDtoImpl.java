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
 * @version $Id$
 */
package com.pyx4j.entity.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;
import com.pyx4j.security.shared.SecurityController;

public abstract class AbstractCrudServiceDtoImpl<BO extends IEntity, TO extends IEntity> extends AbstractListServiceDtoImpl<BO, TO> implements
        AbstractCrudService<TO> {

    private static final Logger log = LoggerFactory.getLogger(AbstractCrudServiceDtoImpl.class);

    private static final I18n i18n = I18n.get(AbstractCrudServiceDtoImpl.class);

    protected AbstractCrudServiceDtoImpl(Class<BO> boClass, Class<TO> toClass) {
        super(boClass, toClass);
    }

    /**
     * Used to retrieve bound detached members before they are copied to DTO
     * 
     * retrieveTarget is null when called for save operations
     */
    protected void retrievedSingle(BO bo, RetrieveTarget retrieveTarget) {
    }

    /**
     * This method called for single entity returned to the GWT client. As opposite to entries in list.
     * This is empty callback function that don't need to be called from implementation.
     * 
     * @param retrieveTarget
     */
    protected void enhanceRetrieved(BO bo, TO to, RetrieveTarget retrieveTarget) {
    }

    protected BO retrieve(Key entityId, RetrieveTarget retrieveTarget) {
        BO bo = Persistence.secureRetrieve(boClass, entityId);
        if (bo == null) {
            log.error("Entity {} {} not found", boClass, entityId);
            throw new UnRecoverableRuntimeException(i18n.tr("{0} not found", EntityFactory.getEntityMeta(boClass).getCaption()));
        }
        return bo;
    }

    protected TO init(InitializationData initializationData) {
        return EntityFactory.create(toClass);
    }

    protected void create(BO bo, TO to) {
        persist(bo, to);
    }

    protected void save(BO bo, TO to) {
        persist(bo, to);
    }

    protected void persist(BO bo, TO to) {
        Persistence.secureSave(bo);
    }

    /**
     * Allows to map BO to id of different TO entity.
     * if changed, need to change getTOKey
     * 
     * @experimental
     * 
     * @param toId
     * @return primary key of BO entity
     */
    protected Key getBOKey(TO to) {
        return to.getPrimaryKey();
    }

    /**
     * Default implementation does noting since the keys mapped one to one.
     * 
     * @experimental
     * 
     * @param bo
     * @param to
     */
    protected Key getTOKey(BO bo, TO to) {
        return bo.getPrimaryKey();
    }

    @Override
    public void retrieve(AsyncCallback<TO> callback, Key toId, RetrieveTarget retrieveTarget) {
        TO to = EntityFactory.createIdentityStub(toClass, toId);
        BO bo = retrieve(getBOKey(to), retrieveTarget);
        if (bo != null) {
            retrievedSingle(bo, retrieveTarget);
        }
        copyBOtoTO(bo, to);
        enhanceRetrieved(bo, to, retrieveTarget);
        to.setPrimaryKey(getTOKey(bo, to));
        callback.onSuccess(to);
    }

    @Override
    public final void init(AsyncCallback<TO> callback, InitializationData initializationData) {
        SecurityController.assertPermission(EntityPermission.permissionCreate(boClass));
        callback.onSuccess(init(initializationData));
    }

    @Override
    public void create(AsyncCallback<Key> callback, TO to) {
        BO bo = createBO(to);
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
    public void save(AsyncCallback<Key> callback, TO to) {
        BO bo = retrieveForSave(to);
        retrievedSingle(bo, null);
        copyTOtoBO(to, bo);
        save(bo, to);
        Persistence.service().commit();
        callback.onSuccess(getTOKey(bo, to));
    }

}
