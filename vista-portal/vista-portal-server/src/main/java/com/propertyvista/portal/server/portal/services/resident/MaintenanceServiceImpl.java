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
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.services.resident.MaintenanceService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.portal.server.ptapp.util.Converter;

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
        callback.onSuccess(listOpenIssues());
    }

    static Vector<MaintenanceRequestDTO> listOpenIssues() {
        Vector<MaintenanceRequestDTO> dto = new Vector<MaintenanceRequestDTO>();
        List<MaintenanceRequest> requests = ServerSideFactory.create(MaintenanceFacade.class).getOpenMaintenanceRequests(
                TenantAppContext.getCurrentUserTenantInLease().leaseParticipant());
        for (MaintenanceRequest mr : requests) {
            Persistence.service().retrieve(mr.issueClassification());
            dto.add(Converter.convert(mr));
        }
        return dto;
    }

    @Override
    public void listHistoryIssues(AsyncCallback<Vector<MaintenanceRequestDTO>> callback) {
        Vector<MaintenanceRequestDTO> dto = new Vector<MaintenanceRequestDTO>();
        List<MaintenanceRequest> requests = ServerSideFactory.create(MaintenanceFacade.class).getClosedMaintenanceRequests(
                TenantAppContext.getCurrentUserTenantInLease().leaseParticipant());
        for (MaintenanceRequest mr : requests) {
            Persistence.service().retrieve(mr.issueClassification());
            dto.add(Converter.convert(mr));
        }
        callback.onSuccess(dto);
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
        Persistence.service().retrieve(dto.leaseParticipant());
        Persistence.service().retrieve(dto.issueClassification());
    }

    @Override
    protected void persist(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        ServerSideFactory.create(MaintenanceFacade.class).postMaintenanceRequest(entity, TenantAppContext.getCurrentUserTenantInLease().leaseParticipant());
    }

    @Override
    public void cancelMaintenanceRequest(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO dto) {
        ServerSideFactory.create(MaintenanceFacade.class).cancelMaintenanceRequest(callback, dto);
    }

    @Override
    public void rateMaintenanceRequest(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO dto, Integer rate) {
        ServerSideFactory.create(MaintenanceFacade.class).rateMaintenanceRequest(callback, dto, rate);
    }
}
