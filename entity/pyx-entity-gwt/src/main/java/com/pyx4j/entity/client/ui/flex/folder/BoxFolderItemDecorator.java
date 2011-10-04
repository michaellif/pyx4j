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
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex.folder;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;

public class BoxFolderItemDecorator<E extends IEntity> extends BaseFolderItemDecorator<E> {

    private boolean expended = true;

    private boolean collapsible = true;

    private BoxFolderItemToolbar toolbar;

    private SimplePanel contentHolder;

    public BoxFolderItemDecorator(EntityFolderImages images, String title) {
        this(images, title, true);
    }

    public BoxFolderItemDecorator(EntityFolderImages images) {
        this(images, null);
    }

    public BoxFolderItemDecorator(EntityFolderImages images, String removeLabel, boolean removable) {
        super(images, removeLabel, removable);

//        getContent().getElement().getStyle().setMarginTop(10, Unit.PX);
//        getContent().getElement().getStyle().setMarginLeft(10, Unit.PX);
//        getContent().getElement().getStyle().setPadding(10, Unit.PX);
//        getContent().getElement().getStyle().setBorderStyle(BorderStyle.DASHED);
//        getContent().getElement().getStyle().setBorderWidth(1, Unit.PX);
//        getContent().getElement().getStyle().setBorderColor("#999");

        VerticalPanel mainPanel = new VerticalPanel();
        setWidget(mainPanel);

        toolbar = new BoxFolderItemToolbar(this);
        mainPanel.add(new BoxFolderItemToolbar(this));

        contentHolder = new SimplePanel();

        mainPanel.add(contentHolder);

    }

    @Override
    public void setComponent(final CEntityFolderItemEditor<E> folderItem) {
        contentHolder.setWidget(folderItem.getContainer());
        toolbar.setTitleIcon(folderItem.getIcon());
        folderItem.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent propertyChangeEvent) {
                System.out.println("+++++++++++++++PropertyChangeHandler");
            }
        });
    }

    public void setExpended(boolean expended) {
        this.expended = expended;
        contentHolder.setVisible(!expended);
        toolbar.onExpended(expended);
    }

    public void setCollapsible(boolean collapsible) {
        this.collapsible = collapsible;
    }

    public boolean isCollapsible() {
        return collapsible;
    }

    public boolean isExpended() {
        return expended;
    }

    @Override
    public HandlerRegistration addItemRemoveClickHandler(ClickHandler handler) {
        if (isRemovable()) {
            return toolbar.getActionsPanel().addItemRemoveClickHandler(handler);
        }
        return null;
    }

    @Override
    public HandlerRegistration addItemClickHandler(ClickHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addRowUpClickHandler(ClickHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addRowDownClickHandler(ClickHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addRowCollapseClickHandler(ClickHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

}
