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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;

import com.propertyvista.crm.client.ui.gadgets.common.Directory;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.AbstractDashboard;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IGadgetDirectory;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class DashboardVisorView extends Composite {

//    private final DashboardPanel dashboard;

    private final IDashboardVisorController controller;

    private final AbstractDashboard bb;

    public DashboardVisorView(IDashboardVisorController controller) {
        this.controller = controller;
        this.bb = new AbstractDashboard(new IGadgetDirectory() {

            @Override
            public IGadgetInstance createGadgetInstance(GadgetMetadata gmd) {
                return Directory.createGadget(gmd);
            }
        }) {

            @Override
            protected void onDashboardMetadataChanged() {
                DashboardVisorView.this.controller.save();
            }
        };

        initWidget(this.bb);
    }

    public List<Building> getSelectedBuildingsStubs() {
        return new ArrayList<Building>();
    }

    public void populate(DashboardMetadata result) {
        this.bb.setDashboardMetatdata(result);
    }

    public DashboardMetadata getDashboardMetadata() {
        return this.bb.getDashboardMetadata();
    }

    public void setBuildings(List<Building> selectedBuildings) {
        //this.bb.setBuildings(selectedBuildings, true);
    }

}
