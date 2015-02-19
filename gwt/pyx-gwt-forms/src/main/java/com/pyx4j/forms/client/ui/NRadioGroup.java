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
 */
package com.pyx4j.forms.client.ui;

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.RadioGroup;

public class NRadioGroup<E> extends NFocusField<E, RadioGroup<E>, CRadioGroup<E>, HTML> {

    public NRadioGroup(CRadioGroup<E> cComponent) {
        super(cComponent);
    }

    @Override
    protected RadioGroup<E> createEditor() {
        RadioGroup<E> radioGroup = new RadioGroup<E>(getCComponent().getLayout());
        radioGroup.setFormatter(new IFormatter<E, SafeHtml>() {

            @Override
            public SafeHtml format(E value) {
                return SafeHtmlUtils.fromTrustedString(getCComponent().getFormat().format(value));
            }
        });
        return radioGroup;
    }

    @Override
    protected HTML createViewer() {
        return new HTML();
    }

    @Override
    protected void onEditorCreate() {
        //Do not call super!

        setTabIndex(getCComponent().getTabIndex());

        List<E> options = getCComponent().getOptions();
        getEditor().setOptions(options);

        getEditor().addValueChangeHandler(new ValueChangeHandler<E>() {
            @Override
            public void onValueChange(ValueChangeEvent<E> event) {
                getCComponent().stopEditing();
            }
        });
    }

    @Override
    public void setNativeValue(E value) {
        if (isViewable()) {
            getViewer().setHTML(SafeHtmlUtils.fromTrustedString(getCComponent().getFormat().format(value)));
        } else {
            getEditor().setValue(value);
        }
    }

    @Override
    public E getNativeValue() {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            return getEditor().getValue();
        }
    }

    public void setOptions(List<E> options) {
        if (getEditor() != null) {
            getEditor().setOptions(options);
        }
    }

}
