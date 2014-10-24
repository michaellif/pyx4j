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
 * Created on Sep 28, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FocusWidget;

import com.pyx4j.gwt.commons.css.CssVariable;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public abstract class TextWatermark {

    public static final String CSS_VAR_TEXT_WATERMARK = "TextWatermark";

    private final FocusWidget component;

    private String watermark;

    private HandlerRegistration focusHandlerRegistration;

    private HandlerRegistration blurHandlerRegistration;

    private boolean insideShowWatermark = false;

    public TextWatermark(FocusWidget component) {
        this.component = component;
    }

    public void setWatermark(String watermark) {
        this.watermark = watermark;
        if (watermark != null && !watermark.isEmpty()) {
            if (focusHandlerRegistration == null) {
                focusHandlerRegistration = component.addFocusHandler(new FocusHandler() {
                    @Override
                    public void onFocus(FocusEvent event) {
                        show(false);
                    }
                });
            }

            if (blurHandlerRegistration == null) {
                blurHandlerRegistration = component.addBlurHandler(new BlurHandler() {
                    @Override
                    public void onBlur(BlurEvent event) {
                        show(true);
                    }

                });
            }
            show(true);
        } else {
            if (focusHandlerRegistration != null) {
                focusHandlerRegistration.removeHandler();
            }
            if (blurHandlerRegistration != null) {
                blurHandlerRegistration.removeHandler();
            }
        }
    }

    public String getWatermark() {
        return watermark;
    }

    public void show() {
        if (insideShowWatermark) {
            return;
        }
        show(watermark != null && !watermark.isEmpty() && isEmptyText());
    }

    void show(boolean show) {
        insideShowWatermark = true;
        if (show) {
            if (isEmptyText() || isShown()) {
                setText(watermark);
                component.addStyleDependentName(WidgetTheme.StyleDependent.watermark.name());
                CssVariable.setVariable(component.getElement(), CSS_VAR_TEXT_WATERMARK, watermark);
            }
        } else {
            if (isShown()) {
                setText(null);
            }
            component.removeStyleDependentName(WidgetTheme.StyleDependent.watermark.name());
            CssVariable.removeVariable(component.getElement(), CSS_VAR_TEXT_WATERMARK);
        }
        insideShowWatermark = false;
    }

    public abstract String getText();

    public abstract void setText(String text);

    protected boolean isEmptyText() {
        return getText() == null || getText().isEmpty();
    }

    public boolean isShown() {
        return CssVariable.getVariable(component.getElement(), CSS_VAR_TEXT_WATERMARK) != null;
    }

}
