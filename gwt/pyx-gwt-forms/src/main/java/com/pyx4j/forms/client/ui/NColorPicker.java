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
 * Created on Aug 23, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.ui.Button;

import com.pyx4j.forms.client.ui.NColorPicker.ColorButton;
import com.pyx4j.widgets.client.IFocusWidget;

public class NColorPicker extends NFocusComponent<Integer, ColorButton, CColorPicker, ColorButton> implements INativeFocusComponent<Integer> {

    public NColorPicker(final CColorPicker colorPicker) {
        super(colorPicker);
    }

    @Override
    protected ColorButton createEditor() {
        return new ColorButton();
    }

    @Override
    protected ColorButton createViewer() {
        return new ColorButton();
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
//        getEditor().addValueChangeHandler(new ValueChangeHandler<Integer>() {
//            @Override
//            public void onValueChange(ValueChangeEvent<Integer> event) {
//                getCComponent().onEditingStop();
//            }
//        });
        setTabIndex(getCComponent().getTabIndex());
    }

    @Override
    public void setNativeValue(Integer value) {
//        if (isViewable()) {
//            getViewer().setHTML(getCComponent().getFormat().format(value));
//        } else {
//            getEditor().setValue(value);
//        }
    }

    @Override
    public Integer getNativeValue() {
//        if (!isViewable()) {
//            return Boolean.valueOf(getEditor().getValue());
//        } else {
//            assert false : "getNativeValue() shouldn't be called in viewable mode";
        return null;
//        }
    }

    public class ColorButton extends Button implements IFocusWidget {

        @Override
        public void setEditable(boolean editable) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean isEditable() {
            // TODO Auto-generated method stub
            return false;
        }

    }

}