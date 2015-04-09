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
 * Created on Jun 11, 2010
 * @author Misha
 */
package com.pyx4j.forms.client.ui;

import java.util.Date;

import com.google.gwt.view.client.Range;

import com.pyx4j.commons.LogicalDate;

public class CMonthYearPicker extends CFocusComponent<LogicalDate, NMonthYearPicker> {

    private final boolean yearOnly;

    @SuppressWarnings("deprecation")
    private Range yearRange = new Range(1900, new Date().getYear() + 7);

    public CMonthYearPicker(boolean yearOnly) {
        this.yearOnly = yearOnly;
        setNativeComponent(new NMonthYearPicker(this));
    }

    public boolean isYearOnly() {
        return yearOnly;
    }

    public void setYearRange(Range yearRange) {
        this.yearRange = yearRange;
        getNativeComponent().setYearRange(yearRange);
    }

    public Range getYearRange() {
        return yearRange;
    }

}
