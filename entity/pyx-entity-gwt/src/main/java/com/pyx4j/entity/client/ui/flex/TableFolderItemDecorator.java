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
 * Created on Feb 12, 2011
 * @author vlads
 * @version $Id: FormsFolderDecorator.java 8142 2011-02-13 04:31:26Z vlads $
 */
package com.pyx4j.entity.client.ui.flex;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.FormNavigationDebugId;

public class TableFolderItemDecorator extends BaseFolderItemDecorator {

    private final HTML validationMessageHolder;

    public TableFolderItemDecorator(ImageResource removeButton, String title, boolean removable) {
        super(removeButton, title, removable);

        VerticalPanel mainPanel = new VerticalPanel();
        setWidget(mainPanel);

        mainPanel.add(rowHolder);

        validationMessageHolder = new HTML();
        validationMessageHolder.getElement().getStyle().setColor("red");
        mainPanel.add(validationMessageHolder);

    }

    public TableFolderItemDecorator(ImageResource removeButton, String title) {
        this(removeButton, title, true);
    }

    public TableFolderItemDecorator(ImageResource removeButton) {
        this(removeButton, null, true);
    }

    @Override
    public void setFolderItem(final CEntityFolderItem<?> folderItem) {
        super.setFolderItem(folderItem);
        folderItem.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName() == PropertyChangeEvent.PropertyName.VALIDITY) {
                    validationMessageHolder.setHTML(folderItem.getValidationResults().getMessagesText(true));
                }
            }
        });
    }

    @Override
    public HandlerRegistration addItemRemoveClickHandler(ClickHandler handler) {
        if (image != null) {
            return image.addClickHandler(handler);
        } else {
            return null;
        }
    }

    @Override
    public HandlerRegistration addItemClickHandler(final ClickHandler handler) {
        //TODO add proper handler removal
        return rowHolder.addDomHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                handler.onClick(event);
            }
        }, ClickEvent.getType());
    }

    @Override
    public HandlerRegistration addRowUpClickHandler(ClickHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addRowDownClickHandler(ClickHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addRowCollapseClickHandler(ClickHandler handler) {
        return null;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
        if (imageHolder != null) {
            imageHolder.setVisible(removable);
        }
    }

}
