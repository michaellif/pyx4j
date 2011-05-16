/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
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

import com.pyx4j.widgets.client.style.Selector;

@SuppressWarnings(/* Date manipulation required */{ "deprecation" })
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
        setStyleName(Selector.getStyleName(DatePickerExtended.BASE_NAME, DatePickerExtended.StyleSuffix.Grid));
        resize(CalendarModel.WEEKS_IN_MONTH + 1, CalendarModel.DAYS_IN_WEEK);
        drawCells();
    }

    public void drawCells() {
        DateCell cell;
        for (int row = 1; row < this.numRows; row++) {
            for (int col = 0; col < this.numColumns; col++) {
                cell = new DateCell(row, col);
                cell.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        DateCell clickedCell = (DateCell) event.getSource();
                        if (selectedCell != null) {
                            selectedCell.setSelected(false);
                        }
                        selectedCell = clickedCell;
                        selectedDate = selectedCell.getDate();
                        selectedCell.setSelected(true);
                    }
                });
                setWidget(row, col, cell);
            }
        }
    }

    public void redraw(Date firstDisplayed) {
        Date lastDisplayed = new Date();
        Date enabledDate;
        int enableMonth;

        if (selectedCell != null) {
            selectedCell.setSelected(false);
            selectedCell = null;
        }

        if (firstDisplayed.getDate() == 1) {
            // show one empty week if date is Monday is the first in month.
            CalendarUtil.addDaysToDate(firstDisplayed, -7);
        }

        enabledDate = new Date(firstDisplayed.getTime());
        enableMonth = enabledDate.getMonth();
        enableMonth = (enableMonth == 11) ? 0 : enableMonth + 1;

        lastDisplayed.setTime(firstDisplayed.getTime());

        for (int i = 0; i < getNumCells(); i++) {
            DateCell cell = getCell(i);
            cell.setDate(lastDisplayed);
            cell.setEnabled(isEnabled(lastDisplayed, enableMonth));
            if (selectedDate != null && CalendarUtil.isSameDate(lastDisplayed, selectedDate)) {
                cell.setSelected(true);
                selectedCell = cell;
            }
            CalendarUtil.addDaysToDate(lastDisplayed, 1);
        }
    }

    private boolean isEnabled(Date currentDate, int enabledMonth) {
        if (currentDate.getMonth() != enabledMonth) {
            return false;
        }

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