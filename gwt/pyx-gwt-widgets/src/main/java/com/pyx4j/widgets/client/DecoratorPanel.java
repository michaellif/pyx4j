/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on May 5, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DecoratorPanel extends DockPanel {

    private final String size;

    private final String styleName;

    public DecoratorPanel(boolean top, boolean right, boolean bottom, boolean left, int size, String styleName) {
        this.size = size + "px";
        this.styleName = styleName;

        if (bottom) {
            createBorder(DockPanel.SOUTH);
        }
        if (top) {
            createBorder(DockPanel.NORTH);
        }
        if (right) {
            createBorder(DockPanel.EAST);
        }
        if (left) {
            createBorder(DockPanel.WEST);
        }

    }

    private void createBorder(DockLayoutConstant side) {
        SimplePanel border = new SimplePanel();
        add(border, side);
        if (DockPanel.NORTH.equals(side) || DockPanel.SOUTH.equals(side)) {
            border.setSize("100%", size);
            setCellHeight(border, size);
        } else if (DockPanel.WEST.equals(side) || DockPanel.EAST.equals(side)) {
            border.setSize(size, "100%");
            setCellWidth(border, size);
            setCellHeight(border, "1px");
        } else {
            return;
        }
        DOM.setStyleAttribute(border.getElement(), "overflow", "hidden");
        DOM.setStyleAttribute(border.getElement(), "fontSize", "0");

        border.setStyleName(styleName);
    }

    public void setWidget(Widget widget) {
        add(widget, DockPanel.CENTER);
        widget.setSize("100%", "100%");
        setCellHeight(widget, "100%");
        setCellWidth(widget, "100%");
    }

}
