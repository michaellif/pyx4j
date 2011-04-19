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
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.widgets.client.dashboard;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class VerticalFlowPanelDropController extends FlowPanelDropController {

    /**
     * @param dropTarget
     */
    public VerticalFlowPanelDropController(FlowPanel dropTarget) {
        super(dropTarget);
    }

    @Override
    protected LocationWidgetComparator getLocationWidgetComparator() {
        return LocationWidgetComparator.BOTTOM_HALF_COMPARATOR;
    }

    @Override
    protected Widget newPositioner(DragContext context) {
        SimplePanel positioner = new SimplePanel();
        positioner.getElement().getStyle().setProperty("WebkitBoxSizing", "border-box");
        positioner.getElement().getStyle().setProperty("MozBoxSizing", "border-box");
        positioner.getElement().getStyle().setProperty("boxSizing", "border-box");

        int width = 0;
        int height = 0;
        for (Widget widget : context.selectedWidgets) {
            width = Math.max(width, widget.getOffsetWidth());
            height += widget.getOffsetHeight();
        }

        positioner.setPixelSize(width, height);

        positioner.getElement().getStyle().setPadding(Dashboard.SPACING, Unit.PX);
        positioner.getElement().getStyle().setZIndex(100);

        SimplePanel positionerBorder = new SimplePanel();
        positionerBorder.setHeight("100%");
        positionerBorder.getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
        positionerBorder.getElement().getStyle().setBorderWidth(1, Unit.PX);
        positionerBorder.getElement().getStyle().setBorderColor("#555");
        positioner.setWidget(positionerBorder);

        return positioner;
    }
}