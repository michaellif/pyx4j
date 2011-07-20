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

public class Reportboard extends SimplePanel implements IBoard, IBoardRoot {

    public static enum Location {
        Full, Left, Right
    }

    private final ReportboardLayoutPanel reportLayoutPanel;

    private final PickupDragController gadgetDragController;

    private final AbsolutePanel boundaryPanel = new AbsolutePanel();

    private final HTML placeholder = new HTML("report_placeholder");

    private final List<BoardEvent> handlers = new ArrayList<BoardEvent>();

    public Reportboard() {
        addStyleName(CSSNames.BASE_NAME);

        boundaryPanel.setSize("100%", "100%");
        setWidget(boundaryPanel);

        reportLayoutPanel = new ReportboardLayoutPanel(this);
        boundaryPanel.add(reportLayoutPanel);

        gadgetDragController = new PickupDragController(boundaryPanel, false);
        gadgetDragController.setBehaviorMultipleSelection(false);
        gadgetDragController.registerDropController(new ReportboardDropController(reportLayoutPanel));
    }

    public void addGadget(IGadget gadget, Reportboard.Location location) {
        reportLayoutPanel.addGadget(new GadgetHolder(gadget, gadgetDragController, this), location);
        onEvent(Reason.addGadget);
    }

    public void insertGadget(IGadget gadget, Reportboard.Location location, int beforeRow) {
        reportLayoutPanel.insertGadget(new GadgetHolder(gadget, gadgetDragController, this), location, beforeRow);
        onEvent(Reason.addGadget);
    }

//
// Mimic IBoard implementation for report:
//
    @Override
    public BoardLayout getLayout() {
        return BoardLayout.Report; // always!..
    }

    @Override
    public boolean setLayout(BoardLayout layoutType) {
        return true; // has no meaning!..
    }

    @Override
    public void addGadget(IGadget gadget) {
        addGadget(gadget, Location.Full);
    }

    @Override
    public void addGadget(IGadget gadget, int column) {
        switch (column) {
        case 0:
            addGadget(gadget, Location.Left);
            break;
        case 1:
            addGadget(gadget, Location.Right);
            break;
        case -1:
            addGadget(gadget, Location.Full);
            break;
        }
    }

    @Override
    public void insertGadget(IGadget gadget, int column, int row) {
        switch (column) {
        case 0:
            insertGadget(gadget, Location.Left, row);
            break;
        case 1:
            insertGadget(gadget, Location.Right, row);
            break;
        case -1:
            insertGadget(gadget, Location.Full, row);
            break;
        }
    }

    @Override
    public void addEventHandler(BoardEvent handler) {
        handlers.add(handler);
    }

    @Override
    public IGadgetIterator getGadgetIterator() {
        return reportLayoutPanel.getGadgetIterator();
    }

// IBoardRoot:

    @Override
    public boolean showMaximized(Widget widget) {
        if (getWidget().equals(boundaryPanel)) {
            reportLayoutPanel.replaceGadget(widget, placeholder);
            setWidget(widget);
            return true;
        }
        return false;
    }

    @Override
    public boolean showNormal(Widget widget) {
        if (getWidget().equals(widget)) {
            setWidget(boundaryPanel);
            reportLayoutPanel.replaceGadget(placeholder, widget);
            return true;
        }
        return false;
    }

    @Override
    public boolean isMaximized(Widget widget) {
        return (getWidget().equals(widget));
    }

    @Override
    public void onEvent(Reason reason) {
        if (!handlers.isEmpty()) {
            for (BoardEvent handler : handlers) {
                handler.onEvent(reason);
            }
        }
    }
}
