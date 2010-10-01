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

import com.pyx4j.forms.client.gwt.NativeSuggestBox;

public class CSuggestBox<E> extends CTextBox<E> {

    private ArrayList<E> options = new ArrayList<E>();

    public CSuggestBox() {
        this(null);
    }

    public CSuggestBox(String title) {
        super(title);
        setWidth("100%");
    }

    @Override
    public INativeEditableComponent<E> initNativeComponent() {
        if (nativeTextField == null) {
            nativeTextField = new NativeSuggestBox<E>(this);
            applyAccessibilityRules();
            setOptions(options);
            setNativeComponentValue(getValue());
        }
        return nativeTextField;
    }

    public void setOptions(Collection<E> options) {
        this.options = new ArrayList<E>();
        this.options.clear();
        if (options == null) {
            return;
        }
        E currentSelection = getValue();
        this.options.addAll(options);
        if (nativeTextField != null) {
            ((NativeSuggestBox<E>) nativeTextField).removeAllItems();
            for (E option : options) {
                ((NativeSuggestBox<E>) nativeTextField).addItem(getOptionName(option));
            }
            setValue(currentSelection);
        }
    }

    public Collection<E> getOptions() {
        return options;
    }

    public String getOptionName(Object o) {
        if (o == null) {
            return "-- NULL --";
        } else {
            return o.toString();
        }
    }

}
