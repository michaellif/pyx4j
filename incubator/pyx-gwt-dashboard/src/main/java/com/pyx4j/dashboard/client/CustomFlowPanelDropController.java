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
 * Created on 2011-01-31
 * @author VladLL
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.dashboard.client;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.allen_sauer.gwt.dnd.client.util.Area;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Modification of Allen Sauer's FlowPanelDropController: I replaced
 * FlowPanelDropController::newPositioner (which was pretty basic (?!)) with the
 * functionality of his VerticalPanelDropController::newPositioner...
 */
public class CustomFlowPanelDropController extends FlowPanelDropController {

    /**
     * Label for IE quirks mode workaround.
     */
    private static final Label DUMMY_LABEL_IE_QUIRKS_MODE_OFFSET_HEIGHT = new Label("x");

    /**
     * @param dropTarget
     */
    public CustomFlowPanelDropController(FlowPanel dropTarget) {
        super(dropTarget);
    }

    @Override
    protected LocationWidgetComparator getLocationWidgetComparator() {
        return LocationWidgetComparator.BOTTOM_HALF_COMPARATOR;
        //        return new LocationWidgetComparator() {
        //            @Override
        //            public boolean locationIndicatesIndexFollowingWidget(Area widgetArea, Location location) {
        //                return (location.getTop() > widgetArea.getTop() || location.getTop() < widgetArea.getBottom());
        //            }
        //        };
    }

    @Override
    protected Widget newPositioner(DragContext context) {
        // Use two widgets so that setPixelSize() consistently affects dimensions
        // excluding positioner border in quirks and strict modes
        SimplePanel outer = new SimplePanel();
        outer.addStyleName(DragClientBundle.INSTANCE.css().positioner());

        // place off screen for border calculation
        RootPanel.get().add(outer, -500, -500);

        // Ensure IE quirks mode returns valid outer.offsetHeight, and thus valid
        // DOMUtil.getVerticalBorders(outer)
        outer.setWidget(DUMMY_LABEL_IE_QUIRKS_MODE_OFFSET_HEIGHT);

        int width = 0;
        int height = 0;
        for (Widget widget : context.selectedWidgets) {
            width = Math.max(width, widget.getOffsetWidth());
            height += widget.getOffsetHeight();
        }

        SimplePanel inner = new SimplePanel();
        inner.setPixelSize(width - DOMUtil.getHorizontalBorders(outer), height - DOMUtil.getVerticalBorders(outer));

        outer.setWidget(inner);
        return outer;
    }
}
