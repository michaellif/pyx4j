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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CContainer;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

/**
 * This component represents list of IEntities
 */
public abstract class CFolder<E extends IEntity> extends CContainer<CFolder<E>, IList<E>, IFolderDecorator<E>> {

    private static final Logger log = LoggerFactory.getLogger(CFolder.class);

    private static final I18n i18n = I18n.get(CFolder.class);

    private final FlowPanel container;

    private final SimplePanel noDataNotificationHolder;

    private boolean orderable = true;

    private boolean addable = true;

    private boolean removable = true;

    private int currentRowDebugId = 0;

    private final List<CFolderItem<E>> itemsList;

    private final E entityPrototype;

    private final Class<E> rowClass;

    public CFolder(Class<E> rowClass) {
        this.rowClass = rowClass;
        asWidget().setStyleName(DefaultFolderTheme.StyleName.CFolder.name());
        itemsList = new ArrayList<CFolderItem<E>>();

        container = new FlowPanel();

        noDataNotificationHolder = new SimplePanel();
        noDataNotificationHolder.setStyleName(DefaultFolderTheme.StyleName.CFolderNoDataMessage.name());
        noDataNotificationHolder.setWidget(new Label(i18n.tr("No Data")));

        if (rowClass != null) {
            entityPrototype = EntityFactory.getEntityPrototype(rowClass);
        } else {
            entityPrototype = null;
        }

        addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.enabled, PropertyName.editable, PropertyName.repopulated)) {
                    calculateActionsState();
                }
            }
        });

        addValueChangeHandler(new ValueChangeHandler<IList<E>>() {

            @Override
            public void onValueChange(ValueChangeEvent<IList<E>> event) {
                calculateActionsState();
            }
        });
    }

    public boolean isOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
        calculateActionsState();
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
        calculateActionsState();
    }

    private void calculateActionsState() {
        boolean addable = isAddable() && isEnabled() && isEditable() && !isViewable();
        if (getDecorator() instanceof IFolderDecorator) {
            ((IFolderDecorator<?>) getDecorator()).setAddButtonVisible(addable);
        }
        for (CFolderItem<E> item : itemsList) {
            item.calculateActionsState();
        }
    }

    /**
     * This mainly use for columns creation when TableFolderDecorator is used
     */
    public E proto() {
        return entityPrototype;
    }

    @Override
    protected IsWidget createContent() {
        FlowPanel contentPanel = new FlowPanel();
        contentPanel.add(noDataNotificationHolder);
        contentPanel.add(container);
        return contentPanel;
    }

    protected abstract IFolderItemDecorator<E> createItemDecorator();

    protected abstract CForm<? extends E> createItemForm(IObject<?> member);

    protected CFolderItem<E> createItem(boolean first) {
        return new CFolderItem<E>(rowClass) {
            @Override
            public IFolderItemDecorator<E> createItemDecorator() {
                return CFolder.this.createItemDecorator();
            }

            @Override
            protected CForm<? extends E> createItemForm(IObject<?> member) {
                return CFolder.this.createItemForm(null);
            }

        };
    }

    private CFolderItem<E> createItemPrivate() {
        return createItem(container.getWidgetCount() == 0);
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

    protected void addItem(E newEntity) {
        if (getValue() == null) {
            log.warn("Request to add item has been issued before the form populated with value");
            return;
        }

        final CFolderItem<E> item = createItemPrivate();

        adopt(item);
        getValue().add(newEntity);
        item.populate(newEntity);
        setVisited(true);

        revalidate();
        ValueChangeEvent.fire(CFolder.this, getValue());

        if (item.getDecorator() instanceof BoxFolderItemDecorator) {
            ((BoxFolderItemDecorator<?>) item.getDecorator()).setExpended(true);
        }

    }

    protected void removeItem(CFolderItem<E> item) {
        abandon(item);
        getValue().remove(item.getValue());
        setVisited(true);
        revalidate();
        ValueChangeEvent.fire(CFolder.this, getValue());

    }

    protected void moveUpItem(CFolderItem<E> item) {
        moveItem(item, true);
    }

    protected void moveDownItem(CFolderItem<E> item) {
        moveItem(item, false);
    }

    protected void moveItem(CFolderItem<E> item, boolean up) {
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

        setVisited(true);
        revalidate();
        ValueChangeEvent.fire(CFolder.this, getValue());
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
    protected void setComponentsValue(IList<E> value, boolean fireEvent, boolean populate) {

        ArrayList<CFolderItem<E>> previousList = new ArrayList<CFolderItem<E>>(itemsList);

        for (CFolderItem<E> item : previousList) {
            abandon(item);
        }

        currentRowDebugId = 0;

        if (value != null) {

            for (E entity : value) {
                CFolderItem<E> item = null;
                for (CFolderItem<E> itemFromCahe : previousList) {
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

        for (CFolderItem<E> item : itemsList) {
            item.calculateActionsState();
        }

        if (getDecorator() instanceof TableFolderDecorator) {
            ((TableFolderDecorator<?>) getDecorator()).setHeaderVisible(container.getWidgetCount() > 0);
        }
    }

    @Override
    public void adopt(final CComponent<?, ?, ?> component) {
        itemsList.add((CFolderItem<E>) component);
        container.add(component);

        IDebugId rowDebugId = new CompositeDebugId(IDebugId.ROW_PREFIX, currentRowDebugId);
        component.setDebugIdSuffix(rowDebugId);
        currentRowDebugId++;

        noDataNotificationHolder.setVisible(getItemCount() <= 0);

        super.adopt(component);

    }

    @Override
    public void abandon(final CComponent<?, ?, ?> component) {
        super.abandon(component);
        container.remove(component);
        itemsList.remove(component);

        noDataNotificationHolder.setVisible(getItemCount() <= 0);

    }

    @Override
    public List<CFolderItem<E>> getComponents() {
        return itemsList;
    }

    public int getItemCount() {
        return itemsList.size();
    }

    public int getItemIndex(CFolderItem<E> item) {
        return itemsList.indexOf(item);
    }

    public CFolderItem<E> getItem(int index) {
        if (itemsList.size() > 0 && index > -1 && index < itemsList.size()) {
            return itemsList.get(index);
        } else {
            return null;
        }
    }

    @Override
    protected void onReset() {
        for (CComponent<?, ?, ?> component : new ArrayList<CComponent<?, ?, ?>>(getComponents())) {
            abandon(component);
        }
        container.clear();
        itemsList.clear();
    };

    public void setNoDataNotificationWidget(IsWidget widget) {
        noDataNotificationHolder.setWidget(widget);
    }
}