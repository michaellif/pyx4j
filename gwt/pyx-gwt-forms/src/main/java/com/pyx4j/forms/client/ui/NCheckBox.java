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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.CCheckBox.Alignment;
import com.pyx4j.widgets.client.CheckBox;

public class NCheckBox extends NFocusField<Boolean, CheckBox, CCheckBox, HTML> implements INativeFocusComponent<Boolean> {

    public NCheckBox(final CCheckBox checkBox) {
        super(checkBox);
    }

    @Override
    protected CheckBox createEditor() {
        return new CheckBox();
    }

    @Override
    protected HTML createViewer() {
        return new HTML();
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                getCComponent().onEditingStop();
            }
        });
        setTabIndex(getCComponent().getTabIndex());
        setAlignmet(getCComponent().getAlignmet());
    }

    @Override
    public void setNativeValue(Boolean value) {
        if (isViewable()) {
            getViewer().setText(getCComponent().getFormat().format(value));
        } else {
            getEditor().setValue(value);
        }
    }

    @Override
    public Boolean getNativeValue() {
        if (!isViewable()) {
            return Boolean.valueOf(getEditor().getValue());
        } else {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        }
    }

    public void setAlignmet(Alignment alignment) {
        getElement().getStyle().setProperty("textAlign", alignment.name());
    }

}
