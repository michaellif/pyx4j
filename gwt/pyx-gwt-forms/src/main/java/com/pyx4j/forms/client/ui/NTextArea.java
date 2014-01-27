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
 * Created on Jan 10, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.widgets.client.TextArea;

public class NTextArea extends NTextComponent<String, TextArea, CTextComponent<String, ?>> {

    private Integer visibleLines;

    public NTextArea(CTextArea cComponent) {
        super(cComponent);
    }

    @Override
    protected TextArea createEditor() {
        TextArea textArea = new TextArea();
        setWidth("100%");
        return textArea;
    }

    @Override
    protected HTML createViewer() {
        HTML viewer = super.createViewer();
        return viewer;
    }

    @Override
    public void setNativeValue(String value) {
        String newValue = value == null ? "" : value;
        if (isViewable()) {
            getViewer().setText(newValue);
        } else {
            if (!newValue.equals(getEditor().getText())) {
                getEditor().setText(newValue);
            }
        }
    }

    @Override
    public String getNativeValue() throws ParseException {
        return getEditor().getText();
    }

    @Override
    protected void onEditorInit() {
        super.onEditorInit();
        if (visibleLines != null) {
            getEditor().setVisibleLines(visibleLines);
        }
    }

    public void setVisibleLines(int visibleLines) {
        this.visibleLines = visibleLines;
    }

    public void scrollToBottom() {
        if (getEditor() != null) {
            //Workaround for initiation of "scrollHeight" - keep next line!!!
            DOM.getElementPropertyInt(getEditor().getElement(), "scrollHeight");
            DOM.setElementPropertyInt(getEditor().getElement(), "scrollTop", Integer.MAX_VALUE);
        }
    }

}