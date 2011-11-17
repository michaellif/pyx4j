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
 * @version $Id$
 */

package com.pyx4j.widgets.client.datepicker;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.widgets.client.datepicker.images.DatePickerImages;

public class MonthSelectorMultiple extends MonthSelectorExtended {

    private Image backwards;

    private Image forwards;

    private Label lblMonthYear;

    private final int previousColumn = 0;

    private final int monthColumn = 1;

    private final int nextColumn = 2;

    public MonthSelectorMultiple(Date minDate, Date maxDate) {
        super(minDate, maxDate);
    }

    public void setAsFirstCalendar() {
        grid.setWidget(0, previousColumn, backwards);
    }

    public void setAsLastCalendar() {
        grid.setWidget(0, nextColumn, forwards);
    }

    @Override
    protected void refresh() {
        if (this.model != null) {
            Date current = this.model.getCurrentMonth();
            if (current != null && lblMonthYear != null) {
                lblMonthYear.setText(getMonthFormatter().format(current) + " " + geYearFormatter().format(current));
            }
        }
    }

    @Override
    protected void setup() {
        DatePickerImages resource = (DatePickerImages) GWT.create(DatePickerImages.class);
        // Set up backwards.
        backwards = new Image(resource.MonthPrevious());
        backwards.ensureDebugId(DatePickerIDs.MonthSelectorButton_BackwardsYear.debugId());
        backwards.addStyleName(DefaultDatePickerTheme.StyleDependent.middle.name());
        backwards.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateDate(-1);
                getParent().updateComponents(picker);
            }
        });

        forwards = new Image(resource.MonthNext());
        forwards.ensureDebugId(DatePickerIDs.MonthSelectorButton_ForwardYear.debugId());
        forwards.addStyleName(DefaultDatePickerTheme.StyleDependent.middle.name());
        forwards.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateDate(+1);
                getParent().updateComponents(picker);
            }
        });

        grid = new Grid(1, 3);

        lblMonthYear = new Label();
        lblMonthYear.ensureDebugId(DatePickerIDs.MonthSelectorLabel_MonthYear.debugId());
        grid.setWidget(0, monthColumn, lblMonthYear);
        grid.getCellFormatter().addStyleName(0, monthColumn, DefaultDatePickerTheme.StyleName.DatePickerMonthYearLabel.name());

        grid.setStyleName(DefaultDatePickerTheme.StyleName.DatePickerMonthSelector.name());
        //grid.addStyleName(DefaultDatePickerTheme.StyleDependent.multiple.name());
        initWidget(grid);
    }

}
