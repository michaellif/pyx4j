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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.ViewImplBase;
import com.pyx4j.site.client.ui.crud.misc.IMemento;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dashboard.BoardLayout;

import com.propertyvista.crm.client.resources.CrmDashboardResources;
import com.propertyvista.crm.client.ui.board.BoardBase;
import com.propertyvista.crm.client.ui.board.BoardBase.StyleSuffix;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
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

public class DashboardViewImpl extends ViewImplBase implements DashboardView {

    private static final class BuildingsSelectionToolbar extends Composite implements IBuildingFilterContainer, ICommonGadgetSettingsContainer {

        private static final I18n i18n = I18n.get(DashboardViewImpl.BuildingsSelectionToolbar.class);

        private final EventBus eventBus;

        private final FlowPanel bar;

        private final HTML buildingsView;

        private Vector<Building> buildings;

        private Button allBuilidngsButton;

        private Button chooseBuildingsButton;

        public BuildingsSelectionToolbar() {
            this.eventBus = new SimpleEventBus();
            this.buildings = new Vector<Building>();

            bar = new FlowPanel();
            bar.setStyleName(BoardBase.DEFAULT_STYLE_PREFIX + StyleSuffix.actionsPanel);
            bar.setSize("100%", "100%");

            buildingsView = new HTML("");
            buildingsView.setStyleName(BoardBase.DEFAULT_STYLE_PREFIX + BoardBase.StyleSuffix.filtersDescription);
            buildingsView.getElement().getStyle().setFloat(Float.LEFT);
            buildingsView.getElement().getStyle().setPaddingTop(5, Unit.PX);
            buildingsView.getElement().getStyle().setPaddingLeft(10, Unit.PX);

            bar.add(buildingsView);

            FlowPanel buttons = new FlowPanel();
            buttons.getElement().getStyle().setFloat(Float.RIGHT);
            buttons.getElement().getStyle().setPaddingTop(5, Unit.PX);
            buttons.getElement().getStyle().setPaddingRight(10, Unit.PX);

            buttons.add(chooseBuildingsButton = new Button("", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    selectBuildings();
                }
            }));
            buttons.add(allBuilidngsButton = new Button(i18n.tr("Reset"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    selectAllBuildings();
                }
            }));
            for (Widget w : buttons) {
                w.getElement().getStyle().setFloat(Float.LEFT);
                w.getElement().getStyle().setMarginLeft(10, Unit.PX);
            }
            bar.add(buttons);

            this.initWidget(bar);
            updateSelection(new Vector<Building>());
        }

        @Override
        public HandlerRegistration addBuildingSelectionChangedEventHandler(BuildingSelectionChangedEventHandler handler) {
            return eventBus.addHandler(BuildingSelectionChangedEvent.TYPE, handler);
        }

        @Override
        public List<Building> getSelectedBuildingsStubs() {
            Vector<Building> stubs = new Vector<Building>();
            for (Building building : buildings) {
                stubs.add(building.<Building> createIdentityStub());
            }
            return new Vector<Building>(buildings);
        }

        @Override
        public void bindGadget(IGadgetInstance gadget) {
            gadget.setContainerBoard(this);
        }

        private void selectBuildings() {
            new BuildingSelectorDialog(true, buildings) {
                @Override
                public boolean onClickOk() {
                    updateSelection(getSelectedItems());
                    return true;
                }

            }.show();
        }

        private void selectAllBuildings() {
            buildings = new Vector<Building>();
            updateSelection(new Vector<Building>());
        }

        private void updateSelection(List<Building> addedBuildings) {
            // update selected buildings
            Vector<Building> updatedFilter = new Vector<Building>();
            updatedFilter = new Vector<Building>(getSelectedBuildingsStubs().size() + addedBuildings.size());
            updatedFilter.addAll(buildings);
            updatedFilter.addAll(addedBuildings);
            buildings = updatedFilter;

            // update buildings string view;
            SafeHtmlBuilder buildingsLabel = new SafeHtmlBuilder().appendEscaped(i18n.tr("Buildings: "));
            if (buildings.isEmpty()) {
                buildingsLabel.appendEscaped(i18n.tr("All"));
            } else {
                new SafeHtmlBuilder();
                for (int i = 0; i < buildings.size(); ++i) {
                    buildingsLabel.appendEscaped(buildings.get(i).propertyCode().getValue());
                    if (i < buildings.size() - 1) {
                        buildingsLabel.appendEscaped(", ");
                    }
                }
            }
            buildingsView.setHTML(buildingsLabel.toSafeHtml());

            allBuilidngsButton.setVisible(!buildings.isEmpty());
            chooseBuildingsButton.setCaption(!buildings.isEmpty() ? i18n.tr("Add...") : i18n.tr("Filter..."));

            // notify interested entities
            eventBus.fireEvent(new BuildingSelectionChangedEvent(getSelectedBuildingsStubs()));
        }
    }

    private final BuildingsSelectionToolbar buildingsFilterProvider;

    private final AbstractDashboard dashboard;

    private DashboardView.Presenter presenter;

    private final DockLayoutPanel panel;

    public DashboardViewImpl() {

        this.buildingsFilterProvider = new BuildingsSelectionToolbar();
        List<ILayoutManager> layoutManagers = Arrays.<ILayoutManager> asList(//@formatter:off
                new DashboardLayoutManager(LayoutType.One, BoardLayout.One, CrmDashboardResources.Layout1ColumnResources.INSTANCE),                
                new DashboardLayoutManager(LayoutType.Two11, BoardLayout.Two11, CrmDashboardResources.Layout22ColumnResources.INSTANCE),
                new DashboardLayoutManager(LayoutType.Two12, BoardLayout.Two12, CrmDashboardResources.Layout12ColumnResources.INSTANCE),
                new DashboardLayoutManager(LayoutType.Two21, BoardLayout.Two21, CrmDashboardResources.Layout21ColumnResources.INSTANCE),                
                new DashboardLayoutManager(LayoutType.Three, BoardLayout.Three, CrmDashboardResources.Layout3ColumnResources.INSTANCE)
        );//@formatter:on

        dashboard = new AbstractDashboard(buildingsFilterProvider, new BuildingGadgetDirectory(), layoutManagers) {

            @Override
            protected void onPrintRequested() {
                presenter.print();
            }

            @Override
            protected void onDashboardMetadataChanged() {
                presenter.save();
            }
        };
        panel = new DockLayoutPanel(Unit.EM);
        panel.addNorth(buildingsFilterProvider, 2.5);
        panel.add(dashboard);
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
