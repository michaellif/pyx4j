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

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.view.client.Range;

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.MonthYearPicker;

public class NMonthYearPicker extends NFocusComponent<Date, MonthYearPicker, CMonthYearPicker> implements INativeFocusComponent<Date> {

    private static final I18n i18n = I18n.get(NMonthYearPicker.class);

    public static final DateTimeFormat defaultDateFormat = DateTimeFormat.getFormat(i18n.tr("MMMM yyyy"));

    public NMonthYearPicker(final CMonthYearPicker cComponent) {
        super(cComponent);
    }

    @Override
    protected MonthYearPicker createEditor() {
        return new MonthYearPicker(getCComponent().getYearRange(), getCComponent().isYearOnly());
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().setYearRange(getCComponent().getYearRange());
    }

    @Override
    public void setNativeValue(Date value) {
        if (isViewable()) {
            getViewer().setHTML(value == null ? "" : defaultDateFormat.format(value));
        } else {
            getEditor().setDate(value);
        }
    }

    @Override
    public Date getNativeValue() {
        if (!isViewable()) {
            return getEditor().getDate();
        } else {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (getEditor() != null) {
            if (enabled) {
                getEditor().getYearSelector().removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.disabled.name());
            } else {
                getEditor().getYearSelector().addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.disabled.name());
            }
            if (!getCComponent().isYearOnly()) {
                if (enabled) {
                    getEditor().getMonthSelector().removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.disabled.name());
                } else {
                    getEditor().getMonthSelector().addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.disabled.name());
                }
            }
        }
    }

    @Override
    public void setEditable(boolean editable) {
        super.setEditable(editable);
        if (getEditor() != null) {
            if (editable) {
                getEditor().getYearSelector().removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.readonly.name());
            } else {
                getEditor().getYearSelector().addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.readonly.name());
            }
            if (!getCComponent().isYearOnly()) {
                if (editable) {
                    getEditor().getMonthSelector().removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.readonly.name());
                } else {
                    getEditor().getMonthSelector().addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.readonly.name());
                }
            }
        }
    }

    @Override
    public void onPropertyChange(PropertyChangeEvent event) {
        super.onPropertyChange(event);
        if (getEditor() != null) {
            if (event.isEventOfType(PropertyName.repopulated)) {
                getEditor().getYearSelector().removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
            } else if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.repopulated)) {
                if (getCComponent().isValid()) {
                    getEditor().getYearSelector().removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
                } else if (getCComponent().isVisited()) {
                    getEditor().getYearSelector().addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
                }
            }

            if (!getCComponent().isYearOnly()) {
                if (event.isEventOfType(PropertyName.repopulated)) {
                    getEditor().getMonthSelector().removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
                } else if (event.isEventOfType(PropertyName.valid, PropertyName.visited, PropertyName.repopulated)) {
                    if (getCComponent().isValid()) {
                        getEditor().getMonthSelector().removeStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
                    } else if (getCComponent().isVisited()) {
                        getEditor().getMonthSelector().addStyleDependentName(DefaultCCOmponentsTheme.StyleDependent.invalid.name());
                    }
                }
            }
        }
    }

    public void setYearRange(Range yearRange) {
        if (getEditor() != null) {
            getEditor().setYearRange(yearRange);
        }
    }

}
