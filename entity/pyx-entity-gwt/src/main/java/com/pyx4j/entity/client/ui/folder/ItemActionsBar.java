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
import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderCustomButton;
import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderDownButton;
import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderRemoveButton;
import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderUpButton;

import java.util.ArrayList;
import java.util.List;

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

    public static enum DebugIds implements IDebugId {
        RemoveButton, UpButton, DownButton;

        @Override
        public String debugId() {
            return name();
        }
    }

    private final IconButton removeCommand;

    private final IconButton upCommand;

    private final IconButton downCommand;

    private final List<IconButton> customControls = new ArrayList<IconButton>();

    boolean boxDecorator = false;

    public ItemActionsBar(boolean removable) {
        setStyleName(EntityFolderActionsBar.name());

        removeCommand = new IconButton(i18n.tr("Delete Item"));
        removeCommand.setVisible(removable);
        removeCommand.setStyleName(EntityFolderRemoveButton.name());

        downCommand = new IconButton(i18n.tr("Move down"));
        downCommand.setStyleName(EntityFolderDownButton.name());

        upCommand = new IconButton(i18n.tr("Move up"));
        upCommand.setStyleName(EntityFolderUpButton.name());
    }

    public void init(IFolderItemDecorator<?> decorator) {
        EntityFolderImages images = decorator.getImages();
        removeCommand.setImages(images.delButton());
        upCommand.setImages(images.moveUpButton());
        downCommand.setImages(images.moveDownButton());

        clear();
        if (decorator instanceof BoxFolderItemDecorator) {
            for (IconButton btn : customControls) {
                add(btn);
            }
            add(upCommand);
            add(downCommand);
            add(removeCommand);
        } else {
            add(removeCommand);
            add(upCommand);
            add(downCommand);
            for (IconButton btn : customControls) {
                add(btn);
            }
        }
    }

    public HandlerRegistration addItemRemoveClickHandler(final ClickHandler handler) {
        return removeCommand.addClickHandler(handler);
    }

    public HandlerRegistration addRowUpClickHandler(final ClickHandler handler) {
        return upCommand.addClickHandler(handler);
    }

    public HandlerRegistration addRowDownClickHandler(final ClickHandler handler) {
        return downCommand.addClickHandler(handler);
    }

    public void setSortingState(SortingState state) {
        switch (state) {
        case First:
            upCommand.setVisible(false);
            downCommand.setVisible(true);
            break;
        case Last:
            upCommand.setVisible(true);
            downCommand.setVisible(false);
            break;
        case Only:
            upCommand.setVisible(false);
            downCommand.setVisible(false);
            break;
        case Inner:
            upCommand.setVisible(true);
            downCommand.setVisible(true);
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
        removeCommand.setVisible(removable);
        upCommand.setVisible(up);
        downCommand.setVisible(down);
    }

    public void setRemoveButtonVisible(boolean show) {
        removeCommand.setVisible(show);
    }

    public void addCustomButton(IconButton button) {
        button.addStyleName(EntityFolderCustomButton.name());
        customControls.add(button);
    }

    public void removeCustomButton(IconButton button) {
        customControls.remove(button);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        removeCommand.ensureDebugId(new CompositeDebugId(baseID, DebugIds.RemoveButton).debugId());
        upCommand.ensureDebugId(new CompositeDebugId(baseID, DebugIds.UpButton).debugId());
        downCommand.ensureDebugId(new CompositeDebugId(baseID, DebugIds.DownButton).debugId());
    }
}
