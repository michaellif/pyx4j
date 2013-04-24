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
package com.propertyvista.biz.financial.maintenance.internal;

import java.sql.Time;
import java.util.List;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.tenant.lease.Tenant;

public class MaintenanceInternalFacadeImpl implements MaintenanceFacade {

    @Override
    public MaintenanceRequestMetadata getMaintenanceMetadata(boolean levelsOnly) {
        return MaintenanceMetadataInternalManager.instance().getMaintenanceMetadata(levelsOnly);
    }

    @Override
    public List<MaintenanceRequest> getOpenMaintenanceRequests(Tenant tenant) {
        return MaintenanceInternalManager.instance().getOpenMaintenanceRequests(tenant);
    }

    @Override
    public List<MaintenanceRequest> getClosedMaintenanceRequests(Tenant tenant) {
        return MaintenanceInternalManager.instance().getClosedMaintenanceRequests(tenant);
    }

    @Override
    public void postMaintenanceRequest(MaintenanceRequest request, Tenant tenant) {
        MaintenanceInternalManager.instance().postMaintenanceRequest(request, tenant);
    }

    @Override
    public void cancelMaintenanceRequest(MaintenanceRequest request) {
        MaintenanceInternalManager.instance().cancelMaintenanceRequest(request);
    }

    @Override
    public void rateMaintenanceRequest(MaintenanceRequest request, SurveyResponse rate) {
        MaintenanceInternalManager.instance().rateMaintenanceRequest(request, rate);
    }

    @Override
    public MaintenanceRequest getMaintenanceRequest(String requestId) {
        return MaintenanceInternalManager.instance().getMaintenanceRequest(requestId);
    }

    @Override
    public MaintenanceRequest createNewRequest(Tenant tenant) {
        return MaintenanceInternalManager.instance().createNewRequest(tenant);
    }

    @Override
    public void sheduleMaintenanceRequest(MaintenanceRequest request, LogicalDate date, Time time) {
        MaintenanceInternalManager.instance().sheduleMaintenanceRequest(request, date, time);
    }

    @Override
    public void resolveMaintenanceRequest(MaintenanceRequest request) {
        MaintenanceInternalManager.instance().resolveMaintenanceRequest(request);
    }

    @Override
    public void beforeItemRequest() {
        // N/A - do nothing
    }

    @Override
    public void beforeListRequest() {
        // N/A - do nothing
    }
}
