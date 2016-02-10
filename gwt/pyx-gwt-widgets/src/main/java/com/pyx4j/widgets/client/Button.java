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
 */
package com.pyx4j.widgets.client;

import java.util.List;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.security.annotations.ActionId;
import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class Button extends ButtonBase {

    private ButtonMenuBar menu;

    private ImageResource imageResource;

    public Button(ImageResource imageResource) {
        this(imageResource, (String) null);
    }

    public Button(ImageResource imageResource, final String text) {
        this(imageResource, text, null, null);
    }

    public Button(ImageResource imageResource, final String text, Command command) {
        this(imageResource, text, command, null);
    }

    public Button(ImageResource imageResource, Command command) {
        this(imageResource, command, null);
    }

    public Button(ImageResource imageResource, Command command, Class<? extends ActionId> actionId) {
        this(imageResource, (String) null, command, actionId);
    }

    public Button(ImageResource imageResource, String text, Command command, Class<? extends ActionId> actionId) {
        this(text, command, actionId);
        setImage(imageResource);
    }

    public Button(String text) {
        this(text, (Permission[]) null);
    }

    public Button(String text, Permission... permission) {
        this(text, null, permission);
    }

    public Button(String text, Command command) {
        this(text, command, (Permission[]) null);
    }

    public Button(String text, Command command, Class<? extends ActionId> actionId) {
        this(text, command, actionId == null ? null : new Permission[] { new ActionPermission(actionId) });
    }

    public Button(String text, Command command, Permission... permission) {
        super(null, text, command, permission);
        setStylePrimaryName(getElement(), WidgetsTheme.StyleName.Button.name());
        getTextLabel().setStyleName(WidgetsTheme.StyleName.ButtonText.name());
    }

    @Override
    protected void execute(HumanInputInfo humanInputInfo) {
        if (menu != null) {
            if (menu.getMenuPopup().isShowing()) {
                menu.getMenuPopup().hide();
            } else if (isEnabled()) {
                menu.getMenuPopup().showRelativeTo(Button.this);
                menu.getElement().getStyle().setProperty("minWidth", getOffsetWidth() + "px");
            }
        } else {
            super.execute(humanInputInfo);
        }
    }

    public void setImage(ImageResource imageResource) {
        this.imageResource = imageResource;
        updateImageState();
    }

    @Override
    protected void updateImageState() {
        if (imageResource != null) {
            getImageHolder().getElement().getStyle().setProperty("paddingLeft", imageResource.getWidth() + "px");
            getImageHolder().getElement().getStyle().setProperty("background",
                    "url('" + imageResource.getSafeUri().asString() + "') no-repeat scroll left center");
        } else {
            super.updateImageState();
        }
    }

    public void setMenu(ButtonMenuBar menu) {
        this.menu = menu;
        String label = getTextLabel().getText();
        //this will call local setTextLabel and reset the label text if the menu is not null
        setTextLabel(label);
    }

    public ButtonMenuBar getMenu() {
        return menu;
    }

    @Override
    public void setSecurityContext(AccessControlContext context) {
        super.setSecurityContext(context);
        if (menu != null) {
            menu.setSecurityContext(context);
        }
    }

    @Deprecated
    public ButtonMenuBar createMenu() {
        ButtonMenuBar menu = new ButtonMenuBar();
        return menu;
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

    @Override
    public void setTextLabel(String label) {
        if (menu != null) {
            Label downArrow = new Label("\u25bc");
            downArrow.setStyleName(WidgetsTheme.StyleName.DownArrow.name());
            super.setTextLabel(label + downArrow);
        } else {
            super.setTextLabel(label);
        }
    }
}