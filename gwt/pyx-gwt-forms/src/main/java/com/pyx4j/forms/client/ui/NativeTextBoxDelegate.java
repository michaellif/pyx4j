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

import java.text.ParseException;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;

public class NativeTextBoxDelegate<E> {

    private final CTextFieldBase<E, ?> cTextBox;

    private final INativeTextComponent<E> nativeTextBox;

    private boolean parseFailed = false;

    public NativeTextBoxDelegate(final INativeTextComponent<E> nativeTextBox, final CTextFieldBase<E, ?> cTextField) {
        super();
        this.nativeTextBox = nativeTextBox;
        this.cTextBox = cTextField;

        //In dialogs or Forms the KeyDown will submit the form. We need values to be there.
        nativeTextBox.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    cTextBox.onEditingStop();
                }
            }
        });

        nativeTextBox.setTabIndex(cTextField.getTabIndex());

        nativeTextBox.setWidth(cTextField.getWidth());
        nativeTextBox.setHeight(cTextField.getHeight());
        setNativeValue(cTextField.getValue());
    }

    public void setNativeValue(E value) {
        String newValue = value == null ? "" : cTextBox.getFormat().format(value);
        if (!newValue.equals(nativeTextBox.getNativeText())) {
            nativeTextBox.setNativeText(newValue);
        }
    }

    public E getNativeValue() {
        try {
            parseFailed = false;
            return cTextBox.getFormat().parse(nativeTextBox.getNativeText());
        } catch (ParseException e) {
            parseFailed = true;
            return null;
        }
    }

    public boolean isParseFailed() {
        return parseFailed;
    }

    public CTextFieldBase<E, ?> getCComponent() {
        return cTextBox;
    }

}
