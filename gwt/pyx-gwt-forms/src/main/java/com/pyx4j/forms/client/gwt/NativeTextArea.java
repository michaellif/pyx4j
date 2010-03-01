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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.DOM;

import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.forms.client.ui.INativeEditableComponent;
import com.pyx4j.widgets.client.TextArea;

public class NativeTextArea extends TextArea implements INativeEditableComponent<String> {

    private final CTextArea textArea;

    public NativeTextArea(final CTextArea textArea) {
        super();
        this.textArea = textArea;
        addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                textArea.setValue(getText());
            }
        });
        setTabIndex(textArea.getTabIndex());

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

    public void setNativeValue(String value) {
        String newValue = value == null ? "" : value;
        if (!newValue.equals(getText())) {
            setText(newValue);
        }
    }

    public CTextArea getCComponent() {
        return textArea;
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

}