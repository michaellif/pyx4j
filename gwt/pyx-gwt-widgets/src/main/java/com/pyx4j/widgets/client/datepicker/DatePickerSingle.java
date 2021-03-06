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
 */

package com.pyx4j.widgets.client.datepicker;

import java.util.ArrayList;
import java.util.Date;

import com.pyx4j.commons.LogicalDate;

public class DatePickerSingle extends DatePickerExtended {

    public DatePickerSingle(Date current, LogicalDate minDate, LogicalDate maxDate, ArrayList<LogicalDate> disabledDates) {
        super(new MonthSelectorSingle(minDate, maxDate), disabledDates);
        monthSelector.setPicker(this);
        monthSelector.setModel(this.getModel());
        this.setCurrentMonth(current);
    }
}
