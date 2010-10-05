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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.style.CSSClass;

/**
 * Block access to GUI elements while service is running.
 */
public class GlassPanel extends AbsolutePanel implements ResizeHandler {

    private static final Logger log = LoggerFactory.getLogger(Dialog.class);

    private static GlassPanel instance;

    private static int showRequestCount;

    private HandlerRegistration handlerRegistration;

    private final HTML label;

    private final SimplePanel glass;

    public static enum GlassStyle {

        Transparent,

        SemiTransparent
    }

    protected GlassPanel() {

        //        DOM.setInnerHTML(
        //                getElement(),
        //                "<center><div style='background-color:#FFFfff;width:10em'><b>loading...</b></div></center><table style=\"width: 100%;height: 100%;\"><tr><td>&nbsp;</td></tr></table>");

        setSize("100%", "100%");
        getElement().getStyle().setProperty("left", "0px");
        getElement().getStyle().setProperty("top", "0px");
        getElement().getStyle().setProperty("overflow", "hidden");
        getElement().getStyle().setProperty("cursor", "wait");
        getElement().getStyle().setProperty("position", "absolute");

        getElement().getStyle().setProperty("zIndex", "100");

        getElement().getStyle().setDisplay(Display.NONE);

        glass = new SimplePanel();
        glass.setSize("100%", "100%");
        add(glass, 0, 0);

        label = new HTML();
        add(label, 0, 0);

    }

    public static GlassPanel instance() {
        if (instance == null) {
            instance = new GlassPanel();
        }
        return instance;
    }

    public static void show() {
        show(GlassStyle.SemiTransparent);
    }

    public static void show(GlassStyle glassStyle) {
        showRequestCount++;
        log.trace("Show glass panel request (" + showRequestCount + ")");
        if (showRequestCount == 1) {
            GlassPanel glassPanel = instance();
            glassPanel.handlerRegistration = Window.addResizeHandler(glassPanel);
            DOM.setCapture(glassPanel.getElement());
            switch (glassStyle) {
            case Transparent:
                glassPanel.glass.setStyleName(CSSClass.pyx4j_GlassPanel_Transparent.name());
                glassPanel.label.setStyleName(CSSClass.pyx4j_GlassPanel_Transparent_Label.name());
                glassPanel.label.setHTML("loading...");
                break;
            case SemiTransparent:
                glassPanel.glass.setStyleName(CSSClass.pyx4j_GlassPanel_SemiTransparent.name());
                glassPanel.label.setStyleName(CSSClass.pyx4j_GlassPanel_SemiTransparent_Label.name());
                glassPanel.label.setHTML("sending...");
                break;
            }
            glassPanel.setGlassPanelSize();
            glassPanel.getElement().getStyle().setDisplay(Display.BLOCK);
        }
    }

    public static boolean isShown() {
        return (showRequestCount > 0);
    }

    public static void hide() {
        showRequestCount--;
        log.trace("Hide glass panel request(" + showRequestCount + ")");
        assert showRequestCount > -1;
        if (showRequestCount == 0) {
            instance().handlerRegistration.removeHandler();
            DOM.releaseCapture(instance().getElement());
            instance().getElement().getStyle().setDisplay(Display.NONE);
        }
    }

    private void setGlassPanelSize() {
        Style style = getElement().getStyle();

        int winWidth = Window.getClientWidth();
        int winHeight = Window.getClientHeight();

        // Hide the glass while checking the document size. Otherwise it would
        // interfere with the measurement.
        style.setDisplay(Display.NONE);
        style.setWidth(0, Unit.PX);
        style.setHeight(0, Unit.PX);

        int width = Document.get().getScrollWidth();
        int height = Document.get().getScrollHeight();

        // Set the glass size to the larger of the window's client size or the
        // document's scroll size.
        style.setWidth(Math.max(width, winWidth), Unit.PX);
        style.setHeight(Math.max(height, winHeight), Unit.PX);

        // The size is set. Show the glass again.
        style.setDisplay(Display.BLOCK);

        setWidgetPosition(label, winWidth / 2, Document.get().getScrollTop());
    }

    public void onResize(ResizeEvent event) {
        setGlassPanelSize();
    }

}