/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jun 11, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.Date;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.widgets.client.MonthYearPicker;

public class NativeMonthYearPicker extends MonthYearPicker implements INativeFocusComponent<Date> {

    private boolean enabled = true;

    private boolean editable = true;

    private final CMonthYearPicker cComponent;

    public NativeMonthYearPicker(final CMonthYearPicker cComponent) {
        super(cComponent.getYearRange(), cComponent.isYearOnly());
        this.cComponent = cComponent;

        addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                cComponent.onEditingStop();
            }
        });

        setTabIndex(cComponent.getTabIndex());
        setDate(cComponent.getValue());
    }

    @Override
    public void setNativeValue(Date value) {
        setDate(value);
    }

    @Override
    public Date getNativeValue() {
        return getDate();
    }

    @Override
    public void setTabIndex(int tabIndex) {
    }

    @Override
    public CComponent<?> getCComponent() {
        return cComponent;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        super.setEnabled(enabled && this.isEditable());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        super.setEnabled(editable && this.isEnabled());
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setValid(boolean valid) {
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void installStyles(String stylePrefix) {
        // TODO Auto-generated method stub

    }
}
