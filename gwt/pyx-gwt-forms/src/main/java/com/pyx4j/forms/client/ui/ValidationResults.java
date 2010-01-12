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

public class ValidationResults {

    private final ArrayList<String> messages = new ArrayList<String>();

    public ValidationResults() {

    }

    public void appendValidationError(String message) {
        messages.add(message);
    }

    public void appendValidationErrors(ValidationResults results) {
        if (results != null) {
            messages.addAll(results.getMessages());
        }
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public String getMessagesText() {
        StringBuilder messagesBuffer = new StringBuilder();
        LoopCounter c = new LoopCounter(messages);
        for (String m : messages) {
            switch (c.next()) {
            case SINGLE:
                messagesBuffer.append(m);
                break;
            case FIRST:
            case ITEM:
                messagesBuffer.append("- ").append(m).append(";\n");
                break;
            case LAST:
                messagesBuffer.append("- ").append(m);
                break;
            }
        }
        return messagesBuffer.toString();
    }

    public boolean isValid() {
        return messages.size() == 0;
    }
}
