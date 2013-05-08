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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Tenant;

public abstract class MaintenanceAbstractManager {
    private final static Logger log = LoggerFactory.getLogger(MaintenanceAbstractManager.class);

    public MaintenanceRequest createNewRequest(Building building) {
        MaintenanceRequest request = EntityFactory.create(MaintenanceRequest.class);
        request.building().set(building);
        return request;
    }

    public MaintenanceRequest createNewRequest(Tenant tenant) {
        MaintenanceRequest request = EntityFactory.create(MaintenanceRequest.class);
        Persistence.ensureRetrieve(tenant.lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(tenant.lease().unit().building(), AttachLevel.Attached);
        request.building().set(tenant.lease().unit().building());
        request.reporter().set(tenant);
        return request;
    }

    public void postMaintenanceRequest(MaintenanceRequest request) {
        request.updated().setValue(SystemDateManager.getDate());
        if (!request.reporter().isNull()) {
            if (request.reporterName().isNull()) {
                request.reporterName().setValue(request.reporter().customer().person().name().getStringView());
            }
            if (request.reporterEmail().isNull()) {
                request.reporterEmail().setValue(request.reporter().customer().person().email().getStringView());
            }
            if (request.reporterPhone().isNull()) {
                String phone = request.reporter().customer().person().mobilePhone().getStringView();
                if (phone == null) {
                    phone = request.reporter().customer().person().homePhone().getStringView();
                }
                if (phone == null) {
                    phone = request.reporter().customer().person().workPhone().getStringView();
                }
                request.reporterPhone().setValue(phone);
            }
        }
        boolean isNewRequest = false;
        if (request.id().isNull()) {
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(request);
            request.status().set(getMaintenanceStatus(StatusPhase.Submitted));
            isNewRequest = true;
        }
        Persistence.secureSave(request);

        if (isNewRequest) {
            sendAdminNote(request, true);
            sendReporterNote(request, true);
        }
    }

    public void cancelMaintenanceRequest(MaintenanceRequest request) {
        request.status().set(getMaintenanceStatus(StatusPhase.Cancelled));
        request.updated().setValue(SystemDateManager.getDate());
        Persistence.secureSave(request);

        sendReporterNote(request, false);
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

        sendReporterNote(request, false);
    }

    public void resolveMaintenanceRequest(MaintenanceRequest request) {
        request.status().set(getMaintenanceStatus(StatusPhase.Resolved));
        Persistence.secureSave(request);

        sendReporterNote(request, false);
    }

    public List<MaintenanceRequest> getMaintenanceRequests(Set<StatusPhase> statuses, BuildingElement buildingElement) {
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status().phase(), statuses));
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), buildingElement));
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

    protected void sendAdminNote(MaintenanceRequest request, boolean isNewRequest) {
        Persistence.ensureRetrieve(request.building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(request.building().contacts().propertyContacts(), AttachLevel.Attached);
        for (PropertyContact cont : request.building().contacts().propertyContacts()) {
            if (PropertyContactType.superintendent.equals(cont.type().getValue())) {
                String sendTo = cont.email().getValue();
                String userName = cont.name().getValue();
                try {
                    ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestEmail(sendTo, userName, request, isNewRequest, true);
                } catch (Exception e) {
                    log.warn("Email communication failed: {}", e);
                }
                return;
            }
        }
    }

    protected void sendReporterNote(MaintenanceRequest request, boolean isNewRequest) {
        if (request.reporter().isNull() || request.reporter().customer().person().email().isNull()) {
            return;
        }

        String sendTo = request.reporter().customer().person().email().getValue();
        String userName = "Tenant";
        if (!request.reporter().isNull() && !request.reporter().customer().person().email().isNull()) {
            userName = request.reporter().customer().person().name().getStringView();
        }
        try {
            ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestEmail(sendTo, userName, request, isNewRequest, false);
        } catch (Exception e) {
            log.warn("Email communication failed: {}", e);
        }
    }
}
