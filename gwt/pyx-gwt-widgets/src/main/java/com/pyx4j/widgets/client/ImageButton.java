/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Dec 3, 2014
 * @author michaellif
 */
package com.pyx4j.widgets.client;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Command;

import com.pyx4j.security.shared.Permission;
import com.pyx4j.widgets.client.images.ButtonImages;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class ImageButton extends ButtonBase {

    private ButtonImages buttonImages;

    public ImageButton(ButtonImages imageBundle, Command command) {
        this(imageBundle, null, command);
    }

    public ImageButton(ButtonImages imageBundle, String text, Command command) {
        super(null, text, command, (Permission[]) null);
        setImageBundle(imageBundle);
        setStylePrimaryName(getElement(), WidgetsTheme.StyleName.ImageButton.name());
        getTextLabel().setStyleName(WidgetsTheme.StyleName.ImageButtonText.name());

        addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                if (buttonImages != null) {
                    getImageHolder().getStyle().setProperty("background",
                            "url('" + buttonImages.hover().getSafeUri().asString() + "') no-repeat scroll left center");
                }
            }
        });

        addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (buttonImages != null) {
                    getImageHolder().getStyle().setProperty("background",
                            "url('" + buttonImages.active().getSafeUri().asString() + "') no-repeat scroll left center");
                }
            }
        });

        addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                if (buttonImages != null) {
                    getImageHolder().getStyle().setProperty("background",
                            "url('" + buttonImages.hover().getSafeUri().asString() + "') no-repeat scroll left center");
                }
            }
        });

        addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                if (buttonImages != null) {
                    getImageHolder().getStyle().setProperty("background",
                            "url('" + buttonImages.regular().getSafeUri().asString() + "') no-repeat scroll left center");
                }
            }
        });
    }

    public void setImageBundle(ButtonImages imageBundle) {
        this.buttonImages = imageBundle;
        updateImageState();
    }

    @Override
    protected void updateImageState() {
        if (buttonImages != null) {
            getImageHolder().getStyle().setProperty("paddingLeft", buttonImages.regular().getWidth() + "px");
            getImageHolder().getStyle().setProperty("height", buttonImages.regular().getHeight() + "px");
            getTextLabel().getStyle().setProperty("lineHeight", buttonImages.regular().getHeight() + "px");
            getImageHolder().getStyle().setProperty("background",
                    "url('" + buttonImages.regular().getSafeUri().asString() + "') no-repeat scroll left center");
        } else {
            super.updateImageState();
        }
    }

}
