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

import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import com.pyx4j.widgets.client.Label;

public class NLabel<E> extends NComponent<E, Label, CLabel<E>, Label> implements INativeComponent<E> {

    public NLabel(final CLabel<E> cLabel) {
        super(cLabel);
    }

    @Override
    protected Label createEditor() {
        return new Label();
    }

    @Override
    protected Label createViewer() {
        return new Label();
    }

    @Override
    public void setNativeValue(E value) {
        String text = value == null ? "" : getCComponent().getFormat().format(value);
        if (isViewable()) {
            getViewer().setHTML(SafeHtmlUtils.fromString(text));
        } else {
            getEditor().setHTML(SafeHtmlUtils.fromString(text));
        }
    }

    @Override
    public E getNativeValue() {
        assert false : "getNativeValue() shouldn't be called on Label";
        return null;
    }

}
