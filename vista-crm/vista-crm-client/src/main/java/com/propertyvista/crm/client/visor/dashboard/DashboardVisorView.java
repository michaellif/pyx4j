/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.dashboard;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;

import com.propertyvista.crm.client.ui.dashboard.DashboardPanel;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class DashboardVisorView extends Composite {

    private final DashboardPanel dashboard;

    private final IDashboardVisorController controller;

    public DashboardVisorView(IDashboardVisorController controller) {
        this.controller = controller;
        this.dashboard = new DashboardPanel();
        this.dashboard.setPresenter(this.controller);

        initWidget(this.dashboard.asWidget());
    }

    public List<Building> getSelectedBuildingsStubs() {
        return this.dashboard.getSelectedBuildingsStubs();
    }

    public void populate(DashboardMetadata result) {
        this.dashboard.populate(result);
    }

    public DashboardMetadata getDashboardMetadata() {
        return this.dashboard.getDashboardMetadata();
    }

    public void setBuildings(List<Building> selectedBuildings) {
        this.dashboard.setBuildings(selectedBuildings, false);
    }

}
