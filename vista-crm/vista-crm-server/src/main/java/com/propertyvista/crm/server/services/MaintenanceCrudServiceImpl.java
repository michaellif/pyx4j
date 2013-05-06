/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.sql.Time;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceCrudServiceImpl extends AbstractCrudServiceDtoImpl<MaintenanceRequest, MaintenanceRequestDTO> implements MaintenanceCrudService {

    public MaintenanceCrudServiceImpl() {
        super(MaintenanceRequest.class, MaintenanceRequestDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(MaintenanceRequest entity, MaintenanceRequestDTO dto, RetrieveTraget retrieveTraget) {
        enhanceAll(dto);
    }

    @Override
    protected void enhanceListRetrieved(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        enhanceAll(dto);
    }

    protected void enhanceAll(MaintenanceRequestDTO dto) {
        enhanceDbo(dto);
    }

    protected void enhanceDbo(MaintenanceRequest dbo) {
        Persistence.service().retrieve(dbo.building());
        Persistence.service().retrieve(dbo.category());
        MaintenanceRequestCategory parent = dbo.category().parent();
        while (!parent.isNull()) {
            Persistence.ensureRetrieve(parent, AttachLevel.Attached);
            parent = parent.parent();
        }
    }

    @Override
    public void retrieve(AsyncCallback<MaintenanceRequestDTO> callback, Key entityId, RetrieveTraget retrieveTraget) {
        ServerSideFactory.create(MaintenanceFacade.class).beforeItemRequest();
        super.retrieve(callback, entityId, retrieveTraget);
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<MaintenanceRequestDTO>> callback, EntityListCriteria<MaintenanceRequestDTO> dtoCriteria) {
        ServerSideFactory.create(MaintenanceFacade.class).beforeListRequest();
        super.list(callback, dtoCriteria);
    }

    @Override
    public void sheduleAction(AsyncCallback<VoidSerializable> callback, LogicalDate date, Time time, Key entityId) {
        MaintenanceRequest request = Persistence.service().retrieve(MaintenanceRequest.class, entityId);
        enhanceDbo(request);
        ServerSideFactory.create(MaintenanceFacade.class).sheduleMaintenanceRequest(request, date, time);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void resolveAction(AsyncCallback<VoidSerializable> callback, Key entityId) {
        MaintenanceRequest request = Persistence.service().retrieve(MaintenanceRequest.class, entityId);
        enhanceDbo(request);
        ServerSideFactory.create(MaintenanceFacade.class).resolveMaintenanceRequest(request);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void rateAction(AsyncCallback<VoidSerializable> callback, SurveyResponse rate, Key entityId) {
        MaintenanceRequest request = Persistence.service().retrieve(MaintenanceRequest.class, entityId);
        enhanceDbo(request);
        ServerSideFactory.create(MaintenanceFacade.class).rateMaintenanceRequest(request, rate);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void cancelAction(AsyncCallback<VoidSerializable> callback, Key entityId) {
        MaintenanceRequest request = Persistence.service().retrieve(MaintenanceRequest.class, entityId);
        enhanceDbo(request);
        ServerSideFactory.create(MaintenanceFacade.class).cancelMaintenanceRequest(request);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void createNewRequest(AsyncCallback<MaintenanceRequestDTO> callback, Building buildingStub) {
        Building building = Persistence.service().retrieve(Building.class, buildingStub.getPrimaryKey());
        MaintenanceRequest maintenanceRequest = ServerSideFactory.create(MaintenanceFacade.class).createNewRequest(building);
        callback.onSuccess(createDTO(maintenanceRequest));
    }

    @Override
    public void createNewRequestForTenant(AsyncCallback<MaintenanceRequestDTO> callback, Tenant tenantStub) {
        Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantStub.getPrimaryKey());
        MaintenanceRequest maintenanceRequest = ServerSideFactory.create(MaintenanceFacade.class).createNewRequestForTenant(tenant);
        callback.onSuccess(createDTO(maintenanceRequest));
    }

    @Override
    public void getCategoryMeta(AsyncCallback<MaintenanceRequestMetadata> callback, boolean levelsOnly) {
        callback.onSuccess(ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(levelsOnly));
    }

    @Override
    protected void persist(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        ServerSideFactory.create(MaintenanceFacade.class).postMaintenanceRequest(entity);
    }
}
