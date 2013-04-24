/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.biz.financial.maintenance;

import java.sql.Time;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;

public abstract class MaintenanceAbstractManager {

    public MaintenanceRequest createNewRequest(Tenant tenant) {
        MaintenanceRequestDTO request = EntityFactory.create(MaintenanceRequestDTO.class);
        request.leaseParticipant().set(tenant);
        request.status().set(getMaintenanceStatus(StatusPhase.Submitted));
        return request;
    }

    public void postMaintenanceRequest(MaintenanceRequest request, Tenant tenant) {
        request.leaseParticipant().set(tenant);
        Persistence.secureSave(request);
    }

    public void cancelMaintenanceRequest(MaintenanceRequest request) {
        request.status().set(getMaintenanceStatus(StatusPhase.Cancelled));
        request.updated().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.secureSave(request);
    }

    public void rateMaintenanceRequest(MaintenanceRequest request, SurveyResponse rate) {
        request.surveyResponse().set(rate);
        Persistence.secureSave(request);
    }

    public void sheduleMaintenanceRequest(MaintenanceRequest request, LogicalDate date, Time time) {
        request.scheduledDate().setValue(date);
        request.scheduledTime().setValue(time);
        request.status().set(getMaintenanceStatus(StatusPhase.Scheduled));
        Persistence.secureSave(request);
    }

    public void resolveMaintenanceRequest(MaintenanceRequest request) {
        request.status().set(getMaintenanceStatus(StatusPhase.Resolved));
        Persistence.secureSave(request);
    }

    public List<MaintenanceRequest> getClosedMaintenanceRequests(Tenant tenant) {
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status().phase(), StatusPhase.closed()));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), tenant));
        return Persistence.service().query(criteria.desc(criteria.proto().updated()));
    }

    public List<MaintenanceRequest> getOpenMaintenanceRequests(Tenant tenant) {
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status().phase(), StatusPhase.opened()));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), tenant));
        return Persistence.service().query(criteria.desc(criteria.proto().updated()));
    }

    public MaintenanceRequest getMaintenanceRequest(String requestId) {
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().requestId(), requestId));
        return Persistence.service().retrieve(criteria);
    }

    protected MaintenanceRequestStatus getMaintenanceStatus(StatusPhase phase) {
        if (phase != null) {
            MaintenanceRequestMetadata meta = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(true);
            for (MaintenanceRequestStatus status : meta.statuses()) {
                if (phase.equals(status.phase().getValue())) {
                    return status;
                }
            }
        }
        return null;
    }
}
