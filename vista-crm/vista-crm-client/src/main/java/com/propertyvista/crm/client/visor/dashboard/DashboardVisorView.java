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
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.widgets.client.dashboard.BoardLayout;

import com.propertyvista.crm.client.resources.CrmDashboardResources;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.AbstractDashboard;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.BuildingGadgetDirectory;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.DashboardLayoutManager;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.ICommonGadgetSettingsContainer;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.ILayoutManager;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.property.asset.building.Building;

public class DashboardVisorView extends Composite {

    private final IDashboardVisorController controller;

    private final AbstractDashboard dashboard;

    private final EventBus eventBus;

    private List<Building> buildingsFilter;

    public DashboardVisorView(IDashboardVisorController controller) {
        this.controller = controller;

        this.buildingsFilter = new ArrayList<Building>();
        this.eventBus = new SimpleEventBus();

        ICommonGadgetSettingsContainer commonGadgetSettingsContainer = new ICommonGadgetSettingsContainer() {

            @Override
            public void bindGadget(IGadgetInstance gadget) {
                gadget.setContainerBoard(new IBuildingFilterContainer() {

                    @Override
                    public HandlerRegistration addBuildingSelectionChangedEventHandler(BuildingSelectionChangedEventHandler handler) {
                        return eventBus.addHandler(BuildingSelectionChangedEvent.TYPE, handler);
                    }

                    @Override
                    public List<Building> getSelectedBuildingsStubs() {
                        return buildingsFilter;
                    }

                });
            }
        };
        List<ILayoutManager> layoutManagers = Arrays.<ILayoutManager> asList(//@formatter:off
                new DashboardLayoutManager(LayoutType.One, BoardLayout.One, CrmDashboardResources.Layout1ColumnResources.INSTANCE),                
                new DashboardLayoutManager(LayoutType.Two11, BoardLayout.Two11, CrmDashboardResources.Layout22ColumnResources.INSTANCE),
                new DashboardLayoutManager(LayoutType.Two12, BoardLayout.Two12, CrmDashboardResources.Layout12ColumnResources.INSTANCE),
                new DashboardLayoutManager(LayoutType.Two21, BoardLayout.Two21, CrmDashboardResources.Layout21ColumnResources.INSTANCE),                
                new DashboardLayoutManager(LayoutType.Three, BoardLayout.Three, CrmDashboardResources.Layout3ColumnResources.INSTANCE)
        );//@formatter:on

        this.dashboard = new AbstractDashboard(commonGadgetSettingsContainer, new BuildingGadgetDirectory(), layoutManagers) {

            @Override
            protected void onDashboardMetadataChanged() {
                DashboardVisorView.this.controller.saveDashboardMetadata();
            }

            @Override
            protected void onPrintRequested() {
                DashboardVisorView.this.controller.print();
            }
        };

        initWidget(this.dashboard);

    }

    public void populate(DashboardMetadata result) {
        this.dashboard.setDashboardMetatdata(result);
    }

    public DashboardMetadata getDashboardMetadata() {
        return this.dashboard.getDashboardMetadata();
    }

    public List<Building> getSelectedBuildingsStubs() {
        return new ArrayList<Building>(buildingsFilter);
    }

    public void setBuildings(List<Building> selectedBuildings) {
        buildingsFilter = new ArrayList<Building>(selectedBuildings);
        eventBus.fireEvent(new BuildingSelectionChangedEvent(buildingsFilter));
    }

}
