package com.pyx4j.site.admin;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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

import com.pyx4j.ria.client.ApplicationManager;
import com.pyx4j.widgets.client.richtext.RichTextEditorDecorator;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Client implements EntryPoint {

    private final PageServiceAsync pageService = GWT.create(PageService.class);

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        ApplicationManager.loadApplication(new AdminApplication());

        //simple();
    }

    void simple() {
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

        // Create a handler for the sendButton and nameField
        class MyHandler implements ClickHandler, KeyUpHandler {
            /**
             * Fired when the user clicks on the sendButton.
             */
            public void onClick(ClickEvent event) {
                sendNameToServer();
            }

            /**
             * Fired when the user types in the nameField.
             */
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    sendNameToServer();
                }
            }

            /**
             * Send the name from the nameField to the server and wait for a response.
             */
            private void sendNameToServer() {
                pageService.savePageHtml(pageNameTextBox.getText(), pageEditor.getHTML(), new AsyncCallback<Void>() {
                    public void onFailure(Throwable caught) {
                    }

                    public void onSuccess(Void result) {
                        htmlViewer.setText(pageEditor.getHTML());
                    }
                });
            }
        }

        // Add a handler to send the name to the server
        MyHandler handler = new MyHandler();
        sendButton.addClickHandler(handler);
        pageNameTextBox.addKeyUpHandler(handler);
    }

}
