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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.rpc.services.ElevatorCrudService;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.dto.ElevatorDTO;

public class ElevatorCrudServiceImpl implements ElevatorCrudService {

    private final static Logger log = LoggerFactory.getLogger(ElevatorCrudServiceImpl.class);

    @Override
    public void create(AsyncCallback<ElevatorDTO> callback, ElevatorDTO editableEntity) {
        Elevator entity = GenericConverter.down(editableEntity, Elevator.class);
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        callback.onSuccess(GenericConverter.up(entity, ElevatorDTO.class));
    }

    @Override
    public void retrieve(AsyncCallback<ElevatorDTO> callback, Key entityId) {
        Elevator entity = PersistenceServicesFactory.getPersistenceService().retrieve(Elevator.class, entityId);
        callback.onSuccess(GenericConverter.up(entity, ElevatorDTO.class));
    }

    @Override
    public void save(AsyncCallback<ElevatorDTO> callback, ElevatorDTO editableEntity) {
        Elevator entity = GenericConverter.down(editableEntity, Elevator.class);
        PersistenceServicesFactory.getPersistenceService().merge(entity);
        callback.onSuccess(GenericConverter.up(entity, ElevatorDTO.class));
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<ElevatorDTO>> callback, EntitySearchCriteria<ElevatorDTO> criteria) {
        EntitySearchCriteria<Elevator> c = GenericConverter.down(criteria, Elevator.class);
        //TODO add Elevator specific criteria
        callback.onSuccess(GenericConverter.up(EntityServicesImpl.secureSearch(c), ElevatorDTO.class));
    }

}
