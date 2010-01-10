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
 * Created on 14-Sep-06
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

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