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

import java.util.ArrayList;

import com.pyx4j.commons.LoopCounter;
import com.pyx4j.i18n.shared.I18n;

public class ValidationResults {

    private static final I18n i18n = I18n.get(ValidationResults.class);

    private final ArrayList<Message> messages = new ArrayList<Message>();

    public ValidationResults() {

    }

    public void appendValidationError(String title, String message, String location) {
        messages.add(new Message(title, message, location));
    }

    public void appendValidationErrors(ValidationResults results) {
        if (results != null) {
            messages.addAll(results.getMessages());
        }
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public String getMessagesText(boolean html, boolean showLocation) {
        StringBuilder messagesBuffer = new StringBuilder();
        LoopCounter c = new LoopCounter(messages);
        if (html) {
            messagesBuffer.append("<ul style='text-align:left'>");
            for (Message m : messages) {
                messagesBuffer.append("<li>").append(m.getMessageString(showLocation)).append("</li>");
            }
            messagesBuffer.append("</ul>");
        } else {
            for (Message m : messages) {
                switch (c.next()) {
                case SINGLE:
                    messagesBuffer.append(m.getMessageString(showLocation));
                    break;
                case FIRST:
                case ITEM:
                    messagesBuffer.append("- ").append(m.getMessageString(showLocation)).append(";\n");
                    break;
                case LAST:
                    messagesBuffer.append("- ").append(m.getMessageString(showLocation));
                    break;
                }
            }
        }
        return messagesBuffer.toString();
    }

    public boolean isValid() {
        return messages.size() == 0;
    }

    private class Message {

        String title;

        String description;

        String location;

        Message(String title, String description, String location) {
            this.title = title;
            this.description = description;
            this.location = location;
        }

        public String getMessageString(boolean showLocation) {
            StringBuilder builder = new StringBuilder();
            if (title != null && !title.isEmpty()) {
                builder.append("'");
                if (showLocation && location != null && !location.isEmpty()) {
                    builder.append(location).append("/");
                }
                builder.append(title).append("' ").append(i18n.tr("is not valid")).append(", ").append(description);
            }
            return builder.toString();
        }
    }
}
