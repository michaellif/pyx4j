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

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AccessibleMenuBar;

import com.pyx4j.gwt.commons.concerns.AbstractConcern;
import com.pyx4j.gwt.commons.concerns.HasSecureConcern;
import com.pyx4j.gwt.commons.concerns.HasSecureConcernedChildren;
import com.pyx4j.gwt.commons.concerns.HasWidgetConcerns;
import com.pyx4j.security.annotations.ActionId;
import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.security.shared.Permission;
import com.pyx4j.widgets.client.event.shared.SecureConcernStateChangeEvent;

public class MenuBar extends AccessibleMenuBar implements HasWidgetConcerns, HasSecureConcernedChildren {

    protected final List<AbstractConcern> concerns = new ArrayList<>();

    private final SecureConcernsHolder secureConcernsHolder = new SecureConcernsHolder();

    private HumanInputInfo humanInputInfo = HumanInputInfo.robot;

    public MenuBar() {
        super(true);
        setAutoOpen(true);
        setAnimationEnabled(true);
    }

    public HandlerRegistration addSecureConcernStateChangeHandler(SecureConcernStateChangeEvent.Handler handler) {
        return addHandler(handler, SecureConcernStateChangeEvent.getType());
    }

    @Override
    public com.google.gwt.user.client.ui.MenuItem insertItem(com.google.gwt.user.client.ui.MenuItem item, int beforeIndex) {
        if (item.getScheduledCommand() != null) {
            final ScheduledCommand origCommand = item.getScheduledCommand();
            item.setScheduledCommand(new Command() {

                @Override
                public void execute() {
                    if (origCommand instanceof HumanInputCommand) {
                        ((HumanInputCommand) origCommand).execute(humanInputInfo);
                    } else {
                        origCommand.execute();
                    }

                }
            });
        }
        if (item instanceof HasSecureConcern) {
            addSecureConcern((HasSecureConcern) item);
        }
        try {
            return super.insertItem(item, beforeIndex);
        } finally {
            fireEvent(new SecureConcernStateChangeEvent());
        }
    }

    public MenuItem addItem(String text, ScheduledCommand cmd, Permission... permissions) {
        MenuItem menuItem = new MenuItem(text, cmd, permissions);
        addItem(menuItem);
        return menuItem;
    }

    public MenuItem addItem(String text, ScheduledCommand cmd, Class<? extends ActionId> actionId) {
        MenuItem menuItem = new MenuItem(text, cmd, actionId);
        addItem(menuItem);
        return menuItem;
    }

    public boolean isMenuEmpty() {
        boolean empty = getItems().isEmpty();
        if (!empty) {
            empty = true;
            for (com.google.gwt.user.client.ui.MenuItem item : getItems()) {
                if (item.isVisible()) {
                    empty = false;
                    break;
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
    public List<com.google.gwt.user.client.ui.MenuItem> getItems() {
        return super.getItems();
    }

    @Override
    public void clearItems() {
        super.clearItems();
        clearSecureConcerns();
        fireEvent(new SecureConcernStateChangeEvent()); // TODO remove
    }

    // --- concerns implementation - start

    @Override
    public void setSecurityContext(AccessControlContext context) {
        HasWidgetConcerns.super.setSecurityContext(context);
        HasSecureConcernedChildren.super.setSecurityContext(context);

        // TODO Fire when state actually changes.
        fireEvent(new SecureConcernStateChangeEvent()); // TODO remove
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
    public void applyEnablingRules() {
        //TODO review
    }

    @Override
    public void setVisible(boolean visible) {
        setConcernsVisible(visible);
    }

    // ---  save to copy paste to other class

    @Override
    protected void onAttach() {
        super.onAttach();
        applyConcernRules();
    }

    @Override
    public void applyVisibilityRules() {
        if (this.isAttached()) {
            super.setVisible(HasWidgetConcerns.super.isVisible());
        }
    }

    @Override
    public List<AbstractConcern> concerns() {
        return concerns;
    }

    @Override
    public SecureConcernsHolder secureConcernsHolder() {
        return secureConcernsHolder;
    }
}
