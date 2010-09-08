/*
 * Copyright 2007 Google Inc.
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
 */
package com.pyx4j.widgets.client.richtext;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;

/**
 * A sample toolbar for use with {@link RichTextArea}. It provides a simple UI for all
 * rich text formatting, dynamically displayed only for the available functionality.
 */
public class BasikRichTextToolbar extends Composite {

    private final WidgetsImageBundle images = ImageFactory.getImages();

    private final EventHandler handler = new EventHandler();

    private final RichTextArea richText;

    private final RichTextArea.Formatter formatter;

    private final Panel toolbar;

    private ToggleButton bold;

    private ToggleButton italic;

    private PushButton indent;

    private PushButton outdent;

    private PushButton justifyLeft;

    private PushButton justifyCenter;

    private PushButton justifyRight;

    private PushButton hr;

    private PushButton ol;

    private PushButton ul;

    private final boolean horizontalLayout;

    /**
     * Creates a new toolbar that drives the given rich text area.
     * 
     * @param richText
     *            the rich text area to be controlled
     */
    public BasikRichTextToolbar(RichTextArea richText, boolean horizontalLayout) {
        this.horizontalLayout = horizontalLayout;
        if (horizontalLayout) {
            toolbar = new HorizontalPanel();
        } else {
            toolbar = new VerticalPanel();
        }
        this.richText = richText;
        this.formatter = richText.getFormatter();

        initWidget(toolbar);
        setStyleName("gwt-RichTextToolbar");

        toolbar.add(bold = createToggleButton(images.bold(), images.boldToggled(), "bold"));
        toolbar.add(italic = createToggleButton(images.italic(), images.italicToggled(), "italic"));

        toolbar.add(indent = createPushButton(images.indent(), "indent"));
        toolbar.add(outdent = createPushButton(images.outdent(), "outdent"));
        toolbar.add(hr = createPushButton(images.hr(), "hr"));
        toolbar.add(ol = createPushButton(images.ol(), "ol"));
        toolbar.add(ul = createPushButton(images.ul(), "ul"));

        // We only use these listeners for updating status, so don't hook them up
        // unless at least basic editing is supported.
        richText.addKeyUpHandler(handler);
        richText.addClickHandler(handler);
    }

    private PushButton createPushButton(ImageResource img, String tip) {
        PushButton pb = new PushButton(new Image(img));
        pb.addClickHandler(handler);
        pb.setTitle(tip);
        if (horizontalLayout) {
            pb.getElement().getStyle().setPadding(3, Unit.PX);
        }
        return pb;
    }

    private ToggleButton createToggleButton(ImageResource upImg, ImageResource downImg, String tip) {
        ToggleButton tb = new ToggleButton(new Image(upImg), new Image(downImg));
        tb.addClickHandler(handler);
        tb.setTitle(tip);
        if (horizontalLayout) {
            tb.getElement().getStyle().setPadding(3, Unit.PX);
        }
        return tb;
    }

    /**
     * Updates the status of all the stateful buttons.
     */
    private void updateStatus() {
        bold.setDown(formatter.isBold());
        italic.setDown(formatter.isItalic());
    }

    private class EventHandler implements ClickHandler, ChangeHandler, KeyUpHandler {

        public void onChange(ChangeEvent event) {
        }

        public void onClick(ClickEvent event) {
            Object sender = event.getSource();
            if (sender == bold) {
                formatter.toggleBold();
            } else if (sender == italic) {
                formatter.toggleItalic();
            } else if (sender == indent) {
                formatter.rightIndent();
            } else if (sender == outdent) {
                formatter.leftIndent();
            } else if (sender == justifyLeft) {
                formatter.setJustification(RichTextArea.Justification.LEFT);
            } else if (sender == justifyCenter) {
                formatter.setJustification(RichTextArea.Justification.CENTER);
            } else if (sender == justifyRight) {
                formatter.setJustification(RichTextArea.Justification.RIGHT);
            } else if (sender == hr) {
                formatter.insertHorizontalRule();
            } else if (sender == ol) {
                formatter.insertOrderedList();
            } else if (sender == ul) {
                formatter.insertUnorderedList();
            } else if (sender == richText) {
                // We use the RichTextArea's onKeyUp event to update the toolbar status.
                // This will catch any cases where the user moves the cursur using the
                // keyboard, or uses one of the browser's built-in keyboard shortcuts.
                updateStatus();
            }
        }

        public void onKeyUp(KeyUpEvent event) {
            if (event.getSource() == richText) {
                // We use the RichTextArea's onKeyUp event to update the toolbar status.
                // This will catch any cases where the user moves the cursur using the
                // keyboard, or uses one of the browser's built-in keyboard shortcuts.
                updateStatus();
            }
        }
    }
}
