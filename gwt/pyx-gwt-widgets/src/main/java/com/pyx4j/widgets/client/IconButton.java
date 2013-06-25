/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 2011-06-01
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.widgets.client.images.ButtonImages;

/*
 *  Image widget which acts as push-button.
 *  Could be supplied with up to three images for various button state.
 *  Also supports title for tool-tip.   
 */
public class IconButton extends Image {

    private final String title;

    private HandlerRegistration registration;

    public IconButton(String title) {
        this(title, null);
    }

    public IconButton(String title, final ButtonImages images) {
        this(title, images, null);
    }

    public IconButton(String title, final ButtonImages images, final Command command) {
        this.title = title;
        setImages(images);
        setCommand(command);
        getElement().getStyle().setProperty("backgroundSize", "contain");

        addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                if (images != null) {
                    setResource(images.hover());
                }
            }
        });

        addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                if (images != null) {
                    setResource(images.active());
                }
            }
        });

        addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                if (images != null) {
                    setResource(images.hover());
                }
            }
        });

        addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                if (images != null) {
                    setResource(images.regular());
                }
            }
        });
    }

    public void setImages(ButtonImages images) {
        if (images != null) {
            setResource(images.regular());
            getElement().getStyle().setCursor(Cursor.POINTER);
            setTitle(title);

        }
    }

    public void setCommand(final Command command) {
        if (command != null) {
            if (registration != null) {
                registration.removeHandler();
            }
            registration = addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    command.execute();
                }
            });
        }
    }
}
