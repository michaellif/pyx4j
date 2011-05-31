/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-31
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.rpc.services.AbstractCrudService;

public abstract class AbstractGenericCrudService<EditableEntityDB extends IEntity, EditableEntityDTO extends EditableEntityDB> implements
        AbstractCrudService<EditableEntityDTO> {

    protected Class<EditableEntityDB> dbEntityClass;

    protected Class<EditableEntityDTO> dtoEntityClass;

    protected AbstractGenericCrudService(Class<EditableEntityDB> dbEntityClass, Class<EditableEntityDTO> dtoEntityClass) {
        this.dbEntityClass = dbEntityClass;
        this.dtoEntityClass = dtoEntityClass;
    }

    protected void enhanceCreateDTO(EditableEntityDB entity, EditableEntityDTO dto) {
    }

    @Override
    public void create(AsyncCallback<EditableEntityDTO> callback, EditableEntityDTO dto) {
        EditableEntityDB entity = GenericConverter.down(dto, dbEntityClass);
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        dto = GenericConverter.up(entity, dtoEntityClass);
        enhanceCreateDTO(entity, dto);
        callback.onSuccess(dto);
    }

    protected void enhanceRetrieveDTO(EditableEntityDB entity, EditableEntityDTO dto) {
    }

    @Override
    public void retrieve(AsyncCallback<EditableEntityDTO> callback, Key entityId) {
        EditableEntityDB entity = PersistenceServicesFactory.getPersistenceService().retrieve(dbEntityClass, entityId);
        EditableEntityDTO dto = GenericConverter.up(entity, dtoEntityClass);
        enhanceRetrieveDTO(entity, dto);
        callback.onSuccess(dto);
    }

    @Override
    public void save(AsyncCallback<EditableEntityDTO> callback, EditableEntityDTO dto) {
        EditableEntityDB entity = GenericConverter.down(dto, dbEntityClass);
        PersistenceServicesFactory.getPersistenceService().merge(entity);
        callback.onSuccess(GenericConverter.up(entity, dtoEntityClass));
    }

    protected void enhanceSearchCriteria(EntitySearchCriteria<EditableEntityDB> entityCriteria, EntitySearchCriteria<EditableEntityDTO> criteria) {
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<EditableEntityDTO>> callback, EntitySearchCriteria<EditableEntityDTO> criteria) {
        EntitySearchCriteria<EditableEntityDB> c = GenericConverter.down(criteria, dbEntityClass);
        enhanceSearchCriteria(c, criteria);
        callback.onSuccess(GenericConverter.up(EntityServicesImpl.secureSearch(c), dtoEntityClass));

    }

}
