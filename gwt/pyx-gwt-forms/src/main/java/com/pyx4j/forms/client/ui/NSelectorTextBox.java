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

import java.text.ParseException;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.pyx4j.gwt.commons.ui.HTML;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.widgets.client.IWatermarkWidget;
import com.pyx4j.widgets.client.selector.SelectorTextBox;

public class NSelectorTextBox<E extends IEntity> extends NFocusField<E, SelectorTextBox<E>, CSelectorTextBox<E>, HTML> {

    public NSelectorTextBox(final CSelectorTextBox<E> cSuggestBox) {
        super(cSuggestBox);
    }

    @Override
    protected HTML createViewer() {
        return new HTML();
    }

    @Override
    protected SelectorTextBox<E> createEditor() {
        return new SelectorTextBox<E>(getCComponent().getOptionsGrabber(), new IFormatter<E, String>() {

            @Override
            public String format(E value) {
                return getCComponent().getFormatter().format(value);
            }
        }, new IFormatter<E, SafeHtml>() {

            @Override
            public SafeHtml format(E value) {
                return getCComponent().getOptionFormatter().format(value);
            }
        });
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        if (getEditor() instanceof IWatermarkWidget) {
            getEditor().setWatermark(getCComponent().getWatermark());
        }
    }

    @Override
    public void setNativeValue(E value) {
        if (isViewable()) {
            getViewer().setText(getCComponent().getFormatter().format(value));
        } else {
            if (getCComponent().isValueEmpty()) {
                getEditor().setValue(null);
            } else {
                getEditor().setValue(value);
            }
        }
    }

    @Override
    public E getNativeValue() throws ParseException {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            return getEditor().getValue();
        }
    }

}
