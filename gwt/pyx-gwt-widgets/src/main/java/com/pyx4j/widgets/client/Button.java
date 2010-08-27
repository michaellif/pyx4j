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

import com.pyx4j.widgets.client.style.CSSClass;

public class Button extends ButtonBase {

    private final Element content;

    private Element textElem;

    private final ButtonFacesHandler buttonFacesHandler;

    public Button(Image image) {
        this(image, null);
    }

    public Button(String text) {
        this((Image) null, text);
    }

    public Button(String text, String stylePrefix) {
        this(null, text, stylePrefix);
    }

    public Button(String text, ClickHandler handler) {
        this((Image) null, text);
        this.addClickHandler(handler);
    }

    public Button(Image image, final String text, String stylePrefix) {
        this(image, text, new ButtonFacesHandler(), stylePrefix);
    }

    public Button(Image image, final String text) {
        this(image, text, new ButtonFacesHandler());
    }

    public Button(Image image, final String text, ButtonFacesHandler facesHandler) {
        this(image, text, facesHandler, null);
    }

    protected Button(Image image, final String text, ButtonFacesHandler facesHandler, String stylePrefix) {
        super(DOM.createDiv());

        if (stylePrefix == null) {
            stylePrefix = CSSClass.pyx4j_Button.name();
        }

        Element container = DOM.createSpan();

        buttonFacesHandler = facesHandler;

        container.getStyle().setProperty("display", "inline-block");
        container.getStyle().setProperty("verticalAlign", "top");
        setStylePrimaryName(container, stylePrefix);

        // for IE6
        // getElement().getStyle().setProperty("borderColor", "pink");
        // getElement().getStyle().setProperty("filter", "chroma(color=pink)");

        facesHandler.init(this);

        content = DOM.createSpan();
        content.getStyle().setProperty("display", "inline-block");
        content.getStyle().setProperty("whiteSpace", "nowrap");
        content.getStyle().setProperty("verticalAlign", "middle");
        content.getStyle().setProperty("height", "100%");

        setStylePrimaryName(content, stylePrefix + "Content");

        if (image != null) {
            Element imageElem = image.getElement();
            imageElem.getStyle().setProperty("verticalAlign", "middle");
            imageElem.getStyle().setProperty("display", "inline-block");
            setStylePrimaryName(imageElem, stylePrefix + "Image");
            content.appendChild(imageElem);
        }
        if (text != null) {
            textElem = DOM.createSpan();
            textElem.setInnerHTML(text);
            textElem.getStyle().setProperty("verticalAlign", "middle");
            textElem.getStyle().setProperty("display", "inline-block");
            content.appendChild(textElem);
            setStylePrimaryName(textElem, stylePrefix + "Text");
        }

        getElement().appendChild(container);
        container.appendChild(content);

    }

    public void setCaption(String html) {
        textElem.setInnerHTML(html);
    }

    public void setTooltip(String text) {
        Tooltip.tooltip(this, text);
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
                button.removeStyleDependentName("disabled");
                if (mouseOver) {
                    onMouseOver(null);
                }
            } else {
                button.addStyleDependentName("disabled");
                button.removeStyleDependentName("pushed");
                button.removeStyleDependentName("hover");
            }
        }

        @Override
        public void onMouseOver(MouseOverEvent event) {
            mouseOver = true;
            if (isEnabled()) {
                button.removeStyleDependentName("pushed");
                button.addStyleDependentName("hover");
            }
        }

        @Override
        public void onMouseOut(MouseOutEvent event) {
            mouseOver = false;
            if (isEnabled()) {
                button.removeStyleDependentName("hover");
                button.removeStyleDependentName("pushed");
            }
        }

        @Override
        public void onMouseDown(MouseDownEvent event) {
            if (isEnabled()) {
                button.removeStyleDependentName("hover");
                button.addStyleDependentName("pushed");
            }
        }

        @Override
        public void onMouseUp(MouseUpEvent event) {
            if (isEnabled()) {
                button.removeStyleDependentName("pushed");
                button.addStyleDependentName("hover");
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