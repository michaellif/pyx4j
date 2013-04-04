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
package com.propertyvista.biz.financial.maintenance.internal;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.tenant.lease.Tenant;

public class MaintenanceInternalManager {

    private static class SingletonHolder {
        public static final MaintenanceInternalManager INSTANCE = new MaintenanceInternalManager();
    }

    static MaintenanceInternalManager instance() {
        return SingletonHolder.INSTANCE;
    }

    protected void postMaintenanceRequest(MaintenanceRequest maintenanceRequest, Tenant tenant) {
        maintenanceRequest.leaseParticipant().set(tenant);
        Persistence.secureSave(maintenanceRequest);
    }

    protected List<MaintenanceRequest> getClosedMaintenanceRequests(Tenant tenant) {
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status(), MaintenanceRequestStatus.Resolved, MaintenanceRequestStatus.Cancelled));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), tenant));
        return Persistence.service().query(criteria.desc(criteria.proto().submitted()));
    }

    protected List<MaintenanceRequest> getOpenMaintenanceRequests(Tenant tenant) {
        EntityQueryCriteria<MaintenanceRequest> criteria = EntityQueryCriteria.create(MaintenanceRequest.class);
        criteria.add(PropertyCriterion.in(criteria.proto().status(), MaintenanceRequestStatus.Scheduled, MaintenanceRequestStatus.Submitted));
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant(), tenant));
        return null;
    }

}
