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
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;

public abstract class CTextComponent<DATA, WIDGET extends INativeValueBox<DATA>> extends CValueBoxBase<DATA, WIDGET> {

    private TextComponentLengthValidator validator;

    public CTextComponent() {
        super();
    }

    public void setMaxLength(int length) {
        removeComponentValidator(validator);
        validator = new TextComponentLengthValidator(length);
        addComponentValidator(validator);
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder(super.getDebugInfo());
        info.append("watermark").append("=").append(getWatermark()).append(";");
        return info.toString();
    }

    class TextComponentLengthValidator extends AbstractComponentValidator<DATA> {

        private final String validationMessage;

        private final int length;

        public TextComponentLengthValidator(int length) {
            this.length = length;
            this.validationMessage = "Max length is " + length;
        }

        @Override
        public BasicValidationError isValid() {
            DATA value = getCComponent().getValue();
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                return ((String) value).length() <= length ? null : new BasicValidationError(CTextComponent.this, validationMessage);
            } else {
                return null;
            }
        }
    }

    static class StringFormat implements IFormatter<String, String> {

        @Override
        public String format(String value) {
            if (value == null) {
                value = "";
            }
            return value;
        }
    }

    static class StringParser implements IParser<String> {

        @Override
        public String parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            return string;
        }
    }
}
