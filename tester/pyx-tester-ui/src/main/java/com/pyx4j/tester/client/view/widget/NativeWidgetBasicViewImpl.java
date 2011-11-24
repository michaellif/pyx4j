/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Nov 9, 2011
 * @author igor
 * @version $Id$
 */
package com.pyx4j.tester.client.view.widget;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

import com.pyx4j.forms.client.ui.CButton;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.datepicker.DatePickerComposite;

public class NativeWidgetBasicViewImpl extends ScrollPanel implements NativeWidgetBasicView {

    private static I18n i18n = I18n.get(NativeWidgetBasicViewImpl.class);

    public NativeWidgetBasicViewImpl() {
        setSize("100%", "100%");

        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Main Form"));

        Date starting = new Date();
        Date minDate = new Date(110, 1, 1);
        Date maxDate = new Date(112, 12, 31);

        final TextBox textBox = new TextBox();

        DatePickerComposite datePicker = new DatePickerComposite();
        datePicker.addDateChosenEventHandler(new DatePickerComposite.DateChosenEventHandler() {

            @Override
            public void onDateChosen(DatePickerComposite.DateChosenEvent event) {
                String date = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).format(event.getChosenDate());
                textBox.setText(date);
            }

        });

        //Label lbl = new Label("CalendarUtil.getStartingDayOfWeek is: " + new Integer(CalendarUtil.getStartingDayOfWeek()).toString());
        //main.setWidget(++row, 0, lbl);
        main.setWidget(++row, 0, textBox);
        main.setWidget(++row, 0, datePicker);

        main.setHR(++row, 0, 1);

        DatePickerComposite datePickerMulti = new DatePickerComposite(3, starting, minDate, maxDate, new ArrayList<Date>());
        main.setWidget(++row, 0, datePickerMulti);

        main.setHR(++row, 0, 1);

        CButton btn = new CButton("Say \"Hello\"", new Command() {
            @Override
            public void execute() {

            }
        });

        main.setWidget(++row, 0, btn);

        add(main);
    }

}
