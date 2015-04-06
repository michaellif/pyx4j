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

import com.pyx4j.commons.IFormatter;

public class CLabel<E> extends CField<E, NLabel<E>> {

    private IFormatter<E, String> formatter;

    public CLabel() {
        this(new IFormatter<E, String>() {
            @Override
            public String format(E value) {
                return value == null ? null : value.toString();
            }
        });
    }

    public CLabel(IFormatter<E, String> formatter) {
        super();
        setFormatter(formatter);
        setNativeComponent(new NLabel<E>(this));
        setEditable(false);
    }

    public void setFormatter(IFormatter<E, String> formatter) {
        this.formatter = formatter;
    }

    public IFormatter<E, String> getFormatter() {
        return formatter;
    }

    protected String format(E value) {
        String text = null;
        try {
            text = getFormatter().format(value);
        } catch (Exception ignore) {
        }
        return text == null ? "" : text;
    }

    public String getFormattedValue() {
        return format(getValue());
    }

}
