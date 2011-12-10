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

import static com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolderRowItemDecorator;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;

public class TableFolderItemDecorator<E extends IEntity> extends BaseFolderItemDecorator<E> {

    private final HTML validationMessageHolder;

    private ItemActionsBar actionsPanel;

    private SimplePanel contentHolder;

    public TableFolderItemDecorator(EntityFolderImages images) {
        this(images, null);
    }

    //TODO propagate removeLabel to tooltip
    public TableFolderItemDecorator(EntityFolderImages images, String removeLabel) {
        super(images);

        setStyleName(EntityFolderRowItemDecorator.name());

        addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                actionsPanel.setHover(true);
            }
        }, MouseOverEvent.getType());

        addDomHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                actionsPanel.setHover(false);
            }
        }, MouseOutEvent.getType());

        DockPanel mainPanel = new DockPanel();
        setWidget(mainPanel);

        validationMessageHolder = new HTML();
        validationMessageHolder.getElement().getStyle().setColor("red");
        mainPanel.add(validationMessageHolder, DockPanel.SOUTH);

        actionsPanel = new ItemActionsBar(true, Direction.RTL, images);
        mainPanel.add(actionsPanel, DockPanel.EAST);

        contentHolder = new SimplePanel();
        mainPanel.add(contentHolder, DockPanel.CENTER);

    }

    @Override
    public void setComponent(final CEntityFolderItem<E> folderItem) {
        super.setComponent(folderItem);
        contentHolder.setWidget(folderItem.createContent());
        folderItem.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid)) {
                    validationMessageHolder.setHTML(folderItem.getValidationResults().getMessagesText(true));
                }
            }
        });
    }

    @Override
    public HandlerRegistration addItemRemoveClickHandler(ClickHandler handler) {
        return actionsPanel.addItemRemoveClickHandler(handler);
    }

    @Override
    public HandlerRegistration addRowUpClickHandler(ClickHandler handler) {
        return actionsPanel.addRowUpClickHandler(handler);
    }

    @Override
    public HandlerRegistration addRowDownClickHandler(ClickHandler handler) {
        return actionsPanel.addRowDownClickHandler(handler);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        validationMessageHolder.ensureDebugId(new CompositeDebugId(baseID, IFolderDecorator.DecoratorsIds.Label).debugId());
    }

    @Override
    public void setActionsState(boolean removable, boolean up, boolean down) {
        actionsPanel.setActionsState(removable, up, down);
    }

}
