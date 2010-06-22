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

import java.util.Date;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MonthYearPicker extends HorizontalPanel implements HasChangeHandlers {

    private final ListBox monthSelector;

    private final ListBox yearSelector;

    private final int lastYear = new Date().getYear() + 7;

    public MonthYearPicker() {

        ChangeHandler changeHandler = new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                MonthYearPicker.this.fireEvent(event);
            }
        };

        monthSelector = new ListBox();
        add(monthSelector);
        String[] months = LocaleInfo.getCurrentLocale().getDateTimeConstants().months();
        monthSelector.addItem(null);
        for (String string : months) {
            monthSelector.addItem(string);
        }
        setCellWidth(monthSelector, "50%");
        monthSelector.addChangeHandler(changeHandler);

        yearSelector = new ListBox();
        add(yearSelector);
        yearSelector.getElement().getStyle().setMarginLeft(5, Unit.PX);
        yearSelector.addItem(null);
        for (int i = lastYear; i >= 0; i--) {
            yearSelector.addItem(String.valueOf(i + 1900));
        }
        setCellWidth(yearSelector, "50%");
        yearSelector.addChangeHandler(changeHandler);

    }

    public void setDate(Date date) {
        if (date == null) {
            monthSelector.setSelectedIndex(0);
            yearSelector.setSelectedIndex(0);
        } else {
            monthSelector.setSelectedIndex(date.getMonth() + 1);
            yearSelector.setSelectedIndex(lastYear - date.getYear() + 1);
        }
    }

    public Date getDate() {
        if (yearSelector.getSelectedIndex() == 0) {
            return null;
        }
        int year = lastYear - yearSelector.getSelectedIndex() + 1;
        int month = monthSelector.getSelectedIndex() == 0 ? 0 : monthSelector.getSelectedIndex() - 1;
        return new Date(year, month, 1);
    }

    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return addDomHandler(handler, ChangeEvent.getType());
    }

    public void setEnabled(boolean enabled) {
        monthSelector.setEnabled(enabled);
        yearSelector.setEnabled(enabled);
    }

    public void setFocus(boolean focused) {
        monthSelector.setFocus(focused);
        if (!focused) {
            yearSelector.setFocus(false);
        }
    }

}
