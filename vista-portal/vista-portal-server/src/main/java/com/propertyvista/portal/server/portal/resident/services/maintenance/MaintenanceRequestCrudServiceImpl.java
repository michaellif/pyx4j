/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.resident.services.maintenance;

import java.util.EnumSet;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestSchedule;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.PermissionToEnterNote;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.policy.policies.MaintenanceRequestPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceRequestStatusDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceSummaryDTO;
import com.propertyvista.portal.rpc.portal.resident.services.maintenance.MaintenanceRequestCrudService;
import com.propertyvista.portal.rpc.shared.PolicyNotFoundException;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;
import com.propertyvista.shared.i18n.CompiledLocale;

public class MaintenanceRequestCrudServiceImpl extends AbstractCrudServiceDtoImpl<MaintenanceRequest, MaintenanceRequestDTO> implements
        MaintenanceRequestCrudService {

    public MaintenanceRequestCrudServiceImpl() {
        super(MaintenanceRequest.class, MaintenanceRequestDTO.class);
    }

    @Override
    public void bind() {
        bindCompleteObject();
    }

    @Override
    protected MaintenanceRequestDTO init(InitializationData initializationData) {
        MaintenanceRequest maintenanceRequest = ServerSideFactory.create(MaintenanceFacade.class).createNewRequestForTenant(
                ResidentPortalContext.getLeaseTermTenant().leaseParticipant());
        MaintenanceRequestDTO dto = createTO(maintenanceRequest);
        setPermissionToEnterNote(dto);

        return dto;
    }

    private void setPermissionToEnterNote(MaintenanceRequestDTO dto) {
        // add PermissionToEnter wording
        LeaseTermTenant tenant = ResidentPortalContext.getLeaseTermTenant();
        Persistence.ensureRetrieve(tenant.leaseParticipant().lease().unit().building(), AttachLevel.IdOnly);
        try {
            MaintenanceRequestPolicy mrPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                    tenant.leaseParticipant().lease().unit().building(), MaintenanceRequestPolicy.class);
            for (PermissionToEnterNote note : mrPolicy.permissionToEnterNote()) {
                if (note.locale().lang().getValue().name().startsWith(CompiledLocale.en.name())) {
                    dto.notePermissionToEnter().set(note.text());
                    break;
                }
            }
        } catch (PolicyNotFoundException e) {
            // ignore
        }
    }

    @Override
    protected void enhanceRetrieved(MaintenanceRequest bo, MaintenanceRequestDTO to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieveMember(bo.pictures());
        to.pictures().set(bo.pictures());
        enhanceAll(to);
    }

    @Override
    protected void enhanceListRetrieved(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        enhanceAll(dto);
    }

    protected void enhanceAll(MaintenanceRequestDTO dto) {
        enhanceDbo(dto);
        dto.reportedForOwnUnit().setValue(ResidentPortalContext.getUnit().id().equals(dto.unit().id()));
        setPermissionToEnterNote(dto);

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
        Persistence.ensureRetrieve(dbo.pictures(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dbo.reporter(), AttachLevel.Attached);
        MaintenanceRequestCategory parent = dbo.category().parent();
        while (!parent.isNull()) {
            Persistence.ensureRetrieve(parent, AttachLevel.Attached);
            parent = parent.parent();
        }
        Persistence.ensureRetrieve(dbo.workHistory(), AttachLevel.Attached);
    }

    @Override
    protected void persist(MaintenanceRequest bo, MaintenanceRequestDTO to) {
        if (!to.reportedForOwnUnit().isBooleanTrue()) {
            bo.permissionToEnter().setValue(false);
        } else if (!to.reporter().isNull()) {
            Persistence.ensureRetrieve(to.reporter().lease(), AttachLevel.Attached);
            bo.unit().set(to.reporter().lease().unit());
        }
        ServerSideFactory.create(MaintenanceFacade.class).postMaintenanceRequest(bo);
    }

    @Override
    public void cancelMaintenanceRequest(AsyncCallback<VoidSerializable> callback, Key requestId) {
        MaintenanceRequest request = Persistence.service().retrieve(MaintenanceRequest.class, requestId);
        MaintenanceRequestStatus oldStatus = request.status().duplicate();
        enhanceDbo(request);
        ServerSideFactory.create(MaintenanceFacade.class).cancelMaintenanceRequest(request);
        saveRequest(request, oldStatus);
        callback.onSuccess(null);
    }

    @Override
    public void rateMaintenanceRequest(AsyncCallback<VoidSerializable> callback, Key requestId, Integer rate) {
        MaintenanceRequest request = Persistence.service().retrieve(MaintenanceRequest.class, requestId);
        MaintenanceRequestStatus oldStatus = request.status().duplicate();
        enhanceDbo(request);
        SurveyResponse response = EntityFactory.create(SurveyResponse.class);
        response.rating().setValue(rate);
        ServerSideFactory.create(MaintenanceFacade.class).rateMaintenanceRequest(request, response);
        saveRequest(request, oldStatus);
        callback.onSuccess(null);
    }

    @Override
    public void getCategoryMeta(AsyncCallback<MaintenanceRequestMetadata> callback, boolean levelsOnly) {
        Tenant tenant = ResidentPortalContext.getLeaseTermTenant().leaseParticipant();
        Persistence.ensureRetrieve(tenant.lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(tenant.lease().unit(), AttachLevel.Attached);
        Persistence.ensureRetrieve(tenant.lease().unit().building(), AttachLevel.Attached);
        Building building = tenant.lease().unit().building();
        MaintenanceRequestMetadata meta = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(building);
        if (levelsOnly) {
            meta.rootCategory().subCategories().setAttachLevel(AttachLevel.Detached);
        }
        callback.onSuccess(meta);
    }

    @Override
    public void retreiveMaintenanceSummary(AsyncCallback<MaintenanceSummaryDTO> callback) {
        MaintenanceSummaryDTO dto = EntityFactory.create(MaintenanceSummaryDTO.class);
        List<MaintenanceRequest> requests = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceRequests(
                EnumSet.allOf(MaintenanceRequestStatus.StatusPhase.class), ResidentPortalContext.getTenant());
        for (MaintenanceRequest mr : requests) {
            MaintenanceRequestStatusDTO statusDto = EntityFactory.create(MaintenanceRequestStatusDTO.class);
            statusDto.id().setValue(mr.getPrimaryKey());
            statusDto.subject().set(mr.summary());
            statusDto.description().set(mr.description());
            statusDto.status().set(mr.status());
            statusDto.priority().set(mr.priority());
            statusDto.lastUpdated().set(mr.updated());
            statusDto.surveyResponse().set(mr.surveyResponse());
            if (MaintenanceRequestStatus.StatusPhase.open().contains(mr.status().phase().getValue())) {
                dto.openMaintenanceRequests().add(statusDto);
            } else {
                dto.closedMaintenanceRequests().add(statusDto);
            }
        }

        callback.onSuccess(dto);
    }

    private void saveRequest(MaintenanceRequest request, MaintenanceRequestStatus oldStatus) {
        ServerSideFactory.create(MaintenanceFacade.class).addStatusHistoryRecord(request, oldStatus);
        Persistence.service().commit();
    }
}
