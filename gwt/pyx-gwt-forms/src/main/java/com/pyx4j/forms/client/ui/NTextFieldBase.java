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
 * Created on Jan 10, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

import com.pyx4j.forms.client.events.NativeValueChangeEvent;
import com.pyx4j.widgets.client.ITextBoxWidget;
import com.pyx4j.widgets.client.Label;

/**
 * @deprecated Use {@link NValueBoxBase}
 */
@Deprecated
public abstract class NTextFieldBase<DATA, WIDGET extends ITextBoxWidget, CCOMP extends CTextFieldBase<DATA, ?>> extends NTextComponent<DATA, WIDGET, CCOMP>
        implements INativeTextComponent<DATA> {

    public NTextFieldBase(CCOMP cComponent) {
        super(cComponent);
    }

    @Override
    protected Label createViewer() {
        return new Label();
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                NativeValueChangeEvent.fire(getCComponent(), getEditor().getText());
            }
        });
    }

    @Override
    public void setNativeValue(DATA value) {
        String newValue = getCComponent().format(value);
        if (isViewable()) {
            getViewer().setText(newValue);
        } else {
            if (!newValue.equals(getEditor().getText())) {
                getEditor().setText(newValue);
            }
        }
        NativeValueChangeEvent.fire(getCComponent(), newValue);
    }

    @Override
    public DATA getNativeValue() throws ParseException {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            try {
                return getCComponent().getParser().parse(getEditor().getText().trim());
            } catch (ParseException e) {
                throw e;
            }
        }
    }

}