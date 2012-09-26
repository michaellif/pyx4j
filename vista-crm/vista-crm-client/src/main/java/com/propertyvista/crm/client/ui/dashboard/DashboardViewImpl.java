/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.dashboard;

import java.util.Vector;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.DockLayoutPanel;

import com.pyx4j.site.client.ui.ViewImplBase;
import com.pyx4j.site.client.ui.crud.misc.IMemento;

import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.AbstractDashboard;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.GadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.LayoutManagersFactory;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.property.asset.building.Building;

public class DashboardViewImpl extends ViewImplBase implements DashboardView {

    private final BuildingsSelectionToolbar buildingsFilterProvider;

    private final AbstractDashboard dashboard;

    private DashboardView.Presenter presenter;

    private final DockLayoutPanel panel;

    public DashboardViewImpl() {
        this.buildingsFilterProvider = new BuildingsSelectionToolbar();
        this.dashboard = new AbstractDashboard(buildingsFilterProvider, new GadgetFactory(), LayoutManagersFactory.createLayoutManagers()) {

            @Override
            protected void onPrintRequested() {
                presenter.print();
            }

            @Override
            protected void onDashboardMetadataChanged() {
                presenter.save();
            }
        };

        this.panel = new DockLayoutPanel(Unit.EM);
        this.panel.addNorth(buildingsFilterProvider, 2.5);
        this.panel.add(dashboard);
        setContentPane(panel);
        setSize("100%", "100%");
    }

    @Override
    public void setPresenter(DashboardView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setDashboardMetadata(DashboardMetadata dashboardMetadata) {
        if (dashboardMetadata != null) {
            panel.setWidgetSize(buildingsFilterProvider, dashboardMetadata.type().getValue() == DashboardType.building ? 2.5 : 0.1);
            dashboard.setDashboardMetatdata(dashboardMetadata);
            setCaption(dashboardMetadata.name().getValue());
        }
    }

    @Override
    public DashboardMetadata getDashboardMetadata() {
        return dashboard.getDashboardMetadata();
    }

    @Override
    public Vector<Building> getSelectedBuildingsStubs() {
        return new Vector<Building>(buildingsFilterProvider.getSelectedBuildingsStubs());
    }

    @Override
    public IMemento getMemento() {
        // FIXME shouldn't exist
        return null;
    }

    @Override
    public void storeState(Place place) {
        // FIXME shouldn't exist
    }

    @Override
    public void restoreState() {
        // FIXME shoudln't exist
    }

}
