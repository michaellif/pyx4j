/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Dec 10, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.validators;

import com.pyx4j.forms.client.ui.CComponent;

public class FieldValidationError extends AbstractValidationError {

    private CComponent<?> originator;

    private String message;

    public FieldValidationError(CComponent<?> originator, String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public CComponent<?> getOriginator() {
        return originator;
    }

    public static final String uncapitalize(final String originalStr) {
        final int splitIndex = 1;
        final String result;
        if (originalStr.isEmpty()) {
            result = originalStr;
        } else {
            final String first = originalStr.substring(0, splitIndex).toLowerCase();
            final String rest = originalStr.substring(splitIndex);
            final StringBuilder uncapStr = new StringBuilder(first).append(rest);
            result = uncapStr.toString();
        }
        return result;
    }
}
