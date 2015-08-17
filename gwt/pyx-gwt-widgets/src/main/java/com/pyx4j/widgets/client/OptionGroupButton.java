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

    protected com.google.gwt.user.client.ui.CheckBox checkBox;

    public OptionGroupButton(SafeHtml label) {
        super();
        initWidget(createButtonImpl(label));
        setStyleName(WidgetsTheme.StyleName.OptionGroupItem.name());
    }

    abstract protected com.google.gwt.user.client.ui.ButtonBase createButtonImpl(SafeHtml label);

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return checkBox.addValueChangeHandler(handler);
    }

    @Override
    public Boolean getValue() {
        return checkBox.getValue();
    }

    @Override
    public void setValue(Boolean value) {
        checkBox.setValue(value);
    }

    @Override
    public void setValue(Boolean value, boolean fireEvents) {
        checkBox.setValue(value, fireEvents);
    }

    @Override
    public int getTabIndex() {
        return checkBox.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        checkBox.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        checkBox.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        checkBox.setTabIndex(index);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return checkBox.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return checkBox.addBlurHandler(handler);
    }

    public void setEnabled(boolean enabled) {
        checkBox.setEnabled(enabled);
    }

}