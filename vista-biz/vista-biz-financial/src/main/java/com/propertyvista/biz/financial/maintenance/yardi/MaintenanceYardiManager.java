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
package com.propertyvista.biz.financial.maintenance.yardi;

import java.util.List;
import java.util.Set;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.mail.MailMessage;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.biz.financial.maintenance.MaintenanceAbstractManager;
import com.propertyvista.biz.system.yardi.YardiMaintenanceFacade;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestSchedule;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Tenant;

public class MaintenanceYardiManager extends MaintenanceAbstractManager {

    private static class SingletonHolder {
        public static final MaintenanceYardiManager INSTANCE = new MaintenanceYardiManager();
    }

    static MaintenanceYardiManager instance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void postMaintenanceRequest(MaintenanceRequest request, Employee requestReporter) {
        if (!request.reporter().isNull()) {
            request.reporterName().setValue(request.reporter().customer().person().name().getStringView());
            // email and phone can be different
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
        boolean isNewRequest = request.id().isNull();
        postRequest(request);

        if (isNewRequest) {
            // TODO: send maintenance request mail from messaging system
            ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestCreatedPMC(request);
            MailMessage mail = ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestCreatedTenant(request);
            ServerSideFactory.create(CommunicationMessageFacade.class).association2Thread(request, requestReporter, mail.getHtmlBody());
        }
    }

    @Override
    public void cancelMaintenanceRequest(MaintenanceRequest request, CommunicationEndpoint requestReporter) {
        request.status().set(getMaintenanceStatus(request.building(), StatusPhase.Cancelled));
        postRequest(request);

        // TODO: send maintenance request mail from messaging system
        MailMessage message = ServerSideFactory.create(CommunicationFacade.class).sendMaintenanceRequestCancelled(request);
        ServerSideFactory.create(CommunicationMessageFacade.class).associationChange2Message(request, requestReporter, extractMailBody(message));
    }

    @Override
    public void rateMaintenanceRequest(MaintenanceRequest request, SurveyResponse rate) {
        request.surveyResponse().set(rate);
        postRequest(request);
    }

    @Override
    public void sheduleMaintenanceRequest(MaintenanceRequest request, MaintenanceRequestSchedule schedule, Employee requestReporter) {
        super.sheduleMaintenanceRequest(request, schedule, requestReporter);
    }

    @Override
    public void resolveMaintenanceRequest(MaintenanceRequest request, Employee requestReporter) {
        super.resolveMaintenanceRequest(request, requestReporter);
    }

    @Override
    public List<MaintenanceRequest> getMaintenanceRequests(Set<StatusPhase> statuses, Tenant reporter) {
        Persistence.ensureRetrieve(reporter.lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(reporter.lease().unit().building(), AttachLevel.Attached);
        importModifiedRequests(reporter.lease().unit().building());
        return super.getMaintenanceRequests(statuses, reporter);
    }

    protected void beforeItemRequest(Building building) {
        importModifiedRequests(building);
    }

    protected void beforeListRequest() {
        // get modified requests for all interfaces
        importModifiedRequests(null);
    }

    private void postRequest(MaintenanceRequest request) {
        try {
            request.updated().setValue(SystemDateManager.getDate());
            ServerSideFactory.create(YardiMaintenanceFacade.class).postMaintenanceRequest(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void importModifiedRequests(Building building) {
        try {
            ServerSideFactory.create(YardiMaintenanceFacade.class).loadMaintenanceRequests(building);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
