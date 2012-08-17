/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Oct 4, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.folder;

import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderActionsBar;
import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderDownButton;
import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderRemoveButton;
import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderUpButton;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.IconButton;

public class ItemActionsBar extends HorizontalPanel {

    private static final I18n i18n = I18n.get(ItemActionsBar.class);

    public static enum SortingState {
        First, Last, Only, Inner
    }

    public static enum Action {
        Remove, Up, Down, Cust1, Cust2, Cust3
    }

    public static enum DebugIds implements IDebugId {
        RemoveButton, UpButton, DownButton;

        @Override
        public String debugId() {
            return name();
        }
    }

    private final Map<Action, IconButton> actions = new HashMap<Action, IconButton>();

    boolean boxDecorator = false;

    public ItemActionsBar(boolean removable) {
        setStyleName(EntityFolderActionsBar.name());

        IconButton action = new IconButton(i18n.tr("Delete Item"));
        action.setVisible(removable);
        action.setStyleName(EntityFolderRemoveButton.name());
        actions.put(Action.Remove, action);

        action = new IconButton(i18n.tr("Move down"));
        action.setStyleName(EntityFolderDownButton.name());
        actions.put(Action.Down, action);

        action = new IconButton(i18n.tr("Move up"));
        action.setStyleName(EntityFolderUpButton.name());
        actions.put(Action.Up, action);
    }

    public void init(IFolderItemDecorator<?> decorator) {
        EntityFolderImages images = decorator.getImages();
        actions.get(Action.Remove).setImages(images.delButton());
        actions.get(Action.Up).setImages(images.moveUpButton());
        actions.get(Action.Down).setImages(images.moveDownButton());

        clear();
        if (decorator instanceof BoxFolderItemDecorator) {
            for (int i = Action.values().length - 1; i >= 0; --i) {
                addAction(Action.values()[i]);
            }
        } else {
            for (Action action : Action.values()) {
                addAction(action);
            }
        }
    }

    private void addAction(Action action) {
        IconButton button = actions.get(action);
        if (button != null) {
            add(button);
        }
    }

    public HandlerRegistration addItemRemoveClickHandler(final ClickHandler handler) {
        return actions.get(Action.Remove).addClickHandler(handler);
    }

    public HandlerRegistration addRowUpClickHandler(final ClickHandler handler) {
        return actions.get(Action.Up).addClickHandler(handler);
    }

    public HandlerRegistration addRowDownClickHandler(final ClickHandler handler) {
        return actions.get(Action.Down).addClickHandler(handler);
    }

    public void setSortingState(SortingState state) {
        switch (state) {
        case First:
            actions.get(Action.Up).setVisible(false);
            actions.get(Action.Down).setVisible(true);
            break;
        case Last:
            actions.get(Action.Up).setVisible(true);
            actions.get(Action.Down).setVisible(false);
            break;
        case Only:
            actions.get(Action.Up).setVisible(false);
            actions.get(Action.Down).setVisible(false);
            break;
        case Inner:
            actions.get(Action.Up).setVisible(true);
            actions.get(Action.Down).setVisible(true);
            break;
        default:
            break;
        }
    }

    public void setHover(boolean hover) {
        if (hover) {
            addStyleDependentName(DefaultEntityFolderTheme.StyleDependent.hover.name());
        } else {
            removeStyleDependentName(DefaultEntityFolderTheme.StyleDependent.hover.name());
        }

    }

    public void setActionsState(boolean removable, boolean up, boolean down) {
        actions.get(Action.Remove).setVisible(removable);
        actions.get(Action.Up).setVisible(up);
        actions.get(Action.Down).setVisible(down);
    }

    public void setRemoveButtonVisible(boolean show) {
        actions.get(Action.Remove).setVisible(show);
    }

    public void addCustomButton(IconButton button) {
//        button.addStyleName(EntityFolderCustomButton.name());
//        customControls.add(button);
    }

    public void removeCustomButton(IconButton button) {
//        customControls.remove(button);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        actions.get(Action.Remove).ensureDebugId(new CompositeDebugId(baseID, DebugIds.RemoveButton).debugId());
        actions.get(Action.Up).ensureDebugId(new CompositeDebugId(baseID, DebugIds.UpButton).debugId());
        actions.get(Action.Down).ensureDebugId(new CompositeDebugId(baseID, DebugIds.DownButton).debugId());
    }
}
