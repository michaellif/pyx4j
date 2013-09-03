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
 * Created on Jan 26, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

public class SuggestBox extends com.google.gwt.user.client.ui.SuggestBox implements WatermarkComponent, ITextWidget {

    private boolean enabled = true;

    private boolean editable = true;

    private TextWatermark watermark;

    public SuggestBox() {
        super(new MultiWordSuggestOracle(), new TextBox());
        setStyleName(DefaultWidgetsTheme.StyleName.TextBox.name());
        addStyleDependentName(DefaultWidgetsTheme.StyleDependent.singleLine.name());
    }

    @Override
    public void setWatermark(String text) {
        if (watermark == null) {
            watermark = new TextWatermark(this) {

                @Override
                String getText() {
                    return SuggestBox.super.getText();
                }

                @Override
                void setText(String text) {
                    SuggestBox.super.setText(text);
                }
            };
        }
        watermark.setWatermark(text);
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        if (watermark != null) {
            watermark.show();
        }
    }

    @Override
    public String getText() {
        if (watermark != null && watermark.isShown()) {
            return null;
        } else {
            return super.getText();
        }
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return addDomHandler(handler, BlurEvent.getType());
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return addDomHandler(handler, FocusEvent.getType());
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        DOM.setElementPropertyBoolean(getElement(), "disabled", !enabled);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        setEnabled(editable && this.isEnabled());
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        addSelectionHandler(new SelectionHandler() {
            @Override
            public void onSelection(SelectionEvent event) {
                NativeEvent nativeEvent = Document.get().createChangeEvent();
                ChangeEvent.fireNativeEvent(nativeEvent, null);
            }
        });
        return addDomHandler(handler, ChangeEvent.getType());
    }
}