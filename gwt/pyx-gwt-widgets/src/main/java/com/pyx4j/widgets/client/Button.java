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

import com.google.gwt.dom.client.ButtonElement;
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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class Button extends FocusPanel {

    private Label textLabel;

    private final Image image;

    private final ButtonFacesHandler buttonFacesHandler;

    private final SimplePanel contentPanel;

    public Button(Image image) {
        this(image, null);
    }

    public Button(String text) {
        this((Image) null, text);
    }

    public Button(String text, ClickHandler handler) {
        this((Image) null, text);
        addClickHandler(handler);
    }

    public Button(Image image, String text, ClickHandler handler) {
        this(image, text);
        addClickHandler(handler);
    }

    public Button(Image image, final String text) {
        this(new ButtonFacesHandler(), image, text);
    }

    protected Button(ButtonFacesHandler facesHandler, Image image, final String text) {

        this.image = image;

        setStylePrimaryName(getElement(), DefaultWidgetsTheme.StyleName.Button.name());

        buttonFacesHandler = facesHandler;

        facesHandler.init(this);

        contentPanel = new SimplePanel();

        contentPanel.setStyleName(DefaultWidgetsTheme.StyleName.ButtonContent.name());

        if (text != null) {
            textLabel = new Label(text);
            contentPanel.setWidget(textLabel);
            textLabel.setStyleName(DefaultWidgetsTheme.StyleName.ButtonText.name());
        }
        if (image != null) {
            textLabel.getElement().getStyle().setProperty("paddingLeft", image.getWidth() + "px");
            contentPanel.getElement().getStyle().setProperty("background", "url('" + image.getUrl() + "') no-repeat");
        }

        setWidget(contentPanel);

    }

    @Override
    public void onBrowserEvent(Event event) {
        if (isEnabled()) {
            super.onBrowserEvent(event);
        } else {
            event.stopPropagation();
        }
    };

    public void setCaption(String text) {
        textLabel.setText(text);
    }

    public void setTooltip(String text) {
        setTitle(text);
    }

    public void setImageVisible(boolean visible) {
        if (image != null) {
            if (visible) {
                textLabel.getElement().getStyle().setProperty("paddingLeft", image.getWidth() + "px");
                contentPanel.getElement().getStyle().setProperty("background", "url('" + image.getUrl() + "') no-repeat");
            } else {
                textLabel.getElement().getStyle().setProperty("paddingLeft", "0px");
                contentPanel.getElement().getStyle().setProperty("background", "none");
            }
        }
    }

    public void click() {
        getButtonElement().click();
    }

    protected ButtonElement getButtonElement() {
        return ((ButtonElement) getElement().cast());
    }

    public boolean isEnabled() {
        return !DOM.getElementPropertyBoolean(getElement(), "disabled");
    }

    public void setEnabled(boolean enabled) {
        DOM.setElementPropertyBoolean(getElement(), "disabled", !enabled);
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