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
package com.pyx4j.forms.client.gwt;

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMonthYearPicker;
import com.pyx4j.forms.client.ui.INativeEditableComponent;
import com.pyx4j.widgets.client.MonthYearPicker;

public class NativeMonthYearPicker extends MonthYearPicker implements INativeEditableComponent<Date> {

    private boolean enabled = true;

    private boolean editable = true;

    private final CMonthYearPicker cComponent;

    private boolean nativeValueUpdate = false;

    public NativeMonthYearPicker(CMonthYearPicker cComponent) {
        this.cComponent = cComponent;

        addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                nativeValueUpdate();
            }
        });

        setTabIndex(cComponent.getTabIndex());
        setDate(cComponent.getValue());
    }

    private void nativeValueUpdate() {
        // Prevents setting the native value while propagating value from native component to CComponent
        nativeValueUpdate = true;
        try {
            cComponent.setValue(getDate());
        } finally {
            nativeValueUpdate = false;
        }
    }

    @Override
    public void setDate(Date date) {
        if (nativeValueUpdate) {
            return;
        }
        super.setDate(date);
    }

    @Override
    public void setNativeValue(Date value) {
        setDate(value);
    }

    @Override
    public void setTabIndex(int tabIndex) {
        // TODO Auto-generated method stub

    }

    @Override
    public CComponent<?> getCComponent() {
        return cComponent;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        super.setEnabled(enabled && this.isEditable());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        super.setEnabled(editable && this.isEnabled());
    }

    @Override
    public boolean isEditable() {
        return editable;
    }
}
