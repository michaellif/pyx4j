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
 * Created on 2011-05-19
 * @author leont
 * @version $Id$
 */
package com.pyx4j.widgets.client.datepicker;

import com.pyx4j.commons.IDebugId;

public enum DatePickerIDs implements IDebugId {
    DatePicker,

    MonthSelectorButton_ForwardYear,

    MonthSelectorButton_BackwardsYear,

    MonthSelectorButton_BackwardsMonth,

    MonthSelectorButton_ForwardMonth,

    MonthSelectorLabel_Month,

    MonthSelectorLabel_Year;

    @Override
    public String debugId() {
        return name();
    }

    public static String[] monthName = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
            "December" };

}
