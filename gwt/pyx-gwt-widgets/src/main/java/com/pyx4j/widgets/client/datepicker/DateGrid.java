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
 * Created on 2011-03-03
 * @author leont
 * @version $Id$
 */

package com.pyx4j.widgets.client.datepicker;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.impl.ElementMapperImpl;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.CalendarUtil;

@SuppressWarnings("deprecation")
public class DateGrid extends Grid {

    private DateCell selectedCell;

    private Date selectedDate;

    private final Date minDate;

    private final Date maxDate;

    private final ArrayList<Date> disabledDates;

    private final ArrayList<DateCell> cellList = new ArrayList<DateCell>();

    private final ElementMapperImpl<DateCell> elementToCell = new ElementMapperImpl<DateCell>();

    protected DateGrid(Date minDate, Date maxDate, ArrayList<Date> disabledDates) {
        setCellPadding(0);
        setCellSpacing(0);
        setBorderWidth(0);

        this.minDate = minDate;
        this.maxDate = maxDate;
        this.disabledDates = disabledDates;

        setStyleName(DefaultDatePickerTheme.StyleName.DatePickerGrid.name());
        resize(CalendarModel.WEEKS_IN_MONTH + 1, CalendarModel.DAYS_IN_WEEK);
        drawCells();
    }

    @Override
    protected void onEnsureDebugId(String id) {
        DateCell cell;

        for (int row = 1; row < this.numRows; row++) {
            for (int col = 0; col < this.numColumns; col++) {
                cell = (DateCell) getWidget(row, col);
                cell.ensureDebugId(id + "_" + row + "_" + col);
            }
        }
    }

    public void drawCells() {
        DateCell cell;

        for (int row = 1; row < this.numRows; row++) {
            for (int col = 0; col < this.numColumns; col++) {
                cell = new DateCell();
                cell.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        DateCell clickedCell = (DateCell) event.getSource();

                        if (!clickedCell.isEmpty()) {
                            if (selectedCell != null) {
                                selectedCell.setSelected(false);
                            }

                            selectedCell = clickedCell;
                            selectedDate = selectedCell.getDate();
                            selectedCell.setSelected(selectedCell.isEnabled());
                        }
                    }
                });
                setWidget(row, col, cell);
            }
        }
    }

    public void redraw(Date firstDisplayed) {
        Date lastDisplayed = new Date();
        Date enabledDate;
        int displayedMonth;

        if (selectedCell != null) {
            selectedCell.setSelected(false);
            selectedCell = null;
        }

//        if (firstDisplayed.getDate() == 1) {
//            // show one empty week if date is Monday is the first in month.
//            CalendarUtil.addDaysToDate(firstDisplayed, -7);
//        }

        //enabledDate = new Date(firstDisplayed.getTime());
        displayedMonth = new Date(firstDisplayed.getTime()).getMonth();

        if (firstDisplayed.getDate() != 1) {
            displayedMonth = (displayedMonth == 11) ? 0 : displayedMonth + 1;
        }

        lastDisplayed.setTime(firstDisplayed.getTime());
        Date current = new Date();
        boolean lastrowempty = false;

        for (int i = 0; i < getNumCells(); i++) {
            DateCell cell = getCell(i);

            if (i >= 35 && lastDisplayed.getMonth() != displayedMonth /* && firstDisplayed.getDate() == 1 */) {
                lastrowempty = true;
            }

            if (!lastrowempty) {
                cell.setDate(lastDisplayed);
                cell.setOutOfMonth(lastDisplayed.getMonth() != displayedMonth);

                if (selectedDate != null && CalendarUtil.isSameDate(lastDisplayed, selectedDate)) {
                    cell.setSelected(true);
                    selectedCell = cell;
                }

                cell.setTodayDay(CalendarUtil.isSameDate(lastDisplayed, current));
            } else {
                cell.setEmpty();
            }

            CalendarUtil.addDaysToDate(lastDisplayed, 1);
        }
    }

    private boolean isEnabled(Date currentDate) {
        if (currentDate.before(minDate)) {
            return false;
        }

        if (currentDate.after(maxDate)) {
            return false;
        }

        for (Date disabledDate : this.disabledDates) {
            if (currentDate.equals(disabledDate)) {
                return false;
            }
        }

        return true;
    }

    public DateCell getCell(int i) {
        return cellList.get(i);
    }

    public DateCell getCell(Event e) {
        // Find out which cell was actually clicked.
        Element td = getEventTargetCell(e);
        return td != null ? elementToCell.get((com.google.gwt.user.client.Element) td) : null;
    }

    public int getNumCells() {
        return cellList.size();
    }

    public DateCell getSelectedCell() {
        return selectedCell;
    }

    public Date getSelectedValue() {
        return getValue(selectedCell);
    }

    public Date getValue(DateCell cell) {
        return (cell == null ? null : cell.getDate());
    }

    public void setWidget(int row, int col, DateCell cell) {
        super.setWidget(row, col, cell);

        elementToCell.put(cell);
        cellList.add(cell);
    }

    public final void clearSelection() {
        if (selectedCell != null) {
            selectedCell.setSelected(false);
        }

        selectedDate = null;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }
}