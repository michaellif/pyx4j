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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

import com.pyx4j.widgets.client.datepicker.DatePickerComposite;

public class NativeWidgetBasicViewImpl extends ScrollPanel implements NativeWidgetBasicView {

    public NativeWidgetBasicViewImpl() {
        setSize("100%", "100");

        final TextBox textBox = new TextBox();
        Date minDate = new Date(0, 1, 15);
        Date maxDate = new Date(111, 3, 3);
        Date starting = new Date(110, 2, 10);

        DatePickerComposite datePicker = new DatePickerComposite(1, starting, minDate, maxDate, new ArrayList<Date>());
        datePicker.addDateChosenEventHandler(new DatePickerComposite.DateChosenEventHandler() {

            @Override
            public void onDateChosen(DatePickerComposite.DateChosenEvent event) {
                String date = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT).format(event.getChosenDate());
                textBox.setText(date);
            }

        });

        add(datePicker);
        add(textBox);

    }

}
