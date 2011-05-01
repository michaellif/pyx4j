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

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class Report extends SimplePanel {

    public static enum Location {
        Full, Left, Right, Any
    }

    private final ReportLayoutPanel reportLayoutPanel;

    private final PickupDragController gadgetDragController;

    public Report() {
        addStyleName(CSSNames.BASE_NAME);

        AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setSize("100%", "100%");
        setWidget(boundaryPanel);

        reportLayoutPanel = new ReportLayoutPanel();
        boundaryPanel.add(reportLayoutPanel);

        gadgetDragController = new PickupDragController(boundaryPanel, false);
        gadgetDragController.setBehaviorMultipleSelection(false);
        gadgetDragController.registerDropController(new ReportDropController(reportLayoutPanel));
    }

    public void addGadget(IGadget gadget, Report.Location location) {
        reportLayoutPanel.addGadget(new GadgetHolder(gadget, gadgetDragController, this), correctLocation(gadget, location));
    }

    public void insertGadget(IGadget gadget, Report.Location location, int beforeRow) {
        reportLayoutPanel.insertGadget(new GadgetHolder(gadget, gadgetDragController, this), correctLocation(gadget, location), beforeRow);
    }

    private Report.Location correctLocation(IGadget gadget, Report.Location location) {
        if (gadget.isFullWidth()) {
            location = Report.Location.Full;
        } else if (location == Report.Location.Full) {
            location = Report.Location.Any;
        }
        return location;
    }
}
