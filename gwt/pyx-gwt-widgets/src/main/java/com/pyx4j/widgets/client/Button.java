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

import java.util.List;

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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;

public class Button extends FocusPanel implements IFocusWidget {

    private final HTML textLabel;

    private final SimplePanel imageHolder;

    private final ImageResource imageResource;

    private final ButtonFacesHandler buttonFacesHandler;

    private DropDownPanel popup;

    private Command command;

    public Button(ImageResource imageResource) {
        this(imageResource, (String) null);
    }

    public Button(String text) {
        this((ImageResource) null, text);
    }

    public Button(ImageResource imageResource, Command command) {
        this(imageResource);
        this.command = command;
    }

    public Button(String text, Command command) {
        this((ImageResource) null, text);
        this.command = command;
    }

    public Button(ImageResource imageResource, String text, Command command) {
        this(imageResource, text);
        this.command = command;
    }

    public Button(ImageResource imageResource, final String text) {
        this(new ButtonFacesHandler(), imageResource, text);
    }

    protected Button(ButtonFacesHandler facesHandler, ImageResource imageResource, String text) {

        this.imageResource = imageResource;

        setStylePrimaryName(getElement(), DefaultWidgetsTheme.StyleName.Button.name());

        buttonFacesHandler = facesHandler;

        facesHandler.init(this);

        textLabel = new HTML();
        setTextLabel(text);

        textLabel.setStyleName(DefaultWidgetsTheme.StyleName.ButtonText.name());

        imageHolder = new SimplePanel();

        imageHolder.setWidget(textLabel);

        setWidget(imageHolder);

        if (imageResource != null) {
            setImageVisible(true);
        }

        super.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (popup != null) {
                    if (popup.isShowing()) {
                        popup.hide();
                    } else if (isEnabled()) {
                        popup.showRelativeTo(Button.this);
                    }
                } else {
                    if (isEnabled() && (command != null)) {
                        command.execute();
                    }
                }
            }
        });
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    /**
     * @deprecated Use setCommand(new Command(){})
     */
    @Override
    @Deprecated
    public HandlerRegistration addClickHandler(final ClickHandler handler) {
        ClickHandler wrapper = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (isEnabled()) {
                    handler.onClick(event);
                }
            }
        };
        return addDomHandler(wrapper, ClickEvent.getType());
    }

    public ButtonMenuBar createMenu() {
        ButtonMenuBar menu = new ButtonMenuBar();
        return menu;
    }

    public void setMenu(ButtonMenuBar menu) {
        if (menu == null) {
            popup = null;
            textLabel.getElement().getStyle().setProperty("paddingRight", "0");
            textLabel.getElement().getStyle().setProperty("background", "none");
        } else {
            Image menuIndicator = new Image(ImageFactory.getImages().viewMenu());
            textLabel.getElement().getStyle().setProperty("paddingRight", (menuIndicator.getWidth() + 5) + "px");
            textLabel.getElement().getStyle().setProperty("background", "url('" + menuIndicator.getUrl() + "') no-repeat scroll right 90%");
            popup = new DropDownPanel();
            popup.setWidget(menu);
        }
    }

    public void setTextLabel(String label) {
        if (label == null) {
            label = "";
        }
        textLabel.setHTML(label);
    }

    protected HTML getTextLabelComponent() {
        return textLabel;
    }

    public void setCaption(String text) {
        textLabel.setText(text);
    }

    public void setTooltip(String text) {
        setTitle(text);
    }

    public void setImageVisible(boolean visible) {
        if (imageResource != null) {
            if (visible) {
                imageHolder.getElement().getStyle().setProperty("paddingLeft", imageResource.getWidth() + "px");
                imageHolder.getElement().getStyle().setProperty("height", "100%");
                imageHolder.getElement().getStyle()
                        .setProperty("background", "url('" + imageResource.getSafeUri().asString() + "') no-repeat scroll left center");
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
    }

    @Override
    protected void onUnload() {
        buttonFacesHandler.onUnload();
    }

    static class ButtonFacesHandler implements MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseUpHandler, ClickHandler {

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
            button.addClickHandler(this);
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
                // IE8: fix for Buttons remain in Mouse Over position after they are clicked in filter
                mouseOver = false;
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

        public void onUnload() {
            // fix for Buttons remain in Mouse Over position after they are clicked.
            mouseOver = false;
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

        @Override
        public void onClick(ClickEvent event) {
            // fix for Buttons remain in Mouse Over position after they are clicked.
            button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.pushed.name());
            button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
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
            boolean empty = getItems().isEmpty();
            if (!empty) {
                empty = true;
                for (MenuItem item : getItems()) {
                    if (item.isVisible()) {
                        empty = false;
                    }
                }
            }
            return empty;
        }

        @Override
        public List<MenuItem> getItems() {
            return super.getItems();
        }
    }
}