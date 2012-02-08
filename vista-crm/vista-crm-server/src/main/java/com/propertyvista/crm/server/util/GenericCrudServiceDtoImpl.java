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
package com.propertyvista.crm.server.util;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

/**
 * Generic parameters:
 * DBO - Data Base Object
 * DTO - Data Transfer Object
 * 
 * enhanceXXX methods supposed to be overridden in ancestors to perform some DTO customisation.
 * 
 * @deprecated use AbstractCrudServiceDtoImpl
 */
@Deprecated
public abstract class GenericCrudServiceDtoImpl<DBO extends IEntity, DTO extends DBO> extends GenericListServiceDtoImpl<DBO, DTO> implements
        AbstractCrudService<DTO> {

    public GenericCrudServiceDtoImpl(Class<DBO> dboClass, Class<DTO> dtoClass) {
        super(dboClass, dtoClass);
    }

    protected void persistDBO(DBO dbo, DTO in) {
        Persistence.service().merge(dbo);
    }

    @Override
    public void create(AsyncCallback<DTO> callback, DTO dto) {
        DBO entity = GenericConverter.convertDTO2DBO(dto, dboClass);
        persistDBO(entity, dto);
        dto = GenericConverter.convertDBO2DTO(entity, dtoClass);
        enhanceDTO(entity, dto, false);
        callback.onSuccess(dto);
    }

    @Override
    public void retrieve(AsyncCallback<DTO> callback, Key entityId) {
        DBO entity = Persistence.service().retrieve(dboClass, entityId);
        if ((entity == null) || (entity.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(dboClass).getCaption() + "' " + entityId + " NotFound");
        }
        DTO dto = GenericConverter.convertDBO2DTO(entity, dtoClass);
        enhanceDTO(entity, dto, false);
        callback.onSuccess(dto);
    }

    @Override
    public void save(AsyncCallback<DTO> callback, DTO dto) {
        DBO entity = GenericConverter.convertDTO2DBO(dto, dboClass);
        persistDBO(entity, dto);

        DTO dto2 = GenericConverter.convertDBO2DTO(entity, dtoClass);
        enhanceDTO(entity, dto2, false);
        callback.onSuccess(dto2);
    }
}
