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

import com.pyx4j.forms.client.ui.CEditableComponent;

/**
 *
 */
public class IntegerRangeValidator<E> extends IntegerValidator<E> {

    private final int min;

    private final int max;

    public IntegerRangeValidator(int min, int max, String validationMessage) {
        super(validationMessage);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isValid(CEditableComponent<E> component, E value) {
        if ((value != null) && super.isValid(component, value)) {
            try {
                int i = Integer.parseInt(value.toString());
                return (i <= max) && (i >= min);
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public String getValidationMessage(CEditableComponent<E> component, E value) {
        if (validationMessage != null) {
            return validationMessage;
        } else {
            return "Should be integer between " + min + " and " + max;
        }
    }
}
