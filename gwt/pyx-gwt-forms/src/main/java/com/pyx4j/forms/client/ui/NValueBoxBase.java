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
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.IParser;
import com.pyx4j.forms.client.events.NativeValueChangeEvent;
import com.pyx4j.widgets.client.IValueBoxWidget;
import com.pyx4j.widgets.client.IWatermarkWidget;

public abstract class NValueBoxBase<DATA, WIDGET extends IValueBoxWidget<DATA>, CCOMP extends CValueBoxBase<DATA, ?>> extends
        NFocusField<DATA, WIDGET, CCOMP, HTML> implements INativeValueBox<DATA> {

    public NValueBoxBase(CCOMP cComponent) {
        super(cComponent);
    }

    @Override
    protected HTML createViewer() {
        return new HTML();
    }

    @Override
    protected void onEditorCreate() {
        getEditor().setWidth("100%");
        super.onEditorCreate();
        getEditor().addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    boolean editingInProgress = getCComponent().isEditingInProgress();
                    getCComponent().stopEditing();
                    if (editingInProgress) {
                        getCComponent().startEditing();
                    }
                }
            }
        });
        getEditor().addValueChangeHandler(new ValueChangeHandler<DATA>() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public void onValueChange(ValueChangeEvent<DATA> event) {
                if (getEditor().isParsedOk()) {
                    boolean editingInProgress = getCComponent().isEditingInProgress();
                    getCComponent().stopEditing();
                    if (editingInProgress) {
                        getCComponent().startEditing();
                    }
                }
                NativeValueChangeEvent.fire((CValueBoxBase) getCComponent(), getEditor().getValue());
            }
        });
        getEditor().addKeyUpHandler(new KeyUpHandler() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public void onKeyUp(KeyUpEvent event) {
                NativeValueChangeEvent.fire((CValueBoxBase) getCComponent(), getEditor().getValue());
            }
        });

        if (getEditor() instanceof IWatermarkWidget) {
            getEditor().setWatermark(getCComponent().getWatermark());
        }

        getEditor().setFormatter(new IFormatter<DATA, String>() {

            @Override
            public String format(DATA value) {
                return getCComponent().getFormatter().format(value);
            }
        });

        getEditor().setParser(new IParser<DATA>() {

            @Override
            public DATA parse(String value) throws ParseException {
                return getCComponent().getParser().parse(value);
            }
        });
    }

    @Override
    protected void onViewerCreate() {
        getViewer().setWidth("100%");
        super.onViewerCreate();
    }

    @Override
    public void setNativeValue(DATA value) {
        if (isViewable()) {
            getViewer().setText(getCComponent().format(value));
        } else {
            getEditor().setValue(value);
        }
    }

    @Override
    public DATA getNativeValue() {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            return getEditor().getValue();
        }
    }

    @Override
    public boolean isParsedOk() {
        if (isViewable()) {
            assert false : "isParsedOk() shouldn't be called in viewable mode";
            return false;
        } else {
            return getEditor().isParsedOk();
        }
    }

    @Override
    public String getParseExceptionMessage() {
        if (isViewable()) {
            assert false : "getParseExceptionMessage() shouldn't be called in viewable mode";
            return null;
        } else {
            return getEditor().getParseExceptionMessage();
        }
    }
}