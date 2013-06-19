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

import java.sql.Time;
import java.util.List;
import java.util.Set;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.maintenance.MaintenanceAbstractManager;
import com.propertyvista.biz.system.YardiMaintenanceFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.tenant.lease.Tenant;

public class MaintenanceYardiManager extends MaintenanceAbstractManager {

    private static class SingletonHolder {
        public static final MaintenanceYardiManager INSTANCE = new MaintenanceYardiManager();
    }

    static MaintenanceYardiManager instance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void postMaintenanceRequest(MaintenanceRequest request) {
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
            ServerSideFactory.create(NotificationFacade.class).maintenanceRequest(request, true);
            sendReporterNote(request, true);
        }
    }

    @Override
    public void cancelMaintenanceRequest(MaintenanceRequest request) {
        MaintenanceRequestStatus status = getMaintenanceStatus(StatusPhase.Cancelled);
        if (status != null) {
            request.status().set(status);
            postRequest(request);

            sendReporterNote(request, false);
        }
    }

    @Override
    public void rateMaintenanceRequest(MaintenanceRequest request, SurveyResponse rate) {
        request.surveyResponse().set(rate);
        postRequest(request);
    }

    @Override
    public void sheduleMaintenanceRequest(MaintenanceRequest request, LogicalDate date, Time time) {
        MaintenanceRequestStatus status = getMaintenanceStatus(StatusPhase.Scheduled);
        if (status != null) {
            request.status().set(status);
            request.scheduledDate().setValue(date);
            request.scheduledTime().setValue(time);
            postRequest(request);

            sendReporterNote(request, false);
        }
    }

    @Override
    public void resolveMaintenanceRequest(MaintenanceRequest request) {
        MaintenanceRequestStatus status = getMaintenanceStatus(StatusPhase.Resolved);
        if (status != null) {
            request.status().set(status);
            postRequest(request);

            sendReporterNote(request, false);
        }
    }

    @Override
    public List<MaintenanceRequest> getMaintenanceRequests(Set<StatusPhase> statuses, Tenant reporter) {
        importModifiedRequests();
        return super.getMaintenanceRequests(statuses, reporter);
    }

    @Override
    public MaintenanceRequest getMaintenanceRequest(String requestId) {
        importModifiedRequests();
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().requestId(), requestId));
        return Persistence.service().retrieve(criteria);
    }

    protected void beforeItemRequest() {
        importModifiedRequests();
    }

    protected void beforeListRequest() {
        importModifiedRequests();
    }

    private void postRequest(MaintenanceRequest request) {
        try {
            request.updated().setValue(SystemDateManager.getDate());
            ServerSideFactory.create(YardiMaintenanceFacade.class).postMaintenanceRequest(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void importModifiedRequests() {
        try {
            ServerSideFactory.create(YardiMaintenanceFacade.class).loadMaintenanceRequests();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
