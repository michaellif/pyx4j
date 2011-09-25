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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.pyx4j.commons.CommonsStringUtils;

public class CTimeField extends CTextFieldBase<Time, NativeTextBox<Time>> {

    protected static I18n i18n = I18nFactory.getI18n(CTimeField.class);

    public static final String defaultTimeFormat = i18n.tr("h:mm a");

    public CTimeField() {
        this(null);
    }

    public CTimeField(String title) {
        super(title);
        setTimeFormat(defaultTimeFormat);
    }

    @Override
    protected NativeTextBox<Time> createWidget() {
        return new NativeTextBox<Time>(this);
    }

    public void setTimeFormat(String format) {
        setFormat(new TimeFormat(format));
    }

    public static class TimeFormat implements IFormat<Time> {

        private final DateTimeFormat parser;

        public TimeFormat(final String format) {
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
                    throw new ParseException("Invalid time format. Must be in the format 12:00 AM/PM", 0);
                }
            }
        }
    }
}