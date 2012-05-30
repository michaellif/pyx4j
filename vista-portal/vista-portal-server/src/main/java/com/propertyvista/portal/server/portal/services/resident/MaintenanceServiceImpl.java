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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
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
        bindCompleateDBO();
    }

    @Override
    public void listOpenIssues(AsyncCallback<Vector<MaintenanceRequestDTO>> callback) {
        Vector<MaintenanceRequestDTO> dto = new Vector<MaintenanceRequestDTO>();
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status(), MaintenanceRequestStatus.Scheduled, MaintenanceRequestStatus.Submitted));
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), TenantAppContext.getCurrentUserTenantInLease()));
        for (MaintenanceRequest mr : Persistence.service().query(criteria.desc(criteria.proto().submitted()))) {
            dto.add(Converter.convert(mr));
        }
        callback.onSuccess(dto);
    }

    @Override
    public void listHistoryIssues(AsyncCallback<Vector<MaintenanceRequestDTO>> callback) {
        Vector<MaintenanceRequestDTO> dto = new Vector<MaintenanceRequestDTO>();
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status(), MaintenanceRequestStatus.Resolved));
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), TenantAppContext.getCurrentUserTenantInLease()));
        for (MaintenanceRequest mr : Persistence.service().query(criteria.desc(criteria.proto().submitted()))) {
            dto.add(Converter.convert(mr));
        }
        callback.onSuccess(dto);
    }

    @Override
    protected void enhanceRetrieved(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        enhanceAll(dto);
    }

    @Override
    protected void enhanceListRetrieved(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        enhanceAll(dto);
    }

    protected void enhanceAll(MaintenanceRequestDTO dto) {
        Persistence.service().retrieve(dto.tenant());
        Persistence.service().retrieve(dto.issueClassification());
        Persistence.service().retrieve(dto.issueClassification().subjectDetails());
        Persistence.service().retrieve(dto.issueClassification().subjectDetails().subject());
        Persistence.service().retrieve(dto.issueClassification().subjectDetails().subject().issueElement());
    }

    @Override
    protected void persist(MaintenanceRequest entity, MaintenanceRequestDTO dto) {
        entity.tenant().set(TenantAppContext.getCurrentUserTenantInLease());
        super.persist(entity, dto);
    }

    @Override
    public void cancelTicket(AsyncCallback<Vector<MaintenanceRequestDTO>> callback, MaintenanceRequestDTO dto) {
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), dto.id()));
        List<MaintenanceRequest> rs = Persistence.service().query(criteria);
        if (rs.size() > 0) {
            MaintenanceRequest req = rs.get(0);
            req.status().setValue(MaintenanceRequestStatus.Cancelled);
            req.updated().setValue(new LogicalDate());
            Persistence.service().merge(req);
            Persistence.service().commit();
            listOpenIssues(callback);
        } else {
            callback.onFailure(new Throwable("Ticket not found."));
        }
    }

    @Override
    public void rateTicket(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO dto, Integer rate) {
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), dto.id()));
        List<MaintenanceRequest> rs = Persistence.service().query(criteria);
        if (rs.size() > 0) {
            MaintenanceRequest req = rs.get(0);
            req.surveyResponse().rating().setValue(rate);
            Persistence.service().merge(req);
            Persistence.service().commit();
            callback.onSuccess(null);
        } else {
            callback.onFailure(new Throwable("Ticket not found."));
        }
    }
}
