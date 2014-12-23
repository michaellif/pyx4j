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
 */
package com.pyx4j.widgets.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class CheckGroup<E> extends OptionGroup<E> {

    public CheckGroup(Layout layout) {
        super(layout, true);
    }

    public void setValue(Collection<E> value) {
        setValue(value, true);
    }

    public void setValue(Collection<E> value, boolean fireChangeEvent) {
        for (OptionGroupButton button : getButtons().values()) {
            button.setValue(Boolean.FALSE);
        }
        if (value != null) {
            for (E item : value) {
                OptionGroupButton selectedButton = getButtons().get(item);
                if (selectedButton != null) {
                    selectedButton.setValue(Boolean.TRUE);
                    if (fireChangeEvent) {
                        fireEvent(new ValueChangeEvent<Boolean>(fireChangeEvent) {
                        });
                    }
                }
            }
        }
        applySelectionStyles();
    }

    public Collection<E> getValue() {
        List<E> value = new ArrayList<>();
        for (E item : getButtons().keySet()) {
            if (getButtons().get(item).getValue()) {
                value.add(item);
            }
        }
        return value;
    }

}
