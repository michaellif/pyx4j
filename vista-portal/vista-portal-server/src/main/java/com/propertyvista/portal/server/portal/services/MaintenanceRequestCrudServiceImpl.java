/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO.MaintenanceType;
import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO.StatusType;
import com.propertyvista.portal.rpc.portal.services.MaintenanceRequestCrudService;

//TODO the whole implementation is TBD
public class MaintenanceRequestCrudServiceImpl implements MaintenanceRequestCrudService {

    @Override
    public void search(AsyncCallback<EntitySearchResult<MaintenanceRequestDTO>> callback, EntitySearchCriteria<MaintenanceRequestDTO> criteria) {
        EntitySearchResult<MaintenanceRequestDTO> result = new EntitySearchResult<MaintenanceRequestDTO>();
        MaintenanceRequestDTO entity = EntityFactory.create(MaintenanceRequestDTO.class);
        entity.maintenanceType().setValue(MaintenanceType.Plumbing);
        entity.problemDescription().setValue("Leaking tap");
        entity.status().setValue(StatusType.Pending);
        result.add(entity);
        entity = EntityFactory.create(MaintenanceRequestDTO.class);
        entity.maintenanceType().setValue(MaintenanceType.Electrical);
        entity.problemDescription().setValue("Living room outlet does not work");
        entity.status().setValue(StatusType.Scheduled);
        result.add(entity);
        callback.onSuccess(result);
    }

    @Override
    public void create(AsyncCallback<MaintenanceRequestDTO> callback, MaintenanceRequestDTO editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retrieve(AsyncCallback<MaintenanceRequestDTO> callback, Key entityId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void save(AsyncCallback<MaintenanceRequestDTO> callback, MaintenanceRequestDTO editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<MaintenanceRequestDTO>> callback, EntityListCriteria<MaintenanceRequestDTO> criteria) {
        // TODO Auto-generated method stub

    }
}
