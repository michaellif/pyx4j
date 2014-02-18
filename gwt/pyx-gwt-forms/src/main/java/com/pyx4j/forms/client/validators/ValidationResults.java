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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;

public class ValidationResults {

    private static final I18n i18n = I18n.get(ValidationResults.class);

    private final ArrayList<ValidationError> validationErrors = new ArrayList<ValidationError>();

    public ValidationResults() {
    }

    public void appendValidationErrors(ValidationResults results) {
        if (results != null) {
            validationErrors.addAll(results.getValidationErrors());
        }
    }

    public void appendValidationError(ValidationError error) {
        validationErrors.add(error);
    }

    public ArrayList<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public ValidationResults getValidationResultsByOriginator(CComponent<?> component) {
        ValidationResults results = new ValidationResults();
        for (ValidationError validationError : validationErrors) {
            if (component == validationError.getOriginator()) {
                results.appendValidationError(validationError);
            }
        }
        return results;
    }

    public String getValidationMessage(boolean html, boolean showFieldName, boolean showLocation) {
        StringBuilder messagesBuffer = new StringBuilder();
        LoopCounter c = new LoopCounter(validationErrors);
        if (html) {
            messagesBuffer.append("<ul style='text-align:left'>");
            for (ValidationError ve : validationErrors) {
                if (ve instanceof FieldValidationError) {
                    messagesBuffer.append("<li>").append(((FieldValidationError) ve).getMessageString(showFieldName, showLocation)).append("</li>");
                }
            }
            messagesBuffer.append("</ul>");
        } else {
            for (ValidationError ve : validationErrors) {
                FieldValidationError fve = (FieldValidationError) ve;
                if (ve instanceof FieldValidationError) {
                    switch (c.next()) {
                    case SINGLE:
                        messagesBuffer.append(fve.getMessageString(showFieldName, showLocation));
                        break;
                    case FIRST:
                    case ITEM:
                        messagesBuffer.append("- ").append(fve.getMessageString(showFieldName, showLocation)).append(";\n");
                        break;
                    case LAST:
                        messagesBuffer.append("- ").append(fve.getMessageString(showFieldName, showLocation));
                        break;
                    }
                }
            }
        }
        return messagesBuffer.toString();
    }

    public String getValidationShortMessage() {
        StringBuilder messagesBuffer = new StringBuilder();
        ArrayList<ValidationError> validationErrors = getValidationErrors();

        int fieldValidationErrorsCounter = 0;

        for (ValidationError validationError : validationErrors) {
            if (validationError instanceof FieldValidationError) {
                fieldValidationErrorsCounter++;
            }
        }

        if (validationErrors.size() > 0) {
            messagesBuffer.append(i18n.tr("{0} error(s)", fieldValidationErrorsCounter));
        }

        return messagesBuffer.toString();
    }

    public boolean isValid() {
        return validationErrors.size() == 0;
    }

}
