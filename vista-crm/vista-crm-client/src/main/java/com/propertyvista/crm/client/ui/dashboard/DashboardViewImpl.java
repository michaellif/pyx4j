/*
 * (7C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;

import com.propertyvista.crm.client.ui.gadgets.common.IGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.AbstractDashboard;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.LayoutManagersFactory;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.property.asset.building.Building;

public class DashboardViewImpl extends AbstractPrimePane implements DashboardView {

    private static final I18n i18n = I18n.get(DashboardViewImpl.class);

    private final BuildingsSelectionToolbar buildingsFilterProvider;

    private final AbstractDashboard dashboard;

    private DashboardView.Presenter presenter;

    private final DockLayoutPanel panel;

    private boolean isReadOnly;

    public DashboardViewImpl() {
        this.isReadOnly = true;

        this.buildingsFilterProvider = new BuildingsSelectionToolbar(this);
        this.dashboard = new AbstractDashboard(//@formatter:off
                buildingsFilterProvider,
                GWT.<IGadgetFactory> create(IGadgetFactory.class),
                LayoutManagersFactory.createLayoutManagers()) {
            
            @Override
            protected void onDashboardMetadataChanged() {
                presenter.save();
            }
            
        };//@formatter:on

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
        dashboard.setVisible(dashboardMetadata != null);
        // this is awkward way to hide buildings bar because setVisible will not work properly in DockLayoutPanel
        panel.setWidgetSize(buildingsFilterProvider, (dashboardMetadata != null) && (dashboardMetadata.type().getValue() == DashboardType.building) ? 2.5 : 0.1);
        if (dashboardMetadata != null) {
            dashboard.setDashboardMetatdata(dashboardMetadata);
        }
        updateCaption();
    }

    @Override
    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
        this.dashboard.setReadOnly(isReadOnly);
        this.updateCaption();
    }

    @Override
    public DashboardMetadata getDashboardMetadata() {
        return dashboard.isVisible() ? dashboard.getDashboardMetadata() : null;
    }

    @Override
    public Vector<Building> getSelectedBuildingsStubs() {
        return new Vector<Building>(buildingsFilterProvider.getSelectedBuildingsStubs());
    }

    private void updateCaption() {
        String caption = "";
        if (getDashboardMetadata() != null) {
            caption = SimpleMessageFormat.format("{0} {1}", dashboard.getDashboardMetadata().name().getValue(), isReadOnly ? i18n.tr(" - Read only mode") : "");
        }
        setCaption(caption);
    }

}
