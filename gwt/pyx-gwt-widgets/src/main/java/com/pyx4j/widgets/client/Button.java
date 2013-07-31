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

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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

import com.pyx4j.widgets.client.images.ButtonImages;

public class Button extends FocusPanel implements IFocusWidget {

    private final HTML textLabel;

    private final SimplePanel imageHolder;

    private final ButtonFacesHandler buttonFacesHandler;

    private Command command;

    private ButtonMenuBar menu;

    private ImageResource singleImage;

    private ButtonImages imageBundle;

    private boolean active = false;

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

        setStylePrimaryName(getElement(), DefaultWidgetsTheme.StyleName.Button.name());

        buttonFacesHandler = facesHandler;

        facesHandler.init(this);

        textLabel = new HTML();
        setTextLabel(text);

        textLabel.setStyleName(DefaultWidgetsTheme.StyleName.ButtonText.name());

        imageHolder = new SimplePanel();
        imageHolder.getElement().getStyle().setProperty("height", "100%");

        imageHolder.setWidget(textLabel);

        setWidget(imageHolder);

        setImage(imageResource);

        super.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (menu != null) {
                    if (menu.getMenuPopup().isShowing()) {
                        menu.getMenuPopup().hide();
                    } else if (isEnabled()) {
                        menu.getMenuPopup().showRelativeTo(Button.this);
                        menu.getElement().getStyle().setProperty("minWidth", getOffsetWidth() + "px");
                    }
                } else {
                    if (isEnabled() && (command != null)) {
                        active = !active;
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
        return super.addClickHandler(wrapper);
    }

    @Deprecated
    public ButtonMenuBar createMenu() {
        ButtonMenuBar menu = new ButtonMenuBar();
        return menu;
    }

    public void setMenu(ButtonMenuBar menu) {
        this.menu = menu;
        if (menu == null) {
            textLabel.getElement().getStyle().setProperty("paddingRight", "0");
            textLabel.getElement().getStyle().setProperty("background", "none");
        } else {
            Image menuIndicator = new Image(ImageFactory.getImages().viewMenu());
            textLabel.getElement().getStyle().setProperty("paddingRight", (menuIndicator.getWidth() + 5) + "px");
            textLabel.getElement().getStyle().setProperty("background", "url('" + menuIndicator.getUrl() + "') no-repeat scroll right center");
        }
    }

    public void setTextLabel(String label) {
        textLabel.setHTML(label);
        textLabel.setVisible(label != null);
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

    public void setImage(ImageResource imageResource) {
        this.singleImage = imageResource;
        if (singleImage != null) {
            this.imageBundle = null;
        }
        updateImageState();
    }

    public void setImageBundle(ButtonImages imageBundle) {
        this.imageBundle = imageBundle;
        if (imageBundle != null) {
            this.singleImage = null;
        }
        updateImageState();
    }

    private void updateImageState() {
        if (singleImage != null) {
            imageHolder.getElement().getStyle().setProperty("paddingLeft", singleImage.getWidth() + "px");
            imageHolder.getElement().getStyle().setProperty("background", "url('" + singleImage.getSafeUri().asString() + "') no-repeat scroll left center");
        } else if (imageBundle != null) {
            imageHolder.getElement().getStyle().setProperty("paddingLeft", singleImage.getWidth() + "px");
            imageHolder.getElement().getStyle()
                    .setProperty("background", "url('" + imageBundle.regular().getSafeUri().asString() + "') no-repeat scroll left center");
        } else {
            imageHolder.getElement().getStyle().setProperty("paddingLeft", "0px");
            imageHolder.getElement().getStyle().setProperty("background", "none");
        }
    }

    public boolean isActive() {
        return active;
    }

    public void toggleActive() {
        this.fireEvent(new ClickEvent() {
        });
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
        buttonFacesHandler.setEnabled(enabled);
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

        private boolean mouseOver = false;

        public ButtonFacesHandler() {
        }

        void init(Button button) {
            this.button = button;
            button.addMouseOverHandler(this);
            button.addMouseOutHandler(this);
            button.addMouseDownHandler(this);
            button.addMouseUpHandler(this);
            button.addClickHandler(this);

        }

        public void setEnabled(boolean enabled) {
            if (button == null) {
                return;
            }
            if (enabled) {
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.disabled.name());
                if (mouseOver) {
                    onMouseOver(null);
                }
            } else {
                button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.disabled.name());
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.active.name());
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
                // IE8: fix for Buttons remain in Mouse Over position after they are clicked in filter
                mouseOver = false;
            }
        }

        public void setActive(boolean active) {
            if (button == null) {
                return;
            }
            if (button.isEnabled()) {
                button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.active.name());
            }
        }

        @Override
        public void onMouseOver(MouseOverEvent event) {
            mouseOver = true;
            if (button.isEnabled()) {
                if (!button.active) {
                    button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
                }
            }
        }

        @Override
        public void onMouseOut(MouseOutEvent event) {
            mouseOver = false;
            if (button.isEnabled()) {
                if (!button.active) {
                    button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
                }
            }
        }

        public void onUnload() {
            // fix for Buttons remain in Mouse Over position after they are clicked.
            mouseOver = false;
        }

        @Override
        public void onMouseDown(MouseDownEvent event) {
            if (button.isEnabled()) {
                if (!button.active) {
                    button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
                    button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.active.name());
                }
            }
        }

        @Override
        public void onMouseUp(MouseUpEvent event) {
            if (button.isEnabled()) {
                if (!button.active) {
                    button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.active.name());
                    button.addStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
                }
            }
        }

        @Override
        public void onClick(ClickEvent event) {
            if (!button.active) {
                // fix for Buttons remain in Mouse Over position after they are clicked.
                button.removeStyleDependentName(DefaultWidgetsTheme.StyleDependent.hover.name());
            }
        }

        public Button getButton() {
            return button;
        }

    }

    @Override
    public void setEditable(boolean editable) {
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    public static class ButtonMenuBar extends MenuBar {

        private final DropDownPanel popup;

        public ButtonMenuBar() {
            super(true);
            setAutoOpen(true);
            setAnimationEnabled(true);
            popup = new DropDownPanel();
            popup.setWidget(this);
        }

        @Override
        public MenuItem insertItem(MenuItem item, int beforeIndex) {
            if (item.getScheduledCommand() != null) {
                final ScheduledCommand origCommand = item.getScheduledCommand();
                item.setScheduledCommand(new Command() {

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

        public DropDownPanel getMenuPopup() {
            return popup;
        }
    }
}