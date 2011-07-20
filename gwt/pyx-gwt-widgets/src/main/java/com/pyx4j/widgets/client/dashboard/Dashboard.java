/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Apr 18, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Dashboard extends SimplePanel implements IBoard, IBoardRoot {

    private final DashboardLayoutPanel dashboardLayoutPanel;

    private final PickupDragController gadgetDragController;

    private final AbsolutePanel boundaryPanel = new AbsolutePanel();

    private final HTML placeholder = new HTML("dashboard_placeholder");

    private final List<BoardEvent> handlers = new ArrayList<BoardEvent>();

    private boolean inhibitEvents = false;

    public Dashboard() {
        addStyleName(CSSNames.BASE_NAME);

        boundaryPanel.setSize("100%", "100%");
        setWidget(boundaryPanel);

        gadgetDragController = new PickupDragController(boundaryPanel, false);
        gadgetDragController.setBehaviorMultipleSelection(false);

        dashboardLayoutPanel = new DashboardLayoutPanel(gadgetDragController, this);
        boundaryPanel.add(dashboardLayoutPanel);
    }

// IBoard:

    @Override
    public BoardLayout getLayout() {
        return dashboardLayoutPanel.getLayout();
    }

    @Override
    public boolean setLayout(BoardLayout layoutType) {
        return dashboardLayoutPanel.setLayout(layoutType);
    }

    @Override
    public void addGadget(IGadget gadget) {
        dashboardLayoutPanel.addGadget(new GadgetHolder(gadget, gadgetDragController, this));
    }

    @Override
    public void addGadget(IGadget gadget, int column) {
        dashboardLayoutPanel.addGadget(new GadgetHolder(gadget, gadgetDragController, this), column);
    }

    @Override
    public void insertGadget(IGadget gadget, int column, int row) {
        dashboardLayoutPanel.addGadget(new GadgetHolder(gadget, gadgetDragController, this), column, row);
    }

    @Override
    public void addEventHandler(BoardEvent handler) {
        handlers.add(handler);
    }

    @Override
    public IGadgetIterator getGadgetIterator() {
        return dashboardLayoutPanel.getGadgetIterator();
    }

// IBoardRoot:

    @Override
    public boolean showMaximized(Widget widget) {
        if (getWidget().equals(boundaryPanel)) {
            DashboardLayoutPanel.Location loc = new DashboardLayoutPanel.Location();
            if (dashboardLayoutPanel.getWidgetLocation(widget, loc)) {
                setWidget(widget);
                inhibitEvents = true;
                dashboardLayoutPanel.addGadget(placeholder, loc.col, loc.row);
                inhibitEvents = false;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean showNormal(Widget widget) {
        if (getWidget().equals(widget)) {
            DashboardLayoutPanel.Location loc = new DashboardLayoutPanel.Location();
            if (dashboardLayoutPanel.getWidgetLocation(placeholder, loc)) {
                inhibitEvents = true;
                dashboardLayoutPanel.removeGadget(loc.col, loc.row);
                dashboardLayoutPanel.addGadget(widget, loc.col, loc.row);
                setWidget(boundaryPanel);
                inhibitEvents = false;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMaximized(Widget widget) {
        return (getWidget().equals(widget));
    }

    @Override
    public void onEvent(Reason reason) {
        if (!inhibitEvents && !handlers.isEmpty()) {
            for (BoardEvent handler : handlers) {
                handler.onEvent(reason);
            }
        }
    }
}
