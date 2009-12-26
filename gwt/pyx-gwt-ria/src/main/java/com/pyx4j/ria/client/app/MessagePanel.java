/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on 14-Sep-06
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client.app;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;

public class MessagePanel extends DockPanel {

    enum Type {
        WARN, INFO, ERROR
    }

    private final Label messageText;

    public MessagePanel() {

        this.messageText = new Label();
        this.add(messageText, DockPanel.WEST);
        this.setCellVerticalAlignment(messageText, HasVerticalAlignment.ALIGN_MIDDLE);

    }

    @Override
    public void clear() {
        messageText.setText("");
    }

    public void message(String text, Type type) {
        DOM.setStyleAttribute(messageText.getElement(), "whiteSpace", "nowrap");
        switch (type) {
        case WARN:
            DOM.setStyleAttribute(messageText.getElement(), "color", "orange");
            break;
        case INFO:
            DOM.setStyleAttribute(messageText.getElement(), "color", "blue");
            break;
        case ERROR:
        default:
            DOM.setStyleAttribute(messageText.getElement(), "color", "#FF0000");
        }
        messageText.setText(text.replace('\n', ' '));
    }
    
}