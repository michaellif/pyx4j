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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockPanel;

import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.INativeEditableComponent;
import com.pyx4j.widgets.client.RichTextArea;
import com.pyx4j.widgets.client.richtext.VerticalRichTextToolbar;

public class NativeRichTextArea extends DockPanel implements INativeEditableComponent<String> {

    private final RichTextArea richTextArea;

    private final CRichTextArea textArea;

    private boolean nativeValueUpdate = false;

    private final VerticalRichTextToolbar toolbar;

    private final Timer keyTimer = new Timer() {
        @Override
        public void run() {
            nativeValueUpdate();
        }
    };

    public NativeRichTextArea(CRichTextArea textArea) {
        super();
        this.textArea = textArea;

        richTextArea = new RichTextArea();
        richTextArea.setWidth("100%");

        textArea.setWidth("100%");

        toolbar = new VerticalRichTextToolbar(richTextArea);
        toolbar.getElement().getStyle().setMarginLeft(2, Unit.PX);
        toolbar.setHeight("100%");

        add(toolbar, EAST);
        setCellHeight(toolbar, "100%");
        add(richTextArea, CENTER);
        setCellWidth(richTextArea, "100%");

        getElement().getStyle().setProperty("resize", "none");

        richTextArea.addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                keyTimer.cancel();
                keyTimer.schedule(500);
            }
        });

        richTextArea.addBlurHandler(new BlurHandler() {

            @Override
            public void onBlur(BlurEvent event) {
                nativeValueUpdate();
            }
        });

        setTabIndex(textArea.getTabIndex());

        setWidth(textArea.getWidth());
        setHeight(textArea.getHeight());

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
            textArea.setValue(trimHtml(richTextArea.getHTML()));
        } finally {
            nativeValueUpdate = false;
        }
    }

    protected String trimHtml(String html) {
        while (html.startsWith("<br>")) {
            html = html.substring(4).trim();
        }
        while (html.endsWith("<br>")) {
            html = html.substring(0, html.length() - 4).trim();
        }
        return html.replaceAll("<br>", "<br />").replaceAll("\r", "").replaceAll("\n", "");
    }

    @Override
    public void setNativeValue(String value) {
        if (nativeValueUpdate) {
            return;
        }
        String newValue = value == null ? "" : value;
        if (!newValue.equals(richTextArea.getHTML())) {
            richTextArea.setHTML(newValue);
        }
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

}