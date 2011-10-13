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

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.components.AnchorButton;
import com.propertyvista.crm.client.ui.crud.building.SelectedBuildingLister;
import com.propertyvista.crm.client.ui.decorations.CrmTitleBar;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.property.asset.building.Building;

public class CrmBoardViewImpl extends BoardViewImpl implements CrmBoardView {

    protected static I18n i18n = I18n.get(CrmBoardViewImpl.class);

    protected final CrmTitleBar header = new CrmTitleBar("");

    protected final SimplePanel filtersPanel = new SimplePanel();

    private BuildingFilters filters;

    public CrmBoardViewImpl() {
        super();
    }

    public CrmBoardViewImpl(BoardBase board) {
        this();

        addNorth(header, VistaCrmTheme.defaultHeaderHeight);
        header.setHeight("100%"); // fill all that defaultHeaderHeight!..

        addNorth(filtersPanel, 0);
        filtersPanel.setStyleName(BoardBase.DEFAULT_STYLE_PREFIX + BoardBase.StyleSuffix.filtersPanel);

        setBoard(board);
    }

    @Override
    public void fill(DashboardMetadata dashboardMetadata) {
        super.fill(dashboardMetadata);

        filters = null;
        filtersPanel.setWidget(null);
        setWidgetSize(filtersPanel, 0);
        if (dashboardMetadata != null) {
            header.setCaption(dashboardMetadata.name().getStringView());

            if (dashboardMetadata.type().getValue() == DashboardType.building) {
                filters = new BuildingFilters();
                filtersPanel.setWidget(filters.getCompactVeiw());
                setWidgetSize(filtersPanel, VistaCrmTheme.defaultActionBarHeight);
            }
        }
    }

    @Override
    public IListerView<Building> getBuildingListerView() {
        return (filters != null ? filters.getBuildingListerView() : null);
    }

    private class BuildingFilters {

        private final IListerView<Building> buildingLister;

        public BuildingFilters() {
            buildingLister = new ListerInternalViewImplBase<Building>(new SelectedBuildingLister());
            buildingLister.getLister().setMultiSelect(true);
        }

        public IListerView<Building> getBuildingListerView() {
            return buildingLister;
        }

        public Widget getCompactVeiw() {
            HorizontalPanel main = new HorizontalPanel();

            HTML description = new HTML(getFilteringDescription());
            description.setStyleName(BoardBase.DEFAULT_STYLE_PREFIX + BoardBase.StyleSuffix.filtersDescription);
            main.add(description);
            main.setCellVerticalAlignment(description, HasVerticalAlignment.ALIGN_MIDDLE);
            main.setCellHorizontalAlignment(description, HasHorizontalAlignment.ALIGN_CENTER);

            Button setup = new Button("Setup", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setWidgetSize(filtersPanel, 30);
                    filtersPanel.setWidget(filters.getSetupVeiw());
                }
            });
            main.add(setup);
            main.setCellWidth(setup, "1%"); // resize it to buttons width!..
            main.setCellVerticalAlignment(setup, HasVerticalAlignment.ALIGN_MIDDLE);
            main.setSpacing(4);

            main.setSize("100%", "100%");
            return main;
        }

        public Widget getSetupVeiw() {
            VerticalPanel main = new VerticalPanel();

            Widget w;
            main.add(w = new ScrollPanel(buildingLister.asWidget()));

            HorizontalPanel buttons = new HorizontalPanel();

            Button apply = new Button("Apply", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {

                    // TODO retrieve and apply filters here...

                    setWidgetSize(filtersPanel, VistaCrmTheme.defaultActionBarHeight);
                    filtersPanel.setWidget(filters.getCompactVeiw());
                }
            });
            buttons.add(apply);

            AnchorButton cancel = new AnchorButton(i18n.tr("Cancel"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setWidgetSize(filtersPanel, VistaCrmTheme.defaultActionBarHeight);
                    filtersPanel.setWidget(filters.getCompactVeiw());
                }
            });
            buttons.add(cancel);
            buttons.setCellWidth(cancel, "60px");
            buttons.setCellHorizontalAlignment(cancel, HasHorizontalAlignment.ALIGN_CENTER);
            buttons.setCellVerticalAlignment(cancel, HasVerticalAlignment.ALIGN_MIDDLE);
            buttons.setSpacing(5);

            main.add(buttons);
            main.setCellHeight(buttons, "1%"); // resize it to buttons height!..
            buttons.setCellVerticalAlignment(buttons, HasVerticalAlignment.ALIGN_MIDDLE);
            main.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_RIGHT);

            main.setSize("100%", "100%");
            return main;
        }

        private String getFilteringDescription() {
            String filterDescription = i18n.tr("Data for Buildings : ");

            List<Building> selectedBuildings = buildingLister.getLister().getSelectedItems();
            if (!selectedBuildings.isEmpty()) {
                for (Building building : selectedBuildings) {
                    filterDescription += building.propertyCode().getStringView();
                    filterDescription += "; ";
                }
            } else {
                filterDescription = i18n.tr("Data for all Buildings");
            }

            return filterDescription;
        }

    }
}
