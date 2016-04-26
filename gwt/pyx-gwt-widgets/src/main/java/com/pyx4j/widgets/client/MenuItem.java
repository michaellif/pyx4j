/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 13, 2016
 * @author vlads
 */
package com.pyx4j.widgets.client;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import com.pyx4j.gwt.commons.concerns.AbstractConcern;
import com.pyx4j.gwt.commons.concerns.ConcernStateChangeEvent;
import com.pyx4j.gwt.commons.concerns.HasSecureConcernedChildren;
import com.pyx4j.gwt.commons.concerns.HasWidgetConcerns;
import com.pyx4j.security.annotations.ActionId;
import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.security.shared.ActionPermission;
import com.pyx4j.security.shared.Permission;

public class MenuItem extends com.google.gwt.user.client.ui.MenuItem implements HasWidgetConcerns, HasSecureConcernedChildren {

    private static final Logger log = LoggerFactory.getLogger(MenuItem.class);

    private HandlerManager handlerManager;

    protected final List<AbstractConcern> concerns = new ArrayList<>();

    private final SecureConcernsHolder secureConcernsHolder = new SecureConcernsHolder();

    private boolean updatingSubmenuIcon = false;

    public MenuItem(String text, Class<? extends ActionId> actionId, ScheduledCommand cmd) {
        this(text, cmd, actionId);
    }

    public MenuItem(String text, ScheduledCommand cmd, Permission... permissions) {
        super(text, cmd);
        setPermission(permissions);
    }

    public MenuItem(String text, ScheduledCommand cmd, Class<? extends ActionId> actionId) {
        this(text, cmd, new ActionPermission(actionId));
    }

    public MenuItem(String text, MenuBar subMenu, Permission... permissions) {
        super(SafeHtmlUtils.fromString(text));
        setSubMenu(subMenu);
        setPermission(permissions);
    }

    @Override
    public MenuBar getSubMenu() {
        if (updatingSubmenuIcon && !isVisible()) {
            return null;
        } else {
            return (MenuBar) super.getSubMenu();
        }
    }

    private boolean hasSubMenu() {
        return super.getSubMenu() != null;
    }

    @Override
    public void setSubMenu(com.google.gwt.user.client.ui.MenuBar subMenu) {
        assert getSubMenu() == null : "Override subMenu not supported";
        assert subMenu != null : "Removal of subMenu not supported";
        assert (subMenu instanceof MenuBar) : "Menu Hierarchy will not work when using raw GWT components";

        super.setSubMenu(subMenu);
        MenuBar subMenuBar = (MenuBar) subMenu;

        subMenuBar.setParentMenuItem(this);
        addSecureConcern(subMenuBar);

        visible(() -> subMenuBar.isVisible(), "SubMenuBar");

        subMenuBar.addConcernStateChangeHandler(new ConcernStateChangeEvent.Handler() {
            @Override
            public void onSecureConcernStateChanged(ConcernStateChangeEvent event) {
                applyConcernRules();
            }
        });
    }

    // Historic method to avoid refactoring
    public void setPermission(Permission... permission) {
        setVisibilityPermission(permission);
    }

    @Override
    public void setVisible(boolean visible) {
        setConcernsVisible(visible);
    }

    boolean isHierarchyAttached() {
        if (getParentMenu() instanceof MenuBar) {
            return ((MenuBar) getParentMenu()).isHierarchyAttached();
        } else {
            return false;
        }
    }

    private void updateSubmenuIcon() {
        try {
            updatingSubmenuIcon = true;
            ((MenuBar) getParentMenu()).updateSubmenuIcon(this);
        } finally {
            updatingSubmenuIcon = false;
        }
    }

    @Override
    public void applyVisibilityRules() {
        if (isHierarchyAttached()) {
            boolean state = HasWidgetConcerns.super.isVisible();
            if (super.isVisible() != state) {
                if (HasWidgetConcerns.debugMenuConcerns) {
                    log.debug("MenuItem {} visible state {} -> {}", this.getText(), super.isVisible(), state);
                }
                super.setVisible(state);
                if (hasSubMenu()) {
                    updateSubmenuIcon();
                }
                fireEvent(new ConcernStateChangeEvent());
            } else if (HasWidgetConcerns.debugMenuConcerns) {
                log.debug("MenuItem {} visible state not change and is {}", this.getText(), state);
            }
        } else if (HasWidgetConcerns.debugMenuConcerns) {
            log.debug("MenuItem {} not yet Attached", this.getText());
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        setConcernsEnabled(enabled);
    }

    @Override
    public void applyEnablingRules() {
        super.setEnabled(HasWidgetConcerns.super.isEnabled());
    }

    @Override
    public void setSecurityContext(AccessControlContext context) {
        HasWidgetConcerns.super.setSecurityContext(context);
        HasSecureConcernedChildren.super.setSecurityContext(context);
    }

    @Override
    public void inserConcernedParent(AbstractConcern parentConcern) {
        HasWidgetConcerns.super.inserConcernedParent(parentConcern);
        HasSecureConcernedChildren.super.inserConcernedParent(parentConcern);
    }

    @Override
    public void applyConcernRules() {
        HasWidgetConcerns.super.applyConcernRules();
        HasSecureConcernedChildren.super.applyConcernRules();
    }

    @Override
    public List<AbstractConcern> concerns() {
        return concerns;
    }

    @Override
    public SecureConcernsHolder secureConcernsHolder() {
        return secureConcernsHolder;
    }

    // Events as in Widget

    HandlerManager ensureHandlers() {
        return handlerManager == null ? handlerManager = new HandlerManager(this) : handlerManager;
    }

    @Override
    public final <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type) {
        return ensureHandlers().addHandler(type, handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (handlerManager != null) {
            handlerManager.fireEvent(event);
        }
    }

}
