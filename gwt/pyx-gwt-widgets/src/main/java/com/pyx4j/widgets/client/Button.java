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

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.gwt.commons.concerns.ConcernStateChangeEvent;
import com.pyx4j.security.annotations.ActionId;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class Button extends ButtonBase {

    private ContextMenuHolder menuHolder;

    private MenuBar menu;

    private HandlerRegistration menuHandlerRegistration;

    private final Label buttonMenuIndicator;

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

        buttonMenuIndicator = new Label(String.valueOf(HtmlUtils.TRIANGLE_DOWN_SMALL_UTF8));
        buttonMenuIndicator.setStyleName(WidgetsTheme.StyleName.ButtonText.name());
        buttonMenuIndicator.addStyleName(WidgetsTheme.StyleName.ButtonMenuIndicator.name());
        buttonMenuIndicator.setVisible(false);
        getImageHolder().add(buttonMenuIndicator);

        visible(() -> (getMenu() == null || (getCommand() != null || (getMenu() != null && !getMenu().isMenuEmpty()))), "Menu|Command");

    }

    @Override
    protected final void execute(HumanInputInfo humanInputInfo) {
        if ((menu != null) && isEnabled()) {
            menuHolder.togleMenu();
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
            getImageHolder().getStyle().setProperty("paddingLeft", imageResource.getWidth() + "px");
            getImageHolder().getStyle().setProperty("background", "url('" + imageResource.getSafeUri().asString() + "') no-repeat scroll left center");
        } else {
            super.updateImageState();
        }
    }

    public void setMenu(MenuBar menu) {
        if (menuHolder == null && menu != null) {
            menuHolder = new ContextMenuHolder();
            getImageHolder().add(menuHolder);
        }
        if (menuHolder != null) {
            menuHolder.setMenu(menu);
        }

        if (this.menu != null) {
            removeSecureConcern(this.menu);
            menuHandlerRegistration.removeHandler();
        }

        this.menu = menu;
        if (menu != null) {
            addSecureConcern(menu);

            applyVisibilityRules();
            menuHandlerRegistration = menu.addConcernStateChangeHandler(new ConcernStateChangeEvent.Handler() {
                @Override
                public void onSecureConcernStateChanged(ConcernStateChangeEvent event) {
                    applyConcernRules();
                }
            });
        }
    }

    public MenuBar getMenu() {
        return menu;
    }

    @Override
    public void applyVisibilityRules() {
        if (buttonMenuIndicator != null) {
            buttonMenuIndicator.setVisible(this.menu != null && this.menu.isVisible());
        }
        super.applyVisibilityRules();
    }

    @Deprecated
    public ButtonMenuBar createMenu() {
        ButtonMenuBar menu = new ButtonMenuBar();
        return menu;
    }

    /**
     *
     * @deprecated renamed to com.pyx4j.widgets.client.MenuBar
     *
     */
    @Deprecated
    public static class ButtonMenuBar extends com.pyx4j.widgets.client.MenuBar {

        public ButtonMenuBar() {
            super();
        }
    }

    /**
     *
     * @deprecated renamed to com.pyx4j.widgets.client.MenuItem
     *
     */
    @Deprecated
    public static class SecureMenuItem extends com.pyx4j.widgets.client.MenuItem {

        public SecureMenuItem(String text, Class<? extends ActionId> actionId, ScheduledCommand cmd) {
            super(text, cmd, actionId);
        }

        public SecureMenuItem(String text, ButtonMenuBar subMenu, Permission... permissions) {
            super(text, subMenu, permissions);
        }

        public SecureMenuItem(String text, ScheduledCommand cmd, Class<? extends ActionId> actionId) {
            super(text, cmd, actionId);
        }

        public SecureMenuItem(String text, ScheduledCommand cmd, Permission... permissions) {
            super(text, cmd, permissions);
        }

    }

}