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
package com.pyx4j.widgets.client.richtext;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.widgets.client.IValueBoxWidget;

public class RichTextEditor extends FlowPanel implements IValueBoxWidget<String> {

    private final RichTextArea richTextArea;

    private final RichTextToolbar toolbar;

    private boolean editable;

    public RichTextEditor() {
        super();

        setStyleName(RichTextEditorTheme.StyleName.ReachTextEditor.name());

        richTextArea = new RichTextArea();
        richTextArea.setWidth("100%");
        richTextArea.setHeight("15em");

        toolbar = new RichTextToolbar(richTextArea);

        add(toolbar);
        add(richTextArea);

        toolbar.getElement().getStyle().setOpacity(0.3);

        sinkEvents(Event.ONMOUSEOVER);
        sinkEvents(Event.ONMOUSEOUT);

        editable = true;
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

    public void setTemplateAction(RichTextTemplateAction action) {
        toolbar.setTemplateAction(action);
    }

    public void setImageProvider(RichTextImageProvider provider) {
        toolbar.setImageProvider(provider);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        /*
         * This is needed to help handling richTextArea onBlur events. When toolbar is inOperation state
         * it may open other dialogs that may have focusable components. When those components receive
         * focus it should not fire onBlur for the editor (see RichTextArea#ignoreBlur())
         */
        if (toolbar.inOperation()) {
            return;
        }
        switch (event.getTypeInt()) {
        case Event.ONMOUSEOUT:
            toolbar.getElement().getStyle().setOpacity(0.3);
            richTextArea.ignoreBlur(false);
            break;
        case Event.ONMOUSEOVER:
            toolbar.getElement().getStyle().setOpacity(1);
            richTextArea.ignoreBlur(true);
            break;
        }
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return richTextArea.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return richTextArea.addBlurHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return null;
    }

    @Override
    public void setEnabled(boolean enabled) {
        richTextArea.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return richTextArea.isEnabled();
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public int getTabIndex() {
        return richTextArea.getTabIndex();
    }

    @Override
    public void setTabIndex(int index) {
        richTextArea.setTabIndex(index);
    }

    @Override
    public void setAccessKey(char key) {
        richTextArea.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        richTextArea.setFocus(focused);
    }

    @Override
    public String getValue() {
        return trimHtml(toolbar.isHtmlMode() ? richTextArea.getHTML() : richTextArea.getText());
    }

    @Override
    public void setValue(String html) {
        if (toolbar.isHtmlMode()) {
            richTextArea.setHTML(html);
        } else {
            richTextArea.setText(html);
        }
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        ensureDebugId(debugId.debugId());
    }

    @Override
    public void setWatermark(String watermark) {

    }

    @Override
    public String getWatermark() {
        return null;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void setParser(IParser<String> parser) {

    }

    @Override
    public void setFormatter(IFormatter<String, String> formatter) {

    }

    @Override
    public boolean isParsedOk() {
        return false;
    }

    @Override
    public String getParseExceptionMessage() {
        return null;
    }

    public void setAreaHeight(String string) {
        richTextArea.setHeight(string);
    }

}