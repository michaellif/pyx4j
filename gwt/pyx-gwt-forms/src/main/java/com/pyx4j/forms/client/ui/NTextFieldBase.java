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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.ValueBoxBase;

import com.pyx4j.widgets.client.WatermarkComponent;

public abstract class NTextFieldBase<DATA, WIDGET extends ValueBoxBase<?>> extends NTextComponent<DATA, WIDGET, CTextFieldBase<DATA, ?>> implements
        INativeTextComponent<DATA>, WatermarkComponent {

    public NTextFieldBase(CTextFieldBase<DATA, ?> cComponent) {
        this(cComponent, null);
    }

    public NTextFieldBase(CTextFieldBase<DATA, ?> cComponent, ImageResource triggerImage) {
        super(cComponent, triggerImage);

    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        setWatermark(getCComponent().getWatermark());
    }

    @Override
    public void setNativeValue(DATA value) {
        String newValue = value == null ? "" : getCComponent().getFormat().format(value);
        if (isViewable()) {
            getViewer().setHTML(newValue);
        } else {
            if (!newValue.equals(getEditor().getText())) {
                getEditor().setText(newValue);
            }
        }
    }

    @Override
    public DATA getNativeValue() throws ParseException {
        if (!isViewable()) {
            try {
                return getCComponent().getFormat().parse(getEditor().getText());
            } catch (ParseException e) {
                throw e;
            }
        } else {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        }
    }

    @Override
    public void setWatermark(String watermark) {
        if (getEditor() instanceof WatermarkComponent) {
            ((WatermarkComponent) getEditor()).setWatermark(watermark);
        }
    }
}