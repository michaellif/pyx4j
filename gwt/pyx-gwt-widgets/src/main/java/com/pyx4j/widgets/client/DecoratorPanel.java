/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
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
