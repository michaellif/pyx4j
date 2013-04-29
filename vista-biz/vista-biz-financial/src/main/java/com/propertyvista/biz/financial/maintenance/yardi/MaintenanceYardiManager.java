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
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.maintenance.MaintenanceAbstractManager;
import com.propertyvista.biz.system.YardiMaintenanceFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.property.asset.BuildingElement;

public class MaintenanceYardiManager extends MaintenanceAbstractManager {

    private Date ticketTS;

    private static class SingletonHolder {
        public static final MaintenanceYardiManager INSTANCE = new MaintenanceYardiManager();
    }

    static MaintenanceYardiManager instance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void postMaintenanceRequest(MaintenanceRequest request) {
        postRequest(request);
    }

    @Override
    public void cancelMaintenanceRequest(MaintenanceRequest request) {
        request.status().set(getMaintenanceStatus(StatusPhase.Cancelled));
        request.updated().setValue(new LogicalDate(SystemDateManager.getDate()));
        postRequest(request);
    }

    @Override
    public void rateMaintenanceRequest(MaintenanceRequest request, SurveyResponse rate) {
        request.surveyResponse().set(rate);
        postRequest(request);
    }

    @Override
    public void sheduleMaintenanceRequest(MaintenanceRequest request, LogicalDate date, Time time) {
        request.scheduledDate().setValue(date);
        request.scheduledTime().setValue(time);
        request.status().set(getMaintenanceStatus(StatusPhase.Scheduled));
        postRequest(request);
    }

    @Override
    public void resolveMaintenanceRequest(MaintenanceRequest request) {
        request.status().set(getMaintenanceStatus(StatusPhase.Resolved));
        postRequest(request);
    }

    @Override
    public List<MaintenanceRequest> getMaintenanceRequests(Set<StatusPhase> statuses, BuildingElement buildingElement) {
        importModifiedRequests();
        return super.getMaintenanceRequests(statuses, buildingElement);
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
            MaintenanceRequest result = ServerSideFactory.create(YardiMaintenanceFacade.class).postMaintenanceRequest(request);
            Persistence.secureSave(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void importModifiedRequests() {
        Date lastMetaUpdate = ServerSideFactory.create(YardiMaintenanceFacade.class).getTicketTimestamp();
        if (ticketTS == null || !ticketTS.equals(lastMetaUpdate)) {
            try {
                ServerSideFactory.create(YardiMaintenanceFacade.class).loadMaintenanceRequests();
                ticketTS = lastMetaUpdate;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
