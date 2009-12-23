/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.richtext.RichTextEditorDecorator;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetsDemo implements EntryPoint {

    public void onModuleLoad() {

        VerticalPanel contentPanel = new VerticalPanel();
        RootPanel.get().add(contentPanel);

        final Button sendButton = new Button("Send");
        final TextBox pageNameTextBox = new TextBox();
        pageNameTextBox.setText("page1");

        final RichTextArea pageEditor = new RichTextArea();

        RichTextEditorDecorator editorDecorator = new RichTextEditorDecorator(pageEditor);

        final TextArea htmlViewer = new TextArea();

        // We can add style names to widgets
        sendButton.addStyleName("sendButton");

        contentPanel.add(pageNameTextBox);
        contentPanel.add(editorDecorator);
        contentPanel.add(sendButton);
        contentPanel.add(htmlViewer);

        // Focus the cursor on the name field when the app loads
        pageNameTextBox.setFocus(true);
        pageNameTextBox.selectAll();

        class MyHandler implements ClickHandler {
            public void onClick(ClickEvent event) {
                htmlViewer.setText(pageEditor.getHTML());
            }
        }

        MyHandler handler = new MyHandler();
        sendButton.addClickHandler(handler);
    }
}
