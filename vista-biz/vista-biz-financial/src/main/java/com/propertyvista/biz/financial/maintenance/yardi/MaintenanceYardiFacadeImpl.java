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
 * @version $Id$
 */
package com.propertyvista.biz.financial.maintenance.yardi;

import java.util.List;
import java.util.Set;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestSchedule;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.property.asset.building.Building;
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
    public void postMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
        MaintenanceYardiManager.instance().postMaintenanceRequest(maintenanceRequest);
    }

    @Override
    public MaintenanceRequest createNewRequest(Building building) {
        return MaintenanceYardiManager.instance().createNewRequest(building);
    }

    @Override
    public MaintenanceRequest createNewRequestForTenant(Tenant tenant) {
        return MaintenanceYardiManager.instance().createNewRequest(tenant);
    }

    @Override
    public void cancelMaintenanceRequest(MaintenanceRequest request) {
        MaintenanceYardiManager.instance().cancelMaintenanceRequest(request);
    }

    @Override
    public void rateMaintenanceRequest(MaintenanceRequest request, SurveyResponse rate) {
        MaintenanceYardiManager.instance().rateMaintenanceRequest(request, rate);
    }

    @Override
    public void sheduleMaintenanceRequest(MaintenanceRequest request, MaintenanceRequestSchedule schedule) {
        MaintenanceYardiManager.instance().sheduleMaintenanceRequest(request, schedule);
    }

    @Override
    public void resolveMaintenanceRequest(MaintenanceRequest request) {
        MaintenanceYardiManager.instance().resolveMaintenanceRequest(request);
    }

    @Override
    public void beforeItemRequest(Building building) {
        MaintenanceYardiManager.instance().beforeItemRequest(building);
    }

    @Override
    public void beforeListRequest() {
        MaintenanceYardiManager.instance().beforeListRequest();
    }

}
