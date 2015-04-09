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

import java.sql.Time;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.i18n.annotations.I18nContext;
import com.pyx4j.i18n.shared.I18n;

public class CTimeField extends CTextFieldBase<Time, NTextBox<Time>> {

    private static final I18n i18n = I18n.get(CTimeField.class);

    @I18nContext(javaFormatFlag = true)
    private static final String defaultTimeFormat() {
        return i18n.tr("h:mm a");
    }

    public static final String defaultTimeFormat = defaultTimeFormat();

    public CTimeField() {
        super();
        setTimeFormat(defaultTimeFormat);
        setNativeComponent(new NTextBox<Time>(this));
        setWatermark("__:__ AM/PM");
    }

    public void setTimeFormat(String format) {
        setFormatter(new TimeFormat(format));
        setParser(new TimeParser(format));
    }

    public static class TimeFormat implements IFormatter<Time, String> {

        final DateTimeFormat formatter;

        public TimeFormat(final String pattern) {
            formatter = DateTimeFormat.getFormat(pattern);
        }

        @Override
        public String format(Time value) {
            if (value == null) {
                return null;
            }
            return formatter.format(value);
        }
    }

    public static class TimeParser implements IParser<Time> {

        final List<DateTimeFormat> parsers;

        final String pattern;

        public TimeParser(final String pattern) {
            parsers = Arrays.asList(DateTimeFormat.getFormat(pattern), DateTimeFormat.getFormat("h:mm a"), DateTimeFormat.getFormat("h:mma"));
            this.pattern = pattern;
        }

        @Override
        public Time parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            } else {
                for (DateTimeFormat parser : parsers) {
                    try {
                        return TimeUtils.logicalTime(parser.parseStrict(string));
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                }
                throw new ParseException(i18n.tr("Invalid time format. Use {0} format", pattern), 0);
            }
        }
    }
}