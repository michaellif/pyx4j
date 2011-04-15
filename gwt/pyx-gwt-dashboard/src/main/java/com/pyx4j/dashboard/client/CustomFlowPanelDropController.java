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
 * @version $Id$
 */
package com.pyx4j.dashboard.client;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Modification of Allen Sauer's FlowPanelDropController: I replaced
 * FlowPanelDropController::newPositioner (which was pretty basic (?!)) with the
 * functionality of his VerticalPanelDropController::newPositioner...
 * getLocationWidgetComparator has been changed also...
 */
public class CustomFlowPanelDropController extends FlowPanelDropController {

    /**
     * Label for IE quirks mode workaround.
     */
    private static final Label DUMMY_LABEL_IE_QUIRKS_MODE_OFFSET_HEIGHT = new Label("x");

    protected Layout layout;

    /**
     * @param dropTarget
     */
    public CustomFlowPanelDropController(FlowPanel dropTarget, Layout layout) {
        super(dropTarget);
        this.layout = layout;
    }

    @Override
    protected LocationWidgetComparator getLocationWidgetComparator() {
        return LocationWidgetComparator.BOTTOM_HALF_COMPARATOR;
    }

    @Override
    protected Widget newPositioner(DragContext context) {
        // Use two widgets so that setPixelSize() consistently affects dimensions
        // excluding positioner border in quirks and strict modes
        SimplePanel outer = new SimplePanel();
        //        outer.addStyleName(DragClientBundle.INSTANCE.css().positioner());
        outer.addStyleName(DashboardPanel.BASE_NAME + DashboardPanel.StyleSuffix.DndPositioner); // standard dnd styles set margin: 0 !important - so we replace them!!!! 

        // place off screen for border calculation
        RootPanel.get().add(outer, -500, -500);

        // Ensure IE quirks mode returns valid outer.offsetHeight, and thus valid DOMUtil.getVerticalBorders(outer)
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

        // some must have styles:
        outer.getElement().getStyle().setProperty("zoom", "1"); /* IE gain hasLayout */
        outer.getElement().getStyle().setMarginTop(layout.getVerticalSpacing(), Unit.PX);
        outer.getElement().getStyle().setMarginBottom(layout.getVerticalSpacing(), Unit.PX);
        outer.getElement().getStyle().setZIndex(100);
        System.out.println("> new Vertical Positioner created!");
        return outer;
    }
}
