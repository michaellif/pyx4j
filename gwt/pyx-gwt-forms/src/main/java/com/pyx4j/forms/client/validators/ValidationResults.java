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
import java.util.Set;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LoopCounter;
import com.pyx4j.i18n.shared.I18n;

public class ValidationResults {

    private static final I18n i18n = I18n.get(ValidationResults.class);

    private final ArrayList<AbstractValidationError> validationErrors = new ArrayList<AbstractValidationError>();

    public ValidationResults() {
    }

    public void appendValidationResults(ValidationResults results) {
        if (results != null) {
            validationErrors.addAll(results.getValidationErrors());
        }
    }

    public void appendValidationErrors(Set<AbstractValidationError> errors) {
        validationErrors.addAll(errors);
    }

    public ArrayList<AbstractValidationError> getValidationErrors() {
        return validationErrors;
    }

    public String getValidationMessage(boolean html) {
        return getValidationMessage(html, false);
    }

    public String getValidationMessage(boolean html, boolean showFieldName) {
        ArrayList<BasicValidationError> fieldValidationErrors = new ArrayList<>();

        for (AbstractValidationError error : validationErrors) {
            if (error instanceof BasicValidationError) {
                fieldValidationErrors.add((BasicValidationError) error);
            }
        }

        StringBuilder messagesBuffer = new StringBuilder();
        if (html) {
            if (fieldValidationErrors.size() == 0) {
            } else if (fieldValidationErrors.size() == 1) {
                AbstractValidationError ve = fieldValidationErrors.get(0);
                if (showFieldName) {
                    messagesBuffer.append("<b>").append(ve.getOriginator().getTitle()).append(" - </b> ");
                }
                messagesBuffer.append(ve.getMessage());
            } else {
                messagesBuffer.append("<ul style='text-align:left'>");
                for (AbstractValidationError ve : fieldValidationErrors) {
                    messagesBuffer.append("<li>");
                    if (showFieldName) {
                        messagesBuffer.append("<b>").append(ve.getOriginator().getTitle()).append(" - </b> ");
                    }
                    messagesBuffer.append(ve.getMessage()).append("</li>");
                }
                messagesBuffer.append("</ul>");
            }
        } else {
            LoopCounter c = new LoopCounter(fieldValidationErrors);
            for (AbstractValidationError ve : fieldValidationErrors) {
                switch (c.next()) {
                case SINGLE:
                    if (showFieldName) {
                        messagesBuffer.append(ve.getOriginator().getTitle()).append(" - ");
                    }
                    messagesBuffer.append(ve.getMessage());
                    break;
                case FIRST:
                case ITEM:
                    messagesBuffer.append("- ");
                    if (showFieldName) {
                        messagesBuffer.append(ve.getOriginator().getTitle()).append(" - ");
                    }
                    messagesBuffer.append(ve.getMessage()).append(";\n");
                    break;
                case LAST:
                    messagesBuffer.append("- ");
                    if (showFieldName) {
                        messagesBuffer.append(ve.getOriginator().getTitle()).append(" - ");
                    }
                    messagesBuffer.append(ve.getMessage());
                    break;
                }
            }
        }
        return messagesBuffer.toString();
    }

    public String getValidationShortMessage() {
        if (validationErrors.size() > 0) {
            return i18n.tr("Error");
        } else {
            return "";
        }
    }

    public boolean isValid() {
        return validationErrors.size() == 0;
    }

    public static ValidationResults getValidationResults(Widget widget) {
        ValidationResults results = new ValidationResults();
        if (widget instanceof IValidatable) {
            results.appendValidationResults(((IValidatable) widget).getValidationResults());
        } else if (widget instanceof HasWidgets) {
            for (Widget childWidget : ((HasWidgets) widget)) {
                results.appendValidationResults(getValidationResults(childWidget));
            }
        }
        return results;
    }

}
