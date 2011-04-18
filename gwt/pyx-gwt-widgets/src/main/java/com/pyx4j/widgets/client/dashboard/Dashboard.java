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
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class Dashboard extends SimplePanel {

    public Dashboard() {
        AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setSize("100%", "100%");
        setWidget(boundaryPanel);

        PickupDragController widgetDragController = new PickupDragController(boundaryPanel, false);
        widgetDragController.setBehaviorMultipleSelection(false);

        FlowPanel columnsContainerPanel = new FlowPanel();
        columnsContainerPanel.setWidth("100%");
        boundaryPanel.add(columnsContainerPanel);

        int count = 0;

        for (int col = 0; col < 3; col++) {
            FlowPanel columnCompositePanel = new FlowPanel();
            columnCompositePanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            columnCompositePanel.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            columnCompositePanel.setWidth(100 / 3 + "%");
            columnsContainerPanel.add(columnCompositePanel);

            // initialize a widget drop controller for the current column
            FlowPanelDropController widgetDropController = new FlowPanelDropController(columnCompositePanel);
            widgetDragController.registerDropController(widgetDropController);

            for (int row = 1; row <= 5; row++) {
                // initialize a widget
                HTML widget = new HTML("Draggable&nbsp;#" + ++count);
                widget.getElement().getStyle().setBackgroundColor("green");
                widget.getElement().getStyle().setMargin(1, Unit.PX);
                widget.setHeight(Random.nextInt(4) + 2 + "em");
                columnCompositePanel.add(widget);

                // make the widget draggable
                widgetDragController.makeDraggable(widget);
            }
        }

    }

}
