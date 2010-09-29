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
package com.pyx4j.forms.client.gwt;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;

import com.pyx4j.commons.ConverterUtils.ToStringConverter;
import com.pyx4j.forms.client.ui.CSuggestBox;
import com.pyx4j.forms.client.ui.INativeEditableComponent;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.style.CSSClass;

public class NativeSuggestBox<E> extends SuggestBox implements INativeEditableComponent<E> {

    private final CSuggestBox<E> cSuggestBox;

    public NativeSuggestBox(CSuggestBox<E> cSuggestBox) {
        super(new MultiWordSuggestOracle(), new TextBox());
        this.cSuggestBox = cSuggestBox;

        setStyleName(CSSClass.pyx4j_TextBox.name());

        setWidth(cSuggestBox.getWidth());
        setHeight(cSuggestBox.getHeight());

        setTabIndex(cSuggestBox.getTabIndex());

    }

    @Override
    public void setEditable(boolean editable) {
        ((TextBox) getWidget()).setEnabled(editable);
        ((TextBox) getWidget()).setReadOnly(!editable);
    }

    @Override
    public boolean isEditable() {
        return !((TextBox) getWidget()).isReadOnly();
    }

    @Override
    public void setEnabled(boolean enabled) {
        ((TextBox) getWidget()).setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return ((TextBox) getWidget()).isEnabled();
    }

    private class StringConverter implements ToStringConverter<Object> {

        @Override
        public String toString(Object value) {
            return "\n" + cSuggestBox.getOptionName(value);
        }
    }

    public void addItem(String optionName) {
        ((MultiWordSuggestOracle) getSuggestOracle()).add(optionName);
    }

    public void removeAllItems() {
        ((MultiWordSuggestOracle) getSuggestOracle()).clear();
    }

    @Override
    public void setNativeValue(Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public CSuggestBox getCComponent() {
        return cSuggestBox;
    }

}
