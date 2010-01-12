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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Timer;

import com.pyx4j.forms.client.ui.CTextBox;
import com.pyx4j.forms.client.ui.INativeTextComponent;

public class NativeTextBoxDelegate<E> {

    private final CTextBox<E> cTextBox;

    private final INativeTextComponent<E> nativeTextBox;

    private boolean nativeValueUpdate = false;

    private final Timer keyTimer = new Timer() {
        @Override
        public void run() {
            nativeValueUpdate();
        }
    };

    public NativeTextBoxDelegate(final INativeTextComponent<E> nativeTextBox, final CTextBox<E> cTextField) {
        super();
        this.nativeTextBox = nativeTextBox;
        this.cTextBox = cTextField;

        nativeTextBox.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                keyTimer.cancel();
                nativeValueUpdate();
            }
        });

        nativeTextBox.addKeyUpHandler(new KeyUpHandler() {

            public void onKeyUp(KeyUpEvent event) {
                keyTimer.cancel();
                keyTimer.schedule(500);
            }
        });

        nativeTextBox.addFocusHandler(new FocusHandler() {
            public void onFocus(FocusEvent event) {
                cTextField.onEditingStart();
            }
        });

        nativeTextBox.addBlurHandler(new BlurHandler() {
            public void onBlur(BlurEvent event) {
                nativeValueUpdate();
                cTextField.onEditingStop();
            }
        });

        nativeTextBox.setTabIndex(cTextField.getTabIndex());

        nativeTextBox.setWidth(cTextField.getWidth());
        nativeTextBox.setHeight(cTextField.getHeight());
        setValue(cTextField.getValue());
    }

    /**
     * Prevents setting wrong value once the value has been Set Externally
     */
    void cancelScheduledUpdate() {
        keyTimer.cancel();
    }

    private void nativeValueUpdate() {
        // Prevents setting the native value while propagating value from native component to CComponent
        nativeValueUpdate = true;
        try {
            cTextBox.setValue(cTextBox.getFormat().parse(nativeTextBox.getNativeText()));
        } finally {
            nativeValueUpdate = false;
        }
    }

    public void setValue(E value) {
        if (nativeValueUpdate) {
            return;
        }
        String newValue = value == null ? "" : cTextBox.getFormat().format(value);
        cancelScheduledUpdate();
        if (!newValue.equals(nativeTextBox.getNativeText())) {
            nativeTextBox.setNativeText(newValue);
        }
    }

    public CTextBox<E> getCComponent() {
        return cTextBox;
    }

}
