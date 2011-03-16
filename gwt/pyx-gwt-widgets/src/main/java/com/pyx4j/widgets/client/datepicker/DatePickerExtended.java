package com.pyx4j.widgets.client.datepicker;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.CalendarView;

public class DatePickerExtended extends Composite implements HasHandlers {

    public static final Type<DateChosenEventHandler> TYPE = new Type<DateChosenEventHandler>();

    public class DateChosenEvent extends GwtEvent<DateChosenEventHandler> {
        private final Date date;

        public DateChosenEvent(Date date) {
            this.date = date;
        }

        public Date getChosenDate() {
            return this.date;
        }

        @Override
        public Type<DateChosenEventHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(DateChosenEventHandler handler) {
            handler.onDateChosen(this);
        }
    }

    public interface DateChosenEventHandler extends EventHandler {
        void onDateChosen(DateChosenEvent event);
    }

    HandlerManager handlerManager;

    HorizontalPanel panel = new HorizontalPanel();

    ArrayList<DatePickerWithYearSelector> pickers = new ArrayList<DatePickerWithYearSelector>();

    ArrayList<Date> disabledDates;

    Date minDate;

    Date maxDate;

    public Date selectedField;

    public DatePickerExtended(int numberOfMonths, Date starting, Date minDate, Date maxDate, ArrayList<Date> disabledDates) {
        DatePickerWithYearSelector picker;
        Date tempMaxDate;
        this.disabledDates = disabledDates;
        this.minDate = minDate;
        this.maxDate = maxDate;
        handlerManager = new HandlerManager(this);

        for (int i = 0; i < numberOfMonths; i++) {
            tempMaxDate = new Date(maxDate.getTime());
            CalendarUtil.addMonthsToDate(tempMaxDate, -numberOfMonths + i + 1);
            picker = new DatePickerWithYearSelector(starting, minDate, tempMaxDate, i);
            pickers.add(picker);
            picker.addValueChangeHandler(new ValueChangeHandler<Date>() {
                @Override
                public void onValueChange(ValueChangeEvent<Date> event) {
                    setGridSelection(event);
                }
            });
            picker.setParent(this);
            disableDates(picker);
            panel.add(picker);
            CalendarUtil.addMonthsToDate(starting, 1);
        }
        initWidget(panel);
    }

    public void setGridSelection(ValueChangeEvent<Date> event) {
        selectedField = event.getValue();
        DateChosenEvent dateChosen = new DateChosenEvent(selectedField);
        fireEvent(dateChosen);
    }

    public void updateComponents() {
        DatePickerWithYearSelector picker = pickers.get(0);
        Date startingDate = picker.getMyModel().getCurrentMonth();
        DatePickerWithYearSelector eachPicker;

        Date tempStartingDate = new Date(startingDate.getTime());
        for (int i = 0; i < pickers.size(); i++) {
            eachPicker = pickers.get(i);
            eachPicker.setCurrentMonth(tempStartingDate);
            CalendarUtil.addMonthsToDate(tempStartingDate, 1);
            disableDates(eachPicker);
        }
    }

    private void disableDates(DatePickerWithYearSelector picker) {
        Date first = picker.getFirstDate();
        Date last = picker.getLastDate();
        Date temp;
        CalendarView view = picker.getMyView();

        if (minDate.after(first) && minDate.before(last)) {
            temp = new Date(first.getTime());
            while (temp.before(minDate)) {
                view.setEnabledOnDate(false, temp);
                CalendarUtil.addDaysToDate(temp, 1);
            }
        }

        if (maxDate.after(first) && maxDate.before(last)) {
            temp = new Date(maxDate.getTime());
            while (temp.before(last)) {
                view.setEnabledOnDate(false, temp);
                CalendarUtil.addDaysToDate(temp, 1);
            }
        }

        for (Date disabledDate : this.disabledDates) {
            if (disabledDate.after(first) && disabledDate.before(last)) {
                view.setEnabledOnDate(false, disabledDate);
            }
        }
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    public HandlerRegistration addDateChosenEventHandler(DateChosenEventHandler handler) {
        return handlerManager.addHandler(TYPE, handler);
    }

}
