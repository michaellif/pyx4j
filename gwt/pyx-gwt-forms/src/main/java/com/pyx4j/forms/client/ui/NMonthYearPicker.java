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
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.pyx4j.gwt.commons.ui.HTML;
import com.google.gwt.view.client.Range;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.i18n.annotations.I18nContext;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.MonthYearPicker;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class NMonthYearPicker extends NFocusField<LogicalDate, MonthYearPicker, CMonthYearPicker, HTML> implements INativeFocusField<LogicalDate> {

    private static final I18n i18n = I18n.get(NMonthYearPicker.class);

    @I18nContext(javaFormatFlag = true)
    private static final String defaultDateFormat() {
        return i18n.tr("MMMM yyyy");
    }

    public static final DateTimeFormat defaultDateFormat = DateTimeFormat.getFormat(defaultDateFormat());

    @I18nContext(javaFormatFlag = true)
    private static final String yearOnlyDateFormat() {
        return i18n.tr("yyyy");
    }

    public static final DateTimeFormat yearOnlyDateFormat = DateTimeFormat.getFormat(yearOnlyDateFormat());

    public NMonthYearPicker(final CMonthYearPicker cComponent) {
        super(cComponent);
    }

    @Override
    protected MonthYearPicker createEditor() {
        return new MonthYearPicker(getCComponent().getYearRange(), getCComponent().isYearOnly());
    }

    @Override
    protected HTML createViewer() {
        return new HTML();
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().setYearRange(getCComponent().getYearRange());
        getEditor().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                getCComponent().stopEditing();
            }
        });
    }

    @Override
    public void setNativeValue(LogicalDate value) {
        if (isViewable()) {
            getViewer().setText(value == null ? "" : getCComponent().isYearOnly() ? yearOnlyDateFormat.format(value) : defaultDateFormat.format(value));
        } else {
            getEditor().setDate(value);
        }
    }

    @Override
    public LogicalDate getNativeValue() {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            return getEditor().getDate();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (getEditor() != null) {
            if (enabled) {
                getEditor().getYearSelector().removeStyleDependentName(WidgetsTheme.StyleDependent.disabled.name());
            } else {
                getEditor().getYearSelector().addStyleDependentName(WidgetsTheme.StyleDependent.disabled.name());
            }
            if (!getCComponent().isYearOnly()) {
                if (enabled) {
                    getEditor().getMonthSelector().removeStyleDependentName(WidgetsTheme.StyleDependent.disabled.name());
                } else {
                    getEditor().getMonthSelector().addStyleDependentName(WidgetsTheme.StyleDependent.disabled.name());
                }
            }
        }
    }

    @Override
    public void setEditable(boolean editable) {
        super.setEditable(editable);
        if (getEditor() != null) {
            if (editable) {
                getEditor().getYearSelector().removeStyleDependentName(WidgetsTheme.StyleDependent.readonly.name());
            } else {
                getEditor().getYearSelector().addStyleDependentName(WidgetsTheme.StyleDependent.readonly.name());
            }
            if (!getCComponent().isYearOnly()) {
                if (editable) {
                    getEditor().getMonthSelector().removeStyleDependentName(WidgetsTheme.StyleDependent.readonly.name());
                } else {
                    getEditor().getMonthSelector().addStyleDependentName(WidgetsTheme.StyleDependent.readonly.name());
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
