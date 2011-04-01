package com.pyx4j.widgets.client.datepicker;

import java.util.ArrayList;
import java.util.Date;

import com.pyx4j.widgets.client.style.Selector;

public class DatePickerMultiple extends DatePickerExtended {

    public DatePickerMultiple(Date current, Date minDate, Date maxDate, ArrayList<Date> disabledDates) {
        super(new MonthSelectorMultiple(minDate, maxDate), disabledDates);
        this.setCurrentMonth(current);
        this.addStyleName(Selector.getDependentName(DatePickerExtended.StyleDependent.multiple));
    }
}