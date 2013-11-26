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
package com.propertyvista.biz.financial.maintenance;

import java.util.List;
import java.util.Set;

import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestSchedule;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Tenant;

/*
 * Open/Update/Close request
 * Get request
 */
public interface MaintenanceFacade {

    MaintenanceRequestMetadata getMaintenanceMetadata(Building building);

    List<MaintenanceRequest> getMaintenanceRequests(Set<StatusPhase> statuses, Tenant reporter);

    MaintenanceRequest createNewRequest(Building building);

    MaintenanceRequest createNewRequest(AptUnit unit);

    MaintenanceRequest createNewRequestForTenant(Tenant tenant);

    void postMaintenanceRequest(MaintenanceRequest request);

    void cancelMaintenanceRequest(MaintenanceRequest request);

    void rateMaintenanceRequest(MaintenanceRequest request, SurveyResponse rate);

    void sheduleMaintenanceRequest(MaintenanceRequest request, MaintenanceRequestSchedule schedule);

    void resolveMaintenanceRequest(MaintenanceRequest request);

    void beforeItemRequest(Building building);

    void beforeListRequest();
}
