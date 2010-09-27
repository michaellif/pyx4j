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
 * Created on Sep 27, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.forms.client.validators.EditableValueValidator;

public abstract class CTextComponent<E> extends CEditableComponent<E> {

    private TextComponentLengthValidator<E> validator;

    public CTextComponent() {
        super();
    }

    public CTextComponent(String title) {
        super(title);
    }

    public void setMaxLength(int length) {
        removeValueValidator(validator);
        validator = new TextComponentLengthValidator(length);
        addValueValidator(validator);
    }

    class TextComponentLengthValidator<E> implements EditableValueValidator<E> {

        private final String validationMessage;

        private final int length;

        public TextComponentLengthValidator(int length) {
            this.length = length;
            this.validationMessage = "Max length is " + length;
        }

        public String getValidationMessage(CEditableComponent<E> component, E value) {
            return validationMessage;
        }

        public boolean isValid(CEditableComponent<E> component, E value) {
            if (value == null) {
                return true;
            }
            if (value instanceof String) {
                return ((String) value).length() <= length;
            } else {
                return true;
            }
        }
    }
}
