/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.ResizableWidget;
import com.google.gwt.widgetideas.client.ResizableWidgetCollection;

public class ResizibleScrollPanel extends SimplePanel implements ResizableWidget {

    private static ResizableWidgetCollection resizableWidgetCollection = new ResizableWidgetCollection(50);

    private final SimplePanel contentPanel;

    //TODO check that RIA folder uses this class
    public ResizibleScrollPanel() {

        DOM.setStyleAttribute(getElement(), "position", "relative");
        setSize("100%", "100%");

        contentPanel = new SimplePanel();

        add(contentPanel);

        contentPanel.setSize("100%", "100%");

        DOM.setStyleAttribute(contentPanel.getElement(), "overflow", "auto");
        DOM.setStyleAttribute(contentPanel.getElement(), "position", "absolute");
        DOM.setStyleAttribute(contentPanel.getElement(), "top", "0px");
        DOM.setStyleAttribute(contentPanel.getElement(), "left", "0px");

    }

    @Override
    protected void onAttach() {
        super.onAttach();
        resizableWidgetCollection.add(this);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        resizableWidgetCollection.remove(this);
    }

    @Override
    public void onResize(int width, int height) {
        onResize();
    }

    public void onResize() {
        contentPanel.setWidth(getOffsetWidth() + "px");
        contentPanel.setHeight(getOffsetHeight() + "px");
    }

    public void setContentWidget(Widget w) {
        contentPanel.add(w);
    }
}
