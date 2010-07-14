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

public class CSuggestBox extends CEditableComponent<Object> {

    private NativeSuggestBox nativeSuggestBox;

    private ArrayList<Object> options = new ArrayList<Object>();

    public CSuggestBox() {
        this(null);
    }

    public CSuggestBox(String title) {
        super(title);
    }

    @Override
    public NativeSuggestBox getNativeComponent() {
        return nativeSuggestBox;
    }

    @Override
    public NativeSuggestBox initNativeComponent() {
        if (nativeSuggestBox == null) {
            nativeSuggestBox = new NativeSuggestBox(this);
            applyAccessibilityRules();
            setOptions(options);
            setNativeComponentValue(getValue());
        }
        return nativeSuggestBox;
    }

    public void setOptions(Collection options) {
        this.options = new ArrayList<Object>();
        this.options.clear();
        if (options == null) {
            return;
        }
        Object currentSelection = getValue();
        this.options.addAll(options);
        if (nativeSuggestBox != null) {
            nativeSuggestBox.removeAllItems();
            for (Object option : options) {
                nativeSuggestBox.addItem(getOptionName(option));
            }
            setValue(currentSelection);
        }
    }

    public Collection getOptions() {
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
