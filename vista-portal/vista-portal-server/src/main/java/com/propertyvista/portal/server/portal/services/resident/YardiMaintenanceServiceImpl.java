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

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.maintenance.YardiServiceRequest;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.YardiServiceRequestDTO;
import com.propertyvista.portal.rpc.portal.services.resident.YardiMaintenanceService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.portal.server.ptapp.util.Converter;

public class YardiMaintenanceServiceImpl extends AbstractCrudServiceDtoImpl<YardiServiceRequest, YardiServiceRequestDTO> implements YardiMaintenanceService {

    public YardiMaintenanceServiceImpl() {
        super(YardiServiceRequest.class, YardiServiceRequestDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    public void listOpenIssues(AsyncCallback<Vector<YardiServiceRequestDTO>> callback) {
        callback.onSuccess(listOpenIssues());
    }

    static Vector<YardiServiceRequestDTO> listOpenIssues() {
        Vector<YardiServiceRequestDTO> dto = new Vector<YardiServiceRequestDTO>();
        EntityQueryCriteria<YardiServiceRequest> criteria = EntityQueryCriteria.create(YardiServiceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().currentStatus(), "Unreviewed", "Pending", "Awaiting Information", "Scheduled"));
        for (YardiServiceRequest mr : Persistence.service().query(criteria)) {
            dto.add(Converter.convert(mr));
        }
        return dto;
    }

    @Override
    public void listHistoryIssues(AsyncCallback<Vector<YardiServiceRequestDTO>> callback) {
        Vector<YardiServiceRequestDTO> dto = new Vector<YardiServiceRequestDTO>();
        EntityQueryCriteria<YardiServiceRequest> criteria = EntityQueryCriteria.create(YardiServiceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().currentStatus(), "Completed", "Closed", "Canceled"));
        criteria.add(PropertyCriterion.eq(criteria.proto().tenantCode(), TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().participantId()));
        for (YardiServiceRequest mr : Persistence.service().query(criteria)) {
            dto.add(Converter.convert(mr));
        }
        callback.onSuccess(dto);
    }

    @Override
    protected void enhanceRetrieved(YardiServiceRequest entity, YardiServiceRequestDTO dto, RetrieveTraget retrieveTraget) {
        enhanceAll(dto);
    }

    @Override
    protected void enhanceListRetrieved(YardiServiceRequest entity, YardiServiceRequestDTO dto) {
        enhanceAll(dto);
    }

    protected void enhanceAll(YardiServiceRequestDTO dto) {

    }

    @Override
    protected void persist(YardiServiceRequest entity, YardiServiceRequestDTO dto) {
        // if it's getting saved, it is a new request.
        entity.requestId().setValue(0);
        Lease lease = TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().lease();
        Persistence.service().retrieve(lease);
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().building());
        entity.propertyCode().setValue(lease.unit().building().propertyCode().getValue());
        entity.unitCode().setValue(lease.unit().info().number().getValue());
        entity.tenantCode().setValue(TenantAppContext.getCurrentUserTenantInLease().leaseParticipant().participantId().getValue());
        entity.currentStatus().setValue("Unreviewed");

        super.persist(entity, dto);
    }

    @Override
    public void cancelTicket(AsyncCallback<VoidSerializable> callback, YardiServiceRequestDTO dto) {
        EntityQueryCriteria<YardiServiceRequest> criteria = EntityQueryCriteria.create(YardiServiceRequest.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), dto.id()));
        List<YardiServiceRequest> rs = Persistence.service().query(criteria);
        if (rs.size() > 0) {
            YardiServiceRequest req = rs.get(0);
            req.currentStatus().setValue("Canceled");
            Persistence.service().merge(req);
            Persistence.service().commit();
            callback.onSuccess(null);
        } else {
            callback.onFailure(new Throwable("Ticket not found."));
        }
    }
}
