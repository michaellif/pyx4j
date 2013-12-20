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

public class CHtml extends CField<String, NHtml> {

    private IFormat<String> format;

    public CHtml() {
        super();
        setFormat(new HtmlFormat());
        setNativeWidget(new NHtml(this));
    }

    public void setFormat(IFormat<String> format) {
        this.format = format;
    }

    public IFormat<String> getFormat() {
        return format;
    }

    protected String format(String value) {
        String text = null;
        try {
            text = getFormat().format(value);
        } catch (Exception ignore) {
        }
        return text == null ? "" : text;
    }

    public String getFormattedValue() {
        return format(getValue());
    }

    class HtmlFormat implements IFormat<String> {
        @Override
        public String format(String value) {
            if (value == null) {
                return null;
            } else {
                return value.toString();
            }
        }

        @Override
        public String parse(String string) {
            return null;
        }
    }
}
