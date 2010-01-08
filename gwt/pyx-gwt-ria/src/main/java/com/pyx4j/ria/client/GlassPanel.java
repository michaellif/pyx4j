/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 21, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.ria.client;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

public class GlassPanel extends SimplePanel implements ResizeHandler {

    private static int showRequestCount;

    protected GlassPanel() {
        super(DOM.createDiv());
        Window.addResizeHandler(this);
        DOM.setInnerHTML(getElement(), "<table style=\"width: 100%;height: 100%;\"><tr><td>&nbsp;</td></tr></table>");
        setSize("100%", "100%");

        DOM.setStyleAttribute(getElement(), "left", "0px");
        DOM.setStyleAttribute(getElement(), "top", "0px");
        DOM.setStyleAttribute(getElement(), "position", "absolute");
        DOM.setStyleAttribute(getElement(), "zIndex", "-10");
        DOM.setStyleAttribute(getElement(), "overflow", "hidden");
        DOM.setStyleAttribute(getElement(), "background", "gray");
        DOM.setStyleAttribute(getElement(), "filter", "alpha(opacity=50)");

        setVisible(false);
    }

    public void show() {
        showRequestCount++;
        if (showRequestCount == 1) {
            setPixelSize(Window.getClientWidth(), Window.getClientHeight());
            DOM.setStyleAttribute(getElement(), "zIndex", "10");
            DOM.setStyleAttribute(getElement(), "cursor", "wait");
            setVisible(true);
        }
    }

    public boolean isShown() {
        return (showRequestCount > 0);
    }

    public void hide() {
        showRequestCount--;
        assert showRequestCount > -1;
        if (showRequestCount == 0) {
            DOM.setStyleAttribute(getElement(), "zIndex", "-10");
            DOM.setStyleAttribute(getElement(), "cursor", "default");
            setVisible(false);
        }
    }

    public void onResize(ResizeEvent event) {
        setPixelSize(event.getWidth(), event.getHeight());
    }

}
