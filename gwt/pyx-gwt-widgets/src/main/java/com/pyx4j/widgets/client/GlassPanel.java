/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 18-Sep-06
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Block access to GUI elements while service is running.
 */
public class GlassPanel extends SimplePanel implements ResizeHandler {

    private static GlassPanel instance;

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
    }

    public static GlassPanel instance() {
        if (instance == null) {
            instance = new GlassPanel();
        }
        return instance;
    }

    public static void show() {
        showRequestCount++;
        if (showRequestCount == 1) {
            instance().setPixelSize(Window.getClientWidth(), Window.getClientHeight());
            DOM.setStyleAttribute(instance.getElement(), "zIndex", "10");
            DOM.setStyleAttribute(instance.getElement(), "cursor", "wait");
        }
    }

    public static boolean isShown() {
        return (showRequestCount > 0);
    }

    public static void hide() {
        showRequestCount--;
        assert showRequestCount > -1;
        if (showRequestCount == 0) {
            DOM.setStyleAttribute(instance.getElement(), "cursor", "move");
            DOM.setStyleAttribute(instance.getElement(), "zIndex", "-10");
        }
    }

    @Override
    public void onResize(ResizeEvent event) {
        instance().setPixelSize(Window.getClientWidth(), Window.getClientHeight());
    }

}