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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.Label;

public class CViewer<E> extends CField<E, NViewer<E>> {

    private IFormatter<E, IsWidget> formatter;

    public CViewer() {
        super();
        setFormatter(new IFormatter<E, IsWidget>() {
            @Override
            public IsWidget format(E value) {
                return new Label(value.toString());
            }

        });
        setNativeComponent(new NViewer<E>(this));
        setEditable(false);
    }

    public void setFormatter(IFormatter<E, IsWidget> formatter) {
        this.formatter = formatter;
    }

    public IFormatter<E, IsWidget> getFormatter() {
        return formatter;
    }

    protected IsWidget format(E value) {
        return getFormatter().format(value);
    }

    public IsWidget getFormattedValue() {
        return format(getValue());
    }

}
