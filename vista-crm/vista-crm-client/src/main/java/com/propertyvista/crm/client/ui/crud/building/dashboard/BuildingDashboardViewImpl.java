/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.dashboard;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.dashboard.DashboardPanel;
import com.propertyvista.crm.rpc.VistaCrmDebugId;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.property.asset.building.Building;

public class BuildingDashboardViewImpl implements BuildingDashboardView {

    private final DashboardPanel dashboard;

    private final CEntityComboBox<DashboardMetadata> dashboardSelector = new CEntityComboBox<DashboardMetadata>(DashboardMetadata.class);

    private com.propertyvista.crm.client.ui.board.BoardView.Presenter presenter;

    public BuildingDashboardViewImpl() {

        dashboard = new DashboardPanel();
        dashboard.setSize("100%", "100%");

        dashboardSelector.setDebugIdSuffix(VistaCrmDebugId.BuildingDashboardSelector);
        dashboardSelector.setWidth("25em");
        dashboardSelector.addCriterion(PropertyCriterion.eq(dashboardSelector.proto().type(), DashboardType.building));
        dashboardSelector.addValueChangeHandler(new ValueChangeHandler<DashboardMetadata>() {
            @Override
            public void onValueChange(ValueChangeEvent<DashboardMetadata> event) {
                if (event.getValue() != null) {
                    presenter.populate(event.getValue().getPrimaryKey());
                } else {
                    dashboard.populate(null);
                }
            }
        });

        dashboardSelector.asWidget().getElement().getStyle().setMarginLeft(0.5, Unit.EM);
        dashboardSelector.asWidget().getElement().getStyle().setMarginRight(3, Unit.EM);
        dashboard.addAction(dashboardSelector.asWidget());
    }

    @Override
    public void populate(DashboardMetadata metadata) {
        dashboard.populate(metadata);
    }

    @Override
    public void setPresenter(com.propertyvista.crm.client.ui.board.BoardView.Presenter presenter) {
        dashboard.setPresenter(presenter);
        this.presenter = presenter;
    }

    @Override
    public HandlerRegistration addBuildingSelectionChangedEventHandler(BuildingSelectionChangedEventHandler handler) {
        return dashboard.addBuildingSelectionChangedEventHandler(handler);
    }

    @Override
    public void setBuildings(List<Building> buildings, boolean fireEvent) {
        dashboard.setBuildings(buildings, fireEvent);
    }

    @Override
    public void stop() {
        dashboard.stop();
    }

    @Override
    public DashboardMetadata getDashboardMetadata() {
        return dashboard.getDashboardMetadata();
    }

    @Override
    public void onSaveSuccess() {
        dashboard.onSaveSuccess();
    }

    @Override
    public boolean onSaveFail(Throwable caught) {
        return dashboard.onSaveFail(caught);
    }

    @Override
    public Widget asWidget() {
        return dashboard;
    }

    @Override
    public LogicalDate getDashboardDate() {
        return dashboard.getDashboardDate();
    }

    @Override
    public List<Building> getSelectedBuildingsStubs() {
        return dashboard.getSelectedBuildingsStubs();
    }

    @Override
    public EventBus getEventBus() {
        return dashboard.getEventBus();
    }

    @Override
    public List<Building> getSelectedBuildings() {
        return dashboard.getSelectedBuildings();
    }

}
