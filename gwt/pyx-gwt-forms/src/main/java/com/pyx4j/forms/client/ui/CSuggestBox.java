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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.forms.client.events.HasOptionsChangeHandlers;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;

public class CSuggestBox<E> extends CTextFieldBase<E, NativeSuggestBox<E>> implements HasOptionsChangeHandlers<List<E>> {

    private List<E> options = new ArrayList<E>();

    public CSuggestBox() {
        this(null);
    }

    public CSuggestBox(String title) {
        super(title);
        setWidth("100%");
    }

    @Override
    protected NativeSuggestBox<E> initWidget() {
        NativeSuggestBox<E> nativeTextField = new NativeSuggestBox<E>(this);
        setOptions(options);
        return nativeTextField;
    }

    @Override
    public HandlerRegistration addOptionsChangeHandler(OptionsChangeHandler<List<E>> handler) {
        return addHandler(handler, OptionsChangeEvent.getType());
    }

    public void setOptions(Collection<E> opt) {
        this.options = new ArrayList<E>();
        this.options.clear();
        if (opt != null) {
            E currentSelection = getValue();
            this.options.addAll(opt);
            if (isWidgetCreated()) {
                asWidget().removeAllItems();
                for (E option : opt) {
                    asWidget().addItem(getOptionName(option));
                }
                setValue(currentSelection);
            }
        }
        OptionsChangeEvent.fire(this, getOptions());
    }

    public List<E> getOptions() {
        return options;
    }

    public String getOptionName(E o) {
        if (o == null) {
            return "-- NULL --";
        } else {
            return o.toString();
        }
    }

}
