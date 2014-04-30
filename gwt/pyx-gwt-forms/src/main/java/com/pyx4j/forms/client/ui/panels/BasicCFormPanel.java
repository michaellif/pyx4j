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

import static com.pyx4j.forms.client.ui.panels.FlexFormPanelTheme.StyleName.FormFlexPanelActionWidget;
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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanelTheme.StyleName;

public class BasicCFormPanel implements IsWidget {

    private final FluidPanel fluidPanel;

    private final CForm<?> parent;

    public BasicCFormPanel(CForm<?> parent) {

        this.parent = parent;

        fluidPanel = new FluidPanel();

    }

    @Override
    public Widget asWidget() {
        return fluidPanel.asWidget();
    }

    public Widget hr() {
        HTML space = new HTML("&nbsp;");
        space.setStyleName(FormPanelHR.name());
        fluidPanel.append(Location.Full, space);
        return space;
    }

    public Widget br() {
        HTML space = new HTML("&nbsp;");
        fluidPanel.append(Location.Full, space);
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
        header.getElement().getStyle().setTextAlign(TextAlign.LEFT);

        if (imageResource != null) {
            Image image = new Image(imageResource);
            image.setStyleName(imageStyle.name());
            image.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            image.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            header.add(image);
        }

        Label label = new Label(caption);
        label.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        label.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        label.setStyleName(labelStyle.name());
        header.add(label);

        if (actionWidget != null) {
            SimplePanel actionWidgetHolder = new SimplePanel();
            actionWidgetHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            actionWidgetHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            actionWidgetHolder.setWidget(actionWidget);
            actionWidgetHolder.setStyleName(FormFlexPanelActionWidget.name());
            header.add(actionWidgetHolder);
        }

        fluidPanel.append(Location.Full, header);
        return header;
    }

    public void append(Location location, IsWidget widget) {
        fluidPanel.append(location, widget);
    }

    public CompOptions append(Location location, IObject<?> member) {
        CField<?, ?> comp = parent.inject(member);
        append(location, comp);
        return new CompOptions(comp, location == Location.Full);
    }

    public CompOptions append(Location location, IObject<?> member, CField<?, ?> comp) {
        comp = parent.inject(member, comp);
        append(location, comp);
        return new CompOptions(comp, location == Location.Full);
    }

    public void append(Location location, IObject<?> member, CComponent<?, ?, ?> comp) {
        comp = parent.inject(member, comp);
        append(location, comp);
    }

    public void setVisible(boolean visible) {
        fluidPanel.setVisible(visible);
    }

    public class CompOptions {

        private final CField<?, ?> comp;

        private final boolean dual;

        public CompOptions(CField<?, ?> comp, boolean dual) {
            this.comp = comp;
            this.dual = dual;
        }

        public FieldDecoratorOptions decorate() {
            final FieldDecoratorOptions options = new FieldDecoratorOptions(dual);
            // Untill init() called, FieldDecoratorOptions can be updated.
            comp.setDecorator(new FieldDecorator(options) {
                @Override
                protected void updateViewable() {
                    if (getLabelPosition() != LabelPosition.top) {
                        if (getComponent().isViewable()) {
                            options.labelAlignment(Alignment.left);
                            options.useLabelSemicolon(false);
                        } else {
                            options.labelAlignment(Alignment.right);
                            options.useLabelSemicolon(true);
                        }
                    }
                    updateCaption();
                    updateLabelAlignment();
                    super.updateViewable();
                }
            });
            return options;
        }
    }

    public class FieldDecoratorOptions extends FieldDecorator.Builder<FieldDecoratorOptions> {

        public static final int LABEL_WIDTH = 180;

        public static final int CONTENT_WIDTH = 300;

        public static final int CONTENT_WIDTH_DUAL = 2 * CONTENT_WIDTH + LABEL_WIDTH;

        public FieldDecoratorOptions(boolean dual) {
            super();
            labelWidth(LABEL_WIDTH + "px");
            contentWidth(dual ? CONTENT_WIDTH_DUAL + "px" : CONTENT_WIDTH + "px");
            componentWidth(dual ? CONTENT_WIDTH_DUAL + "px" : CONTENT_WIDTH + "px");
        }

        public FieldDecoratorOptions componentWidth(int componentWidthPx) {
            return componentWidth(componentWidthPx + "px");
        }

        public FieldDecoratorOptions labelWidth(int labelWidthPx) {
            return labelWidth(labelWidthPx + "px");
        }

        public FieldDecoratorOptions contentWidth(int contentWidthPx) {
            return contentWidth(contentWidthPx + "px");
        }

    }

}
