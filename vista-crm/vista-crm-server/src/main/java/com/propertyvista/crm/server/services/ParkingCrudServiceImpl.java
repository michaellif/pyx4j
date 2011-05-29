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

import com.propertyvista.crm.rpc.services.ParkingCrudService;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.dto.ParkingDTO;

public class ParkingCrudServiceImpl implements ParkingCrudService {

    private final static Logger log = LoggerFactory.getLogger(ParkingCrudServiceImpl.class);

    @Override
    public void create(AsyncCallback<ParkingDTO> callback, ParkingDTO editableEntity) {
        Parking entity = GenericConverter.down(editableEntity, Parking.class);
        PersistenceServicesFactory.getPersistenceService().persist(entity);
        callback.onSuccess(GenericConverter.up(entity, ParkingDTO.class));
    }

    @Override
    public void retrieve(AsyncCallback<ParkingDTO> callback, String entityId) {
        Parking entity = PersistenceServicesFactory.getPersistenceService().retrieve(Parking.class, entityId);
        callback.onSuccess(GenericConverter.up(entity, ParkingDTO.class));
    }

    @Override
    public void save(AsyncCallback<ParkingDTO> callback, ParkingDTO editableEntity) {
        Parking entity = GenericConverter.down(editableEntity, Parking.class);
        PersistenceServicesFactory.getPersistenceService().merge(entity);
        callback.onSuccess(GenericConverter.up(entity, ParkingDTO.class));
    }

    @Override
    public void search(AsyncCallback<EntitySearchResult<ParkingDTO>> callback, EntitySearchCriteria<ParkingDTO> criteria) {
        EntitySearchCriteria<Parking> c = GenericConverter.down(criteria, Parking.class);
        //TODO add Parking specific criteria
        callback.onSuccess(GenericConverter.up(EntityServicesImpl.secureSearch(c), ParkingDTO.class));
    }

}
