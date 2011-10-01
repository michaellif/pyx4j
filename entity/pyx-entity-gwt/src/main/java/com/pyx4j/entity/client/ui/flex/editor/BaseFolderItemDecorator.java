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
 * Created on Mar 5, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex.editor;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.FormNavigationDebugId;
import com.pyx4j.forms.client.ui.decorators.ImageHolder;
import com.pyx4j.widgets.client.ImageButton;

public abstract class BaseFolderItemDecorator<E extends IEntity> extends SimplePanel implements IFolderItemDecorator<E> {

    private final FlowPanel rowHolder = new FlowPanel();;

    private final SimplePanel content = new SimplePanel();;

    private ImageHolder imageHolder = null;

    private Image removeImage = null;

    private boolean removable;

    public BaseFolderItemDecorator(ImageResource button, String title, boolean buttonVisible) {
        this(button, null, title, buttonVisible);
    }

    public BaseFolderItemDecorator(ImageResource button, ImageResource buttonHover, String title, boolean buttonVisible) {
        this.removable = (buttonVisible && button != null);

        rowHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        if (button != null) {
            removeImage = new ImageButton(button, buttonHover, title);
            imageHolder = new ImageHolder(removeImage);
            imageHolder.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);

            if (!buttonVisible) {
                imageHolder.setVisible(false);
            }

            rowHolder.add(imageHolder);
        }

        content.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
        rowHolder.add(content);

        if (removeImage != null) {
            removeImage.setTitle(title);
            removeImage.getElement().getStyle().setCursor(Cursor.POINTER);
        }
    }

    protected SimplePanel getContent() {
        return content;
    }

    protected FlowPanel getRowHolder() {
        return rowHolder;
    }

    protected ImageHolder getImageHolder() {
        return imageHolder;
    }

    protected Image getRemoveImage() {
        return removeImage;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        imageHolder.setVisible(this.removable = removable);
    }

    @Override
    public void setFolderItem(CEntityFolderItemEditor<E> folderItem) {
        content.setWidget(folderItem.getContainer());
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        if (removeImage != null) {
            removeImage.ensureDebugId(baseID + "-" + FormNavigationDebugId.Form_Remove.debugId());
        }
    }

    @Override
    public HandlerRegistration addItemRemoveClickHandler(ClickHandler handler) {
        if (isRemovable()) {
            return getRemoveImage().addClickHandler(handler);
        }
        return null;
    }
}
