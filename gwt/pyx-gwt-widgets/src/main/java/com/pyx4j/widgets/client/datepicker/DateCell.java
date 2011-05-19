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
 *
 * Created on 2011-03-03
 * @author leont
 * @version $Id$
 */

package com.pyx4j.widgets.client.datepicker;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.widgets.client.style.Selector;

public class DateCell extends Label {

    private boolean enabled = true;

    private Date date;

    private DateGrid parent;

    public DateCell() {
        addHandlers();
    }

    public void addHandlers() {
        this.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DateCell cell = (DateCell) event.getSource();
                cell.setStyleName(Selector.getDependentName(DatePickerExtended.StyleDependent.selected), true);
            }
        });
        this.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                heighlight(true);
            }
        });
        this.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                heighlight(false);
            }
        });
    }

    @Override
    public DateGrid getParent() {
        return parent;
    }

    public void setParent(DateGrid parent) {
        this.parent = parent;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = new Date(date.getTime());
        redraw();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.setStyleName(Selector.getDependentName(DatePickerExtended.StyleDependent.disabled), !enabled);
    }

    public final void setSelected(boolean selected) {
        this.setStyleName(Selector.getDependentName(DatePickerExtended.StyleDependent.selected), selected);
        heighlight(false);
    }

    private void heighlight(boolean isheighlighted) {
        this.setStyleName(Selector.getDependentName(DatePickerExtended.StyleDependent.heighlighted), isheighlighted);
    }

    private void redraw() {
        DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DAY);
        String label = format.format(date);
        this.setText(label);
    }
}