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
package com.pyx4j.entity.client.ui.flex.folder;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName;
import com.pyx4j.forms.client.ImageFactory;
import com.pyx4j.widgets.client.ImageButton;

public class ItemActionsBar extends HorizontalPanel {

    private static final I18n i18n = I18nFactory.getI18n(ItemActionsBar.class);

    public static enum SortingState {
        First, Last, Only, Inner
    }

    private final Image removeCommand;

    private final Image upCommand;

    private final Image downCommand;

    public ItemActionsBar(boolean removable, Direction direction, EntityFolderImages images) {

        setStyleName(StyleName.EntityFolderActionsBar.name());

        upCommand = new ImageButton(images.moveUp(), images.moveUpHover(), i18n.tr("Move up"));
        upCommand.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
        upCommand.getElement().getStyle().setMargin(2, Unit.PX);
        upCommand.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
//                    ((CFormFolder) getCComponent().getParentContainer()).moveItem(getCComponent(), true);
//                    mouseOver = false;
//                    installMouseOverStyles();
            }
        });
        add(upCommand);

        downCommand = new ImageButton(images.moveDown(), images.moveDownHover(), i18n.tr("Move down"));
        downCommand.setResource(ImageFactory.getImages().moveDown());
        downCommand.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
        downCommand.getElement().getStyle().setMargin(2, Unit.PX);
        downCommand.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
//                    ((CFormFolder) getCComponent().getParentContainer()).moveItem(getCComponent(), false);
//                    mouseOver = false;
//                    installMouseOverStyles();
            }
        });
        add(downCommand);

        removeCommand = new ImageButton(images.del(), images.delHover(), i18n.tr("Delete Item"));
        removeCommand.setVisible(removable);
        removeCommand.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
        removeCommand.getElement().getStyle().setMargin(2, Unit.PX);
        add(removeCommand);
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
}
