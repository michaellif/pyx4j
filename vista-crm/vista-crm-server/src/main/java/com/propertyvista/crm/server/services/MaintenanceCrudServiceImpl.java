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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestSchedule;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MaintenanceRequestMetadataDTO;
import com.propertyvista.dto.MaintenanceRequestScheduleDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class MaintenanceCrudServiceImpl extends AbstractCrudServiceDtoImpl<MaintenanceRequest, MaintenanceRequestDTO> implements MaintenanceCrudService {

    public MaintenanceCrudServiceImpl() {
        super(MaintenanceRequest.class, MaintenanceRequestDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(MaintenanceRequest entity, MaintenanceRequestDTO dto, RetrieveTarget RetrieveTarget) {
        enhanceAll(dto);
    }

    @Override
    protected void enhanceListRetrieved(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        enhanceAll(dto);
    }

    protected void enhanceAll(MaintenanceRequestDTO dto) {
        enhanceDbo(dto);
        // populate latest scheduled info
        if (!dto.workHistory().isEmpty()) {
            MaintenanceRequestSchedule latest = dto.workHistory().get(dto.workHistory().size() - 1);
            dto.scheduledDate().set(latest.scheduledDate());
            dto.scheduledTimeFrom().set(latest.scheduledTimeFrom());
            dto.scheduledTimeTo().set(latest.scheduledTimeTo());
        }
    }

    protected void enhanceDbo(MaintenanceRequest dbo) {
        Persistence.service().retrieve(dbo.building());
//        Persistence.service().retrieve(dbo.reporter());
//        Persistence.service().retrieve(dbo.category());
        MaintenanceRequestCategory parent = dbo.category().parent();
        while (!parent.isNull()) {
            Persistence.ensureRetrieve(parent, AttachLevel.Attached);
            parent = parent.parent();
        }
        Persistence.ensureRetrieve(dbo.workHistory(), AttachLevel.Attached);
    }

    @Override
    protected MaintenanceRequest retrieve(Key entityId, RetrieveTarget retrieveTarget) {
        // get building first
        MaintenanceRequest request = Persistence.service().retrieve(MaintenanceRequest.class, entityId);
        ServerSideFactory.create(MaintenanceFacade.class).beforeItemRequest(request.building());
        MaintenanceRequest result = super.retrieve(entityId, retrieveTarget);
        return result;
    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<MaintenanceRequestDTO>> callback, EntityListCriteria<MaintenanceRequestDTO> dtoCriteria) {
        ServerSideFactory.create(MaintenanceFacade.class).beforeListRequest();
        super.list(callback, dtoCriteria);
    }

    @Override
    public void sheduleAction(AsyncCallback<VoidSerializable> callback, MaintenanceRequestScheduleDTO scheduleDTO, Key entityId) {
        MaintenanceRequest request = Persistence.service().retrieve(MaintenanceRequest.class, entityId);
        enhanceDbo(request);
        MaintenanceRequestSchedule schedule = EntityFactory.create(MaintenanceRequestSchedule.class);
        schedule.scheduledDate().set(scheduleDTO.scheduledDate());
        schedule.scheduledTimeFrom().set(scheduleDTO.scheduledTimeFrom());
        schedule.scheduledTimeTo().set(scheduleDTO.scheduledTimeTo());
        schedule.workDescription().set(scheduleDTO.workDescription());
        ServerSideFactory.create(MaintenanceFacade.class).sheduleMaintenanceRequest(request, schedule);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void updateProgressAction(AsyncCallback<VoidSerializable> callback, String progressNote, Key scheduleId) {
        MaintenanceRequestSchedule schedule = Persistence.service().retrieve(MaintenanceRequestSchedule.class, scheduleId);
        schedule.progressNote().setValue(progressNote);
        Persistence.service().persist(schedule);
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
    public void getCategoryMeta(AsyncCallback<MaintenanceRequestMetadataDTO> callback, boolean levelsOnly, Building building) {
        if (building.isNull() && VistaFeatures.instance().yardiIntegration()) {
            // ensure single interface
            if (VistaFeatures.instance().yardiInterfaces() > 1) {
                throw new Error("Building selection must be forced");
            }
            // single interface - use first available building
            building = VistaDeployment.getPmcYardiBuildings(VistaDeployment.getPmcYardiCredentials().get(0)).get(0);
        }
        MaintenanceRequestMetadata meta = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(building);
        MaintenanceRequestMetadataDTO metaDto = EntityFactory.create(MaintenanceRequestMetadataDTO.class);
        metaDto.categoryLevels().addAll(meta.categoryLevels());
        metaDto.statuses().addAll(meta.statuses());
        metaDto.priorities().addAll(meta.priorities());
        if (!levelsOnly) {
            metaDto.rootCategory().set(meta.rootCategory());
        }
        callback.onSuccess(metaDto);
    }

    @Override
    protected void persist(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        ServerSideFactory.create(MaintenanceFacade.class).postMaintenanceRequest(entity);
    }
}
