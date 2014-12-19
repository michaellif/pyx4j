/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Apr 23, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.widgets.client.Viewer;

public class NViewer<E> extends NField<E, Viewer, CViewer<E>, Viewer> {

    public NViewer(CViewer<E> component) {
        super(component);
    }

    @Override
    public void setNativeValue(E value) {
        IsWidget widget = getCComponent().format(value);
        if (isViewable()) {
            getViewer().setWidget(widget);
        } else {
            getEditor().setWidget(widget);
        }
    }

    @Override
    public E getNativeValue() throws ParseException {
        throw new IllegalStateException("getNativeValue() shouldn't be called in viewable mode");
    }

    @Override
    protected Viewer createEditor() {
        return new Viewer();
    }

    @Override
    protected Viewer createViewer() {
        return new Viewer();
    }
}