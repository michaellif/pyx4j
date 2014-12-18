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
 *
 * Created on 2011-03-03
 * @author leont
 */

package com.pyx4j.widgets.client.datepicker;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.LogicalDate;

public class DateCell extends Label {

    private boolean enabled = true;

    private LogicalDate date;

    private DateGrid parent;

    private HandlerRegistration onClickHandler;

    public DateCell() {
        addHandlers();
    }

    public void addHandlers() {
        this.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                DateCell cell = (DateCell) event.getSource();

                heighlight(cell.isEnabled() && !cell.isEmpty());
            }
        });

        this.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                heighlight(false);
            }
        });
    }

    @Override
    public DateGrid getParent() {
        return parent;
    }

    public void setParent(DateGrid parent) {
        this.parent = parent;
    }

    public LogicalDate getDate() {
        return this.date;
    }

    public void setDate(LogicalDate date) {
        this.date = new LogicalDate(date.getTime());
        this.setStyleDependentName(DatePickerTheme.StyleDependent.empty.name(), false);
        redraw();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.setStyleDependentName(DatePickerTheme.StyleDependent.disabled.name(), !enabled);

        if (!enabled) {
            removeOnClick();
        }
    }

    public final void setSelected(boolean selected) {
        this.setStyleDependentName(DatePickerTheme.StyleDependent.selected.name(), selected);
        heighlight(false);
    }

    public final void setTodayDay(boolean istoday) {
        this.setStyleDependentName(DatePickerTheme.StyleDependent.todayday.name(), istoday);
    }

    public final void setOutOfDisplayMonth(boolean isoutofmonth) {
        this.setStyleDependentName(DatePickerTheme.StyleDependent.outofmonth.name(), isoutofmonth);
    }

    public final void setEmpty() {
        this.date = null;

        setSelected(false);
        setTodayDay(false);
        setOutOfDisplayMonth(false);
        setEnabled(true);
        removeOnClick();

        this.setStyleDependentName(DatePickerTheme.StyleDependent.empty.name(), true);
        redraw();
    }

    public boolean isEmpty() {
        return this.date == null;
    }

    public void setOnClick(HandlerRegistration handler) {
        this.onClickHandler = handler;
    }

    private void removeOnClick() {
        if (onClickHandler != null) {
            onClickHandler.removeHandler();
            onClickHandler = null;
        }
    }

    private void heighlight(boolean isheighlighted) {
        this.setStyleDependentName(DatePickerTheme.StyleDependent.heighlighted.name(), isheighlighted);
    }

    private void redraw() {
        if (!isEmpty()) {
            DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DAY);
            String label = format.format(date);
            this.setText(label);
        } else
            this.getElement().setInnerHTML("&nbsp;");
    }
}