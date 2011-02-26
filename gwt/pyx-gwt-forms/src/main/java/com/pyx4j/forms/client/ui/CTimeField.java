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

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;

import com.pyx4j.commons.Consts;
import com.pyx4j.forms.client.validators.TextBoxParserValidator;

public class CTimeField extends CTextFieldBase<Integer, NativeTextBox<Integer>> {

    public CTimeField() {
        this(null);
    }

    public CTimeField(String title) {
        super(title);
        setFormat(new TimeFormat());
        addValueValidator(new TextBoxParserValidator<Integer>("Not a valid time. Must be in the format 12:00 AM"));
    }

    @Override
    protected NativeTextBox<Integer> initWidget() {
        return new NativeTextBox<Integer>(this);
    }

    public static class TimeFormat implements IFormat<Integer> {

        private final static DateTimeFormat formatter = DateTimeFormat.getFormat("h:mm a");

        private final static DateTimeFormat parser = DateTimeFormat.getFormat("yyyy.MM.dd Z h:mm a");

        public TimeFormat() {

        }

        @Override
        public String format(Integer value) {
            if (value == null) {
                return null;
            }
            return formatter.format(new Date(value * Consts.MIN2MSEC), TimeZone.createTimeZone(0));
        }

        @Override
        public Integer parse(String string) {
            if (string == null) {
                return null;
            } else {
                try {
                    return Math.round(parser.parseStrict("1970.01.01 GMT " + string).getTime() * 1f / Consts.MIN2MSEC);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }

    }
}