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

import java.sql.Time;
import java.util.List;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.tenant.lease.Tenant;

public class MaintenanceYardiFacadeImpl implements MaintenanceFacade {

    @Override
    public MaintenanceRequestMetadata getMaintenanceMetadata(boolean labelsOnly) {
        return MaintenanceMetadataYardiManager.instance().getMaintenanceMetadata(labelsOnly);
    }

    @Override
    public List<MaintenanceRequest> getOpenMaintenanceRequests(Tenant tenant) {
        return MaintenanceYardiManager.instance().getOpenMaintenanceRequests(tenant);
    }

    @Override
    public List<MaintenanceRequest> getClosedMaintenanceRequests(Tenant tenant) {
        return MaintenanceYardiManager.instance().getClosedMaintenanceRequests(tenant);
    }

    @Override
    public void postMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
        MaintenanceYardiManager.instance().postMaintenanceRequest(maintenanceRequest);
    }

    @Override
    public MaintenanceRequest getMaintenanceRequest(String requestId) {
        return MaintenanceYardiManager.instance().getMaintenanceRequest(requestId);
    }

    @Override
    public MaintenanceRequest createNewRequest(Tenant tenant) {
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
    public void sheduleMaintenanceRequest(MaintenanceRequest request, LogicalDate date, Time time) {
        MaintenanceYardiManager.instance().sheduleMaintenanceRequest(request, date, time);
    }

    @Override
    public void resolveMaintenanceRequest(MaintenanceRequest request) {
        MaintenanceYardiManager.instance().resolveMaintenanceRequest(request);
    }

    @Override
    public void beforeItemRequest() {
        MaintenanceYardiManager.instance().beforeItemRequest();
    }

    @Override
    public void beforeListRequest() {
        MaintenanceYardiManager.instance().beforeListRequest();
    }

}
