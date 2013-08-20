/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestSchedule;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.services.resident.MaintenanceService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class MaintenanceServiceImpl extends AbstractCrudServiceDtoImpl<MaintenanceRequest, MaintenanceRequestDTO> implements MaintenanceService {

    public MaintenanceServiceImpl() {
        super(MaintenanceRequest.class, MaintenanceRequestDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    public void listOpenIssues(AsyncCallback<Vector<MaintenanceRequestDTO>> callback) {
        callback.onSuccess(listIssues(MaintenanceRequestStatus.StatusPhase.open()));
    }

    @Override
    public void listClosedIssues(AsyncCallback<Vector<MaintenanceRequestDTO>> callback) {
        callback.onSuccess(listIssues(MaintenanceRequestStatus.StatusPhase.closed()));
    }

    private Vector<MaintenanceRequestDTO> listIssues(Set<StatusPhase> statuses) {
        Vector<MaintenanceRequestDTO> dto = new Vector<MaintenanceRequestDTO>();
        List<MaintenanceRequest> requests = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceRequests(statuses,
                TenantAppContext.getCurrentUserTenant());
        for (MaintenanceRequest mr : requests) {
            MaintenanceRequestDTO mrDto = createDTO(mr);
            enhanceAll(mrDto);
            dto.add(mrDto);
        }
        return dto;
    }

    @Override
    protected void enhanceRetrieved(MaintenanceRequest entity, MaintenanceRequestDTO dto, RetrieveTarget retrieveTarget) {
        enhanceAll(dto);
    }

    @Override
    protected void enhanceListRetrieved(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        enhanceAll(dto);
    }

    protected void enhanceAll(MaintenanceRequestDTO dto) {
        enhanceDbo(dto);
        dto.reportedForOwnUnit().setValue(TenantAppContext.getCurrentCustomerUnit().id().equals(dto.unit().id()));
        // populate latest scheduled info
        if (!dto.workHistory().isEmpty()) {
            MaintenanceRequestSchedule latest = dto.workHistory().get(dto.workHistory().size() - 1);
            dto.scheduledDate().set(latest.scheduledDate());
            dto.scheduledTimeFrom().set(latest.scheduledTimeFrom());
            dto.scheduledTimeTo().set(latest.scheduledTimeTo());
        }
    }

    protected void enhanceDbo(MaintenanceRequest dbo) {
        Persistence.ensureRetrieve(dbo.building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dbo.reporter(), AttachLevel.Attached);
        MaintenanceRequestCategory parent = dbo.category().parent();
        while (!parent.isNull()) {
            Persistence.ensureRetrieve(parent, AttachLevel.Attached);
            parent = parent.parent();
        }
        Persistence.ensureRetrieve(dbo.workHistory(), AttachLevel.Attached);
    }

    @Override
    protected void persist(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        if (dto.reportedForOwnUnit().isBooleanTrue() && !dto.reporter().isNull()) {
            Persistence.ensureRetrieve(dto.reporter().lease(), AttachLevel.Attached);
            entity.unit().set(dto.reporter().lease().unit());
        }
        ServerSideFactory.create(MaintenanceFacade.class).postMaintenanceRequest(entity);
    }

    @Override
    public void cancelMaintenanceRequest(AsyncCallback<VoidSerializable> callback, Key requestId) {
        MaintenanceRequest request = Persistence.service().retrieve(MaintenanceRequest.class, requestId);
        enhanceDbo(request);
        ServerSideFactory.create(MaintenanceFacade.class).cancelMaintenanceRequest(request);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void rateMaintenanceRequest(AsyncCallback<VoidSerializable> callback, Key requestId, Integer rate) {
        MaintenanceRequest request = Persistence.service().retrieve(MaintenanceRequest.class, requestId);
        enhanceDbo(request);
        SurveyResponse response = EntityFactory.create(SurveyResponse.class);
        response.rating().setValue(rate);
        ServerSideFactory.create(MaintenanceFacade.class).rateMaintenanceRequest(request, response);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void createNewRequest(AsyncCallback<MaintenanceRequestDTO> callback) {
        Tenant tenant = TenantAppContext.getCurrentUserTenantInLease().leaseParticipant();
        MaintenanceRequest maintenanceRequest = ServerSideFactory.create(MaintenanceFacade.class).createNewRequestForTenant(tenant);
        callback.onSuccess(createDTO(maintenanceRequest));
    }

    @Override
    public void getCategoryMeta(AsyncCallback<MaintenanceRequestMetadata> callback, boolean levelsOnly) {
        Tenant tenant = TenantAppContext.getCurrentUserTenantInLease().leaseParticipant();
        Building building = tenant.lease().unit().building();
        MaintenanceRequestMetadata meta = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(building);
        if (levelsOnly) {
            meta.rootCategory().subCategories().setAttachLevel(AttachLevel.Detached);
        }
        callback.onSuccess(meta);
    }
}
