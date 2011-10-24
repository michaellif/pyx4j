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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.commons.css.CSSClass;

public class HtmlArea extends ScrollPanel implements WatermarkComponent {

    private final HTML viewer;

    private TextWatermark watermark;

    public HtmlArea() {
        super();
        setStyleName(CSSClass.pyx4j_TextBox.name());
        getElement().getStyle().setPadding(2, Unit.PX);

        viewer = new HTML();
        viewer.setWidth("100%");

        setWidget(viewer);
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        return addDomHandler(focusHandler, FocusEvent.getType());
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        return addDomHandler(blurHandler, BlurEvent.getType());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getHTML() {
        if (watermark != null && watermark.isShown()) {
            return null;
        } else {
            return "".equals(viewer.getHTML()) ? null : viewer.getHTML();
        }
    }

    public void setHTML(String text) {
        viewer.setHTML(text);
        if (watermark != null) {
            watermark.show();
        }
    }

    public void setWatermark(String text) {
        if (watermark == null) {
            watermark = new TextWatermark(this) {

                @Override
                String getText() {
                    return viewer.getHTML();
                }

                @Override
                void setText(String text) {
                    viewer.setHTML("<pyx:watermark>" + text + "</pyx:watermark>");
                }

                @Override
                protected boolean isEmptyText() {
                    return HtmlUtils.isEmpty(getText());
                }

            };
        }
        watermark.setWatermark(text);
    }

}