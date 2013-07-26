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
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;

public abstract class AbstractCrudServiceDtoImpl<E extends IEntity, DTO extends IEntity> extends AbstractListServiceDtoImpl<E, DTO> implements
        AbstractCrudService<DTO> {

    private static final Logger log = LoggerFactory.getLogger(AbstractCrudServiceDtoImpl.class);

    private static final I18n i18n = I18n.get(AbstractCrudServiceDtoImpl.class);

    protected AbstractCrudServiceDtoImpl(Class<E> entityClass, Class<DTO> dtoClass) {
        super(entityClass, dtoClass);
    }

    /**
     * Used to retrieve bound detached members before they are copied to DTO
     * TODO To make it work magically we have implemented retriveDetachedMember
     * 
     * retrieveTarget when called for save operations
     */
    protected void retrievedSingle(E entity, RetrieveTarget retrieveTarget) {
    }

    /**
     * This method called for single entity returned to the GWT client. As opposite to entries in list.
     * This is empty callback function that don't need to be called from implementation.
     * 
     * @param retrieveTarget
     *            TODO
     */
    protected void enhanceRetrieved(E entity, DTO dto, RetrieveTarget retrieveTarget) {
    }

    protected E retrieve(Key entityId, RetrieveTarget retrieveTarget) {
        E entity = Persistence.secureRetrieve(entityClass, entityId);
        if (entity == null) {
            log.error("Entity {} {} not found", entityClass, entityId);
            throw new UnRecoverableRuntimeException(i18n.tr("{0} not found", EntityFactory.getEntityMeta(entityClass).getCaption()));
        }
        return entity;
    }

    protected void create(E entity, DTO dto) {
        persist(entity, dto);
    }

    protected void save(E entity, DTO dto) {
        persist(entity, dto);
    }

    protected void persist(E entity, DTO dto) {
        Persistence.secureSave(entity);
    }

    @Override
    public void retrieve(AsyncCallback<DTO> callback, Key entityId, RetrieveTarget retrieveTarget) {
        E entity = retrieve(entityId, retrieveTarget);
        if (entity != null) {
            retrievedSingle(entity, retrieveTarget);
        }
        DTO dto = createDTO(entity);
        enhanceRetrieved(entity, dto, retrieveTarget);
        callback.onSuccess(dto);
    }

    @Override
    public void create(AsyncCallback<Key> callback, DTO dto) {
        E entity = createDBO(dto);
        create(entity, dto);
        Persistence.service().commit();
        callback.onSuccess(entity.getPrimaryKey());
    }

    protected E retrieveForSave(DTO dto) {
        E entity = Persistence.secureRetrieve(entityClass, dto.getPrimaryKey());
        if (entity == null) {
            entity = EntityFactory.create(dboClass);
        }
        return entity;
    }

    @Override
    public void save(AsyncCallback<Key> callback, DTO dto) {
        E entity = retrieveForSave(dto);
        retrievedSingle(entity, null);
        copyDTOtoDBO(dto, entity);
        save(entity, dto);
        Persistence.service().commit();
        callback.onSuccess(entity.getPrimaryKey());
    }

}
