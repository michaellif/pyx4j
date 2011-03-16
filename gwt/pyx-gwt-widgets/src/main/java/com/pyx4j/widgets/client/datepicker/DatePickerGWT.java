package com.pyx4j.widgets.client.datepicker;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

import com.pyx4j.widgets.client.datepicker.DatePickerExtended.DateChosenEvent;
import com.pyx4j.widgets.client.datepicker.DatePickerExtended.DateChosenEventHandler;

public class DatePickerGWT implements EntryPoint {

    @Override
    @SuppressWarnings("deprecation")
    public void onModuleLoad() {

        final TextBox textBox = new TextBox();
        Date minDate = new Date(100, 1, 15);
        Date maxDate = new Date(111, 3, 3);
        Date starting = new Date(110, 2, 10);
        ArrayList<Date> disabledDates = new ArrayList<Date>();
        disabledDates.add(new Date(103, 1, 1));
        disabledDates.add(new Date(103, 1, 2));
        disabledDates.add(new Date(103, 1, 3));
        disabledDates.add(new Date(103, 1, 4));
        disabledDates.add(new Date(103, 1, 5));

        DatePickerExtended datePicker = new DatePickerExtended(2, starting, minDate, maxDate, disabledDates);
        datePicker.addDateChosenEventHandler(new DateChosenEventHandler() {

            @Override
            public void onDateChosen(DateChosenEvent event) {
                String date = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).format(event.getChosenDate());
                textBox.setText(date);
            }

        });
        RootPanel.get().add(datePicker);
        RootPanel.get().add(textBox);

    }
}