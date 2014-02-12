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
package com.propertyvista.crm.client.ui.dashboard;

import java.util.List;
import java.util.Vector;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.AbstractDashboard;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.IBuildingFilterContainer;
import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.ICommonGadgetSettingsContainer;
import com.propertyvista.domain.property.asset.building.Building;

final class BuildingsSelectionToolbar extends Composite implements IBuildingFilterContainer, ICommonGadgetSettingsContainer {

    private static final I18n i18n = I18n.get(BuildingsSelectionToolbar.class);

    private final EventBus eventBus;

    private final FlowPanel bar;

    private final HTML buildingsView;

    private Vector<Building> buildings;

    private Button allBuilidngsButton;

    private Button chooseBuildingsButton;

    private final IPane parentView;

    public BuildingsSelectionToolbar(IPane parentView) {
        this.parentView = parentView;
        this.eventBus = new SimpleEventBus();
        this.buildings = new Vector<Building>();

        bar = new FlowPanel();
        bar.setStyleName(AbstractDashboard.DEFAULT_STYLE_PREFIX + AbstractDashboard.StyleSuffix.actionsPanel);
        bar.setSize("100%", "100%");

        buildingsView = new HTML("");
        buildingsView.setStyleName(AbstractDashboard.DEFAULT_STYLE_PREFIX + AbstractDashboard.StyleSuffix.filtersDescription);
        buildingsView.getElement().getStyle().setFloat(Float.LEFT);
        buildingsView.getElement().getStyle().setPaddingTop(5, Unit.PX);
        buildingsView.getElement().getStyle().setPaddingLeft(10, Unit.PX);

        bar.add(buildingsView);

        FlowPanel buttons = new FlowPanel();
        buttons.getElement().getStyle().setFloat(Float.RIGHT);
        buttons.getElement().getStyle().setPaddingTop(5, Unit.PX);
        buttons.getElement().getStyle().setPaddingRight(10, Unit.PX);

        buttons.add(chooseBuildingsButton = new Button("", new Command() {
            @Override
            public void execute() {
                selectBuildings();
            }
        }));
        buttons.add(allBuilidngsButton = new Button(i18n.tr("Reset"), new Command() {
            @Override
            public void execute() {
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
        return stubs;
    }

    @Override
    public void bindGadget(IGadgetInstance gadget) {
        gadget.setContainerBoard(this);
    }

    private void selectBuildings() {
        new BuildingSelectorDialog(parentView, buildings) {
            @Override
            public void onClickOk() {
                updateSelection(getSelectedItems());
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