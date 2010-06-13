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
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import java.util.Date;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MonthYearPicker extends HorizontalPanel implements HasChangeHandlers {

    private final ListBox monthSelector;

    private final TextBox yearBox;

    public MonthYearPicker() {

        ChangeHandler changeHandler = new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                MonthYearPicker.this.fireEvent(event);
            }
        };

        monthSelector = new ListBox();
        add(monthSelector);
        String[] months = LocaleInfo.getCurrentLocale().getDateTimeConstants().months();
        monthSelector.addItem(null);
        for (String string : months) {
            monthSelector.addItem(string);
        }
        setCellWidth(monthSelector, "50%");
        monthSelector.addChangeHandler(changeHandler);

        yearBox = new TextBox();
        yearBox.setWidth("75px");
        add(yearBox);
        yearBox.getElement().getStyle().setMarginLeft(9, Unit.PX);
        setCellWidth(yearBox, "50%");
        yearBox.addChangeHandler(changeHandler);

        yearBox.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                int keyCode = event.getNativeKeyCode();
                if (!Character.isDigit((char) keyCode) && (keyCode != (char) KeyCodes.KEY_TAB) && (keyCode != (char) KeyCodes.KEY_BACKSPACE)
                        && (keyCode != (char) KeyCodes.KEY_DELETE) && (keyCode != (char) KeyCodes.KEY_ENTER) && (keyCode != (char) KeyCodes.KEY_HOME)
                        && (keyCode != (char) KeyCodes.KEY_END) && (keyCode != (char) KeyCodes.KEY_LEFT) && (keyCode != (char) KeyCodes.KEY_UP)
                        && (keyCode != (char) KeyCodes.KEY_RIGHT) && (keyCode != (char) KeyCodes.KEY_DOWN)) {
                    ((TextBox) event.getSource()).cancelKey();
                }
            }
        });

    }

    public void setDate(Date date) {
        if (date == null) {
            monthSelector.setSelectedIndex(0);
            yearBox.setText("");
        } else {
            monthSelector.setSelectedIndex(date.getMonth());
            yearBox.setText(String.valueOf(date.getYear()));
        }
    }

    public Date getDate() {
        if (yearBox.getText() == null || yearBox.getText().trim().equals("")) {
            return null;
        }
        return new Date(Integer.parseInt(yearBox.getText()), monthSelector.getSelectedIndex() == 0 ? 1 : monthSelector.getSelectedIndex(), 1);
    }

    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return addDomHandler(handler, ChangeEvent.getType());
    }

    public void setEnabled(boolean enabled) {
        monthSelector.setEnabled(enabled);
        yearBox.setEnabled(enabled);
    }

    public void setFocus(boolean focused) {
        monthSelector.setFocus(focused);
        if (!focused) {
            yearBox.setFocus(false);
        }
    }

}
