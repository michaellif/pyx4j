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

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.commons.IFormatter;

public class CHtml<E> extends CField<E, NHtml<E>> {

    private IFormatter<E, SafeHtml> formatter;

    public CHtml() {
        this(new IFormatter<E, SafeHtml>() {
            @Override
            public SafeHtml format(E value) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                if (value != null) {
                    builder.appendHtmlConstant(value.toString());
                }
                return builder.toSafeHtml();
            }

        });
    }

    public CHtml(IFormatter<E, SafeHtml> formatter) {
        super();
        setFormatter(formatter);
        setNativeComponent(new NHtml<E>(this));
        setEditable(false);
    }

    public void setFormatter(IFormatter<E, SafeHtml> formatter) {
        this.formatter = formatter;
    }

    public IFormatter<E, SafeHtml> getFormatter() {
        return formatter;
    }

    protected SafeHtml format(E value) {
        return getFormatter().format(value);
    }

    public SafeHtml getFormattedValue() {
        return format(getValue());
    }

}
