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

public class Report extends SimplePanel implements IBoardRoot {

    public static enum Location {
        Full, Left, Right
    }

    private final ReportLayoutPanel reportLayoutPanel;

    private final PickupDragController gadgetDragController;

    private final AbsolutePanel boundaryPanel = new AbsolutePanel();

    private final HTML placeholder = new HTML("report_placeholder");

    private final List<DashboardEvent> handlers = new ArrayList<DashboardEvent>();

    public Report() {
        addStyleName(CSSNames.BASE_NAME);

        boundaryPanel.setSize("100%", "100%");
        setWidget(boundaryPanel);

        reportLayoutPanel = new ReportLayoutPanel(this);
        boundaryPanel.add(reportLayoutPanel);

        gadgetDragController = new PickupDragController(boundaryPanel, false);
        gadgetDragController.setBehaviorMultipleSelection(false);
        gadgetDragController.registerDropController(new ReportDropController(reportLayoutPanel));
    }

    public void addGadget(IGadget gadget, Report.Location location) {
        reportLayoutPanel.addGadget(new GadgetHolder(gadget, gadgetDragController, this), location);
        onEvent(Reason.addGadget);
    }

    public void insertGadget(IGadget gadget, Report.Location location, int beforeRow) {
        reportLayoutPanel.insertGadget(new GadgetHolder(gadget, gadgetDragController, this), location, beforeRow);
        onEvent(Reason.addGadget);
    }

    // Maximize Gadget mechanics:
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
            for (DashboardEvent handler : handlers) {
                handler.onEvent(reason);
            }
        }
    }

    public void addEventHandler(DashboardEvent handler) {
        handlers.add(handler);
    }

    public IGadgetIterator getGadgetIterator() {
        return reportLayoutPanel.getGadgetIterator();
    }
}
