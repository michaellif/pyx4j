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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DockPanel;

import com.pyx4j.widgets.client.RichTextArea;
import com.pyx4j.widgets.client.richtext.BasikRichTextToolbar;

public class NativeRichTextArea extends DockPanel implements INativeRichTextComponent<String> {

    private final RichTextArea richTextArea;

    private final CRichTextArea textArea;

    private final BasikRichTextToolbar toolbar;

    public NativeRichTextArea(CRichTextArea textArea) {
        super();
        this.textArea = textArea;

        richTextArea = new RichTextArea();
        richTextArea.setWidth("100%");
        richTextArea.setHeight("100%");

        textArea.setWidth("100%");

        toolbar = new BasikRichTextToolbar(richTextArea, true);
        toolbar.getElement().getStyle().setMarginLeft(2, Unit.PX);

        add(toolbar, NORTH);
        add(richTextArea, CENTER);
        setCellHeight(richTextArea, "100%");
        setCellWidth(richTextArea, "100%");

        getElement().getStyle().setProperty("resize", "none");

        setTabIndex(textArea.getTabIndex());

        toolbar.getElement().getStyle().setOpacity(0.3);

        sinkEvents(Event.ONMOUSEOVER);
        sinkEvents(Event.ONMOUSEOUT);

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
        richTextArea.setEnabled(editable);
    }

    @Override
    public boolean isEditable() {
        return richTextArea.isEnabled();
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
        if (!newValue.equals(richTextArea.getHTML())) {
            richTextArea.setHTML(newValue);
        }
    }

    @Override
    public String getNativeValue() {
        return trimHtml(richTextArea.getHTML());
    }

    @Override
    public CRichTextArea getCComponent() {
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

    @Override
    public void setFocus(boolean focused) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTabIndex(int tabIndex) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        switch (event.getTypeInt()) {
        case Event.ONMOUSEOUT:
            toolbar.getElement().getStyle().setOpacity(0.3);
            break;
        case Event.ONMOUSEOVER:
            toolbar.getElement().getStyle().setOpacity(1);
            break;
        }
    }

    @Override
    public void setWatermark(String watermark) {
        throw new NotImplementedException();
    }

    @Override
    public void setValid(boolean valid) {
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        return richTextArea.addFocusHandler(focusHandler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        return richTextArea.addBlurHandler(blurHandler);
    }

}