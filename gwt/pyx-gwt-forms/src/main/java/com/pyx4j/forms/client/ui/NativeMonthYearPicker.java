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

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.widgets.client.MonthYearPicker;

public class NativeMonthYearPicker extends MonthYearPicker implements INativeFocusComponent<Date> {

    private final CMonthYearPicker cComponent;

    private boolean enabled = true;

    private boolean editable = true;

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
    public CComponent<?, ?> getCComponent() {
        return cComponent;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        super.setEnabled(enabled && this.isEditable());
        if (enabled) {
            yearSelector.removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.disabled.name());
        } else {
            yearSelector.addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.disabled.name());
        }
        if (!cComponent.isYearOnly()) {
            if (enabled) {
                monthSelector.removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.disabled.name());
            } else {
                monthSelector.addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.disabled.name());
            }
        }

    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        super.setEnabled(editable && this.isEnabled());
        if (editable) {
            yearSelector.removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.readonly.name());
        } else {
            yearSelector.addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.readonly.name());
        }
        if (!cComponent.isYearOnly()) {
            if (editable) {
                monthSelector.removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.readonly.name());
            } else {
                monthSelector.addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.readonly.name());
            }
        }
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void onPropertyChange(PropertyChangeEvent event) {
        if (event.isEventOfType(PropertyName.repopulated)) {
            yearSelector.removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
        } else if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.repopulated)) {
            if (cComponent.isValid()) {
                yearSelector.removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
            } else if (cComponent.isVisited()) {
                yearSelector.addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
            }
        }

        if (!cComponent.isYearOnly()) {
            if (event.isEventOfType(PropertyName.repopulated)) {
                monthSelector.removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
            } else if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.repopulated)) {
                if (cComponent.isValid()) {
                    monthSelector.removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
                } else if (cComponent.isVisited()) {
                    monthSelector.addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
                }
            }
        }
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

}
