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

import com.pyx4j.forms.client.validators.TextBoxParserValidator;

public abstract class CNumberField<E extends Number> extends CTextBox<E> {

    private TextBoxParserValidator<E> validator;

    public CNumberField(String title, String validationMessage) {
        super(title);
        setFormat(new NumberFormat());
        validator = new TextBoxParserValidator<E>(validationMessage);
        addValueValidator(validator);
    }

    public void setRange(E from, E to) {
        removeValueValidator(validator);
        validator = new NumberFieldRangeValidator(from, to);
        addValueValidator(validator);
    }

    class NumberFormat implements IFormat<E> {

        public String format(E value) {
            return value.toString();
        }

        public E parse(String string) {
            try {
                return valueOf(string);
            } catch (NumberFormatException e) {
                return null;
            }
        }

    }

    class NumberFieldRangeValidator extends TextBoxParserValidator<E> {

        private final E from;

        private final E to;

        public NumberFieldRangeValidator(E from, E to) {
            super("Should be numeric in range from " + from + " to " + to);
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean isValid(CEditableComponent<E> component, E value) {
            if (super.isValid(component, value)) {
                if (value == null) {
                    return true;
                } else if (isInRange(value, from, to)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    abstract boolean isInRange(E value, E from, E to);

    public abstract E valueOf(String string);
}
