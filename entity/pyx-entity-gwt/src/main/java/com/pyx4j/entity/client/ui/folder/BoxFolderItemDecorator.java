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
package com.pyx4j.entity.client.ui.folder;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName;
import com.pyx4j.entity.shared.IEntity;

public class BoxFolderItemDecorator<E extends IEntity> extends BaseFolderItemDecorator<E> {

    private boolean expended = true;

    private boolean collapsible = true;

    private BoxFolderItemToolbar toolbar;

    private SimplePanel contentHolder;

    public BoxFolderItemDecorator(EntityFolderImages images) {
        this(images, "Remove");
    }

    //TODO propagate removeLabel
    public BoxFolderItemDecorator(EntityFolderImages images, String removeLabel) {
        super(images);

        setStyleName(StyleName.EntityFolderBoxItemDecorator.name());

        addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                toolbar.getActionsPanel().setHover(true);
            }
        }, MouseOverEvent.getType());

        addDomHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                toolbar.getActionsPanel().setHover(false);
            }
        }, MouseOutEvent.getType());

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setWidth("100%");
        setWidget(mainPanel);

        toolbar = new BoxFolderItemToolbar(this);
        mainPanel.add(toolbar);

        contentHolder = new SimplePanel();
        contentHolder.setStyleName(StyleName.EntityFolderBoxItem.name());

        mainPanel.add(contentHolder);

    }

    @Override
    public void setComponent(final CEntityFolderItem<E> folderItem) {
        super.setComponent(folderItem);
        contentHolder.setWidget(folderItem.getContainer());
        toolbar.setTitleIcon(folderItem.getIcon());
    }

    public void setExpended(boolean expended) {
        this.expended = expended;
        contentHolder.setVisible(expended);
        toolbar.onExpended(expended);
    }

    public void setCollapsible(boolean collapsible) {
        this.collapsible = collapsible;
        toolbar.setCollapseButtonVisible(collapsible);
        if (collapsible == false) {
            setExpended(true);
        }
    }

    public boolean isCollapsible() {
        return collapsible;
    }

    public boolean isExpended() {
        return expended;
    }

    @Override
    public HandlerRegistration addItemRemoveClickHandler(ClickHandler handler) {
        return toolbar.getActionsPanel().addItemRemoveClickHandler(handler);
    }

    @Override
    public HandlerRegistration addRowUpClickHandler(ClickHandler handler) {
        return toolbar.getActionsPanel().addRowUpClickHandler(handler);
    }

    @Override
    public HandlerRegistration addRowDownClickHandler(ClickHandler handler) {
        return toolbar.getActionsPanel().addRowDownClickHandler(handler);
    }

    @Override
    public void setActionsState(boolean removable, boolean up, boolean down) {
        toolbar.getActionsPanel().setActionsState(removable, up, down);
    }

}
