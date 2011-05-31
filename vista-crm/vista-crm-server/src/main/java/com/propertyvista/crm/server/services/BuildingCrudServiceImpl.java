/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.BuildingDTO;

public class BuildingCrudServiceImpl implements BuildingCrudService {

    private final static Logger log = LoggerFactory.getLogger(BuildingCrudServiceImpl.class);

    @Override
    public void create(AsyncCallback<BuildingDTO> callback, BuildingDTO editableEntity) {
        Building entity = GenericConverter.down(editableEntity, Building.class);
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        callback.onSuccess(GenericConverter.up(entity, BuildingDTO.class));
    }

    @Override
    public void retrieve(AsyncCallback<BuildingDTO> callback, Key entityId) {
        Building entity = PersistenceServicesFactory.getPersistenceService().retrieve(Building.class, entityId);
        callback.onSuccess(GenericConverter.up(entity, BuildingDTO.class));
    }

    @Override
    public void save(AsyncCallback<BuildingDTO> callback, BuildingDTO editableEntity) {
        Building entity = GenericConverter.down(editableEntity, Building.class);
        PersistenceServicesFactory.getPersistenceService().merge(entity);
        callback.onSuccess(GenericConverter.up(entity, BuildingDTO.class));
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<BuildingDTO>> callback, EntitySearchCriteria<BuildingDTO> criteria) {
        EntitySearchCriteria<Building> c = GenericConverter.down(criteria, Building.class);
        //TODO add building specific criteria
        callback.onSuccess(GenericConverter.up(EntityServicesImpl.secureSearch(c), BuildingDTO.class));
    }
}
