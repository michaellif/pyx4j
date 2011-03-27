package com.pyx4j.widgets.client.datepicker;

import java.util.ArrayList;
import java.util.Date;

public class DatePickerSingle extends DatePickerExtended {

    public DatePickerSingle(Date current, Date minDate, Date maxDate, ArrayList<Date> disabledDates) {
        super(new MonthSelectorSingle(minDate, maxDate), disabledDates);
        monthSelector.setPicker(this);
        monthSelector.setModel(this.getModel());
        this.setCurrentMonth(current);
    }
}
