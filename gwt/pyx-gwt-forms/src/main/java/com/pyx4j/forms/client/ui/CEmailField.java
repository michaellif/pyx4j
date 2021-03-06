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

import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IParser;
import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.i18n.shared.I18n;

public class CEmailField extends CTextFieldBase<String, NTextBox<String>> {

    private static final I18n i18n = I18n.get(CEmailField.class);

    public CEmailField() {
        this(false);
    }

    public CEmailField(boolean mandatory) {
        super();
        this.setMandatory(mandatory);
        setParser(new EmailParser());
        setNativeComponent(new NTextBox<String>(this));
    }

    public static class EmailParser implements IParser<String> {

        @Override
        public String parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            if (!ValidationUtils.isValidEmail(string)) {
                throw new ParseException(i18n.tr("Not a valid email"), 0);
            }
            return string;
        }

    }
}