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
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.i18n.client.NumberFormat;

import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

public abstract class CNumberField<E extends Number> extends CTextFieldBase<E, NTextBox<E>> {

    private static final I18n i18n = I18n.get(CNumberField.class);

    private AbstractComponentValidator<E> validator;

    private NumberFormat numberFormat;

    public CNumberField() {
        super();
        numberFormat = NumberFormat.getDecimalFormat();
        setNativeComponent(new NTextBox<E>(this));
    }

    public void setRange(E from, E to) {
        removeComponentValidator(validator);
        validator = new NumberFieldRangeValidator(from, to);
        addComponentValidator(validator);
    }

    protected String dataTypeName() {
        return i18n.tr("Numeric");
    }

    class NumberFieldRangeValidator extends AbstractComponentValidator<E> {

        private final E from;

        private final E to;

        public NumberFieldRangeValidator(E from, E to) {
            super();
            this.from = from;
            this.to = to;
        }

        @Override
        public BasicValidationError isValid() {
            if (getCComponent().getValue() == null) {
                return null;
            } else if (isInRange(getCComponent().getValue(), from, to)) {
                return null;
            } else {
                return new BasicValidationError(CNumberField.this, i18n.tr("{0} should be in the range between {1} and {2}", dataTypeName(), from, to));
            }
        }
    }

    abstract boolean isInRange(E value, E from, E to);

    NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public void setNumberPattern(String pattern) {
        numberFormat = NumberFormat.getFormat(pattern);
    }
}
