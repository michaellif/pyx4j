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
 * Created on Feb 11, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.folder;

import static com.pyx4j.forms.client.ui.folder.DefaultEntityFolderTheme.StyleName.EntityFolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityContainer;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

/**
 * This component represents list of IEntities
 */
public abstract class CEntityFolder<E extends IEntity> extends CEntityContainer<IList<E>> {

    private static final Logger log = LoggerFactory.getLogger(CEntityFolder.class);

    private FlowPanel container;

    private boolean orderable = true;

    private boolean addable = true;

    private boolean removable = true;

    private int currentRowDebugId = 0;

    private final List<CEntityFolderItem<E>> itemsList;

    private final E entityPrototype;

    private final Class<E> rowClass;

    public CEntityFolder(Class<E> rowClass) {
        this.rowClass = rowClass;
        asWidget().setStyleName(EntityFolder.name());
        itemsList = new ArrayList<CEntityFolderItem<E>>();
        if (rowClass != null) {
            entityPrototype = EntityFactory.getEntityPrototype(rowClass);
        } else {
            entityPrototype = null;
        }

        addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyName.enabled || event.getPropertyName() == PropertyName.editable
                        || event.getPropertyName() == PropertyName.repopulated) {
                    calculateActionsState();
                }
            }
        });
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
        for (CEntityFolderItem<E> item : itemsList) {
            item.setMovable(orderable);
            item.calculateActionsState();
        }
    }

    public boolean isAddable() {
        return addable;
    }

    public void setAddable(boolean addable) {
        this.addable = addable;
        calculateActionsState();
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
        for (CEntityFolderItem<E> item : itemsList) {
            item.setRemovable(removable);
            item.calculateActionsState();
        }
    }

    private void calculateActionsState() {
        boolean addable = isAddable() && isEnabled() && isEditable();
        if (getDecorator() != null) {
            ((IFolderDecorator) getDecorator()).setAddButtonVisible(addable);
        }
    }

    /**
     * This mainly use for columns creation when TableFolderDecorator is used
     */
    public E proto() {
        return entityPrototype;
    }

    @Override
    public IsWidget createContent() {
        return container = new FlowPanel();
    }

    protected abstract IFolderItemDecorator<E> createItemDecorator();

    protected CEntityFolderItem<E> createItem(boolean first) {
        return new CEntityFolderItem<E>(rowClass) {
            @Override
            public IFolderItemDecorator<E> createItemDecorator() {
                return CEntityFolder.this.createItemDecorator();
            }
        };
    }

    private CEntityFolderItem<E> createItemPrivate() {

        boolean first = container.getWidgetCount() == 0;

        CEntityFolderItem<E> item = createItem(first);

        if (removable == false) {
            item.setRemovable(false);
        }

        if (orderable == false) {
            item.setMovable(false);
        }

        return item;
    }

    protected abstract IFolderDecorator<E> createFolderDecorator();

    @Override
    protected final IFolderDecorator<E> createDecorator() {
        IFolderDecorator<E> folderDecorator = createFolderDecorator();
        folderDecorator.setAddButtonVisible(addable);

        addValueChangeHandler(folderDecorator);

        folderDecorator.addItemAddClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addItem();
            }
        });

        return folderDecorator;
    }

    protected void addItem() {
        createNewEntity(new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                addItem(result);
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected void addItem(E newEntity) {
        if (getValue() == null) {
            log.warn("Request to add item has been issued before the form populated with value");
            return;
        }

        final CEntityFolderItem<E> item = createItemPrivate();

        adopt(item);
        getValue().add(newEntity);
        item.populate(newEntity);

        revalidate();
        ValueChangeEvent.fire(CEntityFolder.this, getValue());

        if (item.getDecorator() instanceof BoxFolderItemDecorator) {
            ((BoxFolderItemDecorator<E>) item.getDecorator()).setExpended(true);
        }
    }

    protected void removeItem(CEntityFolderItem<E> item) {
        abandon(item);
        getValue().remove(item.getValue());
        revalidate();
        ValueChangeEvent.fire(CEntityFolder.this, getValue());
    }

    protected void moveUpItem(CEntityFolderItem<E> item) {
        moveItem(item, true);
    }

    protected void moveDownItem(CEntityFolderItem<E> item) {
        moveItem(item, false);
    }

    protected void moveItem(CEntityFolderItem<E> item, boolean up) {
        int indexBefore = getValue().indexOf(item.getValue());
        int indexAfter = indexBefore + (up ? -1 : +1);
        if (indexAfter < 0 || indexAfter > getValue().size()) {
            return;
        }

        getValue().move(indexBefore, indexAfter);
        itemsList.remove(indexBefore);
        container.remove(item);

        itemsList.add(indexAfter, item);
        container.insert(item, indexAfter);

        revalidate();
        ValueChangeEvent.fire(CEntityFolder.this, getValue());
        return;
    }

    /**
     * Implementation to override new Entity creation. No need to call
     * super.createNewEntity().
     * 
     * @param newEntity
     * @param callback
     */
    @SuppressWarnings("unchecked")
    protected void createNewEntity(AsyncCallback<E> callback) {
        callback.onSuccess((E) EntityFactory.create(entityPrototype.getValueClass()));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void setComponentsValue(IList<E> value, boolean fireEvent, boolean populate) {

        ArrayList<CEntityFolderItem<E>> previousList = new ArrayList<CEntityFolderItem<E>>(itemsList);

        for (CEntityFolderItem<E> item : previousList) {
            abandon(item);
        }

        currentRowDebugId = 0;

        if (value != null) {

            for (E entity : value) {
                CEntityFolderItem<E> item = null;
                for (CEntityFolderItem<E> itemFromCahe : previousList) {
                    if (itemFromCahe.getValue().equals(entity)) {
                        previousList.remove(itemFromCahe);
                        item = itemFromCahe;
                        break;
                    }
                }

                if (item == null) {
                    item = createItemPrivate();
                }

                adopt(item);
                item.setValue(entity, fireEvent, populate);
            }

        }

        for (CEntityFolderItem<E> item : itemsList) {
            item.calculateActionsState();
        }

        if (getDecorator() instanceof TableFolderDecorator) {
            ((TableFolderDecorator<E>) getDecorator()).setHeaderVisible(container.getWidgetCount() > 0);
        }
    }

    @Override
    public void adopt(final CComponent<?> component) {
        itemsList.add((CEntityFolderItem<E>) component);
        container.add(component);

        IDebugId rowDebugId = new CompositeDebugId(IDebugId.ROW_PREFIX, currentRowDebugId);
        component.setDebugIdSuffix(rowDebugId);
        currentRowDebugId++;

        super.adopt(component);

    }

    @Override
    public void abandon(final CComponent<?> component) {
        super.abandon(component);
        container.remove(component);
        itemsList.remove(component);
    }

    @Override
    public Collection<? extends CComponent<?>> getComponents() {
        return itemsList;
    }

    public int getItemCount() {
        return itemsList.size();
    }

    public int getItemIndex(CEntityFolderItem<E> item) {
        return itemsList.indexOf(item);
    }

    public CEntityFolderItem<E> getItem(int index) {
        if (itemsList.size() > 0 && index > -1 && index < itemsList.size()) {
            return itemsList.get(index);
        } else {
            return null;
        }
    }

    @Override
    protected void onReset() {
        for (CComponent<?> component : new ArrayList<CComponent<?>>(getComponents())) {
            abandon(component);
        }
        container.clear();
        itemsList.clear();
    };

    @Override
    public boolean isVisited() {
        return false;
    }
}
