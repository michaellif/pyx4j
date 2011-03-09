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
package com.pyx4j.entity.client.ui.flex;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.forms.client.ui.FormNavigationDebugId;
import com.pyx4j.forms.client.ui.decorators.ImageHolder;

public abstract class BaseFolderItemDecorator extends SimplePanel implements FolderItemDecorator {

    protected Image image;

    protected SimplePanel content;

    protected FlowPanel rowHolder;

    protected boolean removable;

    protected ImageHolder imageHolder;

    public BaseFolderItemDecorator(ImageResource removeButton, String title, boolean removable) {
        this.removable = (removable && removeButton != null);

        rowHolder = new FlowPanel();
        rowHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

        imageHolder = null;
        if (removeButton != null) {
            image = new Image(removeButton);

            imageHolder = new ImageHolder(image);
            imageHolder.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);

            if (!removable) {
                imageHolder.setVisible(false);
            }

            rowHolder.add(imageHolder);
        } else {
            image = null;
        }

        content = new SimplePanel();
        content.getElement().getStyle().setFloat(com.google.gwt.dom.client.Style.Float.LEFT);

        rowHolder.add(content);

        if (image != null) {
            image.setTitle(title);
            image.getElement().getStyle().setCursor(Cursor.POINTER);
        }

    }

    @Override
    public void setFolderItem(CEntityFolderItem<?> folderItem) {
        content.setWidget(folderItem.getContent());
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        if (image != null) {
            image.ensureDebugId(baseID + "_" + FormNavigationDebugId.Form_Remove.getDebugIdString());
        }
    }

}
