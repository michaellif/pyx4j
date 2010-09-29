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

public abstract class TextWatermark {

    private final WatermarkComponent component;

    private String watermark;

    private HandlerRegistration focusHandlerRegistration;

    private HandlerRegistration blurHandlerRegistration;

    private boolean insideShowWatermark = false;

    private boolean shouldBeShown = false;

    public TextWatermark(WatermarkComponent component) {
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
            focusHandlerRegistration.removeHandler();
            blurHandlerRegistration.removeHandler();
        }
    }

    public void show(String text) {
        if (insideShowWatermark) {
            return;
        }
        shouldBeShown = (watermark != null && !watermark.isEmpty() && (text == null || text.isEmpty()));
        show(shouldBeShown);
    }

    private void show(boolean show) {
        insideShowWatermark = true;
        if (show) {
            if (shouldBeShown) {
                component.addStyleDependentName("watermark");
                component.setText(watermark);
            }
        } else {
            if (shouldBeShown) {
                component.setText(null);
            }
            component.removeStyleDependentName("watermark");
        }
        insideShowWatermark = false;
    }

    public boolean isShown() {
        return shouldBeShown;
    }

    abstract String getText();

    abstract void setText(String text);

}
