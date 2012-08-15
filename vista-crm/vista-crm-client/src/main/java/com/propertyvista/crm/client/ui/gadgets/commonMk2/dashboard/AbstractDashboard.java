/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.NotImplementedException;
import com.pyx4j.widgets.client.dashboard.BoardEvent;
import com.pyx4j.widgets.client.dashboard.BoardLayout;
import com.pyx4j.widgets.client.dashboard.Dashboard;
import com.pyx4j.widgets.client.dashboard.IBoard;
import com.pyx4j.widgets.client.dashboard.IGadget;
import com.pyx4j.widgets.client.dashboard.IGadgetIterator;

import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEvent;
import com.propertyvista.crm.client.ui.board.events.BuildingSelectionChangedEventHandler;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetInstance;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public abstract class AbstractDashboard extends Composite {

    private final ScrollPanel dashboardPanel;

    private final IGadgetDirectory gadgetDirectory;

    private IBoard board;

    private DashboardMetadata dashboardMetadata;

    public AbstractDashboard(IGadgetDirectory gadgetDirectory) {
        this.dashboardPanel = new ScrollPanel();
        this.dashboardPanel.setSize("100%", "100%");
        this.gadgetDirectory = gadgetDirectory;
        initWidget(this.dashboardPanel);
    }

    public void setDashboardMetatdata(DashboardMetadata dashboardMetadata) {
        this.dashboardMetadata = dashboardMetadata;

        board = new Dashboard();

        if (dashboardMetadata != null) {
            board.setLayout(asBoardLayout(dashboardMetadata.layoutType().getValue()));

            Iterator<Integer> columnsIterator = getColumnsIterator();
            Iterator<GadgetMetadata> gadgetsIterator = dashboardMetadata.gadgets().iterator();

            while (gadgetsIterator.hasNext()) {
                GadgetMetadata gadgetMetadata = gadgetsIterator.next();
                IGadgetInstance gadget = gadgetDirectory.createGadgetInstance(gadgetMetadata);

                if (gadget != null) {
                    board.addGadget(gadget, columnsIterator.next());
                    // TODO change this dummy container for something real
                    gadget.setContainerBoard(new IBuildingFilterContainer() {

                        EventBus bus = new SimpleEventBus();

                        @Override
                        public HandlerRegistration addBuildingSelectionChangedEventHandler(BuildingSelectionChangedEventHandler handler) {
                            return bus.addHandler(BuildingSelectionChangedEvent.TYPE, handler);
                        }

                        @Override
                        public List<Building> getSelectedBuildingsStubs() {
                            return new ArrayList<Building>();
                        }

                        @Override
                        public List<Building> getSelectedBuildings() {
                            return new ArrayList<Building>();
                        }
                    });
                } else {
                    throw new Error("Gadget defined by " + gadgetMetadata.getEntityMeta().getCaption() + " is not defined in factory");
                }
            }
        } else {
            throw new Error("DashboardMetadata cannot be null");
        }

        board.addEventHandler(new BoardEvent() {
            @Override
            public void onEvent(final Reason reason) {
                // use a deferred command so that the actual event processing unlinked from event!
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        AbstractDashboard.this.proccessDashboardEvent(reason);
                    }
                });
            }
        });

        dashboardPanel.setWidget(board);

        IGadgetIterator it = board.getGadgetIterator();
        while (it.hasNext()) {
            it.next().start();
        }
    }

    public DashboardMetadata getDashboardMetadata() {
        return null;
    }

    protected abstract void onDashboardMetadataChanged();

    private Iterator<Integer> getColumnsIterator() {
//        final String[] columns = dashboardMetadata.encodedLayout().getValue().split(" ");
        final String[] columns = "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0".split(" ");
        return new Iterator<Integer>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < columns.length;
            }

            @Override
            public Integer next() {
                return Integer.parseInt(columns[i++]);
            }

            @Override
            public void remove() {
                throw new NotImplementedException();
            }
        };
    }

    private void proccessDashboardEvent(BoardEvent.Reason reason) {
        switch (reason) {
        case addGadget:
            break;
        case newLayout:
            dashboardMetadata.layoutType().setValue(asDashboardLayoutType(board.getLayout()));
            break;
        case removeGadget:
            break;
        case repositionGadget:
            break;
        case updateGadget:
            // gadget settings were changed: IMHO not supposed to affect the dashboard metadata and be managed internally by the gadget
            break;
        }
        updateEncodedLayout();

        onDashboardMetadataChanged();
    }

    /**
     * Synchronizes the encoded layout stored in dashboard metadata, with the actual layout
     */
    private void updateEncodedLayout() {
        if (dashboardMetadata != null) {
            dashboardMetadata.layoutType().setValue(asDashboardLayoutType(board.getLayout()));
            dashboardMetadata.gadgets().clear();

            IGadgetIterator it = board.getGadgetIterator();
            List<String> columns = new ArrayList<String>();
            while (it.hasNext()) {
                IGadget gadget = it.next();
                columns.add(String.valueOf(it.getColumn()));
                if (gadget instanceof IGadgetInstance) {
                    GadgetMetadata gmd = ((IGadgetInstance) gadget).getMetadata();
                    dashboardMetadata.gadgets().add(gmd);
                }
            }

            StringBuilder encodedLayoutBuilder = new StringBuilder();
            for (int i = 0; i < columns.size(); ++i) {
                encodedLayoutBuilder.append(columns.get(i));
                if (i != (columns.size() - 1)) {
                    encodedLayoutBuilder.append(" ");
                }
            }
            dashboardMetadata.encodedLayout().setValue(encodedLayoutBuilder.toString());
        }
    }

    private static BoardLayout asBoardLayout(LayoutType value) {
        BoardLayout layout = null;
        switch (value) {
        case One:
            layout = BoardLayout.One;
            break;
        case Two11:
            layout = BoardLayout.Two11;
            break;
        case Two12:
            layout = BoardLayout.Two12;
            break;
        case Two21:
            layout = BoardLayout.Two21;
            break;
        case Three:
            layout = BoardLayout.Three;
            break;
        }
        return layout;
    }

    private static LayoutType asDashboardLayoutType(BoardLayout layout) {
        LayoutType layoutType = null;
        switch (layout) {
        case One:
            layoutType = LayoutType.One;
            break;
        case Two11:
            layoutType = LayoutType.Two11;
            break;
        case Two12:
            layoutType = LayoutType.Two12;
            break;
        case Two21:
            layoutType = LayoutType.Two21;
            break;
        case Three:
            layoutType = LayoutType.Three;
            break;
        }
        return layoutType;
    }

}
