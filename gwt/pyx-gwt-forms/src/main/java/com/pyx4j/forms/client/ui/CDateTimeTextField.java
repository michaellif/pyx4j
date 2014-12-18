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
 * Created on Oct 3, 2014
 * @author vlads
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.i18n.shared.I18n;

public class CDateTimeTextField extends CTextFieldBase<Date, NTextBox<Date>> {

    private static final I18n i18n = I18n.get(CDateTimeTextField.class);

    public static final String defaultDateTimeFormat = i18n.tr("MM/dd/yyyy h:mm a");

    public CDateTimeTextField() {
        super();
        setDateTimeFormat(defaultDateTimeFormat);
        setNativeComponent(new NTextBox<Date>(this));
    }

    public void setDateTimeFormat(String format) {
        setFormatter(new DateTimeFormatter(format));
        setParser(new DateTimeParser(format));
        setWatermark(format);
    }

    public static class DateTimeFormatter implements IFormatter<Date, String> {

        final String timeFormat;

        final DateTimeFormat parser;

        public DateTimeFormatter(final String format) {
            this.timeFormat = format;
            parser = DateTimeFormat.getFormat(format);
        }

        @Override
        public String format(Date value) {
            if (value == null) {
                return null;
            }
            return parser.format(value);
        }
    }

    public static class DateTimeParser extends DateTimeFormatter implements IParser<Date> {

        public DateTimeParser(final String format) {
            super(format);
        }

        @Override
        public Date parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            } else {
                try {
                    return parser.parseStrict(string);
                } catch (IllegalArgumentException e) {
                    if (timeFormat.equals(defaultDateTimeFormat)) {
                        throw new ParseException(i18n.tr("Invalid date time format. Use MM/dd/yyyy 12:00 AM/PM format"), 0);
                    } else {
                        throw new ParseException(i18n.tr("Invalid date time format. Use {0} format", timeFormat), 0);
                    }
                }
            }
        }
    }

}
