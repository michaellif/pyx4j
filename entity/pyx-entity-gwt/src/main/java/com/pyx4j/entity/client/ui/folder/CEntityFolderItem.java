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
 * Created on Feb 13, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.folder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.CEntityContainer;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.IDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.widgets.client.IconButton;

public abstract class CEntityFolderItem<E extends IEntity> extends CEntityContainer<E> {

    private final SimplePanel container;

    private boolean first;

    private boolean last;

    private boolean movable = true;

    private boolean removable = true;

    private final List<HandlerRegistration> handlerRegistrations;

    private CEntityEditor<E> editor;

    private final Class<E> clazz;

    private ItemActionsBar actionsPanel;

    private boolean initiated = false;

    public CEntityFolderItem(Class<E> clazz) {
        this(clazz, true, true);
    }

    public CEntityFolderItem(Class<E> clazz, boolean movable, boolean removable) {
        super();
        this.clazz = clazz;
        container = new SimplePanel();
        this.movable = movable;
        this.removable = removable;

        handlerRegistrations = new ArrayList<HandlerRegistration>();
        actionsPanel = new ItemActionsBar(removable);

    }

    @Override
    protected abstract IFolderItemDecorator<E> createDecorator();

    @Override
    public void initContent() {
        super.initContent();
        if (!initiated) {
            IDecorator decorator = getDecorator();
            if (decorator instanceof IFolderItemDecorator) {
                actionsPanel.init((IFolderItemDecorator) decorator);
                ((IFolderItemDecorator) decorator).setItemActionsBar(actionsPanel);
            } else {
                throw new Error("Correct decorator is missing");
            }
            initiated = true;
        }
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public boolean isRemovable() {
        return removable;
    }

    @Override
    public SimplePanel getContainer() {
        return container;
    }

    @Override
    public IsWidget createContent() {
        editor = (CEntityEditor<E>) create(EntityFactory.getEntityPrototype(clazz));
        adopt(editor);
        return editor;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<E> handler) {
        HandlerRegistration handlerRegistration = super.addValueChangeHandler(handler);
        handlerRegistrations.add(handlerRegistration);
        return handlerRegistration;
    }

    @Override
    public HandlerRegistration addPropertyChangeHandler(PropertyChangeHandler handler) {
        HandlerRegistration handlerRegistration = super.addPropertyChangeHandler(handler);
        handlerRegistrations.add(handlerRegistration);
        return handlerRegistration;
    }

    private HandlerRegistration addItemRemoveClickHandler(ClickHandler handler) {
        HandlerRegistration handlerRegistration = actionsPanel.addItemRemoveClickHandler(handler);
        handlerRegistrations.add(handlerRegistration);
        return handlerRegistration;
    }

    private HandlerRegistration addRowUpClickHandler(ClickHandler handler) {
        HandlerRegistration handlerRegistration = actionsPanel.addRowUpClickHandler(handler);
        handlerRegistrations.add(handlerRegistration);
        return handlerRegistration;
    }

    private HandlerRegistration addRowDownClickHandler(ClickHandler handler) {
        HandlerRegistration handlerRegistration = actionsPanel.addRowDownClickHandler(handler);
        handlerRegistrations.add(handlerRegistration);
        return handlerRegistration;
    }

    public void addCustomButton(IconButton button) {
        actionsPanel.addCustomButton(button);
    }

    public void removeCustomButton(IconButton button) {
        actionsPanel.removeCustomButton(button);
    }

    @Override
    public void onAbandon() {
        super.onAbandon();
        for (HandlerRegistration handlerRegistration : handlerRegistrations) {
            handlerRegistration.removeHandler();
        }
        handlerRegistrations.clear();
    }

    @Override
    public void onAdopt(final CContainer<?, ?> parent) {
        super.onAdopt(parent);

        final CEntityFolder<E> folder = (CEntityFolder<E>) parent;

        handlerRegistrations.clear();

        HandlerRegistration handlerRegistration = folder.addValueChangeHandler(new ValueChangeHandler<IList<E>>() {

            @Override
            public void onValueChange(ValueChangeEvent<IList<E>> event) {
                calculateActionsState();
            }
        });
        handlerRegistrations.add(handlerRegistration);

        calculateActionsState();

        addItemRemoveClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                folder.removeItem(CEntityFolderItem.this);
            }
        });
        addRowUpClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                folder.moveUpItem(CEntityFolderItem.this);
            }
        });
        addRowDownClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                folder.moveDownItem(CEntityFolderItem.this);
            }
        });

    }

    @SuppressWarnings("unchecked")
    protected void calculateActionsState() {
        boolean enabled = isEnabled() && isEditable();
        if (!enabled) {
            actionsPanel.setActionsState(false, false, false);
        } else {

            CEntityFolder<E> parent = ((CEntityFolder<E>) getParent());
            int index = parent.getItemIndex(CEntityFolderItem.this);

            first = index == 0;
            last = index == parent.getItemCount() - 1;

            CEntityFolderItem<?> previousSibling = parent.getItem(index - 1);
            CEntityFolderItem<?> nextSibling = parent.getItem(index + 1);

            actionsPanel.setActionsState(removable, movable && !first && previousSibling.isMovable(), movable && !last && nextSibling.isMovable());
        }

    }

    protected void disableActions() {
        ((IFolderItemDecorator<?>) getDecorator()).setActionsState(false, false, false);
    }

    @Override
    protected void setComponentsValue(E entity, boolean fireEvent, boolean populate) {
        editor.setValue(entity, fireEvent, populate);
    }

    @Override
    public Collection<CComponent<?, ?>> getComponents() {
        if (editor == null) {
            return null;
        }
        return Arrays.asList(new CComponent<?, ?>[] { editor });
    }

}
