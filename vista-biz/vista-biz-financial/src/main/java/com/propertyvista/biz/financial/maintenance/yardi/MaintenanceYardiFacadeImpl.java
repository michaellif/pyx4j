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
 * @author michaellif
 */
package com.propertyvista.biz.financial.maintenance.yardi;

import java.util.List;
import java.util.Set;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestWorkOrder;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Tenant;

public class MaintenanceYardiFacadeImpl implements MaintenanceFacade {

    @Override
    public MaintenanceRequestMetadata getMaintenanceMetadata(Building building) {
        return MaintenanceMetadataYardiManager.instance().getMaintenanceMetadata(building);
    }

    @Override
    public List<MaintenanceRequest> getMaintenanceRequests(Set<StatusPhase> statuses, Tenant reporter) {
        return MaintenanceYardiManager.instance().getMaintenanceRequests(statuses, reporter);
    }

    @Override
    public void postMaintenanceRequest(MaintenanceRequest maintenanceRequest, Employee requestReporter) {
        MaintenanceYardiManager.instance().postMaintenanceRequest(maintenanceRequest, requestReporter);
    }

    @Override
    public MaintenanceRequest createNewRequest() {
        return MaintenanceYardiManager.instance().createNewRequest();
    }

    @Override
    public MaintenanceRequest createNewRequest(Building building) {
        return MaintenanceYardiManager.instance().createNewRequest(building);
    }

    @Override
    public MaintenanceRequest createNewRequest(AptUnit unit) {
        return MaintenanceYardiManager.instance().createNewRequest(unit);
    }

    @Override
    public MaintenanceRequest createNewRequestForTenant(Tenant tenant) {
        return MaintenanceYardiManager.instance().createNewRequest(tenant);
    }

    @Override
    public void cancelMaintenanceRequest(MaintenanceRequest request, CommunicationEndpoint requestReporter) {
        MaintenanceYardiManager.instance().cancelMaintenanceRequest(request, requestReporter);
    }

    @Override
    public void rateMaintenanceRequest(MaintenanceRequest request, SurveyResponse rate) {
        MaintenanceYardiManager.instance().rateMaintenanceRequest(request, rate);
    }

    @Override
    public void scheduleMaintenanceRequest(MaintenanceRequest request, MaintenanceRequestWorkOrder schedule, Employee requestReporter) {
        MaintenanceYardiManager.instance().scheduleMaintenanceRequest(request, schedule, requestReporter);
    }

    @Override
    public void resolveMaintenanceRequest(MaintenanceRequest request, Employee requestReporter) {
        MaintenanceYardiManager.instance().resolveMaintenanceRequest(request, requestReporter);
    }

    @Override
    public void beforeItemRequest(Building building) {
        MaintenanceYardiManager.instance().beforeItemRequest(building);
    }

    @Override
    public void beforeListRequest() {
        MaintenanceYardiManager.instance().beforeListRequest();
    }

    @Override
    public void addStatusHistoryRecord(MaintenanceRequest request, MaintenanceRequestStatus oldStatus) {
        MaintenanceYardiManager.instance().addStatusHistoryRecord(request, oldStatus);
    }
}
