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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import com.pyx4j.widgets.client.richtext.ExtendedRichTextArea;
import com.pyx4j.widgets.client.richtext.RichTextImageProvider;

public class NRichTextArea extends NTextComponent<String, ExtendedRichTextArea, CRichTextArea> {

    public NRichTextArea(CRichTextArea textArea) {
        super(textArea);
        textArea.setWidth("100%");
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
        //Workaround for initiation of "scrollHeight" - keep next line!!!
        DOM.getElementPropertyInt(getElement(), "scrollHeight");
        DOM.setElementPropertyInt(getElement(), "scrollTop", Integer.MAX_VALUE);
    }

    static String trimHtml(String html) {
        while (html.startsWith("<br>")) {
            html = html.substring(4).trim();
        }
        while (html.endsWith("<br>")) {
            html = html.substring(0, html.length() - 4).trim();
        }
        // make all tags lower case as in JTidy
        StringBuilder b = new StringBuilder();
        boolean tag = false;
        boolean cr = false;
        for (char part : html.toCharArray()) {
            if (part == '<') {
                tag = true;
                cr = false;
            } else if (tag) {
                if (((part >= 'A') && (part <= 'Z')) || (part == '/')) {
                    part = Character.toLowerCase(part);
                } else {
                    tag = false;
                }
                cr = false;
            } else if ((part == '\r') || (part == '\n')) {
                cr = true;
                continue;
            } else if (cr && (part != ' ')) {
                b.append(' ');
                cr = false;
            }
            b.append(part);
        }
        html = b.toString();
        return html.replaceAll("<br>", "<br />");
    }

    @Override
    public void setNativeValue(String value) {
        String newValue = value == null ? "" : value;
// TODO        
//        if (!newValue.equals(richTextArea.getHTML())) {
//            richTextArea.setHTML(newValue);
//        }

        getEditor();
    }

    @Override
    public String getNativeValue() {
        return "";//TODO trimHtml(richTextArea.getHTML());
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        switch (event.getTypeInt()) {
        case Event.ONMOUSEOUT:
//TODO            toolbar.getElement().getStyle().setOpacity(0.3);
            break;
        case Event.ONMOUSEOVER:
//            toolbar.getElement().getStyle().setOpacity(1);
            break;
        }
    }

    public void setImageProvider(RichTextImageProvider provider) {
        if (getEditor() != null) {
            getEditor().setImageProvider(provider);
        }
    }

}