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
package com.pyx4j.entity.client.ui.folder;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.client.ui.folder.CEntityFolder.StyleName;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.FormNavigationDebugId;
import com.pyx4j.gwt.commons.HandlerRegistrationGC;
import com.pyx4j.widgets.client.ImageButton;

public abstract class BaseFolderDecorator<E extends IEntity> extends FlowPanel implements IFolderDecorator<E> {

    private final SimplePanel container = new SimplePanel();

    private FlowPanel actionsPanel = null;

    private Image addImage = null;

    private Label addButtonLabel;

    private boolean addable;

    public BaseFolderDecorator(EntityFolderImages images, String title, boolean addable) {
        this.addable = addable && (images.add() != null);

        if (images.add() != null) {

            actionsPanel = new FlowPanel();
            actionsPanel.setStyleName(StyleName.EntityFolderAddButton.name());

            addImage = new ImageButton(images.add(), images.addHover(), title);
            addImage.setStyleName(StyleName.EntityFolderAddButtonImage.name());
            actionsPanel.add(addImage);

            addButtonLabel = new Label(title);
            addButtonLabel.setStyleName(StyleName.EntityFolderAddButtonLabel.name());
            actionsPanel.add(addButtonLabel);
        }
    }

    protected SimplePanel getContainer() {
        return container;
    }

    protected FlowPanel getActionsPanel() {
        return actionsPanel;
    }

    protected Image getAddImage() {
        return addImage;
    }

    protected boolean isAddable() {
        return addable;
    }

    public void setAddable(boolean addable) {
        this.addable = addable;
    }

    @Override
    public HandlerRegistration addItemAddClickHandler(ClickHandler handler) {
        if (isAddable()) {
            HandlerRegistrationGC h = new HandlerRegistrationGC();
            h.add(addImage.addClickHandler(handler));
            h.add(addButtonLabel.addClickHandler(handler));
            return h;
        }
        return null;
    }

    @Override
    public void setComponent(CEntityFolder<E> w) {
        container.setWidget(w.getContainer());
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        //TODO use inheritance of objects
        //image.ensureDebugId(CompositeDebugId.debugId(parentFolder.getDebugId(), FormNavigationDebugId.Form_Add));
        if (isAddable()) {
            if (baseID.endsWith(IFolderDecorator.DEBUGID_SUFIX)) {
                baseID = baseID.substring(0, baseID.length() - IFolderDecorator.DEBUGID_SUFIX.length());
            }
            addImage.ensureDebugId(baseID + "-" + FormNavigationDebugId.Form_Add.debugId());
            addButtonLabel.ensureDebugId(baseID + "-" + FormNavigationDebugId.Form_Add.debugId() + "-label");
        }
    }

    @Override
    public void setAddButtonVisible(boolean visible) {
        addImage.setVisible(visible);
        addButtonLabel.setVisible(visible);
    }
}
