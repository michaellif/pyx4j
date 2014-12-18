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
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;

import com.pyx4j.forms.client.events.HasNativeValueChangeHandlers;
import com.pyx4j.forms.client.events.NativeValueChangeEvent;
import com.pyx4j.forms.client.events.NativeValueChangeHandler;

public abstract class CTextFieldBase<DATA, WIDGET extends INativeValueBox<DATA>> extends CTextComponent<DATA, WIDGET> implements IAcceptsText,
        HasNativeValueChangeHandlers<String> {

    public CTextFieldBase() {
        super();
    }

    public void requestFocus() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                INativeField<DATA> impl = getNativeComponent();
                if (impl instanceof FocusWidget) {
                    ((FocusWidget) impl).setFocus(true);
                }
            }
        });
    }

    @Override
    public boolean isValuesEqual(DATA value1, DATA value2) {
        //This takes in consideration that 2 values can be null but actual state of component is not empty after failed parsing
        return value1 != null && value1 == value2;
    }

    /**
     * Native component value change event handler, (Real Time) as opposite to CComponent
     */
    @Override
    public HandlerRegistration addNativeValueChangeHandler(NativeValueChangeHandler<String> handler) {
        return addHandler(handler, NativeValueChangeEvent.getType());
    }

}
