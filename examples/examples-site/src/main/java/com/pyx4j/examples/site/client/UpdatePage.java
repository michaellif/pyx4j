/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 20, 2009
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.examples.site.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.site.admin.*;
import com.pyx4j.site.admin.PageServiceAsync;

public class UpdatePage extends VerticalPanel {

    private final PageServiceAsync pageService = GWT.create(PageService.class);

    public UpdatePage() {
        final Button showButton = new Button("Show");
        final TextBox nameField = new TextBox();
        nameField.setText("page1");

        final HTML page = new HTML();

        // We can add style names to widgets
        showButton.addStyleName("sendButton");

        add(nameField);
        add(showButton);
        add(page);

        // Focus the cursor on the name field when the app loads
        nameField.setFocus(true);
        nameField.selectAll();

        // Create a handler for the sendButton and nameField
        class MyHandler implements ClickHandler, KeyUpHandler {
            /**
             * Fired when the user clicks on the sendButton.
             */
            public void onClick(ClickEvent event) {
                refresh();
            }

            /**
             * Fired when the user types in the nameField.
             */
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    refresh();
                }
            }

            /**
             * Send the name from the nameField to the server and wait for a response.
             */
            private void refresh() {
                pageService.loadPageHtml(nameField.getText(), new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        page.setText(caught.getMessage());
                    }

                    public void onSuccess(String result) {
                        page.setHTML(result);
                    }
                });
            }
        }

        // Add a handler to send the name to the server
        MyHandler handler = new MyHandler();
        showButton.addClickHandler(handler);
        nameField.addKeyUpHandler(handler);
    }

}
