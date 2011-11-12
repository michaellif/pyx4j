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

import com.pyx4j.widgets.client.datepicker.HoldableImage.HoldElapsedEvent;
import com.pyx4j.widgets.client.datepicker.HoldableImage.HoldElapsedEventHandler;
import com.pyx4j.widgets.client.datepicker.images.DatePickerImages;

public class MonthSelectorSingle extends MonthSelectorExtended {

    private Image backwards;

    private Image forwards;

    private HoldableImage backwardsYear;

    private HoldableImage forwardsYear;

    private Label lblYear;

    private Label lblMonth;

    private final int previousMonthColumn = 0;

    private final int dateMonthColumn = 1;

    private final int nextMonthColumn = 2;

    private final int dateYearColumn = 3;

    private final int yearNavigationColumn = 4;

    private final int nextYearRow = 0;

    private final int previousYearRow = 1;

    public MonthSelectorSingle(Date minDate, Date maxDate) {
        super(minDate, maxDate);
    }

    @Override
    protected void refresh() {
        if (this.model != null) {
            Date current = this.model.getCurrentMonth();
            if (current != null && lblMonth != null && lblYear != null) {
                lblMonth.setText(getMonthFormatter().format(current));
                lblYear.setText(geYearFormatter().format(current));
            }
        }
    }

    @Override
    protected void setup() {
        DatePickerImages resource = (DatePickerImages) GWT.create(DatePickerImages.class);
        // Set up backwards.        
        backwards = new Image(resource.MonthPrevious());
        backwards.ensureDebugId(DatePickerIDs.MonthSelectorButton_BackwardsMonth.debugId());
        backwards.addStyleName(DefaultDatePickerTheme.StyleDependent.middle.name());
        backwards.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateDate(-1);
                getParent().updateComponents(picker);
            }
        });

        forwards = new Image(resource.MonthNext());
        forwards.ensureDebugId(DatePickerIDs.MonthSelectorButton_ForwardMonth.debugId());
        forwards.addStyleName(DefaultDatePickerTheme.StyleDependent.middle.name());
        forwards.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                updateDate(+1);
                getParent().updateComponents(picker);
            }
        });
        // Set up backwards year
        backwardsYear = new HoldableImage(resource.YearPrevious(), 300);
        backwardsYear.ensureDebugId(DatePickerIDs.MonthSelectorButton_BackwardsYear.debugId());
        backwardsYear.addStyleName(DefaultDatePickerTheme.StyleDependent.bottom.name());
        backwardsYear.addHoldElapsedHandler(new HoldElapsedEventHandler() {

            @Override
            public void onHoldElapsed(HoldElapsedEvent event) {
                updateDate(-12 * event.getChange());
                getParent().updateComponents(picker);
            }
        });

        forwardsYear = new HoldableImage(resource.YearNext(), 300);
        forwardsYear.ensureDebugId(DatePickerIDs.MonthSelectorButton_ForwardYear.debugId());
        forwardsYear.addStyleName(DefaultDatePickerTheme.StyleDependent.top.name());
        forwardsYear.addHoldElapsedHandler(new HoldElapsedEventHandler() {

            @Override
            public void onHoldElapsed(HoldElapsedEvent event) {
                updateDate(+12 * event.getChange());
                getParent().updateComponents(picker);
            }
        });

        lblMonth = new Label();
        lblMonth.ensureDebugId(DatePickerIDs.MonthSelectorLabel_Month.debugId());
        lblYear = new Label();
        lblYear.ensureDebugId(DatePickerIDs.MonthSelectorLabel_Year.debugId());
        grid = new Grid(1, 5);
        Grid yearGrid = new Grid(2, 1);

        grid.setWidget(0, previousMonthColumn, backwards);
        grid.setWidget(0, dateMonthColumn, lblMonth);
        grid.setWidget(0, nextMonthColumn, forwards);
        grid.setWidget(0, dateYearColumn, lblYear);

        yearGrid.setWidget(nextYearRow, 0, forwardsYear);
        yearGrid.setWidget(previousYearRow, 0, backwardsYear);

        grid.setWidget(0, yearNavigationColumn, yearGrid);

        grid.setStyleName(DefaultDatePickerTheme.StyleName.DatePickerMonthSelector.name());
        grid.getCellFormatter().addStyleName(0, previousMonthColumn, DefaultDatePickerTheme.StyleName.DatePickerNavigation.name());
        grid.getCellFormatter().addStyleName(0, dateMonthColumn, DefaultDatePickerTheme.StyleName.DatePickerMonthLabel.name());
        grid.getCellFormatter().addStyleName(0, nextMonthColumn, DefaultDatePickerTheme.StyleName.DatePickerNavigation.name());
        grid.getCellFormatter().addStyleName(0, nextMonthColumn, DefaultDatePickerTheme.StyleDependent.right.name());
        grid.getCellFormatter().addStyleName(0, dateYearColumn, DefaultDatePickerTheme.StyleName.DatePickerYearLabel.name());
        grid.getCellFormatter().addStyleName(0, yearNavigationColumn, DefaultDatePickerTheme.StyleName.DatePickerYearNavigation.name());
        initWidget(grid);
    }
}
