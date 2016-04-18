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
 */
package com.pyx4j.forms.client.ui.panels;

import static com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName.FormPanelActionWidget;
import static com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName.FormPanelH1;
import static com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName.FormPanelH1Image;
import static com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName.FormPanelH1Label;
import static com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName.FormPanelH2;
import static com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName.FormPanelH2Label;
import static com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName.FormPanelH3;
import static com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName.FormPanelH3Label;
import static com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName.FormPanelH4;
import static com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName.FormPanelH4Label;
import static com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName.FormPanelHR;

import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.resources.client.ImageResource;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.HTML;
import com.pyx4j.gwt.commons.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.pyx4j.gwt.commons.ui.Label;
import com.pyx4j.gwt.commons.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName;

public abstract class AbstractFormPanel {

    private final CForm<?> parent;

    public AbstractFormPanel(CForm<?> parent) {
        this.parent = parent;
    }

    public CForm<?> getParent() {
        return parent;
    }

    public Widget hr() {
        Widget br = br();
        br.setStyleName(FormPanelHR.name());
        return br;
    }

    public Widget br() {
        HTML space = new HTML("&nbsp;");
        append(space);
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

    private Widget hx(ImageResource imageResource, String caption, Widget actionWidget, StyleName imageStyle, StyleName labelStyle, StyleName headerStyle) {
        FlowPanel header = new FlowPanel();
        header.setStyleName(headerStyle.name());
        header.getStyle().setProperty("display", "table");
        header.getStyle().setProperty("width", "100%");

        if (imageResource != null) {
            Image image = new Image(imageResource);
            image.setStyleName(imageStyle.name());
            image.getStyle().setProperty("display", "table-cell");
            image.getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            header.add(image);
        }

        Label label = new Label(caption);
        label.getStyle().setProperty("display", "table-cell");
        label.getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        label.setStyleName(labelStyle.name());
        header.add(label);

        if (actionWidget != null) {
            SimplePanel actionWidgetHolder = new SimplePanel();
            actionWidgetHolder.getStyle().setProperty("display", "table-cell");
            actionWidgetHolder.getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            actionWidgetHolder.setWidget(actionWidget);
            actionWidgetHolder.setStyleName(FormPanelActionWidget.name());
            header.add(actionWidgetHolder);
        }

        append(header);
        return header;
    }

    protected abstract void append(IsWidget widget);

    protected FormFieldDecoratorOptions createFieldDecoratorOptions() {
        return new FormFieldDecoratorOptions();
    }

    protected FieldDecorator createFieldDecorator(final FormFieldDecoratorOptions options) {
        return new FormFieldDecorator(options);
    }

    public class CompOptions {

        private final CField<?, ?> comp;

        public CompOptions(CField<?, ?> comp) {
            this.comp = comp;
        }

        public FormFieldDecoratorOptions decorate() {
            final FormFieldDecoratorOptions options = createFieldDecoratorOptions();
            // Until init() method called, FieldDecoratorOptions can be updated.
            comp.setDecorator(createFieldDecorator(options));
            return options;
        }
    }
}
