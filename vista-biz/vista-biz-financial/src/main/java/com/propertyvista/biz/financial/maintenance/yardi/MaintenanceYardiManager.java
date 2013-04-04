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

import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceYardiManager {

    private static class SingletonHolder {
        public static final MaintenanceYardiManager INSTANCE = new MaintenanceYardiManager();
    }

    static MaintenanceYardiManager instance() {
        return SingletonHolder.INSTANCE;
    }

    protected void postMaintenanceRequest(MaintenanceRequest maintenanceRequest, Tenant tenant) {
        // TODO post Maintenance Request to Yardi
    }

    protected List<MaintenanceRequest> getClosedMaintenanceRequests(Tenant tenant) {
        // TODO get Maintenance Requests from Yardi, return ones that are NOT Open
        return null;
    }

    protected List<MaintenanceRequest> getOpenMaintenanceRequests(Tenant tenant) {
        // TODO get Maintenance Requests from Yardi, return ones that are Open
        return null;
    }

    public void cancelMaintenanceRequest(MaintenanceRequestDTO dto) {
        // TODO Change status of maintenance request in Yardi to Canceled by dto's id

    }

}
