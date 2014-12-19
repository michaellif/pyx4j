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
package com.propertyvista.biz.financial.maintenance.internal;

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

public class MaintenanceInternalFacadeImpl implements MaintenanceFacade {

    @Override
    public MaintenanceRequestMetadata getMaintenanceMetadata(Building building) {
        return MaintenanceMetadataInternalManager.instance().getMaintenanceMetadata(building);
    }

    @Override
    public List<MaintenanceRequest> getMaintenanceRequests(Set<StatusPhase> statuses, Tenant reporter) {
        return MaintenanceInternalManager.instance().getMaintenanceRequests(statuses, reporter);
    }

    @Override
    public void postMaintenanceRequest(MaintenanceRequest request, Employee requestReporter) {
        MaintenanceInternalManager.instance().postMaintenanceRequest(request, requestReporter);
    }

    @Override
    public void cancelMaintenanceRequest(MaintenanceRequest request, CommunicationEndpoint requestReporter) {
        MaintenanceInternalManager.instance().cancelMaintenanceRequest(request, requestReporter);
    }

    @Override
    public void rateMaintenanceRequest(MaintenanceRequest request, SurveyResponse rate) {
        MaintenanceInternalManager.instance().rateMaintenanceRequest(request, rate);
    }

    @Override
    public MaintenanceRequest createNewRequest() {
        return MaintenanceInternalManager.instance().createNewRequest();
    }

    @Override
    public MaintenanceRequest createNewRequest(Building building) {
        return MaintenanceInternalManager.instance().createNewRequest(building);
    }

    @Override
    public MaintenanceRequest createNewRequest(AptUnit unit) {
        return MaintenanceInternalManager.instance().createNewRequest(unit);
    }

    @Override
    public MaintenanceRequest createNewRequestForTenant(Tenant tenant) {
        return MaintenanceInternalManager.instance().createNewRequest(tenant);
    }

    @Override
    public void sheduleMaintenanceRequest(MaintenanceRequest request, MaintenanceRequestWorkOrder schedule, Employee requestReporter) {
        MaintenanceInternalManager.instance().sheduleMaintenanceRequest(request, schedule, requestReporter);
    }

    @Override
    public void resolveMaintenanceRequest(MaintenanceRequest request, Employee requestReporter) {
        MaintenanceInternalManager.instance().resolveMaintenanceRequest(request, requestReporter);
    }

    @Override
    public void beforeItemRequest(Building building) {
        // N/A - do nothing
    }

    @Override
    public void beforeListRequest() {
        // N/A - do nothing
    }

    @Override
    public void addStatusHistoryRecord(MaintenanceRequest request, MaintenanceRequestStatus oldStatus) {
        MaintenanceInternalManager.instance().addStatusHistoryRecord(request, oldStatus);
    }
}
