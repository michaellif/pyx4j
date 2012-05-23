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
package com.propertyvista.crm.client.ui.board;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.board.BoardBase.StyleSuffix;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.property.asset.building.Building;

public class CrmBoardViewImpl extends BoardViewImpl implements CrmBoardView {

    private final static I18n i18n = I18n.get(CrmBoardViewImpl.class);

    private final BuildingsBar buildingsBar;

    private final static int BUILDINGS_BAR_HEIGHT = 30;

    public CrmBoardViewImpl(BoardBase board) {
        buildingsBar = new BuildingsBar();
        buildingsBar.asWidget().getElement().setId("buildingsbar");
        addNorth(buildingsBar, 0.1);
        setBoard(board);
    }

    @Override
    public void populate(DashboardMetadata dashboardMetadata) {
        super.populate(dashboardMetadata);
        setCaption(asBoardCaption(dashboardMetadata));
        if (dashboardMetadata != null) {
            boolean isBuildingDashboard = dashboardMetadata.type().getValue() == DashboardType.building;
            this.setWidgetSize(buildingsBar.asWidget(), isBuildingDashboard ? BUILDINGS_BAR_HEIGHT : 0.1); // for some reason setVisible() doesn't work here
        }
    }

    @Override
    public void setBuildings(List<Building> buildings, boolean fireEvents) {
        super.setBuildings(buildings, fireEvents);
        updateBuildingsBar(buildings);
    }

    private void updateBuildingsBar(List<Building> buildings) {
        buildingsBar.setBuildingsStringView(asStringView(buildings));
    }

    protected String asStringView(List<Building> buildings) {
        String stringView;
        if (buildings.isEmpty()) {
            stringView = i18n.tr("Status for All Buildings");
        } else {
            StringBuilder stringViewBuilder = new StringBuilder();
            int last = buildings.size() - 1;
            for (int i = 0; i < last; i++) {
                stringViewBuilder.append(buildings.get(i).propertyCode().getValue()).append(", ");
            }
            stringViewBuilder.append(buildings.get(last).propertyCode().getValue());
            stringView = stringViewBuilder.toString();
        }
        return stringView;
    }

    /**
     * @param dashboardMetadata
     *            <code>null<code> must be handled in appropriate way.
     * @return
     */
    protected String asBoardCaption(DashboardMetadata dashboardMetadata) {
        if (dashboardMetadata == null) {
            return i18n.tr("Please create a dashboard via Dashboards/Manage Dashboards");
        } else {
            return dashboardMetadata.name().getValue();
        }
    }

    protected class BuildingsBar implements IsWidget {

        private final HorizontalPanel bar;

        final HTML buildingsStringView;

        public BuildingsBar() {

            bar = new HorizontalPanel();
            bar.setStyleName(BoardBase.DEFAULT_STYLE_PREFIX + StyleSuffix.actionsPanel);
            bar.setWidth("100%");
            bar.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
            bar.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

            buildingsStringView = new HTML("");
            buildingsStringView.setStyleName(BoardBase.DEFAULT_STYLE_PREFIX + BoardBase.StyleSuffix.filtersDescription);

            HorizontalPanel buttons = new HorizontalPanel();
            buttons.setSpacing(5);
            buttons.add(new Button(i18n.tr("Clear Selected"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    selectAllBuildings();
                }
            }));

            buttons.add(new Button(i18n.tr("Select Buildings..."), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    selectBuildings();
                }
            }));
            bar.add(buildingsStringView);
            bar.add(buttons);
        }

        protected void selectAllBuildings() {
            CrmBoardViewImpl.this.setBuildings(new ArrayList<Building>(1), true);
        }

        protected void selectBuildings() {
            new BuildingSelectorDialog(true, getSelectedBuildings()) {
                @Override
                public boolean onClickOk() {
                    ArrayList<Building> selected = new ArrayList<Building>(getSelectedBuildingsStubs().size() + getSelectedItems().size());
                    selected.addAll(getSelectedBuildings());
                    selected.addAll(getSelectedItems());
                    CrmBoardViewImpl.this.setBuildings(selected, true);
                    return true;
                }

            }.show();
        }

        public void setBuildingsStringView(String stringView) {
            buildingsStringView.setHTML(new SafeHtmlBuilder().appendEscaped(stringView).toSafeHtml());
        }

        @Override
        public Widget asWidget() {
            return bar;
        }

    }

}
