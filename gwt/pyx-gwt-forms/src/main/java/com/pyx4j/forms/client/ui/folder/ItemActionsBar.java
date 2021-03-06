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
 */
package com.pyx4j.forms.client.ui.folder;

import static com.pyx4j.forms.client.ui.folder.FolderTheme.StyleName.CFolderActionsBar;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.gwt.commons.ui.HorizontalPanel;
import com.pyx4j.widgets.client.ImageButton;
import com.pyx4j.widgets.client.images.ButtonImages;

public class ItemActionsBar extends HorizontalPanel {

    public static enum SortingState {
        First, Last, Only, Inner
    }

    public static enum ActionType implements IDebugId {
        Remove, Up, Down, Cust1, Cust2, Cust3;

        @Override
        public String debugId() {
            return name() + "Button";
        }
    }

    private final Map<ActionType, ImageButton> actions = new HashMap<ActionType, ImageButton>();

    boolean boxDecorator = false;

    public ItemActionsBar(boolean removable) {
        setStyleName(CFolderActionsBar.name());

    }

    public void init(Direction direction) {
        clear();
        if (direction == Direction.RTL) {
            for (int i = ActionType.values().length - 1; i >= 0; --i) {
                placeAction(ActionType.values()[i]);
            }
        } else {
            for (ActionType action : ActionType.values()) {
                placeAction(action);
            }
        }
    }

    private void placeAction(ActionType action) {
        ImageButton button = actions.get(action);
        if (button != null) {
            add(button);
        }
    }

    public void addAction(ActionType action, String tooltip, ButtonImages images, Command command) {
        ImageButton button = new ImageButton(images, command);
        button.setTooltip(tooltip);
        actions.put(action, button);
    }

    public void setActionCommand(ActionType type, Command command) {
        assert actions.containsKey(type) : "Command is not added";
        actions.get(type).setCommand(command);
    }

    public void setSortingState(SortingState state) {
        switch (state) {
        case First:
            actions.get(ActionType.Up).setVisible(false);
            actions.get(ActionType.Down).setVisible(true);
            break;
        case Last:
            actions.get(ActionType.Up).setVisible(true);
            actions.get(ActionType.Down).setVisible(false);
            break;
        case Only:
            actions.get(ActionType.Up).setVisible(false);
            actions.get(ActionType.Down).setVisible(false);
            break;
        case Inner:
            actions.get(ActionType.Up).setVisible(true);
            actions.get(ActionType.Down).setVisible(true);
            break;
        default:
            break;
        }
    }

    public void setHover(boolean hover) {
        if (hover) {
            addStyleDependentName(FolderTheme.StyleDependent.hover.name());
        } else {
            removeStyleDependentName(FolderTheme.StyleDependent.hover.name());
        }

    }

    public void setDefaultActionsState(boolean removable, boolean up, boolean down) {
        actions.get(ActionType.Remove).setVisible(removable);
        actions.get(ActionType.Up).setVisible(up);
        actions.get(ActionType.Down).setVisible(down);
    }

    public void setButtonVisible(ActionType type, boolean show) {
        if (actions.containsKey(type)) {
            actions.get(type).setVisible(show);
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        for (ActionType type : ActionType.values()) {
            ImageButton button = actions.get(type);
            if (button != null) {
                button.ensureDebugId(new CompositeDebugId(baseID, type).debugId());
            }
        }
    }

}
