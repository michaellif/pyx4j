/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Apr 24, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.panels;

import static com.pyx4j.forms.client.ui.panels.DualColumnFormPanelTheme.StyleName.FormPanelActionWidget;
import static com.pyx4j.forms.client.ui.panels.DualColumnFormPanelTheme.StyleName.FormPanelH1;
import static com.pyx4j.forms.client.ui.panels.DualColumnFormPanelTheme.StyleName.FormPanelH1Image;
import static com.pyx4j.forms.client.ui.panels.DualColumnFormPanelTheme.StyleName.FormPanelH1Label;
import static com.pyx4j.forms.client.ui.panels.DualColumnFormPanelTheme.StyleName.FormPanelH2;
import static com.pyx4j.forms.client.ui.panels.DualColumnFormPanelTheme.StyleName.FormPanelH2Label;
import static com.pyx4j.forms.client.ui.panels.DualColumnFormPanelTheme.StyleName.FormPanelH3;
import static com.pyx4j.forms.client.ui.panels.DualColumnFormPanelTheme.StyleName.FormPanelH3Label;
import static com.pyx4j.forms.client.ui.panels.DualColumnFormPanelTheme.StyleName.FormPanelH4;
import static com.pyx4j.forms.client.ui.panels.DualColumnFormPanelTheme.StyleName.FormPanelH4Label;
import static com.pyx4j.forms.client.ui.panels.DualColumnFormPanelTheme.StyleName.FormPanelHR;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.DualColumnFormPanelTheme.StyleName;

public class DualColumnFormPanel implements IsWidget {

    private final DualColumnFluidPanel fluidPanel;

    public DualColumnFormPanel() {
        fluidPanel = new DualColumnFluidPanel();
    }

    @Override
    public Widget asWidget() {
        return fluidPanel.asWidget();
    }

    public Widget hr() {
        HTML space = new HTML("&nbsp;");
        space.setStyleName(FormPanelHR.name());
        fluidPanel.append(Location.Dual, space);
        return space;
    }

    public Widget br() {
        HTML space = new HTML("&nbsp;");
        fluidPanel.append(Location.Dual, space);
        return space;
    }

    public Widget h1(String caption) {
        return h1(caption, null);
    }

    public Widget h1(ImageResource image, String caption) {
        return h1(image, caption, null);
    }

    public Widget h1(String caption, Widget actionWidget) {
        return h1(null, caption, actionWidget);
    }

    public Widget h1(ImageResource image, String caption, Widget actionWidget) {
        return hx(image, caption, actionWidget, FormPanelH1Image, FormPanelH1Label, FormPanelH1);
    }

    public Widget h2(String caption) {
        return h2(caption, null);
    }

    public Widget h2(String caption, Widget actionWidget) {
        return hx(caption, actionWidget, null, FormPanelH2Label, FormPanelH2);
    }

    public Widget h3(String caption) {
        return h3(caption, null);
    }

    public Widget h3(String caption, Widget actionWidget) {
        return hx(caption, actionWidget, null, FormPanelH3Label, FormPanelH3);
    }

    public Widget h4(String caption) {
        return h4(caption, null);
    }

    public Widget h4(String caption, Widget actionWidget) {
        return hx(caption, actionWidget, null, FormPanelH4Label, FormPanelH4);
    }

    private Widget hx(String caption, Widget actionWidget, StyleName imageStyle, StyleName labelStyle, StyleName headerStyle) {
        return hx(null, caption, actionWidget, imageStyle, labelStyle, headerStyle);
    }

    public void clear() {
        fluidPanel.clear();
    }

    private Widget hx(ImageResource imageResource, String caption, Widget actionWidget, StyleName imageStyle, StyleName labelStyle, StyleName headerStyle) {
        FlowPanel header = new FlowPanel();
        header.setStyleName(headerStyle.name());
        header.getElement().getStyle().setProperty("display", "table");
        header.getElement().getStyle().setProperty("width", "100%");

        if (imageResource != null) {
            Image image = new Image(imageResource);
            image.setStyleName(imageStyle.name());
            image.getElement().getStyle().setProperty("display", "table-cell");
            image.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            header.add(image);
        }

        Label label = new Label(caption);
        label.getElement().getStyle().setProperty("display", "table-cell");
        label.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        label.setStyleName(labelStyle.name());
        header.add(label);

        if (actionWidget != null) {
            SimplePanel actionWidgetHolder = new SimplePanel();
            actionWidgetHolder.getElement().getStyle().setProperty("display", "table-cell");
            actionWidgetHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            actionWidgetHolder.setWidget(actionWidget);
            actionWidgetHolder.setStyleName(FormPanelActionWidget.name());
            header.add(actionWidgetHolder);
        }

        fluidPanel.append(Location.Dual, header);
        return header;
    }

    public void append(Location location, IsWidget widget) {
        Widget handlerPanel = new SimplePanel(widget.asWidget());
        handlerPanel.setStyleName(DualColumnFormPanelTheme.StyleName.FormPanelCell.name());
        switch (location) {
        case Left:
            handlerPanel.addStyleDependentName(DualColumnFormPanelTheme.StyleDependent.left.name());
            break;
        case Right:
            handlerPanel.addStyleDependentName(DualColumnFormPanelTheme.StyleDependent.right.name());
            break;
        case Dual:
            handlerPanel.addStyleDependentName(DualColumnFormPanelTheme.StyleDependent.dual.name());
            break;
        }
        fluidPanel.append(location, handlerPanel);
    }

    public void setVisible(boolean visible) {
        fluidPanel.setVisible(visible);
    }

    public void addStyleName(String style) {
        fluidPanel.addStyleName(style);
    }

}