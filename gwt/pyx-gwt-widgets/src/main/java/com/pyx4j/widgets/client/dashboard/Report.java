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

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class Report extends SimplePanel {

    public static enum Location {
        Left, Right, Full
    }

    public static final int SPACING = 10;

    private final ReportLayoutPanel reportLayoutPanel;

    public Report() {
        AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setSize("100%", "100%");
        setWidget(boundaryPanel);

        PickupDragController widgetDragController = new PickupDragController(boundaryPanel, false);
        widgetDragController.setBehaviorMultipleSelection(false);
        widgetDragController.addDragHandler(new DragHandler() {

            @Override
            public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
            }

            @Override
            public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
            }

            @Override
            public void onDragStart(DragStartEvent event) {
                ((GadgetHolder) event.getSource()).setWidth(((GadgetHolder) event.getSource()).getOffsetWidth() + "px");
            }

            @Override
            public void onDragEnd(DragEndEvent event) {
                ((GadgetHolder) event.getSource()).setWidth("auto");
            }
        });

        int count = 0;

        reportLayoutPanel = new ReportLayoutPanel();
        boundaryPanel.add(reportLayoutPanel);

        // initialize a widget drop controller for the current column
        ReportDropController widgetDropController = new ReportDropController(reportLayoutPanel);
        widgetDragController.registerDropController(widgetDropController);

        for (int i = 0; i <= 15; i++) {
            GadgetHolder gadget = new GadgetHolder("Draggable&nbsp;#" + ++count, "green", "blue");
            if (i % 4 == 0) {
                reportLayoutPanel.addGadget(gadget, Report.Location.Full);
            } else if (i % 4 == 1) {
                reportLayoutPanel.addGadget(gadget, Report.Location.Left);
            } else if (i % 4 == 2 || i % 4 == 3) {
                reportLayoutPanel.addGadget(gadget, Report.Location.Right);
            }
            widgetDragController.makeDraggable(gadget, gadget.getDragHandler());
        }

    }

    public void addGadget(IGadget gadget, Report.Location location) {
        reportLayoutPanel.addGadget(gadget.asWidget(), Report.Location.Full);
    }

    public void insertGadget(IGadget gadget, Report.Location location, int beforeRow) {
        reportLayoutPanel.insertGadget(gadget.asWidget(), Report.Location.Full, beforeRow);
    }

}
