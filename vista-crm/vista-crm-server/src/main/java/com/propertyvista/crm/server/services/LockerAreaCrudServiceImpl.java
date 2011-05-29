/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.rpc.services.LockerAreaCrudService;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.dto.LockerAreaDTO;

public class LockerAreaCrudServiceImpl implements LockerAreaCrudService {

    private final static Logger log = LoggerFactory.getLogger(LockerAreaCrudServiceImpl.class);

    @Override
    public void create(AsyncCallback<LockerAreaDTO> callback, LockerAreaDTO editableEntity) {
        LockerArea entity = GenericConverter.down(editableEntity, LockerArea.class);
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        callback.onSuccess(GenericConverter.up(entity, LockerAreaDTO.class));
    }

    @Override
    public void retrieve(AsyncCallback<LockerAreaDTO> callback, String entityId) {
        LockerArea entity = PersistenceServicesFactory.getPersistenceService().retrieve(LockerArea.class, entityId);
        callback.onSuccess(GenericConverter.up(entity, LockerAreaDTO.class));
    }

    @Override
    public void save(AsyncCallback<LockerAreaDTO> callback, LockerAreaDTO editableEntity) {
        LockerArea entity = GenericConverter.down(editableEntity, LockerArea.class);
        PersistenceServicesFactory.getPersistenceService().merge(entity);
        callback.onSuccess(GenericConverter.up(entity, LockerAreaDTO.class));
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<LockerAreaDTO>> callback, EntitySearchCriteria<LockerAreaDTO> criteria) {
        EntitySearchCriteria<LockerArea> c = GenericConverter.down(criteria, LockerArea.class);
        //TODO add LockerArea specific criteria
        callback.onSuccess(GenericConverter.up(EntityServicesImpl.secureSearch(c), LockerAreaDTO.class));
    }

}
