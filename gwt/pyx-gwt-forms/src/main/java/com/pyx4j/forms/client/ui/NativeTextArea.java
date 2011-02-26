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

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;

import com.pyx4j.widgets.client.TextArea;

public class NativeTextArea extends TextArea implements INativeEditableComponent<String> {

    private final CTextArea cComponent;

    private boolean nativeValueUpdate = false;

    private final Timer keyTimer = new Timer() {
        @Override
        public void run() {
            nativeValueUpdate();
        }
    };

    public NativeTextArea(CTextArea textArea) {
        super();
        this.cComponent = textArea;

        getElement().getStyle().setProperty("resize", "none");

        addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                keyTimer.cancel();
                nativeValueUpdate();
            }
        });

        addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                keyTimer.cancel();
                keyTimer.schedule(CTextComponent.PARSINT_PERIOD);
            }
        });

        //        addFocusHandler(new FocusHandler() {
        //            public void onFocus(FocusEvent event) {
        //                //textArea.onEditingStart();
        //            }
        //        });

        addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                nativeValueUpdate();
                //textArea.onEditingStop();
            }
        });

        setTabIndex(textArea.getTabIndex());

        setWidth(textArea.getWidth());
        setHeight(textArea.getHeight());

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void setEditable(boolean editable) {
        super.setReadOnly(!editable);
    }

    @Override
    public boolean isEditable() {
        return !super.isReadOnly();
    }

    public void scrollToBottom() {
        //Workaround for initiation of "scrollHeight" - keep next line!!!
        DOM.getElementPropertyInt(getElement(), "scrollHeight");
        DOM.setElementPropertyInt(getElement(), "scrollTop", Integer.MAX_VALUE);
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
            cComponent.setValueByNativeComponent(getText());
        } finally {
            nativeValueUpdate = false;
        }
    }

    @Override
    public void setNativeValue(String value) {
        if (nativeValueUpdate) {
            return;
        }
        String newValue = value == null ? "" : value;
        if (!newValue.equals(getText())) {
            setText(newValue);
        }
    }

    @Override
    public CTextArea getCComponent() {
        return cComponent;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        DomDebug.attachedWidget();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        DomDebug.detachWidget();
    }

    @Override
    public void setValid(boolean valid) {
    }

}