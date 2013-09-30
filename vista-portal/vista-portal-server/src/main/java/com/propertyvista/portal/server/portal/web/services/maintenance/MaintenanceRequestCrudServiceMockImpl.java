/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.web.services.maintenance;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceRequestStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceSummaryDTO;
import com.propertyvista.portal.rpc.portal.web.services.maintenance.MaintenanceRequestCrudService;

public class MaintenanceRequestCrudServiceMockImpl implements MaintenanceRequestCrudService {

    @Override
    public void retreiveMaintenanceSummary(AsyncCallback<MaintenanceSummaryDTO> callback) {
        MaintenanceSummaryDTO maintenanceSummary = EntityFactory.create(MaintenanceSummaryDTO.class);

        {
            MaintenanceRequestStatusDTO requestDTO = EntityFactory.create(MaintenanceRequestStatusDTO.class);
            requestDTO.description().setValue("Request 1");
            requestDTO.status().phase().setValue(StatusPhase.Submitted);
            requestDTO.setPrimaryKey(new Key(11));
            maintenanceSummary.openMaintenanceRequests().add(requestDTO);
        }

        {
            MaintenanceRequestStatusDTO requestDTO = EntityFactory.create(MaintenanceRequestStatusDTO.class);
            requestDTO.description().setValue("Request 2");
            requestDTO.status().phase().setValue(StatusPhase.Scheduled);
            requestDTO.setPrimaryKey(new Key(22));
            maintenanceSummary.openMaintenanceRequests().add(requestDTO);
        }

        {
            MaintenanceRequestStatusDTO requestDTO = EntityFactory.create(MaintenanceRequestStatusDTO.class);
            requestDTO.description().setValue("Request 3");
            requestDTO.status().phase().setValue(StatusPhase.Cancelled);
            requestDTO.setPrimaryKey(new Key(33));
            maintenanceSummary.closedMaintenanceRequests().add(requestDTO);
        }

        {
            MaintenanceRequestStatusDTO requestDTO = EntityFactory.create(MaintenanceRequestStatusDTO.class);
            requestDTO.description().setValue("Request 4");
            requestDTO.status().phase().setValue(StatusPhase.Resolved);
            requestDTO.setPrimaryKey(new Key(44));
            maintenanceSummary.closedMaintenanceRequests().add(requestDTO);
        }

        {
            MaintenanceRequestStatusDTO requestDTO = EntityFactory.create(MaintenanceRequestStatusDTO.class);
            requestDTO.description().setValue("Request 5");
            requestDTO.status().phase().setValue(StatusPhase.Resolved);
            requestDTO.setPrimaryKey(new Key(55));
            maintenanceSummary.closedMaintenanceRequests().add(requestDTO);
        }

        callback.onSuccess(maintenanceSummary);
    }

    @Override
    public void init(AsyncCallback<MaintenanceRequestDTO> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void retrieve(AsyncCallback<MaintenanceRequestDTO> callback, Key entityId, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        MaintenanceRequestDTO requestDTO = EntityFactory.create(MaintenanceRequestDTO.class);
        requestDTO.setPrimaryKey(entityId);
        requestDTO.requestId().setValue("1111-111");
        requestDTO.description().setValue("Request 1");
        requestDTO.category().name().setValue("Category 1");
        requestDTO.status().phase().setValue(StatusPhase.Submitted);
        requestDTO.summary().setValue("summary summary summary");
        requestDTO.description().setValue("description description description description description description description description");
        requestDTO.priority().name().setValue("Priority");
        callback.onSuccess(requestDTO);
    }

    @Override
    public void create(AsyncCallback<Key> callback, MaintenanceRequestDTO editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, MaintenanceRequestDTO editableEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<MaintenanceRequestDTO>> callback, EntityListCriteria<MaintenanceRequestDTO> criteria) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancelMaintenanceRequest(AsyncCallback<VoidSerializable> callback, Key requestId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void rateMaintenanceRequest(AsyncCallback<VoidSerializable> callback, Key requestId, Integer rate) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getCategoryMeta(AsyncCallback<MaintenanceRequestMetadata> callback, boolean levelsOnly) {
        // TODO Auto-generated method stub

    }

}
