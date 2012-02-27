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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;

import com.pyx4j.widgets.client.ITextWidget;
import com.pyx4j.widgets.client.RichTextArea;
import com.pyx4j.widgets.client.richtext.ExtendedRichTextToolbar.RichTextAction;

public class ExtendedRichTextArea extends DockPanel implements ITextWidget {
    private final RichTextArea richTextArea;

    private final ExtendedRichTextToolbar toolbar;

    private boolean editable;

    private boolean textMode;

    public ExtendedRichTextArea() {
        super();

        richTextArea = new RichTextArea();
        richTextArea.setWidth("100%");
        richTextArea.setHeight("100%");

        toolbar = new ExtendedRichTextToolbar(richTextArea);
        toolbar.getElement().getStyle().setMarginLeft(2, Unit.PX);

        HorizontalPanel toolPanel = new HorizontalPanel();
        toolPanel.add(toolbar);
        ToggleButton textHtmlSwitch = new ToggleButton("HTML", "RichText");
        textHtmlSwitch.setTitle("Toggle HTML or Text mode");
        textHtmlSwitch.setWidth("60px");
        textHtmlSwitch.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (((ToggleButton) event.getSource()).isDown()) {
                    richTextArea.setText(richTextArea.getHTML());
                    textMode = false;
                    toolbar.getElement().getStyle().setVisibility(Visibility.HIDDEN);
                } else {
                    richTextArea.setHTML(richTextArea.getText());
                    textMode = true;
                    toolbar.getElement().getStyle().setVisibility(Visibility.VISIBLE);
                }
            }
        });
        /*
         * When this button gets focus the textarea will ignore blur event and therefore
         * will not update component value with any changes that were made.
         * So, we need to fire onBlur for textarea to save changes when focus moves somewhere else.
         */
        textHtmlSwitch.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                richTextArea.fireEvent(event);
            }
        });
        toolPanel.setVerticalAlignment(ALIGN_BOTTOM);
        toolPanel.setHorizontalAlignment(ALIGN_RIGHT);
        toolPanel.add(textHtmlSwitch);
        toolPanel.setWidth("100%");

        add(toolPanel, NORTH);
        add(richTextArea, CENTER);
        setCellHeight(richTextArea, "100%");
        setCellWidth(richTextArea, "100%");

        getElement().getStyle().setProperty("resize", "none");

        toolbar.getElement().getStyle().setOpacity(0.3);

        sinkEvents(Event.ONMOUSEOVER);
        sinkEvents(Event.ONMOUSEOUT);

        editable = true;
        textMode = true;
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

    public PushButton getCustomButton() {
        return toolbar.getCustomButton();
    }

    public void setCustomAction(RichTextAction action) {
        toolbar.setCustomAction(action);
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
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setTabIndex(int index) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAccessKey(char key) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFocus(boolean focused) {
        richTextArea.setFocus(focused);
    }

    @Override
    public String getText() {
        return trimHtml(textMode ? richTextArea.getHTML() : richTextArea.getText());
    }

    @Override
    public void setText(String html) {
        if (textMode) {
            richTextArea.setHTML(html);
        } else {
            richTextArea.setText(html);
        }
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }
}