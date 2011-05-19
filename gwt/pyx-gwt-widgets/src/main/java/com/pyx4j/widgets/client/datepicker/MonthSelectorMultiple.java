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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.widgets.client.datepicker.images.DatePickerImages;
import com.pyx4j.widgets.client.style.Selector;

@SuppressWarnings("deprecation")
public class MonthSelectorMultiple extends MonthSelectorExtended {

    private Image backwards;

    private Image forwards;

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
            Date currentDate = this.model.getCurrentMonth();
            int monthIndex = currentDate.getMonth();
            int year = currentDate.getYear() + 1900;
            String month = monthName[monthIndex];
            HTML monthWidget = new HTML(month + " " + year);

            monthWidget.setStyleName("headerCenter");
            grid.setWidget(0, monthColumn, monthWidget);
        }
    }

    @Override
    protected void setup() {
        DatePickerImages resource = (DatePickerImages) GWT.create(DatePickerImages.class);
        // Set up backwards.
        backwards = new Image(resource.MonthPrevious());
        backwards.ensureDebugId(DatePickerDebugIDs.MonthSelectorButton_BackwardsYear.debugId());
        backwards.addStyleName(Selector.getDependentName(DatePickerExtended.StyleDependent.top));
        backwards.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateDate(-1);
                getParent().updateComponents(picker);
            }
        });

        forwards = new Image(resource.MonthNext());
        forwards.ensureDebugId(DatePickerDebugIDs.MonthSelectorButton_ForwardYear.debugId());
        forwards.addStyleName(Selector.getDependentName(DatePickerExtended.StyleDependent.top));
        forwards.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateDate(+1);
                getParent().updateComponents(picker);
            }
        });

        grid = new Grid(1, 3);

        grid.setStyleName(Selector.getStyleName(DatePickerExtended.BASE_NAME, DatePickerExtended.StyleSuffix.MonthSelector));
        grid.setStyleName(Selector.getDependentName(DatePickerExtended.StyleDependent.multiple), true);
        initWidget(grid);
    }

}
