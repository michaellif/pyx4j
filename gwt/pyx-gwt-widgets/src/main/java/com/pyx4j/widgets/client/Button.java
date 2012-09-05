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

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;

public class Button extends FocusPanel implements IFocusWidget {

    private final HTML textLabel;

    private final SimplePanel imageHolder;

    private final Image image;

    private final ButtonFacesHandler buttonFacesHandler;

    private DropDownPanel popup;

    public Button(Image image) {
        this(image, (String) null);
    }

    public Button(String text) {
        this((Image) null, text);
    }

    public Button(Image image, ClickHandler handler) {
        this(image);
        addClickHandler(handler);
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

    protected Button(ButtonFacesHandler facesHandler, Image image, String text) {

        this.image = image;

        setStylePrimaryName(getElement(), DefaultWidgetsTheme.StyleName.Button.name());

        buttonFacesHandler = facesHandler;

        facesHandler.init(this);

        textLabel = new HTML();
        setTextLabel(text);

        textLabel.setStyleName(DefaultWidgetsTheme.StyleName.ButtonText.name());

        imageHolder = new SimplePanel();

        imageHolder.setWidget(textLabel);

        setWidget(imageHolder);

        if (image != null) {
            setImageVisible(true);
        }

    }

    public ButtonMenuBar createMenu() {
        ButtonMenuBar menu = new ButtonMenuBar();
        return menu;
    }

    public void setMenu(ButtonMenuBar menu) {
        Image menuIndicator = new Image(ImageFactory.getImages().viewMenu());
        textLabel.getElement().getStyle().setProperty("paddingRight", (menuIndicator.getWidth() + 5) + "px");
        textLabel.getElement().getStyle().setProperty("background", "url('" + menuIndicator.getUrl() + "') no-repeat scroll right 90%");

        popup = new DropDownPanel();
        popup.setWidget(menu);

        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (popup.isShowing()) {
                    popup.hide();
                } else {
                    popup.showRelativeTo(Button.this);
                }
            }
        });
    }

    public void setTextLabel(String label) {
        if (label == null) {
            label = "";
        }
        textLabel.setHTML(label);
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
                imageHolder.getElement().getStyle().setProperty("paddingLeft", image.getWidth() + "px");
                imageHolder.getElement().getStyle().setProperty("height", "100%");
                imageHolder.getElement().getStyle().setProperty("background", "url('" + image.getUrl() + "') no-repeat scroll left center");
            } else {
                imageHolder.getElement().getStyle().setProperty("paddingLeft", "0px");
                imageHolder.getElement().getStyle().setProperty("background", "none");
            }

        }
    }

    public void click() {
        DomEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), this);
    }

    @Override
    public boolean isEnabled() {
        return !DOM.getElementPropertyBoolean(getElement(), "disabled");
    }

    @Override
    public void setEnabled(boolean enabled) {
        DOM.setElementPropertyBoolean(getElement(), "disabled", !enabled);
        buttonFacesHandler.enable(enabled);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        if (textLabel != null) {
            textLabel.ensureDebugId(baseID);
        }
        if (this.image != null) {
            if (textLabel != null) {
                image.ensureDebugId(baseID);
            } else {
                image.ensureDebugId(baseID + "-image");
            }
        }
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

    @Override
    public void setEditable(boolean editable) {
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    public class ButtonMenuBar extends MenuBar {

        public ButtonMenuBar() {
            super(true);
            setAutoOpen(true);
            setAnimationEnabled(true);
        }

        @Override
        public MenuItem insertItem(MenuItem item, int beforeIndex) {
            if (item.getCommand() != null) {
                final Command origCommand = item.getCommand();
                item.setCommand(new Command() {

                    @Override
                    public void execute() {
                        popup.hide();
                        origCommand.execute();

                    }
                });
            }
            return super.insertItem(item, beforeIndex);
        }

        public boolean isMenuEmpty() {
            return getItems().isEmpty();
        }
    }

}