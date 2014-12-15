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
 * Created on Jan 24, 2012
 * @author igor
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class RadioGroup<E> extends OptionGroup<E> {

    public RadioGroup(Layout layout) {
        super(layout, false);
    }

    public void setValue(E value) {
        setValue(value, false);
    }

    public void setValue(E value, boolean fireChangeEvent) {
        OptionGroupButton selectedButton = getButtons().get(value);
        if (selectedButton != null) {
            selectedButton.setValue(Boolean.TRUE);
            if (fireChangeEvent) {
                fireEvent(new ValueChangeEvent<Boolean>(fireChangeEvent) {
                });
            }
        } else {
            for (OptionGroupButton button : getButtons().values()) {
                button.setValue(Boolean.FALSE);
            }
        }

        applySelectionStyles();
    }

    public E getValue() {
        for (E value : getButtons().keySet()) {
            if (getButtons().get(value).getValue()) {
                return value;
            }
        }
        return null;
    }
}
