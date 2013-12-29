/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.site.client.ReportDialog;

import com.propertyvista.crm.rpc.services.reports.DashboardReportService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class DashboardPrinterDialog {

    public static void print(DashboardMetadata dashboardStub, List<Building> buildingsFilterStubs) {
        print(dashboardStub.getPrimaryKey(), buildingsFilterStubs);
    }

    /**
     * Prints a dashboard and displays progress bar with "cancel" option
     */
    public static void print(Key dashboardId, List<Building> buildingsFilterStubs) {
        EntityQueryCriteria<DashboardMetadata> criteria = new EntityQueryCriteria<DashboardMetadata>(DashboardMetadata.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), dashboardId));
        HashMap<String, Serializable> parameters = new HashMap<String, Serializable>();
        Vector<Key> buildingsPks = new Vector<Key>(buildingsFilterStubs.size());

        for (Building b : buildingsFilterStubs) {
            buildingsPks.add(b.getPrimaryKey());
        }

        parameters.put(DashboardReportService.PARAM_SELECTED_BUILDINGS, buildingsPks);
        new ReportDialog("", "Creating dashboard printout...").start(GWT.<DashboardReportService> create(DashboardReportService.class), criteria, parameters);

    }
}
