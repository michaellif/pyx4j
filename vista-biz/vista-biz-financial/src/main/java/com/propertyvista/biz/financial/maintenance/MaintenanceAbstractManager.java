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

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestSchedule;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
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
        request.unit().set(tenant.lease().unit());
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
        Persistence.service().merge(request);

        if (isNewRequest) {
            ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestCreatedPMC(request);
            ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestCreatedTenant(request);
        }
    }

    public void cancelMaintenanceRequest(MaintenanceRequest request) {
        request.status().set(getMaintenanceStatus(StatusPhase.Cancelled));
        request.updated().setValue(SystemDateManager.getDate());
        Persistence.service().merge(request);

        ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestCancelled(request);
    }

    public void rateMaintenanceRequest(MaintenanceRequest request, SurveyResponse rate) {
        request.surveyResponse().set(rate);
        Persistence.service().merge(request);
    }

    public void sheduleMaintenanceRequest(MaintenanceRequest request, MaintenanceRequestSchedule schedule) {
        request.workHistory().add(schedule);
        request.status().set(getMaintenanceStatus(StatusPhase.Scheduled));

        if (!request.unit().isNull() && request.permissionToEnter().isBooleanTrue()) {
            // send notice of entry if permission to access unit is granted
            MailMessage email = ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestEntryNotice(request);

            if (email != null) {
                schedule.noticeOfEntry().text().setValue(email.getHtmlBody() != null ? email.getHtmlBody() : email.getTextBody());
                schedule.noticeOfEntry().messageId().setValue(email.getHeader("Message-ID"));
                schedule.noticeOfEntry().messageDate().setValue(email.getHeader("Date"));
            }
        }

        Persistence.service().merge(request);
    }

    public void resolveMaintenanceRequest(MaintenanceRequest request) {
        request.status().set(getMaintenanceStatus(StatusPhase.Resolved));
        Persistence.service().merge(request);

        ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestCompleted(request);
    }

    public List<MaintenanceRequest> getMaintenanceRequests(Set<StatusPhase> statuses, Tenant reporter) {
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status().phase(), statuses));
        if (reporter != null) {
            criteria.add(PropertyCriterion.eq(criteria.proto().reporter(), reporter));
        }
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
