/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
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

import com.pyx4j.widgets.client.style.Theme.CSSClass;

public class Button extends ButtonBase {

    public Button(Image image) {
        this(image, null);
    }

    public Button(String text) {
        this(null, text);
    }

    public Button(String text, ClickHandler handler) {
        this(null, text);
        this.addClickHandler(handler);
    }

    public Button(Image image, final String text) {
        this(image, text, new ButtonFacesHandler());
    }

    protected Button(Image image, final String text, ButtonFacesHandler facesHandler) {
        super(DOM.createSpan());
        getElement().getStyle().setProperty("display", "inline-block");
        getElement().getStyle().setProperty("verticalAlign", "top");
        // for IE6
        // getElement().getStyle().setProperty("borderColor", "pink");
        // getElement().getStyle().setProperty("filter", "chroma(color=pink)");

        facesHandler.install(this);

        Element content = DOM.createSpan();
        content.getStyle().setProperty("display", "inline");
        content.getStyle().setProperty("whiteSpace", "nowrap");
        content.getStyle().setProperty("verticalAlign", "middle");

        if (image != null) {
            Element imageElem = image.getElement();
            imageElem.getStyle().setProperty("verticalAlign", "middle");
            content.appendChild(imageElem);
        }
        if (text != null) {
            Element textElem = DOM.createSpan();
            textElem.setInnerText(text);
            content.appendChild(textElem);
        }
        getElement().appendChild(content);

        setStylePrimaryName(CSSClass.pyx4j_Button.name());

        setTabIndex(-1);

    }

    public void setTooltip(String text) {
        Tooltip.tooltip(this, text);
    }

    static class ButtonFacesHandler implements MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseUpHandler {

        private Button button;

        public ButtonFacesHandler() {
        }

        public void install(Button button) {
            this.button = button;
            button.addMouseOverHandler(this);
            button.addMouseOutHandler(this);
            button.addMouseDownHandler(this);
            button.addMouseUpHandler(this);
        }

        @Override
        public void onMouseOver(MouseOverEvent event) {
            button.removeStyleDependentName("pushed");
            button.addStyleDependentName("hover");
        }

        @Override
        public void onMouseOut(MouseOutEvent event) {
            button.removeStyleDependentName("hover");
            button.removeStyleDependentName("pushed");
        }

        @Override
        public void onMouseDown(MouseDownEvent event) {
            button.removeStyleDependentName("hover");
            button.addStyleDependentName("pushed");
        }

        @Override
        public void onMouseUp(MouseUpEvent event) {
            button.removeStyleDependentName("pushed");
            button.addStyleDependentName("hover");
        }

    }

}