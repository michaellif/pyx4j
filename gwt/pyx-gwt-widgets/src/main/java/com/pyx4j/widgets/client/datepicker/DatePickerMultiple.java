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

import com.pyx4j.widgets.client.style.Selector;

public class DatePickerMultiple extends DatePickerExtended {

    public DatePickerMultiple(Date current, Date minDate, Date maxDate, ArrayList<Date> disabledDates) {
        super(new MonthSelectorMultiple(minDate, maxDate), disabledDates);
        this.setCurrentMonth(current);
        this.addStyleName(Selector.getDependentName(DatePickerExtended.StyleDependent.multiple));
    }
}