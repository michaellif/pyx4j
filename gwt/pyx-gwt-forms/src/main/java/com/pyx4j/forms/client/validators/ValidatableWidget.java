/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on May 27, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.validators;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ValidatableWidget {

    public static ValidationResults getValidationResults(IsWidget widget) {
        Widget w = widget.asWidget();
        ValidationResults results = new ValidationResults();
        if (w instanceof IValidatable) {
            results.appendValidationResults(((IValidatable) w).getValidationResults());
        } else if (w instanceof HasWidgets) {
            for (Widget childWidget : ((HasWidgets) w)) {
                results.appendValidationResults(getValidationResults(childWidget));
            }
        }
        return results;
    }

    public static void showErrors(IsWidget widget, boolean show) {
        Widget w = widget.asWidget();
        if (w instanceof IValidatable) {
            ((IValidatable) w).showErrors(show);
        } else if (w instanceof HasWidgets) {
            for (Widget childWidget : ((HasWidgets) w)) {
                showErrors(childWidget, show);
            }
        }
    }
}
