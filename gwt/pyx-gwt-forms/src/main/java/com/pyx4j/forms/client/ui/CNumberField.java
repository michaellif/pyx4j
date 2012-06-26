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

import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.validators.TextBoxParserValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

public abstract class CNumberField<E extends Number> extends CTextFieldBase<E, NTextBox<E>> {

    private static final I18n i18n = I18n.get(CNumberField.class);

    private TextBoxParserValidator<E> validator;

    private final String validationMessage;

    public CNumberField(String title, String validationMessage) {
        super(title);
        this.validationMessage = validationMessage;
        setFormat(new DefaultNumberFormat());
        validator = new TextBoxParserValidator<E>();
        addValueValidator(validator);
    }

    @Override
    protected NTextBox<E> createWidget() {
        return new NTextBox<E>(this);
    }

    public void setRange(E from, E to) {
        removeValueValidator(validator);
        validator = new NumberFieldRangeValidator(from, to);
        addValueValidator(validator);
    }

    class DefaultNumberFormat implements IFormat<E> {

        @Override
        public String format(E value) {
            return value.toString();
        }

        @Override
        public E parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            try {
                return valueOf(string);
            } catch (NumberFormatException e) {
                throw new ParseException(validationMessage, 0);
            }
        }

    }

    protected String dataTypeName() {
        return i18n.tr("Numeric");
    }

    class NumberFieldRangeValidator extends TextBoxParserValidator<E> {

        private final E from;

        private final E to;

        public NumberFieldRangeValidator(E from, E to) {
            super();
            this.from = from;
            this.to = to;
        }

        @Override
        public ValidationError isValid(CComponent<E, ?> component, E value) {
            ValidationError failure = super.isValid(component, value);
            if (failure == null) {
                if (value == null) {
                    return null;
                } else if (isInRange(value, from, to)) {
                    return null;
                } else {
                    return new ValidationError(i18n.tr("{0} Should Be In The Range Between {1} And {2}", dataTypeName(), from, to));
                }
            } else {
                return failure;
            }
        }
    }

    abstract boolean isInRange(E value, E from, E to);

    public abstract E valueOf(String string);
}
