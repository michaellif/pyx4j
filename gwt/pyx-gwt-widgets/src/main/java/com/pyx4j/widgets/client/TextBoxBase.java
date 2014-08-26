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
 * Created on Aug 26, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public abstract class TextBoxBase extends Composite implements ITextWidget, WatermarkComponent {

    private TextWatermark watermark;

    private com.google.gwt.user.client.ui.TextBoxBase textBoxWidget;

    private final SimplePanel textBoxHolder;

    private final FlowPanel contentPanel;

    private Button actionButton;

    public TextBoxBase() {
        contentPanel = new FlowPanel();
        contentPanel.setStyleName(WidgetTheme.StyleName.TextBoxContainer.name());
        contentPanel.getElement().getStyle().setPosition(Position.RELATIVE);

        textBoxHolder = new SimplePanel();
        textBoxHolder.getElement().getStyle().setMarginRight(0, Unit.PX);

        contentPanel.add(textBoxHolder);

        initWidget(contentPanel);
    }

    protected void setTextBoxWidget(com.google.gwt.user.client.ui.TextBoxBase textBoxWidget) {
        this.textBoxWidget = textBoxWidget;

        textBoxWidget.setStyleName(WidgetTheme.StyleName.TextBox.name());
        if (textBoxWidget instanceof com.google.gwt.user.client.ui.TextBox) {
            textBoxWidget.addStyleDependentName(WidgetTheme.StyleDependent.singleLine.name());
        }

        textBoxHolder.setWidget(textBoxWidget);
    }

    protected com.google.gwt.user.client.ui.TextBoxBase getTextBoxWidget() {
        return textBoxWidget;
    }

    protected void setAction(Command command, ImageResource imageResource) {
        if (actionButton != null) {
            contentPanel.remove(actionButton);
        }
        if (command == null) {
            textBoxHolder.getElement().getStyle().setMarginRight(0, Unit.PX);
        } else {
            actionButton = new Button(imageResource, command) {
                @Override
                protected void onAttach() {
                    super.onAttach();
                    textBoxHolder.getElement().getStyle().setMarginRight(actionButton.getOffsetWidth(), Unit.PX);
                }
            };
            actionButton.getElement().getStyle().setPosition(Position.ABSOLUTE);
            actionButton.getElement().getStyle().setTop(0, Unit.PX);
            actionButton.getElement().getStyle().setRight(0, Unit.PX);

            actionButton.setStyleName(WidgetTheme.StyleName.TextBoxActionButton.name());
            contentPanel.add(actionButton);
        }
    }

    @Override
    public void setWatermark(String text) {
        if (watermark == null) {
            watermark = createWatermark();
        }
        watermark.setWatermark(text);
    }

    protected TextWatermark createWatermark() {
        return new TextWatermark(textBoxWidget) {

            @Override
            public String getText() {
                return textBoxWidget.getText();
            }

            @Override
            public void setText(String text) {
                textBoxWidget.setText(text);
            }
        };
    }

    @Override
    public String getWatermark() {
        return watermark.getWatermark();
    }

    @Override
    public void setText(String text) {
        textBoxWidget.setText(text);
        if (watermark != null) {
            watermark.show();
        }
    }

    @Override
    public String getText() {
        if (watermark != null && watermark.isShown()) {
            return "";
        } else {
            return textBoxWidget.getText();
        }
    }

    @Override
    public void setEditable(boolean editable) {
        textBoxWidget.setReadOnly(!editable);
    }

    @Override
    public boolean isEditable() {
        return !textBoxWidget.isReadOnly();
    }

    @Override
    public void setEnabled(boolean enabled) {
        textBoxWidget.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return textBoxWidget.isEnabled();
    }

    @Override
    public int getTabIndex() {
        return textBoxWidget.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        textBoxWidget.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        textBoxWidget.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        textBoxWidget.setTabIndex(index);
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return textBoxWidget.addValueChangeHandler(handler);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return textBoxWidget.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return textBoxWidget.addBlurHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return textBoxWidget.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return textBoxWidget.addKeyUpHandler(handler);
    }

    @Deprecated
    //TODO remove after reimplementing NDatePicker
    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return textBoxWidget.addMouseDownHandler(handler);
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return textBoxWidget.addChangeHandler(handler);
    }
}
