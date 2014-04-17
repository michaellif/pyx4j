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

import java.sql.Time;
import java.text.ParseException;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormat;
import com.pyx4j.forms.client.validators.TextBoxParserValidator;
import com.pyx4j.i18n.shared.I18n;

public class CTimeField extends CTextFieldBase<Time, NTextBox<Time>> {

    private static final I18n i18n = I18n.get(CTimeField.class);

    public static final String defaultTimeFormat = i18n.tr("h:mm a");

    public CTimeField() {
        super();
        setTimeFormat(defaultTimeFormat);
        addComponentValidator(new TextBoxParserValidator<Time>());
        setNativeComponent(new NTextBox<Time>(this));
        setWatermark("__:__ AM/PM");
        asWidget().setWidth("100%");
    }

    public void setTimeFormat(String format) {
        setFormat(new TimeFormat(format));
    }

    public static class TimeFormat implements IFormat<Time> {

        private final String timeFormat;

        private final DateTimeFormat parser;

        public TimeFormat(final String format) {
            this.timeFormat = format;
            parser = DateTimeFormat.getFormat(format);
        }

        @Override
        public String format(Time value) {
            if (value == null) {
                return null;
            }
            return parser.format(value);
        }

        @Override
        public Time parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            } else {
                try {
                    return new Time(parser.parseStrict(string).getTime());
                } catch (IllegalArgumentException e) {
                    if (timeFormat.equals(defaultTimeFormat)) {
                        throw new ParseException(i18n.tr("Invalid time format. Use 12:00 AM/PM format"), 0);
                    } else {
                        throw new ParseException(i18n.tr("Invalid time format. Use {0} format", timeFormat), 0);
                    }
                }
            }
        }
    }
}