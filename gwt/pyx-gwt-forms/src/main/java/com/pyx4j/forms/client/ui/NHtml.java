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
 * Created on May 8, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.safehtml.shared.SafeHtml;

import com.pyx4j.widgets.client.Label;

public class NHtml extends NField<String, Label, CHtml, Label> {

    public NHtml(CHtml cComponent) {
        super(cComponent);
    }

    @Override
    public void setNativeValue(String value) {
        String newValue = getCComponent().format(value);
        if (isViewable()) {
            getViewer().setHTML(newValue);
        } else {
            if (!newValue.equals(getEditor().getText())) {
                getEditor().setHTML(newValue);
            }
        }
    }

    @Override
    public String getNativeValue() throws ParseException {
        throw new IllegalStateException("getNativeValue() shouldn't be called in viewable mode");
    }

    @Override
    protected Label createEditor() {
        return new Label();
    }

    @Override
    protected Label createViewer() {
        return new Label();
    }
}
