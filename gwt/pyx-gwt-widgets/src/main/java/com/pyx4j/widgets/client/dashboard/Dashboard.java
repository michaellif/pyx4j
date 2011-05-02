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

public class Dashboard extends SimplePanel {

    public enum LayoutType {
        One(1), Two11(2), Two12(2), Two21(2), Three(3);

        private final int columns;

        LayoutType(int columns) {
            this.columns = columns;
        }

        public int columns() {
            return columns;
        }
    }

    private final DashboardLayoutPanel dashboardLayoutPanel;

    private final PickupDragController gadgetDragController;

    public Dashboard() {
        addStyleName(CSSNames.BASE_NAME);

        AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setSize("100%", "100%");
        setWidget(boundaryPanel);

        gadgetDragController = new PickupDragController(boundaryPanel, false);
        gadgetDragController.setBehaviorMultipleSelection(false);

        dashboardLayoutPanel = new DashboardLayoutPanel(gadgetDragController);
        boundaryPanel.add(dashboardLayoutPanel);
    }

    public LayoutType getLayout() {
        return dashboardLayoutPanel.getLayout();
    }

    public boolean setLayout(LayoutType layoutType) {
        return dashboardLayoutPanel.setLayout(layoutType);
    }

    public void addGadget(IGadget gadget) {
        dashboardLayoutPanel.addGadget(new GadgetHolder(gadget, gadgetDragController, this));
    }

    public void addGadget(IGadget gadget, int column) {
        dashboardLayoutPanel.addGadget(new GadgetHolder(gadget, gadgetDragController, this), column);
    }

    public void insertGadget(IGadget gadget, int column, int row) {
        dashboardLayoutPanel.addGadget(new GadgetHolder(gadget, gadgetDragController, this), column, row);
    }
}
