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
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.view.client.Range;

import com.pyx4j.commons.LogicalDate;

public class MonthYearPicker extends HorizontalPanel implements HasChangeHandlers, IFocusWidget {

    protected final ListBox monthSelector;

    protected final ListBox yearSelector;

    private Range yearRange;

    private final boolean showYearOnly;

    private boolean enabled = true;

    private boolean editable = true;

    private final GroupFocusHandler focusHandlerManager;

    public MonthYearPicker() {
        this(new Range(1900, new LogicalDate().getYear() + 7), true);
    }

    public MonthYearPicker(Range yearRange, boolean showYearOnly) {

        this.showYearOnly = showYearOnly;
        this.yearRange = yearRange;
        ChangeHandler changeHandler = new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                MonthYearPicker.this.fireEvent(event);
            }
        };

        if (!showYearOnly) {
            monthSelector = new ListBox();

            add(monthSelector);
            String[] months = LocaleInfo.getCurrentLocale().getDateTimeConstants().months();
            monthSelector.addItem("");
            for (String string : months) {
                monthSelector.addItem(string);
            }
            setCellWidth(monthSelector, "50%");
            monthSelector.addChangeHandler(changeHandler);
        } else {
            monthSelector = null;
        }

        yearSelector = new ListBox();
        initYearSelector();
        add(yearSelector);
        if (!showYearOnly) {
            setCellWidth(yearSelector, "50%");
            yearSelector.getElement().getStyle().setMarginLeft(5, Unit.PX);
        }
        yearSelector.addChangeHandler(changeHandler);

        focusHandlerManager = new GroupFocusHandler(this);

        yearSelector.addFocusHandler(focusHandlerManager);
        yearSelector.addBlurHandler(focusHandlerManager);

        if (!showYearOnly) {
            monthSelector.addFocusHandler(focusHandlerManager);
            monthSelector.addBlurHandler(focusHandlerManager);
        }

    }

    private void initYearSelector() {
        yearSelector.clear();
        yearSelector.addItem("");
        for (int i = yearRange.getStart() + yearRange.getLength(); i >= yearRange.getStart(); --i) {
            yearSelector.addItem(String.valueOf(i));
        }
    }

    public void setDate(LogicalDate date) {
        if (date == null) {
            if (!showYearOnly) {
                monthSelector.setSelectedIndex(0);
            }
            yearSelector.setSelectedIndex(0);
        } else {
            if (!showYearOnly) {
                monthSelector.setSelectedIndex(date.getMonth() + 1);
            }
            int index = yearRange.getStart() + yearRange.getLength() - (1900 + date.getYear()) + 1;
            if (index > 0) {
                yearSelector.setSelectedIndex(index);
            } else {
                yearSelector.setSelectedIndex(0);
            }
        }
    }

    public LogicalDate getDate() {
        if (yearSelector.getSelectedIndex() == 0) {
            return null;
        }
        int year = yearRange.getStart() + yearRange.getLength() - yearSelector.getSelectedIndex() + 1;
        int month = 0;
        if (!showYearOnly) {
            month = monthSelector.getSelectedIndex() == 0 ? 0 : monthSelector.getSelectedIndex() - 1;
        }
        return new LogicalDate(year - 1900, month, 1);
    }

    public void setYearRange(Range yearRange) {
        LogicalDate current = getDate();
        this.yearRange = yearRange;
        initYearSelector();
        setDate(current);
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return addDomHandler(handler, ChangeEvent.getType());
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!showYearOnly) {
            monthSelector.setEnabled(enabled);
        }
        yearSelector.setEnabled(enabled);
    }

    @Override
    public void setFocus(boolean focused) {
        if (showYearOnly) {
            yearSelector.setFocus(focused);
        } else {
            monthSelector.setFocus(focused);
            if (!focused) {
                yearSelector.setFocus(false);
            }
        }
    }

    @Override
    public void onEnsureDebugId(String debugID) {
        super.onEnsureDebugId(debugID);
        yearSelector.ensureDebugId(debugID + "_yy");
        if (monthSelector != null) {
            monthSelector.ensureDebugId(debugID + "_mm");
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        if (!showYearOnly) {
            monthSelector.setEditable(editable);
        }
        yearSelector.setEditable(editable);
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public int getTabIndex() {
        return yearSelector.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        yearSelector.setAccessKey(key);
    }

    @Override
    public void setTabIndex(int index) {
        yearSelector.setTabIndex(index);
    }

    public ListBox getMonthSelector() {
        return monthSelector;
    }

    public ListBox getYearSelector() {
        return yearSelector;
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        return focusHandlerManager.addHandler(FocusEvent.getType(), focusHandler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        return focusHandlerManager.addHandler(BlurEvent.getType(), blurHandler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return null;
    }
}
