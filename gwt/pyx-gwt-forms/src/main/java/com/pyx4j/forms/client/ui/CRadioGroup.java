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
 * Created on 2010-04-22
 * @author vlads
 */
package com.pyx4j.forms.client.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.forms.client.events.HasOptionsChangeHandlers;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.widgets.client.RadioGroup;

public abstract class CRadioGroup<E> extends CFocusComponent<E, NRadioGroup<E>> implements HasOptionsChangeHandlers<List<E>> {

    private final RadioGroup.Layout layout;

    private IFormatter<E, String> format;

    private List<E> options;

    public CRadioGroup(RadioGroup.Layout layout) {
        super();
        this.layout = layout;
    }

    public RadioGroup.Layout getLayout() {
        return layout;
    }

    @SuppressWarnings("serial")
    private List<E> createOptionsImpl() {
        return new ArrayList<E>() {
            @SuppressWarnings("unchecked")
            @Override
            public int indexOf(Object o) {
                for (int i = 0; i < this.size(); i++) {
                    if (isValuesEqual(this.get(i), (E) o)) {
                        return i;
                    }
                }
                return -1;
            }
        };
    }

    public List<E> getOptions() {
        if (options == null) {
            options = createOptionsImpl();
        }
        return options;
    }

    public void setOptions(Collection<E> opt) {
        if (options == null) {
            options = createOptionsImpl();
        }
        options.clear();
        if (opt != null) {
            options.addAll(opt);
        }
        getNativeComponent().setOptions(getOptions());
        OptionsChangeEvent.fire(this, getOptions());
    }

    @Override
    public HandlerRegistration addOptionsChangeHandler(OptionsChangeHandler<List<E>> handler) {
        return addHandler(handler, OptionsChangeEvent.getType());
    }

    public final IFormatter<E, String> getFormat() {
        return format;
    }

    public final void setFormat(IFormatter<E, String> format) {
        this.format = format;
    }

}
