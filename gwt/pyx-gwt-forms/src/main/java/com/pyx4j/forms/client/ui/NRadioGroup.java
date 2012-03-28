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
 * Created on 2010-04-22
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.widgets.client.RadioGroup;

public class NRadioGroup<E> extends NFocusComponent<E, RadioGroup<E>, CRadioGroup<E>, HTML> {

    public NRadioGroup(CRadioGroup<E> cComponent) {
        super(cComponent);
    }

    @Override
    protected RadioGroup<E> createEditor() {
        return new RadioGroup<E>(getCComponent().getLayout(), getCComponent().getOptions()) {
            @Override
            protected String format(E value) {
                return getCComponent().getFormat().format(value);
            };
        };
    }

    @Override
    protected HTML createViewer() {
        return new HTML();
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addValueChangeHandler(new ValueChangeHandler<E>() {
            @Override
            public void onValueChange(ValueChangeEvent<E> event) {
                getCComponent().onEditingStop();
            }
        });
    }

    @Override
    public void setNativeValue(E value) {
        if (isViewable()) {
            getViewer().setHTML(value == null ? "" : getCComponent().getFormat().format(value));
        } else {
            getEditor().setValue(value);
        }
    }

    @Override
    public E getNativeValue() {
        if (!isViewable()) {
            return getEditor().getValue();
        } else {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        }
    }

}
