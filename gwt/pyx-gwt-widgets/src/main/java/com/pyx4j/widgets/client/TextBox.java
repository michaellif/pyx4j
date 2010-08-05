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
 * Created on Jan 28, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import com.pyx4j.widgets.client.style.CSSClass;

public class TextBox extends com.google.gwt.user.client.ui.TextBox {

    private String watermark;

    private HandlerRegistration focusHandlerRegistration;

    private HandlerRegistration blurHandlerRegistration;

    public TextBox() {
        setStyleName(CSSClass.pyx4j_TextBox.name());
    }

    public void setWatermark(String watermark) {
        this.watermark = watermark;
        if (!watermark.isEmpty()) {
            if (focusHandlerRegistration == null) {
                focusHandlerRegistration = addFocusHandler(new FocusHandler() {
                    @Override
                    public void onFocus(FocusEvent event) {
                        showWatermark(false);
                    }
                });
            }

            if (blurHandlerRegistration == null) {
                blurHandlerRegistration = addBlurHandler(new BlurHandler() {
                    @Override
                    public void onBlur(BlurEvent event) {
                        showWatermark(true);

                    }

                });
            }
            showWatermark(true);
        } else {
            focusHandlerRegistration.removeHandler();
            blurHandlerRegistration.removeHandler();
        }
    }

    private void showWatermark(boolean show) {
        if (!isReadOnly() && isEnabled()) {
            if (show) {
                if (getText().isEmpty() || getText().equals(watermark)) {
                    setText(watermark);
                    addStyleDependentName("watermark");
                }
            } else {
                if (!getText().isEmpty() && getText().equals(watermark)) {
                    setText("");
                    removeStyleDependentName("watermark");
                }
            }
        }
    }

}
