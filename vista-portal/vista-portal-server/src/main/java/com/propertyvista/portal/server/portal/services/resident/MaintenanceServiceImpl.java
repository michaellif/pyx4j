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
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
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
                TenantAppContext.getCurrentCustomerUnit());
        for (MaintenanceRequest mr : requests) {
            MaintenanceRequestDTO mrDto = createDTO(mr);
            enhanceAll(mrDto);
            dto.add(mrDto);
        }
        return dto;
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
        Persistence.service().retrieve(dto.reporter());
        Persistence.service().retrieve(dto.category());
        MaintenanceRequestCategory parent = dto.category().parent();
        while (!parent.isNull()) {
            Persistence.ensureRetrieve(parent, AttachLevel.Attached);
            parent = parent.parent();
        }
        dto.reportedForOwnUnit().setValue(TenantAppContext.getCurrentCustomerUnit().id().equals(dto.unit().id()));
    }

    @Override
    protected void persist(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        if (dto.reportedForOwnUnit().isBooleanTrue()) {
            entity.reporter().set(TenantAppContext.getCurrentUserTenant());
            entity.unit().set(TenantAppContext.getCurrentCustomerUnit());
            entity.building().setPrimaryKey(entity.unit().building().getPrimaryKey());
        }
        ServerSideFactory.create(MaintenanceFacade.class).postMaintenanceRequest(entity);
    }

    @Override
    public void cancelMaintenanceRequest(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO dto) {
        enhanceAll(dto);
        ServerSideFactory.create(MaintenanceFacade.class).cancelMaintenanceRequest(dto);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void rateMaintenanceRequest(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO dto, Integer rate) {
        SurveyResponse response = EntityFactory.create(SurveyResponse.class);
        response.rating().setValue(rate);
        ServerSideFactory.create(MaintenanceFacade.class).rateMaintenanceRequest(dto, response);
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
        callback.onSuccess(ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(levelsOnly));
    }
}
