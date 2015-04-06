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
 * Created on Jan 28, 2010
 * @author michaellif
 */
package com.pyx4j.widgets.client;

import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;

public class TextArea extends ValueBoxBase<String> {

    private final com.google.gwt.user.client.ui.TextArea textBoxWidget;

    public TextArea() {
        textBoxWidget = new com.google.gwt.user.client.ui.TextArea();
        textBoxWidget.getElement().getStyle().setProperty("resize", "vertical");
        setTextBoxWidget(textBoxWidget);
    }

    public void setVisibleLines(int visibleLines) {
        textBoxWidget.setVisibleLines(visibleLines);
    }

    @Override
    protected IParser<String> getParser() {
        if (super.getParser() == null) {
            setParser(new StringParser());
        }
        return super.getParser();
    }

    @Override
    protected IFormatter<String, String> getFormatter() {
        if (super.getFormatter() == null) {
            setFormatter(new StringFormat());
        }
        return super.getFormatter();
    }

    private class StringFormat implements IFormatter<String, String> {

        @Override
        public String format(String value) {
            if (value == null) {
                value = "";
            }
            return value;
        }
    }

    private class StringParser implements IParser<String> {

        @Override
        public String parse(String string) throws ParseException {
            if (CommonsStringUtils.isEmpty(string)) {
                return null; // empty value case
            }
            return string;
        }
    }
}
