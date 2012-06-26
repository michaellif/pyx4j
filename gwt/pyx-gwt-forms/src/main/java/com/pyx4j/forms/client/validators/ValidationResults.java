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
package com.pyx4j.forms.client.validators;

import java.util.ArrayList;

import com.pyx4j.commons.LoopCounter;

public class ValidationResults {

    private final ArrayList<ValidationError> validationErrors = new ArrayList<ValidationError>();

    public ValidationResults() {

    }

    public void appendValidationError(String title, String message, String location) {
        validationErrors.add(new ValidationError(title, message, location));
    }

    public void appendValidationErrors(ValidationResults results) {
        if (results != null) {
            validationErrors.addAll(results.getMessages());
        }
    }

    public ArrayList<ValidationError> getMessages() {
        return validationErrors;
    }

    public String getMessagesText(boolean html, boolean showLocation) {
        StringBuilder messagesBuffer = new StringBuilder();
        LoopCounter c = new LoopCounter(validationErrors);
        if (html) {
            messagesBuffer.append("<ul style='text-align:left'>");
            for (ValidationError ve : validationErrors) {
                messagesBuffer.append("<li>").append(ve.getMessageString(showLocation)).append("</li>");
            }
            messagesBuffer.append("</ul>");
        } else {
            for (ValidationError ve : validationErrors) {
                switch (c.next()) {
                case SINGLE:
                    messagesBuffer.append(ve.getMessageString(showLocation));
                    break;
                case FIRST:
                case ITEM:
                    messagesBuffer.append("- ").append(ve.getMessageString(showLocation)).append(";\n");
                    break;
                case LAST:
                    messagesBuffer.append("- ").append(ve.getMessageString(showLocation));
                    break;
                }
            }
        }
        return messagesBuffer.toString();
    }

    public boolean isValid() {
        return validationErrors.size() == 0;
    }

}
