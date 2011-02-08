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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.Date;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.StringDebugId;

public class CRangeDatePicker extends CHorizontalPanel {

    private final CDatePicker fromDate;

    private final CDatePicker toDate;

    public CRangeDatePicker(String title) {
        super();
        super.setTitle(title);

        // Sum of Width should be 200px
        fromDate = new CDatePicker();
        fromDate.setWidth("97px");
        CLayoutConstraints fromDateConstraints = new CLayoutConstraints();
        fromDateConstraints.padding = new CLayoutConstraints.Padding(0, 0, 0, 0);
        fromDateConstraints.margin = new CLayoutConstraints.Margin(0, 0, 0, 0);
        fromDate.setConstraints(fromDateConstraints);
        this.setConstraints(fromDateConstraints);

        addComponent(fromDate);

        CLabel dash = new CLabel() {
            @Override
            public boolean isValueEmpty() {
                return true;
            }
        };
        dash.setValue("-");
        dash.setWidth("5px");
        CLayoutConstraints dashConstraints = new CLayoutConstraints();
        dashConstraints.margin = new CLayoutConstraints.Margin(3, 0, 0, 0);
        dashConstraints.padding = new CLayoutConstraints.Padding(0, 0, 0, 0);
        dash.setConstraints(dashConstraints);

        addComponent(dash);

        toDate = new CDatePicker();
        toDate.setWidth("97px");
        addComponent(toDate);

        setWidth("1px");

    }

    @Override
    public void setDebugId(IDebugId debugID) {
        //TODO: use CompositeDebugId here
        fromDate.setDebugId(new StringDebugId(debugID.getDebugIdString() + "-from"));
        toDate.setDebugId(new StringDebugId(debugID.getDebugIdString() + "-to"));
    }

    public void setValue(Date[] dates) {
        if (dates == null) {
            fromDate.setValue(null);
            toDate.setValue(null);
        } else if (dates.length != 2) {
            throw new IllegalArgumentException("Array of dates should have 'from' and 'to' dates");
        } else {
            fromDate.setValue(dates[0]);
            toDate.setValue(dates[1]);
        }
    }

    @SuppressWarnings("deprecation")
    public Date[] getValue() {
        Date toDateEndOfDay = null;
        if (toDate.getValue() != null) {
            toDateEndOfDay = new Date(toDate.getValue().getYear(), toDate.getValue().getMonth(), toDate.getValue().getDate(), 23, 59, 59);
        }
        return new Date[] { fromDate.getValue(), toDateEndOfDay };
    }

    public boolean isValueEmpty() {
        return fromDate.isValueEmpty() && toDate.isValueEmpty();
    }
}
