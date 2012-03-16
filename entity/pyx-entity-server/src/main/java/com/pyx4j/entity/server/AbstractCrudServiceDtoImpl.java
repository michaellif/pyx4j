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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UnRecoverableRuntimeException;

public abstract class AbstractCrudServiceDtoImpl<E extends IEntity, DTO extends IEntity> extends AbstractListServiceDtoImpl<E, DTO> implements
        AbstractCrudService<DTO> {

    private static final I18n i18n = I18n.get(AbstractCrudServiceDtoImpl.class);

    protected AbstractCrudServiceDtoImpl(Class<E> entityClass, Class<DTO> dtoClass) {
        super(entityClass, dtoClass);
    }

    /**
     * This method called for single entity returned to the GWT client. As opposite to entries in list.
     * This is empty callback function that don't need to be called from implementation.
     */
    protected void enhanceRetrieved(E entity, DTO dto) {
    }

    protected E retrieve(Key entityId, RetrieveTraget retrieveTraget) {
        E entity = Persistence.secureRetrieve(entityClass, entityId);
        if (entity == null) {
            throw new UnRecoverableRuntimeException(i18n.tr("{0} not found", EntityFactory.getEntityMeta(entityClass).getCaption()));
        }
        return entity;
    }

    protected void persist(E entity, DTO dto) {
        Persistence.secureSave(entity);
    }

    @Override
    public void retrieve(AsyncCallback<DTO> callback, Key entityId, RetrieveTraget retrieveTraget) {
        E entity = retrieve(entityId, retrieveTraget);
        DTO dto = createDTO(entity);
        enhanceRetrieved(entity, dto);
        callback.onSuccess(dto);
    }

    @Override
    public void create(AsyncCallback<DTO> callback, DTO dto) {
        E entity = createDBO(dto);
        persist(entity, dto);
        Persistence.service().commit();
        DTO dtoReturn = createDTO(entity);
        enhanceRetrieved(entity, dtoReturn);
        callback.onSuccess(dtoReturn);
    }

    @Override
    public void save(AsyncCallback<DTO> callback, DTO dto) {
        E entity = createDBO(dto);
        persist(entity, dto);
        Persistence.service().commit();
        DTO dtoReturn = createDTO(entity);
        enhanceRetrieved(entity, dtoReturn);
        callback.onSuccess(dtoReturn);
    }

}
