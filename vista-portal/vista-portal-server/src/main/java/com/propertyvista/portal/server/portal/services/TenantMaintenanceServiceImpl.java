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
package com.propertyvista.portal.server.portal.services;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.portal.rpc.portal.dto.MaintananceDTO;
import com.propertyvista.portal.rpc.portal.dto.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.services.TenantMaintenanceService;
import com.propertyvista.portal.server.portal.TenantAppContext;
import com.propertyvista.portal.server.ptapp.util.Converter;

public class TenantMaintenanceServiceImpl implements TenantMaintenanceService {

    private Customer getOwner() {
        EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), TenantAppContext.getCurrentUser()));
        return Persistence.service().retrieve(criteria);
    }

    @Override
    public void listOpenIssues(AsyncCallback<Vector<MaintananceDTO>> callback) {
        Vector<MaintananceDTO> dto = new Vector<MaintananceDTO>();
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status(), MaintenanceRequestStatus.Scheduled, MaintenanceRequestStatus.Submitted));
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), getOwner()));
        for (MaintenanceRequest mr : Persistence.service().query(criteria.desc(criteria.proto().submitted()))) {
            dto.add(Converter.convert(mr));
        }
        callback.onSuccess(dto);
    }

    @Override
    public void listHistoryIssues(AsyncCallback<Vector<MaintananceDTO>> callback) {
        Vector<MaintananceDTO> dto = new Vector<MaintananceDTO>();
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status(), MaintenanceRequestStatus.Completed));
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), getOwner()));
        for (MaintenanceRequest mr : Persistence.service().query(criteria.desc(criteria.proto().submitted()))) {
            dto.add(Converter.convert(mr));
        }
        callback.onSuccess(dto);
    }

    @Override
    public void createNewTicket(AsyncCallback<VoidSerializable> callback, MaintenanceRequestDTO dto) {
        MaintenanceRequest req = EntityFactory.create(MaintenanceRequest.class);
        req.tenant().set(getOwner());
        req.issueClassification().set(dto.issueClassification());
        req.description().set(dto.description());
        req.status().setValue(MaintenanceRequestStatus.Submitted);
        req.submitted().setValue(new LogicalDate());
        Persistence.service().persist(req);
        Persistence.service().commit();

        callback.onSuccess(null);
    }

    @Override
    public void cancelTicket(AsyncCallback<Vector<MaintananceDTO>> callback, MaintananceDTO dto) {
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
    public void rateTicket(AsyncCallback<VoidSerializable> callback, MaintananceDTO dto, Integer rate) {
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
