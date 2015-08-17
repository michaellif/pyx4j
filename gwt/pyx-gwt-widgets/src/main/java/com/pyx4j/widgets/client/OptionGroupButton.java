/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Aug 4, 2015
 * @author arminea
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;

import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public abstract class OptionGroupButton extends Composite implements HasValue<Boolean>, Focusable, HasAllFocusHandlers {

    protected com.google.gwt.user.client.ui.CheckBox button;

    public OptionGroupButton(SafeHtml label) {
        super();
        button = createButtonImpl(label);
        initWidget(button);
        setStyleName(WidgetsTheme.StyleName.OptionGroupItem.name());
    }

    abstract protected com.google.gwt.user.client.ui.CheckBox createButtonImpl(SafeHtml label);

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return button.addValueChangeHandler(handler);
    }

    @Override
    public Boolean getValue() {
        return button.getValue();
    }

    @Override
    public void setValue(Boolean value) {
        button.setValue(value);
    }

    @Override
    public void setValue(Boolean value, boolean fireEvents) {
        button.setValue(value, fireEvents);
    }

    @Override
    public int getTabIndex() {
        return button.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        button.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        button.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        button.setTabIndex(index);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return button.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return button.addBlurHandler(handler);
    }

    public void setEnabled(boolean enabled) {
        button.setEnabled(enabled);
    }

}