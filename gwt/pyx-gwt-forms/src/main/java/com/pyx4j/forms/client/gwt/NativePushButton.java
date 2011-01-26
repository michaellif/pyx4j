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
package com.pyx4j.forms.client.gwt;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.CustomButton.Face;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * Special class to enable click in selenium.
 */
public class NativePushButton extends ComplexPanel {

    private final PushButton gwtPushButton;

    public NativePushButton(Image upImage, Image downImage) {
        super();
        setElement(Document.get().createDivElement());
        gwtPushButton = new PushButton(upImage, downImage);
        add(gwtPushButton, getElement());

        gwtPushButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (gwtPushButton.isEnabled()) {
                    NativePushButton.this.fireEvent(event);
                    event.stopPropagation();
                }
            }
        });

        gwtPushButton.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                NativePushButton.this.fireEvent(event);
                event.stopPropagation();
            }
        });
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return addDomHandler(handler, KeyDownEvent.getType());
    }

    public Face getUpDisabledFace() {
        return gwtPushButton.getUpDisabledFace();
    }

    public Face getUpHoveringFace() {
        return gwtPushButton.getUpHoveringFace();
    }

    public void setEnabled(boolean enabled) {
        gwtPushButton.setEnabled(enabled);
    }

    public void setFocus(boolean focused) {
        gwtPushButton.setFocus(focused);
    }

    public void setTabIndex(int index) {
        gwtPushButton.setTabIndex(index);
    }
}
