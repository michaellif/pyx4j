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
 * Created on May 8, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Image;

public class Button extends ButtonBase {

    private final Element container;

    private final Element content;

    private Element textElem;

    private final ButtonFacesHandler buttonFacesHandler;

    public Button(Image image) {
        this(image, null);
    }

    public Button(String text) {
        this((Image) null, text);
    }

    public Button(String text, ClickHandler handler) {
        this((Image) null, text);
        this.addClickHandler(handler);
    }

    public Button(Image image, String text, ClickHandler handler) {
        this(image, text);
        this.addClickHandler(handler);
    }

    public Button(Image image, final String text) {
        this(new ButtonFacesHandler(), image, text);
    }

    protected Button(ButtonFacesHandler facesHandler, Image image, final String text) {
        super(DOM.createDiv());

        setStylePrimaryName(getElement(), DefaultWidgetsTheme.StyleName.Button.name());

        container = DOM.createSpan();
        setStylePrimaryName(container, DefaultWidgetsTheme.StyleName.ButtonContainer.name());

        buttonFacesHandler = facesHandler;

        container.getStyle().setProperty("display", "inline-block");
        container.getStyle().setProperty("verticalAlign", "top");

        // for IE6
        // getElement().getStyle().setProperty("borderColor", "pink");
        // getElement().getStyle().setProperty("filter", "chroma(color=pink)");

        facesHandler.init(this);

        content = DOM.createSpan();
        content.getStyle().setProperty("display", "inline-block");
        content.getStyle().setProperty("whiteSpace", "nowrap");
        content.getStyle().setProperty("verticalAlign", "middle");
        content.getStyle().setProperty("height", "100%");

        setStylePrimaryName(content, DefaultWidgetsTheme.StyleName.ButtonContent.name());

        if (image != null) {
            Element imageElem = image.getElement();
            imageElem.getStyle().setProperty("verticalAlign", "middle");
            imageElem.getStyle().setProperty("display", "inline-block");
            setStylePrimaryName(imageElem, DefaultWidgetsTheme.StyleName.ButtonImage.name());
            content.appendChild(imageElem);
        }
        if (text != null) {
            textElem = DOM.createSpan();
            textElem.setInnerHTML(text);
            textElem.getStyle().setProperty("verticalAlign", "middle");
            textElem.getStyle().setProperty("display", "inline-block");
            content.appendChild(textElem);
            setStylePrimaryName(textElem, DefaultWidgetsTheme.StyleName.ButtonText.name());
        }

        getElement().appendChild(container);
        container.appendChild(content);

    }

    public void setCaption(String html) {
        textElem.setInnerHTML(html);
    }

    public void setTooltip(String text) {
        setTitle(text);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        buttonFacesHandler.enable(enabled);
    }

    static class ButtonFacesHandler implements MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseUpHandler {

        private Button button;

        private boolean enabled = true;

        private boolean mouseOver = false;

        public ButtonFacesHandler() {
        }

        public void init(Button button) {
            this.button = button;
            button.addMouseOverHandler(this);
            button.addMouseOutHandler(this);
            button.addMouseDownHandler(this);
            button.addMouseUpHandler(this);
        }

        public void enable(boolean flag) {
            enabled = flag;
            if (flag) {
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.disabled.name());
                if (mouseOver) {
                    onMouseOver(null);
                }
            } else {
                button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.disabled.name());
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.pushed.name());
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
            }
        }

        @Override
        public void onMouseOver(MouseOverEvent event) {
            mouseOver = true;
            if (isEnabled()) {
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.pushed.name());
                button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
            }
        }

        @Override
        public void onMouseOut(MouseOutEvent event) {
            mouseOver = false;
            if (isEnabled()) {
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.pushed.name());
            }
        }

        @Override
        public void onMouseDown(MouseDownEvent event) {
            if (isEnabled()) {
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
                button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.pushed.name());
            }
        }

        @Override
        public void onMouseUp(MouseUpEvent event) {
            if (isEnabled()) {
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.pushed.name());
                button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
            }
        }

        public Button getButton() {
            return button;
        }

        public boolean isEnabled() {
            return enabled;
        }

    }

}