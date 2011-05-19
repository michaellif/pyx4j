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

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.datepicker.client.CalendarModel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.MonthSelector;

@SuppressWarnings("deprecation")
public abstract class MonthSelectorExtended extends MonthSelector {

    protected CalendarModel model;

    protected DatePickerExtended picker;

    protected DatePickerComposite parent;

    protected Date minDate;

    protected Date maxDate;

    protected Grid grid;

    protected String[] monthName = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

    public MonthSelectorExtended(Date minDate, Date maxDate) {
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setModel(CalendarModel model) {
        this.model = model;
        picker.refreshComponents();
    }

    public void setPicker(DatePickerExtended picker) {
        this.picker = picker;
    }

    public void setCompositeParent(DatePickerComposite parent) {
        this.parent = parent;
    }

    @Override
    public DatePickerComposite getParent() {
        return this.parent;
    }

    public void setValidDateWithShift(int shift) {
        Date current = this.model.getCurrentMonth();
        CalendarUtil.addMonthsToDate(current, shift);
        setValidDate(current);
    }

    public void setValidDate(Date checkDate) {
        int minDateMultiplier = minDate.getYear() * 12 + minDate.getMonth();
        int maxDateMultiplier = maxDate.getYear() * 12 + maxDate.getMonth();
        int checkDateMultiplier = checkDate.getYear() * 12 + checkDate.getMonth();

        if (checkDate.compareTo(minDate) >= 0 && checkDate.compareTo(maxDate) <= 0) {
            model.setCurrentMonth(checkDate);
        } else if (checkDateMultiplier > maxDateMultiplier) {
            model.setCurrentMonth(maxDate);
        } else if (checkDateMultiplier < minDateMultiplier) {
            model.setCurrentMonth(minDate);
        }
    }

    @Override
    public void addMonths(int numMonths) {
        Date current = model.getCurrentMonth();
        CalendarUtil.addMonthsToDate(current, numMonths);
        model.setCurrentMonth(current);
        picker.refreshComponents();
    }

    public void updateDate(int months) {
        setValidDateWithShift(months);
        getParent().updateComponents(picker);
    }
}
