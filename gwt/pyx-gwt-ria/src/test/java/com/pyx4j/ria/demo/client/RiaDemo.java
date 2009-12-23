package com.pyx4j.ria.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class RiaDemo implements EntryPoint {

    public void onModuleLoad() {
        final Button sendButton = new Button("Send");
        final TextBox nameField = new TextBox();
        nameField.setText("GWT User");

        // We can add style names to widgets
        sendButton.addStyleName("sendButton");

        // Add the nameField and sendButton to the RootPanel
        // Use RootPanel.get() to get the entire body element
        RootPanel.get().add(nameField);

        // Focus the cursor on the name field when the app loads
        nameField.setFocus(true);
        nameField.selectAll();

    }
}
