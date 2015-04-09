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
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.widgets.client.richtext.RichTextEditor;
import com.pyx4j.widgets.client.richtext.RichTextImageProvider;
import com.pyx4j.widgets.client.richtext.RichTextViewer;

public class NRichTextArea extends NValueBoxBase<String, RichTextEditor, CRichTextArea> {

    public NRichTextArea(CRichTextArea textArea) {
        super(textArea);
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
    protected RichTextEditor createEditor() {
        RichTextEditor area = new RichTextEditor();
        area.setAreaHeight("20em");
        return area;
    }

    @Override
    protected HTML createViewer() {
        return new RichTextViewer();
    }

    public void scrollToBottom() {
        if (getEditor() != null) {
            getEditor().scrollToBottom();
        }
    }

    @Override
    public void setNativeValue(String value) {
        if (isViewable()) {
            String v = value;
            if (v != null) {
                //VISTA-5364 - RTF editor inserts <blockquote> tags on text indent
                v = value.replace("<blockquote>", "<span style=\"padding: 0 40px\"></span>").replace("<blockquote/>", "");
            }
            getViewer().setHTML(v);
        } else {
            getEditor().setValue(value);
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