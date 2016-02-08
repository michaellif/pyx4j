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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.security.annotations.ActionId;
import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.widgets.client.images.ButtonImages;
import com.pyx4j.widgets.client.style.theme.WidgetTheme;

public class Button extends FocusPanel implements IFocusWidget, HasSecureConcern {

    private final HTML textLabel;

    private final SimplePanel imageHolder;

    private final ButtonFacesHandler buttonFacesHandler;

    private Command command;

    private ButtonMenuBar menu;

    private ImageResource singleImage;

    private ButtonImages imageBundle;

    private boolean active = false;

    private final SecureConcern visible = new SecureConcern();

    public Button(ImageResource imageResource) {
        this(imageResource, (String) null);
    }

    public Button(String text) {
        this((ImageResource) null, text);
    }

    public Button(String text, Permission... permission) {
        this((ImageResource) null, text);
        this.setPermission(permission);
    }

    public Button(String text, Class<? extends ActionId> actionId) {
        this(text, new ActionPermission(actionId));
    }

    public Button(ImageResource imageResource, Command command) {
        this(imageResource);
        this.command = command;
    }

    public Button(ImageResource imageResource, Command command, Class<? extends ActionId> actionId) {
        this(imageResource);
        this.command = command;
        this.setPermission(new ActionPermission(actionId));
    }

    public Button(String text, Command command) {
        this((ImageResource) null, text);
        this.command = command;
    }

    public Button(String text, Command command, Permission... permission) {
        this(text, command);
        this.setPermission(permission);
    }

    public Button(String text, Command command, Class<? extends ActionId> actionId) {
        this(text, command, new ActionPermission(actionId));
    }

    public Button(ImageResource imageResource, String text, Command command) {
        this(imageResource, text);
        this.command = command;
    }

    public Button(ImageResource imageResource, final String text) {
        this(new ButtonFacesHandler(), imageResource, text);
    }

    protected Button(ButtonFacesHandler facesHandler, ImageResource imageResource, String text) {

        setStylePrimaryName(getElement(), WidgetTheme.StyleName.Button.name());

        buttonFacesHandler = facesHandler;

        facesHandler.init(this);

        textLabel = new HTML();
        setTextLabel(text);

        textLabel.setStyleName(WidgetTheme.StyleName.ButtonText.name());

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
                        // start changes to merge
                        if (command instanceof HumanInputCommand) {
                            ((HumanInputCommand) command).execute(new HumanInputInfo(event));
                        } else {
                            command.execute();
                        }
                        // end changes to merge
                        if (isActive()) {
                            addStyleDependentName(WidgetTheme.StyleDependent.active.name());
                        } else {
                            removeStyleDependentName(WidgetTheme.StyleDependent.active.name());

                        }
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
            imageHolder.getElement().getStyle().setProperty("background",
                    "url('" + imageBundle.regular().getSafeUri().asString() + "') no-repeat scroll left center");
        } else {
            imageHolder.getElement().getStyle().setProperty("paddingLeft", "0px");
            imageHolder.getElement().getStyle().setProperty("background", "none");
        }
    }

    public void setPermission(Permission... permission) {
        visible.setPermission(permission);
        setVisibleImpl();
    }

    private void setVisibleImpl() {
        super.setVisible(this.visible.getDecision());
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible.setDecision(visible);
        if (this.visible.hasDecision()) {
            setVisibleImpl();
        }
    }

    @Override
    public void setSecurityContext(AccessControlContext context) {
        visible.setContext(context);
        setVisibleImpl();
        if (menu != null) {
            menu.setSecurityContext(context);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void toggleActive() {
        this.fireEvent(new ClickEvent() {
        });
        buttonFacesHandler.setActive(isActive());
    }

    public void click() {
        DomEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), this);
    }

    @Override
    public boolean isEnabled() {
        return !getElement().getPropertyBoolean("disabled");
    }

    @Override
    public void setEnabled(boolean enabled) {
        getElement().setPropertyBoolean("disabled", !enabled);
        buttonFacesHandler.setEnabled(enabled);
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        ensureDebugId(debugId.debugId());
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

    @Override
    public void setEditable(boolean editable) {
    }

    @Override
    public boolean isEditable() {
        return false;
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
                button.removeStyleDependentName(WidgetTheme.StyleDependent.disabled.name());
                if (mouseOver) {
                    onMouseOver(null);
                }
            } else {
                button.addStyleDependentName(WidgetTheme.StyleDependent.disabled.name());
                button.removeStyleDependentName(WidgetTheme.StyleDependent.active.name());
                button.removeStyleDependentName(WidgetTheme.StyleDependent.hover.name());
                // IE8: fix for Buttons remain in Mouse Over position after they are clicked in filter
                mouseOver = false;
            }
        }

        public void setActive(boolean active) {
            if (button == null) {
                return;
            }
            if (button.isEnabled() && active) {
                button.addStyleDependentName(WidgetTheme.StyleDependent.active.name());
            } else {
                button.removeStyleDependentName(WidgetTheme.StyleDependent.active.name());
            }
        }

        @Override
        public void onMouseOver(MouseOverEvent event) {
            mouseOver = true;
            if (button.isEnabled()) {
                button.addStyleDependentName(WidgetTheme.StyleDependent.hover.name());
            }
        }

        @Override
        public void onMouseOut(MouseOutEvent event) {
            mouseOver = false;
            if (button.isEnabled()) {
                button.removeStyleDependentName(WidgetTheme.StyleDependent.hover.name());
            }
        }

        public void onUnload() {
            // fix for Buttons remain in Mouse Over position after they are clicked.
            mouseOver = false;
        }

        @Override
        public void onMouseDown(MouseDownEvent event) {
            if (button.isEnabled()) {
                if (!button.isActive()) {
                    button.removeStyleDependentName(WidgetTheme.StyleDependent.hover.name());
                    button.addStyleDependentName(WidgetTheme.StyleDependent.active.name());
                }
            }
        }

        @Override
        public void onMouseUp(MouseUpEvent event) {
            if (button.isEnabled()) {
                if (button.isActive()) {
                    button.addStyleDependentName(WidgetTheme.StyleDependent.active.name());
                } else {
                    button.removeStyleDependentName(WidgetTheme.StyleDependent.active.name());
                    button.addStyleDependentName(WidgetTheme.StyleDependent.hover.name());
                }
            }
        }

        @Override
        public void onClick(ClickEvent event) {
            // fix for Buttons remain in Mouse Over position after they are clicked.

            button.removeStyleDependentName(WidgetTheme.StyleDependent.hover.name());

        }

        public Button getButton() {
            return button;
        }

    }

    public static class ButtonMenuBar extends MenuBar implements HasSecureConcern {

        private final DropDownPanel popup;

        private final SecureConcernsHolder secureConcerns = new SecureConcernsHolder();

        private HumanInputInfo humanInputInfo = HumanInputInfo.robot;

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
                        if (origCommand instanceof HumanInputCommand) {
                            ((HumanInputCommand) origCommand).execute(humanInputInfo);
                        } else {
                            origCommand.execute();
                        }

                    }
                });
            }
            if (item instanceof HasSecureConcern) {
                secureConcerns.addSecureConcern((HasSecureConcern) item);
            }
            return super.insertItem(item, beforeIndex);
        }

        public SecureMenuItem addItem(String text, ScheduledCommand cmd, Permission... permissions) {
            SecureMenuItem menuItem = new SecureMenuItem(text, cmd, permissions);
            addItem(menuItem);
            return menuItem;
        }

        public SecureMenuItem addItem(String text, ScheduledCommand cmd, Class<? extends ActionId> actionId) {
            SecureMenuItem menuItem = new SecureMenuItem(text, cmd, actionId);
            addItem(menuItem);
            return menuItem;
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

        public boolean isControlKeyDown() {
            return humanInputInfo.isControlKeyDown();
        }

        @Override
        public void onBrowserEvent(Event event) {
            if ((DOM.eventGetType(event) == Event.ONCLICK) && (event.getCtrlKey())) {
                humanInputInfo = new HumanInputInfo(event);
            } else {
                humanInputInfo = HumanInputInfo.robot;
            }
            super.onBrowserEvent(event);
        }

        @Override
        public List<MenuItem> getItems() {
            return super.getItems();
        }

        public DropDownPanel getMenuPopup() {
            return popup;
        }

        @Override
        public void clearItems() {
            super.clearItems();
            secureConcerns.clear();
        }

        @Override
        public void setSecurityContext(AccessControlContext context) {
            secureConcerns.setSecurityContext(context);
        }
    }

    public static class SecureMenuItem extends MenuItem implements HasSecureConcern {

        private final SecureConcern visible = new SecureConcern();

        public SecureMenuItem(String text, ScheduledCommand cmd, Permission... permissions) {
            super(text, cmd);
            setPermission(permissions);
        }

        public SecureMenuItem(String text, ScheduledCommand cmd, Class<? extends ActionId> actionId) {
            this(text, cmd, new ActionPermission(actionId));
        }

        public void setPermission(Permission... permission) {
            visible.setPermission(permission);
            setVisibleImpl();
        }

        private void setVisibleImpl() {
            super.setVisible(this.visible.getDecision());
        }

        @Override
        public void setVisible(boolean visible) {
            this.visible.setDecision(visible);
            if (this.visible.hasDecision()) {
                setVisibleImpl();
            }
        }

        @Override
        public void setSecurityContext(AccessControlContext context) {
            visible.setContext(context);
            super.setVisible(visible.getDecision());
        }

    }

}