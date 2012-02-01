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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.Event;

import com.pyx4j.widgets.client.richtext.ExtendedRichTextArea;
import com.pyx4j.widgets.client.richtext.RichTextImageProvider;

public class NRichTextArea extends NTextComponent<String, ExtendedRichTextArea, CRichTextArea> {
    private static final Logger log = LoggerFactory.getLogger(NRichTextArea.class);

    public NRichTextArea(CRichTextArea textArea) {
        super(textArea);
        textArea.setWidth("100%");
        textArea.setHeight("20em");

        getElement().getStyle().setProperty("resize", "none");
        sinkEvents(Event.ONMOUSEOVER);
        sinkEvents(Event.ONMOUSEOUT);
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().setImageProvider(getCComponent().getImageProvider());
    }

    @Override
    protected ExtendedRichTextArea createEditor() {
        return new ExtendedRichTextArea();
    }

    public void scrollToBottom() {
        if (getEditor() != null) {
            getEditor().scrollToBottom();
        }
    }

    @Override
    public void setNativeValue(String value) {
        if (getEditor() != null) {
            getEditor().setText(value);
        }
    }

    @Override
    public String getNativeValue() {
        if (getEditor() != null) {
            log.debug("NativeValue = " + getEditor().getText());
            return getEditor().getText();
        } else {
            return "";
        }
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (getEditor() != null) {
            getEditor().onBrowserEvent(event);
        }
    }

    public void setImageProvider(RichTextImageProvider provider) {
        if (getEditor() != null) {
            getEditor().setImageProvider(provider);
        }
    }

}