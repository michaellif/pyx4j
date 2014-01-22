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
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequest.ContactPhoneType;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestSchedule;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Tenant;

public abstract class MaintenanceAbstractManager {
    private final static Logger log = LoggerFactory.getLogger(MaintenanceAbstractManager.class);

    private final static I18n i18n = I18n.get(MaintenanceAbstractManager.class);

    public MaintenanceRequest createNewRequest() {
        MaintenanceRequest request = EntityFactory.create(MaintenanceRequest.class);
        request.reportedDate().setValue(SystemDateManager.getLogicalDate());

        return request;
    }

    public MaintenanceRequest createNewRequest(Building building) {
        MaintenanceRequest request = createNewRequest();

        Persistence.ensureRetrieve(building, AttachLevel.Attached);

        request.building().set(building);

        return request;
    }

    public MaintenanceRequest createNewRequest(AptUnit unit) {
        MaintenanceRequest request = createNewRequest();

        Persistence.ensureRetrieve(unit, AttachLevel.Attached);
        Persistence.ensureRetrieve(unit.building(), AttachLevel.Attached);

        request.building().set(unit.building());
        request.unit().set(unit);

        return request;
    }

    public MaintenanceRequest createNewRequest(Tenant tenant) {
        MaintenanceRequest request = createNewRequest();

        Persistence.ensureRetrieve(tenant, AttachLevel.Attached);
        Persistence.ensureRetrieve(tenant.lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(tenant.lease().unit().building(), AttachLevel.Attached);

        request.building().set(tenant.lease().unit().building());
        request.unit().set(tenant.lease().unit());
        request.reporter().set(tenant);

        if (!request.reporter().isNull()) {
            request.reporterName().setValue(request.reporter().customer().person().name().getStringView());
            request.reporterEmail().setValue(request.reporter().customer().person().email().getStringView());
            setReporterPhone(request);
        }

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
            if (request.reporterPhone().isNull() || request.phoneType().isNull()) {
                setReporterPhone(request);
            }
        }

        boolean isNewRequest = false;
        if (request.id().isNull()) {
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(request);
            request.status().set(getMaintenanceStatus(request.building(), StatusPhase.Submitted));
            isNewRequest = true;
        }
        Persistence.service().merge(request);

        if (isNewRequest) {
            ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestCreatedPMC(request);
            ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestCreatedTenant(request);
        }
    }

    public void cancelMaintenanceRequest(MaintenanceRequest request) {
        request.status().set(getMaintenanceStatus(request.building(), StatusPhase.Cancelled));
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
        request.status().set(getMaintenanceStatus(request.building(), StatusPhase.Scheduled));

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
        request.status().set(getMaintenanceStatus(request.building(), StatusPhase.Resolved));
        Persistence.service().merge(request);

        ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestCompleted(request);
    }

    public List<MaintenanceRequest> getMaintenanceRequests(Set<StatusPhase> statuses, Tenant reporter) {
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.in(criteria.proto().status().phase(), statuses);
        if (reporter != null) {
            Persistence.ensureRetrieve(reporter.lease(), AttachLevel.Attached);
            criteria.or( //
                    // everything opened by this tenant
                    PropertyCriterion.eq(criteria.proto().reporter(), reporter),
                    // everything opened for his unit during his lease
                    new AndCriterion( //
                            PropertyCriterion.eq(criteria.proto().unit(), reporter.lease().unit()), //
                            PropertyCriterion.ge(criteria.proto().submitted(), reporter.lease().leaseFrom()) //
                    ) //
            );
        }

        return Persistence.service().query(criteria.desc(criteria.proto().updated()));
    }

    protected MaintenanceRequestStatus getMaintenanceStatus(Building building, StatusPhase phase) {
        if (phase != null) {
            MaintenanceRequestMetadata meta = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(building);
            for (MaintenanceRequestStatus status : meta.statuses()) {
                if (phase.equals(status.phase().getValue())) {
                    return status;
                }
            }
        }
        return null;
    }

    private void setReporterPhone(MaintenanceRequest request) {
        if (request.reporter().isNull()) {
            return;
        }
        // check if phone number is available - use mobile first
        String phone = request.reporter().customer().person().mobilePhone().getStringView();
        ContactPhoneType type = ContactPhoneType.mobile;
        if (phone == null) {
            phone = request.reporter().customer().person().homePhone().getStringView();
            type = ContactPhoneType.home;
            if (phone == null) {
                phone = request.reporter().customer().person().workPhone().getStringView();
                type = ContactPhoneType.work;
            }
        }
        request.reporterPhone().setValue(phone);
        request.phoneType().setValue(type);
    }
}
