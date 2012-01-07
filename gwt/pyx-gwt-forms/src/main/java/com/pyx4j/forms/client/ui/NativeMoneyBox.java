/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jan 7, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.forms.client.events.PropertyChangeEvent;

public class NativeMoneyBox extends HorizontalPanel implements INativeTextComponent<Double> {

    private final NativeTextBox<Double> valueBox;

    public NativeMoneyBox(CMoneyField cMoneyField) {
        super();
        valueBox = new NativeTextBox<Double>(cMoneyField);
        add(valueBox);
        valueBox.setWidth("100%");
        setCellWidth(valueBox, "100%");
        add(new Label("$"));
    }

    @Override
    public void setTabIndex(int tabIndex) {
        valueBox.setTabIndex(tabIndex);
    }

    @Override
    public void setFocus(boolean focused) {
        valueBox.setFocus(focused);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return valueBox.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return valueBox.addBlurHandler(handler);
    }

    @Override
    public void setEnabled(boolean enabled) {
        valueBox.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return valueBox.isEnabled();
    }

    @Override
    public CComponent<?, ?> getCComponent() {
        return valueBox.getCComponent();
    }

    @Override
    public void setEditable(boolean editable) {
        valueBox.setEditable(editable);
    }

    @Override
    public boolean isEditable() {
        return valueBox.isEditable();
    }

    @Override
    public void setNativeValue(Double value) {
        valueBox.setNativeValue(value);
    }

    @Override
    public Double getNativeValue() throws ParseException {
        return valueBox.getNativeValue();
    }

    @Override
    public void onPropertyChange(PropertyChangeEvent event) {
        valueBox.onPropertyChange(event);
    }

    @Override
    public void setNativeText(String newValue) {
        valueBox.setNativeText(newValue);
    }

    @Override
    public String getNativeText() {
        return valueBox.getNativeText();
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return valueBox.addChangeHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return valueBox.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return valueBox.addKeyUpHandler(handler);
    }
}
